package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TextFieldInstance;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trip contract for the standalone element-instance form: a top-level
 * {@code type: element-instance} YAML document, and the bare JSON-LD object on the JSON
 * side. The nested form (an element instance inside a template instance) predates this
 * and is covered by the template-instance round-trip tests; standalone documents exist
 * so an element instance can travel as a first-class artifact.
 */
public class StandaloneElementInstanceRoundTripTest
{
  @Test public void yamlRoundTripPreservesAStandaloneElementInstance()
  {
    ElementInstanceArtifact original = ElementInstanceArtifact.builder()
      .withName("Address")
      .withDescription("A postal address sub-record")
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template-element-instances/aaaa1111-2222-3333-4444-555566667777"))
      .withSingleInstanceFieldInstance("street", TextFieldInstance.builder().withValue("Main St").build())
      .withMultiInstanceFieldInstances("tags", List.of(
        TextFieldInstance.builder().withValue("home").build(),
        TextFieldInstance.builder().withValue("billing").build()))
      .withSingleInstanceElementInstance("inner", ElementInstanceArtifact.builder()
        .withSingleInstanceFieldInstance("note", TextFieldInstance.builder().withValue("n1").build())
        .build())
      .build();

    YamlArtifactRenderer renderer = new YamlArtifactRenderer(false);
    YamlArtifactReader reader = new YamlArtifactReader();

    LinkedHashMap<String, Object> rendering = renderer.renderElementInstanceArtifact(original);

    assertEquals("element-instance", rendering.get("type"));
    assertEquals("Address", rendering.get("name"));

    // Field instances read back as the generic FieldInstanceArtifactRecord (the per-type
    // record class is a schema-side fact the instance doesn't carry), so the round-trip
    // contract is a rendering fixpoint rather than model equality.
    ElementInstanceArtifact roundTripped = reader.readElementInstanceArtifact(rendering);
    assertEquals(rendering, renderer.renderElementInstanceArtifact(roundTripped));
    assertEquals(original.jsonLdId(), roundTripped.jsonLdId());
    assertEquals(original.name(), roundTripped.name());
    assertEquals(original.description(), roundTripped.description());
    assertEquals(original.childKeys(), roundTripped.childKeys());
    assertEquals("Main St",
      roundTripped.singleInstanceFieldInstances().get("street").jsonLdValue().orElseThrow());
    assertEquals(List.of("home", "billing"),
      roundTripped.multiInstanceFieldInstances().get("tags").stream()
        .map(f -> f.jsonLdValue().orElseThrow()).toList());
    assertEquals("n1",
      roundTripped.singleInstanceElementInstances().get("inner")
        .singleInstanceFieldInstances().get("note").jsonLdValue().orElseThrow());
  }

  @Test public void yamlRoundTripPreservesAnEmptySkeleton()
  {
    ElementInstanceArtifact original = ElementInstanceArtifact.builder()
      .withName("Address")
      .build();

    LinkedHashMap<String, Object> rendering =
      new YamlArtifactRenderer(false).renderElementInstanceArtifact(original);

    assertEquals("element-instance", rendering.get("type"),
      "an empty skeleton must still render as a typed document");

    ElementInstanceArtifact roundTripped = new YamlArtifactReader().readElementInstanceArtifact(rendering);
    assertEquals(original, roundTripped);  // no field instances, so model equality holds
  }

  @Test public void emptyEntriesInAMultiInstanceElementListSurviveTheYamlRoundTrip()
  {
    // Entry count is information: an appended-but-not-yet-filled sub-record must not vanish.
    // Empty entries render as `type: element-instance` stubs and classify as elements on read.
    ElementInstanceArtifact emptyEntry = ElementInstanceArtifact.builder()
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template-element-instances/bbbb1111-2222-3333-4444-555566667777"))
      .build();
    ElementInstanceArtifact filledEntry = ElementInstanceArtifact.builder()
      .withSingleInstanceFieldInstance("street", TextFieldInstance.builder().withValue("Second St").build())
      .build();
    ElementInstanceArtifact original = ElementInstanceArtifact.builder()
      .withName("Outer")
      .withMultiInstanceElementInstances("addresses", List.of(emptyEntry, filledEntry))
      .build();

    YamlArtifactRenderer renderer = new YamlArtifactRenderer(false);
    LinkedHashMap<String, Object> rendering = renderer.renderElementInstanceArtifact(original);

    ElementInstanceArtifact roundTripped = new YamlArtifactReader().readElementInstanceArtifact(rendering);
    List<ElementInstanceArtifact> entries = roundTripped.multiInstanceElementInstances().get("addresses");
    assertEquals(2, entries.size(), "both entries must survive; got: " + rendering);
    assertEquals(emptyEntry.jsonLdId(), entries.get(0).jsonLdId(),
      "the empty entry keeps its position and id; got: " + rendering);
    assertEquals("Second St",
      entries.get(1).singleInstanceFieldInstances().get("street").jsonLdValue().orElseThrow());
    assertEquals(rendering, renderer.renderElementInstanceArtifact(roundTripped), "fixpoint");
  }

  @Test public void yamlReaderRejectsAWrongDocumentType()
  {
    LinkedHashMap<String, Object> templateInstanceDocument = new LinkedHashMap<>();
    templateInstanceDocument.put("type", "instance");
    templateInstanceDocument.put("name", "Not an element instance");

    assertThrows(ArtifactParseException.class,
      () -> new YamlArtifactReader().readElementInstanceArtifact(templateInstanceDocument));
  }

  @Test public void jsonRenderReadRenderReachesAFixpoint()
  {
    // Strict model equality doesn't hold across the JSON boundary for a context-less
    // instance (the renderer mints the deterministic default @context, which the reader
    // then carries). The contract is a fixpoint instead: after one render+read, a second
    // render+read changes nothing.
    ElementInstanceArtifact original = ElementInstanceArtifact.builder()
      .withName("Address")
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/template-element-instances/aaaa1111-2222-3333-4444-555566667777"))
      .withSingleInstanceFieldInstance("street", TextFieldInstance.builder().withValue("Main St").build())
      .build();

    JsonArtifactRenderer renderer = new JsonArtifactRenderer();
    JsonArtifactReader reader = new JsonArtifactReader();

    ObjectNode firstRendering = renderer.renderElementInstanceArtifact(original);
    ElementInstanceArtifact firstRead = reader.readElementInstanceArtifact(firstRendering);
    ObjectNode secondRendering = renderer.renderElementInstanceArtifact(firstRead);
    ElementInstanceArtifact secondRead = reader.readElementInstanceArtifact(secondRendering);

    assertEquals(firstRead, secondRead);
    assertEquals(firstRendering, secondRendering);
    assertEquals("Main St", secondRendering.path("street").path("@value").asText(),
      "the field value must survive the JSON boundary; got: " + secondRendering);
    assertTrue(secondRendering.path("@context").has("street"),
      "the minted default @context must cover the child; got: " + secondRendering);
  }
}
