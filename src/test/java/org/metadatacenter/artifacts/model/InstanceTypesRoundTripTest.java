package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** The classes an instance and its element occurrences state as their @type survive a JSON round trip. */
class InstanceTypesRoundTripTest {
  private static final URI STUDY = URI.create("https://example.org/classes/Study");
  private static final URI DATASET = URI.create("https://example.org/classes/Dataset");
  private static final URI PERSON = URI.create("https://example.org/classes/Person");
  private final JsonArtifactRenderer json = new JsonArtifactRenderer();

  private TemplateInstanceArtifact instance(List<URI> types) {
    var person = ElementInstanceArtifact.builder().withJsonLdType(PERSON).build();
    var builder = TemplateInstanceArtifact.builder().withName("Typed")
      .withIsBasedOn(URI.create("https://example.org/templates/example"))
      .withJsonLdContextEntry("Person", URI.create("https://example.org/properties/person"))
      .withSingleInstanceElementInstance("Person", person);
    types.forEach(builder::withJsonLdType);
    return builder.build();
  }

  @Test void writesOneClassAsAStringAndSeveralAsAList() {
    JsonNode one = json.renderTemplateInstanceArtifact(instance(List.of(STUDY)));
    assertEquals(STUDY.toString(), one.path("@type").asText());
    JsonNode several = json.renderTemplateInstanceArtifact(instance(List.of(STUDY, DATASET)));
    assertTrue(several.path("@type").isArray());
    assertEquals(List.of(STUDY.toString(), DATASET.toString()),
      List.of(several.path("@type").get(0).asText(), several.path("@type").get(1).asText()));
    assertEquals(PERSON.toString(), several.path("Person").path("@type").asText());
    assertFalse(json.renderTemplateInstanceArtifact(instance(List.of())).has("@type"));
  }

  @Test void readsBackWhatItWrites() {
    JsonNode source = json.renderTemplateInstanceArtifact(instance(List.of(STUDY, DATASET)));
    TemplateInstanceArtifact read = new JsonArtifactReader().readTemplateInstanceArtifact((com.fasterxml.jackson.databind.node.ObjectNode) source);
    assertEquals(List.of(STUDY, DATASET), read.jsonLdTypes());
    assertEquals(List.of(PERSON), read.singleInstanceElementInstances().get("Person").jsonLdTypes());
    assertEquals(source, json.renderTemplateInstanceArtifact(read));
  }

  @Test void refusesARelativeClass() {
    var source = (com.fasterxml.jackson.databind.node.ObjectNode) json.renderTemplateInstanceArtifact(instance(List.of(STUDY)));
    source.put("@type", "Study");
    assertThrows(org.metadatacenter.artifacts.model.reader.ArtifactParseException.class,
      () -> new JsonArtifactReader().readTemplateInstanceArtifact(source));
    ((com.fasterxml.jackson.databind.node.ObjectNode) source.path("Person")).put("@type", "Person");
    source.put("@type", STUDY.toString());
    assertThrows(org.metadatacenter.artifacts.model.reader.ArtifactParseException.class,
      () -> new JsonArtifactReader().readTemplateInstanceArtifact(source));
  }

  @Test void refusesARelativeAnnotationIdentifier() {
    var source = (com.fasterxml.jackson.databind.node.ObjectNode) json.renderTemplateInstanceArtifact(instance(List.of()));
    source.putObject("_annotations").putObject("https://example.org/p").put("@id", "B1");
    assertThrows(org.metadatacenter.artifacts.model.reader.ArtifactParseException.class,
      () -> new JsonArtifactReader().readTemplateInstanceArtifact(source));
    ((com.fasterxml.jackson.databind.node.ObjectNode) source.path("_annotations").path("https://example.org/p")).put("@id", "https://example.org/B1");
    assertEquals(1, new JsonArtifactReader().readTemplateInstanceArtifact(source).annotations().orElseThrow().annotations().size());
  }
}
