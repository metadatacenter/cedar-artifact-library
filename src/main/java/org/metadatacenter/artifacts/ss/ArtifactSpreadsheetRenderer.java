package org.metadatacenter.artifacts.ss;

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
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUI;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.NumberType;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.TemporalType;
import org.metadatacenter.artifacts.model.core.ValueConstraints;
import org.metadatacenter.artifacts.util.ConnectionUtil;
import org.metadatacenter.model.ModelNodeNames;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.metadatacenter.artifacts.ss.SpreadSheetUtil.setCellComment;

public class ArtifactSpreadsheetRenderer
{
  private final Workbook workbook;
  private final String terminologyServerIntegratedSearchEndpoint;

  private final String terminologyServerAPIKey;
  private final ObjectMapper mapper;
  private final ObjectWriter objectWriter;

  private final String metadataSheetName = ".metadata";

  private final Short LIGHT_CORNFLOUR_BLUE = IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex();

  private final CellStyle headerCellstyle;

  public static final String xsdDateTimeFormatterString = "uuuu-MM-dd'T'HH:mm:ssZZZZZ";
  public static final DateTimeFormatter xsdDateTimeFormatter =
    DateTimeFormatter.ofPattern(xsdDateTimeFormatterString).withZone(ZoneId.systemDefault());

  public ArtifactSpreadsheetRenderer(Workbook workbook, String terminologyServerIntegratedSearchEndpoint, String terminologyServerAPIKey)
  {
    this.workbook = workbook;
    this.terminologyServerIntegratedSearchEndpoint = terminologyServerIntegratedSearchEndpoint;
    this.terminologyServerAPIKey = terminologyServerAPIKey;

    this.mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    this.objectWriter = mapper.writer().withDefaultPrettyPrinter();

    this.headerCellstyle = createHeaderCellStyle(workbook);
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

    addMetadataSheet(templateSchemaArtifact);
  }

  public void render(ElementSchemaArtifact elementSchemaArtifact, Sheet sheet)
  {
    throw new RuntimeException("element rendering not implemented");
  }

  public void render(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, Row headerRow)
  {
    String fieldName = fieldSchemaArtifact.getName();
    String fieldDescription = fieldSchemaArtifact.getDescription();
    FieldInputType fieldInputType = fieldSchemaArtifact.getFieldUI().getInputType();
    CellStyle cellStyle = createCellStyle(fieldSchemaArtifact);
    int rowIndex = headerRow.getRowNum() + 1;
    Cell columnNameHeaderCell = headerRow.createCell(columnIndex);

    //    if (fieldSchemaArtifact.getSkosPrefLabel().isPresent())
    //      columnNameCell.setCellValue(fieldSchemaArtifact.getSkosPrefLabel().get());
    //    else
    columnNameHeaderCell.setCellValue(fieldName);
    columnNameHeaderCell.setCellStyle(headerCellstyle);
    setCellComment(columnNameHeaderCell, fieldDescription);

    sheet.setDefaultColumnStyle(columnIndex, cellStyle);
    sheet.autoSizeColumn(columnIndex);

    setColumnDataValidationConstraintIfRequired(fieldSchemaArtifact, sheet, columnIndex, rowIndex);

    if (fieldSchemaArtifact.isHidden())
      sheet.setColumnHidden(columnIndex, true);
  }

