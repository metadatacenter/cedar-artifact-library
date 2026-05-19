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
 * Group J — focused JSON round-trip tests for attribute-value field groups. Existing
 * JsonArtifactRoundTripTest covers attribute-value via fixture files but asserts on the
 * whole artifact's equality — these tests pin the specific contract on
 * {@code attributeValueFieldInstanceGroups()} so a regression in the JSON shape (the
 * "array of names + sibling single instances" form) doesn't slip past.
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
    // The renderer serializes via an array of names + sibling single instances; entry order
    // must survive the round-trip (UI consumers display fields in this order).
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
    // Empty group is a degenerate case — the renderer's behavior here defines whether such
    // a group survives at all. We don't assert it's preserved; we just pin that the round-trip
    // doesn't throw, so consumers can pass through these instances safely.
    TemplateInstanceArtifact original = TemplateInstanceArtifact.builder().withName("Inst")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withAttributeValueFieldGroup("empty", new LinkedHashMap<>())
      .build();

    ObjectNode json = renderer.renderTemplateInstanceArtifact(original);
    TemplateInstanceArtifact roundTripped = reader.readTemplateInstanceArtifact(json);

    // Either the group survives (empty) or it gets dropped — both are acceptable; we just
    // want the round-trip itself not to throw.
    assertEquals("Inst", roundTripped.name().get());
  }
}
