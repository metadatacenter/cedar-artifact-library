package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.*;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReservedInstanceNamesTest
{
  private final ObjectMapper mapper = new ObjectMapper();

  @Test void readersRejectReservedOrdinaryChildrenAtEveryDepth()
  {
    for (String key : List.of("__proto__", "constructor", "prototype", "@anything", "rdfs:label")) {
      ObjectNode field = mapper.createObjectNode();
      field.put("@value", "KEEP");
      for (boolean nested : List.of(false, true)) {
        ObjectNode json = mapper.createObjectNode().put("schema:name", "Audit")
          .put("schema:isBasedOn", "https://example.org/t");
        ObjectNode parent = nested ? json.putObject("Element") : json;
        if (nested) parent.putObject("@context");
        parent.set(key, field);
        assertThrows(RuntimeException.class, () -> new JsonArtifactReader().readTemplateInstanceArtifact(json), key);
        Map<String, Object> children = Map.of(key, Map.of("value", "KEEP"));
        Map<String, Object> yaml = Map.of("type", "instance", "name", "Audit", "isBasedOn", "https://example.org/t",
          "children", nested ? Map.of("Element", Map.of("children", children)) : children);
        LinkedHashMap<String, Object> source = mapper.convertValue(yaml, new TypeReference<>() {});
        assertThrows(RuntimeException.class, () -> new YamlArtifactReader().readTemplateInstanceArtifact(source), key);
      }
      assertThrows(IllegalStateException.class, () -> ElementInstanceArtifact.builder()
        .withSingleInstanceFieldInstance(key, TextFieldInstance.builder().withValue("KEEP").build()).build());
    }
  }

  @Test void elementGroupsMustBeWritableNestedAndStandalone()
  {
    var fields = new LinkedHashMap<String, FieldInstanceArtifact>();
    fields.put("given", TextFieldInstance.builder().withValue("KEEP").build());
    for (String key : ReservedNames.STANDALONE_ELEMENT_INSTANCE_YAML_KEYS)
      assertThrows(IllegalStateException.class, () -> ElementInstanceArtifact.builder()
        .withAttributeValueFieldGroup(key, fields).build(), key);
    var element = ElementInstanceArtifact.builder().withAttributeValueFieldGroup("annotations", fields).build();
    var rendered = new YamlArtifactRenderer(false).renderElementInstanceArtifact(element);
    var restored = new YamlArtifactReader().readElementInstanceArtifact(rendered);
    assertEquals(rendered, new YamlArtifactRenderer(false).renderElementInstanceArtifact(restored));
  }
}
