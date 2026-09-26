package org.metadatacenter.artifacts.model.renderer;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class YamlAttributeGroupNamesTest
{
  private LinkedHashMap<String, FieldInstanceArtifact> values()
  {
    var values = new LinkedHashMap<String, FieldInstanceArtifact>();
    values.put("answer", TextFieldInstance.builder().withValue("kept").build());
    return values;
  }

  @Test void refusesEnvelopeCollisionsWhenTheInstanceIsBuilt()
  {
    // The model refuses these before any renderer sees them; see ReservedNames.
    for (String key : List.of("type", "id", "children", "name")) {
      assertThrows(IllegalStateException.class, () -> TemplateInstanceArtifact.builder().withName("Instance")
        .withIsBasedOn(URI.create("urn:template"))
        .withAttributeValueFieldGroup(key, values()).build());
    }
    for (String key : List.of("type", "id", "children")) {
      assertThrows(IllegalStateException.class,
        () -> ElementInstanceArtifact.builder().withAttributeValueFieldGroup(key, values()).build());
    }
  }

  @Test void refusesAStandaloneElementInstanceWhoseGroupCollidesWithItsEnvelope()
  {
    // A nested element reserves only type, id and children, so the model accepts a group named name;
    // written on its own, the element also carries a name, and the renderer still refuses it.
    var element = ElementInstanceArtifact.builder().withAttributeValueFieldGroup("name", values()).build();
    for (boolean compact : List.of(false, true))
      assertThrows(ArtifactRenderException.class,
        () -> new YamlArtifactRenderer(compact).renderElementInstanceArtifact(element));
  }

  @Test void nestedNameGroupSurvivesRoundTrip()
  {
    var element = ElementInstanceArtifact.builder().withJsonLdId(URI.create("urn:element"))
      .withAttributeValueFieldGroup("name", values()).build();
    var instance = TemplateInstanceArtifact.builder().withName("Instance")
      .withIsBasedOn(URI.create("urn:template"))
      .withSingleInstanceElementInstance("Element", element).build();
    for (boolean compact : List.of(false, true)) {
      var yaml = new YamlArtifactRenderer(compact).renderTemplateInstanceArtifact(instance);
      var result = new YamlArtifactReader().readTemplateInstanceArtifact(yaml)
        .singleInstanceElementInstances().get("Element");
      assertEquals(element.attributeValueFieldInstanceGroups().keySet(), result.attributeValueFieldInstanceGroups().keySet());
      var group = result.attributeValueFieldInstanceGroups().get("name");
      assertEquals(values().keySet(), group.keySet());
      assertEquals("kept", group.get("answer").jsonLdValue().orElseThrow());
      if (!compact) assertEquals(element.jsonLdId(), result.jsonLdId());
    }
  }
}
