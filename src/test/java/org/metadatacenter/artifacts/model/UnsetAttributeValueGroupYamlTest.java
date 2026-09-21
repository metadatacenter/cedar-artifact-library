package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * An instance says what it holds by omission, so an attribute-value group whose every attribute is
 * unset is written out rather than rendered as {@code name: {}}.
 *
 * <p>The group was judged by how many attributes it held rather than by what it rendered to, so a
 * group of one unset attribute produced an empty mapping — which this library's own YAML reader
 * then refuses, with "an empty mapping ({}) is not a valid value". The document did not survive its
 * own round trip. Ten of 959 production instances sampled hit it, at all three nesting levels, so
 * each level is covered here.
 */
public class UnsetAttributeValueGroupYamlTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private String yamlFor(String json) throws Exception
  {
    TemplateInstanceArtifact instance = new JsonArtifactReader()
      .readTemplateInstanceArtifact((ObjectNode) MAPPER.readTree(json));
    return YamlSerializer.getYAML(instance, false, true);
  }

  private void assertNoPlaceholderAndReadsBack(String yaml)
  {
    assertFalse(yaml.lines().anyMatch(line -> line.trim().endsWith(": {}")),
      "the rendering states an empty mapping:\n" + yaml);
    assertDoesNotThrow(() -> {
      @SuppressWarnings("unchecked")
      LinkedHashMap<String, Object> parsed = new Yaml().loadAs(yaml, LinkedHashMap.class);
      new YamlArtifactReader().readTemplateInstanceArtifact(parsed);
    }, "the rendering does not read back:\n" + yaml);
  }

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

  @Test public void anUnsetGroupOnTheInstanceIsNotWrittenAsAnEmptyMapping() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i1",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Extra": ["Extra1"], "Extra1": {"@value": null}}""";
    assertNoPlaceholderAndReadsBack(yamlFor(json));
  }

  @Test public void anUnsetGroupInsideAnElementIsNotWrittenAsAnEmptyMapping() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i2",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Section": {"@id": "https://repo.metadatacenter.org/template-element-instances/e1",
                  "Name": {"@value": "set"}, "Extra": ["Extra1"], "Extra1": {"@value": null}}}""";
    assertNoPlaceholderAndReadsBack(yamlFor(json));
  }

  @Test public void anUnsetGroupInsideARepeatedElementIsNotWrittenAsAnEmptyMapping() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i3",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Section": [{"@id": "https://repo.metadatacenter.org/template-element-instances/e2",
                   "Name": {"@value": "set"}, "Extra": ["Extra1"], "Extra1": {"@value": null}}]}""";
    assertNoPlaceholderAndReadsBack(yamlFor(json));
  }

  @Test public void aGroupWithASetAttributeIsStillWritten() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i4",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Extra": ["Extra1"], "Extra1": {"@value": "kept"}}""";
    String yaml = yamlFor(json);
    assertNoPlaceholderAndReadsBack(yaml);
    org.junit.jupiter.api.Assertions.assertTrue(yaml.contains("Extra1"),
      "a group with a set attribute must survive:\n" + yaml);
  }
}
