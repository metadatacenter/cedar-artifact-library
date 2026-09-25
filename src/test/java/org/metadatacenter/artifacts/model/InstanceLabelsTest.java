package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermFieldInstance;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import java.net.URI;
import org.junit.jupiter.params.provider.ValueSource;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import static org.junit.jupiter.api.Assertions.*;

class InstanceLabelsTest {
  @Test void builtControlledTermLabelDoesNotInventALiteral() {
    var instance = TemplateInstanceArtifact.builder().withName("Example")
      .withIsBasedOn(URI.create("https://example.org/template"))
      .withSingleInstanceFieldInstance("field", ControlledTermFieldInstance.builder().withLabel("Term").build()).build();
    var document = new YamlArtifactRenderer(false).renderTemplateInstanceArtifact(instance);
    var restored = new YamlArtifactReader().readTemplateInstanceArtifact(document);
    var field = new JsonArtifactRenderer().renderTemplateInstanceArtifact(restored).get("field");
    assertEquals("Term", field.get("rdfs:label").asText());
    assertFalse(field.has("@value"));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "{\"@value\":\"literal\",\"rdfs:label\":\"Personal\"}",
    "{\"@value\":null,\"rdfs:label\":\"High-quality MAG\"}",
    "{\"@value\":null,\"rdfs:label\":\"\",\"@type\":\"xsd:string\"}",
    "{\"rdfs:label\":\"Category\"}",
    "{\"rdfs:label\":\"\"}"
  })
  void preservesLabelsAndLiteralPresence(String fieldJson) throws Exception {
    var mapper = new ObjectMapper();
    var field = mapper.readTree(fieldJson);
    var source = mapper.createObjectNode().put("schema:name", "Example")
      .put("schema:isBasedOn", "https://example.org/template");
    source.set("field", field);
    var instance = new JsonArtifactReader().readTemplateInstanceArtifact(source);
    for (boolean compact : new boolean[] {false, true}) {
      var writer = new YamlArtifactRenderer(compact);
      var document = writer.renderTemplateInstanceArtifact(instance);
      var restored = new YamlArtifactReader(compact).readTemplateInstanceArtifact(document);
      assertEquals(field, new JsonArtifactRenderer().renderTemplateInstanceArtifact(restored).get("field"));
      assertEquals(document, writer.renderTemplateInstanceArtifact(restored));
    }
  }
}
