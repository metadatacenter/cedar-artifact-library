package org.metadatacenter.artifacts.model.core;

import org.junit.Before;
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
    Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    Map<String, URI> childPropertyURIs = new HashMap<>();
    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName("Test")
      .withFieldSchemas(fieldSchemas)
      .withElementSchemas(elementSchemas)
      .withChildPropertyURIs(childPropertyURIs)
      .withTemplateUI(templateUI).build();

    assertEquals(templateSchemaArtifact.getName(), "Test");
    assertEquals(templateSchemaArtifact.getFieldSchemas(), fieldSchemas);
    assertEquals(templateSchemaArtifact.getElementSchemas(), elementSchemas);
    assertEquals(templateSchemaArtifact.getChildPropertyURIs(), childPropertyURIs);
    assertEquals(templateSchemaArtifact.getTemplateUI(), templateUI);
  }


  @Test(expected = IllegalStateException.class)
  public void testMissingName()
  {
    TemplateUI templateUI = TemplateUI.builder().build();

    TemplateSchemaArtifact.builder().withTemplateUI(templateUI).build();
  }

  @Test(expected = IllegalStateException.class)
  public void testMissingUI()
  {
    TemplateSchemaArtifact.builder().withName("Test").build();
  }

}