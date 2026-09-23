package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An element instance whose only content is attribute-value groups holds no ordinary child, so it
 * renders without a {@code children:} key. The reader tells an element from a field by that key,
 * or by an explicit {@code type: element-instance}, so without either the element came back as a
 * field and its groups were lost.
 *
 * <p>The renderer already emits the discriminator for an empty entry in a repeated element, for
 * the same reason: a bare {@code id:} map reads as a field. The rule is the same wherever
 * {@code children:} is absent. Two of 959 production instances sampled were affected.
 */
public class AttributeValueOnlyElementYamlTest
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

  private TemplateInstanceArtifact roundTrip(String json) throws Exception
  {
    TemplateInstanceArtifact fromJson = new JsonArtifactReader()
      .readTemplateInstanceArtifact((ObjectNode) MAPPER.readTree(json));
    String yaml = YamlSerializer.getYAML(fromJson, false, true);
    @SuppressWarnings("unchecked")
    LinkedHashMap<String, Object> parsed = new Yaml().loadAs(yaml, LinkedHashMap.class);
    return new YamlArtifactReader().readTemplateInstanceArtifact(parsed);
  }

  @Test public void anElementHoldingOnlyAttributeValuesStaysAnElement() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i1",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Block": {"@context": {}, "@id": "https://repo.metadatacenter.org/template-element-instances/e1",
                "Extra": ["Extra1"], "Extra1": {"@value": "kept"}}}""";
    TemplateInstanceArtifact instance = roundTrip(json);

    assertTrue(instance.singleInstanceElementInstances().containsKey("Block"),
      "the element came back as " + instance.childKeys() + " rather than an element instance");
    ElementInstanceArtifact block = instance.singleInstanceElementInstances().get("Block");
    assertEquals(1, block.attributeValueFieldInstanceGroups().size(),
      "the element's attribute-value group did not survive");
    assertTrue(block.attributeValueFieldInstanceGroups().get("Extra").containsKey("Extra1"),
      "the attribute itself did not survive");
  }

  @Test public void aRepeatedElementHoldingOnlyAttributeValuesStaysAnElement() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i2",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Block": [{"@context": {}, "@id": "https://repo.metadatacenter.org/template-element-instances/e2",
                 "Extra": ["Extra1"], "Extra1": {"@value": "kept"}}]}""";
    TemplateInstanceArtifact instance = roundTrip(json);

    assertTrue(instance.multiInstanceElementInstances().containsKey("Block"),
      "the repeated element came back as " + instance.childKeys() + " rather than element instances");
    ElementInstanceArtifact block = instance.multiInstanceElementInstances().get("Block").get(0);
    assertTrue(block.attributeValueFieldInstanceGroups().get("Extra").containsKey("Extra1"),
      "the attribute itself did not survive");
  }

  @Test public void aNestedElementHoldingOnlyAttributeValuesStaysAnElement() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i3",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Outer": {"@context": {}, "@id": "https://repo.metadatacenter.org/template-element-instances/e3",
                "Inner": {"@context": {}, "@id": "https://repo.metadatacenter.org/template-element-instances/e4",
                          "Extra": ["Extra1"], "Extra1": {"@value": "kept"}}}}""";
    TemplateInstanceArtifact instance = roundTrip(json);

    ElementInstanceArtifact outer = instance.singleInstanceElementInstances().get("Outer");
    assertTrue(outer != null && outer.singleInstanceElementInstances().containsKey("Inner"),
      "the nested element did not come back as an element instance");
    assertTrue(outer.singleInstanceElementInstances().get("Inner")
        .attributeValueFieldInstanceGroups().get("Extra").containsKey("Extra1"),
      "the nested attribute did not survive");
  }

  @Test public void anElementWithChildrenNeedsNoDiscriminator() throws Exception
  {
    String json = "{" + CONTEXT + """
      , "@id": "https://repo.metadatacenter.org/template-instances/i4",
      "schema:isBasedOn": "https://repo.metadatacenter.org/templates/t1",
      "schema:name": "Instance", "schema:description": "",
      "pav:createdOn": null, "pav:createdBy": null,
      "pav:lastUpdatedOn": null, "oslc:modifiedBy": null,
      "Block": {"@context": {}, "@id": "https://repo.metadatacenter.org/template-element-instances/e5",
                "Name": {"@value": "set"}}}""";
    TemplateInstanceArtifact fromJson = new JsonArtifactReader()
      .readTemplateInstanceArtifact((ObjectNode) MAPPER.readTree(json));
    String yaml = YamlSerializer.getYAML(fromJson, false, true);

    assertTrue(yaml.contains("children:"), "an element with a child states children:\n" + yaml);
    assertTrue(yaml.lines().noneMatch(line -> line.trim().equals("type: element-instance")),
      "children: already identifies the element, so no discriminator is written:\n" + yaml);
  }
}
