package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Null/invalid-arg validation paths in the field-schema and constraint builders. Pins the
 * visible invariants so future refactors don't silently loosen them.
 */
public class BuilderValidationTest
{
  // ---- FieldSchemaArtifactBuilder null guards (via TextField builder) ----

  @Test public void testBuildRejectsNullName()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withName(null));
    assertTrue(ex.getMessage().contains("name"));
  }

  @Test public void testBuildRejectsNullJsonLdContext()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withJsonLdContext(null));
  }

  @Test public void testBuildRejectsNullJsonLdType()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withJsonLdType(null));
  }

  @Test public void testBuildRejectsNullDescription()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withDescription(null));
    assertTrue(ex.getMessage().contains("description"));
  }

  @Test public void testBuildRejectsNullIdentifier()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withIdentifier(null));
  }

  @Test public void testBuildRejectsNullPreferredLabel()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withPreferredLabel(null));
  }

  @Test public void testBuildRejectsNullAlternateLabels()
  {
    // Empty list is OK; null is not.
    TextField.builder().withName("OK").withAlternateLabels(Collections.emptyList()).build();
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withAlternateLabels(null));
  }

  @Test public void testBuildRejectsNullVersion()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withVersion(null));
  }

  // ---- TextValueConstraints negative paths ----

  @Test public void testTextValueConstraintsBuilderRejectsEmptyDefaultValue()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextValueConstraints.builder().withDefaultValue(""));
    assertTrue(ex.getMessage().contains("empty"));
  }

  @Test public void testTextValueConstraintsBuilderRejectsNullRegex()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextValueConstraints.builder().withRegex(null));
    assertTrue(ex.getMessage().contains("regex"));
  }
}
