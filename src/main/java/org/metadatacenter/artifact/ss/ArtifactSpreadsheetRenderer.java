package org.metadatacenter.artifact.ss;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
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

  public void render(TemplateSchemaArtifact templateSchemaArtifact, int headerStartColumnIndex, int headerRowNumber)
  {
    String sheetName = templateSchemaArtifact.getName();
    int columnIndex = headerStartColumnIndex;
    Sheet sheet = workbook.createSheet(sheetName);
    Row headerRow = sheet.createRow(headerRowNumber);

    for (String fieldName : templateSchemaArtifact.getFieldNames()) {
      FieldSchemaArtifact fieldSchemaArtifact = templateSchemaArtifact.getFieldSchemaArtifact(fieldName);

      render(fieldSchemaArtifact, sheet, columnIndex, headerRow);

      columnIndex += 1;
    }
  }

  public void render(ElementSchemaArtifact elementSchemaArtifact, Sheet sheet)
  {
    // TODO
  }

  public void render(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, Row headerRow)
  {
    String fieldName = fieldSchemaArtifact.getName();
    FieldInputType fieldInputType = fieldSchemaArtifact.getFieldUI().getInputType();
    CellStyle cellStyle = createCellStyle(fieldName, fieldSchemaArtifact.getFieldUI(), fieldSchemaArtifact.getValueConstraints());
    int rowIndex = headerRow.getRowNum() + 1;
    Cell columnNameCell = headerRow.createCell(columnIndex);

    columnNameCell.setCellValue(fieldSchemaArtifact.getName());

    sheet.setDefaultColumnStyle(columnIndex, cellStyle);
    sheet.autoSizeColumn(columnIndex);

    setFieldDataValidationConstraintIfRequired(fieldName, fieldInputType, fieldSchemaArtifact.getValueConstraints(),
      sheet, columnIndex, rowIndex);
  }

  private CellStyle createCellStyle(String fieldName, FieldUI fieldUI, ValueConstraints valueConstraints)
  {
    DataFormat dataFormat = workbook.createDataFormat();
    CellStyle cellStyle = workbook.createCellStyle();
    String formatString = getFormatString(fieldName, fieldUI, valueConstraints);
    cellStyle.setDataFormat(dataFormat.getFormat(formatString));

    return cellStyle;
  }

  private void setFieldDataValidationConstraintIfRequired(String fieldName, FieldInputType fieldInputType,
    ValueConstraints valueConstraints, Sheet sheet, int columnIndex, int firstRow)
  {
    DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
    Optional<DataValidationConstraint> constraint = createDataValidationConstraint(fieldName, fieldInputType, valueConstraints,
      dataValidationHelper);

    if (constraint.isPresent()) { // TODO Hard-coded 100
      CellRangeAddressList cellRange = new CellRangeAddressList(firstRow, 100, columnIndex, columnIndex);
      DataValidation dataValidation = dataValidationHelper.createValidation(constraint.get(), cellRange);

      dataValidation.createErrorBox("Validation Error", createDataValidationText(fieldName, fieldInputType, valueConstraints));
      dataValidation.setSuppressDropDownArrow(true);
      dataValidation.setShowErrorBox(true);
      sheet.addValidationData(dataValidation);
    }
  }

  private String createDataValidationText(String fieldName, FieldInputType fieldInputType, ValueConstraints valueConstraints) {
    int validationType = getValidationType(fieldName, fieldInputType, valueConstraints);

    // Only some fields have validation constraints that we can act on
    if (validationType ==  DataValidationConstraint.ValidationType.ANY)
      return "";

    if (fieldInputType == FieldInputType.TEXTFIELD || fieldInputType == FieldInputType.TEXTAREA) {

      if (valueConstraints.getMinLength().isPresent()) {
        Integer minLength = valueConstraints.getMinLength().get();
        if (valueConstraints.getMaxLength().isPresent()) { // Minimum length present, maximum length present
          Integer maxLength = valueConstraints.getMaxLength().get();
          return "Value should have a minimum of " +  minLength + " characters and a maximum of " + maxLength;
        } else { // Minimum length present, maximum length not present
          return "Value should have a minimum of " + minLength + " characters";
        }
      } else {
        if (valueConstraints.getMaxLength().isPresent()) { // Minimum length not present, maximum length present
          Integer maxLength = valueConstraints.getMaxLength().get();
          return "Value should have a maximum of " + maxLength + " characters";
        } else { // Minimum length not present, maximum length not present
          return "";
        }
      }
    } else if (fieldInputType == FieldInputType.NUMERIC) {

      if (valueConstraints.getMinValue().isPresent()) {
        Number minValue = valueConstraints.getMinValue().get();
        if (valueConstraints.getMaxValue().isPresent()) { // Minimum present, maximum present
          Number maxValue = valueConstraints.getMaxValue().get();
          return "Value should be between " +  minValue + " and " + maxValue;
        } else { // Minimum present, maximum not present
          return "Value should be greater than " +  minValue;
        }
      } else {
        if (valueConstraints.getMaxValue().isPresent()) { // Maximum present, minimum not present
          Number maxValue = valueConstraints.getMaxValue().get();
          return "Value should be less than " +  maxValue;
        } else { // Maximum not present, minimum not present
          return "";
        }
      }
    } else if (fieldInputType == FieldInputType.LIST) {
      return "Value not in specified list";
    } else if (fieldInputType == FieldInputType.RADIO) {
      return "Value not in specified list";
    } else if (fieldInputType == FieldInputType.CHECKBOX) {
      return "Value not in specified list";
    } else return "";
  }

  private Optional<DataValidationConstraint> createDataValidationConstraint(String fieldName,
    FieldInputType fieldInputType, ValueConstraints valueConstraints, DataValidationHelper dataValidationHelper)
  {
    int validationType = getValidationType(fieldName, fieldInputType, valueConstraints);

    // Only some fields have validation constraints that we can act on
    if (validationType ==  DataValidationConstraint.ValidationType.ANY)
      return Optional.empty();

    if (fieldInputType == FieldInputType.TEXTFIELD || fieldInputType == FieldInputType.TEXTAREA) {

      if (valueConstraints.getMinLength().isPresent()) {
        Integer minLength = valueConstraints.getMinLength().get();
        if (valueConstraints.getMaxLength().isPresent()) { // Minimum length present, maximum length present
          Integer maxLength = valueConstraints.getMaxLength().get();
          return Optional.of(
            dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.BETWEEN,
              minLength.toString(), maxLength.toString()));
        } else { // Minimum length present, maximum length not present
          return Optional.of(
            dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.GREATER_THAN,
              minLength.toString(), ""));
        }
      } else {
        if (valueConstraints.getMaxLength().isPresent()) { // Minimum length not present, maximum length present
          Integer maxLength = valueConstraints.getMaxLength().get();
          return Optional.of(dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.LESS_OR_EQUAL,
            maxLength.toString(), ""));
        } else { // Minimum length not present, maximum length not present
          return Optional.empty(); // TODO Handle lists of values
        }
      }
    } else if (fieldInputType == FieldInputType.NUMERIC) {

      if (valueConstraints.getMinValue().isPresent()) {
        Number minValue = valueConstraints.getMinValue().get();
        if (valueConstraints.getMaxValue().isPresent()) { // Minimum present, maximum present
          Number maxValue = valueConstraints.getMaxValue().get();
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
              minValue.toString(), maxValue.toString()));
        } else { // Minimum present, maximum not present
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.GREATER_THAN,
              minValue.toString(), ""));
        }
      } else {
        if (valueConstraints.getMaxValue().isPresent()) { // Maximum present, minimum not present
          Number maxValue = valueConstraints.getMaxValue().get();
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType,
            DataValidationConstraint.OperatorType.LESS_OR_EQUAL, maxValue.toString(), ""));
        } else { // Maximum not present, minimum not present
          return Optional.empty();
        }
      }
    } else if (fieldInputType == FieldInputType.LIST) {
      return Optional.empty(); // TODO list of values
    } else if (fieldInputType == FieldInputType.RADIO) {
      return Optional.empty(); // TODO list of values
    } else if (fieldInputType == FieldInputType.CHECKBOX) {
      return Optional.empty(); // TODO list of values
    } else return Optional.empty();
  }
  // Returns DataValidationConstraint.ValidationType
  private int getValidationType(String fieldName, FieldInputType fieldInputType, ValueConstraints valueConstraints)
  {
    if (fieldInputType == FieldInputType.NUMERIC) {
      if (valueConstraints.getNumberType().isPresent()) {
        NumberType numberType = valueConstraints.getNumberType().get();

        if (numberType == NumberType.DECIMAL) {
          return DataValidationConstraint.ValidationType.DECIMAL;
        } else if (numberType == NumberType.DOUBLE) {
          return DataValidationConstraint.ValidationType.DECIMAL;
        } else if (numberType == NumberType.FLOAT) {
          return DataValidationConstraint.ValidationType.DECIMAL;
        } else if (numberType == NumberType.LONG) {
          return DataValidationConstraint.ValidationType.INTEGER;
        } else if (numberType == NumberType.INTEGER) {
          return DataValidationConstraint.ValidationType.INTEGER;
        } else if (numberType == NumberType.INT) {
          return DataValidationConstraint.ValidationType.INTEGER;
        } else if (numberType == NumberType.SHORT) {
          return DataValidationConstraint.ValidationType.INTEGER;
        } else if (numberType == NumberType.BYTE) {
          return DataValidationConstraint.ValidationType.INTEGER;
        } else
          throw new RuntimeException("Invalid number type " + numberType + " for field " + fieldName);
      } else
        throw new RuntimeException("Missing number type for field " + fieldName);
    } else if (fieldInputType == FieldInputType.TEXTFIELD) {
      if (valueConstraints.getMinLength().isPresent() || valueConstraints.getMaxLength().isPresent())
        return DataValidationConstraint.ValidationType.TEXT_LENGTH;
      else
        return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.TEXTAREA) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.RADIO) {
      return DataValidationConstraint.ValidationType.LIST;
    } else if (fieldInputType == FieldInputType.CHECKBOX) {
      return DataValidationConstraint.ValidationType.LIST;
    } else if (fieldInputType == FieldInputType.TEMPORAL) {
      if (valueConstraints.getTemporalType().isPresent()) {
        TemporalType temporalType = valueConstraints.getTemporalType().get();

        if (temporalType == TemporalType.DATE || temporalType == TemporalType.DATETIME)
          return DataValidationConstraint.ValidationType.DATE;
        else if (temporalType == TemporalType.TIME)
          return DataValidationConstraint.ValidationType.TIME;
        else
          throw new RuntimeException("Invalid temporal type " + temporalType + " for field " + fieldName);
      } else throw new RuntimeException("Missing temporal type for field " + fieldName);


    } else if (fieldInputType == FieldInputType.EMAIL) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.LIST) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.PHONE_NUMBER) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.SECTION_BREAK) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.RICHTEXT) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.IMAGE) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.LINK) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.YOUTUBE) {
      return DataValidationConstraint.ValidationType.ANY;
    } else
      throw new RuntimeException("Invalid field input type " + fieldInputType + " for field " + fieldName);
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
      return getNumericFormatString(fieldName, valueConstraints.getNumberType(),
        valueConstraints.getDecimalPlaces(), valueConstraints.getUnitOfMeasure());
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
    Optional<Integer> decimalPlaces, Optional<String> unitOfMeasure)
  {
    String numericFormatString = "";

    if (numberType.isPresent()) {
      if (numberType.get() == NumberType.DECIMAL) {
        if (decimalPlaces.isPresent())
          numericFormatString += ""; // TODO
      } else if (numberType.get() == NumberType.DOUBLE) {
        if (decimalPlaces.isPresent())
          numericFormatString += ""; // TODO
      } else if (numberType.get() == NumberType.FLOAT) {
        if (decimalPlaces.isPresent())
          numericFormatString += ""; // TODO
      } else if (numberType.get() == NumberType.LONG) {
      } else if (numberType.get() == NumberType.INTEGER) {
      } else if (numberType.get() == NumberType.INT) {
      } else if (numberType.get() == NumberType.SHORT) {
      } else if (numberType.get() == NumberType.BYTE) {
      } else
        throw new RuntimeException("Invalid number type " + numberType + " for numeric field " + fieldName);
    } else
      throw new RuntimeException("Number type is not present for numeric field " + fieldName);

    if (unitOfMeasure.isPresent()) {
      // TODO
    }

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
            temporalFormatString += "yyyy";
          else if (temporalGranularity.get() == TemporalGranularity.MONTH)
            temporalFormatString += "yyyy/m";
          else if (temporalGranularity.get() == TemporalGranularity.DAY)
            temporalFormatString += "yyyy/m/d";
          else if (temporalGranularity.get() == TemporalGranularity.HOUR)
            temporalFormatString += "yyyy/m/d hh";
          else if (temporalGranularity.get() == TemporalGranularity.MINUTE)
            temporalFormatString += "yyyy/m/d hh:mm";
          else if (temporalGranularity.get() == TemporalGranularity.SECOND)
            temporalFormatString += "yyyy/m/d hh:mm:ss";
          else if (temporalGranularity.get() == TemporalGranularity.DECIMAL_SECOND)
            temporalFormatString += "yyyy/m/d hh:mm:ss.000";
          else
            throw new RuntimeException(
              "Unknown temporal granularity " + temporalGranularity.get() + " specified for temporal field " + fieldName);
        } else
          throw new RuntimeException("No granularity specified for temporal field " + fieldName);
      } else if (temporalType.get() == TemporalType.DATE) {
        if (temporalGranularity.get() == TemporalGranularity.YEAR)
          temporalFormatString += "yyyy";
        else if (temporalGranularity.get() == TemporalGranularity.MONTH)
          temporalFormatString += "yyyy/m";
        else if (temporalGranularity.get() == TemporalGranularity.DAY)
          temporalFormatString += "yyyy/m/d";
        else
          throw new RuntimeException(
            "Invalid temporal granularity " + temporalGranularity.get() + " specified for date temporal field " + fieldName);

      } else if (temporalType.get() == TemporalType.TIME) {
        if (temporalGranularity.get() == TemporalGranularity.HOUR)
          temporalFormatString += "hh";
        else if (temporalGranularity.get() == TemporalGranularity.MINUTE)
          temporalFormatString += "hh:mm";
        else if (temporalGranularity.get() == TemporalGranularity.SECOND)
          temporalFormatString += "hh:mm:ss";
        else if (temporalGranularity.get() == TemporalGranularity.DECIMAL_SECOND)
          temporalFormatString += "hh:mm:ss.000";
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
    }

    return temporalFormatString;
  }

}
