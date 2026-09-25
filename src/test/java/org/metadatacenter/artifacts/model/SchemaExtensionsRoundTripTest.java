package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;
import org.yaml.snakeyaml.Yaml;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class SchemaExtensionsRoundTripTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();

  private SchemaExtensions extensions() throws Exception {
    return new SchemaExtensions(new LinkedHashMap<>(java.util.Map.of("openminds", URI.create("https://openminds.om-i.org/vocab/"))),
      (ObjectNode) MAPPER.readTree("""
        {"openminds:schemaVersion":"latest", "openminds:generatedAt":"2026-02-02T12:58:16.747364+00:00",
         "openminds:termMappings":{"z":null,"emptyMap":{},"emptyList":[],"a":[true,5,"yes"],"12":{"label":"female"},"2":"first"}}
        """));
  }

  @Test void nestedMetadataSurvivesJsonAndBothYamlForms() throws Exception {
    var extensions = extensions();
    var field = TextField.builder().withName("value").withExtensions(extensions).build();
    var element = ElementSchemaArtifact.builder().withName("subject").withExtensions(extensions).withFieldSchema(field).build();
    var template = TemplateSchemaArtifact.builder().withName("study").withExtensions(extensions).withElementSchema(element).build();
    var source = JSON.renderTemplateSchemaArtifact(template);
    var read = new JsonArtifactReader().readTemplateSchemaArtifact(source);
    assertEquals(extensions, read.extensions());
    for (boolean compact : new boolean[] {false, true}) {
      String yaml = YamlSerializer.getYAML(read, compact, true);
      LinkedHashMap<String, Object> map = new Yaml().load(yaml);
      var restored = new YamlArtifactReader(compact).readTemplateSchemaArtifact(map);
      var output = JSON.renderTemplateSchemaArtifact(restored);
      for (String path : new String[] {"", "/properties/subject", "/properties/subject/properties/value"}) {
        assertEquals(source.at(path).get("openminds:termMappings"), output.at(path).get("openminds:termMappings"));
        assertEquals(source.at(path).get("openminds:generatedAt"), output.at(path).get("openminds:generatedAt"));
        assertEquals("https://openminds.om-i.org/vocab/", output.at(path + "/@context/openminds").asText());
      }
    }
    assertEquals(extensions, TemplateSchemaArtifact.builder(read).build().extensions());
    assertEquals(extensions, ElementSchemaArtifact.builder(element).build().extensions());
    assertEquals(extensions, TextField.builder(field).build().extensions());
  }

  @Test void standaloneFieldsKeepExtensionsIncludingStaticAndAttributeValueFields() throws Exception {
    for (FieldSchemaArtifact field : new FieldSchemaArtifact[] {
      TextField.builder().withName("text").build(), RichTextField.builder().withName("static").build(),
      AttributeValueField.builder().withName("attributes").build()}) {
      field = field.withExtensions(extensions());
      var jsonRead = new JsonArtifactReader().readFieldSchemaArtifact(JSON.renderFieldSchemaArtifact(field));
      assertEquals(extensions(), jsonRead.extensions());
      LinkedHashMap<String, Object> yaml = new Yaml().load(YamlSerializer.getYAML(jsonRead, false, true));
      assertEquals(extensions(), new YamlArtifactReader().readFieldSchemaArtifact(yaml).extensions());
    }
  }

  @Test void extensionMetadataIsDefensivelyCopiedAndCannotOverwriteCoreProperties() throws Exception {
    var extensions = extensions();
    extensions.prefixes().clear();
    extensions.properties().removeAll();
    assertFalse(extensions.isEmpty());
    assertEquals(java.util.List.of("2", "12", "a", "emptyList", "emptyMap", "z"),
      MAPPER.convertValue(extensions.properties().get("openminds:termMappings"), LinkedHashMap.class).keySet().stream().toList());
    assertThrows(IllegalArgumentException.class, () -> new SchemaExtensions(extensions.prefixes(), MAPPER.createObjectNode().put("schema:name", "bad")));
    assertThrows(IllegalArgumentException.class, () -> new SchemaExtensions(new LinkedHashMap<>(java.util.Map.of("schema", URI.create("urn:bad"))), MAPPER.createObjectNode()));
    assertThrows(IllegalArgumentException.class, () -> new SchemaExtensions(new LinkedHashMap<>(java.util.Map.of("openminds", URI.create("relative"))), MAPPER.createObjectNode()));
  }
}
