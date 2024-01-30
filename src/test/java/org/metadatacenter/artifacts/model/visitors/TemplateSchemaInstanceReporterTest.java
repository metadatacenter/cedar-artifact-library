package org.metadatacenter.artifacts.model.visitors;

import org.junit.Test;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TemplateSchemaInstanceReporterTest
{
  @Test
  public void basicTemplateReporterTest()
  {
    String textFieldName = "text field";
    String regex = "*";
    String numericFieldName = "numeric field";
    XsdNumericDatatype numericType = XsdNumericDatatype.DOUBLE;
    String temporalFieldName = "temporal field";
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.TIME;
    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    String elementName = "my element";
    String templateName = "my template";

    FieldSchemaArtifact textFieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder()
      .withName(textFieldName)
      .withRegex(regex)
      .build();

    FieldSchemaArtifact numericFieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder()
      .withName(numericFieldName)
      .withNumericType(numericType)
      .build();

    FieldSchemaArtifact temporalFieldSchemaArtifact = FieldSchemaArtifact.temporalFieldBuilder()
      .withName(temporalFieldName)
      .withTemporalType(temporalType)
      .withTemporalGranularity(temporalGranularity)
      .build();

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().
      withName(elementName).
      withFieldSchema(numericFieldSchemaArtifact).
      withFieldSchema(temporalFieldSchemaArtifact).
      build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withName(templateName).
      withFieldSchema(textFieldSchemaArtifact).
      withElementSchema(elementSchemaArtifact).
      build();

    TemplateReporter reporter = new TemplateReporter(templateSchemaArtifact);

    assertTrue(reporter.getValueConstraints("/" + textFieldName).isPresent());
    assertTrue(reporter.getValueConstraints("/" + elementName + "/" + numericFieldName).isPresent());
    assertTrue(reporter.getValueConstraints("/" + elementName + "/" + temporalFieldName).isPresent());

    assertEquals(textFieldSchemaArtifact, reporter.getFieldSchema("/" + textFieldName).get());
    assertEquals(numericFieldSchemaArtifact, reporter.getFieldSchema("/" + elementName + "/" + numericFieldName).get());
    assertEquals(temporalFieldSchemaArtifact, reporter.getFieldSchema("/" + elementName + "/" + temporalFieldName).get());
    assertEquals(elementSchemaArtifact, reporter.getElementSchema("/" + elementName ).get());

    assertTrue(reporter.getValueConstraints("/" + textFieldName).get().isTextValueConstraint());
    assertEquals(regex, reporter.getValueConstraints("/" + textFieldName).get().asTextValueConstraints().regex().get());

    assertTrue(reporter.getValueConstraints("/" + elementName + "/" + numericFieldName).get().isNumericValueConstraint());
    assertEquals(numericType, reporter.getValueConstraints("/" + elementName + "/" + numericFieldName).get().asNumericValueConstraints().numberType());

    assertTrue(reporter.getValueConstraints("/" + elementName + "/" + temporalFieldName).get().isTemporalValueConstraint());
    assertEquals(temporalType, reporter.getValueConstraints("/" + elementName + "/" + temporalFieldName).get().asTemporalValueConstraints().temporalType());
  }

  @Test
  public void normalizedPathReporterTest()
  {
    String numericFieldName = "numeric field";
    XsdNumericDatatype numericType = XsdNumericDatatype.DOUBLE;
    String elementName = "my element";
    String templateName = "my template";

    FieldSchemaArtifact numericFieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder()
      .withName(numericFieldName)
      .withNumericType(numericType)
      .build();

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().
      withName(elementName).
      withFieldSchema(numericFieldSchemaArtifact).
      build();

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withName(templateName).
      withElementSchema(elementSchemaArtifact).
      build();

    TemplateReporter reporter = new TemplateReporter(templateSchemaArtifact);

    assertTrue(reporter.getValueConstraints("/" + elementName + "/" + numericFieldName).isPresent());
    assertTrue(reporter.getValueConstraints("/" + elementName + "[3]/" + numericFieldName + "[7]").isPresent());
  }

}