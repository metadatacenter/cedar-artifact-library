package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.RichTextField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.ModelValidator;
import org.metadatacenter.model.validation.report.ValidationReport;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Which children may carry a line placement, and which may not.
 *
 * The CEDAR model gives {@code continuePreviousLine} to a dynamic field, in
 * {@code literalFieldUIContent} and {@code iriFieldUIContent}. An element's {@code _ui} takes an
 * order, property labels and property descriptions; a static field's takes an input type, content,
 * a size and a hidden flag. Both close with {@code additionalProperties: false}, so a rendering
 * that states the setting in either place is one the validation library rejects — which is what
 * this library rendered for a static field whose builder had been asked for one. The setters are
 * gone from the static builders, so that artifact can no longer be built; a document that states
 * it anyway is read past, the way a document stating it for an element always has been.
 */
public class ChildLinePlacementTest
{
  private static final String TEMPLATE_YAML = """
    type: template
    name: "T"
    description: "d"
    id: "https://repo.metadatacenter.org/templates/00000000-0000-0000-0000-000000000000"
    status: draft
    version: 0.0.1
    modelVersion: 1.6.0
    children:
      - key: "el"
        type: element
        name: "el"
        id: "https://repo.metadatacenter.org/template-elements/00000000-0000-0000-0000-000000000001"
        status: draft
        version: 0.0.1
        modelVersion: 1.6.0
        children:
          - key: "inner"
            type: text-field
            name: "inner"
            id: "https://repo.metadatacenter.org/template-fields/00000000-0000-0000-0000-000000000002"
            modelVersion: 1.6.0
        configuration:
          continuePreviousLine: true
      - key: "rt"
        type: static-rich-text
        name: "rt"
        id: "https://repo.metadatacenter.org/template-fields/00000000-0000-0000-0000-000000000003"
        modelVersion: 1.6.0
        content: "<p>hello</p>"
        configuration:
          continuePreviousLine: true
      - key: "f"
        type: text-field
        name: "f"
        id: "https://repo.metadatacenter.org/template-fields/00000000-0000-0000-0000-000000000004"
        modelVersion: 1.6.0
        configuration:
          continuePreviousLine: true
    """;

  private YamlArtifactReader yamlReader;
  private JsonArtifactRenderer jsonRenderer;
  private ModelValidator validator;

  @BeforeEach public void setup()
  {
    yamlReader = new YamlArtifactReader();
    jsonRenderer = new JsonArtifactRenderer();
    validator = new CedarValidator();
  }

  private TemplateSchemaArtifact template()
  {
    @SuppressWarnings("unchecked") LinkedHashMap<String, Object> yamlMap =
      (LinkedHashMap<String, Object>) new Yaml().load(TEMPLATE_YAML);
    return yamlReader.readTemplateSchemaArtifact(yamlMap);
  }

  private ObjectNode ui(ObjectNode rendering, String childName)
  {
    return (ObjectNode) rendering.get("properties").get(childName).get("_ui");
  }

  @Test public void testStaticFieldAndElementCarryNoLinePlacementIntoJson()
  {
    ObjectNode rendering = jsonRenderer.renderTemplateSchemaArtifact(template());

    assertFalse(ui(rendering, "rt").has("continuePreviousLine"));
    assertFalse(ui(rendering, "el").has("continuePreviousLine"));
  }

  @Test public void testDynamicFieldKeepsItsOwn()
  {
    ObjectNode rendering = jsonRenderer.renderTemplateSchemaArtifact(template());

    assertTrue(ui(rendering, "f").get("continuePreviousLine").asBoolean());
  }

  @Test public void testRenderedTemplateIsValid() throws Exception
  {
    ValidationReport report = validator.validateTemplate(jsonRenderer.renderTemplateSchemaArtifact(template()));

    assertEquals("true", report.getValidationStatus(), report.getErrors().toString());
  }

  @Test public void testTheSameHoldsForTheYamlRendering()
  {
    LinkedHashMap<String, Object> rendering = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(template());

    assertFalse(configurationOf(rendering, "rt").containsKey("continuePreviousLine"));
    assertFalse(configurationOf(rendering, "el").containsKey("continuePreviousLine"));
    assertEquals(true, configurationOf(rendering, "f").get("continuePreviousLine"));
  }

  @Test public void testAStaticFieldBuiltHereReportsNoLinePlacement()
  {
    // The builder has no withContinuePreviousLine to call, so this is the only answer it can give.
    assertFalse(RichTextField.builder().withName("rt").withContent("<p>hello</p>").build().fieldUi().continuePreviousLine());
  }

  @SuppressWarnings("unchecked")
  private LinkedHashMap<String, Object> configurationOf(LinkedHashMap<String, Object> rendering, String childKey)
  {
    for (Object child : (List<Object>) rendering.get("children")) {
      LinkedHashMap<String, Object> childMap = (LinkedHashMap<String, Object>) child;
      if (childKey.equals(childMap.get("key"))) {
        LinkedHashMap<String, Object> configuration = (LinkedHashMap<String, Object>) childMap.get("configuration");
        return configuration == null ? new LinkedHashMap<>() : configuration;
      }
    }
    throw new AssertionError("no child keyed " + childKey + " in the rendering");
  }
}
