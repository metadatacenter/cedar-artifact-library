package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.Annotations;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Group K — Annotations equality and JSON round-trip ordering. Annotations is a record
 * around a LinkedHashMap; the equality semantics and the JSON renderer's ordering both
 * deserve explicit pinning.
 */
public class AnnotationsEqualityAndOrderTest
{
  private JsonArtifactReader reader;
  private JsonArtifactRenderer renderer;

  @BeforeEach public void setup()
  {
    reader = new JsonArtifactReader();
    renderer = new JsonArtifactRenderer();
  }

  @Test public void testAnnotationsEqualityIgnoresInsertionOrder()
  {
    // Annotations is a record wrapping a LinkedHashMap; LinkedHashMap.equals checks
    // entry-by-entry without regard to insertion order. Pin this contract — if someone
    // refactors equality to be order-sensitive, downstream callers that compare annotations
    // built in different orders would silently break.
    Annotations a = Annotations.builder()
      .withLiteralAnnotation("x", "1")
      .withLiteralAnnotation("y", "2")
      .build();
    Annotations b = Annotations.builder()
      .withLiteralAnnotation("y", "2")
      .withLiteralAnnotation("x", "1")
      .build();

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test public void testJsonRoundTripPreservesAnnotationInsertionOrder()
  {
    // Even though equality is order-insensitive, the renderer's *output* should be ordered
    // (so YAML/JSON files have stable diffs). Verify the round-tripped Annotations preserves
    // the original insertion order.
    Annotations annotations = Annotations.builder()
      .withLiteralAnnotation("z", "1")
      .withLiteralAnnotation("a", "2")
      .withLiteralAnnotation("m", "3")
      .build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder().withName("T")
      .withAnnotations(annotations).build();

    ObjectNode json = renderer.renderTemplateSchemaArtifact(original);
    TemplateSchemaArtifact roundTripped = reader.readTemplateSchemaArtifact(json);

    List<String> keys = new ArrayList<>(roundTripped.annotations().get().annotations().keySet());
    assertEquals(List.of("z", "a", "m"), keys,
      "Annotation insertion order lost through JSON round-trip");
  }
}
