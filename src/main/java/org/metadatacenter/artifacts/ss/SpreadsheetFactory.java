package org.metadatacenter.artifacts.ss;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SpreadsheetFactory
{
  public static Workbook createEmptyWorkbook()
  {
    return new XSSFWorkbook(); // An empty workbook
  }

  public static void writeWorkbook(Workbook workbook, File file)
  {
    try {
      OutputStream outputStream = new FileOutputStream(file);
      workbook.write(outputStream);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("error " + e.getMessage() + " opening file " + file.getAbsolutePath());
    } catch (IOException e) {
      throw new RuntimeException("error " + e.getMessage() + " writing file " + file.getAbsolutePath());
    }
  }

  public static Workbook loadWorkbookFromDocument(String path) throws Exception
  {
    return org.apache.poi.ss.usermodel.WorkbookFactory.create(new FileInputStream(path));
  }
}
