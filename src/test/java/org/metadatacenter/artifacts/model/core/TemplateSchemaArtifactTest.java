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
  public void testAKeyHoldingOneKindOfChildRefusesTheOtherKind()
  {
    // Some overloads check the key themselves and others rely on the UI builder's order list; either
    // way a template must never hold a field and an element under one key.
    TextField field = TextField.builder().withName("x").build();
    ElementSchemaArtifact element = ElementSchemaArtifact.builder().withName("x").build();
    List<UnaryOperator<TemplateSchemaArtifact.Builder>> addElement = List.of(
      b -> b.withElementSchema("x", element),
      b -> b.withElementSchema("x", element, "Label"),
      b -> b.withElementSchema("x", element, "Label", "Description"));
    List<UnaryOperator<TemplateSchemaArtifact.Builder>> addField = List.of(
      b -> b.withFieldSchema("x", field),
      b -> b.withFieldSchema("x", field, "Label"),
      b -> b.withFieldSchema("x", field, "Label", "Description"));

    for (UnaryOperator<TemplateSchemaArtifact.Builder> add : addElement)
      assertThrows(IllegalArgumentException.class,
        () -> add.apply(TemplateSchemaArtifact.builder().withName("Study").withFieldSchema("x", field)).build());
    for (UnaryOperator<TemplateSchemaArtifact.Builder> add : addField)
      assertThrows(IllegalArgumentException.class,
        () -> add.apply(TemplateSchemaArtifact.builder().withName("Study").withElementSchema("x", element)).build());
  }
}
