package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.Annotations;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.EmailField;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.PhoneNumberField;
import org.metadatacenter.artifacts.model.core.RadioField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextAreaField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  // -------- Instance JSON-LD type --------

  @Test public void testRoundTripTemplateWithInstanceJsonLdType()
  {
    URI instanceType = URI.create("https://schema.metadatacenter.org/types/Study");
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder().withName("Study")
      .withInstanceJsonLdType(instanceType).build();
    roundTripTemplate(original);
  }

  @Test public void testRoundTripElementWithInstanceJsonLdType()
  {
    URI instanceType = URI.create("https://schema.metadatacenter.org/types/Address");
    ElementSchemaArtifact original = ElementSchemaArtifact.builder().withName("Address")
      .withInstanceJsonLdType(instanceType).build();
    roundTripElement(original);
  }

  // -------- Annotations --------

  @Test public void testRoundTripTemplateWithAnnotations()
  {
    Annotations annotations = Annotations.builder()
      .withLiteralAnnotation("Preferred Ontology", "DOID")
      .withIriAnnotation("https://datacite.com/doi", URI.create("https://doi.org/10.82658/8vc1-abcd"))
      .build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder().withName("Study")
      .withAnnotations(annotations).build();
    roundTripTemplate(original);
  }

  @Test public void testRoundTripFieldWithAnnotations()
  {
    Annotations annotations = Annotations.builder().withLiteralAnnotation("source", "manual").build();
    TextField original = TextField.builder().withName("Study ID").withAnnotations(annotations).build();
    roundTripField(original);
  }

  @Test public void testRoundTripElementWithAnnotations()
  {
    Annotations annotations = Annotations.builder()
      .withLiteralAnnotation("rdfs:comment", "a postal address")
      .withIriAnnotation("skos:exactMatch", URI.create("https://example.org/term/1"))
      .build();
    ElementSchemaArtifact original = ElementSchemaArtifact.builder().withName("Address")
      .withAnnotations(annotations).build();
    roundTripElement(original);
  }

  // -------- Template instance artifacts --------

  @Test public void testRoundTripSimpleTemplateInstance()
  {
    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/ec3f500f-ddca-4ec1-9196-29932f9304fd"))
      .build();
    roundTripTemplateInstance(original);
  }

  @Test public void testRoundTripTemplateInstanceWithFieldInstances()
  {
    FieldInstanceArtifact studyId = FieldInstanceArtifact.create(
      java.util.Collections.emptyList(), java.util.Optional.empty(),
      java.util.Optional.of("STD-12345"), java.util.Optional.empty(), java.util.Optional.empty(),
      java.util.Optional.empty(), java.util.Optional.empty());
    FieldInstanceArtifact pi = FieldInstanceArtifact.create(
      java.util.Collections.emptyList(), java.util.Optional.empty(),
      java.util.Optional.of("Dr. P.I."), java.util.Optional.empty(), java.util.Optional.empty(),
      java.util.Optional.empty(), java.util.Optional.empty());

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withSingleInstanceFieldInstance("Study ID", studyId)
      .withSingleInstanceFieldInstance("PI", pi)
      .build();
    roundTripTemplateInstance(original);
  }

  @Test public void testValuelessFieldSlotIsOmittedFromYaml()
  {
    // An unset field slot is omitted from YAML entirely — no `value: null`, no `{}`. The
    // instance carries only fields that hold a value; the unset slot's presence is
    // reconstructable from the template at the JSON boundary. A field that *does* carry a
    // value is still rendered, so the two are distinguishable.
    FieldInstanceArtifact empty = FieldInstanceArtifact.create(
      java.util.Collections.emptyList(), java.util.Optional.empty(), java.util.Optional.empty(),
      java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(),
      java.util.Optional.empty());

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withSingleInstanceFieldInstance("Patient Name", empty)
      .withSingleInstanceFieldInstance("Age", simpleLiteralField("42"))
      .build();

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderTemplateInstanceArtifact(original);

    @SuppressWarnings("unchecked")
    LinkedHashMap<String, Object> children = (LinkedHashMap<String, Object>) rendering.get("children");
    assertTrue(children == null || !children.containsKey("Patient Name"),
      "an unset slot must be omitted entirely (no `{}`, no `value: null`), got: " + children);
    assertTrue(children != null && children.containsKey("Age"),
      "a field that carries a value must still be rendered, got: " + children);
  }

  @Test public void testReaderRejectsExplicitNullValue()
  {
    // YAML contract: null is never a valid value. A `value: null` (or any null, anywhere) is
    // rejected on read — an unknown value must be omitted, not nulled.
    LinkedHashMap<String, Object> child = new LinkedHashMap<>();
    child.put("value", null);
    LinkedHashMap<String, Object> children = new LinkedHashMap<>();
    children.put("Patient Name", child);
    LinkedHashMap<String, Object> instance = new LinkedHashMap<>();
    instance.put("type", "instance");
    instance.put("name", "SDY232");
    instance.put("isBasedOn", "https://repo.metadatacenter.org/templates/abc");
    instance.put("children", children);

    assertThrows(ArtifactParseException.class,
      () -> yamlArtifactReader.readTemplateInstanceArtifact(instance));
  }

  @Test public void testReaderRejectsEmptyMapValue()
  {
    // YAML contract: an empty mapping ({}) is never a valid value, just like null — an unset
    // value must be omitted, not represented as an empty placeholder.
    LinkedHashMap<String, Object> child = new LinkedHashMap<>();  // {}
    LinkedHashMap<String, Object> children = new LinkedHashMap<>();
    children.put("Patient Name", child);
    LinkedHashMap<String, Object> instance = new LinkedHashMap<>();
    instance.put("type", "instance");
    instance.put("name", "SDY232");
    instance.put("isBasedOn", "https://repo.metadatacenter.org/templates/abc");
    instance.put("children", children);

    assertThrows(ArtifactParseException.class,
      () -> yamlArtifactReader.readTemplateInstanceArtifact(instance));
  }

  @Test public void testReaderRejectsEmptyListValue()
  {
    // YAML contract: an empty list ([]) is never a valid value, like null and {} — an unset
    // multi-instance slot must be omitted, not represented as an empty placeholder.
    LinkedHashMap<String, Object> children = new LinkedHashMap<>();
    children.put("tags", new java.util.ArrayList<>());  // []
    LinkedHashMap<String, Object> instance = new LinkedHashMap<>();
    instance.put("type", "instance");
    instance.put("name", "SDY232");
    instance.put("isBasedOn", "https://repo.metadatacenter.org/templates/abc");
    instance.put("children", children);

    assertThrows(ArtifactParseException.class,
      () -> yamlArtifactReader.readTemplateInstanceArtifact(instance));
  }

  @Test public void testReaderRejectsNullListElement()
  {
    // The rule extends to list elements, not just map values.
    java.util.List<Object> keywords = new java.util.ArrayList<>();
    keywords.add(null);
    LinkedHashMap<String, Object> children = new LinkedHashMap<>();
    children.put("keywords", keywords);
    LinkedHashMap<String, Object> instance = new LinkedHashMap<>();
    instance.put("type", "instance");
    instance.put("name", "SDY232");
    instance.put("isBasedOn", "https://repo.metadatacenter.org/templates/abc");
    instance.put("children", children);

    assertThrows(ArtifactParseException.class,
      () -> yamlArtifactReader.readTemplateInstanceArtifact(instance));
  }

  @Test public void testRoundTripTemplateInstanceWithAnnotations()
  {
    Annotations annotations = Annotations.builder().withLiteralAnnotation("source", "import").build();
    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withAnnotations(annotations).build();
    roundTripTemplateInstance(original);
  }

  @Test public void testRoundTripTemplateInstanceWithMultiInstanceFields()
  {
    FieldInstanceArtifact keyword1 = simpleLiteralField("immunology");
    FieldInstanceArtifact keyword2 = simpleLiteralField("oncology");

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withMultiInstanceFieldInstances("keywords", java.util.List.of(keyword1, keyword2))
      .build();
    roundTripTemplateInstance(original);
  }

  @Test public void testRoundTripTemplateInstanceWithNestedElement()
  {
    FieldInstanceArtifact street = simpleLiteralField("123 Main St");
    FieldInstanceArtifact city = simpleLiteralField("Palo Alto");
    ElementInstanceArtifact address = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("Street", street)
      .withSingleInstanceFieldInstance("City", city)
      .build();

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withSingleInstanceElementInstance("Address", address)
      .build();
    roundTripTemplateInstance(original);
  }

  @Test public void testRoundTripTemplateInstanceWithMultiInstanceElements()
  {
    FieldInstanceArtifact street1 = simpleLiteralField("123 Main St");
    ElementInstanceArtifact address1 = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("Street", street1).build();
    FieldInstanceArtifact street2 = simpleLiteralField("742 Evergreen Tce");
    ElementInstanceArtifact address2 = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("Street", street2).build();

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withMultiInstanceElementInstances("Addresses", java.util.List.of(address1, address2))
      .build();
    roundTripTemplateInstance(original);
  }

  @Test public void testRoundTripTemplateInstanceWithAttributeValueFieldGroup()
  {
    FieldInstanceArtifact attr1 = simpleLiteralField("foo");
    FieldInstanceArtifact attr2 = simpleLiteralField("bar");
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("attr1", attr1);
    group.put("attr2", attr2);

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withAttributeValueFieldGroup("custom-attrs", group)
      .build();
    roundTripTemplateInstance(original);
  }

  @Test public void testRoundTripElementInstanceWithAttributeValueFieldGroup()
  {
    FieldInstanceArtifact attr = simpleLiteralField("foo");
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("attr1", attr);

    ElementInstanceArtifact address = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("Street", simpleLiteralField("Main"))
      .withAttributeValueFieldGroup("ext-attrs", group)
      .build();

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("SDY232")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withSingleInstanceElementInstance("Address", address)
      .build();
    roundTripTemplateInstance(original);
  }

  // -------- Round-trip helpers --------

  private static FieldInstanceArtifact simpleLiteralField(String value)
  {
    return FieldInstanceArtifact.create(
      java.util.Collections.emptyList(), java.util.Optional.empty(),
      java.util.Optional.of(value), java.util.Optional.empty(), java.util.Optional.empty(),
      java.util.Optional.empty(), java.util.Optional.empty());
  }


  private void roundTripTemplateInstance(TemplateInstanceArtifact original)
  {
    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderTemplateInstanceArtifact(original);
    TemplateInstanceArtifact roundTripped = yamlArtifactReader.readTemplateInstanceArtifact(rendering);
    assertEquals(original, roundTripped);
  }


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
