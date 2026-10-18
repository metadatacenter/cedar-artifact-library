package org.metadatacenter.artifacts.model.renderer;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ExcelArtifactRendererTest {
  @Test void exactDefaultsSurviveWorkbookRoundTrip() throws Exception {
    for (String value : new String[]{"1.5", "0.1", "9223372036854775807", "0.10000000000000001", "1e400"}) {
      var field = NumericField.builder().withName("Number").withNumericType(XsdNumericDatatype.DECIMAL)
          .withDefaultValue(new BigDecimal(value)).build();
      var template = TemplateSchemaArtifact.builder().withName("Numbers").withJsonLdId(java.net.URI.create("urn:test:numbers")).withFieldSchema(field).build();
      try (var workbook = new ExcelArtifactRenderer(null).render(template, 0, 0)) {
        var bytes = new ByteArrayOutputStream();
        workbook.write(bytes);
        try (var reloaded = WorkbookFactory.create(new ByteArrayInputStream(bytes.toByteArray()))) {
          var cell = reloaded.getSheetAt(0).getRow(1).getCell(0);
          if (value.equals("1.5") || value.equals("0.1")) {
            assertEquals(CellType.NUMERIC, cell.getCellType());
            assertEquals(0, new BigDecimal(value).compareTo(BigDecimal.valueOf(cell.getNumericCellValue())));
          } else {
            assertEquals(CellType.STRING, cell.getCellType());
            assertEquals(0, new BigDecimal(value).compareTo(new BigDecimal(cell.getStringCellValue())));
          }
        }
      }
    }
  }
}
