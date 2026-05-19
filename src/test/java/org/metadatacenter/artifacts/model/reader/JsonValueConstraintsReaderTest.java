package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Group C — JsonValueConstraintsReader has 401 LOC and zero direct tests. Value constraints
 * control field validation, so silent bugs here mean fields validate the wrong things.
 */
public class JsonValueConstraintsReaderTest
{
  private final ObjectMapper mapper = new ObjectMapper();

  // VALUE_CONSTRAINTS_* keys are all literal strings under JSON Schema; using literals here
  // keeps the test focused on shape rather than constants.

  @Test public void testReadOntologyValueConstraint()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("uri", "https://data.bioontology.org/ontologies/DOID");
    node.put("acronym", "DOID");
    node.put("name", "Human Disease Ontology");
    node.put("numTerms", 15000);

    OntologyValueConstraint result = JsonValueConstraintsReader.readOntologyValueConstraint(node, "/");

    assertEquals(URI.create("https://data.bioontology.org/ontologies/DOID"), result.uri());
    assertEquals("DOID", result.acronym());
    assertEquals("Human Disease Ontology", result.name());
    assertEquals(15000, result.numTerms().get());
  }

  @Test public void testReadOntologyValueConstraintsArray()
  {
    ObjectNode parent = mapper.createObjectNode();
    ArrayNode arr = parent.putArray("ontologies");
    ObjectNode entry = arr.addObject();
    entry.put("uri", "https://example.com/ont/1");
    entry.put("acronym", "ONT1");
    entry.put("name", "Test Ontology");

    List<OntologyValueConstraint> results =
      JsonValueConstraintsReader.readOntologyValueConstraints(parent, "/", "ontologies");

    assertEquals(1, results.size());
    assertEquals("ONT1", results.get(0).acronym());

    // Non-array value: returns empty (silently — documenting the contract).
    ObjectNode notArray = mapper.createObjectNode();
    notArray.put("ontologies", "not an array");
    assertTrue(
      JsonValueConstraintsReader.readOntologyValueConstraints(notArray, "/", "ontologies").isEmpty());
  }

  @Test public void testReadClassValueConstraint()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("uri", "http://purl.bioontology.org/ontology/LNC/LA19711-3");
    node.put("source", "LOINC");
    node.put("label", "Homo Sapiens");
    node.put("prefLabel", "Homo Sapiens");
    node.put("type", ValueType.ONTOLOGY_CLASS.getText());

    ClassValueConstraint result = JsonValueConstraintsReader.readClassValueConstraint(node, "/");

    assertEquals("LOINC", result.source());
    assertEquals("Homo Sapiens", result.label());
    assertEquals(ValueType.ONTOLOGY_CLASS, result.type());
  }

  @Test public void testReadValueSetValueConstraint()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("uri", "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161");
    node.put("name", "Area unit");
    node.put("vsCollection", "HRAVS");
    node.put("numTerms", 40);

    ValueSetValueConstraint result = JsonValueConstraintsReader.readValueSetValueConstraint(node, "/");

    assertEquals("HRAVS", result.vsCollection());
    assertEquals("Area unit", result.name());
    assertEquals(40, result.numTerms().get());
  }

  @Test public void testReadBranchValueConstraint()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("uri", "http://purl.org/twc/dpo/ont/Disease");
    node.put("source", "DPCO");
    node.put("acronym", "DPCO");
    node.put("name", "Disease");
    node.put("maxDepth", 3);

    BranchValueConstraint result = JsonValueConstraintsReader.readBranchValueConstraint(node, "/");

    assertEquals("DPCO", result.source());
    assertEquals(3, result.maxDepth());
  }

  @Test public void testReadValueConstraintsActionFullShape()
  {
    // The action shape carries all fields; absence of the array node returns empty.
    ObjectNode action = mapper.createObjectNode();
    action.put("termUri", "https://purl.org/datacite/v4.4/TranslatedTitle");
    action.put("sourceUri", "https://purl.org/datacite/v4.4/TitleType");
    action.put("source", "DATACITE-VOCAB");
    action.put("type", ValueType.ONTOLOGY_CLASS.getText());
    action.put("action", ValueConstraintsActionType.DELETE.getText());
    action.put("to", 5);

    ControlledTermValueConstraintsAction result =
      JsonValueConstraintsReader.readValueConstraintsAction(action, "/");

    assertEquals(URI.create("https://purl.org/datacite/v4.4/TranslatedTitle"), result.termUri());
    assertEquals(ValueConstraintsActionType.DELETE, result.action());
    assertEquals(5, result.to().get());

    // And the array-reading helper bubbles up an exception on non-object entries.
    ObjectNode parent = mapper.createObjectNode();
    ArrayNode arr = parent.putArray("actions");
    arr.add("not-an-object");
    assertThrows(ArtifactParseException.class,
      () -> JsonValueConstraintsReader.readValueConstraintsActions(parent, "/", "actions"));
  }
}
