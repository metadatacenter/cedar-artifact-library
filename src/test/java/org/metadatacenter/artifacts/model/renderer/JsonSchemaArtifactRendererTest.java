package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReaderTest;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.RDFS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE_TERM_URI;

public class JsonSchemaArtifactRendererTest
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
  public void testRenderTemplateSchemaArtifact() {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName("Study").
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), "Study");
  }

  @Test public void testRenderTemporalField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    boolean requiredValue = false;
    TemporalGranularity granularity = TemporalGranularity.DAY;
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.DATE;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.temporalFieldBuilder().
            withName(fieldName).
            withDescription(fieldDescription).
            withRequiredValue(requiredValue).
            withTemporalGranularity(granularity).
            withTemporalType(temporalType).
            build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    //assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).textValue(), defaultURI.toString());
    // CEDAR requires the follow keys to be present, even if the values are null
    assertTrue(rendering.has(PAV_CREATED_BY));
    assertTrue(rendering.has(PAV_CREATED_ON));
    assertTrue(rendering.has(PAV_LAST_UPDATED_ON));
    assertTrue(rendering.has(OSLC_MODIFIED_BY));
    assertTrue(rendering.has(JSON_LD_ID));
    // catch potential error case where "requiredValue": false does not render
    assertFalse(rendering.get("_valueConstraints").get("requiredValue").asBoolean());
    assertEquals(rendering.get("_valueConstraints").get("temporalType").textValue(), temporalType.getText());
    assertEquals(rendering.get("_ui").get("temporalGranularity").textValue(), granularity.getText());
    assertTrue(rendering.get("_ui").has("timezoneEnabled"));
  }

  @Test public void testRenderNumericField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    boolean requiredValue = false;
    XsdNumericDatatype numericType = XsdNumericDatatype.DECIMAL;
    int decimalPlaces = 3;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder().
            withName(fieldName).
            withDescription(fieldDescription).
            withRequiredValue(requiredValue).
            withNumericType(numericType).
            withDecimalPlaces(decimalPlaces).
            build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    //assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).textValue(), defaultURI.toString());
    // CEDAR requires the follow keys to be present, even if the values are null
    assertTrue(rendering.has(PAV_CREATED_BY));
    assertTrue(rendering.has(PAV_CREATED_ON));
    assertTrue(rendering.has(PAV_LAST_UPDATED_ON));
    assertTrue(rendering.has(OSLC_MODIFIED_BY));
    assertTrue(rendering.has(JSON_LD_ID));
    // catch potential error case where "requiredValue": false does not render
    assertFalse(rendering.get("_valueConstraints").get("requiredValue").asBoolean());
    assertEquals(rendering.get("_valueConstraints").get("numberType").textValue(), numericType.getText());
    assertEquals(rendering.get("_valueConstraints").get("decimalPlace").asInt(), decimalPlaces);
  }

  @Test public void testRenderTextField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    String defaultValue = "default value";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder().
      withName(fieldName).
      withDescription(fieldDescription).
      withDefaultValue(defaultValue).
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    //assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).textValue(), defaultURI.toString());
    // CEDAR requires the follow keys to be present, even if the values are null
    assertTrue(rendering.has(PAV_CREATED_BY));
    assertTrue(rendering.has(PAV_CREATED_ON));
    assertTrue(rendering.has(PAV_LAST_UPDATED_ON));
    assertTrue(rendering.has(OSLC_MODIFIED_BY));
    assertTrue(rendering.has(JSON_LD_ID));
    // catch potential error case where "requiredValue": false (default) does not render
    assertFalse(rendering.get("_valueConstraints").get("requiredValue").asBoolean());
  }

  @Test public void testRenderLinkField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    URI defaultURI = URI.create("https://example.com/Study");
    String defaultLabel = "Study";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.linkFieldBuilder().
      withName(fieldName).
      withDescription(fieldDescription).
      withDefaultValue(defaultURI, defaultLabel).
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(VALUE_CONSTRAINTS_DEFAULT_VALUE_TERM_URI).textValue(), defaultURI.toString());
    assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(RDFS_LABEL).textValue(), defaultLabel);
  }

  @Test
  public void testRenderTemplateInstanceArtifact() {
    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().
      withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/123")).
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template-instances/333")).
      withName("SDY232").
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), "SDY232");
  }

  @Test
  public void testValidateInstanceAgainstTemplateArtifact() {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withName("Study").
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      build();

    ObjectNode templateRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().
      withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/123")).
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template-instances/333")).
      withName("SDY232").
      build();

    ObjectNode instanceRendering = jsonSchemaArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    assertTrue(validateJsonSchema(templateRendering, instanceRendering));
  }

  @Test
  public void testRenderHuBMAPSampleSection()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("SampleSection.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    ObjectNode templateRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJsonSchema(templateRendering));

    //System.out.println(templateRendering.toPrettyString());
  }

  @Test
  public void testRenderRADxMetadataSpecification()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("RADxMetadataSpecification.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    ObjectNode templateRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJsonSchema(templateRendering));

    //System.out.println(templateRendering.toPrettyString());
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

  private boolean validateJsonSchema(ObjectNode schemaNode, ObjectNode instanceNode)
  {
    try {
      JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
      schema.validate(instanceNode);
      return true;
    } catch (ProcessingException e) {
      return false;
    }
  }

  @Test
  public void testRenderElementSchemaArtifact() {
    // Similar test for renderElementSchemaArtifact() if needed
  }

  @Test
  public void testRenderFieldSchemaArtifact() {
    // Similar test for renderFieldSchemaArtifact() if needed
  }

  private ObjectNode getFileContentAsObjectNode(String jsonFileName)
  {
    try {
      return (ObjectNode)mapper.readTree(new File(
        JsonSchemaArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }

}
