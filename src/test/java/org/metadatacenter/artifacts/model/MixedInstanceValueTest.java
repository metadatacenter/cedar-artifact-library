package org.metadatacenter.artifacts.model;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import java.util.LinkedHashMap;
import static org.junit.jupiter.api.Assertions.*;
class MixedInstanceValueTest {
  @Test void bothReadersRejectMixedValuesIncludingNull() throws Exception {
    for (String value : new String[] {"null", "\"https://example.org/term\"", "\"other\""}) {
      var source = new ObjectMapper().readTree("{\"schema:name\":\"Example\",\"schema:isBasedOn\":\"https://example.org/t\",\"field\":{\"@id\":\"https://example.org/term\",\"@value\":"+value+"}}");
      assertTrue(assertThrows(RuntimeException.class, () -> new JsonArtifactReader().readTemplateInstanceArtifact((com.fasterxml.jackson.databind.node.ObjectNode)source)).getMessage().contains("both @id and @value"));
      var field = new LinkedHashMap<String,Object>();field.put("id", "https://example.org/term");field.put("value", new ObjectMapper().readTree(value).isNull() ? null : new ObjectMapper().readTree(value).asText());
      field.put("label", "Term");
      var yaml = new LinkedHashMap<String,Object>();yaml.put("type","instance");yaml.put("name","Example");yaml.put("isBasedOn","https://example.org/t");yaml.put("children",new LinkedHashMap<>(java.util.Map.of("field",field)));
      assertTrue(assertThrows(RuntimeException.class, () -> new YamlArtifactReader().readTemplateInstanceArtifact(yaml)).getMessage().contains("both id and value"));
    }
  }
}
