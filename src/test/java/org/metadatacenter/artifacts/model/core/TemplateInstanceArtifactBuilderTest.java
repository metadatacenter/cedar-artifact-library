package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Group E (template instance) — exercises duplicate / remove-then-re-add / attribute-value
 * conflict paths on the template-instance builder.
 */
public class TemplateInstanceArtifactBuilderTest
{
  private static FieldInstanceArtifact literal(String value)
  {
    return FieldInstanceArtifact.create(java.util.Collections.emptyList(), Optional.empty(),
      Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  private static TemplateInstanceArtifact.Builder minimalBuilder()
  {
    return TemplateInstanceArtifact.builder()
      .withName("Inst")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"));
  }

  @Test public void testBuilderRejectsDuplicateChildKey()
  {
    TemplateInstanceArtifact.Builder builder = minimalBuilder()
      .withSingleInstanceFieldInstance("f", literal("v1"));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> builder.withSingleInstanceFieldInstance("f", literal("v2")));
    assertTrue(ex.getMessage().contains("already present"));
  }

  @Test public void testRemoveChildThenReadd()
  {
    // After removal, the key is free to be re-bound — possibly to a different child kind.
    FieldInstanceArtifact newField = literal("v2");
    TemplateInstanceArtifact instance = minimalBuilder()
      .withSingleInstanceFieldInstance("f", literal("v1"))
      .withoutSingleInstanceFieldInstance("f")
      .withSingleInstanceFieldInstance("f", newField)
      .build();

    assertEquals(newField, instance.singleInstanceFieldInstances().get("f"));
    assertEquals(1, instance.childKeys().size());
  }

  @Test public void testAttributeValueGroupRejectsConflictWithExistingChild()
  {
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("attr1", literal("a"));

    TemplateInstanceArtifact.Builder builder = minimalBuilder()
      .withSingleInstanceFieldInstance("attrs", literal("clash"));

    // The group *name* itself ("attrs") collides with the previously-registered field child.
    assertThrows(IllegalArgumentException.class,
      () -> builder.withAttributeValueFieldGroup("attrs", group));
  }
}
