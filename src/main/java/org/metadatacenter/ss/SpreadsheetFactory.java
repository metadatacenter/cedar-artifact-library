package org.metadatacenter.ss;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

public class SpreadsheetFactory
{
  public static Workbook createEmptyWorkbook()
  {
    return new XSSFWorkbook(); // An empty workbook
  }

  public static Workbook loadWorkbookFromDocument(String path) throws Exception
  {
    return WorkbookFactory.create(new FileInputStream(path));
  }
}
