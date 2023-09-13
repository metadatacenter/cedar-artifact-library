package org.metadatacenter.artifacts.redcap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.NumericType;
import org.metadatacenter.artifacts.model.core.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalFieldUi;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.TemporalType;
import org.metadatacenter.artifacts.model.core.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.ValueConstraints;

import java.util.Optional;

public class TemplateSchemaArtifact2REDCapConvertor
{
  private final TemplateSchemaArtifact templateSchemaArtifact;

  public TemplateSchemaArtifact2REDCapConvertor(TemplateSchemaArtifact templateSchemaArtifact)
  {
    this.templateSchemaArtifact = templateSchemaArtifact;
  }

  public void generateREDCapWorkbook(Workbook workbook)
  {
    processTemplateSchemaArtifact(workbook);
  }

  private void processTemplateSchemaArtifact(Workbook workbook)
  {
    String templateName = templateSchemaArtifact.jsonSchemaTitle();
    String templateDescription = templateSchemaArtifact.jsonSchemaDescription();
    Sheet sheet = workbook.createSheet(templateName);
    createHeader(sheet);

    int currentRowNumber = REDCapConstants.HEADER_ROW_NUMBER + 1;
    for (String fieldName : templateSchemaArtifact.templateUi().order()) {
      if (templateSchemaArtifact.fieldSchemas().containsKey(fieldName)) {
        FieldSchemaArtifact fieldSchemaArtifact = templateSchemaArtifact.fieldSchemas().get(fieldName);

        processFieldSchemaArtifact(fieldSchemaArtifact, sheet, currentRowNumber, templateName, templateDescription);
      } else {
        // TODO Ignore non-field values silently for the moment
      }

      currentRowNumber++;
    }
  }

