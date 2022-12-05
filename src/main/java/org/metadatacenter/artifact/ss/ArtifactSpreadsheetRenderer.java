package org.metadatacenter.artifact.ss;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.artifact.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifact.model.core.FieldInputType;
import org.metadatacenter.artifact.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifact.model.core.FieldUI;
import org.metadatacenter.artifact.model.core.InputTimeFormat;
import org.metadatacenter.artifact.model.core.NumberType;
import org.metadatacenter.artifact.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifact.model.core.TemporalGranularity;
import org.metadatacenter.artifact.model.core.TemporalType;
import org.metadatacenter.artifact.model.core.ValueConstraints;

import java.util.Optional;

public class ArtifactSpreadsheetRenderer
{
  private Workbook workbook;

  public ArtifactSpreadsheetRenderer(Workbook workbook)
  {
    this.workbook = workbook;
  }

  public void render(TemplateSchemaArtifact templateSchemaArtifact, Sheet sheet,
    int headerStartColumnIndex, int headerRowNumber)
  {
    int columnIndex = headerStartColumnIndex;

    for (String fieldName : templateSchemaArtifact.getFieldNames()) {
      FieldSchemaArtifact fieldSchemaArtifact = templateSchemaArtifact.getFieldSchemaArtifact(fieldName);

      Row headerRow = sheet.createRow(headerRowNumber);

      render(fieldSchemaArtifact, sheet, columnIndex, headerRow);

      columnIndex++;
    }

  }

  public void render(ElementSchemaArtifact elementSchemaArtifact, Sheet sheet)
  {

  }

  public void render(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, Row headerRow)
  {
    // TODO help text

    String fieldName = fieldSchemaArtifact.getName();
     Cell columnNameCell = headerRow.createCell(columnIndex);
     columnNameCell.setCellValue(fieldSchemaArtifact.getName());

     CellStyle cellStyle = createCellStyle(fieldName, fieldSchemaArtifact.getFieldUI(), fieldSchemaArtifact.getValueConstraints());
  }

  private CellStyle createCellStyle(String fieldName, FieldUI fieldUI, ValueConstraints valueConstraints)
  {
    DataFormat dataFormat = workbook.createDataFormat();
    CellStyle cellStyle = workbook.createCellStyle();
    String formatString = getFormatString(fieldName, fieldUI, valueConstraints);
    cellStyle.setDataFormat(dataFormat.getFormat(formatString));

    // worksheet.setDefaultColumnStyle(0, textStyle);
    return cellStyle;
  }

