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
    // Annotations wraps a LinkedHashMap; LinkedHashMap.equals is entry-by-entry without
    // regard to insertion order.
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
    // Renderer output is ordered so YAML/JSON files have stable diffs.
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