  private void processFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int rowNumber,
    String templateName, String templateDescription)
  {

    Row row = sheet.createRow(rowNumber);
    String fieldName = fieldSchemaArtifact.name();

    Cell variableNameCell = row.createCell(REDCapConstants.VARIABLE_NAME_COLUMN_INDEX);
    // TODO fieldName will have to be processed to allow only valid REDCap variable names
    variableNameCell.setCellValue(fieldName);

    Cell formNameCell = row.createCell(REDCapConstants.FORM_NAME_COLUMN_INDEX);
    formNameCell.setCellValue(templateName);

    // TODO Put template description in cell comment

    Cell sectionHeaderCell = row.createCell(REDCapConstants.SECTION_HEADER_COLUMN_INDEX);
    sectionHeaderCell.setCellValue(""); // TODO Deal with section headers

    Cell fieldTypeCell = row.createCell(REDCapConstants.FIELD_TYPE_COLUMN_INDEX);
    String fieldType = generateREDCapFieldType(fieldSchemaArtifact);
    fieldTypeCell.setCellValue(fieldType);

    Cell fieldLabelHeaderCell = row.createCell(REDCapConstants.FIELD_LABEL_COLUMN_INDEX);
    fieldLabelHeaderCell.setCellValue(fieldName);

    Cell choicesCalculationsORSliderLabelsHeaderCell = row.createCell(REDCapConstants.CHOICES_CALCULATIONS_OR_SLIDER_LABELS_COLUMN_INDEX);
    // TODO

    Cell fieldNotesHeaderCell = row.createCell(REDCapConstants.FIELD_NOTES_COLUMN_INDEX);
    fieldNotesHeaderCell.setCellValue(fieldSchemaArtifact.description());

    Cell textValidationTypeORShowSliderNumberHeaderCell = row.createCell(REDCapConstants.TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER_COLUMN_INDEX);
    if (fieldType == REDCapConstants.TEXT_FIELD_TYPE) {
      Optional<String> textFieldValidationValue = createTextFieldValidationValue(fieldSchemaArtifact);
    } else if (fieldType == REDCapConstants.SLIDER_FIELD_TYPE) {
      // TODO
    }

    Cell textValidationMinMaxHeaderCell = row.createCell(REDCapConstants.TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER_COLUMN_INDEX);
    // TODO

    Cell requiredFieldHeaderCell = row.createCell(REDCapConstants.REQUIRED_FIELD_COLUMN_INDEX);
    if (fieldSchemaArtifact.valueConstraints().isPresent())
      requiredFieldHeaderCell.setCellValue(fieldSchemaArtifact.valueConstraints().get().requiredValue());
    else
      requiredFieldHeaderCell.setCellValue(false);
  }

  Optional<String> createTextFieldValidationValue(FieldSchemaArtifact fieldSchemaArtifact)
  {
    FieldInputType fieldInputType = fieldSchemaArtifact.fieldUi().inputType();
    Optional<ValueConstraints> valueConstraints = fieldSchemaArtifact.valueConstraints();
    FieldUi fieldUi = fieldSchemaArtifact.fieldUi();

    switch (fieldInputType) {
    case TEMPORAL:
      if (valueConstraints.isPresent() && (valueConstraints.get() instanceof TemporalValueConstraints)) {
        TemporalValueConstraints temporalValueConstraints = (TemporalValueConstraints)valueConstraints.get();
        TemporalType temporalType = temporalValueConstraints.temporalType();
        TemporalFieldUi temporalFieldUi = fieldUi.asTemporalFieldUi();
        InputTimeFormat inputTimeFormat = temporalFieldUi.inputTimeFormat();
        TemporalGranularity temporalGranularity = temporalFieldUi.temporalGranularity();

        switch (temporalType) {
        case DATE:
            if (temporalGranularity == TemporalGranularity.MONTH)
              return Optional.of(REDCapConstants.DATE_MY_TEXTFIELD_VALIDATION);
            else if (temporalGranularity == TemporalGranularity.DAY)
              return Optional.of(REDCapConstants.DATE_DY_TEXTFIELD_VALIDATION);
            else
              return Optional.of(REDCapConstants.DATE_YMD_TEXTFIELD_VALIDATION);

          // CEDAR has no way of specifying the following REDCap validations:
          // DATE_MDY_TEXTFIELD_VALIDATION = "DATE_MDY";
          // DATE_DMY_TEXTFIELD_VALIDATION = "DATE_DMY";
          // MD_TEXTFIELD_VALIDATION = "MD";
        case DATETIME:
            if (temporalGranularity == TemporalGranularity.SECOND)
              return Optional.of(REDCapConstants.DATETIME_SECONDS_Y_TEXTFIELD_VALIDATION);
            else
              return Optional.of(REDCapConstants.DATETIME_YMD_TEXTFIELD_VALIDATION);
          // CEDAR has no way of specifying the following REDCap validations:
          // DATETIME_SECONDS_M_TEXTFIELD_VALIDATION = "DATETIME_SECONDS_M";
          // DATETIME_SECONDS_D_TEXTFIELD_VALIDATION = "DATETIME_SECONDS_D";
          // DATETIME_DMY_TEXTFIELD_VALIDATION = "DATETIME_DMY";
          // DATETIME_YMD_FIELD_VALIDATION = "DATETIME_YMD";
          // DATETIME_MDY_FIELD_VALIDATION = "DATETIME_MDY";
        case TIME:
          if (temporalGranularity == TemporalGranularity.SECOND)
            return Optional.of(REDCapConstants.TIME_MM_SS_TEXTFIELD_VALIDATION);
          else
            return Optional.of(REDCapConstants.TIME_TEXTFIELD_VALIDATION);
        }
      } else
        throw new RuntimeException("Missing temporalType value in value constraint for numeric field " + fieldSchemaArtifact.name());

    case EMAIL:
      return Optional.of(REDCapConstants.EMAIL_TEXTFIELD_VALIDATION);
    case NUMERIC:

      if (valueConstraints.isPresent() && (valueConstraints.get() instanceof NumericValueConstraints)) {
        NumericValueConstraints numericValueConstraints = valueConstraints.get().asNumericValueConstraints();
        NumericType numberType = numericValueConstraints.numberType();

        switch (numberType) {
        case INTEGER, LONG, INT, SHORT, BYTE -> {
          return Optional.of(REDCapConstants.INTEGER_TEXTFIELD_VALIDATION);
        }
        case DECIMAL, FLOAT, DOUBLE -> {
          if (numericValueConstraints.decimalPlaces().isPresent()) {
            Integer decimalPlaces = numericValueConstraints.decimalPlaces().get();
            if (decimalPlaces == 1)
              return Optional.of(REDCapConstants.NUMBER_1_DECIMAL_PLACE_TEXTFIELD_VALIDATION);
            else if (decimalPlaces == 2)
              return Optional.of(REDCapConstants.NUMBER_2_DECIMAL_PLACE_TEXTFIELD_VALIDATION);
            else if (decimalPlaces == 2)
              return Optional.of(REDCapConstants.NUMBER_3_DECIMAL_PLACE_TEXTFIELD_VALIDATION);
            else if (decimalPlaces == 4)
              return Optional.of(REDCapConstants.NUMBER_4_DECIMAL_PLACE_TEXTFIELD_VALIDATION);
            else
              return Optional.of(REDCapConstants.NUMBER_TEXTFIELD_VALIDATION);
          } else {
            return Optional.of(REDCapConstants.NUMBER_TEXTFIELD_VALIDATION);
          }
        }
        }
      } else
        throw new RuntimeException("Missing numericType value in value constraint  for numeric field " + fieldSchemaArtifact.name());

    case PHONE_NUMBER:
      return Optional.of(REDCapConstants.PHONE_TEXTFIELD_VALIDATION);
    default:
      return Optional.empty();
    }

    // We don't currently generate:
    //  PHONE_AUSTRALIA_TEXTFIELD_VALIDATION = "Phone (Australia)";
    //  POSTAL_CODE_AUSTRALIA_TEXTFIELD_VALIDATION = "Postal Code (Australia)";
    //  POSTAL_CODE_CANADA_TEXTFIELD_VALIDATION = "Postal Code (Canada)";
    //  SOCIAL_SECURITY_NUMBER_US_TEXTFIELD_VALIDATION = "Social Security Number (US)";
    //  LETTERS_ONLY_TEXTFIELD_VALIDATION = "Letters only";
    //  SQL_TEXTFIELD_VALIDATION = "SQL";
    //  MRN_TEXTFIELD_VALIDATION = "MRN";
    //  ZIPCODE_TEXTFIELD_VALIDATION = "ZIPCODE";
  }


  //  public static final int TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER_COLUMN_INDEX = 7;
  //  public static final int TEXT_VALIDATION_MIN_MAX_COLUMN_INDEX = 8;
  //  public static final int IDENTIFIERS_COLUMN_INDEX = 9;

  private String generateREDCapFieldType(FieldSchemaArtifact fieldSchemaArtifact)
  {
    FieldInputType fieldInputType = fieldSchemaArtifact.fieldUi().inputType();

    if (fieldInputType == FieldInputType.TEXTFIELD)
      return REDCapConstants.TEXT_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.TEMPORAL)
      return REDCapConstants.TEXT_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.EMAIL)
      return REDCapConstants.TEXT_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.NUMERIC)
      return REDCapConstants.TEXT_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.PHONE_NUMBER)
      return REDCapConstants.TEXT_FIELD_TYPE;
     else if (fieldInputType == FieldInputType.TEXTAREA)
      return REDCapConstants.NOTES_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.RADIO)
      return REDCapConstants.RADIO_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.CHECKBOX)
      return REDCapConstants.CHECKBOX_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.RICHTEXT)
      return REDCapConstants.DESCRIPTIVE_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.IMAGE)
      throw new RuntimeException("CEDAR image field type not currently mappable to REDCap");
    else if (fieldInputType == FieldInputType.LINK)
      return REDCapConstants.TEXT_FIELD_TYPE;
    else if (fieldInputType == FieldInputType.YOUTUBE)
      throw new RuntimeException("CEDAR YouTube field type not currently mappable to REDCap");
    else
      throw new RuntimeException("Unknown CEDAR field input type " + fieldInputType);
  }

  private void createHeader(Sheet sheet)
  {
    Row headerRow = sheet.createRow(0);

    int columnIndex = REDCapConstants.HEADER_ROW_NUMBER;
    for (String columnName: REDCapConstants.COLUMN_NAMES) {
      Cell cell = headerRow.createCell(columnIndex);
      cell.setCellValue(columnName);
      sheet.setColumnWidth(columnIndex, (columnName.length() + 2) * 256);
      columnIndex++;
    }
  }

}
