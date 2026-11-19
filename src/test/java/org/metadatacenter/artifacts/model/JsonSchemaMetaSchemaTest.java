package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonSchemaMetaSchemaTest
{
  @Test void aRenderedTemplateIsAJsonSchema()
  {
    var template = TemplateSchemaArtifact.builder().withName("T")
      .withFieldSchema(TextField.builder().withName("Title").build()).build();
    var rendering = new JsonArtifactRenderer().renderTemplateSchemaArtifact(template);
    assertTrue(JsonSchemaMetaSchema.violations(rendering).isEmpty(), JsonSchemaMetaSchema.violations(rendering).toString());
  }

  @Test void aDocumentThatOnlyCompilesIsNot() throws Exception
  {
    var mapper = new ObjectMapper();
    assertFalse(JsonSchemaMetaSchema.violations(mapper.readTree("{\"type\": 42}")).isEmpty());
    assertFalse(JsonSchemaMetaSchema.violations(mapper.readTree("{\"required\": \"Title\"}")).isEmpty());
    assertFalse(JsonSchemaMetaSchema.violations(mapper.readTree("{\"properties\": {\"Title\": 7}}")).isEmpty());
  }
}
