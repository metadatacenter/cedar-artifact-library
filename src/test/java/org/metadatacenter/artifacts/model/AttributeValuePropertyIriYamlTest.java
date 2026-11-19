package org.metadatacenter.artifacts.model;

import org.metadatacenter.artifacts.model.reader.Refusals;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.ArtifactSourceParser;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;

import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An attribute's property IRI is the instance's own. The server mints it when the attribute is first
 * stored, and no template states it, so the YAML form has to carry it or a trip through YAML loses it.
 * The writer puts it beside the attribute's value under {@code propertyIri}, the key a template uses for
 * a child's property IRI, in the full and the compact form, at the root and in an element. The reader
 * puts it back in the {@code @context} of the instance or element that holds the attribute. An attribute
 * that states none, as a draft does, reads as one with no term.
 */
public class AttributeValuePropertyIriYamlTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String ROOT_IRI = "https://schema.metadatacenter.org/properties/root-attribute";
  private static final String NESTED_IRI = "https://schema.metadatacenter.org/properties/nested-attribute";

  private static final String INSTANCE = """
    {"@context": {"xsd": "http://www.w3.org/2001/XMLSchema#", "pav": "http://purl.org/pav/",
      "schema": "http://schema.org/", "oslc": "http://open-services.net/ns/core#",
      "skos": "http://www.w3.org/2004/02/skos/core#",
      "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
      "pav:createdOn": {"@type": "xsd:dateTime"}, "pav:createdBy": {"@type": "@id"},
      "pav:lastUpdatedOn": {"@type": "xsd:dateTime"}, "oslc:modifiedBy": {"@type": "@id"},
      "schema:isBasedOn": {"@type": "@id"}, "schema:name": {"@type": "xsd:string"},
      "schema:description": {"@type": "xsd:string"},
      "rdfs:label": {"@type": "xsd:string"}, "skos:notation": {"@type": "xsd:string"},
      "Extra": "https://schema.metadatacenter.org/properties/extra",
      "Root attribute": "%s",
      "Block": "https://schema.metadatacenter.org/properties/block"},
     "@id": "https://repo.metadatacenter.org/template-instances/i1",
     "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
     "schema:name": "Instance", "schema:description": "",
     "pav:createdOn": null, "pav:createdBy": null, "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
     "Extra": ["Root attribute"], "Root attribute": {"@value": "at the root"},
     "Block": {"@context": {"More": "https://schema.metadatacenter.org/properties/more",
                            "Nested attribute": "%s"},
               "@id": "https://repo.metadatacenter.org/template-element-instances/e1",
               "More": ["Nested attribute"], "Nested attribute": {"@value": "in an element"}}}"""
    .formatted(ROOT_IRI, NESTED_IRI);

  @Test public void bothFormsWriteTheIriBesideTheValue() throws Exception
  {
    TemplateInstanceArtifact instance = readJson(INSTANCE);
    for (boolean compact : new boolean[] { false, true }) {
      String yaml = YamlSerializer.getYAML(instance, compact, true);
      assertTrue(yaml.contains("propertyIri: \"" + ROOT_IRI + "\""), "the root attribute's IRI is written:\n" + yaml);
      assertTrue(yaml.contains("propertyIri: \"" + NESTED_IRI + "\""), "the nested attribute's IRI is written:\n" + yaml);
    }
  }

  @Test public void theIriSurvivesJsonThroughYaml() throws Exception
  {
    TemplateInstanceArtifact instance = readJson(INSTANCE);
    TemplateInstanceArtifact back = readYaml(YamlSerializer.getYAML(instance, false, true));

    assertEquals(URI.create(ROOT_IRI), back.jsonLdContext().get("Root attribute"));
    assertEquals(URI.create(NESTED_IRI),
      back.singleInstanceElementInstances().get("Block").jsonLdContext().get("Nested attribute"));

    ObjectNode written = new JsonArtifactRenderer().renderTemplateInstanceArtifact(back);
    assertEquals(ROOT_IRI, written.get("@context").get("Root attribute").asText());
    assertEquals(NESTED_IRI, written.get("Block").get("@context").get("Nested attribute").asText());
  }

  @Test public void anAttributeStatingNoIriReadsWithoutOne() throws Exception
  {
    String yaml = YamlSerializer.getYAML(readJson(INSTANCE), false, true)
      .replaceAll("(?m)^\\s*propertyIri: \"[^\"]*\"\\n", "");
    TemplateInstanceArtifact back = readYaml(yaml);

    assertFalse(back.jsonLdContext().containsKey("Root attribute"));
    assertEquals("at the root",
      back.attributeValueFieldInstanceGroups().get("Extra").get("Root attribute").jsonLdValue().orElseThrow());
  }

  @Test public void anIriThatIsNotAbsoluteIsRefused() throws Exception
  {
    String yaml = YamlSerializer.getYAML(readJson(INSTANCE), false, true).replace(ROOT_IRI, "relative/attribute");
    assertThrows(ArtifactParseException.class, () -> readYaml(yaml));
  }

  @Test public void anOrdinaryFieldStatesNoIri() throws Exception
  {
    String yaml = """
      type: instance
      name: "Instance"
      isBasedOn: "https://repo.metadatacenter.org/templates/t1"
      children:
        Text:
          value: "plain"
          propertyIri: "https://schema.metadatacenter.org/properties/text"
      """;
    // A field's IRI is its template's, not the instance's, so the YAML form has no key for it.
    assertEquals("propertyIri at /Text", Refusals.keyAndPath(() -> readYaml(yaml)));
  }

  private static TemplateInstanceArtifact readJson(String json) throws Exception
  {
    return new JsonArtifactReader().readTemplateInstanceArtifact((ObjectNode) MAPPER.readTree(json));
  }

  private static TemplateInstanceArtifact readYaml(String yaml) throws Exception
  {
    LinkedHashMap<String, Object> parsed = ArtifactSourceParser.yaml(yaml);
    return new YamlArtifactReader().readTemplateInstanceArtifact(parsed);
  }
}
