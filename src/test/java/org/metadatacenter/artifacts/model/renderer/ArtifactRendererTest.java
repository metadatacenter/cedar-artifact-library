package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.ModelNodeNames;

import static org.junit.Assert.assertEquals;

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

    assertEquals(rendering.get(ModelNodeNames.JSON_SCHEMA_SCHEMA).textValue(), ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);

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
