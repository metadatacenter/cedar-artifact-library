package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.ValueType;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReaderTest;
import org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactRenderer;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.artifacts.model.core.ValueConstraintsActionType.DELETE;

public class ArtifactRoundTripTest
{
  private JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
  private JsonSchemaArtifactRenderer jsonSchemaArtifactRenderer;
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    artifactReader = new JsonSchemaArtifactReader();
    jsonSchemaArtifactRenderer = new JsonSchemaArtifactRenderer();
    mapper = new ObjectMapper();
  }

  @Test
  public void testRoundTripTemplateSchemaArtifact() {
    TemplateSchemaArtifact originalTemplateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName("Study").
      build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(originalTemplateSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    TemplateSchemaArtifact finalTemplateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(finalTemplateSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalTemplateSchemaArtifact, finalTemplateSchemaArtifact);
  }

  @Test
  public void testRoundTripElementSchemaArtifact()
  {
    ElementSchemaArtifact originalElementSchemaArtifact = ElementSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123"))
      .withName("Study")
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderElementSchemaArtifact(originalElementSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    ElementSchemaArtifact finalElementSchemaArtifact = artifactReader.readElementSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderElementSchemaArtifact(finalElementSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalElementSchemaArtifact, finalElementSchemaArtifact);
  }

  @Test
  public void testRoundTripTextField()
  {
    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123"))
      .withName("Study")
      .withDefaultValue("A default value")
      .withRegex("*")
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripNumericField()
  {
    String name = "Field name";
    String description = "Field description";
    XsdNumericDatatype numericType = XsdNumericDatatype.DOUBLE;
    Number minValue = 0.0;
    Number maxValue = 100.0;

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withNumericType(numericType)
      .withMinValue(minValue)
      .withMaxValue(maxValue)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripTemporalField()
  {
    String name = "Field name";
    String description = "Field description";
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.TIME;
    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timeZoneEnabled = false;

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.temporalFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withTemporalType(temporalType)
      .withTemporalGranularity(temporalGranularity)
      .withInputTimeFormat(inputTimeFormat)
      .withTimeZoneEnabled(timeZoneEnabled)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripControlledTermField()
  {
    String name = "Field name";
    String description = "Field description";
    URI ontologyUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String ontologyAcronym = "DOID";
    String ontologyName = "Human Disease Ontology";
    URI branchUri = URI.create("http://purl.bioontology.org/ontology/SNOMEDCT/64572001");
    String branchAcronym = "SNOMEDCT";
    String branchName = "Disease";
    String branchSource = "SNOMEDCT";
    Integer branchMaxDepth = 3;
    URI classUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String classSource = "LOINC";
    String classLabel = "Human";
    String classPrefLabel = "Homo Spiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;
    URI valueSetUri = URI.create("https://cadsr.nci.nih.gov/metadata/CADSR-VS/77d61de250089d223d7153a4283e738043a15707");
    String valueSetCollection = "CADSR-VS";
    String valueSetName = "Stable Disease";
    Integer valueSetNumberOfTerms = 1;
    URI actionTermUri = URI.create("http://purl.obolibrary.org/obo/NCBITaxon_51291");
    URI actionSourceUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String actionSource = "DOID";
    ValueType actionValueType = ValueType.ONTOLOGY_CLASS;
    Integer actionTo = 0;

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.controlledTermFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withOntologyValueConstraint(ontologyUri, ontologyAcronym, ontologyName)
      .withBranchValueConstraint(branchUri, branchSource, branchAcronym, branchName, branchMaxDepth)
      .withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType)
      .withValueSetValueConstraint(valueSetUri, valueSetCollection, valueSetName, valueSetNumberOfTerms)
      .withValueConstraintsAction(actionTermUri, actionSource, actionValueType, DELETE, actionSourceUri, actionTo)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripRadioField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.radioFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withOption("Choice 1")
      .withOption("Choice 2")
      .withOption("Choice 3", true)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testCreateListField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.listFieldBuilder().
      withName(name).
      withDescription(description).
      withOption("Choice 1").
      withOption("Choice 2").
      withOption("Choice 3", true).
      build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripCheckboxField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.checkboxFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withOption("Choice 1", false)
      .withOption("Choice 2", false)
      .withOption("Choice 3", true)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripPhoneNumberField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.phoneNumberFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withMinLength(minLength)
      .withMaxLength(maxLength)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripEmailField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.emailFieldBuilder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripLinkField()
  {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://example.com/Study");
    String defaultLabel = "Study";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.linkFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withDefaultValue(defaultURI, defaultLabel)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripTextAreaField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.textAreaFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withMinLength(minLength)
      .withMaxLength(maxLength)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripAttributeValueField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.attributeValueFieldBuilder()
      .withName(name)
      .withDescription(description)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripSectionBreakField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.sectionBreakFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withContent(content)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripImageField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.imageFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withContent(content)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripYouTubeField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.youTubeFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withContent(content)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripRichTextField()
  {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Preferred label";
    String content = "Content";

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.richTextFieldBuilder()
      .withName(name)
      .withDescription(description)
      .withContent(content)
      .withPreferredLabel(preferredLabel)
      .build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

  @Test public void testRoundTripHuBMAPSampleBlock()
  {
    testRoundTripFromFile("templates/SampleBlock.json");
  }

  @Test public void testRoundTripHuBMAPSampleSection()
  {
    testRoundTripFromFile("templates/SampleSection.json");
  }

  @Test public void testRoundTripHuBMAPSampleSuspension()
  {
    testRoundTripFromFile("templates/SampleSuspension.json");
  }

//  @Test public void testRoundTripADVANCE()
//  {
//    testRoundTripFromFile("ADVANCETemplate.json");
//  }

  private void testRoundTripFromFile(String fileName)
  {
    ObjectNode originalRendering = getJSONFileContentAsObjectNode(fileName);

    assertTrue(validateJsonSchema(originalRendering));

    TemplateSchemaArtifact originalTemplateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(originalTemplateSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    TemplateSchemaArtifact finalTemplateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(finalRendering);

    assertEquals(originalTemplateSchemaArtifact, finalTemplateSchemaArtifact);
  }

  private ObjectNode getJSONFileContentAsObjectNode(String jsonFileName)
  {
    try {
      JsonNode jsonNode = mapper.readTree(new File(
        JsonSchemaArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));

      if (jsonNode.isObject())
        return (ObjectNode)jsonNode;
      else
        throw new RuntimeException("Error reading JSON file " + jsonFileName + ": root node is not an ObjectNode");
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }

  private boolean validateJsonSchema(ObjectNode schemaNode)
  {
    try {
      JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
      return true;
    } catch (ProcessingException e) {
      return false;
    }
  }

}
