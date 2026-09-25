package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;
import java.util.LinkedHashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class YamlChildNamesTest {
  @Test void keywordsSurviveRealYamlSerialization() throws Exception {
    var names = List.of("true", "false", "yes", "no", "on", "off", "null", "~", "<<", "=",
        "123", "2026-09-25", "type", "name", "key", "children", "value", "description");
    var builder = TemplateSchemaArtifact.builder().withName("Keyword field names");
    for (String name : names) builder.withFieldSchema(TextField.builder().withName(name).build());
    var original = builder.build();
    String yaml = YamlSerializer.getYAML(original, false, true);
    var map = new ObjectMapper(new YAMLFactory()).readValue(yaml, LinkedHashMap.class);
    var restored = new YamlArtifactReader().readTemplateSchemaArtifact(map);
    assertEquals(names, List.copyOf(restored.fieldSchemas().keySet()));
    for (String name : names) assertEquals(name, restored.fieldSchemas().get(name).name());
    assertTrue(yaml.contains("key: \"true\""));
    assertTrue(yaml.contains("key: \"null\""));
  }

  @Test void reservedInstanceNamesCannotEnterThroughYaml() throws Exception {
    for (String name : List.of("@value", "@id", "schema:name", "rdfs:label")) {
      var template = TemplateSchemaArtifact.builder().withName("Example")
          .withFieldSchema(TextField.builder().withName("Field").build()).build();
      var map = new org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer(false)
          .renderTemplateSchemaArtifact(template);
      ((LinkedHashMap<String, Object>) ((List<?>) map.get("children")).get(0)).put("key", name);
      var reader = new YamlArtifactReader();
      assertTrue(assertThrows(ArtifactParseException.class,
          () -> reader.readTemplateSchemaArtifact(map)).getMessage().contains("reserved instance property name"));
      map.put("type", "element");
      assertTrue(assertThrows(ArtifactParseException.class,
          () -> reader.readElementSchemaArtifact(map)).getMessage().contains("reserved instance property name"));
    }
  }
}
