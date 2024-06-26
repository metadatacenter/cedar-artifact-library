package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  @Test(expected = IllegalStateException.class)
  public void testMissingName()
  {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .build();
  }
}