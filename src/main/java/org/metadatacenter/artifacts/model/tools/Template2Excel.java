package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.ExcelArtifactRenderer;
import org.metadatacenter.artifacts.ss.SpreadsheetFactory;
import org.metadatacenter.artifacts.util.ConnectionUtil;
import org.metadatacenter.artifacts.util.TerminologyServerClient;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Template2Excel
{
  private static final String TEMPLATE_FILE_OPTION = "f";
  private static final String TEMPLATE_IRI_OPTION = "i";
  private static final String EXCEL_FILE_OPTION = "e";
  private static final String CEDAR_SEARCH_ENDPOINT_OPTION = "s";
  private static final String CEDAR_RESOURCE_BASE_OPTION = "r";
  private static final String CEDAR_APIKEY_OPTION = "k";

  public static void main(String[] args) throws Exception
  {

    CommandLineParser parser = new DefaultParser();
    Options options = buildCommandLineOptions();

    try {
      CommandLine command = parser.parse(options, args);

      checkCommandLine(command, options);

      String terminologyServerIntegratedSearchEndpoint = command.getOptionValue(CEDAR_SEARCH_ENDPOINT_OPTION);
      String cedarAPIKey = command.getOptionValue(CEDAR_APIKEY_OPTION);
      String excelFileName = command.getOptionValue(EXCEL_FILE_OPTION);
      File excelFile = new File(excelFileName);

      ObjectNode templateObjectNode = null;

      if (command.hasOption(TEMPLATE_FILE_OPTION)) {
        String templateFileName = command.getOptionValue(TEMPLATE_FILE_OPTION);
        File templateFile = new File(templateFileName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(templateFile);

        if (!jsonNode.isObject())
          throw new RuntimeException("Expecting JSON object");

        templateObjectNode = (ObjectNode)jsonNode;
      } else if (command.hasOption(TEMPLATE_IRI_OPTION)) {
        String templateIRI = command.getOptionValue(TEMPLATE_IRI_OPTION);
        String resourceServerBase = command.getOptionValue(CEDAR_RESOURCE_BASE_OPTION);
        String requestURL = resourceServerBase + URLEncoder.encode(templateIRI, StandardCharsets.UTF_8);
        HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", requestURL, cedarAPIKey);
        int responseCode = connection.getResponseCode();

        if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
          throw new RuntimeException("Error retrieving template at " + requestURL + ": " + responseCode);

        templateObjectNode = ConnectionUtil.readJsonResponseMessage(connection.getInputStream());
      } else
        Usage(options, "Both a template file path and a template IRI cannot be specified together");

      JsonArtifactReader artifactReader = new JsonArtifactReader();
      TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

      TerminologyServerClient terminologyServerClient = new TerminologyServerClient(
        terminologyServerIntegratedSearchEndpoint, cedarAPIKey);
      ExcelArtifactRenderer renderer = new ExcelArtifactRenderer(terminologyServerClient);

      Workbook workbook = renderer.render(templateSchemaArtifact, 0, 0);

      if (workbook.getNumberOfSheets() == 0)
        throw new RuntimeException("No sheets in generated workbook");

      SpreadsheetFactory.writeWorkbook(workbook, excelFile);

      System.out.println("Successfully generated Excel file " + excelFile.getAbsolutePath());
    } catch (ParseException e) {
      Usage(options, e.getMessage());
    }
  }

  private static Options buildCommandLineOptions()
  {
    Options options = new Options();

    Option templateFileOption = Option.builder(TEMPLATE_FILE_OPTION)
      .argName("template-file")
      .hasArg()
      .desc("Template file")
      .build();

    Option templateIRIOption = Option.builder(TEMPLATE_IRI_OPTION)
      .argName("template-iri")
      .hasArg()
      .desc("Template IRI")
      .build();

    Option excelOption = Option.builder(EXCEL_FILE_OPTION)
      .argName("excel-output-file")
      .hasArg()
      .desc("Excel output file")
      .required()
      .build();

    Option searchOption = Option.builder(CEDAR_SEARCH_ENDPOINT_OPTION)
      .argName("cedar-search-endpoint")
      .hasArg()
      .desc("CEDAR Terminology Server search endpoint")
      .required()
      .build();

    Option resourceOption = Option.builder(CEDAR_RESOURCE_BASE_OPTION)
      .argName("cedar-resource-base")
      .hasArg()
      .desc("CEDAR Resource Server base")
      .build();

    Option keyOption = Option.builder(CEDAR_APIKEY_OPTION)
      .argName("cedar-api-key")
      .hasArg()
      .desc("CEDAR API key")
      .required()
      .build();

    OptionGroup templateGroup = new OptionGroup();
    templateGroup.addOption(templateFileOption);
    templateGroup.addOption(templateIRIOption);

    options.addOptionGroup(templateGroup);

    options.addOption(excelOption);
    options.addOption(searchOption);
    options.addOption(resourceOption);
    options.addOption(keyOption);

    return options;
  }

  private static void checkCommandLine(CommandLine command, Options options)
  {
    if (command.hasOption(TEMPLATE_FILE_OPTION) && command.hasOption(TEMPLATE_IRI_OPTION))
      Usage(options, "Both a template file path and a template IRI cannot be specified together");

    if (command.hasOption(TEMPLATE_FILE_OPTION)) {
      if (!command.hasOption(EXCEL_FILE_OPTION) || !command.hasOption(CEDAR_SEARCH_ENDPOINT_OPTION) || !command.hasOption(
        CEDAR_APIKEY_OPTION))
        Usage(options, "Excel file path, Terminology Server search endpoint, and CEDAR API key must be provided when template file option is selected");
    } else if (command.hasOption(TEMPLATE_IRI_OPTION)) {
      if (!command.hasOption(EXCEL_FILE_OPTION) || !command.hasOption(CEDAR_SEARCH_ENDPOINT_OPTION) || !command.hasOption(
        CEDAR_RESOURCE_BASE_OPTION) || !command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options, "Excel file path, Terminology Server search endpoint, Resource Server REST base, and CEDAR API key must be provided when template IRI option is selected");
    } else
      Usage(options, "Please specify a template file path or a template IRI");
  }

  private static void Usage(Options options, String errorMessage) {

    String header = "CEDAR Template to Excel Translation Tool";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Template2Excel.class.getName(), header, options, errorMessage, true);

    System.exit(-1);
  }
}
