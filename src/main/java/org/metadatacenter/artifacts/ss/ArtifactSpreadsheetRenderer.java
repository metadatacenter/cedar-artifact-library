package org.metadatacenter.artifacts.ss;

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
import org.metadatacenter.artifacts.model.core.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUI;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.NumberType;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.TemporalType;
import org.metadatacenter.artifacts.model.core.ValueConstraints;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    throw new RuntimeException("element rendering not implemented");
  }

  public void render(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, Row headerRow)
  {
    String fieldName = fieldSchemaArtifact.getName();
    FieldInputType fieldInputType = fieldSchemaArtifact.getFieldUI().getInputType();
    CellStyle cellStyle = createCellStyle(fieldSchemaArtifact);
    int rowIndex = headerRow.getRowNum() + 1;
    Cell columnNameCell = headerRow.createCell(columnIndex);

//    if (fieldSchemaArtifact.getSkosPrefLabel().isPresent())
//      columnNameCell.setCellValue(fieldSchemaArtifact.getSkosPrefLabel().get());
//    else
      columnNameCell.setCellValue(fieldSchemaArtifact.getName());

    sheet.setDefaultColumnStyle(columnIndex, cellStyle);
    sheet.autoSizeColumn(columnIndex);

    setColumnDataValidationConstraintIfRequired(fieldSchemaArtifact, sheet, columnIndex, rowIndex);

    if (fieldSchemaArtifact.isHidden())
      sheet.setColumnHidden(columnIndex, true);
  }

  private void setColumnDataValidationConstraintIfRequired(FieldSchemaArtifact fieldSchemaArtifact,
    Sheet sheet, int columnIndex, int firstRow)
  {
    DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
    Optional<DataValidationConstraint> constraint = createDataValidationConstraint(fieldSchemaArtifact,
      dataValidationHelper);

    if (constraint.isPresent()) {
      // TODO Check lastRow
      CellRangeAddressList cellRange = new CellRangeAddressList(firstRow, 1000, columnIndex, columnIndex);
      DataValidation dataValidation = dataValidationHelper.createValidation(constraint.get(), cellRange);

      dataValidation.createErrorBox("Validation Error", createDataValidationMessage(fieldSchemaArtifact));
      dataValidation.setSuppressDropDownArrow(true);
      dataValidation.setShowErrorBox(true);
      sheet.addValidationData(dataValidation);
    }
  }

  private String createDataValidationMessage(FieldSchemaArtifact fieldSchemaArtifact)
  {
    String fieldName = fieldSchemaArtifact.getName();
    FieldUI fieldUI = fieldSchemaArtifact.getFieldUI();
    FieldInputType fieldInputType = fieldUI.getInputType();
    ValueConstraints valueConstraints = fieldSchemaArtifact.getValueConstraints();

    // Only some fields have validation constraints that we can create messages for
    if (fieldInputType == FieldInputType.TEXTFIELD || fieldInputType == FieldInputType.TEXTAREA) {
      if (valueConstraints.getMinLength().isPresent()) {
        Integer minLength = valueConstraints.getMinLength().get();
        if (valueConstraints.getMaxLength().isPresent()) { // Minimum length present, maximum length present
          Integer maxLength = valueConstraints.getMaxLength().get();
          return "Value should have a minimum of " + minLength + " characters and a maximum of " + maxLength;
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
          return "Value should be between " + minValue + " and " + maxValue;
        } else { // Minimum present, maximum not present
          return "Value should be greater than " + minValue;
        }
      } else {
        if (valueConstraints.getMaxValue().isPresent()) { // Maximum present, minimum not present
          Number maxValue = valueConstraints.getMaxValue().get();
          return "Value should be less than " + maxValue;
        } else { // Maximum not present, minimum not present
          return "";
        }
      }
    } else if (fieldInputType == FieldInputType.TEMPORAL) {
      String temporalFormatString = getTemporalFormatString(fieldName, valueConstraints.getTemporalType(),
        fieldUI.getTemporalGranularity(), fieldUI.getInputTimeFormat(), fieldUI.getTimeZoneEnabled());
      return temporalFormatString;
    } else
      return "";
  }

  private Optional<DataValidationConstraint> createDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    String fieldName = fieldSchemaArtifact.getName();

    int validationType = getValidationType(fieldSchemaArtifact);

    // Only some fields have validation constraints that we can act on
    if (validationType ==  DataValidationConstraint.ValidationType.ANY)
      return Optional.empty();
    else if (validationType == DataValidationConstraint.ValidationType.TEXT_LENGTH)
      return createTextLengthDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.DECIMAL)
      return createDecimalDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.INTEGER)
      return createIntegerDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.DATE)
      return createDateDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.TIME)
      return createTimeDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.FORMULA) {
      return createFormulaDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
      // TODO Need to store values in hidden sheet
      //return Optional.of(dataValidationHelper.createExplicitListConstraint(literals.toArray(new String[0])));
    } else throw new RuntimeException("Do no know how to handle data validation type " + validationType +
      " for field " + fieldName);
  }

  private static Optional<DataValidationConstraint> createDecimalDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    ValueConstraints valueConstraints = fieldSchemaArtifact.getValueConstraints();
    int validationType = DataValidationConstraint.ValidationType.DECIMAL;

    if (valueConstraints.getMinValue().isPresent()) {
      Number minValue = valueConstraints.getMinValue().get();
      if (valueConstraints.getMaxValue().isPresent()) { // Minimum present, maximum present
        Number maxValue = valueConstraints.getMaxValue().get();
        return Optional.of(
          dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
            minValue.toString(), maxValue.toString()));
      } else { // Minimum present, maximum not present
        return Optional.of(dataValidationHelper.createNumericConstraint(validationType,
          DataValidationConstraint.OperatorType.GREATER_THAN, minValue.toString(), ""));
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
  }

  private static Optional<DataValidationConstraint> createIntegerDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    FieldUI fieldUI = fieldSchemaArtifact.getFieldUI();
    ValueConstraints valueConstraints = fieldSchemaArtifact.getValueConstraints();
    int validationType = DataValidationConstraint.ValidationType.INTEGER;

    if (valueConstraints.getMinValue().isPresent()) {
      Number minValue = valueConstraints.getMinValue().get();
      if (valueConstraints.getMaxValue().isPresent()) { // Minimum present, maximum present
        Number maxValue = valueConstraints.getMaxValue().get();
        return Optional.of(
          dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
            minValue.toString(), maxValue.toString()));
      } else { // Minimum present, maximum not present
        return Optional.of(dataValidationHelper.createNumericConstraint(validationType,
          DataValidationConstraint.OperatorType.GREATER_THAN, minValue.toString(), ""));
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
  }

  private static Optional<DataValidationConstraint> createTextLengthDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    ValueConstraints valueConstraints = fieldSchemaArtifact.getValueConstraints();

    if (valueConstraints.getMinLength().isPresent()) {
      Integer minLength = valueConstraints.getMinLength().get();
      if (valueConstraints.getMaxLength().isPresent()) { // Minimum length present, maximum length present
        Integer maxLength = valueConstraints.getMaxLength().get();
        return Optional.of(
          dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.BETWEEN, minLength.toString(), maxLength.toString()));
      } else { // Minimum length present, maximum length not present
        return Optional.of(
          dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.GREATER_THAN, minLength.toString(), ""));
      }
    } else {
      if (valueConstraints.getMaxLength().isPresent()) { // Minimum length not present, maximum length present
        Integer maxLength = valueConstraints.getMaxLength().get();
        return Optional.of(
          dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.LESS_OR_EQUAL, maxLength.toString(), ""));
      } else { // Minimum length not present, maximum length not present
        return Optional.empty();
      }
    }
  }

  private static Optional<DataValidationConstraint> createDateDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    if (fieldSchemaArtifact.getFieldUI().isTemporal())
      return Optional.of(
        dataValidationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "Date(1, 1, 1)",
          "Date(9999,12,31)", "dd/mm/yyyy"));
    else
      return Optional.empty();
  }

  private static Optional<DataValidationConstraint> createTimeDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    if (fieldSchemaArtifact.getFieldUI().isTemporal())
      return Optional.of(
        dataValidationHelper.createTimeConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=TIME(0,0,0)",
          "=TIME(23,59,59)"));
    else
      return Optional.empty();
  }

  private static Optional<DataValidationConstraint> createFormulaDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    String fieldName = fieldSchemaArtifact.getName();
    ValueConstraints valueConstraints = fieldSchemaArtifact.getValueConstraints();

    if (valueConstraints.hasValueBasedConstraints()) {
      List<String> labels = valueConstraints.getClasses().stream().map(ClassValueConstraint::getLabel).collect(Collectors.toList()); // TODO Need to store values in hidden sheet
      // See: https://stackoverflow.com/questions/27630507/is-there-a-max-number-items-while-generating-drop-down-list-in-excel-using-apach/27639609#27639609
      //return Optional.of(dataValidationHelper.createExplicitListConstraint(labels.toArray(new String[0])));
      return Optional.empty();
    } else
      throw new RuntimeException("No value-based constraints for field " + fieldName);
  }

  // Returns DataValidationConstraint.ValidationType (ANY, FORMULA, LIST, DATE, TIME, DECIMAL, INTEGER, TEXT_LENGTH)
  private int getValidationType(FieldSchemaArtifact fieldSchemaArtifact)
  {
    String fieldName = fieldSchemaArtifact.getName();
    FieldUI fieldUI = fieldSchemaArtifact.getFieldUI();
    FieldInputType fieldInputType = fieldUI.getInputType();
    ValueConstraints valueConstraints = fieldSchemaArtifact.getValueConstraints();

    if (fieldInputType == FieldInputType.PHONE_NUMBER || fieldInputType == FieldInputType.SECTION_BREAK
      || fieldInputType == FieldInputType.RICHTEXT || fieldInputType == FieldInputType.EMAIL
      || fieldInputType == FieldInputType.IMAGE || fieldInputType == FieldInputType.LINK
      || fieldInputType == FieldInputType.YOUTUBE) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.LIST || fieldInputType == FieldInputType.RADIO ||
      fieldInputType == FieldInputType.CHECKBOX) {
      return DataValidationConstraint.ValidationType.FORMULA;
    } else if (fieldInputType == FieldInputType.TEXTFIELD || fieldInputType == FieldInputType.TEXTAREA) {
      if (valueConstraints.hasValueBasedConstraints())
        return DataValidationConstraint.ValidationType.FORMULA;
      else if (valueConstraints.getMinLength().isPresent() || valueConstraints.getMaxLength().isPresent())
        return DataValidationConstraint.ValidationType.TEXT_LENGTH;
      else
        return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.NUMERIC) {
      if (valueConstraints.getNumberType().isPresent()) {
        NumberType numberType = valueConstraints.getNumberType().get();

        if (numberType == NumberType.DECIMAL || numberType == NumberType.DOUBLE || numberType == NumberType.FLOAT) {
          return DataValidationConstraint.ValidationType.DECIMAL;
        } else if (numberType == NumberType.LONG || numberType == NumberType.INTEGER || numberType == NumberType.INT
          || numberType == NumberType.SHORT || numberType == NumberType.BYTE) {
          return DataValidationConstraint.ValidationType.INTEGER;
        } else
          throw new RuntimeException("Invalid number type " + numberType + " for numeric field " + fieldName);
      } else
        throw new RuntimeException("Missing number type for numeric field " + fieldName);
    } else if (fieldInputType == FieldInputType.TEMPORAL) {
      if (valueConstraints.getTemporalType().isPresent()) {
        TemporalType temporalType = valueConstraints.getTemporalType().get();

        if (temporalType == TemporalType.DATE || temporalType == TemporalType.DATETIME)
          return DataValidationConstraint.ValidationType.DATE;
        else if (temporalType == TemporalType.TIME)
          return DataValidationConstraint.ValidationType.TIME;
        else
          throw new RuntimeException("Invalid temporal type " + temporalType + " for temporal field " + fieldName);
      } else
        throw new RuntimeException("Missing temporal type for temporal field " + fieldName);
    } else
      throw new RuntimeException("Invalid field input type " + fieldInputType + " for field " + fieldName);
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

    if (inputTimeFormat.isPresent() && inputTimeFormat.get() == InputTimeFormat.TWELVE_HOUR)
      temporalFormatString += " AM/PM";

    return temporalFormatString;
  }

  private CellStyle createCellStyle(FieldSchemaArtifact fieldSchemaArtifact)
  {
    String fieldName = fieldSchemaArtifact.getName();
    FieldUI fieldUI = fieldSchemaArtifact.getFieldUI();
    ValueConstraints valueConstraints = fieldSchemaArtifact.getValueConstraints();
    DataFormat dataFormat = workbook.createDataFormat();
    CellStyle cellStyle = workbook.createCellStyle();

    if (fieldUI.isNumeric()) {
      String formatString = getNumericFormatString(fieldName, valueConstraints.getNumberType(),
        valueConstraints.getDecimalPlaces(), valueConstraints.getUnitOfMeasure());
      cellStyle.setDataFormat(dataFormat.getFormat(formatString));
    } else if (fieldUI.isTemporal()) {
      String formatString = getTemporalFormatString(fieldName, valueConstraints.getTemporalType(),
        fieldUI.getTemporalGranularity(), fieldUI.getInputTimeFormat(), fieldUI.getTimeZoneEnabled());
      cellStyle.setDataFormat(dataFormat.getFormat(formatString));
    }

    return cellStyle;
  }
}
