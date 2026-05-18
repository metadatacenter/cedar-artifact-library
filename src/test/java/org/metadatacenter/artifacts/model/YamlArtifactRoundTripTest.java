package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.EmailField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.PhoneNumberField;
import org.metadatacenter.artifacts.model.core.RadioField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextAreaField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlArtifactRoundTripTest
{
  private YamlArtifactReader yamlArtifactReader = new YamlArtifactReader();
  private YamlArtifactRenderer yamlArtifactRenderer;

  @BeforeEach public void setUp()
  {
    yamlArtifactReader = new YamlArtifactReader();
    yamlArtifactRenderer = new YamlArtifactRenderer(false);
  }

  // -------- Simple schema artifacts --------

  @Test public void testRoundTripSimpleTemplate()
  {
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).withName("Study").build();
    roundTripTemplate(original);
  }

  @Test public void testRoundTripSimpleElementSchemaArtifact()
  {
    ElementSchemaArtifact original = ElementSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123")).withName("Study").build();
    roundTripElement(original);
  }

  // -------- Per-field-type round trips --------

  @Test public void testRoundTripTextField()
  {
    TextField original = TextField.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).withName("Study")
      .withDefaultValue("A default value").withRegex("*").build();
    roundTripField(original);
  }

  @Test public void testRoundTripTextFieldWithLengthBounds()
  {
    TextField original = TextField.builder().withName("ZIP").withMinLength(5).withMaxLength(5).build();
    roundTripField(original);
  }

  @Test public void testRoundTripTextAreaField()
  {
    TextAreaField original = TextAreaField.builder().withName("Description").withMinLength(20).withMaxLength(1000)
      .build();
    roundTripField(original);
  }

  @Test public void testRoundTripNumericField()
  {
    // XsdNumericDatatype.INT and INTEGER both have text "xsd:int", so fromString cannot
    // distinguish them. Use DOUBLE (unique text) for a clean round-trip.
    NumericField original = NumericField.builder().withName("Percent").withNumericType(XsdNumericDatatype.DOUBLE)
      .withMinValue(0).withMaxValue(100).withDefaultValue(0).build();
    roundTripField(original);
  }

  @Test public void testRoundTripTemporalField()
  {
    TemporalField original = TemporalField.builder().withName("Visit time")
      .withTemporalType(XsdTemporalDatatype.DATETIME).withTemporalGranularity(TemporalGranularity.MINUTE)
      .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR).withTimeZoneEnabled(true).build();
    roundTripField(original);
  }

  @Test public void testRoundTripEmailField()
  {
    EmailField original = EmailField.builder().withName("Email").build();
    roundTripField(original);
  }

  @Test public void testRoundTripPhoneNumberField()
  {
    PhoneNumberField original = PhoneNumberField.builder().withName("Phone").build();
    roundTripField(original);
  }

  @Test public void testRoundTripRadioFieldWithOptions()
  {
    RadioField original = RadioField.builder().withName("Covid-19 Status").withOption("Yes").withOption("No")
      .withOption("Maybe", true).build();
    roundTripField(original);
  }

  @Test public void testRoundTripCheckboxFieldWithOptions()
  {
    CheckboxField original = CheckboxField.builder().withName("DTAP Status").withOption("Yes").withOption("No")
      .withOption("Don't Know", true).build();
    roundTripField(original);
  }

  @Test public void testRoundTripListFieldWithOptions()
  {
    ListField original = ListField.builder().withName("Vaccine").withOption("Moderna").withOption("Pfizer")
      .withOption("None", true).build();
    roundTripField(original);
  }

  @Test public void testRoundTripLinkField()
  {
    // The renderer's renderCoreValueConstraints doesn't serialize LinkDefaultValue today
    // (handles only Text/Numeric/ControlledTerm defaults), so a default URI wouldn't round-trip.
    // Test the plain link case for now.
    LinkField original = LinkField.builder().withName("Institution Home Page").build();
    roundTripField(original);
  }

  @Test public void testRoundTripControlledTermFieldWithOntologyAndClass()
  {
    ControlledTermField original = ControlledTermField.builder().withName("Disease")
      .withOntologyValueConstraint(URI.create("https://data.bioontology.org/ontologies/DOID"), "DOID",
        "Human Disease Ontology")
      .withClassValueConstraint(URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3"), "LOINC", "Human",
        "Human",
        org.metadatacenter.artifacts.model.core.fields.constraints.ValueType.ONTOLOGY_CLASS)
      .build();
    roundTripField(original);
  }

  // -------- Schemas with child schemas --------

  @Test public void testRoundTripTemplateWithSingleField()
  {
    TextField studyId = TextField.builder().withName("Study ID").withMinLength(2).withMaxLength(10).build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder().withName("Study").withFieldSchema(studyId)
      .build();
    roundTripTemplate(original);
  }

  @Test public void testRoundTripTemplateWithTwoFields()
  {
    TextField studyId = TextField.builder().withName("Study ID").withMinLength(2).withMaxLength(10).build();
    NumericField pct = NumericField.builder().withName("Percent").withNumericType(XsdNumericDatatype.DOUBLE)
      .withMinValue(0).withMaxValue(100).build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder().withName("Study").withFieldSchema(studyId)
      .withFieldSchema(pct).build();
    roundTripTemplate(original);
  }

  @Test public void testRoundTripElementWithChildField()
  {
    TextField zip = TextField.builder().withName("ZIP").withMinLength(5).withMaxLength(5).build();
    ElementSchemaArtifact original = ElementSchemaArtifact.builder().withName("Address").withFieldSchema(zip).build();
    roundTripElement(original);
  }

  @Test public void testRoundTripTemplateWithNestedElement()
  {
    TextField street = TextField.builder().withName("Street").build();
    TextField zip = TextField.builder().withName("ZIP").withMinLength(5).withMaxLength(5).build();
    ElementSchemaArtifact address = ElementSchemaArtifact.builder().withName("Address").withFieldSchema(street)
      .withFieldSchema(zip).build();
    TextField studyId = TextField.builder().withName("Study ID").build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder().withName("Study").withFieldSchema(studyId)
      .withElementSchema(address).build();
    roundTripTemplate(original);
  }

  // -------- Round-trip helpers --------

  private void roundTripTemplate(TemplateSchemaArtifact original)
  {
    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(original);
    TemplateSchemaArtifact roundTripped = yamlArtifactReader.readTemplateSchemaArtifact(rendering);
    assertEquals(original, roundTripped);
  }

  private void roundTripElement(ElementSchemaArtifact original)
  {
    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderElementSchemaArtifact(original);
    ElementSchemaArtifact roundTripped = yamlArtifactReader.readElementSchemaArtifact(rendering);
    assertEquals(original, roundTripped);
  }

  private void roundTripField(FieldSchemaArtifact original)
  {
    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderFieldSchemaArtifact(original);
    FieldSchemaArtifact roundTripped = yamlArtifactReader.readFieldSchemaArtifact(rendering);
    assertEquals(original, roundTripped);
  }
}
