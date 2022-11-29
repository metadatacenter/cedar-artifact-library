package org.metadatacenter.ss;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.model.core.ElementSchemaArtifact;
import org.metadatacenter.model.core.FieldSchemaArtifact;
import org.metadatacenter.model.core.FieldUI;
import org.metadatacenter.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.core.ValueConstraints;

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

     Cell columnNameCell = headerRow.createCell(columnIndex);
     columnNameCell.setCellValue(fieldSchemaArtifact.getName());

     CellStyle cellStyle = createCellStyle(fieldSchemaArtifact.getFieldUI(), fieldSchemaArtifact.getValueConstraints());
  }

  private CellStyle createCellStyle(FieldUI fieldUI, ValueConstraints valueConstraints)
  {
    DataFormat dataFormat = workbook.createDataFormat();
    CellStyle textStyle = workbook.createCellStyle();
    textStyle.setDataFormat(dataFormat.getFormat("text"));

    // worksheet.setDefaultColumnStyle(0, textStyle);
    return textStyle;
  }
}
