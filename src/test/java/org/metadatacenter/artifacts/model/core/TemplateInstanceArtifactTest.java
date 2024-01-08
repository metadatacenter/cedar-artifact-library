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
    String textField1Name = "Text Field 1";
    String element1Name = "Element 1";
    String textField2Name = "Text Field 2";

    FieldInstanceArtifact textField1Instance = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();
    ElementInstanceArtifact element1Instance = ElementInstanceArtifact.builder().withFieldInstance(textField1Name, textField1Instance).build();
    FieldInstanceArtifact textField2Instance1 = FieldInstanceArtifact.builder().withJsonLdValue("Value 2").build();
    FieldInstanceArtifact textField2Instance2 = FieldInstanceArtifact.builder().withJsonLdValue("Value 3").build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
      .withName(instanceName)
      .withIsBasedOn(isBasedOnTemplateUri)
      .withFieldInstance(textField2Name, textField2Instance1)
      .withFieldInstance(textField2Name, textField2Instance2)
      .withElementInstance(element1Name, element1Instance)
      .build();

    assertEquals(instanceName, templateInstanceArtifact.name().get());
    assertEquals(isBasedOnTemplateUri, templateInstanceArtifact.isBasedOn());
    assertEquals(1, templateInstanceArtifact.fieldInstances().size());
    assertEquals(1, templateInstanceArtifact.elementInstances().size());
    assertEquals(textField2Instance1, templateInstanceArtifact.fieldInstances().get(textField2Name).get(0));
    assertEquals(element1Instance, templateInstanceArtifact.elementInstances().get(element1Name).get(0));
  }
}