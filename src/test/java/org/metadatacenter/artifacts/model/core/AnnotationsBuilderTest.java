package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationsBuilderTest
{
  @Test public void testLiteralAndIriAnnotationsMixed()
  {
    URI doi = URI.create("https://doi.org/10.82658/8vc1");
    Annotations annotations = Annotations.builder()
      .withLiteralAnnotation("source", "manual")
      .withIriAnnotation("doi", doi)
      .build();

    AnnotationValue source = annotations.annotations().get("source");
    AnnotationValue doiAnn = annotations.annotations().get("doi");

    assertInstanceOf(LiteralAnnotationValue.class, source);
    assertEquals("manual", ((LiteralAnnotationValue) source).getValue());
    assertInstanceOf(IriAnnotationValue.class, doiAnn);
    assertEquals(doi, ((IriAnnotationValue) doiAnn).getValue());
  }

  @Test public void testWithAnnotationGenericPath()
  {
    // The generic withAnnotation(name, AnnotationValue) overload accepts a pre-built
    // AnnotationValue rather than constructing one from a String / URI.
    LiteralAnnotationValue lit = new LiteralAnnotationValue("preset");
    Annotations annotations = Annotations.builder()
      .withAnnotation("note", lit)
      .build();

    assertEquals(lit, annotations.annotations().get("note"));
  }

  @Test public void testAnnotationsPreserveInsertionOrder()
  {
    // Builder uses a LinkedHashMap internally; entries must surface in insertion order.
    Annotations annotations = Annotations.builder()
      .withLiteralAnnotation("z", "1")
      .withLiteralAnnotation("a", "2")
      .withLiteralAnnotation("m", "3")
      .build();

    List<String> keys = new ArrayList<>(annotations.annotations().keySet());
    assertEquals(List.of("z", "a", "m"), keys);
    // And iterating Map.Entry sees the same order.
    List<String> entryKeys = new ArrayList<>();
    for (Map.Entry<String, AnnotationValue> e : annotations.annotations().entrySet())
      entryKeys.add(e.getKey());
    assertEquals(List.of("z", "a", "m"), entryKeys);
    assertTrue(annotations.annotations() instanceof java.util.LinkedHashMap);
  }
}
