package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.WorkbookUtil;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TemporalDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;
import org.metadatacenter.artifacts.ss.SpreadsheetFactory;
import org.metadatacenter.artifacts.util.ConnectionUtil;
import org.metadatacenter.artifacts.util.TerminologyServerClient;
import org.metadatacenter.model.ModelNodeNames;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.ss.SpreadSheetUtil.setCellComment;

public class ExcelArtifactRenderer
{
  private final TerminologyServerClient terminologyServerClient;
  private final Workbook workbook;
  private final ObjectMapper mapper;
  private final ObjectWriter objectWriter;

  private final String metadataSheetName = ".metadata";

  private final Short LIGHT_CORNFLOUR_BLUE = IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex();

  private final CellStyle headerCellstyle;

  public static final String xsdDateTimeFormatterString = "uuuu-MM-dd'T'HH:mm:ssZZZZZ";
  public static final DateTimeFormatter xsdDateTimeFormatter =
    DateTimeFormatter.ofPattern(xsdDateTimeFormatterString).withZone(ZoneId.systemDefault());

  public ExcelArtifactRenderer(TerminologyServerClient terminologyServerClient)
  {
    this.terminologyServerClient = terminologyServerClient;

    this.workbook = SpreadsheetFactory.createEmptyWorkbook();

    this.mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    this.objectWriter = mapper.writer().withDefaultPrettyPrinter();

    this.headerCellstyle = createHeaderCellStyle(workbook);
  }

  public Workbook render(TemplateSchemaArtifact templateSchemaArtifact, int headerStartColumnIndex, int headerRowNumber)
  {
    String proposedSheetName = templateSchemaArtifact.name();
    int columnIndex = headerStartColumnIndex;

    Sheet sheet = createSheet(workbook, proposedSheetName);
    Row headerRow = sheet.createRow(headerRowNumber);
    Row firstDataRow = sheet.createRow(headerRowNumber + 1);

    for (String fieldName : templateSchemaArtifact.getFieldNames()) {
      FieldSchemaArtifact fieldSchemaArtifact = templateSchemaArtifact.getFieldSchemaArtifact(fieldName);

      render(fieldSchemaArtifact, sheet, columnIndex, headerRow, firstDataRow);

      columnIndex += 1;
    }

    addMetadataSheet(templateSchemaArtifact);

    return this.workbook;
  }

  public Workbook render(ElementSchemaArtifact elementSchemaArtifact, Sheet sheet)
  {
    throw new RuntimeException("element rendering not implemented");
  }

  private Workbook render(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, Row headerRow, Row firstDataRow)
  {
    String fieldName = fieldSchemaArtifact.name();
    String fieldDescription = fieldSchemaArtifact.description();
    CellStyle cellStyle = createCellStyle(fieldSchemaArtifact);
    int rowIndex = headerRow.getRowNum() + 1;
    Cell columnNameHeaderCell = headerRow.createCell(columnIndex);
    boolean isRequiredValue =
      fieldSchemaArtifact.valueConstraints().isPresent() && fieldSchemaArtifact.valueConstraints().get()
        .requiredValue();
    Optional<? extends DefaultValue> defaultValue = fieldSchemaArtifact.valueConstraints().isPresent() ?
      fieldSchemaArtifact.valueConstraints().get().defaultValue() : Optional.empty();

    //    if (fieldSchemaArtifact.getPreferredLabel().isPresent())
    //      columnNameCell.setCellValue(fieldSchemaArtifact.getPreferredLabel().get());
    //    else
    columnNameHeaderCell.setCellValue(fieldName);
    columnNameHeaderCell.setCellStyle(headerCellstyle);

    if (isRequiredValue)
      fieldDescription = "(Required) " + fieldDescription;

    setCellComment(columnNameHeaderCell, fieldDescription);

    sheet.setDefaultColumnStyle(columnIndex, cellStyle);
    sheet.autoSizeColumn(columnIndex);

    setColumnDataValidationConstraintIfRequired(fieldSchemaArtifact, sheet, columnIndex, rowIndex);

    if (fieldSchemaArtifact.hidden() || fieldSchemaArtifact.isStatic())
      sheet.setColumnHidden(columnIndex, true);

    if (defaultValue.isPresent()) {
      DefaultValue value = defaultValue.get(); // TODO Use typesafe switch when available
      Cell dataCell = firstDataRow.createCell(columnIndex);

      if (value.isTextDefaultValue()) {
        TextDefaultValue textDefaultValue = value.asTextDefaultValue();
        dataCell.setCellValue(textDefaultValue.value());
      } else if (value.isNumericDefaultValue()) {
        NumericDefaultValue numericDefaultValue = value.asNumericDefaultValue();
        Number n = numericDefaultValue.value();

        dataCell.setCellValue(n.doubleValue());
      } else if (value.isControlledTermDefaultValue()) {
        ControlledTermDefaultValue controlledTermDefaultValue = value.asControlledTermDefaultValue();
        String label = controlledTermDefaultValue.value().getRight();

        dataCell.setCellValue(label);
      } else if (value.isTemporalDefaultValue()) {
        TemporalDefaultValue temporalDefaultValue = value.asTemporalDefaultValue();
        dataCell.setCellValue(temporalDefaultValue.value());
      } else
        throw new RuntimeException("Unknown default value type " + value.getValueType() + " for field " + fieldName);
    }
    return this.workbook;
  }

