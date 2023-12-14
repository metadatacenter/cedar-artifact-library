package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TemplateInstanceArtifactVisitorTest
{
  @Test
  public void testVisitor()
  {
    String instanceName = "Template 1";
    String textFieldName1 = "Text Field 1";
    String element1Name = "Element 1";
    String textFieldName2 = "Text Field 2";
    URI isBasedOn = URI.create("https://repo.metadatacenter.org/templates/3232");

    FieldInstanceArtifact textField1 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();
    ElementInstanceArtifact element1 = ElementInstanceArtifact.builder().withFieldInstance(textFieldName1, textField1).build();
    FieldInstanceArtifact textField2 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
      .withName(instanceName)
      .withIsBasedOn(isBasedOn)
      .withFieldInstance(textFieldName2, textField2)
      .withElementInstance(element1Name, element1)
      .build();

    Reporter reporter = new Reporter();

    templateInstanceArtifact.accept(reporter, "/");

    assertEquals(4, reporter.getReport().size());
    assertEquals("/", reporter.getReport().get(0));
    assertEquals("/" + textFieldName2, reporter.getReport().get(1));
    assertEquals("/" + element1Name, reporter.getReport().get(2));
    assertEquals("/" + element1Name + "/" + textFieldName1, reporter.getReport().get(3));
  }

  private class Reporter implements InstanceArtifactVisitor
  {
    private List<String> report = new ArrayList<>();

    public List<String> getReport()
    {
      return report;
    }

    @Override public void visitTemplateInstanceArtifact(ParentInstanceArtifact parentInstanceArtifact, String path)
    {
      report.add(path);
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