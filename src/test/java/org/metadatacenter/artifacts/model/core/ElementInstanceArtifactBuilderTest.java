package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Group E (element instance) — exercises the multi-child / attribute-value / removal paths and
 * their duplicate / missing validation guards.
 */
public class ElementInstanceArtifactBuilderTest
{
  private static FieldInstanceArtifact literal(String value)
  {
    return FieldInstanceArtifact.create(java.util.Collections.emptyList(), Optional.empty(),
      Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  @Test public void testBuilderWithSingleAndMultiFieldChildren()
  {
    FieldInstanceArtifact a = literal("street");
    FieldInstanceArtifact b = literal("k1");
    FieldInstanceArtifact c = literal("k2");

    ElementInstanceArtifact element = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("Street", a)
      .withMultiInstanceFieldInstances("Keywords", List.of(b, c))
      .build();

    assertEquals(a, element.singleInstanceFieldInstances().get("Street"));
    assertEquals(2, element.multiInstanceFieldInstances().get("Keywords").size());
    assertTrue(element.childKeys().contains("Street"));
    assertTrue(element.childKeys().contains("Keywords"));
  }

  @Test public void testBuilderRejectsDuplicateChildKey()
  {
    ElementInstanceArtifact.Builder builder = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("k", literal("v"));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> builder.withSingleInstanceFieldInstance("k", literal("v2")));
    assertTrue(ex.getMessage().contains("already present"));
  }

  @Test public void testWithoutSingleInstanceFieldInstanceRemovesEntry()
  {
    ElementInstanceArtifact element = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("k", literal("v"))
      .withoutSingleInstanceFieldInstance("k")
      .build();

    assertTrue(element.singleInstanceFieldInstances().isEmpty());
    assertTrue(!element.childKeys().contains("k"));
  }

  @Test public void testWithoutMissingChildThrows()
  {
    ElementInstanceArtifact.Builder builder = ElementInstanceArtifact.builder();
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> builder.withoutSingleInstanceFieldInstance("absent"));
    assertTrue(ex.getMessage().contains("not present"));
  }

  @Test public void testWithAttributeValueFieldGroupRejectsOverlappingKey()
  {
    // Group field-instance names must not collide with already-registered child keys.
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("Street", literal("attr"));

    ElementInstanceArtifact.Builder builder = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("Street", literal("v"));

    assertThrows(IllegalArgumentException.class,
      () -> builder.withAttributeValueFieldGroup("attrs", group));
  }
}
