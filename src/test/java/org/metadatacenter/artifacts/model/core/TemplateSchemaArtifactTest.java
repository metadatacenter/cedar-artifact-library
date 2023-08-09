package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import static org.junit.Assert.*;

public class TemplateSchemaArtifactTest
{
  @Test
  public void testMinimalSimpleTemplateSchemaArtifact() {

    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test").withTemplateUI(templateUI).build();

    assertEquals(templateSchemaArtifact.getName(), "Test");
  }

  @Test
  public void testCreateTemplateSchemaArtifact() {
    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test")
      .withTemplateUI(templateUI).build();

    assertEquals(templateSchemaArtifact.getName(), "Test");
    assertEquals(templateSchemaArtifact.getTemplateUI(), templateUI);
  }


  @Test(expected = IllegalStateException.class)
  public void testMissingName()
  {
    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact.builder().withTemplateUI(templateUI).build();
  }

}