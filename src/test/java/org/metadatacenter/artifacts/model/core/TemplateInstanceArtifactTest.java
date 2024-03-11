package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    ElementInstanceArtifact element1Instance = ElementInstanceArtifact.builder().withSingleInstanceFieldInstance(textField1Name, textField1Instance).build();
    FieldInstanceArtifact textField2Instance1 = FieldInstanceArtifact.builder().withJsonLdValue("Value 2").build();
    FieldInstanceArtifact textField2Instance2 = FieldInstanceArtifact.builder().withJsonLdValue("Value 3").build();
    List<FieldInstanceArtifact> textField2Instances = new ArrayList<>();
    textField2Instances.add(textField2Instance1);
    textField2Instances.add(textField2Instance2);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
      .withName(instanceName)
      .withIsBasedOn(isBasedOnTemplateUri)
      .withMultiInstanceFieldInstances(textField2Name, textField2Instances)
      .withElementInstance(element1Name, element1Instance)
      .build();

    assertEquals(instanceName, templateInstanceArtifact.name().get());
    assertEquals(isBasedOnTemplateUri, templateInstanceArtifact.isBasedOn());
    assertEquals(0, templateInstanceArtifact.singleInstanceFieldInstances().size());
    assertEquals(1, templateInstanceArtifact.multiInstanceFieldInstances().size());
    assertEquals(1, templateInstanceArtifact.singleInstanceElementInstances().size());
    assertEquals(0, templateInstanceArtifact.multiInstanceElementInstances().size());
    assertEquals(2, templateInstanceArtifact.multiInstanceFieldInstances().get(textField2Name).size());
    assertEquals(textField2Instance1, templateInstanceArtifact.multiInstanceFieldInstances().get(textField2Name).get(0));
    assertEquals(textField2Instance2, templateInstanceArtifact.multiInstanceFieldInstances().get(textField2Name).get(1));
    assertEquals(element1Instance, templateInstanceArtifact.singleInstanceElementInstances().get(element1Name));
  }

}