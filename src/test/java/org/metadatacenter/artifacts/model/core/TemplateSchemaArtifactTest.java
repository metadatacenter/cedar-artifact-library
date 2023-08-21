package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemplateSchemaArtifactTest
{
  @Test
  public void testMinimalSimpleTemplateSchemaArtifact() {

    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test").withTemplateUI(templateUI).build();

    assertEquals(templateSchemaArtifact.name(), "Test");
  }

  @Test
  public void testCreateTemplateSchemaArtifact() {
    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test")
      .withTemplateUI(templateUI).build();

    assertEquals(templateSchemaArtifact.name(), "Test");
    assertEquals(templateSchemaArtifact.templateUI(), templateUI);
  }


  @Test(expected = IllegalStateException.class)
  public void testMissingName()
  {
    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact.builder().withTemplateUI(templateUI).build();
  }

}