package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.util.ConnectionUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;

public class Template2Ubkg
{
  private static final String TEMPLATE_FILE_OPTION = "f";
  private static final String TEMPLATE_IRI_OPTION = "i";
  private static final String UBKG_NODE_FILE_OPTION = "un";
  private static final String UBKG_EDGE_FILE_OPTION = "ue";
  private static final String CEDAR_RESOURCE_BASE_OPTION = "r";
  private static final String CEDAR_APIKEY_OPTION = "k";

  public static void main(String[] args) throws IOException
  {
    CommandLineParser parser = new DefaultParser();
    Options options = buildCommandLineOptions();

    try {
      CommandLine command = parser.parse(options, args);

      checkCommandLine(command, options);

      String cedarAPIKey = command.getOptionValue(CEDAR_APIKEY_OPTION);
      String ubkgNodeFileName = command.getOptionValue(UBKG_NODE_FILE_OPTION);
      String ubkgEdgeFileName = command.getOptionValue(UBKG_EDGE_FILE_OPTION);
      File ubkgNodeFile = new File(ubkgNodeFileName);
      File ubkgEdgeFile = new File(ubkgEdgeFileName);

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
        String requestURL = resourceServerBase + URLEncoder.encode(templateIRI, "UTF-8");
        HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", requestURL, cedarAPIKey);
        int responseCode = connection.getResponseCode();

        if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
          throw new RuntimeException("Error retrieving template at " + requestURL + ": " + responseCode);

        templateObjectNode = ConnectionUtil.readJsonResponseMessage(connection.getInputStream());
      } else
        Usage(options, "Both a template file path and a template IRI cannot be specified together");

      JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
      TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

      YamlArtifactRenderer yamlRenderer = new YamlArtifactRenderer(true);

      LinkedHashMap<String, Object> yamlRendering = yamlRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

      try {
        YAMLFactory yamlFactory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).disable(YAMLGenerator.Feature.SPLIT_LINES);
        ObjectMapper mapper = new ObjectMapper(yamlFactory);
        mapper.writeValue(ubkgNodeFile, yamlRendering);
        System.out.println("Successfully generated YAML file " + ubkgNodeFile.getAbsolutePath());
      } catch (IOException e) {
        throw new RuntimeException("Error saving YAML file: " + e.getMessage());
      }
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

    Option yamlOption = Option.builder(UBKG_NODE_FILE_OPTION)
      .argName("yaml-output-file")
      .hasArg()
      .desc("YAML output file")
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
      .build();

    OptionGroup templateGroup = new OptionGroup();
    templateGroup.addOption(templateFileOption);
    templateGroup.addOption(templateIRIOption);

    options.addOptionGroup(templateGroup);

    options.addOption(yamlOption);
    options.addOption(resourceOption);
    options.addOption(keyOption);

    return options;
  }

  private static void checkCommandLine(CommandLine command, Options options)
  {
    if (command.hasOption(TEMPLATE_FILE_OPTION) && command.hasOption(TEMPLATE_IRI_OPTION))
      Usage(options, "Both a template file path and a template IRI cannot be specified together");

    if (command.hasOption(TEMPLATE_FILE_OPTION)) {
      if (!command.hasOption(UBKG_NODE_FILE_OPTION) || !command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options, "YAML file path and CEDAR API key must be provided when template file option is selected");
    } else if (command.hasOption(TEMPLATE_IRI_OPTION)) {
      if (!command.hasOption(UBKG_NODE_FILE_OPTION) || !command.hasOption(CEDAR_RESOURCE_BASE_OPTION) || !command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options, "YAML file path, Resource Server REST base, and CEDAR API key must be provided when template IRI option is selected");
    } else
      Usage(options, "Please specify a template file path or a template IRI");
  }

  private static void Usage(Options options, String errorMessage) {

    String header = "CEDAR Template to YAML Translation Tool";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Template2Yaml.class.getName(), header, options, errorMessage, true);

    System.exit(-1);
  }

}