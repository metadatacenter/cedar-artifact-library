package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.cli.*;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.util.ConnectionUtil;
import org.metadatacenter.artifacts.util.TerminologyServerClient;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArtifactConvertor {
  private static final String TEMPLATE_SCHEMA_FILE_OPTION = "tsf";
  private static final String ELEMENT_SCHEMA_FILE_OPTION = "esf";
  private static final String FIELD_SCHEMA_FILE_OPTION = "fsf";
  private static final String TEMPLATE_INSTANCE_FILE_OPTION = "tif";
  private static final String TEMPLATE_SCHEMA_IRI_OPTION = "tsi";
  private static final String ELEMENT_SCHEMA_IRI_OPTION = "esi";
  private static final String FIELD_SCHEMA_IRI_OPTION = "fsi";
  private static final String TEMPLATE_INSTANCE_IRI_OPTION = "tii";
  private static final String YAML_FORMAT_OPTION = "yf";
  private static final String JSON_FORMAT_OPTION = "jf";
  private static final String COMPACT_YAML_OPTION = "cy";
  private static final String YAML_FULL_QUOTES = "yq";
  private static final String OUTPUT_FILE_OPTION = "f";
  private static final String CEDAR_RESOURCE_REST_API_BASE_OPTION = "r";
  private static final String CEDAR_TERMINOLOGY_INTEGRATED_SEARCH_REST_API = "t";
  private static final String CEDAR_APIKEY_OPTION = "k";

  private static final String TEMPLATE_SCHEMA_RESOURCE_PATH_EXTENSION = "templates";
  private static final String ELEMENT_SCHEMA_RESOURCE_PATH_EXTENSION = "template-elements";
  private static final String FIELD_SCHEMA_RESOURCE_PATH_EXTENSION = "template-fields";
  private static final String TEMPLATE_INSTANCE_RESOURCE_PATH_EXTENSION = "template-instances";

  private static final Set<String> ARTIFACT_FILE_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      TEMPLATE_SCHEMA_FILE_OPTION,
      ELEMENT_SCHEMA_FILE_OPTION,
      FIELD_SCHEMA_FILE_OPTION,
      TEMPLATE_INSTANCE_FILE_OPTION
  )));

  private static final Set<String> ARTIFACT_IRI_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      TEMPLATE_SCHEMA_IRI_OPTION,
      ELEMENT_SCHEMA_IRI_OPTION,
      FIELD_SCHEMA_IRI_OPTION,
      TEMPLATE_INSTANCE_IRI_OPTION
  )));

  private static final Set<String> ARTIFACT_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      TEMPLATE_SCHEMA_FILE_OPTION,
      ELEMENT_SCHEMA_FILE_OPTION,
      FIELD_SCHEMA_FILE_OPTION,
      TEMPLATE_INSTANCE_FILE_OPTION,
      TEMPLATE_SCHEMA_IRI_OPTION,
      ELEMENT_SCHEMA_IRI_OPTION,
      FIELD_SCHEMA_IRI_OPTION,
      TEMPLATE_INSTANCE_IRI_OPTION
  )));

  private static final Set<String> FORMAT_OPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      YAML_FORMAT_OPTION, JSON_FORMAT_OPTION
  )));

  private static ObjectWriter PRETTY_OBJECT_WRITER;

  static {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.registerModule(new JavaTimeModule());
    DefaultPrettyPrinter prettyPrinter = new CustomPrettyPrinter();
    PRETTY_OBJECT_WRITER = mapper.writer(prettyPrinter);
  }

  // TODO Clean up.
  public static void main(String[] args) throws IOException {
    CommandLineParser parser = new DefaultParser();
    Options options = buildCommandLineOptions();

    try {
      CommandLine command = parser.parse(options, args);

      checkCommandLine(command, options);

      JsonArtifactReader artifactReader = new JsonArtifactReader();
      JsonArtifactRenderer jsonArtifactRenderer = new JsonArtifactRenderer();
      ObjectNode jsonRendering = null;

      if (command.hasOption(TEMPLATE_SCHEMA_FILE_OPTION)) {
        ObjectNode templateObjectNode = readArtifactJsonFromFile(command, TEMPLATE_SCHEMA_FILE_OPTION);
        TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(templateSchemaArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
        }
      } else if (command.hasOption(ELEMENT_SCHEMA_FILE_OPTION)) {
        ObjectNode elementObjectNode = readArtifactJsonFromFile(command, ELEMENT_SCHEMA_FILE_OPTION);
        ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(elementObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(elementSchemaArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderElementSchemaArtifact(elementSchemaArtifact);
        }
      } else if (command.hasOption(FIELD_SCHEMA_FILE_OPTION)) {
        ObjectNode fieldObjectNode = readArtifactJsonFromFile(command, FIELD_SCHEMA_FILE_OPTION);
        FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(fieldObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(fieldSchemaArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);
        }
      } else if (command.hasOption(TEMPLATE_INSTANCE_FILE_OPTION)) {
        ObjectNode fieldObjectNode = readArtifactJsonFromFile(command, TEMPLATE_INSTANCE_FILE_OPTION);
        TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(
            fieldObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(templateInstanceArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);
        }
      } else if (command.hasOption(TEMPLATE_SCHEMA_IRI_OPTION)) {
        ObjectNode templateObjectNode = readArtifactJsonFromRestApi(command, TEMPLATE_SCHEMA_IRI_OPTION,
            TEMPLATE_SCHEMA_RESOURCE_PATH_EXTENSION);
        TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(templateSchemaArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);
        }
      } else if (command.hasOption(ELEMENT_SCHEMA_IRI_OPTION)) {
        ObjectNode elementObjectNode = readArtifactJsonFromRestApi(command, ELEMENT_SCHEMA_IRI_OPTION,
            ELEMENT_SCHEMA_RESOURCE_PATH_EXTENSION);
        ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(elementObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(elementSchemaArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderElementSchemaArtifact(elementSchemaArtifact);
        }
      } else if (command.hasOption(FIELD_SCHEMA_IRI_OPTION)) {
        ObjectNode fieldObjectNode = readArtifactJsonFromRestApi(command, FIELD_SCHEMA_IRI_OPTION,
            FIELD_SCHEMA_RESOURCE_PATH_EXTENSION);
        FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(fieldObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(fieldSchemaArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);
        }
      } else if (command.hasOption(TEMPLATE_INSTANCE_IRI_OPTION)) {
        ObjectNode fieldObjectNode = readArtifactJsonFromRestApi(command, TEMPLATE_INSTANCE_IRI_OPTION,
            TEMPLATE_INSTANCE_RESOURCE_PATH_EXTENSION);
        TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(
            fieldObjectNode);

        if (command.hasOption(YAML_FORMAT_OPTION)) {
          renderYaml(templateInstanceArtifact, command);
        } else if (command.hasOption(JSON_FORMAT_OPTION)) {
          jsonRendering = jsonArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);
        }
      } else {
        Usage(options, "No artifact file or artifact IRI option specified");
      }

      try {
        if (command.hasOption(JSON_FORMAT_OPTION)) {

          if (command.hasOption(OUTPUT_FILE_OPTION)) {
            String jsonOutputFileName = command.getOptionValue(OUTPUT_FILE_OPTION);
            Path jsonOutputFilePath = Paths.get(jsonOutputFileName);
            PRETTY_OBJECT_WRITER.writeValue(jsonOutputFilePath.toFile(), jsonRendering);
            Files.write(jsonOutputFilePath, "\n".getBytes(), StandardOpenOption.APPEND);
            System.out.println("Successfully generated JSON file at: " + jsonOutputFilePath.toAbsolutePath());
          } else {
            PRETTY_OBJECT_WRITER.writeValue(System.out, jsonRendering);
            System.out.println();
          }
        }
      } catch (IOException e) {
        throw new RuntimeException("Error writing file: " + e.getMessage());
      }
    } catch (ParseException e) {
      Usage(options, e.getMessage());
    }
  }

  private static void renderYaml(Artifact artifact, CommandLine command) {
    boolean compactYaml = command.hasOption(COMPACT_YAML_OPTION);
    TerminologyServerClient terminologyServerClient = createTerminologyServerClientIfPossible(command);

    boolean yamlFullQuotes = command.hasOption(YAML_FULL_QUOTES);

    if (command.hasOption(OUTPUT_FILE_OPTION)) {
      String yamlOutputFileName = command.getOptionValue(OUTPUT_FILE_OPTION);
      Path path = Path.of(yamlOutputFileName);
      YamlSerializer.saveYAML(artifact, compactYaml, yamlFullQuotes, terminologyServerClient, path);
      System.out.println("Successfully generated YAML file " + path.toFile().getAbsolutePath());
    } else {
      YamlSerializer.outputYAML(artifact, compactYaml, yamlFullQuotes, terminologyServerClient);
    }
  }

  private static ObjectNode readArtifactJsonFromRestApi(CommandLine command, String artifactCommandOption,
                                                        String artifactResourcePathExtension) throws IOException {
    String cedarApiKey = command.getOptionValue(CEDAR_APIKEY_OPTION);
    String artifactIri = command.getOptionValue(artifactCommandOption);
    String resourceServerBase = command.getOptionValue(CEDAR_RESOURCE_REST_API_BASE_OPTION);
    String requestURL = resourceServerBase + "/" + artifactResourcePathExtension + "/" + URLEncoder.encode(artifactIri, StandardCharsets.UTF_8);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", requestURL, cedarApiKey);
    int responseCode = connection.getResponseCode();

    if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
      throw new RuntimeException("Error retrieving artifact at " + requestURL + ": " + responseCode);
    }

    return ConnectionUtil.readJsonResponseMessage(connection.getInputStream());
  }

  private static ObjectNode readArtifactJsonFromFile(CommandLine command, String artifactCommandOption) throws IOException {
    String artifactFileName = command.getOptionValue(artifactCommandOption);
    File artifactFile = new File(artifactFileName);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode artifactJsonNode = mapper.readTree(artifactFile);

    if (!artifactJsonNode.isObject()) {
      throw new RuntimeException("Expecting JSON object");
    }

    return (ObjectNode) artifactJsonNode;
  }

  private static TerminologyServerClient createTerminologyServerClientIfPossible(CommandLine command) {
    if (command.hasOption(CEDAR_TERMINOLOGY_INTEGRATED_SEARCH_REST_API)) {
      String terminologyServerIntegratedSearchEndpoint = command.getOptionValue(
          CEDAR_TERMINOLOGY_INTEGRATED_SEARCH_REST_API);

      if (!command.hasOption(CEDAR_APIKEY_OPTION)) {
        throw new RuntimeException("no CEDAR API key provided for terminology server");
      }

      String terminologyServerApiKey = command.getOptionValue(CEDAR_APIKEY_OPTION);

      return new TerminologyServerClient(terminologyServerIntegratedSearchEndpoint, terminologyServerApiKey);
    } else {
      return null;
    }
  }

  private static Options buildCommandLineOptions() {
    Options options = new Options();

    Option templateSchemaFileOption = Option.builder(TEMPLATE_SCHEMA_FILE_OPTION)
        .argName("template-schema-file")
        .hasArg()
        .desc("Template schema file")
        .build();

    Option elementSchemaFileOption = Option.builder(ELEMENT_SCHEMA_FILE_OPTION)
        .argName("element-schema-file")
        .hasArg()
        .desc("Element schema file")
        .build();

    Option fieldSchemaFileOption = Option.builder(FIELD_SCHEMA_FILE_OPTION)
        .argName("field-schema-file")
        .hasArg()
        .desc("Field schema file")
        .build();

    Option templateInstanceFileOption = Option.builder(TEMPLATE_INSTANCE_FILE_OPTION)
        .argName("template-instance-file")
        .hasArg()
        .desc("Template instance file")
        .build();

    Option templateSchemaIriOption = Option.builder(TEMPLATE_SCHEMA_IRI_OPTION)
        .argName("template-schema-iri")
        .hasArg()
        .desc("Template schema IRI")
        .build();

    Option elementSchemaIriOption = Option.builder(ELEMENT_SCHEMA_IRI_OPTION)
        .argName("element-schema-iri")
        .hasArg()
        .desc("Element schema IRI")
        .build();

    Option fieldSchemaIriOption = Option.builder(FIELD_SCHEMA_IRI_OPTION)
        .argName("field-schema-iri")
        .hasArg()
        .desc("Field schema IRI")
        .build();

    Option templateInstanceIriOption = Option.builder(TEMPLATE_INSTANCE_IRI_OPTION)
        .argName("template-instance-iri")
        .hasArg()
        .desc("Template instance IRI")
        .build();

    Option outputFileOption = Option.builder(OUTPUT_FILE_OPTION)
        .argName("output-file")
        .hasArg()
        .desc("output file")
        .build();

    Option yamlFormatOption = Option.builder(YAML_FORMAT_OPTION)
        .argName("yaml-format")
        .desc("YAML format")
        .build();

    Option jsonFormatOption = Option.builder(JSON_FORMAT_OPTION)
        .argName("json-format")
        .desc("JSON format")
        .build();

    Option compactYamlOption = Option.builder(COMPACT_YAML_OPTION)
        .argName("compact-yaml")
        .desc("Compact YAML")
        .build();

    Option yamlFullquotesOption = Option.builder(YAML_FULL_QUOTES)
        .argName("yaml-full-quotes")
        .desc("YAML Full Quotes")
        .build();

    Option resourceOption = Option.builder(CEDAR_RESOURCE_REST_API_BASE_OPTION)
        .argName("cedar-resource-rest-api-base")
        .hasArg()
        .desc("CEDAR Resource Server REST API base, e.g., https://resource.metadatacenter.org")
        .build();

    Option terminologySearchOption = Option.builder(CEDAR_TERMINOLOGY_INTEGRATED_SEARCH_REST_API)
        .argName("cedar-terminology-terminology-integrated-search-rest-api")
        .hasArg()
        .desc("CEDAR Terminology Server REST API, e.g., https://resource.metadatacenter.org")
        .build();

    Option keyOption = Option.builder(CEDAR_APIKEY_OPTION)
        .argName("cedar-api-key")
        .hasArg()
        .desc("CEDAR API key")
        .build();

    OptionGroup artifactGroup = new OptionGroup();
    artifactGroup.addOption(templateSchemaFileOption);
    artifactGroup.addOption(elementSchemaFileOption);
    artifactGroup.addOption(fieldSchemaFileOption);
    artifactGroup.addOption(templateInstanceFileOption);
    artifactGroup.addOption(templateSchemaIriOption);
    artifactGroup.addOption(elementSchemaIriOption);
    artifactGroup.addOption(fieldSchemaIriOption);
    artifactGroup.addOption(templateInstanceIriOption);
    artifactGroup.setRequired(true);

    options.addOptionGroup(artifactGroup);

    OptionGroup formatGroup = new OptionGroup();
    formatGroup.addOption(yamlFormatOption);
    formatGroup.addOption(jsonFormatOption);

    options.addOptionGroup(formatGroup);

    options.addOption(outputFileOption);
    options.addOption(compactYamlOption);
    options.addOption(yamlFullquotesOption);
    options.addOption(resourceOption);
    options.addOption(terminologySearchOption);
    options.addOption(keyOption);

    return options;
  }

  private static void checkCommandLine(CommandLine command, Options options) {
    if (ARTIFACT_OPTIONS.stream().filter(o -> command.hasOption(o)).count() != 1) {
      Usage(options, "One artifact option should be specified");
    }

    if (FORMAT_OPTIONS.stream().filter(o -> command.hasOption(o)).count() != 1) {
      Usage(options, "One output format should be specified");
    }

    if (ARTIFACT_FILE_OPTIONS.stream().anyMatch(o -> command.hasOption(o))) {
    } else if (ARTIFACT_IRI_OPTIONS.stream().anyMatch(o -> command.hasOption(o))) {
      if (!command.hasOption(CEDAR_RESOURCE_REST_API_BASE_OPTION) || !command.hasOption(CEDAR_APIKEY_OPTION)) {
        Usage(options,
            "A Resource Server REST API base and a CEDAR API key must be provided when an artifact IRI option is selected");
      }
    } else {
      Usage(options, "Please specify a template file path or a template IRI");
    }
  }

  private static void Usage(Options options, String errorMessage) {

    String header = "CEDAR Artifact Convertor Tool";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(ArtifactConvertor.class.getName(), header, options, errorMessage, true);

    System.exit(-1);
  }

}
