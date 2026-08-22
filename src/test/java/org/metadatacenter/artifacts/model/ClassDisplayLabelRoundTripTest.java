package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A class constraint carries two labels: what the ontology calls the term, and what this template calls
 * it in this form.
 *
 * <p>The second is the author's, typed into the editor, and it is written only where the two differ —
 * which is one class in the shared corpus and none of the other 108. A class with no display label of
 * its own reads back with the preferred label as both, so the ordinary entry stays a single key. The
 * YAML carried only the preferred label for a while, which lost the author's wording on any round trip
 * through it: {@code "Music UI Label added in the editor"} came back as {@code "Music"}.
 */
class ClassDisplayLabelRoundTripTest
{
  private static final URI TERM = URI.create("http://purl.bioontology.org/ontology/MESH/D009146");

  private final YamlArtifactRenderer renderer = new YamlArtifactRenderer(false);
  private final YamlArtifactReader reader = new YamlArtifactReader();

  @Test void anAuthorsDisplayLabelIsWrittenAndReadBack()
  {
    LinkedHashMap<String, Object> yaml = renderer.renderFieldSchemaArtifact(
      classField("Music", "Music UI Label added in the editor"));

    assertEquals("Music UI Label added in the editor", classEntry(yaml).get("termDisplayLabel"));
    assertEquals("Music", classEntry(yaml).get("termLabel"));

    FieldSchemaArtifact readBack = reader.readFieldSchemaArtifact(yaml);
    var constraint = readBack.valueConstraints().orElseThrow().asControlledTermValueConstraints().classes().get(0);
    assertEquals("Music UI Label added in the editor", constraint.label());
    assertEquals("Music", constraint.prefLabel());
  }

  @Test void aClassWithNoLabelOfItsOwnCarriesOneKey()
  {
    LinkedHashMap<String, Object> yaml = renderer.renderFieldSchemaArtifact(classField("Music", "Music"));

    assertFalse(classEntry(yaml).containsKey("termDisplayLabel"),
      "a class whose labels agree should say so once");

    FieldSchemaArtifact readBack = reader.readFieldSchemaArtifact(yaml);
    var constraint = readBack.valueConstraints().orElseThrow().asControlledTermValueConstraints().classes().get(0);
    assertEquals("Music", constraint.label());
    assertEquals("Music", constraint.prefLabel());
  }

  @Test void theJsonKeepsBothLabelsWhicheverWayTheYamlWentIn()
  {
    LinkedHashMap<String, Object> yaml = renderer.renderFieldSchemaArtifact(
      classField("Music", "Music UI Label added in the editor"));

    ObjectNode json = new JsonArtifactRenderer().renderFieldSchemaArtifact(reader.readFieldSchemaArtifact(yaml));
    ObjectNode entry = (ObjectNode) json.at("/_valueConstraints/classes/0");

    assertTrue(entry.has("prefLabel") && entry.has("label"), "the JSON has always carried both: " + entry);
    assertEquals("Music", entry.get("prefLabel").asText());
    assertEquals("Music UI Label added in the editor", entry.get("label").asText());
  }

  private static FieldSchemaArtifact classField(String prefLabel, String displayLabel)
  {
    return ControlledTermField.builder().withName("Genre")
      .withClassValueConstraint(TERM, "MESH", displayLabel, prefLabel, ValueType.ONTOLOGY_CLASS).build();
  }

  @SuppressWarnings("unchecked") private static LinkedHashMap<String, Object> classEntry(
    LinkedHashMap<String, Object> fieldRendering)
  {
    var values = (java.util.List<LinkedHashMap<String, Object>>) fieldRendering.get("values");
    return values.get(0);
  }
}
