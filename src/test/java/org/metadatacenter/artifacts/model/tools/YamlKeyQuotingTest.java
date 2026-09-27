package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class YamlKeyQuotingTest {
  @Test void sharedKeyCorpusPreservesNamesAndUsesExplicitPolicy() throws Exception {
    ObjectMapper json = new ObjectMapper();
    JsonNode cases = json.readTree(getClass().getResourceAsStream("/yaml-key-quoting.json"));
    YamlScalarQuotingChecker checker = new YamlScalarQuotingChecker();
    for (boolean minimize : new boolean[] {false, true}) {
      ObjectMapper yaml = new ObjectMapper(YAMLFactory.builder().stringQuotingChecker(checker).build()
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, minimize)
        .disable(YAMLGenerator.Feature.SPLIT_LINES));
      for (JsonNode item : cases) {
        String key = item.get("key").asText();
        boolean quoted = item.get("quoted").asBoolean();
        assertEquals(quoted, checker.needToQuoteName(key), key);
        String output = yaml.writeValueAsString(Map.of(key, "value"));
        assertEquals(quoted, (output.startsWith("\"") || output.startsWith("? \"")), output);
        assertEquals(Map.of(key, "value"), yaml.readValue(output, Map.class), output);
      }
    }
  }
}
