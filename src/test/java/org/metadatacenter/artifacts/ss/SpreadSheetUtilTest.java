package org.metadatacenter.artifacts.ss;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class SpreadSheetUtilTest {
  private static final String EOL = System.lineSeparator();

  @Test void aSheetFlattensToOneLinePerRowWithoutItsHiddenColumns() throws IOException {
    try (var workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Study");
      Row header = sheet.createRow(0);
      String[] names = {"Title", "Secret", "Empty", "Flag", "Sum"};
      for (int i = 0; i < names.length; i++) header.createCell(i).setCellValue(names[i]);
      Row data = sheet.createRow(1);
      data.createCell(0).setCellValue("Lung, left");
      data.createCell(1).setCellValue("hidden value");
      data.createCell(3).setCellValue(true);
      data.createCell(4).setCellFormula("1+1");
      sheet.setColumnHidden(1, true);

      assertEquals("Title\tEmpty\tFlag\tSum" + EOL + "Lung, left\t\ttrue\t1+1" + EOL,
          SpreadSheetUtil.convertSheetToTsv(sheet).toString());
      // Commas inside a value are written as they are.
      assertEquals("Title,Empty,Flag,Sum" + EOL + "Lung, left,,true,1+1" + EOL,
          SpreadSheetUtil.convertSheetToCsv(sheet).toString());
    }
  }

  @Test void aHiddenLastColumnLeavesNoTrailingSeparator() throws IOException {
    try (var workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Study");
      Row header = sheet.createRow(0);
      header.createCell(0).setCellValue("Title");
      header.createCell(1).setCellValue("Section");
      Row data = sheet.createRow(1);
      data.createCell(0).setCellValue("Lung");
      sheet.setColumnHidden(1, true);

      assertEquals("Title" + EOL + "Lung" + EOL, SpreadSheetUtil.convertSheetToTsv(sheet).toString());
    }
  }

  @Test void aCellCommentCarriesTheWrappedTextAndItsAuthor() throws IOException {
    try (var workbook = new XSSFWorkbook()) {
      var cell = workbook.createSheet("Study").createRow(2).createCell(1);
      String text = "word ".repeat(40).trim();

      SpreadSheetUtil.setCellComment(cell, text);

      var comment = cell.getCellComment();
      assertEquals("CEDAR Metadata Validator", comment.getAuthor());
      String[] lines = comment.getString().getString().split("\n");
      assertEquals(3, lines.length);
      for (String line : lines) assertTrue(line.length() <= 80, line);
      assertEquals(text, String.join(" ", lines));
      var anchor = comment.getClientAnchor();
      assertEquals(1, anchor.getCol1());
      assertEquals(2, anchor.getRow1());
      assertEquals(2 + lines.length, anchor.getRow2());
    }
  }

  @Test void columnNamesAndNumbersConvertBothWays() {
    assertEquals(1, SpreadSheetUtil.columnName2Index("A"));
    assertEquals(26, SpreadSheetUtil.columnName2Index("Z"));
    assertEquals(27, SpreadSheetUtil.columnName2Index("AA"));
    assertEquals(702, SpreadSheetUtil.columnName2Index("ZZ"));
    assertEquals(703, SpreadSheetUtil.columnName2Index("AAA"));
    for (int column = 1; column <= 20000; column++)
      assertEquals(column, SpreadSheetUtil.columnName2Index(SpreadSheetUtil.columnNumber2Name(column)));

    for (String invalid : new String[]{"", "a", "A1", "+"})
      assertThrows(RuntimeException.class, () -> SpreadSheetUtil.columnName2Index(invalid), invalid);
    assertEquals(12, SpreadSheetUtil.row2Number("12"));
    for (String invalid : new String[]{"", "x"})
      assertThrows(RuntimeException.class, () -> SpreadSheetUtil.row2Number(invalid), invalid);
  }
}
