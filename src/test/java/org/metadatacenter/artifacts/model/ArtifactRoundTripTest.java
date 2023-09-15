package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementUi;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.NumericType;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReaderTest;
import org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactRenderer;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
  public void testRoundTripElementSchemaArtifact() {
    ElementSchemaArtifact originalElementSchemaArtifact = ElementSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123")).
      withName("Study").
      withElementUi(ElementUi.builder().build()).
      build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderElementSchemaArtifact(originalElementSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    ElementSchemaArtifact finalElementSchemaArtifact = artifactReader.readElementSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderElementSchemaArtifact(finalElementSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalElementSchemaArtifact, finalElementSchemaArtifact);
  }

  @Test
  public void testRoundTripTextField() {
    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).
      withName("Study").
      build();

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
    NumericType numericType = NumericType.DOUBLE;
    Number minValue = 0.0;
    Number maxValue = 100.0;

    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder().
      withName(name).
      withDescription(description).
      withNumericType(numericType).
      withMinValue(minValue).
      withMaxValue(maxValue).
      build();

    ObjectNode originalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
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
