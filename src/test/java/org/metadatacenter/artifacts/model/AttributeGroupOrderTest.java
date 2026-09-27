package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import java.net.URI;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AttributeGroupOrderTest {
  @Test void preservesRootAndNestedGroupOrderAndDefensiveCopies() {
    // Several permutations ensure the test cannot pass by chance with Map.copyOf's JVM-dependent order.
    for (var names : List.of(List.of("filename1", "filename2", "filename3"),
      List.of("filename3", "filename2", "filename1"), List.of("filename2", "filename1", "filename3"))) {
      var group = new LinkedHashMap<String, FieldInstanceArtifact>();
      for (String name : names) group.put(name, TextFieldInstance.builder().withValue(name + ".txt").build());
      var element = ElementInstanceArtifact.builder().withAttributeValueFieldGroup("files", group).build();
      var instance = TemplateInstanceArtifact.builder().withName("Example").withIsBasedOn(URI.create("urn:template"))
        .withAttributeValueFieldGroup("files", group).withSingleInstanceElementInstance("nested", element).build();
      group.clear();
      assertEquals(names, new ArrayList<>(element.attributeValueFieldInstanceGroups().get("files").keySet()));
      assertThrows(UnsupportedOperationException.class, () -> element.attributeValueFieldInstanceGroups().get("files").clear());
      for (boolean compact : new boolean[] {false, true}) {
        var yaml = new YamlArtifactRenderer(compact).renderTemplateInstanceArtifact(instance);
        var restored = new YamlArtifactReader(compact).readTemplateInstanceArtifact(yaml);
        var json = new JsonArtifactRenderer().renderTemplateInstanceArtifact(restored);
        for (var node : List.of(json.get("files"), json.get("nested").get("files"))) {
          var actual = new ArrayList<String>();
          node.forEach(value -> actual.add(value.asText()));
          assertEquals(names, actual);
        }
      }
    }
  }
}
