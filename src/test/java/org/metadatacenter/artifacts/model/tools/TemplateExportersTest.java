package org.metadatacenter.artifacts.model.tools;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The hand-run exporters, run as their command lines run them, on a template file. A template with no
 * controlled-term field needs no terminology lookup, so the search endpoint is never contacted.
 */
public class TemplateExportersTest
{
  private static final String UNUSED_ENDPOINT = "http://127.0.0.1:9/terminology";

  private static Path templateFile(Path directory) throws Exception
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/exporters"))
      .withFieldSchema(TextField.builder().withName("Title").withRequiredValue(true).build())
      .withFieldSchema(NumericField.builder().withName("Weight").withNumericType(XsdNumericDatatype.DECIMAL)
        .withDecimalPlaces(2).withUnitOfMeasure("kg").build())
      .build();
    Path file = directory.resolve("template.json");
    Files.writeString(file, new JsonArtifactRenderer().renderTemplateSchemaArtifact(template).toString());
    return file;
  }

  @Test public void template2ExcelWritesAWorkbookWithAColumnPerField(@TempDir Path directory) throws Exception
  {
    Path excel = directory.resolve("study.xlsx");
    Template2Excel.main(new String[] { "-f", templateFile(directory).toString(), "-e", excel.toString(),
      "-s", UNUSED_ENDPOINT, "-k", "unused" });

    try (var workbook = WorkbookFactory.create(excel.toFile())) {
      var header = workbook.getSheetAt(0).getRow(0);
      assertEquals("Title", header.getCell(0).getStringCellValue());
      assertEquals("Weight", header.getCell(1).getStringCellValue());
      assertEquals("0.00\" kg\"", workbook.getSheetAt(0).getColumnStyle(1).getDataFormatString());
    }
  }

  @Test public void template2TsvWritesAHeaderLineWithAColumnPerField(@TempDir Path directory) throws Exception
  {
    Path tsv = directory.resolve("study.tsv");
    Template2Tsv.main(new String[] { "-f", templateFile(directory).toString(), "-t", tsv.toString(),
      "-s", UNUSED_ENDPOINT, "-k", "unused" });

    String text = Files.readString(tsv);
    assertTrue(text.startsWith("Title\tWeight"), text);
  }
}
