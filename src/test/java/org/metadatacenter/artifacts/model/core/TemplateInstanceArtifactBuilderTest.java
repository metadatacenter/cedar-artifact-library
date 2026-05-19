package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test public void testWithoutAttributeValueFieldGroupAllowsReadditionUnderSameName()
  {
    LinkedHashMap<String, FieldInstanceArtifact> first = new LinkedHashMap<>();
    first.put("attr1", literal("a"));
    LinkedHashMap<String, FieldInstanceArtifact> second = new LinkedHashMap<>();
    second.put("attr1", literal("b"));

    // Remove must clear childKeys cleanly enough that re-adding under the same name works.
    TemplateInstanceArtifact instance = minimalBuilder()
      .withAttributeValueFieldGroup("custom", first)
      .withoutAttributeValueFieldGroup("custom")
      .withAttributeValueFieldGroup("custom", second)
      .build();

    assertEquals("b", instance.attributeValueFieldInstanceGroups().get("custom").get("attr1").jsonLdValue().get());
  }

  @Test public void testWithoutAttributeValueFieldGroupRejectsAbsentName()
  {
    TemplateInstanceArtifact.Builder builder = minimalBuilder();
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> builder.withoutAttributeValueFieldGroup("never-added"));
    assertTrue(ex.getMessage().contains("not present"));
  }
}
