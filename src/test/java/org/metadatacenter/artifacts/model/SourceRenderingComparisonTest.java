package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.SourceRenderingComparison.Difference;
import org.metadatacenter.artifacts.model.SourceRenderingComparison.Family;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The comparison behind the battery's and the corpus's source checks must keep reporting a loss no normalization explains. */
class SourceRenderingComparisonTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static JsonNode json(String text) throws Exception { return MAPPER.readTree(text); }

  private static List<Optional<Family>> families(String source, String rendering) throws Exception
  {
    return SourceRenderingComparison.compare(json(source), json(rendering)).stream().map(Difference::family).toList();
  }

  @Test void lostBoundChildAndLabelAreUnexplained() throws Exception
  {
    assertEquals(List.of(Optional.<Family>empty()), families(
      "{\"properties\": {\"Tags\": {\"type\": \"array\", \"minItems\": 1, \"items\": {}}}}",
      "{\"properties\": {\"Tags\": {\"type\": \"array\", \"items\": {}}}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(
      "{\"properties\": {\"Title\": {\"type\": \"object\"}}}", "{\"properties\": {}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(
      "{\"_ui\": {\"propertyLabels\": {\"Title\": \"Heading\"}}}", "{\"_ui\": {\"propertyLabels\": {}}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(
      "{\"properties\": {\"@context\": {\"properties\": {\"Title\": {\"enum\": [\"https://example.org/a\"]}}}}}",
      "{\"properties\": {\"@context\": {\"properties\": {\"Title\": {\"enum\": [\"https://example.org/b\"]}}}}}"));
    assertEquals(List.of(Optional.<Family>empty()), families(
      "{\"_valueConstraints\": {\"multipleChoice\": true}, \"_ui\": {\"inputType\": \"checkbox\"}}",
      "{\"_valueConstraints\": {}, \"_ui\": {\"inputType\": \"checkbox\"}}"));
  }

  @Test void eachNormalizationIsRecognized() throws Exception
  {
    assertEquals(List.of(Optional.of(Family.SCHEMA_TEXT)),
      families("{\"$schema\": \"x\", \"title\": \"by hand\"}", "{\"$schema\": \"x\", \"title\": \"generated\"}"));
    assertEquals(List.of(Optional.of(Family.GENERATED_SCHEMA)), families(
      "{\"properties\": {\"@type\": {\"oneOf\": []}}}", "{\"properties\": {\"@type\": {\"type\": \"string\"}}}"));
    assertEquals(List.of(Optional.of(Family.DEFAULT_CONSTRAINT_DROPPED), Optional.of(Family.DEFAULT_CONSTRAINT_DROPPED)),
      families("{\"_valueConstraints\": {\"actions\": [], \"defaultValue\": \"\"}}", "{\"_valueConstraints\": {}}"));
    assertEquals(List.of(Optional.of(Family.DEFAULT_CONSTRAINT_ADDED)),
      families("{\"_valueConstraints\": {}}", "{\"_valueConstraints\": {\"valueSets\": []}}"));
    assertEquals(List.of(Optional.of(Family.EMPTY_UI_ENTRY_DROPPED)),
      families("{\"_ui\": {\"propertyDescriptions\": {\"Title\": \"\"}}}", "{\"_ui\": {\"propertyDescriptions\": {}}}"));
    assertEquals(List.of(Optional.of(Family.EMPTY_LABELS_DROPPED)), families("{\"skos:altLabel\": []}", "{}"));
    assertEquals(List.of(Optional.of(Family.LEGACY_KEY_DROPPED), Optional.of(Family.LEGACY_KEY_DROPPED)),
      families("{\"multiValued\": false, \"_ui\": {\"pages\": []}}", "{\"_ui\": {}}"));
    assertEquals(List.of(Optional.of(Family.UI_DEFAULT_DROPPED)), families("{\"_ui\": {\"hidden\": false}}", "{\"_ui\": {}}"));
    assertEquals(List.of(Optional.of(Family.INAPPLICABLE_TEMPORAL_UI)), families(
      "{\"_ui\": {\"inputType\": \"temporal\", \"valueRecommendationEnabled\": true}}", "{\"_ui\": {\"inputType\": \"temporal\"}}"));
    assertEquals(List.of(Optional.of(Family.INAPPLICABLE_CONSTRAINT_DROPPED)), families(
      "{\"_valueConstraints\": {\"multipleChoice\": true}, \"_ui\": {\"inputType\": \"textfield\"}}",
      "{\"_valueConstraints\": {}, \"_ui\": {\"inputType\": \"textfield\"}}"));
    assertEquals(List.of(Optional.of(Family.ARRAY_WRAPPER_KEY_DROPPED)), families(
      "{\"type\": \"array\", \"items\": {}, \"pav:derivedFrom\": \"https://example.org/e\"}", "{\"type\": \"array\", \"items\": {}}"));
    assertEquals(List.of(Optional.of(Family.TEMPLATE_PREF_LABEL_DROPPED)), families(
      "{\"@type\": \"https://schema.metadatacenter.org/core/Template\", \"skos:prefLabel\": \"T\"}",
      "{\"@type\": \"https://schema.metadatacenter.org/core/Template\"}"));
    assertEquals(List.of(Optional.of(Family.NULL_ID_SUPPLIED)), families("{\"$schema\": \"x\"}", "{\"$schema\": \"x\", \"@id\": null}"));
    assertEquals(List.of(Optional.of(Family.DANGLING_ORDER_ENTRY)), families(
      "{\"properties\": {\"A\": {}}, \"_ui\": {\"order\": [\"A\", \"Gone\"]}}", "{\"properties\": {\"A\": {}}, \"_ui\": {\"order\": [\"A\"]}}"));
  }
}