  private String getFormatString(String fieldName, FieldUI fieldUI, ValueConstraints valueConstraints)
  {
    if (fieldUI.getInputType() == FieldInputType.TEXTFIELD) {
      return "text"; // TODO Handle min and max length
    } else if (fieldUI.getInputType() == FieldInputType.TEXTAREA) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.RADIO) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.CHECKBOX) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.TEMPORAL) {
        return getTemporalFormatString(fieldName, valueConstraints.getTemporalType(),
          fieldUI.getTemporalGranularity(), fieldUI.getInputTimeFormat(), fieldUI.getTimeZoneEnabled());

    } else if (fieldUI.getInputType() == FieldInputType.EMAIL) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.LIST) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.NUMERIC) {
      return getNumericFormatString(fieldName, valueConstraints.getNumberType(), valueConstraints.getDecimalPlaces());
    } else if (fieldUI.getInputType() == FieldInputType.PHONE_NUMBER) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.SECTION_BREAK) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.RICHTEXT) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.IMAGE) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.LINK) {
      return "text";
    } else if (fieldUI.getInputType() == FieldInputType.YOUTUBE) {
      return "text";
    } else
      throw new RuntimeException("Unknown field type " + fieldUI.getInputType() + " for field " + fieldName);
  }

  private String getNumericFormatString(String fieldName, Optional<NumberType> numberType,
    Optional<Integer> decimalPlaces) {

    String numericFormatString = "";

    if (numberType.isPresent()) {
      if (numberType.get() == NumberType.DECIMAL) {
        if (decimalPlaces.isPresent()) numericFormatString += ""; // TODO
      } else if (numberType.get() == NumberType.DOUBLE) {
        if (decimalPlaces.isPresent()) numericFormatString += ""; // TODO
      } else if (numberType.get() == NumberType.FLOAT) {
        if (decimalPlaces.isPresent()) numericFormatString += ""; // TODO
      } else if (numberType.get() == NumberType.LONG) {
      } else if (numberType.get() == NumberType.INTEGER) {
      } else if (numberType.get() == NumberType.INT) {
      } else if (numberType.get() == NumberType.SHORT) {
      } else if (numberType.get() == NumberType.BYTE) {
      } else throw new RuntimeException("Invalid number type " + numberType + " for numeric field " + fieldName);

      } else throw new RuntimeException("Number type is not present for numeric field " + fieldName);

    return numericFormatString;
  }

  private String getTemporalFormatString(String fieldName, Optional<TemporalType> temporalType,
    Optional<TemporalGranularity> temporalGranularity, Optional<InputTimeFormat> inputTimeFormat,
    Optional<Boolean> timeZoneEnabled)
  {
    String temporalFormatString = "";

    if (temporalType.isPresent()) {
      if (temporalType.get() == TemporalType.DATETIME) {
        if (temporalGranularity.isPresent()) {
          if (temporalGranularity.get() == TemporalGranularity.YEAR)
            temporalFormatString += "yy";
          else if (temporalGranularity.get() == TemporalGranularity.MONTH)
            temporalFormatString += "yy/m";
          else if (temporalGranularity.get() == TemporalGranularity.DAY)
            temporalFormatString += "yy/m/d";
          else if (temporalGranularity.get() == TemporalGranularity.HOUR)
            temporalFormatString += "yy/m/d hh";
          else if (temporalGranularity.get() == TemporalGranularity.MINUTE)
            temporalFormatString += "yy/m/d hh:mm";
          else if (temporalGranularity.get() == TemporalGranularity.SECOND)
            temporalFormatString += "yy/m/d hh:mm:s";
          else if (temporalGranularity.get() == TemporalGranularity.DECIMAL_SECOND)
            temporalFormatString += "yy/m/d hh:mm:s"; // TODO
          else
            throw new RuntimeException(
              "Unknown temporal granularity " + temporalGranularity.get() + " specified for temporal field " + fieldName);
        } else
          throw new RuntimeException("No granularity specified for temporal field " + fieldName);
      } else if (temporalType.get() == TemporalType.DATE) {
        if (temporalGranularity.get() == TemporalGranularity.DAY)
          temporalFormatString += "d";
        else if (temporalGranularity.get() == TemporalGranularity.HOUR)
          temporalFormatString += "d hh";
        else if (temporalGranularity.get() == TemporalGranularity.MINUTE)
          temporalFormatString += "d hh:mm";
        else if (temporalGranularity.get() == TemporalGranularity.SECOND)
          temporalFormatString += "d hh:mm:s";
        else if (temporalGranularity.get() == TemporalGranularity.DECIMAL_SECOND)
          temporalFormatString += "d hh:mm:s"; // TODO
        else
          throw new RuntimeException(
            "Invalid temporal granularity " + temporalGranularity.get() + " specified for date temporal field " + fieldName);

      } else if (temporalType.get() == TemporalType.TIME) {
        if (temporalGranularity.get() == TemporalGranularity.HOUR)
          temporalFormatString += "hh";
        else if (temporalGranularity.get() == TemporalGranularity.MINUTE)
          temporalFormatString += "hh:mm";
        else if (temporalGranularity.get() == TemporalGranularity.SECOND)
          temporalFormatString += "hh:mm:s";
        else if (temporalGranularity.get() == TemporalGranularity.DECIMAL_SECOND)
          temporalFormatString += "hh:mm:s"; // TODO
        else
          throw new RuntimeException(
            "Invalid temporal granularity " + temporalGranularity.get() + " specified for time temporal field " + fieldName);
      } else
        throw new RuntimeException(
          "Unknown temporal type " + temporalType.get() + " specified for temporal field " + fieldName);

    } else
      throw new RuntimeException("No temporal type specified for temporal field " + fieldName);

    if (inputTimeFormat.isPresent()) {
      if (inputTimeFormat.get() == InputTimeFormat.TWELVE_HOUR) {
        temporalFormatString += "AM/PM";
      } else if (inputTimeFormat.get() == InputTimeFormat.TWENTY_FOUR_HOUR) {
      } else
        throw new RuntimeException("Unknown time format " + inputTimeFormat + " specified for temporal field " + fieldName);
    } else
      throw new RuntimeException("No input time format specified for field " + fieldName);

    return temporalFormatString;
  }

}
