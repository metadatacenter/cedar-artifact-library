package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InstanceArtifactVisitorTest
{
  @Test public void testVisitor()
  {
    String instanceName = "Template 1";
    String textFieldName1 = "Text Field 1";
    String element1Name = "Element 1";
    String textFieldName2 = "Text Field 2";
    String attributeValueFieldName = "Attribute-value Field A";
    String attributeValueFieldInstanceName = "Attribute-value Field Instance 1";
    URI isBasedOn = URI.create("https://repo.metadatacenter.org/templates/3232");

    FieldInstanceArtifact textField1 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();
    ElementInstanceArtifact element1 = ElementInstanceArtifact.builder().withFieldInstance(textFieldName1, textField1)
      .build();
    FieldInstanceArtifact textField2 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();
    FieldInstanceArtifact attributeValueFieldInstance1 = FieldInstanceArtifact.builder().withJsonLdValue("AV Value 1")
      .build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().withName(instanceName)
      .withIsBasedOn(isBasedOn).withFieldInstance(textFieldName2, textField2)
      .withElementInstance(element1Name, element1).withAttributeValueFieldInstances(attributeValueFieldName,
        Map.of(attributeValueFieldInstanceName, attributeValueFieldInstance1)).build();

    Reporter reporter = new Reporter();

    templateInstanceArtifact.accept(reporter);

    assertEquals(5, reporter.getReport().size());
    assertTrue(reporter.getReport().contains("/"));
    assertTrue(reporter.getReport().contains("/" + textFieldName2));
    assertTrue(reporter.getReport().contains("/" + element1Name));
    assertTrue(reporter.getReport().contains("/" + element1Name + "/" + textFieldName1));
    assertTrue(reporter.getReport().contains("/" + attributeValueFieldName));
  }

  private class Reporter implements InstanceArtifactVisitor
  {
    private List<String> report = new ArrayList<>();

    public List<String> getReport()
    {
      return report;
    }

    @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
    {
      report.add("/");
    }

    @Override public void visitElementInstanceArtifact(ElementInstanceArtifact childInstanceArtifact, String path)
    {
      report.add(path);
    }

    @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path)
    {
      report.add(path);
    }
  }
}