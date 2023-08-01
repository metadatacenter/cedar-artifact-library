package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.ModelNodeNames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtifactRendererTest
{
  private ArtifactRenderer artifactRenderer;

  @Before
  public void setUp() {
    artifactRenderer = new ArtifactRenderer();
  }

  @Test
  public void testRenderTemplateSchemaArtifact() {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withName("Study").
      build();

    ObjectNode rendering = artifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJSONSchema(rendering));

    assertEquals(rendering.get(ModelNodeNames.JSON_SCHEMA_SCHEMA).textValue(), ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);

    System.out.println(rendering.toPrettyString());

  }

  private boolean validateJSONSchema(ObjectNode schemaNode)
  {
    try {
      JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
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
}