  private void setColumnDataValidationConstraintIfRequired(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, int firstRow)
  {
    DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
    Optional<DataValidationConstraint> constraint = createDataValidationConstraint(fieldSchemaArtifact,
      dataValidationHelper);

    if (constraint.isPresent()) {
      // TODO Replace arbitrary 1000 with final row in spreadsheet
      CellRangeAddressList cellRange = new CellRangeAddressList(firstRow, 1000, columnIndex, columnIndex);
      DataValidation dataValidation = dataValidationHelper.createValidation(constraint.get(), cellRange);

     dataValidation.createErrorBox("Validation Error", createDataValidationMessage(fieldSchemaArtifact));
 //    dataValidation.setSuppressDropDownArrow(true);
       dataValidation.setShowErrorBox(true);
      sheet.addValidationData(dataValidation);
    }
  }

  private String createDataValidationMessage(FieldSchemaArtifact fieldSchemaArtifact)
  {
    String fieldName = fieldSchemaArtifact.name();
    FieldUi fieldUi = fieldSchemaArtifact.fieldUi();
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.valueConstraints();

    // Only some fields have validation constraints that we can create messages for
    if (valueConstraints.isPresent()) {
      if (valueConstraints.get() instanceof TextValueConstraints) { // TODO Use typesafe switch when available
        TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints.get();

        if (textValueConstraints.minLength().isPresent()) {
          Integer minLength = textValueConstraints.minLength().get();
          if (textValueConstraints.maxLength().isPresent()) { // Minimum length present, maximum length present
            Integer maxLength = textValueConstraints.maxLength().get();
            return "Value should have a minimum of " + minLength + " characters and a maximum of " + maxLength;
          } else { // Minimum length present, maximum length not present
            return "Value should have a minimum of " + minLength + " characters";
          }
        } else {
          if (textValueConstraints.maxLength().isPresent()) { // Minimum length not present, maximum length present
            Integer maxLength = textValueConstraints.maxLength().get();
            return "Value should have a maximum of " + maxLength + " characters";
          } else { // Minimum length not present, maximum length not present
            return "";
          }
        }
      } else if (valueConstraints.get() instanceof NumericValueConstraints) {
        NumericValueConstraints numericValueConstraints = (NumericValueConstraints)valueConstraints.get();
        if (numericValueConstraints.minValue().isPresent()) {
          Number minValue = numericValueConstraints.minValue().get();
          if (numericValueConstraints.maxValue().isPresent()) { // Minimum present, maximum present
            Number maxValue = numericValueConstraints.maxValue().get();
            return "Value should be between " + minValue + " and " + maxValue;
          } else { // Minimum present, maximum not present
            return "Value should be greater than " + minValue;
          }
        } else {
          if (numericValueConstraints.maxValue().isPresent()) { // Maximum present, minimum not present
            Number maxValue = numericValueConstraints.maxValue().get();
            return "Value should be less than " + maxValue;
          } else { // Maximum not present, minimum not present
            return "Value should be a number";
          }
        }
      } else if (valueConstraints.get() instanceof TemporalValueConstraints) {
        TemporalValueConstraints temporalValueConstraints = (TemporalValueConstraints)valueConstraints.get();
        TemporalFieldUi temporalFieldUi = fieldUi.asTemporalFieldUi(); // TODO Temporary until we use typed switch
        XsdTemporalDatatype temporalType = temporalValueConstraints.temporalType();
        return getTemporalFormatString(fieldName, temporalType, temporalFieldUi.temporalGranularity(),
          temporalFieldUi.inputTimeFormat(), temporalFieldUi.timezoneEnabled());

      } else
        return "";
    } else
      return "";
  }

