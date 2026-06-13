package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.LinkValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonValueConstraintsReaderTest
{
  private final ObjectMapper mapper = new ObjectMapper();

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

    // Non-array value silently returns empty rather than throwing.
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

    // Array-reading helper throws on non-object entries.
    ObjectNode parent = mapper.createObjectNode();
    ArrayNode arr = parent.putArray("actions");
    arr.add("not-an-object");
    assertThrows(ArtifactParseException.class,
      () -> JsonValueConstraintsReader.readValueConstraintsActions(parent, "/", "actions"));
  }

  // When a value-bearing field carries no _valueConstraints node, the reader synthesizes the
  // type-appropriate empty constraints so a JSON-read field matches a builder-built one and a
  // YAML-read one (both of which always carry value constraints). Static and attribute-value
  // fields, which legitimately have no constraints, stay empty.

  private Optional<ValueConstraints> readAbsent(FieldInputType fieldInputType)
  {
    // A source node with no _valueConstraints child.
    return JsonValueConstraintsReader.readValueConstraints(
      mapper.createObjectNode(), "/", "_valueConstraints", fieldInputType, false, false);
  }

  @Test public void testAbsentValueConstraintsSynthesizesTextForTextField()
  {
    Optional<ValueConstraints> vc = readAbsent(FieldInputType.TEXTFIELD);

    assertTrue(vc.isPresent());
    assertInstanceOf(TextValueConstraints.class, vc.get());
  }

  @Test public void testAbsentValueConstraintsSynthesizesNumericForNumericField()
  {
    Optional<ValueConstraints> vc = readAbsent(FieldInputType.NUMERIC);

    assertTrue(vc.isPresent());
    NumericValueConstraints numeric = assertInstanceOf(NumericValueConstraints.class, vc.get());
    // Number type defaults to xsd:decimal when unspecified.
    assertEquals(XsdNumericDatatype.DECIMAL, numeric.numberType());
  }

  @Test public void testAbsentValueConstraintsSynthesizesTemporalForTemporalField()
  {
    // Regression: a temporal field with no _valueConstraints previously threw because no temporal
    // type was present; it now defaults to xsd:dateTime, mirroring the YAML reader.
    Optional<ValueConstraints> vc = readAbsent(FieldInputType.TEMPORAL);

    assertTrue(vc.isPresent());
    TemporalValueConstraints temporal = assertInstanceOf(TemporalValueConstraints.class, vc.get());
    assertEquals(XsdTemporalDatatype.DATETIME, temporal.temporalType());
  }

  @Test public void testAbsentValueConstraintsSynthesizesLinkForIriField()
  {
    Optional<ValueConstraints> vc = readAbsent(FieldInputType.LINK);

    assertTrue(vc.isPresent());
    assertInstanceOf(LinkValueConstraints.class, vc.get());
  }

  @Test public void testAbsentValueConstraintsStaysEmptyForAttributeValueField()
  {
    assertFalse(readAbsent(FieldInputType.ATTRIBUTE_VALUE).isPresent());
  }

  @Test public void testAbsentValueConstraintsStaysEmptyForStaticField()
  {
    assertFalse(readAbsent(FieldInputType.PAGE_BREAK).isPresent());
  }
}
