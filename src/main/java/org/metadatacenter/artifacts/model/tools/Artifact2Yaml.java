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
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.util.ConnectionUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class Artifact2Yaml
{
  private static final String TEMPLATE_SCHEMA_FILE_OPTION = "tsf";
  private static final String ELEMENT_SCHEMA_FILE_OPTION = "esf";
  private static final String FIELD_SCHEMA_FILE_OPTION = "fsf";
  private static final String TEMPLATE_SCHEMA_IRI_OPTION = "tsi";
  private static final String ELEMENT_SCHEMA_IRI_OPTION = "esi";
  private static final String FIELD_SCHEMA_IRI_OPTION = "fsi";
  private static final String YAML_FILE_OPTION = "y";
  private static final String YAML_EXPAND_OPTION = "x";
  private static final String CEDAR_RESOURCE_BASE_OPTION = "r";
  private static final String CEDAR_APIKEY_OPTION = "k";

  private static final String TEMPLATE_SCHEMA_RESOURCE_PATH_EXTENSION = "templates";
  private static final String ELEMENT_SCHEMA_RESOURCE_PATH_EXTENSION = "template-elements";
  private static final String FIELD_SCHEMA_RESOURCE_PATH_EXTENSION = "template-fields";

  private static final Set<String> ARTIFACT_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
    TEMPLATE_SCHEMA_FILE_OPTION,
    ELEMENT_SCHEMA_FILE_OPTION,
    FIELD_SCHEMA_FILE_OPTION,
    TEMPLATE_SCHEMA_IRI_OPTION,
    ELEMENT_SCHEMA_IRI_OPTION,
    FIELD_SCHEMA_IRI_OPTION
  )));

  private static final Set<String> ARTIFACT_FILE_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
    TEMPLATE_SCHEMA_FILE_OPTION,
    ELEMENT_SCHEMA_FILE_OPTION,
    FIELD_SCHEMA_FILE_OPTION
  )));

  private static final Set<String> ARTIFACT_IRI_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
    TEMPLATE_SCHEMA_IRI_OPTION,
    ELEMENT_SCHEMA_IRI_OPTION,
    FIELD_SCHEMA_IRI_OPTION
  )));

  public static void main(String[] args) throws IOException
  {
    CommandLineParser parser = new DefaultParser();
    Options options = buildCommandLineOptions();

    try {
      CommandLine command = parser.parse(options, args);

      checkCommandLine(command, options);

      boolean yamlExpand = command.hasOption(YAML_EXPAND_OPTION);

      JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
      YamlArtifactRenderer yamlRenderer = new YamlArtifactRenderer(yamlExpand);
      LinkedHashMap<String, Object> yamlRendering = null;

      if (command.hasOption(TEMPLATE_SCHEMA_FILE_OPTION)) {
        ObjectNode templateObjectNode = readArtifactJsonFromFile(command, TEMPLATE_SCHEMA_FILE_OPTION);
        TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

        yamlRendering = yamlRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
      } else if (command.hasOption(ELEMENT_SCHEMA_FILE_OPTION)) {
        ObjectNode elementObjectNode = readArtifactJsonFromFile(command, ELEMENT_SCHEMA_FILE_OPTION);
        ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(elementObjectNode);

        yamlRendering = yamlRenderer.renderElementSchemaArtifact(elementSchemaArtifact);
      } else if (command.hasOption(FIELD_SCHEMA_FILE_OPTION)) {
        ObjectNode fieldObjectNode = readArtifactJsonFromFile(command, FIELD_SCHEMA_FILE_OPTION);
        FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(fieldObjectNode);

        yamlRendering = yamlRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);
      } else if (command.hasOption(TEMPLATE_SCHEMA_IRI_OPTION)) {
        ObjectNode templateObjectNode =
          readArtifactJsonFromRestApi(command, TEMPLATE_SCHEMA_IRI_OPTION, TEMPLATE_SCHEMA_RESOURCE_PATH_EXTENSION);
        TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

        yamlRendering = yamlRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
      } else if (command.hasOption(ELEMENT_SCHEMA_IRI_OPTION)) {
        ObjectNode elementObjectNode =
          readArtifactJsonFromRestApi(command, ELEMENT_SCHEMA_IRI_OPTION, ELEMENT_SCHEMA_RESOURCE_PATH_EXTENSION);
        ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(elementObjectNode);

        yamlRendering = yamlRenderer.renderElementSchemaArtifact(elementSchemaArtifact);
      } else if (command.hasOption(FIELD_SCHEMA_IRI_OPTION)) {
        ObjectNode fieldObjectNode =
          readArtifactJsonFromRestApi(command, FIELD_SCHEMA_IRI_OPTION, FIELD_SCHEMA_RESOURCE_PATH_EXTENSION);
        FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(fieldObjectNode);

        yamlRendering = yamlRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);
      } else
        Usage(options, "No artifact file or IRI option specified");

      try {

        YAMLFactory yamlFactory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).disable(YAMLGenerator.Feature.SPLIT_LINES);
        ObjectMapper mapper = new ObjectMapper(yamlFactory);

        if (command.hasOption(YAML_FILE_OPTION)) {
          String yamlOutputFileName = command.getOptionValue(YAML_FILE_OPTION);
          File yamlOutputFile = new File(yamlOutputFileName);
          mapper.writeValue(yamlOutputFile, yamlRendering);
          System.out.println("Successfully generated YAML file " + yamlOutputFile.getAbsolutePath());
        } else {
          mapper.writeValue(System.out, yamlRendering);
        }
      } catch (IOException e) {
        throw new RuntimeException("Error writing YAML: " + e.getMessage());
      }
    } catch (ParseException e) {
      Usage(options, e.getMessage());
    }
  }

  private static ObjectNode readArtifactJsonFromRestApi(CommandLine command, String artifactCommandOption,
    String artifactResourcePathExtension) throws IOException
  {
    String cedarApiKey = command.getOptionValue(CEDAR_APIKEY_OPTION);
    String artifactIri = command.getOptionValue(artifactCommandOption);
    String resourceServerBase = command.getOptionValue(CEDAR_RESOURCE_BASE_OPTION);
    String requestURL = resourceServerBase + "/" + artifactResourcePathExtension + "/" +
      URLEncoder.encode(artifactIri, StandardCharsets.UTF_8);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", requestURL, cedarApiKey);
    int responseCode = connection.getResponseCode();

    if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
      throw new RuntimeException("Error retrieving artifact at " + requestURL + ": " + responseCode);

    return ConnectionUtil.readJsonResponseMessage(connection.getInputStream());
  }

  private static ObjectNode readArtifactJsonFromFile(CommandLine command, String artifactCommandOption) throws IOException
  {
    String artifactFileName = command.getOptionValue(artifactCommandOption);
    File artifactFile = new File(artifactFileName);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode artifactJsonNode = mapper.readTree(artifactFile);

    if (!artifactJsonNode.isObject())
      throw new RuntimeException("Expecting JSON object");

    return (ObjectNode)artifactJsonNode;
  }

  private static Options buildCommandLineOptions()
  {
    Options options = new Options();

    Option templateFileOption = Option.builder(TEMPLATE_SCHEMA_FILE_OPTION)
      .argName("template-file")
      .hasArg()
      .desc("Template file")
      .build();

    Option elementFileOption = Option.builder(ELEMENT_SCHEMA_FILE_OPTION)
      .argName("element-file")
      .hasArg()
      .desc("Element file")
      .build();

    Option fieldFileOption = Option.builder(FIELD_SCHEMA_FILE_OPTION)
      .argName("field-file")
      .hasArg()
      .desc("Field file")
      .build();

    Option templateIriOption = Option.builder(TEMPLATE_SCHEMA_IRI_OPTION)
      .argName("template-iri")
      .hasArg()
      .desc("Template IRI")
      .build();

    Option elementIriOption = Option.builder(ELEMENT_SCHEMA_IRI_OPTION)
      .argName("element-iri")
      .hasArg()
      .desc("Element IRI")
      .build();

    Option fieldIriOption = Option.builder(FIELD_SCHEMA_IRI_OPTION)
      .argName("field-iri")
      .hasArg()
      .desc("Field IRI")
      .build();

    Option yamlOption = Option.builder(YAML_FILE_OPTION)
      .argName("yaml-output-file")
      .hasArg()
      .desc("YAML output file")
      .build();

    Option yamlExpandOption = Option.builder(YAML_EXPAND_OPTION)
      .argName("yaml-expand")
      .desc("Expand YAML")
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

    OptionGroup artifactGroup = new OptionGroup();
    artifactGroup.addOption(templateFileOption);
    artifactGroup.addOption(elementFileOption);
    artifactGroup.addOption(fieldFileOption);
    artifactGroup.addOption(templateIriOption);
    artifactGroup.addOption(elementIriOption);
    artifactGroup.addOption(fieldIriOption);
    artifactGroup.setRequired(true);

    options.addOptionGroup(artifactGroup);

    options.addOption(yamlOption);
    options.addOption(yamlExpandOption);
    options.addOption(resourceOption);
    options.addOption(keyOption);

    return options;
  }

  private static void checkCommandLine(CommandLine command, Options options)
  {
    if (ARTIFACT_OPTIONS.stream().filter(o -> command.hasOption(o)).count() != 1)
      Usage(options, "Exactly one artifact option should be specified");

    if (ARTIFACT_FILE_OPTIONS.stream().anyMatch(o -> command.hasOption(o))) {
      if (command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options, "A CEDAR API key should not be provided when an artifact file option is selected");
    } else if (ARTIFACT_IRI_OPTIONS.stream().anyMatch(o -> command.hasOption(o))) {
      if (!command.hasOption(CEDAR_RESOURCE_BASE_OPTION) || !command.hasOption(CEDAR_APIKEY_OPTION))
        Usage(options,
          "A Resource Server REST base and a CEDAR API key must be provided when an artifact IRI option is selected");
    } else
      Usage(options, "Please specify a template file path or a template IRI");
  }

  private static void Usage(Options options, String errorMessage) {

    String header = "CEDAR Artifact to YAML Translation Tool";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Artifact2Yaml.class.getName(), header, options, errorMessage, true);

    System.exit(-1);
  }

}