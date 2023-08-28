package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemplateSchemaArtifactTest
{
  @Test
  public void testMinimalSimpleTemplateSchemaArtifact() {

    TemplateUi templateUi = TemplateUi.builder().build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test").withTemplateUi(templateUi).build();

    assertEquals(templateSchemaArtifact.name(), "Test");
  }

  @Test
  public void testCreateTemplateSchemaArtifact() {
    TemplateUi templateUi = TemplateUi.builder().build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test")
      .withTemplateUi(templateUi).build();

    assertEquals(templateSchemaArtifact.name(), "Test");
    assertEquals(templateSchemaArtifact.templateUi(), templateUi);
  }

  @Test(expected = IllegalStateException.class)
  public void testMissingName()
  {
    TemplateUi templateUi = TemplateUi.builder().build();

    TemplateSchemaArtifact.builder().withTemplateUi(templateUi).build();
  }

}