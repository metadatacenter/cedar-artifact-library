package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementUi;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.model.reader.ArtifactReaderTest;
import org.metadatacenter.artifacts.model.renderer.ArtifactRenderer;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtifactRoundTripTest
{
  private ArtifactReader artifactReader = new ArtifactReader();
  private ArtifactRenderer artifactRenderer;
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    artifactReader = new ArtifactReader();
    artifactRenderer = new ArtifactRenderer();
    mapper = new ObjectMapper();
  }

  @Test
  public void testRoundTripTemplateSchemaArtifact() {
    TemplateSchemaArtifact originalTemplateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName("Study").
      build();

    ObjectNode originalRendering = artifactRenderer.renderTemplateSchemaArtifact(originalTemplateSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    TemplateSchemaArtifact finalTemplateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(originalRendering);

    ObjectNode finalRendering = artifactRenderer.renderTemplateSchemaArtifact(finalTemplateSchemaArtifact);

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

    ObjectNode originalRendering = artifactRenderer.renderElementSchemaArtifact(originalElementSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    ElementSchemaArtifact finalElementSchemaArtifact = artifactReader.readElementSchemaArtifact(originalRendering);

    ObjectNode finalRendering = artifactRenderer.renderElementSchemaArtifact(finalElementSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalElementSchemaArtifact, finalElementSchemaArtifact);
  }

  @Test
  public void testRoundTripFieldSchemaArtifact() {
    FieldSchemaArtifact originalFieldSchemaArtifact = FieldSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).
      withName("Study").
      withFieldUi(FieldUi.builder().withInputType(FieldInputType.TEXTFIELD).build()).
      build();

    ObjectNode originalRendering = artifactRenderer.renderFieldSchemaArtifact(originalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(originalRendering));

    FieldSchemaArtifact finalFieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(originalRendering);

    ObjectNode finalRendering = artifactRenderer.renderFieldSchemaArtifact(finalFieldSchemaArtifact);

    assertTrue(validateJsonSchema(finalRendering));

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }
  
  private ObjectNode getJSONFileContentAsObjectNode(String jsonFileName)
  {
    try {
      JsonNode jsonNode = mapper.readTree(new File(ArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));

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
