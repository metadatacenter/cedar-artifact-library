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
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.renderer.UbkgArtifactRenderer;
import org.metadatacenter.artifacts.ubkg.UbkgRendering;
import org.metadatacenter.artifacts.ubkg.UbkgTsvRenderer;
import org.metadatacenter.artifacts.util.ConnectionUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public class Templates2Ubkg
{
  private static final String TEMPLATE_DIRECTORY_OPTION = "d";
  private static final String TEMPLATE_FILE_OPTION = "f";
  private static final String TEMPLATE_IRI_OPTION = "i";
  private static final String UBKG_NODE_FILE_OPTION = "un";
  private static final String UBKG_EDGE_FILE_OPTION = "ue";
  private static final String CEDAR_RESOURCE_BASE_OPTION = "r";
  private static final String CEDAR_APIKEY_OPTION = "k";
  private static final String JSON_FILE_EXTENSION = ".json";

  public static void main(String[] args) throws IOException
  {
    CommandLineParser parser = new DefaultParser();
    Options options = buildCommandLineOptions();

    try {
      CommandLine command = parser.parse(options, args);

      checkCommandLine(command, options);

      String ubkgNodeFilePath = command.getOptionValue(UBKG_NODE_FILE_OPTION);
      String ubkgEdgeFilePath = command.getOptionValue(UBKG_EDGE_FILE_OPTION);
      File ubkgNodeFile = new File(ubkgNodeFilePath);
      File ubkgEdgeFile = new File(ubkgEdgeFilePath);

      List<ObjectNode> templateObjectNodes = new ArrayList<>();

      if (command.hasOption(TEMPLATE_DIRECTORY_OPTION)) {
        String templateDirectoryPath = command.getOptionValue(TEMPLATE_DIRECTORY_OPTION);
        List<ObjectNode> objectNodes = readJsonFromFilesInDirectory(templateDirectoryPath);
        templateObjectNodes.addAll(objectNodes);
      } else if (command.hasOption(TEMPLATE_FILE_OPTION)) {
        String templateFilePath = command.getOptionValue(TEMPLATE_FILE_OPTION);
        ObjectNode templateObjectNode = readJsonFromFile(templateFilePath);
        templateObjectNodes.add(templateObjectNode);
      } else if (command.hasOption(TEMPLATE_IRI_OPTION)) {
        String templateIRI = command.getOptionValue(TEMPLATE_IRI_OPTION);
        String resourceServerBase = command.getOptionValue(CEDAR_RESOURCE_BASE_OPTION);
        String cedarAPIKey = command.getOptionValue(CEDAR_APIKEY_OPTION);
        ObjectNode templateObjectNode = readJsonFromApi(resourceServerBase, templateIRI, cedarAPIKey);
        templateObjectNodes.add(templateObjectNode);
      }

      JsonSchemaArtifactReader jsonSchemaArtifactReader = new JsonSchemaArtifactReader();
      UbkgRendering.Builder ubkgRenderingBuilder = UbkgRendering.builder();
      UbkgArtifactRenderer ubkgRenderer = new UbkgArtifactRenderer(ubkgRenderingBuilder);

      for (ObjectNode templateObjectNode : templateObjectNodes) {
        TemplateSchemaArtifact templateSchemaArtifact = jsonSchemaArtifactReader.readTemplateSchemaArtifact(templateObjectNode);
        ubkgRenderingBuilder = ubkgRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
      }

      UbkgRendering ubkgRendering = ubkgRenderingBuilder.build();
      UbkgTsvRenderer ubkgTsvRenderer = new UbkgTsvRenderer(ubkgRendering);

      writeToFile(ubkgNodeFile, ubkgTsvRenderer.renderNodes());
      writeToFile(ubkgEdgeFile, ubkgTsvRenderer.renderEdges());
    } catch (ParseException e) {
      Usage(options, e.getMessage());
    }
  }

  public static void writeToFile(File file, StringBuffer content) throws IOException {

    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(content.toString());
    } catch (IOException e) {
      throw new RuntimeException("Error writing to file " + file.getName() + ": " + e.getMessage());
    }
  }

  private static ObjectNode readJsonFromApi(String resourceServerBase, String templateIri, String cedarAPIKey) throws IOException
  {
    String requestUrl = resourceServerBase + URLEncoder.encode(templateIri, StandardCharsets.UTF_8);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", requestUrl, cedarAPIKey);
    int responseCode = connection.getResponseCode();

    if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
      throw new RuntimeException("Error retrieving JSON from " + requestUrl + ": " + responseCode);

    ObjectNode objectNode = ConnectionUtil.readJsonResponseMessage(connection.getInputStream());

    return objectNode;
  }

  private static ObjectNode readJsonFromFile(String filePath) throws IOException
  {
    File templateFile = new File(filePath);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(templateFile);

    if (!jsonNode.isObject())
      throw new RuntimeException("Expecting JSON object in file " + filePath);

    return (ObjectNode)jsonNode;
  }

  public static List<ObjectNode> readJsonFromFilesInDirectory(String directoryPath)
  {
    List<ObjectNode> objectNodes = new ArrayList<>();
    File directory = new File(directoryPath);

    if (!directory.isDirectory())
      throw new RuntimeException("Provided path " + directoryPath + " is not a directory");

    File[] files = directory.listFiles();

    if (files == null)
      throw new RuntimeException("No JSON files found in directory " + directoryPath);

    for (File file : files) {
      if (file.isFile() && file.getName().endsWith(JSON_FILE_EXTENSION)) {
        try {
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode jsonNode = objectMapper.readTree(file);
          if (!jsonNode.isObject())
            throw new RuntimeException("Expecting JSON object in file " + file.getName());
          objectNodes.add((ObjectNode)jsonNode);
        } catch (IOException e) {
          throw new RuntimeException("Error reading JSON file " + file.getName() + ": " + e.getMessage());
        }
      }
    }

    return objectNodes;
  }

  private static Options buildCommandLineOptions()
  {
    Options options = new Options();

    Option templateFileOption = Option.builder(TEMPLATE_FILE_OPTION)
      .argName("template-file")
      .hasArg()
      .desc("Template file")
      .build();

    Option templatedirectoryOption = Option.builder(TEMPLATE_DIRECTORY_OPTION)
      .argName("template-dir")
      .hasArg()
      .desc("Template directory")
      .build();

    Option templateIRIOption = Option.builder(TEMPLATE_IRI_OPTION)
      .argName("template-iri")
      .hasArg()
      .desc("Template IRI")
      .build();

    Option ubkgNodeFileOption = Option.builder(UBKG_NODE_FILE_OPTION)
      .argName("ubkg-node-output-file")
      .hasArg()
      .desc("UBKG node output file")
      .required()
      .build();

    Option ubkgEdgeFileOption = Option.builder(UBKG_EDGE_FILE_OPTION)
      .argName("ubkg-edge-output-file")
      .hasArg()
      .desc("UBKG edge output file")
      .required()
      .build();

    Option resourceOption = Option.builder(CEDAR_RESOURCE_BASE_OPTION)
      .argName("cedar-resource-base")
      .hasArg()
      .desc("CEDAR Resource Server base")
      .build();

    Option apiKeyOption = Option.builder(CEDAR_APIKEY_OPTION)
      .argName("cedar-api-key")
      .hasArg()
      .desc("CEDAR API key")
      .build();

    OptionGroup templateGroup = new OptionGroup();
    templateGroup.addOption(templatedirectoryOption);
    templateGroup.addOption(templateFileOption);
    templateGroup.addOption(templateIRIOption);

    options.addOptionGroup(templateGroup);

    options.addOption(ubkgEdgeFileOption);
    options.addOption(ubkgNodeFileOption);
    options.addOption(resourceOption);
    options.addOption(apiKeyOption);

    return options;
  }

  private static void checkCommandLine(CommandLine command, Options options)
  {
    if (command.hasOption(TEMPLATE_DIRECTORY_OPTION) && command.hasOption(TEMPLATE_IRI_OPTION))
      Usage(options, "Both a template directory path and a template IRI cannot be specified together");

    if (command.hasOption(TEMPLATE_DIRECTORY_OPTION) && command.hasOption(TEMPLATE_FILE_OPTION))
      Usage(options, "Both a template directory path and a template file path cannot be specified together");

    if (command.hasOption(TEMPLATE_FILE_OPTION) && command.hasOption(TEMPLATE_IRI_OPTION))
      Usage(options, "Both a template file path and a template IRI cannot be specified together");

    if (!command.hasOption(UBKG_NODE_FILE_OPTION) || !command.hasOption(UBKG_EDGE_FILE_OPTION))
      Usage(options, "UBKG node file path and UBKG edge file path must be provided");

    if (command.hasOption(TEMPLATE_DIRECTORY_OPTION)) {
      if (command.hasOption(CEDAR_RESOURCE_BASE_OPTION) || command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options,
          "Resource server REST base, and CEDAR API key must not be provided when a template directory option is selected");
    } else if (command.hasOption(TEMPLATE_FILE_OPTION)) {
      if (command.hasOption(CEDAR_RESOURCE_BASE_OPTION) || command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options,
          "Resource server REST base, and CEDAR API key must not be provided when a template file option is selected");
    } else if (command.hasOption(TEMPLATE_IRI_OPTION)) {
      if (!command.hasOption(CEDAR_RESOURCE_BASE_OPTION) || !command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options,
          "Resource server REST base, and CEDAR API key must be provided when template IRI option is selected");
    } else
      Usage(options, "Please specify a template directory path, file path, or a template IRI");
  }

  private static void Usage(Options options, String errorMessage) {

    String header = "CEDAR Template to UBKG Translation Tool";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Templates2Ubkg.class.getName(), header, options, errorMessage, true);

    System.exit(-1);
  }

}