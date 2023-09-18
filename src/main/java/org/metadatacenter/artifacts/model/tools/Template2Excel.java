package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.renderer.ExcelArtifactRenderer;
import org.metadatacenter.artifacts.ss.SpreadsheetFactory;

import java.io.File;
import java.io.IOException;

public class Template2Excel
{
  public static void main(String[] args) throws Exception {

    CommandLineParser parser = new DefaultParser();
    Options options = new Options();
    options.addOption("t", "template", true, "Template JSON file");
    options.addOption("e", "excel", true, "Excel output file");
    options.addOption("s", "search-endpoint", true, "Terminology Server Search Endpoint");
    options.addOption("k", "api-key", true, "Terminology Server API Key");

    try {
      CommandLine cmd = parser.parse(options, args);

      if (!(cmd.hasOption("t") && cmd.hasOption("e") && cmd.hasOption("s") && cmd.hasOption("k"))) {
        Usage();
        return;
      }

      String templateFileName = cmd.getOptionValue("t");
      String excelFileName = cmd.getOptionValue("e");
      String terminologyServerIntegratedSearchEndpoint = cmd.getOptionValue("s");
      String terminologyServerAPIKey = cmd.getOptionValue("k");

      ObjectMapper mapper = new ObjectMapper();
      File templateFile = new File(templateFileName);
      File excelFile = new File(excelFileName);

      JsonNode jsonNode = mapper.readTree(templateFile);

      if (!jsonNode.isObject()) {
        throw new RuntimeException("Expecting JSON object");
      }

      ObjectNode templateObjectNode = (ObjectNode) jsonNode;

      JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
      TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

      ExcelArtifactRenderer renderer = new ExcelArtifactRenderer(terminologyServerIntegratedSearchEndpoint, terminologyServerAPIKey);

      Workbook workbook = renderer.render(templateSchemaArtifact, 0, 0);

      if (workbook.getNumberOfSheets() == 0)
        throw new RuntimeException("No sheets in generated workbook");

      SpreadsheetFactory.writeWorkbook(workbook, excelFile);

      System.out.println("Successfully generated Excel file " + excelFile.getAbsolutePath());
    } catch (ParseException e) {
      // Handle parsing errors here
      System.err.println("Command line argument parsing error: " + e.getMessage());
      Usage();
    }
  }

  private static void Usage() {
    System.err.println("Usage: " + Template2Excel.class.getName() + " -t <templateFile> -e <excelFile> -s <searchEndpoint> -k <apiKey>");
  }
}
