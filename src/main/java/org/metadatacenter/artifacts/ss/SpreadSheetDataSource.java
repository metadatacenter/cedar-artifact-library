package org.metadatacenter.artifacts.ss;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpreadSheetDataSource
{
  private final Workbook workbook;

  private final Map<String, Sheet> sheetMap = new HashMap<>();

  public SpreadSheetDataSource(Workbook workbook)
  {
    this.workbook = workbook;

    for (int i = 0; i < workbook.getNumberOfSheets(); i++)
      sheetMap.put(workbook.getSheetName(i), workbook.getSheetAt(i));
  }

  public CellLocation getCellLocation(String cellLocationSpecification, CellLocation currentCellLocation)
  {
    String sheetName = currentCellLocation.getSheetName();
    Sheet sheet = getWorkbook().getSheet(sheetName);

    if (sheet == null)
      throw new RuntimeException("invalid current sheet name '" + sheetName + "'");

    return getCellLocation(sheet, cellLocationSpecification, currentCellLocation);
  }

  public CellRange getDefaultEnclosingCellRange()
  {
    Sheet firstSheet = this.workbook.getSheetAt(0);
    String firstSheetName = firstSheet.getSheetName();
    int firstRow = firstSheet.getFirstRowNum();
    int lastRow = firstSheet.getLastRowNum();
    int firstColumn = 0;
    int lastColumn = 0;

    for (int currentRow = firstRow; currentRow < lastRow; currentRow++) {
      int currentNumberOfColumns = firstSheet.getRow(currentRow).getLastCellNum();
      if (lastColumn < currentNumberOfColumns)
        lastColumn = currentNumberOfColumns;
    }

    CellLocation startRange = new CellLocation(firstSheetName, firstColumn, firstRow);
    CellLocation finishRange = new CellLocation(firstSheetName, lastColumn, lastRow);

    return new CellRange(startRange, finishRange);
  }

  private CellLocation getCellLocation(Sheet sheet, String cellLocationSpecification, CellLocation currentCellLocation)
  {
    Pattern p = Pattern.compile("(\\*|[a-zA-Z]+)(\\*|[0-9]+)"); // ( \* | [a-zA-z]+ ) ( \* | [0-9]+ )
    Matcher m = p.matcher(cellLocationSpecification);
    CellLocation resolvedCellLocation;

    if (m.find()) {
      String columnSpecification = m.group(1);
      String rowSpecification = m.group(2);

      if (columnSpecification == null) {
        throw new RuntimeException("missing column specification in location " + cellLocationSpecification);
      }
      if (rowSpecification == null) {
        throw new RuntimeException("missing row specification in location " + cellLocationSpecification);
      }
      boolean isColumnWildcard = "*".equals(columnSpecification);
      boolean isRowWildcard = "*".equals(rowSpecification);
      int columnNumber, rowNumber;

      if (isColumnWildcard) {
        columnNumber = currentCellLocation.getColumnIndex();
      } else {
        columnNumber = SpreadSheetUtil.getColumnNumber(sheet, columnSpecification) - 1;
      }
      if (isRowWildcard) {
        rowNumber = currentCellLocation.getRowNumber();
      } else {
        rowNumber = SpreadSheetUtil.getRowNumber(sheet, rowSpecification) - 1;
      }
      resolvedCellLocation = new CellLocation(sheet.getSheetName(), columnNumber, rowNumber);
    } else {
      throw new RuntimeException("invalid source specification " + cellLocationSpecification);
    }
    return resolvedCellLocation;
  }

  private Workbook getWorkbook()
  {
    return workbook;
  }

}
