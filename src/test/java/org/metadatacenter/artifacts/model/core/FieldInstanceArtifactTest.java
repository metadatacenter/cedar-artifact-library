package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class FieldInstanceArtifactTest
{
  @Test
  public void fieldInstanceWithJsonLdValueTest()
  {
    URI aType = URI.create("https://example.com/types/t1");
    String aValue = "a value";

    FieldInstanceArtifact fieldInstance = FieldInstanceArtifact.builder().
      withJsonLdType(aType).
      withJsonLdValue(aValue).
      build();

    assertEquals(aType, fieldInstance.jsonLdTypes().get(0));
    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void fieldInstanceWithJsonLdTypeTest()
  {
    URI aUriValue = URI.create("https://example.com/values/v1");

    FieldInstanceArtifact fieldInstance = FieldInstanceArtifact.builder().
      withJsonLdId(aUriValue).
      build();

    assertEquals(aUriValue, fieldInstance.jsonLdId().get());
  }

  @Test
  public void fieldInstanceWithLabelNotationAndPrefLabelTest()
  {
    URI aUriValue = URI.create("https://example.com/values/v1");
    String label = "a label";
    String notation = "a notation";
    String prefLabel = "a prefLabel";
    String language = "en";

    FieldInstanceArtifact fieldInstance = FieldInstanceArtifact.builder().
      withJsonLdId(aUriValue).
      withLabel(label).
      withPrefLabel(prefLabel).
      withLanguage(language).
      withNotation(notation).
      build();

    assertEquals(aUriValue, fieldInstance.jsonLdId().get());
    assertEquals(label, fieldInstance.label().get());
    assertEquals(notation, fieldInstance.notation().get());
    assertEquals(prefLabel, fieldInstance.prefLabel().get());
    assertEquals(language, fieldInstance.language().get());
  }
}