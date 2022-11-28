package org.metadatacenter.ss;

import org.apache.poi.ss.usermodel.Sheet;
import org.metadatacenter.model.core.ElementSchemaArtifact;
import org.metadatacenter.model.core.FieldSchemaArtifact;
import org.metadatacenter.model.core.TemplateSchemaArtifact;

public class ArtifactSpreadsheetRenderer
{

  public void render(TemplateSchemaArtifact templateSchemaArtifact, Sheet sheet,
    int headerStartColumnIndex, int headerRowNumber)
  {
    int columnIndex = headerStartColumnIndex;

    for (String fieldName : templateSchemaArtifact.getFieldNames()) {
      FieldSchemaArtifact fieldSchemaArtifact = templateSchemaArtifact.getFieldSchemaArtifact(fieldName);

      render(fieldSchemaArtifact, sheet, columnIndex, headerRowNumber);

      columnIndex++;
    }

  }

  public void render(ElementSchemaArtifact elementSchemaArtifact, Sheet sheet)
  {

  }

  public void render(FieldSchemaArtifact fieldSchemaArtifact, Sheet sheet, int columnIndex, int rowNumber)
  {

  }

}
