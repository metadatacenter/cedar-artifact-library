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
  private static final JsonNode NO_TEMPLATE = MAPPER.createObjectNode();

  @Test void absentDescriptionReadAsNullIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareSchemas(
      json("{\"schema:description\": \"\", \"_ui\": {\"propertyDescriptions\": {\"Child\": \"\"}}}"),
      json("{\"schema:description\": null, \"_ui\": {\"propertyDescriptions\": {\"Child\": null}}}"));
    assertEquals(List.of(Optional.<Family>empty(), Optional.<Family>empty()), families(differences));
  }

  @Test void valueLostAmongEmptyOccurrencesIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareInstances(NO_TEMPLATE,
      json("{\"Field\": [{\"@value\": \"a\"}, {\"@value\": null}, {\"@value\": \"b\"}]}"),
      json("{\"Field\": [{\"@value\": \"a\"}]}"));
    assertEquals(List.of(Optional.of(Family.INSTANCE_COMPLETION), Optional.<Family>empty()), families(differences));
  }

  @Test void changedContextPrefixIsUnexplained() throws Exception
  {
    var differences = YamlTripComparison.compareInstances(NO_TEMPLATE,
      json("{\"@context\": {\"xsd\": \"http://www.w3.org/2001/XMLSchema#\"}}"),
      json("{\"@context\": {\"xsd\": \"http://www.w3.org/2001/XMLSchema\"}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(differences));
  }

  @Test void attributeIriLostOrChangedIsUnexplained() throws Exception
  {
    String direct = "{\"@context\": {\"A\": \"https://example.org/a\"}, \"Names\": [\"A\"], \"A\": {\"@value\": \"kept\"}}";
    var lost = YamlTripComparison.compareInstances(NO_TEMPLATE, json(direct),
      json("{\"@context\": {}, \"Names\": [\"A\"], \"A\": {\"@value\": \"kept\"}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(lost));
    var changed = YamlTripComparison.compareInstances(NO_TEMPLATE, json(direct),
      json("{\"@context\": {\"A\": \"https://example.org/b\"}, \"Names\": [\"A\"], \"A\": {\"@value\": \"kept\"}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(changed));
  }

  private static final String TITLE = "https://schema.metadatacenter.org/properties/title";

  /**
   * A template requiring Title's IRI at the root and Site's inside the repeated element Visits, and declaring
   * the optional mapping of the attribute-value group Pairs.
   */
  private static JsonNode titleTemplate() throws Exception
  {
    return json("{\"properties\": {\"@context\": {\"properties\": {\"Title\": {\"enum\": [\"" + TITLE + "\"]},"
      + " \"Pairs\": {\"enum\": [\"https://example.org/pairs\"]}}, \"required\": [\"Title\"]},"
      + " \"Visits\": {\"items\": {\"properties\": {\"@context\": {\"properties\": {"
      + "\"Site\": {\"enum\": [\"https://example.org/site\"]}}, \"required\": [\"Site\"]}}}}}}");
  }

  private static JsonNode titled(String iri) throws Exception
  {
    return json("{\"@context\": {" + (iri == null ? "" : "\"Title\": \"" + iri + "\"") + "}, \"Title\": {\"@value\": \"x\"}}");
  }

  @Test void childIriTakenFromTheTemplateIsCompletion() throws Exception
  {
    var differences = YamlTripComparison.compareInstances(titleTemplate(), titled("https://example.org/custom"), titled(TITLE));
    assertEquals(List.of(Optional.of(Family.INSTANCE_COMPLETION)), families(differences));
  }

  @Test void childIriChangedToAnotherOrLostIsUnexplained() throws Exception
  {
    var changed = YamlTripComparison.compareInstances(titleTemplate(), titled(TITLE), titled("https://example.org/some/other/property"));
    assertEquals(List.of(Optional.<Family>empty()), families(changed));
    var lost = YamlTripComparison.compareInstances(titleTemplate(), titled(TITLE), titled(null));
    assertEquals(List.of(Optional.<Family>empty()), families(lost));
  }

  @Test void optionalMappingLeftOutIsCompletion() throws Exception
  {
    String direct = "{\"@context\": {\"Pairs\": \"https://example.org/pairs\"}, \"Pairs\": [\"A\"], \"A\": {\"@value\": \"x\"}}";
    var differences = YamlTripComparison.compareInstances(titleTemplate(), json(direct),
      json("{\"@context\": {}, \"Pairs\": [\"A\"], \"A\": {\"@value\": \"x\"}}"));
    assertEquals(List.of(Optional.of(Family.INSTANCE_COMPLETION)), families(differences));
  }

  @Test void childIriInARepeatedElementIsJudgedByThatElementsSchema() throws Exception
  {
    String direct = "{\"Visits\": [{\"@context\": {\"Site\": \"https://example.org/custom\"}, \"Site\": {\"@value\": \"x\"}}]}";
    var completed = YamlTripComparison.compareInstances(titleTemplate(), json(direct),
      json(direct.replace("https://example.org/custom", "https://example.org/site")));
    assertEquals(List.of(Optional.of(Family.INSTANCE_COMPLETION)), families(completed));
    var wrong = YamlTripComparison.compareInstances(titleTemplate(), json(direct),
      json(direct.replace("https://example.org/custom", TITLE)));
    assertEquals(List.of(Optional.<Family>empty()), families(wrong));
  }

  @Test void attributeDroppedForHoldingNothingTakesItsIri() throws Exception
  {
    var differences = YamlTripComparison.compareInstances(NO_TEMPLATE,
      json("{\"@context\": {\"A\": \"https://example.org/a\"}, \"Names\": [\"A\"], \"A\": {\"@value\": null}}"),
      json("{\"@context\": {}, \"Names\": []}"));
    assertTrue(families(differences).stream().allMatch(f -> f.equals(Optional.of(Family.INSTANCE_COMPLETION))),
      differences.toString());
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
    assertEquals(List.of(Optional.of(Family.INSTANCE_COMPLETION)), families(YamlTripComparison.compareInstances(NO_TEMPLATE,
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
