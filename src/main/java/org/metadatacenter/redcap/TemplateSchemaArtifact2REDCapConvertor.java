package org.metadatacenter.redcap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.model.core.FieldInputType;
import org.metadatacenter.model.core.FieldSchemaArtifact;
import org.metadatacenter.model.core.TemplateSchemaArtifact;
import org.metadatacenter.ss.SpreadsheetFactory;

public class TemplateSchemaArtifact2REDCapConvertor
{
  private final TemplateSchemaArtifact templateSchemaArtifact;

  TemplateSchemaArtifact2REDCapConvertor(TemplateSchemaArtifact templateSchemaArtifact)
  {
    this.templateSchemaArtifact = templateSchemaArtifact;
  }

  public Workbook generateREDCapSpreadsheet()
  {
    Workbook workbook = SpreadsheetFactory.createEmptyWorkbook();

    processTemplateSchemaArtifact(workbook);

    return workbook;
  }

  private void processTemplateSchemaArtifact(Workbook workbook)
  {
    String templateName = templateSchemaArtifact.getJsonSchemaTitle();
    String templateDescription = templateSchemaArtifact.getJsonSchemaDescription();
    Sheet sheet = workbook.createSheet(templateName);
    createHeader(sheet);

    int currentRowNumber = REDCapConstants.HEADER_ROW_NUMBER + 1;
    for (String fieldName : templateSchemaArtifact.getTemplateUI().getOrder()) {
      if (templateSchemaArtifact.getFieldSchemas().containsKey(fieldName)) {
        FieldSchemaArtifact fieldSchemaArtifact = templateSchemaArtifact.getFieldSchemas().get(fieldName);

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
    String fieldName = fieldSchemaArtifact.getName();

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

    Cell fieldNotesHeaderCell = row.createCell(REDCapConstants.FIELD_NOTES_COLUMN_INDEX);
    fieldNotesHeaderCell.setCellValue(fieldSchemaArtifact.getDescription());

    Cell requiredFieldHeaderCell = row.createCell(REDCapConstants.REQUIRED_FIELD_COLUMN_INDEX);
    requiredFieldHeaderCell.setCellValue(fieldSchemaArtifact.getValueConstraints().isRequiredValue());

  }

  //  public static final int TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER_COLUMN_INDEX = 7;
  //  public static final int TEXT_VALIDATION_MIN_MAX_COLUMN_INDEX = 8;
  //  public static final int IDENTIFIERS_COLUMN_INDEX = 9;

  private String generateREDCapFieldType(FieldSchemaArtifact fieldSchemaArtifact)
  {
    FieldInputType fieldInputType = fieldSchemaArtifact.getFieldUI().getInputType();

    if (fieldInputType == FieldInputType.TEXTFIELD || fieldInputType == FieldInputType.TEMPORAL ||
      fieldInputType == FieldInputType.EMAIL ||       fieldInputType == FieldInputType.NUMERIC ||
      fieldInputType == FieldInputType.PHONE_NUMBER)

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
      columnIndex++;
    }
  }

}