  private void setColumnDataValidationConstraintIfRequired(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, int firstRow)
  {
    DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
    Optional<DataValidationConstraint> constraint = createDataValidationConstraint(fieldSchemaArtifact,
      dataValidationHelper);

    if (constraint.isPresent()) {
      // TODO Check lastRow
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
    String fieldName = fieldSchemaArtifact.getName();
    FieldUI fieldUI = fieldSchemaArtifact.getFieldUI();
    FieldInputType fieldInputType = fieldUI.getInputType();
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.getValueConstraints();

    // Only some fields have validation constraints that we can create messages for
    if (fieldInputType == FieldInputType.TEXTFIELD || fieldInputType == FieldInputType.TEXTAREA) {

      if (valueConstraints.isPresent()) {
        if (valueConstraints.get().getMinLength().isPresent()) {
          Integer minLength = valueConstraints.get().getMinLength().get();
          if (valueConstraints.get().getMaxLength().isPresent()) { // Minimum length present, maximum length present
            Integer maxLength = valueConstraints.get().getMaxLength().get();
            return "Value should have a minimum of " + minLength + " characters and a maximum of " + maxLength;
          } else { // Minimum length present, maximum length not present
            return "Value should have a minimum of " + minLength + " characters";
          }
        } else {
          if (valueConstraints.get().getMaxLength().isPresent()) { // Minimum length not present, maximum length present
            Integer maxLength = valueConstraints.get().getMaxLength().get();
            return "Value should have a maximum of " + maxLength + " characters";
          } else { // Minimum length not present, maximum length not present
            return "";
          }
        }
      } else if (fieldInputType == FieldInputType.NUMERIC) {
        if (valueConstraints.get().getMinValue().isPresent()) {
          Number minValue = valueConstraints.get().getMinValue().get();
          if (valueConstraints.get().getMaxValue().isPresent()) { // Minimum present, maximum present
            Number maxValue = valueConstraints.get().getMaxValue().get();
            return "Value should be between " + minValue + " and " + maxValue;
          } else { // Minimum present, maximum not present
            return "Value should be greater than " + minValue;
          }
        } else {
          if (valueConstraints.get().getMaxValue().isPresent()) { // Maximum present, minimum not present
            Number maxValue = valueConstraints.get().getMaxValue().get();
            return "Value should be less than " + maxValue;
          } else { // Maximum not present, minimum not present
            return "Value should be a number";
          }
        }
      } else if (fieldInputType == FieldInputType.TEMPORAL) {
        String temporalFormatString = getTemporalFormatString(fieldName, valueConstraints.get().getTemporalType(),
          fieldUI.getTemporalGranularity(), fieldUI.getInputTimeFormat(), fieldUI.getTimeZoneEnabled());
        return temporalFormatString;
      } else
        return "";
    } else
      return "";
  }

  private Optional<DataValidationConstraint> createDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    String fieldName = fieldSchemaArtifact.getName();

    int validationType = getValidationType(fieldSchemaArtifact);

    // Only some fields have validation constraints that we can act on
    if (validationType == DataValidationConstraint.ValidationType.ANY)
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
    } else
      throw new RuntimeException("Do no know how to handle data validation type " + validationType + " for field " + fieldName);
  }

  private Optional<DataValidationConstraint> createDecimalDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.getValueConstraints();
    int validationType = DataValidationConstraint.ValidationType.DECIMAL;

    if (valueConstraints.isPresent()) {
      if (valueConstraints.get().getMinValue().isPresent()) {
        Number minValue = valueConstraints.get().getMinValue().get();
        if (valueConstraints.get().getMaxValue().isPresent()) { // Minimum present, maximum present
          Number maxValue = valueConstraints.get().getMaxValue().get();
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
              minValue.toString(), maxValue.toString()));
        } else { // Minimum present, maximum not present
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.GREATER_THAN, minValue.toString(), ""));
        }
      } else {
        if (valueConstraints.get().getMaxValue().isPresent()) { // Maximum present, minimum not present
          Number maxValue = valueConstraints.get().getMaxValue().get();
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.LESS_OR_EQUAL, maxValue.toString(), ""));
        } else { // Maximum not present, minimum not present
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
            String.valueOf(Float.MIN_VALUE), String.valueOf(Float.MAX_VALUE)));
        }
      }
    } else
      return Optional.of(dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
        String.valueOf(Float.MIN_VALUE), String.valueOf(Float.MAX_VALUE)));
  }

  private Optional<DataValidationConstraint> createIntegerDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.getValueConstraints();
    int validationType = DataValidationConstraint.ValidationType.INTEGER;

    if (valueConstraints.isPresent()) {
      if (valueConstraints.get().getMinValue().isPresent()) {
        Number minValue = valueConstraints.get().getMinValue().get();
        if (valueConstraints.get().getMaxValue().isPresent()) { // Minimum present, maximum present
          Number maxValue = valueConstraints.get().getMaxValue().get();
          return Optional.of(
            dataValidationHelper.createNumericConstraint(validationType, DataValidationConstraint.OperatorType.BETWEEN,
              minValue.toString(), maxValue.toString()));
        } else { // Minimum present, maximum not present
          return Optional.of(dataValidationHelper.createNumericConstraint(validationType,
            DataValidationConstraint.OperatorType.GREATER_THAN, minValue.toString(), ""));
        }
      } else {
        if (valueConstraints.get().getMaxValue().isPresent()) { // Maximum present, minimum not present
          Number maxValue = valueConstraints.get().getMaxValue().get();
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

  private Optional<DataValidationConstraint> createTextLengthDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.getValueConstraints();

    if (valueConstraints.isPresent()) {
      if (valueConstraints.get().getMinLength().isPresent()) {
        Integer minLength = valueConstraints.get().getMinLength().get();
        if (valueConstraints.get().getMaxLength().isPresent()) { // Minimum length present, maximum length present
          Integer maxLength = valueConstraints.get().getMaxLength().get();
          return Optional.of(
            dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.BETWEEN, minLength.toString(), maxLength.toString()));
        } else { // Minimum length present, maximum length not present
          return Optional.of(dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.GREATER_THAN,
            minLength.toString(), ""));
        }
      } else {
        if (valueConstraints.get().getMaxLength().isPresent()) { // Minimum length not present, maximum length present
          Integer maxLength = valueConstraints.get().getMaxLength().get();
          return Optional.of(dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.LESS_OR_EQUAL,
            maxLength.toString(), ""));
        } else { // Minimum length not present, maximum length not present
          return Optional.empty();
        }
      }
    } else
      return Optional.empty();
  }

  private Optional<DataValidationConstraint> createDateDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    if (fieldSchemaArtifact.getFieldUI().isTemporal())
      return Optional.of(
        dataValidationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "Date(1, 1, 1)",
          "Date(9999,12,31)", "dd/mm/yyyy"));
    else
      return Optional.empty();
  }

  private Optional<DataValidationConstraint> createTimeDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    if (fieldSchemaArtifact.getFieldUI().isTemporal())
      return Optional.of(
        dataValidationHelper.createTimeConstraint(DataValidationConstraint.OperatorType.BETWEEN, "=TIME(0,0,0)",
          "=TIME(23,59,59)"));
    else
      return Optional.empty();
  }

  // See: https://stackoverflow.com/questions/27630507/is-there-a-max-number-items-while-generating-drop-down-list-in-excel-using-apach/27639609#27639609

  private Optional<DataValidationConstraint> createFormulaDataValidationConstraint(FieldSchemaArtifact fieldSchemaArtifact,
    DataValidationHelper dataValidationHelper)
  {
    Map<String, String> values = getPossibleValues(fieldSchemaArtifact.getValueConstraints());

    if (!values.isEmpty()) {
      String sheetName = fieldSchemaArtifact.getName();
      Sheet valueSheet = workbook.createSheet(sheetName);
      int numberOfValues = values.keySet().size();
      String formula = "'" + sheetName + "'!$A$1:$A$" + numberOfValues;

      int rowNumber = 0;
      for (String prefLabel : values.keySet()) {
        String iri = values.get(prefLabel);
        Row row = valueSheet.createRow(rowNumber);
        Cell valueCell = row.createCell(0);
        valueCell.setCellValue(prefLabel);

        // Even though we don't use them we put the IRI in the second column
        if (iri != null || !iri.isEmpty()) {
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

  private Map<String, String> getPossibleValues(Optional<ValueConstraints> valueConstraints)
  {
    if (valueConstraints.isPresent()) {
      Map<String, String> values = new HashMap<>();
      List<String> labels = valueConstraints.get().getLiterals().stream().map(LiteralValueConstraint::getLabel).collect(Collectors.toList());

      if (valueConstraints.get().hasOntologyValueBasedConstraints()) {
        Map<String, String> ontologyBasedValues = getValuesFromTerminologyServer(valueConstraints.get());
        values.putAll(ontologyBasedValues);
      }

      for (String label : labels)
        values.put(label, "");

      return values;
    } else
      return Collections.emptyMap();
  }

  // Return prefLabel->IRI
  private Map<String, String> getValuesFromTerminologyServer(ValueConstraints valueConstraints)
  {

    Map<String, String> values = new HashMap<>();

    try {
      String vc = objectWriter.writeValueAsString(valueConstraints);
      Map<String, Object> vcMap = mapper.readValue(vc, Map.class);

      List<Map<String, String>> valueDescriptions;
      Map<String, Object> searchResult = integratedSearch(vcMap, 1, 5000, // TODO
        terminologyServerIntegratedSearchEndpoint, terminologyServerAPIKey);
      valueDescriptions = searchResult.containsKey("collection") ?
        (List<Map<String, String>>)searchResult.get("collection") :
        new ArrayList<>();
      if (valueDescriptions.size() > 0) {
        for (int valueDescriptionsIndex = 0; valueDescriptionsIndex < valueDescriptions.size(); valueDescriptionsIndex++) {
          String uri = valueDescriptions.get(valueDescriptionsIndex).get("@id");
          String prefLabel = valueDescriptions.get(valueDescriptionsIndex).get("prefLabel");
          values.put(prefLabel, uri);
        }
      }
    } catch (IOException | RuntimeException e) {
      throw new RuntimeException("Error retrieving values from terminology server " + e.getMessage());
    }
    return values;
  }

  // Returns DataValidationConstraint.ValidationType (ANY, FORMULA, LIST, DATE, TIME, DECIMAL, INTEGER, TEXT_LENGTH)
  private int getValidationType(FieldSchemaArtifact fieldSchemaArtifact)
  {
    String fieldName = fieldSchemaArtifact.getName();
    FieldUI fieldUI = fieldSchemaArtifact.getFieldUI();
    FieldInputType fieldInputType = fieldUI.getInputType();
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.getValueConstraints();

    if (fieldInputType == FieldInputType.PHONE_NUMBER || fieldInputType == FieldInputType.SECTION_BREAK
      || fieldInputType == FieldInputType.RICHTEXT || fieldInputType == FieldInputType.EMAIL
      || fieldInputType == FieldInputType.IMAGE || fieldInputType == FieldInputType.LINK
      || fieldInputType == FieldInputType.YOUTUBE) {
      return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.LIST || fieldInputType == FieldInputType.RADIO ||
      fieldInputType == FieldInputType.CHECKBOX) {
      return DataValidationConstraint.ValidationType.FORMULA;
    } else if (fieldInputType == FieldInputType.TEXTFIELD || fieldInputType == FieldInputType.TEXTAREA) {
      if (valueConstraints.isPresent() && valueConstraints.get().hasValueBasedConstraints())
        return DataValidationConstraint.ValidationType.FORMULA;
      else if (valueConstraints.isPresent() && (valueConstraints.get().getMinLength().isPresent() || valueConstraints.get().getMaxLength().isPresent()))
        return DataValidationConstraint.ValidationType.TEXT_LENGTH;
      else
        return DataValidationConstraint.ValidationType.ANY;
    } else if (fieldInputType == FieldInputType.NUMERIC) {
      if (valueConstraints.isPresent() && valueConstraints.get().getNumberType().isPresent()) {
        NumberType numberType = valueConstraints.get().getNumberType().get();

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
      if (valueConstraints.isPresent() && valueConstraints.get().getTemporalType().isPresent()) {
        TemporalType temporalType = valueConstraints.get().getTemporalType().get();

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
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.getValueConstraints();
    DataFormat dataFormat = workbook.createDataFormat();
    CellStyle cellStyle = workbook.createCellStyle();

    if (fieldUI.isNumeric()) {
      if (valueConstraints.isPresent()) {
        String formatString = getNumericFormatString(fieldName, valueConstraints.get().getNumberType(),
          valueConstraints.get().getDecimalPlaces(), valueConstraints.get().getUnitOfMeasure());
        cellStyle.setDataFormat(dataFormat.getFormat(formatString));
      } else {
        String formatString = getNumericFormatString(fieldName, Optional.empty(), Optional.empty(), Optional.empty());
        cellStyle.setDataFormat(dataFormat.getFormat(formatString));
      }
    } else if (fieldUI.isTemporal()) {
      if (valueConstraints.isPresent()) {
        String formatString = getTemporalFormatString(fieldName, valueConstraints.get().getTemporalType(),
          fieldUI.getTemporalGranularity(), fieldUI.getInputTimeFormat(), fieldUI.getTimeZoneEnabled());
        cellStyle.setDataFormat(dataFormat.getFormat(formatString));
      } else {
        String formatString = getTemporalFormatString(fieldName, Optional.empty(), fieldUI.getTemporalGranularity(), fieldUI.getInputTimeFormat(), fieldUI.getTimeZoneEnabled());
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
    Map<String, Object> resultsMap = new HashMap<>();
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
    Sheet metadataSheet = workbook.createSheet(metadataSheetName);
    Row headerRow = metadataSheet.createRow(0);
    Row dataRow = metadataSheet.createRow(1);

    Cell schemaTitleHeaderCell = headerRow.createCell(0);
    schemaTitleHeaderCell.setCellValue(ModelNodeNames.SCHEMA_ORG_TITLE);

    Cell schemaTitleDataCell = dataRow.createCell(0);
    schemaTitleDataCell.setCellValue(templateSchemaArtifact.getName());

    if (templateSchemaArtifact.getVersion().isPresent()) {
      Cell pavVersionHeaderCell = headerRow.createCell(1);
      pavVersionHeaderCell.setCellValue(ModelNodeNames.PAV_VERSION);
      Cell pavVersionDataCell = dataRow.createCell(1);
      pavVersionDataCell.setCellValue(templateSchemaArtifact.getVersion().get().toString());
    } else
      throw new RuntimeException("template " + templateSchemaArtifact.getName() + " has no field " + ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION);

    Cell pavCreatedOnHeaderCell = headerRow.createCell(2);
    pavCreatedOnHeaderCell.setCellValue(ModelNodeNames.PAV_CREATED_ON);
    Cell pavCreatedOnDataCell = dataRow.createCell(2);
    pavCreatedOnDataCell.setCellValue(ZonedDateTime.now( ZoneId.systemDefault()).format(xsdDateTimeFormatter));

    if (templateSchemaArtifact.getJsonLDID().isPresent()) {
      Cell derivedFromHeaderCell = headerRow.createCell(3);
      derivedFromHeaderCell.setCellValue(ModelNodeNames.PAV_DERIVED_FROM);
      Cell derivedFromDataCell = dataRow.createCell(3);
      derivedFromDataCell.setCellValue(templateSchemaArtifact.getJsonLDID().get().toString());
    } else
      throw new RuntimeException("template " + templateSchemaArtifact.getName() + " has no field " + ModelNodeNames.JSON_LD_ID);

    metadataSheet.autoSizeColumn(0);
    metadataSheet.autoSizeColumn(1);
    metadataSheet.autoSizeColumn(2);
    metadataSheet.autoSizeColumn(3);
  }
}
