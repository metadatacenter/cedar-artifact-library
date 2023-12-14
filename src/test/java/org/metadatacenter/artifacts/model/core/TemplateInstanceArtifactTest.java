package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class TemplateInstanceArtifactTest
{
  @Test
  public void basicInstanceBuilderTest()
  {
    String instanceName = "Template 1";
    URI isBasedOnTemplateUri = URI.create("https://repo.metadatacenter.org/templates/3232");
    String textFieldName1 = "Text Field 1";
    String element1Name = "Element 1";
    String textFieldName2 = "Text Field 2";

    FieldInstanceArtifact textField1 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();
    ElementInstanceArtifact element1 = ElementInstanceArtifact.builder().withFieldInstance(textFieldName1, textField1).build();
    FieldInstanceArtifact textField2 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
      .withName(instanceName)
      .withIsBasedOn(isBasedOnTemplateUri)
      .withFieldInstance(textFieldName2, textField2)
      .withElementInstance(element1Name, element1)
      .build();

    assertEquals(instanceName, templateInstanceArtifact.name().get());
    assertEquals(isBasedOnTemplateUri, templateInstanceArtifact.isBasedOn());
    assertEquals(1, templateInstanceArtifact.fieldInstances().size());
    assertEquals(1, templateInstanceArtifact.elementInstances().size());
    assertEquals(textField2, templateInstanceArtifact.fieldInstances().get(textFieldName2).get(0));
    assertEquals(element1, templateInstanceArtifact.elementInstances().get(element1Name).get(0));

  }
}