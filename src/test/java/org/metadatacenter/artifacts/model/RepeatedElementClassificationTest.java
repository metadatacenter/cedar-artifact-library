package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import static org.junit.jupiter.api.Assertions.*;

class RepeatedElementClassificationTest {
  private ObjectNode source(boolean reverse) throws Exception {
    var mapper = new ObjectMapper();
    var document = mapper.createObjectNode().put("schema:name", "Example")
      .put("schema:isBasedOn", "https://example.org/template");
    var identified = mapper.readTree("{\"@id\":\"https://example.org/occurrence\"}");
    var empty = mapper.readTree("{\"@context\":{},\"field\":{\"@value\":null}}");
    var array = document.putArray("items");
    array.add(reverse ? empty : identified).add(reverse ? identified : empty);
    return document;
  }

  @Test void contextualSiblingDisambiguatesIdentifierOnlyOccurrenceInEitherPosition() throws Exception {
    for (boolean reverse : new boolean[] {false, true}) {
      var instance = new JsonArtifactReader().readTemplateInstanceArtifact(source(reverse));
      assertFalse(instance.multiInstanceFieldInstances().containsKey("items"));
      assertEquals(2, instance.multiInstanceElementInstances().get("items").size());
      for (boolean compact : new boolean[] {false, true}) {
        var writer = new YamlArtifactRenderer(compact);
        var yaml = writer.renderTemplateInstanceArtifact(instance);
        var restored = new YamlArtifactReader(compact).readTemplateInstanceArtifact(yaml);
        var json = new JsonArtifactRenderer().renderTemplateInstanceArtifact(restored);
        assertEquals(2, json.get("items").size());
        assertTrue(json.get("items").get(0).has("@context"));
        assertTrue(json.get("items").get(1).has("@context"));
        if (!compact) assertEquals("https://example.org/occurrence", json.get("items").get(reverse ? 1 : 0).get("@id").asText());
        assertEquals(yaml, writer.renderTemplateInstanceArtifact(restored));
      }
    }
  }

  @Test void rejectsMixedLiteralAndElementInsteadOfLosingOne() throws Exception {
    var document = source(false);
    ((ObjectNode) document.get("items").get(0)).put("@value", "keep me");
    assertThrows(RuntimeException.class, () -> new JsonArtifactReader().readTemplateInstanceArtifact(document));
  }
}
