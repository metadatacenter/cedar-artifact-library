package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.model.reader.ArtifactReaderTest;
import org.metadatacenter.model.ModelNodeNames;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;

public class ArtifactRendererTest
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
  public void testRenderTemplateSchemaArtifact() {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName("Study").
      build();

    ObjectNode rendering = artifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJSONSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), "Study");

    //System.out.println(rendering.toPrettyString());
  }

  @Test
  public void testRenderTemplateInstanceArtifact() {
    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().
      withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/123")).
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template-instances/333")).
      withName("SDY232").
      build();

    ObjectNode rendering = artifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), "SDY232");

    //System.out.println(rendering.toPrettyString());
  }

  @Test
  public void testValidateInstanceAgainstTemplateArtifact() {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withName("Study").
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      build();

    ObjectNode templateRendering = artifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().
      withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/123")).
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template-instances/333")).
      withName("SDY232").
      build();

    ObjectNode instanceRendering = artifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    assertTrue(validateJSONSchema(templateRendering, instanceRendering));
  }

  @Test
  public void testRenderSampleSectionTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("SampleSection.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    ObjectNode templateRendering = artifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJSONSchema(templateRendering));

    //System.out.println(templateRendering.toPrettyString());
  }

  @Test
  public void testRenderRADxMetadataSpecificationTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("RADxMetadataSpecification.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    ObjectNode templateRendering = artifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJSONSchema(templateRendering));

    //System.out.println(templateRendering.toPrettyString());
  }

  private boolean validateJSONSchema(ObjectNode schemaNode)
  {
    try {
      JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
      return true;
    } catch (ProcessingException e) {
      return false;
    }
  }

  private boolean validateJSONSchema(ObjectNode schemaNode, ObjectNode instanceNode)
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
      return (ObjectNode)mapper.readTree(new File(ArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }

}
