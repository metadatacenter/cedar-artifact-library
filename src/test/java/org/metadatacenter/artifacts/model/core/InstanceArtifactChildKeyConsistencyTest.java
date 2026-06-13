package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the childKey/child-instance-map consistency invariant enforced in the instance-artifact
 * record compact constructors (via {@link InstanceArtifactInvariants#validateChildKeyConsistency}).
 *
 * <p>The builders keep childKeys and the maps consistent by construction, so a violation can only
 * be produced by constructing the package-private records directly — which these tests do.
 */
public class InstanceArtifactChildKeyConsistencyTest
{
  private static final URI IS_BASED_ON = URI.create("https://example.com/templates/A");

  private static FieldInstanceArtifact field()
  {
    return TextFieldInstance.builder().build();
  }

  private static LinkedHashMap<String, FieldInstanceArtifact> singleField(String... keys)
  {
    LinkedHashMap<String, FieldInstanceArtifact> map = new LinkedHashMap<>();
    for (String key : keys)
      map.put(key, field());
    return map;
  }

  private static LinkedHashMap<String, Map<String, FieldInstanceArtifact>> noGroups()
  {
    return new LinkedHashMap<>();
  }

  private static LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attrGroup(String groupName,
    String... innerNames)
  {
    LinkedHashMap<String, FieldInstanceArtifact> inner = new LinkedHashMap<>();
    for (String name : innerNames)
      inner.put(name, field());
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> groups = new LinkedHashMap<>();
    groups.put(groupName, inner);
    return groups;
  }

  private static TemplateInstanceArtifact templateRecord(List<String> childKeys,
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups)
  {
    return new TemplateInstanceArtifactRecord(new LinkedHashMap<>(), List.of(), Optional.empty(), Optional.of("I"),
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), IS_BASED_ON,
      Optional.empty(), childKeys, singleInstanceFieldInstances, new LinkedHashMap<>(), new LinkedHashMap<>(),
      new LinkedHashMap<>(), attributeValueFieldInstanceGroups, Optional.empty());
  }

  private static ElementInstanceArtifact elementRecord(List<String> childKeys,
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups)
  {
    return new ElementInstanceArtifactRecord(new LinkedHashMap<>(), List.of(), Optional.empty(), Optional.empty(),
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), childKeys,
      singleInstanceFieldInstances, new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(),
      attributeValueFieldInstanceGroups);
  }

  // ---- No extras in the maps ----

  @Test public void testTemplateRejectsFieldInstanceNotDeclaredInChildKeys()
  {
    IllegalStateException ex = assertThrows(IllegalStateException.class,
      () -> templateRecord(List.of(), singleField("Age"), noGroups()));
    assertTrue(ex.getMessage().contains("Age"), ex.getMessage());
    assertTrue(ex.getMessage().contains("not declared in childKeys"), ex.getMessage());
  }

  @Test public void testElementRejectsFieldInstanceNotDeclaredInChildKeys()
  {
    IllegalStateException ex = assertThrows(IllegalStateException.class,
      () -> elementRecord(List.of(), singleField("Age"), noGroups()));
    assertTrue(ex.getMessage().contains("Age"), ex.getMessage());
    assertTrue(ex.getMessage().contains("not declared in childKeys"), ex.getMessage());
  }

  @Test public void testTemplateRejectsAttributeValueInnerNameNotDeclaredInChildKeys()
  {
    // group name + one inner name declared, but the second inner name ("zip") is missing
    IllegalStateException ex = assertThrows(IllegalStateException.class,
      () -> templateRecord(List.of("addr", "city"), singleField(), attrGroup("addr", "city", "zip")));
    assertTrue(ex.getMessage().contains("zip"), ex.getMessage());
    assertTrue(ex.getMessage().contains("not declared in childKeys"), ex.getMessage());
  }

  // ---- Every childKey backed by an instance ----

  @Test public void testTemplateRejectsChildKeyWithoutInstance()
  {
    IllegalStateException ex = assertThrows(IllegalStateException.class,
      () -> templateRecord(List.of("Age"), singleField(), noGroups()));
    assertTrue(ex.getMessage().contains("Age"), ex.getMessage());
    assertTrue(ex.getMessage().contains("no corresponding child instance"), ex.getMessage());
  }

  @Test public void testElementRejectsChildKeyWithoutInstance()
  {
    IllegalStateException ex = assertThrows(IllegalStateException.class,
      () -> elementRecord(List.of("Age"), singleField(), noGroups()));
    assertTrue(ex.getMessage().contains("Age"), ex.getMessage());
    assertTrue(ex.getMessage().contains("no corresponding child instance"), ex.getMessage());
  }

  // ---- Consistent instances build cleanly ----

  @Test public void testTemplateAcceptsConsistentSingleField()
  {
    assertDoesNotThrow(() -> templateRecord(List.of("Age"), singleField("Age"), noGroups()));
  }

  @Test public void testElementAcceptsConsistentSingleField()
  {
    assertDoesNotThrow(() -> elementRecord(List.of("Age"), singleField("Age"), noGroups()));
  }

  @Test public void testTemplateAcceptsAttributeValueGroupWithInnerNames()
  {
    // childKeys carries both the group name and its inner field-instance names
    assertDoesNotThrow(
      () -> templateRecord(List.of("addr", "city", "zip"), singleField(), attrGroup("addr", "city", "zip")));
  }

  @Test public void testTemplateAcceptsEmptyInstance()
  {
    assertDoesNotThrow(() -> templateRecord(List.of(), singleField(), noGroups()));
  }
}
