package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class YamlArtifactRoundTripTest
{
  private YamlArtifactReader yamlArtifactReader = new YamlArtifactReader();
  private YamlArtifactRenderer yamlArtifactRenderer;
  private ObjectMapper mapper;

  @Before public void setUp()
  {
    yamlArtifactReader = new YamlArtifactReader();
    yamlArtifactRenderer = new YamlArtifactRenderer(false);
    mapper = new ObjectMapper();
  }

  @Test public void testRoundTripSimpleTemplate()
  {
    TemplateSchemaArtifact originalTemplateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).withName("Study").build();

    testRoundTripTemplateSchemaArtifact(originalTemplateSchemaArtifact);
  }

  private void testRoundTripTemplateSchemaArtifact(TemplateSchemaArtifact originalTemplateSchemaArtifact)
  {
    LinkedHashMap<String, Object> originalRendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(
      originalTemplateSchemaArtifact);

    TemplateSchemaArtifact finalTemplateSchemaArtifact = yamlArtifactReader.readTemplateSchemaArtifact(
      originalRendering);

    assertEquals(originalTemplateSchemaArtifact, finalTemplateSchemaArtifact);
  }

}
