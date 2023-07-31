package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.ss.ArtifactExcelRenderer;
import org.metadatacenter.artifacts.ss.SpreadsheetFactory;

import java.io.File;
import java.io.IOException;

public class Template2Excel
{
  public static void main(String[] args) throws IOException
  {
    if (args.length != 4)
      Usage();

    ObjectMapper mapper = new ObjectMapper();
    File templateFile = new File(args[0]);
    File spreadsheetFile = new File(args[1]);
    String terminologyServerIntegratedSearchEndpoint = args[2];
    String terminologyServerAPIKey= args[3];

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

    SpreadsheetFactory.writeWorkbook(workbook, spreadsheetFile);
  }

  private static void Usage()
  {
    System.err.println("Usage: " + Template2Excel.class.getName() +
      " <templateFileName> <spreadsheetFileName> <terminologyServerIntegratedSearchEndpoint> <terminologyServerAPIKey>");
    System.exit(1);
  }
}
