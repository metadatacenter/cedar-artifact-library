package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.tools.InstanceInflater;
import org.metadatacenter.model.validation.CedarValidator;

import java.net.URI;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AttributeGroupPropertyIriTest {
  private static final URI ID = URI.create("https://repo.metadatacenter.org/templates/2adc9a19-6132-4525-bd59-341e6a09f4e0");
  private final JsonArtifactRenderer json = new JsonArtifactRenderer();

  @Test void groupMappingsSurviveBothFormatsWithoutBecomingInstanceRequirements() throws Exception {
    for (String iri : Arrays.asList(null,
        "https://schema.metadatacenter.org/properties/d01cb533-265c-474a-95f3-9afb4616a6e1",
        "https://w3id.org/radx/radmo/auxiliaryMetadataKeyValuePair")) {
      var group = AttributeValueField.builder().withName("attributes");
      if (iri != null) group.withPropertyUri(URI.create(iri));
      var ordinary = TextField.builder().withName("text").withPropertyUri(URI.create("urn:text")).build();
      var element = ElementSchemaArtifact.builder().withName("nested").withPropertyUri(URI.create("urn:nested"))
          .withFieldSchema(ordinary).withFieldSchema(group.build()).build();
      var template = TemplateSchemaArtifact.builder().withName("Groups").withJsonLdId(ID)
          .withFieldSchema(ordinary).withFieldSchema(group.build()).withElementSchema(element).build();
      ObjectNode rendered = json.renderTemplateSchemaArtifact(template);
      ObjectNode renderedElement = json.renderElementSchemaArtifact(element);
      assertContext(rendered, iri);
      assertContext(renderedElement, iri);
      assertContext(rendered.path("properties").path("nested"), iri);
      assertEquals(rendered, json.renderTemplateSchemaArtifact(new JsonArtifactReader().readTemplateSchemaArtifact(rendered)));
      assertEquals(renderedElement, json.renderElementSchemaArtifact(new JsonArtifactReader().readElementSchemaArtifact(renderedElement)));
      var yaml = new YamlArtifactRenderer(false);
      var reader = new YamlArtifactReader();
      assertEquals(rendered, json.renderTemplateSchemaArtifact(reader.readTemplateSchemaArtifact(yaml.renderTemplateSchemaArtifact(template))));
      assertEquals(renderedElement, json.renderElementSchemaArtifact(reader.readElementSchemaArtifact(yaml.renderElementSchemaArtifact(element))));
      var validator = new CedarValidator();
      var report = validator.validateTemplate(rendered);
      assertEquals("true", report.getValidationStatus(), report.getErrors().toString());
      var sparse = TemplateInstanceArtifact.builder().withIsBasedOn(ID).withName("metadata").withDescription("")
          .withSingleInstanceElementInstance("nested", ElementInstanceArtifact.builder()
              .withJsonLdId(URI.create("https://repo.metadatacenter.org/template-element-instances/8feaa3a4-7a2d-48d3-8a19-478562bb1bfa")).build()).build();
      var inflated = InstanceInflater.inflate(template, sparse);
      assertFalse(inflated.jsonLdContext().containsKey("attributes"));
      var instanceJson = json.renderTemplateInstanceArtifact(inflated);
      assertFalse(instanceJson.path("nested").path("@context").has("attributes"));
      assertEquals("urn:text", instanceJson.path("@context").path("text").asText());
      var instanceReport = validator.validateTemplateInstance(instanceJson, rendered);
      assertEquals("true", instanceReport.getValidationStatus(), instanceReport.getErrors().toString());
      assertFalse(InstanceInflater.emptyElement(element).jsonLdContext().containsKey("attributes"));
    }
  }

  private void assertContext(JsonNode schema, String iri) {
    JsonNode context = schema.path("properties").path("@context");
    if (iri == null) assertFalse(context.path("properties").has("attributes"));
    else assertEquals(iri, context.path("properties").path("attributes").path("enum").path(0).asText());
    assertFalse(context.path("required").toString().contains("\"attributes\""));
    assertTrue(context.path("required").toString().contains("\"text\""));
  }
}
