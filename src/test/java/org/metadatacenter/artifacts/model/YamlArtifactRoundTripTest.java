package org.metadatacenter.artifacts.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class YamlArtifactRoundTripTest
{
  private YamlArtifactReader yamlArtifactReader = new YamlArtifactReader();
  private YamlArtifactRenderer yamlArtifactRenderer;

  @Before public void setUp()
  {
    yamlArtifactReader = new YamlArtifactReader();
    yamlArtifactRenderer = new YamlArtifactRenderer(false);
  }

  @Test public void testRoundTripSimpleTemplate()
  {
    TemplateSchemaArtifact originalTemplateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).withName("Study").build();

    testRoundTripTemplateSchemaArtifact(originalTemplateSchemaArtifact);
  }

  @Test public void testRoundTripSimpleElementSchemaArtifact()
  {
    ElementSchemaArtifact originalElementSchemaArtifact = ElementSchemaArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123")).withName("Study").build();

    testRoundTripElementSchemaArtifact(originalElementSchemaArtifact);
  }

  // TODO Need to activate this
  @Ignore @Test public void testRoundTripTextField()
  {
    TextField originalFieldSchemaArtifact = TextField.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).withName("Study")
      .withDefaultValue("A default value").withRegex("*").build();

    testRoundTripFieldSchemaArtifact(originalFieldSchemaArtifact);
  }

  private void testRoundTripTemplateSchemaArtifact(TemplateSchemaArtifact originalTemplateSchemaArtifact)
  {
    LinkedHashMap<String, Object> originalRendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(
      originalTemplateSchemaArtifact);

    TemplateSchemaArtifact finalTemplateSchemaArtifact = yamlArtifactReader.readTemplateSchemaArtifact(
      originalRendering);

    assertEquals(originalTemplateSchemaArtifact, finalTemplateSchemaArtifact);
  }

  private void testRoundTripElementSchemaArtifact(ElementSchemaArtifact originalElementSchemaArtifact)
  {
    LinkedHashMap<String, Object> originalRendering = yamlArtifactRenderer.renderElementSchemaArtifact(
      originalElementSchemaArtifact);

    ElementSchemaArtifact finalElementSchemaArtifact = yamlArtifactReader.readElementSchemaArtifact(originalRendering);

    assertEquals(originalElementSchemaArtifact, finalElementSchemaArtifact);
  }

  private void testRoundTripFieldSchemaArtifact(FieldSchemaArtifact originalFieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> originalRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(
      originalFieldSchemaArtifact);

    FieldSchemaArtifact finalFieldSchemaArtifact = yamlArtifactReader.readFieldSchemaArtifact(originalRendering);

    assertEquals(originalFieldSchemaArtifact, finalFieldSchemaArtifact);
  }

}
