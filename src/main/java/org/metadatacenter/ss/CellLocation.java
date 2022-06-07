package org.metadatacenter.ss;

public class CellLocation
{
  // There is an equals() method defined on this class.
  private final String sheetName;
  // Column and row number are 0-based
  private final int columnNumber, rowNumber;

  /**
   *
   * @param sheetName The name of the sheet
   * @param columnNumber The column number (0-based)
   * @param rowNumber The row number (0-based)
   */
  public CellLocation(String sheetName, int columnNumber, int rowNumber)
  {
    this.sheetName = sheetName;
    this.columnNumber = columnNumber;
    this.rowNumber = rowNumber;
  }

  public String getSheetName()
  {
    return sheetName;
  }

  /**
   * Get the physical column number as usually presented in a spreadsheet application (0-based)
   *
   * @return The column number (0-based).
   */
  public int getColumnNumber()
  {
    return columnNumber;
  }

  public String getColumnName()
  {
    return SpreadSheetUtil.columnNumber2Name(this.columnNumber + 1);
  }

  /**
   * Get the physical row number as usually presented in a spreadsheet application (0-based).
   *
   * @return The row number (0-based).
   */
  public int getRowNumber()
  {
    return rowNumber;
  }

  /**
   * @return The cell location in format [COLUMN_NAME][ROW_NUMBER], e.g., A1, B3, H24
   */
  public String getCellLocation()
  {
    return getColumnName() + (this.rowNumber + 1);
  }

  public String getFullyQualifiedCellLocation()
  {
    return "'" + getSheetName() + "'!" + getCellLocation();
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    CellLocation that = (CellLocation)o;

    if (columnNumber != that.columnNumber)
      return false;
    if (rowNumber != that.rowNumber)
      return false;
    return !(sheetName != null ? !sheetName.equals(that.sheetName) : that.sheetName != null);

  }

  @Override public int hashCode()
  {
    int result = sheetName != null ? sheetName.hashCode() : 0;
    result = 31 * result + columnNumber;
    result = 31 * result + rowNumber;
    return result;
  }

  public String toString()
  {
    return getFullyQualifiedCellLocation();
  }
}
