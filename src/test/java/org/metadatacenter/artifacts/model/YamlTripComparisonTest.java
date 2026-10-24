package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.YamlTripComparison.Difference;
import org.metadatacenter.artifacts.model.YamlTripComparison.Family;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The comparison behind {@link CorpusYamlTripVerifier} must keep reporting what no known family explains. */
class YamlTripComparisonTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test void absentDescriptionReadAsNullIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareSchemas(
      json("{\"schema:description\": \"\", \"_ui\": {\"propertyDescriptions\": {\"Child\": \"\"}}}"),
      json("{\"schema:description\": null, \"_ui\": {\"propertyDescriptions\": {\"Child\": null}}}"));
    assertEquals(List.of(Optional.<Family>empty(), Optional.<Family>empty()), families(differences));
  }

  @Test void valueLostAmongEmptyOccurrencesIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareInstances(
      json("{\"Field\": [{\"@value\": \"a\"}, {\"@value\": null}, {\"@value\": \"b\"}]}"),
      json("{\"Field\": [{\"@value\": \"a\"}]}"));
    assertEquals(List.of(Optional.of(Family.INSTANCE_COMPLETION), Optional.<Family>empty()), families(differences));
  }

  @Test void changedContextPrefixIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareInstances(
      json("{\"@context\": {\"xsd\": \"http://www.w3.org/2001/XMLSchema#\"}}"),
      json("{\"@context\": {\"xsd\": \"http://www.w3.org/2001/XMLSchema\"}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(differences));
  }

  @Test void ontologyMovedToAnotherAcronymIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareSchemas(ontology("http://data.bioontology.org/ontologies/OBI"),
      ontology("https://data.bioontology.org/ontologies/OBO"));
    assertEquals(List.of(Optional.<Family>empty()), families(differences));
  }

  @Test void eachKnownFamilyIsRecognized() throws Exception
  {
    assertEquals(List.of(Optional.of(Family.ONTOLOGY_ADDRESS)), families(YamlTripComparison.compareSchemas(
      ontology("http://data.bioontology.org/ontologies/OBI"), ontology("https://data.bioontology.org/ontologies/OBI"))));
    assertEquals(List.of(Optional.of(Family.UI_ENTRY_FILLED)), families(YamlTripComparison.compareSchemas(
      json("{\"_ui\": {\"propertyLabels\": {}}}"), json("{\"_ui\": {\"propertyLabels\": {\"Child\": \"Child\"}}}"))));
    assertEquals(List.of(Optional.of(Family.SCHEMA_TEXT)), families(YamlTripComparison.compareSchemas(
      json("{\"$schema\": \"x\", \"description\": \"written by hand\"}"),
      json("{\"$schema\": \"x\", \"description\": \"generated\"}"))));
    assertEquals(List.of(Optional.of(Family.INSTANCE_COMPLETION)), families(YamlTripComparison.compareInstances(
      json("{\"Element\": {\"@context\": {}, \"Field\": {\"@value\": null}}}"), json("{}"))));
    assertEquals(List.of(Optional.of(Family.NUMBER_SPELLING)), families(YamlTripComparison.compareSchemas(
      json("{\"_valueConstraints\": {\"minValue\": 5.0}}"), json("{\"_valueConstraints\": {\"minValue\": 5}}"))));
  }

  @Test void numberChangedInValueOrTypeIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareSchemas(
      json("{\"_valueConstraints\": {\"minValue\": 5.1, \"maxValue\": 10, \"decimalPlace\": 2}}"),
      json("{\"_valueConstraints\": {\"minValue\": 5, \"maxValue\": \"10\", \"decimalPlace\": 2.0}}"));
    assertEquals(List.of(Optional.<Family>empty(), Optional.<Family>empty(), Optional.of(Family.NUMBER_SPELLING)),
      families(differences));
  }

  @Test void elementHoldingSomethingIsNotEmptyContent() throws Exception
  {
    assertTrue(YamlTripComparison.isEmptyContent(json("{\"@context\": {}, \"Names\": [\"A\"], \"A\": {\"@value\": null}}")));
    assertFalse(YamlTripComparison.isEmptyContent(json("{\"@context\": {}, \"A\": {\"@value\": \"kept\"}}")));
    assertFalse(YamlTripComparison.isEmptyContent(json("{\"@context\": {}, \"@id\": \"urn:uuid:1\", \"A\": {\"@value\": null}}")));
  }

  @Test void declarationsNameOnlyKnownFamiliesAndPinTheExpectedRejections() throws Exception
  {
    var declarations = CorpusYamlTripVerifier.declarations();
    assertFalse(declarations.families().values().stream().anyMatch(families -> families.contains(Family.SCHEMA_TEXT)));
    CorpusFixtureVerifier.expectedRejections().keySet()
      .forEach(source -> assertTrue(declarations.exclusions().containsKey(CorpusYamlTripVerifier.caseId(source)), source));
    assertEquals("cee-template-001", CorpusYamlTripVerifier.caseId("cee-suite/001/template-001.json"));
  }

  private static List<Optional<Family>> families(List<Difference> differences)
  {
    return differences.stream().map(Difference::family).toList();
  }

  private static JsonNode ontology(String uri) throws Exception
  {
    return json("{\"_valueConstraints\": {\"ontologies\": [{\"uri\": \"" + uri + "\"}]}}");
  }

  private static JsonNode json(String text) throws Exception { return MAPPER.readTree(text); }
}
