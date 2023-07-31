package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.ss.ArtifactExcelRenderer;
import org.metadatacenter.artifacts.ss.SpreadSheetUtil;
import org.metadatacenter.artifacts.ss.SpreadsheetFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Template2TSV
{
  public static void main(String[] args) throws IOException
  {
    if (args.length != 4)
      Usage();

    ObjectMapper mapper = new ObjectMapper();
    File templateFile = new File(args[0]);
    File tsvFile = new File(args[1]);
    String terminologyServerIntegratedSearchEndpoint = args[2];
    String terminologyServerAPIKey = args[3];

    JsonNode jsonNode = mapper.readTree(templateFile);

    if (!jsonNode.isObject())
      throw new RuntimeException("Expecting JSON object");

    ObjectNode templateObjectNode = (ObjectNode)jsonNode;

    ArtifactReader artifactReader = new ArtifactReader();
    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

    ArtifactExcelRenderer renderer
      = new ArtifactExcelRenderer(terminologyServerIntegratedSearchEndpoint, terminologyServerAPIKey);

    Workbook workbook = renderer.render(templateSchemaArtifact, 0, 0);

    if (workbook.getNumberOfSheets() == 0)
      throw new RuntimeException("No sheets in generated workbook");

    StringBuffer tsvBuffer = SpreadSheetUtil.convertSheetToTsv(workbook.getSheetAt(0));

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tsvFile))) {
      writer.write(tsvBuffer.toString());
      System.out.println("Successfully generated TSV file " + tsvFile.getAbsolutePath());
    } catch (IOException e) {
      throw new RuntimeException("Error saving TSV file: " + e.getMessage());
    }
  }

  private static void Usage()
  {
    System.err.println("Usage: " + Template2TSV.class.getName() +
      " <templateFileName> <tsvFileName> <terminologyServerIntegratedSearchEndpoint> <terminologyServerAPIKey>");
    System.exit(1);
  }

}