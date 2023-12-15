package org.metadatacenter.artifacts.model.visitors;

import org.junit.Test;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;

public class TemplateValueConstraintReporterTest
{
  @Test
  public void basicTemplateValueConstraintReporterTest()
  {
    String textFieldName = "text field";
    String defaultValue = "a default value";
    String regex = "*";
    String numericFieldName = "numeric field";
    XsdNumericDatatype numericType = XsdNumericDatatype.DOUBLE;
    Number minValue = 0.0;
    Number maxValue = 100.0;
    String temporalFieldName = "";
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.TIME;
    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timeZoneEnabled = false;
    String elementName = "element";
    String templateName = "template";

    FieldSchemaArtifact textFieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder()
      .withName(textFieldName)
      .withDefaultValue(defaultValue)
      .withRegex(regex)
      .build();

    FieldSchemaArtifact numericFieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder()
      .withName(numericFieldName)
      .withNumericType(numericType)
      .withMinValue(minValue)
      .withMaxValue(maxValue)
      .build();

    FieldSchemaArtifact temporalFieldSchemaArtifact = FieldSchemaArtifact.temporalFieldBuilder()
      .withName(temporalFieldName)
      .withTemporalType(temporalType)
      .withTemporalGranularity(temporalGranularity)
      .withInputTimeFormat(inputTimeFormat)
      .withTimeZoneEnabled(timeZoneEnabled)
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

    TemplateValueConstraintReporter templateValueConstraintReporter = new TemplateValueConstraintReporter(templateSchemaArtifact);

    System.err.println();
  }
}