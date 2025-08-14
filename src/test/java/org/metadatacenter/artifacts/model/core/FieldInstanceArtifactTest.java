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

    TextFieldInstance fieldInstance = TextFieldInstance.builder().
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

    TextAreaFieldInstance fieldInstance = TextAreaFieldInstance.builder().
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

    NumericFieldInstance fieldInstance = NumericFieldInstance.builder().
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

    TemporalFieldInstance fieldInstance = TemporalFieldInstance.builder().
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

    RadioFieldInstance fieldInstance = RadioFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void phoneNumberFieldInstanceTest()
  {
    String aValue = "8675309";

    PhoneNumberFieldInstance fieldInstance = PhoneNumberFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }


  @Test
  public void emailFieldInstanceTest()
  {
    String aValue = "bob@bob.com";

    EmailFieldInstance fieldInstance = EmailFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void checkboxFieldInstanceTest()
  {
    String aValue = "a value";

    CheckboxFieldInstance fieldInstance = CheckboxFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void listFieldInstanceTest()
  {
    String aValue = "a value";

    ListFieldInstance fieldInstance = ListFieldInstance.builder().
      withValue(aValue).
      build();

    assertEquals(aValue, fieldInstance.jsonLdValue().get());
  }

  @Test
  public void controlledTermFieldInstanceTest()
  {
    URI aUriValue = URI.create("https://example.com/values/v1");

    ControlledTermFieldInstance fieldInstance = ControlledTermFieldInstance.builder().
      withValue(aUriValue).
      build();

    assertEquals(aUriValue, fieldInstance.jsonLdId().get());
  }

  @Test
  public void linkFieldInstanceTest()
  {
    String aLabel = "v1";
    URI aUri = URI.create("https://example.com/values/v1");

    LinkFieldInstance fieldInstance = LinkFieldInstance.builder().
      withLabel(aLabel).
      withValue(aUri).
      build();

    assertEquals(aLabel, fieldInstance.label().get());
    assertEquals(aUri, fieldInstance.jsonLdId().get());
  }

  @Test
  public void rorFieldInstanceTest()
  {
    String aRorLabel = "University of Chicago";
    URI aRorUri = URI.create("https://ror.org/024mw5h28");

    RorFieldInstance fieldInstance = RorFieldInstance.builder().
        withLabel(aRorLabel).
        withValue(aRorUri).
        build();

    assertEquals(aRorLabel, fieldInstance.label().get());
    assertEquals(aRorUri, fieldInstance.jsonLdId().get());
  }

  @Test
  public void orcidFieldInstanceTest()
  {
    String anOrcidLabel = "Billy Bob";
    URI anOrcidUri = URI.create("http://orcid.org/0000-0001-5109-3700");

    OrcidFieldInstance fieldInstance = OrcidFieldInstance.builder().
        withLabel(anOrcidLabel).
        withValue(anOrcidUri).
        build();

    assertEquals(anOrcidLabel, fieldInstance.label().get());
    assertEquals(anOrcidUri, fieldInstance.jsonLdId().get());
  }

  @Test
  public void pfasFieldInstanceTest()
  {
    String aPfasLabel = "Bisphenol A";
    URI aPfasUri = URI.create("https://api-ccte.epa.gov/chemical/detail/search/by-dtxsid/DTXSID7020182");

    PfasFieldInstance fieldInstance = PfasFieldInstance.builder().
        withLabel(aPfasLabel).
        withValue(aPfasUri).
        build();

    assertEquals(aPfasLabel, fieldInstance.label().get());
    assertEquals(aPfasUri, fieldInstance.jsonLdId().get());
  }

  @Test
  public void rridFieldInstanceTest()
  {
    String aRridLabel = "Research Randomizer";
    URI aRridUri = URI.create("https://identifiers.org/RRID:SCR_008563");

    RridFieldInstance rridInstance = RridFieldInstance.builder().
        withLabel(aRridLabel).
        withValue(aRridUri).
        build();

    assertEquals(aRridLabel, rridInstance.label().get());
    assertEquals(aRridUri, rridInstance.jsonLdId().get());
  }

  @Test
  public void pubMedFieldInstanceTest()
  {
    String aPubMedLabel = "HuBMAPR: an R client for the HuBMAP data portal.";
    URI aPubMedUri = URI.create("https://pubmed.ncbi.nlm.nih.gov/40212885");

    PubMedFieldInstance pubMedInstance = PubMedFieldInstance.builder().
        withLabel(aPubMedLabel).
        withValue(aPubMedUri).
        build();

    assertEquals(aPubMedLabel, pubMedInstance.label().get());
    assertEquals(aPubMedUri, pubMedInstance.jsonLdId().get());
  }

  @Test
  public void controlledTermFieldInstanceWithLabelNotationAndPrefLabelTest()
  {
    URI aUriValue = URI.create("https://example.com/values/v1");
    String label = "a label";
    String notation = "a notation";
    String preferredLabel = "a prefLabel";

    ControlledTermFieldInstance fieldInstance = ControlledTermFieldInstance.builder().
      withValue(aUriValue).
      withLabel(label).withPreferredLabel(preferredLabel).
      withNotation(notation).
      build();

    assertEquals(aUriValue, fieldInstance.jsonLdId().get());
    assertEquals(label, fieldInstance.label().get());
    assertEquals(notation, fieldInstance.notation().get());
    assertEquals(preferredLabel, fieldInstance.preferredLabel().get());
  }
}