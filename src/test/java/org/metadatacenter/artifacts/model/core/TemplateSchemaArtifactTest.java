package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.report.ValidationReport;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;

public class TemplateSchemaArtifactTest
{
  @Test
  public void testMinimalSimpleTemplateSchemaArtifact()
  {
    String name = "Test Name";
    String description = "Test Description";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName(name)
      .withDescription(description)
      .build();

    assertEquals(templateSchemaArtifact.name(), name);
    assertEquals(templateSchemaArtifact.description(), description);
  }

  @Test
  public void testCreateTemplateSchemaArtifact() {

    String templateName = "Test Name";
    String header = "Header";
    String footer = "Footer";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName(templateName)
      .withHeader(header)
      .withFooter(footer)
      .build();

    assertEquals(templateSchemaArtifact.name(), templateName);
    assertEquals(templateSchemaArtifact.templateUi().header().get(), header);
    assertEquals(templateSchemaArtifact.templateUi().footer().get(), footer);
  }

  @Test
  public void testCreateTemplateSchemaArtifactWithChildren()
  {
    String templateName = "Template 1";
    String textFieldName1 = "Text Field 1";
    String textFieldName2 = "Text Field 2";
    String textField2Label = "text field 2 label";
    String textField2Description = "text field 2 description";

    TextField textField1 = TextField.builder().withName(textFieldName1).build();
    TextField textField2 = TextField.builder().withName(textFieldName2).build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName(templateName)
      .withFieldSchema(textField1)
      .withFieldSchema(textField2, textField2Label, textField2Description)
      .build();

    assertEquals(templateSchemaArtifact.name(), templateName);
    assertTrue(templateSchemaArtifact.hasFields());
    assertEquals(templateSchemaArtifact.getFieldSchemaArtifact(textFieldName1), textField1);
    assertEquals(templateSchemaArtifact.getFieldSchemaArtifact(textFieldName2), textField2);
    assertEquals(templateSchemaArtifact.templateUi().order().size(), 2);
    assertEquals(templateSchemaArtifact.templateUi().order().get(0), textFieldName1);
    assertEquals(templateSchemaArtifact.templateUi().order().get(1), textFieldName2);
    assertEquals(templateSchemaArtifact.templateUi().propertyLabels().size(), 2);
    assertEquals(templateSchemaArtifact.templateUi().propertyLabels().get(textFieldName2), textField2Label);
    assertEquals(templateSchemaArtifact.templateUi().propertyDescriptions().size(), 2);
    assertEquals(templateSchemaArtifact.templateUi().propertyDescriptions().get(textFieldName2), textField2Description);
  }

  @Test
  public void testMissingName()
  {
    Assertions.assertThrows(IllegalStateException.class, () -> TemplateSchemaArtifact.builder().build());
  }

  @Test
  public void testEmptyNameIsAcceptedBySetterAndRefusedByBuild()
  {
    TemplateSchemaArtifact.Builder builder = TemplateSchemaArtifact.builder().withName("");

    IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
    assertTrue(ex.getMessage().startsWith("field " + SCHEMA_ORG_NAME + " is empty in "), ex.getMessage());
  }

  @Test
  public void testBuilderRejectsWhitespaceOnlyName()
  {
    for (String name : BuilderValidationTest.WHITESPACE_ONLY_NAMES) {
      TemplateSchemaArtifact.Builder builder = TemplateSchemaArtifact.builder();

      IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> builder.withName(name));
      assertEquals("whitespace-only name passed to builder", ex.getMessage());

      // The refusal leaves the builder unchanged, so the internal name follows the next name given.
      TemplateSchemaArtifact template = builder.withName("Study").build();
      assertEquals("Study", template.name());
      assertEquals("Study template schema", template.internalName());

      assertThrows(IllegalArgumentException.class, () -> TemplateSchemaArtifact.builder(template).withName(name));
    }
  }

  @Test
  public void testBuilderAcceptsNameThatHasWhitespaceAroundIt()
  {
    assertEquals("\tStudy ", TemplateSchemaArtifact.builder().withName("\tStudy ").build().name());
  }

  @Test
  public void testRemovingAChildRemovesItsUiEntriesAndLeavesAValidTemplate() throws IOException
  {
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder().withName("Study")
      .withFieldSchema("title", TextField.builder().withName("Title").build(), "Title label", "Title description")
      .withElementSchema("address", ElementSchemaArtifact.builder().withName("Address").build())
      .withFieldSchema("keyword", TextField.builder().withName("Keyword").build()).build();

    TemplateSchemaArtifact pruned = TemplateSchemaArtifact.builder(original).withoutFieldSchema("title")
      .withoutElementSchema("address").build();

    assertEquals(Set.of("keyword"), pruned.fieldSchemas().keySet());
    assertTrue(pruned.elementSchemas().isEmpty());
    assertEquals(List.of("keyword"), pruned.templateUi().order());
    assertEquals(Set.of("keyword"), pruned.templateUi().propertyLabels().keySet());
    assertEquals(Set.of("keyword"), pruned.templateUi().propertyDescriptions().keySet());
    ValidationReport report = new CedarValidator()
      .validateTemplate(new JsonArtifactRenderer().renderTemplateSchemaArtifact(pruned));
    assertEquals("true", report.getValidationStatus(), report.getErrors().toString());

    // A removed key is free again, which is how a child is replaced.
    TemplateSchemaArtifact replaced = TemplateSchemaArtifact.builder(pruned)
      .withFieldSchema("title", TextField.builder().withName("Title").build(), "New label").build();
    assertEquals(List.of("keyword", "title"), replaced.templateUi().order());

    TemplateSchemaArtifact.Builder builder = TemplateSchemaArtifact.builder(original);
    assertThrows(IllegalArgumentException.class, () -> builder.withoutElementSchema("title"));
    assertThrows(IllegalArgumentException.class, () -> builder.withoutFieldSchema("address"));
    assertThrows(IllegalArgumentException.class, () -> builder.withoutFieldSchema("missing"));
  }

  @Test
  public void testAKeyHoldingAChildRefusesAnotherChildThroughEveryOverload()
  {
    // Every overload refuses a key the template already uses, for a field and an element alike, with
    // the same message and before changing the builder, so the child already held survives.
    TextField field = TextField.builder().withName("x").build();
    TextField otherField = TextField.builder().withName("x").withDescription("other").build();
    ElementSchemaArtifact element = ElementSchemaArtifact.builder().withName("x").build();
    List<UnaryOperator<TemplateSchemaArtifact.Builder>> addChild = List.of(
      b -> b.withElementSchema("x", element),
      b -> b.withElementSchema("x", element, "Label"),
      b -> b.withElementSchema("x", element, "Label", "Description"),
      b -> b.withFieldSchema("x", otherField),
      b -> b.withFieldSchema("x", otherField, "Label"),
      b -> b.withFieldSchema("x", otherField, "Label", "Description"));

    for (UnaryOperator<TemplateSchemaArtifact.Builder> add : addChild) {
      for (UnaryOperator<TemplateSchemaArtifact.Builder> existing : List.<UnaryOperator<TemplateSchemaArtifact.Builder>>of(
        b -> b.withFieldSchema("x", field), b -> b.withElementSchema("x", element))) {
        TemplateSchemaArtifact.Builder builder = existing.apply(TemplateSchemaArtifact.builder().withName("Study"));
        TemplateSchemaArtifact before = builder.build();

        IllegalArgumentException refusal = assertThrows(IllegalArgumentException.class, () -> add.apply(builder));

        assertEquals("Template already has a child x", refusal.getMessage());
        assertEquals(before, builder.build());
      }
    }
  }
}