  private Optional<DataValidationConstraint> createDataValidationConstraint(
    FieldSchemaArtifact fieldSchemaArtifact, DataValidationHelper dataValidationHelper)
  {
    String fieldName = fieldSchemaArtifact.name();


    int validationType = getValidationType(fieldSchemaArtifact);

    // Only some fields have validation constraints that we can act on
    if (validationType == DataValidationConstraint.ValidationType.ANY)
      return Optional.empty();
    else if (validationType == DataValidationConstraint.ValidationType.TEXT_LENGTH)
      return createTextLengthDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.INTEGER)
      return createIntegerDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.DECIMAL)
      return createDecimalDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.DATE)
      return createDateDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.TIME)
      return createTimeDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    else if (validationType == DataValidationConstraint.ValidationType.FORMULA) {
      return createFormulaDataValidationConstraint(fieldSchemaArtifact, dataValidationHelper);
    } else
      throw new RuntimeException("Do no know how to handle data validation type " + validationType + " for field " + fieldName);
  }

  private Optional<DataValidationConstraint> createDecimalDataValidationConstraint(
    FieldSchemaArtifact fieldSchemaArtifact, DataValidationHelper dataValidationHelper)
  {
    int validationType = DataValidationConstraint.ValidationType.DECIMAL;

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      NumericValueConstraints numericValueConstraints = (NumericValueConstraints)fieldSchemaArtifact.valueConstraints().get();
      if (numericValueConstraints.minValue().isPresent()) {
        Number minValue = numericValueConstraints.minValue().get();
        if (numericValueConstraints.maxValue().isPresent()) { // Minimum present, maximum present
          Number maxValue = numericValueConstraints.maxValue().get();
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
              minValue.toString(), maxValue.toString()));
        } else { // Minimum present, maximum not present
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, minValue.toString(), ""));
        }
      } else {
        if (numericValueConstraints.maxValue().isPresent()) { // Maximum present, minimum not present
          Number maxValue = numericValueConstraints.maxValue().get();
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.LESS_OR_EQUAL, maxValue.toString(), ""));
        } else { // Maximum not present, minimum not present
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
              String.valueOf(-Float.MAX_VALUE), String.valueOf(Float.MAX_VALUE)));
        }
      }
    } else
      return Optional.of(
        dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
          String.valueOf(-Float.MAX_VALUE), String.valueOf(Float.MAX_VALUE)));
  }

  private Optional<DataValidationConstraint> createIntegerDataValidationConstraint(
    FieldSchemaArtifact fieldSchemaArtifact, DataValidationHelper dataValidationHelper)
  {
    int validationType = DataValidationConstraint.ValidationType.INTEGER;

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      NumericValueConstraints numericValueConstraints = (NumericValueConstraints)fieldSchemaArtifact.valueConstraints().get();
      if (numericValueConstraints.minValue().isPresent()) {
        Number minValue = numericValueConstraints.minValue().get();
        if (numericValueConstraints.maxValue().isPresent()) { // Minimum present, maximum present
          Number maxValue = numericValueConstraints.maxValue().get();
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
              minValue.toString(), maxValue.toString()));
        } else { // Minimum present, maximum not present
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType,
            DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, minValue.toString(), ""));
        }
      } else {
        if (numericValueConstraints.maxValue().isPresent()) { // Maximum present, minimum not present
          Number maxValue = numericValueConstraints.maxValue().get();
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType,
            DataValidationConstraint.OperatorType.LESS_OR_EQUAL, maxValue.toString(), ""));
        } else { // Maximum not present, minimum not present
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
              String.valueOf(Integer.MIN_VALUE), String.valueOf(Integer.MAX_VALUE)));
        }
      }
    } else
      return Optional.of(
        dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
          String.valueOf(Integer.MIN_VALUE), String.valueOf(Integer.MAX_VALUE)));
  }

  private Optional<DataValidationConstraint> createTextLengthDataValidationConstraint(
    FieldSchemaArtifact fieldSchemaArtifact, DataValidationHelper dataValidationHelper)
  {
    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      TextValueConstraints textValueConstraints = (TextValueConstraints)fieldSchemaArtifact.valueConstraints().get();
      if (textValueConstraints.minLength().isPresent()) {
        Integer minLength = textValueConstraints.minLength().get();
        if (textValueConstraints.maxLength().isPresent()) { // Minimum length present, maximum length present
          Integer maxLength = textValueConstraints.maxLength().get();
          return Optional.of(
            dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.BETWEEN, minLength.toString(), maxLength.toString()));
        } else { // Minimum length present, maximum length not present
          return Optional.of(dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.GREATER_THAN,
            minLength.toString(), ""));
        }
      } else {
        if (textValueConstraints.maxLength().isPresent()) { // Minimum length not present, maximum length present
          Integer maxLength = textValueConstraints.maxLength().get();
          return Optional.of(dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.LESS_OR_EQUAL,
            maxLength.toString(), ""));
        } else { // Minimum length not present, maximum length not present
          return Optional.empty();
        }
      }
    } else
      return Optional.empty();
  }

  private Optional<DataValidationConstraint> createDateDataValidationConstraint(
    FieldSchemaArtifact fieldSchemaArtifact, DataValidationHelper dataValidationHelper)
  {
    if (fieldSchemaArtifact.fieldUi().isTemporal())
      return Optional.of(
        dataValidationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "Date(1, 1, 1)",
          "Date(9999,12,31)", "dd/mm/yyyy"));
    else
      return Optional.empty();
  }

  private Optional<DataValidationConstraint> createTimeDataValidationConstraint(
    FieldSchemaArtifact fieldSchemaArtifact, DataValidationHelper dataValidationHelper)
  {
    if (fieldSchemaArtifact.fieldUi().isTemporal())
      return Optional.of(
        dataValidationHelper.createTimeConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=TIME(0,0,0)",
          "=TIME(23,59,59)"));
    else
      return Optional.empty();
  }

  // See: https://stackoverflow.com/questions/27630507/is-there-a-max-number-items-while-generating-drop-down-list-in-excel-using-apach/27639609#27639609

  private Optional<DataValidationConstraint> createFormulaDataValidationConstraint(
    FieldSchemaArtifact fieldSchemaArtifact, DataValidationHelper dataValidationHelper)
  {
    Map<String, String> values = getPossibleValues(fieldSchemaArtifact.valueConstraints());

    if (!values.isEmpty()) {
      String proposedSheetName = fieldSchemaArtifact.name();
      Sheet valueSheet = createSheet(workbook, proposedSheetName);
      String sheetName = valueSheet.getSheetName();
      int numberOfValues = values.keySet().size();
      String formula = "'" + sheetName + "'!$A$1:$A$" + numberOfValues;

      int rowNumber = 0;
      for (String preferredLabel : values.keySet()) {
        String iri = values.get(preferredLabel);
        Row row = valueSheet.createRow(rowNumber);
        Cell valueCell = row.createCell(0);
        valueCell.setCellValue(preferredLabel);

        // Even though we don't use them we put the IRI in the second column
        if (iri != null && !iri.isEmpty()) {
          Cell iriCell = row.createCell(1);
          iriCell.setCellValue(iri);
        }
        rowNumber++;
      }
//      workbook.setSheetHidden(valueSheetIndex, true);

      DataValidationConstraint dataValidationConstraint = dataValidationHelper.createFormulaListConstraint(formula);
      return Optional.of(dataValidationConstraint);
    } else
      return Optional.empty();
  }

  private LinkedHashMap<String, String> getPossibleValues(Optional<ValueConstraints> valueConstraints)
  {
    if (valueConstraints.isPresent()) {
      LinkedHashMap<String, String> possibleValues = new LinkedHashMap<>();

      if (valueConstraints.get() instanceof TextValueConstraints) {
        TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints.get(); // TODO Use typesafe switch

        List<String> labels = textValueConstraints.literals().stream().map(LiteralValueConstraint::label).toList();

        for (String label : labels)
          possibleValues.put(label, "");
      }

      if (valueConstraints.get() instanceof ControlledTermValueConstraints) {
        ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints.get();

        if (controlledTermValueConstraints.hasExplicitConstraints()) {
          Map<String, String> ontologyBasedValues = terminologyServerClient.getValuesFromTerminologyServer(controlledTermValueConstraints);
          possibleValues.putAll(ontologyBasedValues);
        }
      }

      return possibleValues;
    } else
      return new LinkedHashMap<>();
  }


  // Returns DataValidationConstraint.ValidationType (ANY, FORMULA, LIST, DATE, TIME, DECIMAL, INTEGER, TEXT_LENGTH)
  private int getValidationType(FieldSchemaArtifact fieldSchemaArtifact)
  {
    String fieldName = fieldSchemaArtifact.name();
    FieldUi fieldUi = fieldSchemaArtifact.fieldUi();
    FieldInputType fieldInputType = fieldUi.inputType();
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.valueConstraints();

    if (fieldInputType == FieldInputType.PHONE_NUMBER || fieldInputType == FieldInputType.SECTION_BREAK
      || fieldInputType == FieldInputType.RICHTEXT || fieldInputType == FieldInputType.EMAIL
      || fieldInputType == FieldInputType.IMAGE || fieldInputType == FieldInputType.LINK
      || fieldInputType == FieldInputType.YOUTUBE || fieldInputType == FieldInputType.TEXTAREA) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.LIST || fieldInputType == FieldInputType.RADIO || fieldInputType == FieldInputType.CHECKBOX) {
      return DataValidationConstraint.ValidationType.FORMULA;
    } else if (fieldInputType == FieldInputType.TEXTFIELD) {

      if (valueConstraints.isPresent() && (valueConstraints.get() instanceof TextValueConstraints)) {
        TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints.get(); // TODO Use typesafe switch
        if (!textValueConstraints.literals().isEmpty())
          return DataValidationConstraint.ValidationType.FORMULA;
        else if (textValueConstraints.minLength().isPresent() || textValueConstraints.maxLength().isPresent())
          return DataValidationConstraint.ValidationType.TEXT_LENGTH;
        else
          return DataValidationConstraint.ValidationType.ANY;
      } else if (valueConstraints.isPresent() && (valueConstraints.get() instanceof ControlledTermValueConstraints)) {
        return DataValidationConstraint.ValidationType.FORMULA;
      } else
        return DataValidationConstraint.ValidationType.ANY;

    } else if (fieldInputType == FieldInputType.NUMERIC) {
      if (valueConstraints.isPresent() && (valueConstraints.get() instanceof NumericValueConstraints)) { // TODO Use typesafe switch
        NumericValueConstraints numericValueConstraints = (NumericValueConstraints)valueConstraints.get();
        XsdNumericDatatype numericType = numericValueConstraints.numberType();

        if (numericType == XsdNumericDatatype.DECIMAL || numericType == XsdNumericDatatype.DOUBLE || numericType == XsdNumericDatatype.FLOAT) {
          return DataValidationConstraint.ValidationType.DECIMAL;
        } else if (numericType == XsdNumericDatatype.LONG || numericType == XsdNumericDatatype.INTEGER || numericType == XsdNumericDatatype.INT
          || numericType == XsdNumericDatatype.SHORT || numericType == XsdNumericDatatype.BYTE) {
          return DataValidationConstraint.ValidationType.INTEGER;
        } else
          throw new RuntimeException("Invalid number type " + numericType + " for numeric field " + fieldName);
      } else
        throw new RuntimeException("Missing number type for numeric field " + fieldName);
    } else if (fieldInputType == FieldInputType.TEMPORAL) {
      if (valueConstraints.isPresent() && valueConstraints.get() instanceof TemporalValueConstraints) {
        TemporalValueConstraints temporalValueConstraints = (TemporalValueConstraints)valueConstraints.get();
        XsdTemporalDatatype temporalType = temporalValueConstraints.temporalType();

        if (temporalType == XsdTemporalDatatype.DATE || temporalType == XsdTemporalDatatype.DATETIME)
          return DataValidationConstraint.ValidationType.DATE;
        else if (temporalType == XsdTemporalDatatype.TIME)
          return DataValidationConstraint.ValidationType.TIME;
        else
          throw new RuntimeException("Invalid temporal type " + temporalType + " for temporal field " + fieldName);
      } else
        throw new RuntimeException("Missing temporal type for temporal field " + fieldName);
    } else
      throw new RuntimeException("Invalid field input type " + fieldInputType + " for field " + fieldName);
  }

  private String getNumericFormatString(String fieldName, XsdNumericDatatype numericType,
    Optional<Integer> decimalPlaces, Optional<String> unitOfMeasure)
  {
    String numericFormatString = "";

      if (numericType == XsdNumericDatatype.DECIMAL) {
        if (decimalPlaces.isPresent())
          numericFormatString += ""; // TODO Handle decimal places in Excel rendering
      } else if (numericType == XsdNumericDatatype.DOUBLE) {
        if (decimalPlaces.isPresent())
          numericFormatString += ""; // TODO Handle decimal places in Excel rendering
      } else if (numericType == XsdNumericDatatype.FLOAT) {
        if (decimalPlaces.isPresent())
          numericFormatString += ""; // TODO Handle decimal places in Excel rendering
      } else if (numericType == XsdNumericDatatype.LONG) {
      } else if (numericType == XsdNumericDatatype.INTEGER) {
      } else if (numericType == XsdNumericDatatype.INT) {
      } else if (numericType == XsdNumericDatatype.SHORT) {
      } else if (numericType == XsdNumericDatatype.BYTE) {
      } else
        throw new RuntimeException("Invalid number type " + numericType + " for numeric field " + fieldName);

    if (unitOfMeasure.isPresent()) {
      // TODO Handle unit representation in Excel if possible
    }

    return numericFormatString;
  }

  private String getTemporalFormatString(String fieldName, XsdTemporalDatatype temporalType,
    TemporalGranularity temporalGranularity,
    Optional<InputTimeFormat> inputTimeFormat, Optional<Boolean> timeZoneEnabled)
  {
    String temporalFormatString = "";

    if (temporalType == XsdTemporalDatatype.DATETIME) {
      if (temporalGranularity == TemporalGranularity.YEAR)
        temporalFormatString += "yyyy";
      else if (temporalGranularity == TemporalGranularity.MONTH)
        temporalFormatString += "yyyy/m";
      else if (temporalGranularity == TemporalGranularity.DAY)
        temporalFormatString += "yyyy/m/d";
      else if (temporalGranularity == TemporalGranularity.HOUR)
        temporalFormatString += "yyyy/m/d hh";
      else if (temporalGranularity == TemporalGranularity.MINUTE)
        temporalFormatString += "yyyy/m/d hh:mm";
      else if (temporalGranularity == TemporalGranularity.SECOND)
        temporalFormatString += "yyyy/m/d hh:mm:ss";
      else if (temporalGranularity == TemporalGranularity.DECIMAL_SECOND)
        temporalFormatString += "yyyy/m/d hh:mm:ss.000";
      else
        throw new RuntimeException(
          "Unknown temporal granularity " + temporalGranularity + " specified for temporal field " + fieldName);
    } else if (temporalType == XsdTemporalDatatype.DATE) {
      if (temporalGranularity == TemporalGranularity.YEAR)
        temporalFormatString += "yyyy";
      else if (temporalGranularity == TemporalGranularity.MONTH)
        temporalFormatString += "yyyy/m";
      else if (temporalGranularity == TemporalGranularity.DAY)
        temporalFormatString += "yyyy/m/d";
      else
        throw new RuntimeException(
          "Invalid temporal granularity " + temporalGranularity + " specified for date temporal field " + fieldName);
    } else if (temporalType == XsdTemporalDatatype.TIME) {
      if (temporalGranularity == TemporalGranularity.HOUR)
        temporalFormatString += "hh";
      else if (temporalGranularity == TemporalGranularity.MINUTE)
        temporalFormatString += "hh:mm";
      else if (temporalGranularity == TemporalGranularity.SECOND)
        temporalFormatString += "hh:mm:ss";
      else if (temporalGranularity == TemporalGranularity.DECIMAL_SECOND)
        temporalFormatString += "hh:mm:ss.000";
      else
        throw new RuntimeException(
          "Invalid temporal granularity " + temporalGranularity + " specified for time temporal field " + fieldName);
    } else
      throw new RuntimeException("Unknown temporal type " + temporalType + " specified for temporal field " + fieldName);

    if (inputTimeFormat.isPresent() && inputTimeFormat.get().isTwelveHour())
      temporalFormatString += " AM/PM";

    return temporalFormatString;
  }

  private CellStyle createCellStyle(FieldSchemaArtifact fieldSchemaArtifact)
  {
    String fieldName = fieldSchemaArtifact.name();
    FieldUi fieldUi = fieldSchemaArtifact.fieldUi();
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.valueConstraints();
    DataFormat dataFormat = workbook.createDataFormat();
    CellStyle cellStyle = workbook.createCellStyle();

    if (fieldUi.isNumeric()) {
      if (valueConstraints.isPresent()) {
        NumericValueConstraints numericValueConstraints = valueConstraints.get().asNumericValueConstraints();
        String formatString = getNumericFormatString(fieldName, numericValueConstraints.numberType(),
          numericValueConstraints.decimalPlace(), numericValueConstraints.unitOfMeasure());
        cellStyle.setDataFormat(dataFormat.getFormat(formatString));
      } else {
        String formatString = getNumericFormatString(fieldName, XsdNumericDatatype.DOUBLE, Optional.empty(), Optional.empty());
        cellStyle.setDataFormat(dataFormat.getFormat(formatString));
      }
    } else if (fieldUi.isTemporal()) {
      TemporalFieldUi temporalFieldUi = fieldUi.asTemporalFieldUi();
      if (valueConstraints.isPresent()) {
        TemporalValueConstraints temporalValueConstraints = valueConstraints.get().asTemporalValueConstraints();
        String formatString = getTemporalFormatString(fieldName, temporalValueConstraints.temporalType(),
          temporalFieldUi.temporalGranularity(), temporalFieldUi.inputTimeFormat(), temporalFieldUi.timezoneEnabled());
        cellStyle.setDataFormat(dataFormat.getFormat(formatString));
      }
    }
    return cellStyle;
  }

  private CellStyle createHeaderCellStyle(Workbook workbook)
    {
      CellStyle headerCellStyle = workbook.createCellStyle();
      headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
      headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      headerCellStyle.setFillForegroundColor(LIGHT_CORNFLOUR_BLUE);
      headerCellStyle.setFillBackgroundColor(LIGHT_CORNFLOUR_BLUE);

      return headerCellStyle;
    }

  private Map<String, Object> integratedSearch(Map<String, Object> valueConstraints,
    Integer page, Integer pageSize, String integratedSearchEndpoint, String apiKey) throws IOException, RuntimeException
  {
    HttpURLConnection connection = null;
    Map<String, Object> resultsMap;
    try {
      Map<String, Object> vcMap = new HashMap<>();
      vcMap.put("valueConstraints", valueConstraints);
      Map<String, Object> payloadMap = new HashMap<>();
      payloadMap.put("parameterObject", vcMap);
      payloadMap.put("page", page);
      payloadMap.put("pageSize", pageSize);
      String payload = mapper.writeValueAsString(payloadMap);
      connection = ConnectionUtil.createAndOpenConnection("POST", integratedSearchEndpoint, apiKey);
      OutputStream os = connection.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = connection.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        String message = "Error running integrated search. Response code: " + responseCode + "; Payload: " + payload;
        throw new RuntimeException(message);
      } else {
        String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
        resultsMap = mapper.readValue(response, HashMap.class);
      }
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return resultsMap;
  }

  private void addMetadataSheet(TemplateSchemaArtifact templateSchemaArtifact)
  {
    if (workbook.getSheetIndex(metadataSheetName) != -1)
      throw new RuntimeException("duplicate sheet called " + metadataSheetName + " in spreadsheet");

    Sheet metadataSheet = workbook.createSheet(metadataSheetName);
    Row headerRow = metadataSheet.createRow(0);
    Row dataRow = metadataSheet.createRow(1);

    Cell schemaTitleHeaderCell = headerRow.createCell(0);
    schemaTitleHeaderCell.setCellValue(ModelNodeNames.SCHEMA_ORG_TITLE);

    Cell schemaTitleDataCell = dataRow.createCell(0);
    schemaTitleDataCell.setCellValue(templateSchemaArtifact.name());

    if (templateSchemaArtifact.version().isPresent()) {
      Cell pavVersionHeaderCell = headerRow.createCell(1);
      pavVersionHeaderCell.setCellValue(ModelNodeNames.PAV_VERSION);
      Cell pavVersionDataCell = dataRow.createCell(1);
      pavVersionDataCell.setCellValue(templateSchemaArtifact.version().get().toString());
    } else
      throw new RuntimeException("template " + templateSchemaArtifact.name() + " has no field " + ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION);

    Cell pavCreatedOnHeaderCell = headerRow.createCell(2);
    pavCreatedOnHeaderCell.setCellValue(ModelNodeNames.PAV_CREATED_ON);
    Cell pavCreatedOnDataCell = dataRow.createCell(2);
    pavCreatedOnDataCell.setCellValue(ZonedDateTime.now( ZoneId.systemDefault()).format(xsdDateTimeFormatter));

    if (templateSchemaArtifact.jsonLdId().isPresent()) {
      Cell derivedFromHeaderCell = headerRow.createCell(3);
      derivedFromHeaderCell.setCellValue(ModelNodeNames.PAV_DERIVED_FROM);
      Cell derivedFromDataCell = dataRow.createCell(3);
      derivedFromDataCell.setCellValue(templateSchemaArtifact.jsonLdId().get().toString());
    } else
      throw new RuntimeException("template " + templateSchemaArtifact.name() + " has no field " + ModelNodeNames.JSON_LD_ID);

    metadataSheet.autoSizeColumn(0);
    metadataSheet.autoSizeColumn(1);
    metadataSheet.autoSizeColumn(2);
    metadataSheet.autoSizeColumn(3);
  }

  private Sheet createSheet(Workbook workbook, String proposedSheetName)
  {
    String safeSheetName = WorkbookUtil.createSafeSheetName(proposedSheetName);
    int sheetIndex = workbook.getSheetIndex(safeSheetName);

    if (sheetIndex != -1) { // There is already a sheet with this name
      String replacementSheetName = "Sheet" + sheetIndex;
      // Iterate until we find an unused sheet name
      while (workbook.getSheetIndex(replacementSheetName) != -1) {
        sheetIndex++;
        if (sheetIndex == Integer.MAX_VALUE)
          throw new RuntimeException("could not create sheet " + proposedSheetName);
        replacementSheetName = "Sheet" + sheetIndex;
      }
      return workbook.createSheet(replacementSheetName);
    } else
      return workbook.createSheet(safeSheetName);
  }
}
