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

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test")
      .build();

    assertEquals(templateSchemaArtifact.name(), "Test");
  }

  @Test
  public void testCreateTemplateSchemaArtifactWithChildren()
  {
    String templateName = "Template 1";
    String textFieldName = "Text Field 1";

    FieldSchemaArtifact textField = FieldSchemaArtifact.textFieldBuilder().withName(textFieldName).build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName(templateName)
      .withFieldSchema(textField)
      .build();

    assertEquals(templateSchemaArtifact.name(), templateName);
    assertTrue(templateSchemaArtifact.hasFields());
    assertEquals(templateSchemaArtifact.getFieldSchemaArtifact(textFieldName), textField);
  }

  @Test(expected = IllegalStateException.class)
  public void testMissingName()
  {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .build();
  }

}