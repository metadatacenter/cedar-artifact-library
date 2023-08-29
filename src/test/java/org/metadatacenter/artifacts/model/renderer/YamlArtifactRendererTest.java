package org.metadatacenter.artifacts.model.renderer;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class YamlArtifactRendererTest {

  @Test
  public void testRenderTemplateSchemaArtifact() {

    String name = "Study";
    String description = "Study template";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(YamlArtifactRenderer.TEMPLATE, name);
    expectedRendering.put(YamlArtifactRenderer.DESCRIPTION, description);

    assertEquals(expectedRendering, rendering);
  }

  @Test
  public void testRenderElementSchemaArtifact() {

    String name = "Address";
    String description = "Address element";

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123")).
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderElementSchemaArtifact(elementSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(YamlArtifactRenderer.ELEMENT, name);
    expectedRendering.put(YamlArtifactRenderer.DESCRIPTION, description);

    assertEquals(expectedRendering, rendering);
  }

  @Test
  public void testRenderFieldSchemaArtifact() {

    String name = "Study Name";
    String description = "Study name field";
    FieldInputType fieldInputType = FieldInputType.TEXTFIELD;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).
      withName(name).
      withDescription(description).
      withFieldUi(FieldUi.builder().withInputType(fieldInputType).build()).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(YamlArtifactRenderer.FIELD, name);
    expectedRendering.put(YamlArtifactRenderer.DESCRIPTION, description);
    expectedRendering.put(YamlArtifactRenderer.TYPE, fieldInputType);

    assertEquals(expectedRendering, rendering);
  }

}