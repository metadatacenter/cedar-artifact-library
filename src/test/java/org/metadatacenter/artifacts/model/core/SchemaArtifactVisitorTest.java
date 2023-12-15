package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SchemaArtifactVisitorTest
{
  @Test
  public void testVisitor()
  {
    String templateName = "Template 1";
    String textFieldName1 = "Text Field 1";

    FieldSchemaArtifact textField1 = FieldSchemaArtifact.textFieldBuilder().withName(textFieldName1).build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
      .withName(templateName)
      .withFieldSchema(textField1)
      .build();

    Reporter reporter = new Reporter();

    templateSchemaArtifact.accept(reporter);

    assertEquals(2, reporter.getReport().size());
    assertEquals(templateName, reporter.getReport().get(0));
    assertEquals(textFieldName1, reporter.getReport().get(1));
  }

  private class Reporter implements SchemaArtifactVisitor
  {
    private List<String> report = new ArrayList<>();

    public List<String> getReport()
    {
      return report;
    }

    @Override public void visitTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
    {
      report.add(templateSchemaArtifact.name());
    }

    @Override public void visitElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact, String path)
    {
      report.add(elementSchemaArtifact.name());
    }

    @Override public void visitFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact, String path)
    {
      report.add(fieldSchemaArtifact.name());
    }
  }
}