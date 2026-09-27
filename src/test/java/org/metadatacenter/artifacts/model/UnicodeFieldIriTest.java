package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import static org.junit.jupiter.api.Assertions.*;

class UnicodeFieldIriTest {
  private ObjectNode source(String iri) {
    var mapper = new ObjectMapper();
    var source = mapper.createObjectNode().put("schema:name", "IRI example")
        .put("schema:isBasedOn", "https://example.org/template");
    source.putObject("country").put("@id", iri).put("rdfs:label", "Niger (NER)");
    return source;
  }

  @ParameterizedTest
  @ValueSource(strings = {"https://example.org/Niger\u00a0NER", "https://example.org/Niger%C2%A0NER",
      "https://example.org/café", "https://example.org/\u2003term", "https://example.org/\uD83D\uDE00",
      "https://example.org/?q=\ue000", "urn:example:Niger\u00a0NER"})
  void preservesRdfSpellingThroughBothReadersAndWriters(String iri) {
    var model = new JsonArtifactReader().readTemplateInstanceArtifact(source(iri));
    assertEquals(iri, model.singleInstanceFieldInstances().get("country").jsonLdIdIri().orElseThrow());
    for (boolean compact : new boolean[] {false, true}) {
      var yaml = new YamlArtifactRenderer(compact).renderTemplateInstanceArtifact(model);
      var restored = new YamlArtifactReader(compact).readTemplateInstanceArtifact(yaml);
      assertEquals(iri, new JsonArtifactRenderer().renderTemplateInstanceArtifact(restored)
          .path("country").path("@id").asText());
    }
  }

  @Test void controlledTermBuilderAndCopyPreserveIriIdentity() {
    String iri = "https://example.org/Niger\u00a0NER";
    var term = org.metadatacenter.artifacts.model.core.ControlledTermFieldInstance.builder()
        .withIriValue(iri).withLabel("Niger (NER)").build();
    var copy = org.metadatacenter.artifacts.model.core.ControlledTermFieldInstance.builder(term).build();
    assertEquals(iri, copy.jsonLdIdIri().orElseThrow());
    assertEquals(term, copy);
    var changed = org.metadatacenter.artifacts.model.core.ControlledTermFieldInstance.builder(term)
        .withValue(java.net.URI.create("urn:replacement")).build();
    assertEquals("urn:replacement", changed.jsonLdIdIri().orElseThrow());
  }

  @Test void encodedAndRawTermsRemainDistinctInTheModel() {
    var reader = new JsonArtifactReader();
    var raw = reader.readTemplateInstanceArtifact(source("https://example.org/Niger\u00a0NER"));
    var encoded = reader.readTemplateInstanceArtifact(source("https://example.org/Niger%C2%A0NER"));
    assertNotEquals(raw.singleInstanceFieldInstances().get("country"), encoded.singleInstanceFieldInstances().get("country"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "https://example.org/a b", "https://example.org/a\tb", "https://example.org/\u0085",
      "https://example.org/%xx", "https://example.org/a#b#c", "://example.org/a", "https://example.org/\ud800",
      "https://example.org/\uffff", "https://example.org/\ue000", "https://example.org/#\ue000"})
  void rejectsMalformedFieldIdentifiersInBothReaders(String iri) {
    assertThrows(RuntimeException.class, () -> new JsonArtifactReader().readTemplateInstanceArtifact(source(iri)));
    var valid = new JsonArtifactReader().readTemplateInstanceArtifact(source("https://example.org/term"));
    var yaml = new YamlArtifactRenderer(false).renderTemplateInstanceArtifact(valid);
    // Renderer structure is exercised by the valid round-trip above; replace the field's actual map.
    replaceId(yaml, iri);
    assertThrows(RuntimeException.class, () -> new YamlArtifactReader().readTemplateInstanceArtifact(yaml));
  }

  private void replaceId(java.util.Map<String, Object> node, String iri) {
    for (var entry : node.entrySet()) {
      if (entry.getKey().equals("id") && entry.getValue().equals("https://example.org/term")) entry.setValue(iri);
      else if (entry.getValue() instanceof java.util.Map<?, ?> nested) replaceId((java.util.Map<String, Object>) nested, iri);
      else if (entry.getValue() instanceof java.util.List<?> list)
        for (Object item : list) if (item instanceof java.util.Map<?, ?> nested) replaceId((java.util.Map<String, Object>) nested, iri);
    }
  }
}
