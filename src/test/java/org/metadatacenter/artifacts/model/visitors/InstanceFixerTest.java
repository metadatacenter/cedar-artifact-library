package org.metadatacenter.artifacts.model.visitors;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InstanceFixerTest
{
  private static FieldInstanceArtifact literal(String value)
  {
    return FieldInstanceArtifact.create(Collections.emptyList(), Optional.empty(),
      Optional.of(value), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  // ---- Constructor validation ----

  @Test public void testConstructorRejectsTemplateWithoutJsonLdId()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T").build();
    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("I")
      .withIsBasedOn(URI.create("https://example.com/templates/whatever")).build();

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> new InstanceFixer(template, instance));
    assertTrue(ex.getMessage().contains("@id"),
      "Expected message to reference @id; got: " + ex.getMessage());
  }

  @Test public void testConstructorRejectsMismatchedIsBasedOn()
  {
    URI templateId = URI.create("https://example.com/templates/A");
    URI wrongId = URI.create("https://example.com/templates/B");

    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T")
      .withJsonLdId(templateId).build();
    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("I")
      .withIsBasedOn(wrongId).build();

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> new InstanceFixer(template, instance));
    assertTrue(ex.getMessage().contains(templateId.toString()));
    assertTrue(ex.getMessage().contains(wrongId.toString()));
  }

  @Test public void testConstructorAcceptsMatchingTemplateAndInstance()
  {
    URI templateId = URI.create("https://example.com/templates/A");
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T")
      .withJsonLdId(templateId).build();
    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("I")
      .withIsBasedOn(templateId).build();

    assertDoesNotThrow(() -> new InstanceFixer(template, instance));
  }

  // ---- visitFieldInstanceArtifact behavior ----

  @Test public void testVisitFieldInstanceArtifactThrowsForUnknownPath()
  {
    URI templateId = URI.create("https://example.com/templates/A");
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T")
      .withJsonLdId(templateId).build();
    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("I")
      .withIsBasedOn(templateId).build();
    InstanceFixer fixer = new InstanceFixer(template, instance);

    // Field-visit needs a field schema mapped at the path; an unknown path triggers a
    // RuntimeException describing the missing specification.
    RuntimeException ex = assertThrows(RuntimeException.class,
      () -> fixer.visitFieldInstanceArtifact(literal("anything"), "/not-in-template"));
    assertTrue(ex.getMessage().contains("/not-in-template"));
    assertTrue(ex.getMessage().contains("T"));
  }

  @Test public void testVisitFieldInstanceArtifactIsNoOpForKnownField()
  {
    URI templateId = URI.create("https://example.com/templates/A");
    TextField studyId = TextField.builder().withName("StudyID").build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T")
      .withJsonLdId(templateId).withFieldSchema(studyId).build();
    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("I")
      .withIsBasedOn(templateId).build();
    InstanceFixer fixer = new InstanceFixer(template, instance);

    // TemplateReporter indexes the field at "/StudyID". Visiting that path is currently a
    // no-op (the per-constraint-type arms in InstanceFixer are empty TODOs) — but it must
    // not throw.
    assertDoesNotThrow(() -> fixer.visitFieldInstanceArtifact(literal("S001"), "/StudyID"));
  }

  // ---- Empty-visit-method behavior (documents current scaffold state) ----

  @Test public void testTopLevelVisitMethodsAreNoOps()
  {
    URI templateId = URI.create("https://example.com/templates/A");
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("T")
      .withJsonLdId(templateId).build();
    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("I")
      .withIsBasedOn(templateId).build();
    InstanceFixer fixer = new InstanceFixer(template, instance);

    // visitTemplateInstanceArtifact and visitAttributeValueFieldInstanceArtifact are currently
    // stubs; this test pins that they don't throw so adding real logic later will register
    // as a behavior change in the test suite.
    assertDoesNotThrow(() -> fixer.visitTemplateInstanceArtifact(instance));
    assertDoesNotThrow(() -> fixer.visitAttributeValueFieldInstanceArtifact(
      literal("av"), "/group/attr", "/specPath"));
    assertNotNull(fixer);
  }
}
