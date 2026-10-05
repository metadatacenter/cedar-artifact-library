package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every shape a field value can take, in every position an instance can hold one, read and written by
 * this library; TypeScript reads these exact outputs.
 *
 * <p>A value node is a literal ({@code @value}), an IRI ({@code @id}) or a label alone, with any of
 * the keys that qualify it: a datatype, a language, a label, a notation and a preferred label. Each
 * library decided for itself which of those a node may carry, and they disagreed. TypeScript threw on
 * a controlled term carrying both {@code rdfs:label} and {@code skos:prefLabel}, the form the YAML
 * specification documents, and dropped a preferred label from a literal. Java reads and writes both.
 *
 * <p>The expectation is that a node survives exactly: read from JSON and written back, and written as
 * YAML, read and written as JSON again. The fixture records what Java writes at each step, so the
 * TypeScript side compares itself with Java rather than with its own idea of the node.
 */
public class InstanceValueConcordanceMatrixTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();
  private static final Path FIXTURE = Path.of("src/test/resources/concordance/instance-value-matrix.json");
  private static final String IRI = "http://purl.obolibrary.org/obo/DOID_530";
  private static final String DATATYPE = "http://example.org/types/Disease";

  /** The value nodes, by a name saying what each carries. */
  private static Map<String, String> nodes() {
    Map<String, String> nodes = new LinkedHashMap<>();
    nodes.put("literal", "{\"@value\":\"Cardiology cohort\"}");
    nodes.put("literal null", "{\"@value\":null}");
    nodes.put("literal typed", "{\"@value\":\"2323\",\"@type\":\"xsd:int\"}");
    nodes.put("literal language", "{\"@value\":\"cohorte\",\"@language\":\"fr\"}");
    nodes.put("literal label", "{\"@value\":\"Cardiology cohort\",\"rdfs:label\":\"Cohort\"}");
    nodes.put("literal null label", "{\"@value\":null,\"rdfs:label\":\"Cohort\"}");
    nodes.put("literal notation", "{\"@value\":\"Cardiology cohort\",\"skos:notation\":\"C1\"}");
    nodes.put("literal prefLabel", "{\"@value\":\"Cardiology cohort\",\"skos:prefLabel\":\"Cohort\"}");
    nodes.put("literal language prefLabel",
        "{\"@value\":\"cohorte\",\"@language\":\"fr\",\"skos:prefLabel\":\"Cohorte\"}");
    nodes.put("iri", "{\"@id\":\"" + IRI + "\"}");
    nodes.put("iri typed", "{\"@id\":\"" + IRI + "\",\"@type\":\"" + DATATYPE + "\"}");
    nodes.put("iri label", "{\"@id\":\"" + IRI + "\",\"rdfs:label\":\"eyelid disease\"}");
    nodes.put("iri prefLabel", "{\"@id\":\"" + IRI + "\",\"skos:prefLabel\":\"eyelid disease\"}");
    nodes.put("iri label prefLabel",
        "{\"@id\":\"" + IRI + "\",\"rdfs:label\":\"eyelid disease\",\"skos:prefLabel\":\"eyelid disease\"}");
    nodes.put("iri label notation",
        "{\"@id\":\"" + IRI + "\",\"rdfs:label\":\"eyelid disease\",\"skos:notation\":\"DOID:530\"}");
    nodes.put("iri label notation prefLabel", "{\"@id\":\"" + IRI + "\",\"rdfs:label\":\"eyelid disease\","
        + "\"skos:notation\":\"DOID:530\",\"skos:prefLabel\":\"eyelid disease\"}");
    nodes.put("iri typed label prefLabel", "{\"@id\":\"" + IRI + "\",\"@type\":\"" + DATATYPE + "\","
        + "\"rdfs:label\":\"eyelid disease\",\"skos:prefLabel\":\"eyelid disease\"}");
    nodes.put("label", "{\"rdfs:label\":\"eyelid disease\"}");
    nodes.put("label prefLabel", "{\"rdfs:label\":\"eyelid disease\",\"skos:prefLabel\":\"eyelid disease\"}");
    nodes.put("notation", "{\"skos:notation\":\"DOID:530\"}");
    return nodes;
  }

  /** Where an instance holds a value: on itself, in an element, or as one of a list. */
  private static final List<String> POSITIONS = List.of("root", "element", "repeated");

  private static JsonNode at(JsonNode instance, String position) {
    return switch (position) {
      case "root" -> instance.get("value");
      case "element" -> instance.path("element").get("value");
      case "repeated" -> instance.get("value");
      default -> throw new IllegalArgumentException(position);
    };
  }

  private static ObjectNode source(JsonNode node, String position) {
    ObjectNode source = MAPPER.createObjectNode()
        .put("schema:name", "Matrix")
        .put("schema:isBasedOn", "https://example.org/templates/matrix");
    switch (position) {
      case "root" -> source.set("value", node.deepCopy());
      // An element carries @context, which is how a reader tells it from a value.
      case "element" -> source.set("element", MAPPER.createObjectNode()
          .<ObjectNode>set("@context", MAPPER.createObjectNode()).set("value", node.deepCopy()));
      case "repeated" -> source.set("value", MAPPER.createArrayNode().add(node.deepCopy()).add(node.deepCopy()));
      default -> throw new IllegalArgumentException(position);
    }
    return source;
  }

  private static boolean holdsNothing(JsonNode node) {
    return node.size() == 1 && node.has("@value") && node.get("@value").isNull();
  }

  private static JsonNode expected(JsonNode node, String position) {
    return position.equals("repeated") ? MAPPER.createArrayNode().add(node).add(node) : node;
  }

  private static ObjectNode renderCase(String name, JsonNode node, String position) {
    ObjectNode source = source(node, position);
    TemplateInstanceArtifact instance = new JsonArtifactReader().readTemplateInstanceArtifact(source);
    ObjectNode json = JSON.renderTemplateInstanceArtifact(instance);
    String id = name + " / " + position;
    assertEquals(expected(node, position), at(json, position), id + ": JSON → model → JSON");

    ObjectNode result = MAPPER.createObjectNode().put("id", id).put("node", name).put("position", position);
    result.set("value", node);
    result.set("json", json);
    for (boolean compact : List.of(false, true)) {
      result.put(compact ? "compactYaml" : "yaml", YamlSerializer.getYAML(instance, compact, true));
      LinkedHashMap<String, Object> yaml = new YamlArtifactRenderer(compact).renderTemplateInstanceArtifact(instance);
      TemplateInstanceArtifact restored = new YamlArtifactReader(compact).readTemplateInstanceArtifact(yaml);
      ObjectNode fromYaml = JSON.renderTemplateInstanceArtifact(restored);
      if (!compact) {
        // YAML has no way to write a field that holds nothing, so an unfilled value is left out and
        // only completion against the template restores it.
        JsonNode survives = holdsNothing(node) ? null : expected(node, position);
        assertEquals(survives, at(fromYaml, position), id + ": model → YAML → model → JSON");
      }
      result.set(compact ? "jsonFromCompactYaml" : "jsonFromYaml", fromYaml);
    }
    return result;
  }

  @Test
  public void instanceValueConcordanceMatrix() throws Exception {
    ArrayNode cases = MAPPER.createArrayNode();
    for (Map.Entry<String, String> node : nodes().entrySet()) {
      for (String position : POSITIONS) {
        cases.add(renderCase(node.getKey(), MAPPER.readTree(node.getValue()), position));
      }
    }
    if (Boolean.getBoolean("updateInstanceValueConcordance")) {
      Files.createDirectories(FIXTURE.getParent());
      Files.writeString(FIXTURE, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(cases) + "\n");
    }
    assertTrue(Files.exists(FIXTURE), "Generate with -DupdateInstanceValueConcordance=true");
    assertEquals(MAPPER.readTree(Files.readString(FIXTURE)), cases,
        "Java matrix fixtures are stale; regenerate and refresh the TypeScript concordance fixture");
  }
}
