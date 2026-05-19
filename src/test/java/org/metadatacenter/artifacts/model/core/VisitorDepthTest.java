package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Group G — Visitor pattern depth. Existing tests cover the single-template happy path; this
 * batch pins the contract for deeply nested templates and multi-instance children, which are
 * the harder paths consumers actually rely on (e.g., REDCap export, terminology lookups).
 */
public class VisitorDepthTest
{
  private static FieldInstanceArtifact literal(String value)
  {
    return FieldInstanceArtifact.create(java.util.Collections.emptyList(), Optional.empty(),
      Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  @Test public void testSchemaVisitorVisitsDeeplyNestedChildren()
  {
    // template -> outerElement -> innerElement -> textField
    TextField leaf = TextField.builder().withName("leaf").build();
    ElementSchemaArtifact inner = ElementSchemaArtifact.builder()
      .withName("inner").withFieldSchema(leaf).build();
    ElementSchemaArtifact outer = ElementSchemaArtifact.builder()
      .withName("outer").withElementSchema(inner).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder()
      .withName("T").withElementSchema(outer).build();

    SchemaReporter reporter = new SchemaReporter();
    template.accept(reporter);

    // Visitor must reach every level — 1 template + 2 elements + 1 field = 4 names.
    assertEquals(4, reporter.names.size());
    assertTrue(reporter.names.contains("T"));
    assertTrue(reporter.names.contains("outer"));
    assertTrue(reporter.names.contains("inner"));
    assertTrue(reporter.names.contains("leaf"));
  }

  @Test public void testSchemaVisitorVisitsInDocumentOrder()
  {
    // The visit order should follow childKeys insertion order, not alphabetical.
    TextField a = TextField.builder().withName("alpha").build();
    TextField b = TextField.builder().withName("zulu").build();
    TextField c = TextField.builder().withName("mike").build();

    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T")
      .withFieldSchema(b).withFieldSchema(c).withFieldSchema(a).build();

    SchemaReporter reporter = new SchemaReporter();
    template.accept(reporter);

    // First name is the template, then children in insertion order.
    assertEquals("T", reporter.names.get(0));
    assertEquals("zulu", reporter.names.get(1));
    assertEquals("mike", reporter.names.get(2));
    assertEquals("alpha", reporter.names.get(3));
  }

  @Test public void testInstanceVisitorVisitsMultiInstanceFieldElements()
  {
    FieldInstanceArtifact a = literal("first");
    FieldInstanceArtifact b = literal("second");
    FieldInstanceArtifact c = literal("third");

    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("Inst")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withMultiInstanceFieldInstances("keywords", List.of(a, b, c))
      .build();

    InstanceReporter reporter = new InstanceReporter();
    instance.accept(reporter);

    // 1 template + 3 multi-instance field entries
    assertEquals(4, reporter.paths.size());
    // Multi-instance paths are indexed.
    assertTrue(reporter.paths.stream().anyMatch(p -> p.contains("keywords")),
      "Expected keywords path; got " + reporter.paths);
  }

  @Test public void testInstanceVisitorFiresAttributeValueHookPerEntry()
  {
    // visitAttributeValueFieldInstanceArtifact must fire once per entry in the group, not once
    // per group. This is the contract `InstanceReporter` relies on for spec-path lookups.
    LinkedHashMap<String, FieldInstanceArtifact> group = new LinkedHashMap<>();
    group.put("attr1", literal("a"));
    group.put("attr2", literal("b"));
    group.put("attr3", literal("c"));

    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("Inst")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/abc"))
      .withAttributeValueFieldGroup("custom", group).build();

    AvCountingReporter reporter = new AvCountingReporter();
    instance.accept(reporter);

    assertEquals(3, reporter.attrCalls);
  }

  // ---- visitor implementations ----

  private static class SchemaReporter implements SchemaArtifactVisitor
  {
    final List<String> names = new ArrayList<>();

    @Override public void visitTemplateSchemaArtifact(TemplateSchemaArtifact t) { names.add(t.name()); }
    @Override public void visitElementSchemaArtifact(ElementSchemaArtifact e, String p) { names.add(e.name()); }
    @Override public void visitFieldSchemaArtifact(FieldSchemaArtifact f, String p) { names.add(f.name()); }
  }

  private static class InstanceReporter implements InstanceArtifactVisitor
  {
    final List<String> paths = new ArrayList<>();

    @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact t) { paths.add("/"); }
    @Override public void visitElementInstanceArtifact(ElementInstanceArtifact e, String p) { paths.add(p); }
    @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact f, String p) { paths.add(p); }
    @Override public void visitAttributeValueFieldInstanceArtifact(FieldInstanceArtifact f, String p, String sp)
    {
      paths.add(p);
    }
  }

  private static class AvCountingReporter implements InstanceArtifactVisitor
  {
    int attrCalls = 0;

    @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact t) {}
    @Override public void visitElementInstanceArtifact(ElementInstanceArtifact e, String p) {}
    @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact f, String p) {}
    @Override public void visitAttributeValueFieldInstanceArtifact(FieldInstanceArtifact f, String p, String sp)
    {
      attrCalls++;
    }
  }
}
