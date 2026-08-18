package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.report.ValidationReport;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;

public class JsonArtifactLegacySchemaCompatibilityTest
{
  private JsonArtifactReader reader;
  private JsonArtifactRenderer renderer;
  private ObjectMapper mapper;

  @BeforeEach public void setUp()
  {
    reader = new JsonArtifactReader();
    renderer = new JsonArtifactRenderer();
    mapper = new ObjectMapper();
  }

  @Test public void missingSchemaOnNestedFieldIsRestoredOnRender() throws Exception
  {
    ObjectNode source = readTemplate("templates/SimpleTemplate.json");
    ObjectNode field = (ObjectNode) source.at("/properties/Text Field");
    field.remove(JSON_SCHEMA_SCHEMA);

    ObjectNode rendered = readAndRender(source);

    assertEquals(JSON_SCHEMA_SCHEMA_IRI,
      rendered.at("/properties/Text Field/$schema").asText());
    assertValid(rendered);
  }

  @Test public void missingSchemaOnNestedMultiInstanceFieldIsRestoredOnRender() throws Exception
  {
    ObjectNode source = readTemplate("templates/SimpleTemplate.json");
    ObjectNode field = (ObjectNode) source.at("/properties/Checkbox/items");
    field.remove(JSON_SCHEMA_SCHEMA);

    ObjectNode rendered = readAndRender(source);

    assertEquals(JSON_SCHEMA_SCHEMA_IRI,
      rendered.at("/properties/Checkbox/items/$schema").asText());
    assertValid(rendered);
  }

  @Test public void missingSchemaOnNestedElementIsRestoredOnRender() throws Exception
  {
    ObjectNode source = readTemplate("templates/TemplateWithCustomPropertyLabels.json");
    ObjectNode element = (ObjectNode) source.at("/properties/Work Title");
    element.remove(JSON_SCHEMA_SCHEMA);

    ObjectNode rendered = readAndRender(source);

    assertEquals(JSON_SCHEMA_SCHEMA_IRI,
      rendered.at("/properties/Work Title/$schema").asText());
    assertValid(rendered);
  }

  @Test public void missingSchemaOnRootArtifactIsStillRejected() throws Exception
  {
    ObjectNode source = readTemplate("templates/SimpleTemplate.json");
    source.remove(JSON_SCHEMA_SCHEMA);

    assertThrows(ArtifactParseException.class, () -> reader.readTemplateSchemaArtifact(source));
  }

  @Test public void explicitWrongSchemaOnNestedArtifactIsStillRejected() throws Exception
  {
    ObjectNode source = readTemplate("templates/SimpleTemplate.json");
    ObjectNode field = (ObjectNode) source.at("/properties/Text Field");
    field.put(JSON_SCHEMA_SCHEMA, "https://json-schema.org/draft/2020-12/schema");

    assertThrows(ArtifactParseException.class, () -> reader.readTemplateSchemaArtifact(source));
  }

  @Test public void explicitNonTextSchemaOnNestedArtifactIsStillRejected() throws Exception
  {
    ObjectNode source = readTemplate("templates/SimpleTemplate.json");
    ObjectNode field = (ObjectNode) source.at("/properties/Text Field");
    field.put(JSON_SCHEMA_SCHEMA, 4);

    assertThrows(ArtifactParseException.class, () -> reader.readTemplateSchemaArtifact(source));
  }

  private ObjectNode readAndRender(ObjectNode source)
  {
    TemplateSchemaArtifact template = reader.readTemplateSchemaArtifact(source);
    return renderer.renderTemplateSchemaArtifact(template);
  }

  private void assertValid(ObjectNode artifact) throws Exception
  {
    ValidationReport report = new CedarValidator().validateTemplate(artifact);
    assertEquals("true", report.getValidationStatus(), report.getErrors().toString());
  }

  private ObjectNode readTemplate(String resource) throws IOException
  {
    File file = new File(JsonArtifactLegacySchemaCompatibilityTest.class.getClassLoader().getResource(resource).getFile());
    return (ObjectNode) mapper.readTree(file);
  }
}
