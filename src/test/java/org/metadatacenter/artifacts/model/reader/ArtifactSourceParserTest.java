package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.model.policy.JsonSourcePolicy;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class ArtifactSourceParserTest {
  @Test void normativeSourceCorpus() throws Exception {
    var mapper = JsonSourcePolicy.apply(new ObjectMapper());
    try (var input = getClass().getResourceAsStream("/concordance/source-policy.json")) {
      for (var row : mapper.readTree(input).get("cases")) {
        String source = row.get("source").asText(), format = row.get("format").asText();
        String id = row.get("id").asText();
        if (row.get("expected").isNull()) {
          assertThrows(IOException.class, () -> {
            if (format.equals("json")) ArtifactSourceParser.json(source);
            else ArtifactSourceParser.yaml(source);
          }, id);
        } else {
          Object parsed = format.equals("json") ? ArtifactSourceParser.json(source) : ArtifactSourceParser.yaml(source);
          var rendered = mapper.valueToTree(parsed);
          assertEquals(mapper.readTree(row.get("expected").asText()), rendered, id);
          assertEquals(rendered, ArtifactSourceParser.json(mapper.writeValueAsString(parsed)), id);
        }
      }
    }
  }
}
