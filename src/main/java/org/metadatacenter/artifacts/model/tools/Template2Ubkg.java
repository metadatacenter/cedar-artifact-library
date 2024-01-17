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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Template2Ubkg
{
  private static final String TEMPLATE_DIRECTORY_OPTION = "d";
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

      List<ObjectNode> templateObjectNodes = new ArrayList<>();

      if (command.hasOption(TEMPLATE_FILE_OPTION)) {
        String templateFileName = command.getOptionValue(TEMPLATE_FILE_OPTION);
        ObjectNode templateObjectNode = readJsonFromFile(templateFileName);
        templateObjectNodes.add(templateObjectNode);
      } else if (command.hasOption(TEMPLATE_IRI_OPTION)) {
        String templateIRI = command.getOptionValue(TEMPLATE_IRI_OPTION);
        String resourceServerBase = command.getOptionValue(CEDAR_RESOURCE_BASE_OPTION);
        String requestURL = resourceServerBase + URLEncoder.encode(templateIRI, StandardCharsets.UTF_8);
        ObjectNode templateObjectNode = readJsonFromApi(requestURL, cedarAPIKey);
        templateObjectNodes.add(templateObjectNode);
      } else
        Usage(options, "Both a template file path and a template IRI cannot be specified together");

      UbkgRendering.Builder ubkgRenderingBuilder = UbkgRendering.builder();

      UbkgArtifactRenderer ubkgRenderer = new UbkgArtifactRenderer(ubkgRenderingBuilder);

      for (ObjectNode templateObjectNode : templateObjectNodes) {
        JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
        TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

        ubkgRenderingBuilder = ubkgRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
      }

      UbkgRendering ubkgRendering = ubkgRenderingBuilder.build();
      UbkgTsvRenderer ubkgTsvRenderer = new UbkgTsvRenderer(ubkgRendering);

      System.out.println("Nodes: \n" + ubkgTsvRenderer.renderNodes());
      System.out.println("Edges: \n" + ubkgTsvRenderer.renderEdges());
    } catch (ParseException e) {
      Usage(options, e.getMessage());
    }
  }

  private static ObjectNode readJsonFromApi(String requestApiUrl, String cedarAPIKey) throws IOException
  {
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", requestApiUrl, cedarAPIKey);
    int responseCode = connection.getResponseCode();

    if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
      throw new RuntimeException("Error retrieving JSON from " + requestApiUrl + ": " + responseCode);

    ObjectNode objectNode = ConnectionUtil.readJsonResponseMessage(connection.getInputStream());

    return objectNode;
  }

  private static ObjectNode readJsonFromFile(String templateFileName) throws IOException
  {
    File templateFile = new File(templateFileName);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(templateFile);

    if (!jsonNode.isObject())
      throw new RuntimeException("Expecting JSON object in file " + templateFileName);

    return (ObjectNode)jsonNode;
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

    Option keyOption = Option.builder(CEDAR_APIKEY_OPTION)
      .argName("cedar-api-key")
      .hasArg()
      .desc("CEDAR API key")
      .build();

    OptionGroup templateGroup = new OptionGroup();
    templateGroup.addOption(templateFileOption);
    templateGroup.addOption(templateIRIOption);

    options.addOptionGroup(templateGroup);

    options.addOption(ubkgEdgeFileOption);
    options.addOption(ubkgNodeFileOption);
    options.addOption(resourceOption);
    options.addOption(keyOption);

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
    formatter.printHelp(Template2Ubkg.class.getName(), header, options, errorMessage, true);

    System.exit(-1);
  }

}