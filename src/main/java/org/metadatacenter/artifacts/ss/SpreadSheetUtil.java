package org.metadatacenter.artifacts.ss;

import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SpreadSheetUtil
{
  static public void checkColumnSpecification(String columnSpecification)
  {
    if (columnSpecification.isEmpty())
      throw new RuntimeException("empty column specification");

    if (!columnSpecification.equals("+")) {
      for (int i = 0; i < columnSpecification.length(); i++) {
        char c = columnSpecification.charAt(i);
        if (!isAlpha(c))
          throw new RuntimeException("invalid column specification " + columnSpecification);
      }
    }
  }

  static public void checkRowSpecification(String rowSpecification)
  {
    if (rowSpecification.isEmpty())
      throw new RuntimeException("empty row specification");

    if (!rowSpecification.equals("*")) {
      for (int i = 0; i < rowSpecification.length(); i++) {
        char c = rowSpecification.charAt(i);
        if (!isNumeric(c))
          throw new RuntimeException("invalid row specification " + rowSpecification);
      }
    }
  }

  /**
   * @param columnName
   * @return 1-based column number
   */
  static public int columnName2Index(String columnName)
  {
    int pos = 0;

    checkColumnSpecification(columnName);

    for (int i = 0; i < columnName.length(); i++) {
      pos *= 26;
      try {
        pos += Integer.parseInt(columnName.substring(i, i + 1), 36) - 9;
      } catch (NumberFormatException e) {
        throw new RuntimeException("invalid column name " + columnName);
      }
    }
    return pos;
  }

  /**
   * @param columnNumber 1-based
   * @return
   */
  static public String columnNumber2Name(int columnNumber)
  {
    String col = "";
    while (columnNumber > 0) {
      columnNumber--;
      col = (char)(columnNumber % 26 + 65) + col;
      columnNumber = columnNumber / 26;
    }
    return col;
  }

  /**
   * @param row 1-based
   * @return
   */
  static public int row2Number(String row)
  {
    if (row.isEmpty())
      throw new RuntimeException("empty row number");

    try {
      return Integer.parseInt(row);
    } catch (NumberFormatException e) {
      throw new RuntimeException(row + " is not a valid row number");
    }
  }

  /**
   * @param sheet
   * @param columnSpecification
   * @return 1-based column number
   */
  public static int getColumnNumber(Sheet sheet, String columnSpecification)
  {
    checkColumnSpecification(columnSpecification);
    int columnNumber = columnName2Index(columnSpecification);
    return columnNumber; // 0-indexed
  }

  /**
   * @param sheet
   * @param rowSpecification
   * @return 1-based row number
   */
  public static int getRowNumber(Sheet sheet, String rowSpecification)
  {
    checkRowSpecification(rowSpecification);
    int rowNumber = Integer.parseInt(rowSpecification);
    return rowNumber; // 0-indexed
  }

  public static Sheet getSheet(Workbook workbook, String sheetName)
  {
    Sheet sheet = workbook.getSheet(sheetName);

    if (sheet == null)
      throw new RuntimeException("invalid sheet name " + sheetName);

    return sheet;
  }

  public static void setCellComment(Cell cell, String commentText)
  {
    Drawing drawingPatriarch = cell.getSheet().createDrawingPatriarch();
    CreationHelper creationHelper = cell.getSheet().getWorkbook().getCreationHelper();
    ClientAnchor clientAnchor = creationHelper.createClientAnchor();
    String wrappedText = WordUtils.wrap(commentText, 80);
    int numberOfNewLines = (int)wrappedText.chars().filter(ch -> ch == '\n').count();

    clientAnchor.setCol1(cell.getColumnIndex());
    clientAnchor.setRow1(cell.getRowIndex());
    clientAnchor.setCol2(cell.getColumnIndex() + 3); // TODO Not principled but should work for the moment
    clientAnchor.setRow2(cell.getRowIndex() + numberOfNewLines + 1);

    Comment newComment = drawingPatriarch.createCellComment(clientAnchor);
    newComment.setString(creationHelper.createRichTextString(wrappedText));
    newComment.setAuthor("CEDAR Metadata Validator");
    cell.setCellComment(newComment);
  }

  public static StringBuffer convertSheetToTsv(Sheet sheet)
  {
    return convertSheetToFlatFile(sheet, "\t");
  }

  public static StringBuffer convertSheetToCsv(Sheet sheet)
  {
    return convertSheetToFlatFile(sheet, ",");
  }

  public static StringBuffer convertSheetToFlatFile(Sheet sheet, String separator)
  {
    StringBuffer data = new StringBuffer();
    int rowCount = sheet.getLastRowNum();
    int colCount = sheet.getRow(0).getLastCellNum();

    for (int i = 0; i <= rowCount; i++) {
      Row row = sheet.getRow(i);
      for (int j = 0; j < colCount; j++) {
        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String cellValue = "";

        switch (cell.getCellType()) {
        case STRING:
          cellValue = cell.getStringCellValue();
          break;
        case NUMERIC:
          cellValue = String.valueOf(cell.getNumericCellValue());
          break;
        case BOOLEAN:
          cellValue = String.valueOf(cell.getBooleanCellValue());
          break;
        case FORMULA:
          cellValue = cell.getCellFormula();
          break;
        }

        data.append(cellValue);

        if (j < colCount - 1) {
          data.append(separator);
        }
      }

      data.append(System.lineSeparator());
    }

    return data;
  }

  private static boolean isAlpha(char c) {return c >= 'A' && c <= 'Z';}

  private static boolean isNumeric(char c) {return c >= '0' && c <= '9';}

}
