package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A controlled-term field can hold a label and no identifier, and its sub-schema allows no
 * {@code @value} at all. Rendering one back with {@code "@value": null} beside the label produces
 * a shape that is neither of the two empty forms — {@code {}} for an identifier-valued field,
 * {@code {"@value": null}} for a literal one — and one the field's own sub-schema rejects, since
 * it declares {@code additionalProperties: false} over {@code @id}, {@code rdfs:label} and
 * {@code @type}.
 *
 * <p>The YAML a field with only a label renders to carries no {@code value:} key, so nothing in it
 * claims a literal. The reader said otherwise: it built every field through the overload that
 * asserts the document wrote an {@code @value} key, and the JSON renderer believed it.
 *
 * <p>402 production instances hold this shape, all in one field of one template, and validating
 * that field alone turns a valid stored instance invalid.
 */
public class LabelWithoutIdRoundTripTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String CONTEXT = """
    "@context": {"xsd": "http://www.w3.org/2001/XMLSchema#", "pav": "http://purl.org/pav/",
      "schema": "http://schema.org/", "oslc": "http://open-services.net/ns/core#",
      "skos": "http://www.w3.org/2004/02/skos/core#",
      "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
      "pav:createdOn": {"@type": "xsd:dateTime"}, "pav:createdBy": {"@type": "@id"},
      "pav:lastUpdatedOn": {"@type": "xsd:dateTime"}, "oslc:modifiedBy": {"@type": "@id"},
      "schema:isBasedOn": {"@type": "@id"}, "schema:name": {"@type": "xsd:string"},
      "schema:description": {"@type": "xsd:string"},
      "rdfs:label": {"@type": "xsd:string"}, "skos:notation": {"@type": "xsd:string"}}""";

  private ObjectNode throughYaml(String json) throws Exception
  {
    TemplateInstanceArtifact fromJson = new JsonArtifactReader()
      .readTemplateInstanceArtifact((ObjectNode) MAPPER.readTree(json));
    LinkedHashMap<String, Object> asYaml =
      new YamlArtifactRenderer(false).renderTemplateInstanceArtifact(fromJson);
    TemplateInstanceArtifact viaYaml = new YamlArtifactReader().readTemplateInstanceArtifact(asYaml);
    return new JsonArtifactRenderer().renderTemplateInstanceArtifact(viaYaml);
  }

  private static String instanceWith(String field)
  {
    return "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i1",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      """ + field + "}";
  }

  @Test public void aFieldHoldingOnlyALabelKeepsItsShape() throws Exception
  {
    ObjectNode back = throughYaml(instanceWith(
      "\"Category\": {\"rdfs:label\": \"Resource Type Category\"}"));

    ObjectNode field = (ObjectNode) back.get("Category");
    assertEquals("Resource Type Category", field.get("rdfs:label").asText());
    assertFalse(field.has("@value"),
      "a field that carried no value key must not gain one: " + field);
  }

  @Test public void theLabelSurvivesTheYamlItself() throws Exception
  {
    TemplateInstanceArtifact fromJson = new JsonArtifactReader()
      .readTemplateInstanceArtifact((ObjectNode) MAPPER.readTree(instanceWith(
        "\"Category\": {\"rdfs:label\": \"Resource Type Category\"}")));
    String yaml = org.metadatacenter.artifacts.model.tools.YamlSerializer
      .getYAML(fromJson, false, true);

    assertTrue(yaml.contains("label: \"Resource Type Category\""),
      "the label should reach the YAML:\n" + yaml);
    assertFalse(yaml.contains("value:"), "the YAML should claim no value:\n" + yaml);
  }

  @Test public void anUnfilledLiteralFieldStillStatesItsNullValue() throws Exception
  {
    // The counterpart shape: a document that did write an @value key keeps it, because a literal
    // field's sub-schema requires one.
    ObjectNode back = throughYaml(instanceWith(
      "\"Notes\": {\"@value\": \"kept\"}, \"Category\": {\"rdfs:label\": \"a label\"}"));

    assertEquals("kept", back.get("Notes").get("@value").asText());
    assertFalse(((ObjectNode) back.get("Category")).has("@value"));
  }

  @Test public void aFilledControlledTermKeepsItsIdentifierAndLabel() throws Exception
  {
    ObjectNode back = throughYaml(instanceWith(
      "\"Category\": {\"@id\": \"http://purl.obolibrary.org/obo/GO_0005524\","
        + " \"rdfs:label\": \"ATP binding\"}"));

    ObjectNode field = (ObjectNode) back.get("Category");
    assertEquals("http://purl.obolibrary.org/obo/GO_0005524", field.get("@id").asText());
    assertEquals("ATP binding", field.get("rdfs:label").asText());
    assertFalse(field.has("@value"));
  }
}
