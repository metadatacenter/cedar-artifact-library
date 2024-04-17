package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldInstanceArtifactTest
{
  @Test
  public void textFieldInstanceTest()
  {
    String aValue = "a value";
    String language = "en";

    FieldInstanceArtifact fieldInstance = TextFieldInstance.builder().
      withValue(aValue).
      withLanguage(language).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
    assertEquals(language, fieldInstance.language().get());
  }

  @Test
  public void textAreaFieldInstanceTest()
  {
    String aValue = "a value";
    String language = "en";

    FieldInstanceArtifact fieldInstance = TextAreaFieldInstance.builder().
      withValue(aValue).
      withLanguage(language).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
    assertEquals(language, fieldInstance.language().get());
  }

  @Test
  public void numericFieldInstanceTest()
  {
    Integer value = 33;
    XsdNumericDatatype datatype = XsdNumericDatatype.DOUBLE;

    FieldInstanceArtifact fieldInstance = NumericFieldInstance.builder().
      withValue(value).
      withType(datatype).
      build();

    assertEquals(value.toString(), fieldInstance.jsonLdValue().get());
    assertTrue(fieldInstance.jsonLdTypes().contains(datatype.toUri()));
  }


  @Test
  public void temporalFieldInstanceTest()
  {
    String value = "1999-01-01";
    XsdTemporalDatatype datatype = XsdTemporalDatatype.DATE;

    FieldInstanceArtifact fieldInstance = TemporalFieldInstance.builder().
      withValue(value).
      withType(datatype).
      build();

    assertEquals(value.toString(), fieldInstance.jsonLdValue().get());
    assertTrue(fieldInstance.jsonLdTypes().contains(datatype.toUri()));
  }

  @Test
  public void radioFieldInstanceTest()
  {
    String aValue = "a value";

    FieldInstanceArtifact fieldInstance = RadioFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void phoneNumberFieldInstanceTest()
  {
    String aValue = "8675309";

    FieldInstanceArtifact fieldInstance = PhoneNumberFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }


  @Test
  public void emailFieldInstanceTest()
  {
    String aValue = "bob@bob.com";

    FieldInstanceArtifact fieldInstance = EmailFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void checkboxFieldInstanceTest()
  {
    String aValue = "a value";

    FieldInstanceArtifact fieldInstance = CheckboxFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void listFieldInstanceTest()
  {
    String aValue = "a value";

    FieldInstanceArtifact fieldInstance = ListFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void controlledTermFieldInstanceTest()
  {
    URI aUriValue = URI.create("https://example.com/values/v1");

    FieldInstanceArtifact fieldInstance = ControlledTermFieldInstance.builder().
      withValue(aUriValue).
      build();

    assertEquals(aUriValue, fieldInstance.jsonLdId().get());
  }

  @Test
  public void linkFieldInstanceTest()
  {
    URI aUriValue = URI.create("https://example.com/values/v1");

    FieldInstanceArtifact fieldInstance = LinkFieldInstance.builder().
      withValue(aUriValue).
      build();

    assertEquals(aUriValue, fieldInstance.jsonLdId().get());
  }

  @Test
  public void controlledTermFieldInstanceWithLabelNotationAndPrefLabelTest()
  {
    URI aUriValue = URI.create("https://example.com/values/v1");
    String label = "a label";
    String notation = "a notation";
    String prefLabel = "a prefLabel";

    FieldInstanceArtifact fieldInstance = ControlledTermFieldInstance.builder().
      withValue(aUriValue).
      withLabel(label).
      withPrefLabel(prefLabel).
      withNotation(notation).
      build();

    assertEquals(aUriValue, fieldInstance.jsonLdId().get());
    assertEquals(label, fieldInstance.label().get());
    assertEquals(notation, fieldInstance.notation().get());
    assertEquals(prefLabel, fieldInstance.prefLabel().get());
  }
}