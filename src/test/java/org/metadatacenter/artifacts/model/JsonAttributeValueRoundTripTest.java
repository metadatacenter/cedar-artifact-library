package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Focused JSON round-trip tests for attribute-value field groups. Pins the
 * {@link TemplateInstanceArtifact#attributeValueFieldInstanceGroups()} contract against
 * regressions in the JSON shape (array of names + sibling single instances).
 */
public class JsonAttributeValueRoundTripTest
{
  private JsonArtifactReader reader;
  private JsonArtifactRenderer renderer;

  @BeforeEach public void setup()
  {
    reader = new JsonArtifactReader();
    renderer = new JsonArtifactRenderer();
  }

  private static FieldInstanceArtifact literal(String value)
  {
    return FieldInstanceArtifact.create(java.util.Collections.emptyList(), Optional.empty(),
      Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  @Test public void testJsonRoundTripPreservesAttributeValueGroupNameAndEntries()
  {
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("attr1", literal("foo"));
    group.put("attr2", literal("bar"));

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("Inst")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withAttributeValueFieldGroup("custom-attrs", group)
      .build();

    ObjectNode json = renderer.renderTemplateInstanceArtifact(original);
    TemplateInstanceArtifact roundTripped = reader.readTemplateInstanceArtifact(json);

    Map<String, Map<String, FieldInstanceArtifact>> groups = roundTripped.attributeValueFieldInstanceGroups();
    assertTrue(groups.containsKey("custom-attrs"),
      "Group name lost; got keys " + groups.keySet());
    assertEquals(2, groups.get("custom-attrs").size());
    assertEquals("foo", groups.get("custom-attrs").get("attr1").jsonLdValue().get());
    assertEquals("bar", groups.get("custom-attrs").get("attr2").jsonLdValue().get());
  }

  @Test public void testJsonRoundTripPreservesAttributeValueEntryOrder()
  {
    // UI consumers display attribute-value fields in builder order.
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("z-attr", literal("3"));
    group.put("a-attr", literal("1"));
    group.put("m-attr", literal("2"));

    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("Inst")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withAttributeValueFieldGroup("custom", group).build();

    ObjectNode json = renderer.renderTemplateInstanceArtifact(original);
    TemplateInstanceArtifact roundTripped = reader.readTemplateInstanceArtifact(json);

    List<String> keys = new ArrayList<>(
      roundTripped.attributeValueFieldInstanceGroups().get("custom").keySet());
    assertEquals(List.of("z-attr", "a-attr", "m-attr"), keys);
  }

  @Test public void testJsonRoundTripWithEmptyAttributeValueGroupRendersWithoutError()
  {
    // The empty-group case isn't required to survive (renderer may drop it), but the
    // round-trip must not throw.
    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("Inst")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withAttributeValueFieldGroup("empty", new LinkedHashMap<>())
      .build();

    ObjectNode json = renderer.renderTemplateInstanceArtifact(original);
    TemplateInstanceArtifact roundTripped = reader.readTemplateInstanceArtifact(json);

    assertEquals("Inst", roundTripped.name().get());
  }
}
