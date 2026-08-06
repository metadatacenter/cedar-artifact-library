package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.cli.*;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.ArtifactRenderException;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.util.ConnectionUtil;
import org.metadatacenter.artifacts.util.TerminologyServerClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convert a CEDAR artifact between JSON and YAML.
 * <p>
 * The artifact is named either by a file option ({@code -tsf}, {@code -esf}, {@code -fsf},
 * {@code -tif}) or by an IRI option ({@code -tsi}, {@code -esi}, {@code -fsi}, {@code -tii})
 * naming an artifact on a running CEDAR system.
 * <p>
 * Input and output formats are selected independently: {@code -yif} or {@code -jif} for the
 * input, {@code -yof} or {@code -jof} for the output. All four combinations are supported, so
 * the tool converts in either direction and can also normalize an artifact into its own format.
 * <p>
 * For an artifact retrieved by IRI, the input format selects the {@code Accept} media type of
 * the request, and the Resource Server negotiates its response accordingly.
 */
public class ArtifactConvertor {
  private static final String TEMPLATE_SCHEMA_FILE_OPTION = "tsf";
  private static final String ELEMENT_SCHEMA_FILE_OPTION = "esf";
  private static final String FIELD_SCHEMA_FILE_OPTION = "fsf";
  private static final String TEMPLATE_INSTANCE_FILE_OPTION = "tif";
  private static final String TEMPLATE_SCHEMA_IRI_OPTION = "tsi";
  private static final String ELEMENT_SCHEMA_IRI_OPTION = "esi";
  private static final String FIELD_SCHEMA_IRI_OPTION = "fsi";
  private static final String TEMPLATE_INSTANCE_IRI_OPTION = "tii";
  private static final String YAML_INPUT_FORMAT_OPTION = "yif";
  private static final String JSON_INPUT_FORMAT_OPTION = "jif";
  private static final String YAML_OUTPUT_FORMAT_OPTION = "yof";
  private static final String JSON_OUTPUT_FORMAT_OPTION = "jof";
  private static final String COMPACT_YAML_OPTION = "cy";
  private static final String YAML_FULL_QUOTES = "yq";
  private static final String OUTPUT_FILE_OPTION = "f";
  private static final String CEDAR_RESOURCE_REST_API_BASE_OPTION = "r";
  private static final String CEDAR_TERMINOLOGY_INTEGRATED_SEARCH_REST_API = "t";
  private static final String CEDAR_APIKEY_OPTION = "k";

  private static final String APPLICATION_JSON_MEDIA_TYPE = "application/json";
  private static final String APPLICATION_YAML_MEDIA_TYPE = "application/yaml";

  /**
   * The four artifact types the tool converts, each with the command line options that name an
   * instance of it and the Resource Server path segment under which such instances are served.
   */
  enum ArtifactKind {
    TEMPLATE_SCHEMA(TEMPLATE_SCHEMA_FILE_OPTION, TEMPLATE_SCHEMA_IRI_OPTION, "templates"),
    ELEMENT_SCHEMA(ELEMENT_SCHEMA_FILE_OPTION, ELEMENT_SCHEMA_IRI_OPTION, "template-elements"),
    FIELD_SCHEMA(FIELD_SCHEMA_FILE_OPTION, FIELD_SCHEMA_IRI_OPTION, "template-fields"),
    TEMPLATE_INSTANCE(TEMPLATE_INSTANCE_FILE_OPTION, TEMPLATE_INSTANCE_IRI_OPTION, "template-instances");

    private final String fileOption;
    private final String iriOption;
    private final String resourcePathExtension;

    ArtifactKind(String fileOption, String iriOption, String resourcePathExtension) {
      this.fileOption = fileOption;
      this.iriOption = iriOption;
      this.resourcePathExtension = resourcePathExtension;
    }

    String fileOption() { return fileOption; }

    String iriOption() { return iriOption; }

    String resourcePathExtension() { return resourcePathExtension; }
  }

  private static final Set<String> ARTIFACT_FILE_OPTIONS = Arrays.stream(ArtifactKind.values())
      .map(ArtifactKind::fileOption).collect(Collectors.toUnmodifiableSet());

  private static final Set<String> ARTIFACT_IRI_OPTIONS = Arrays.stream(ArtifactKind.values())
      .map(ArtifactKind::iriOption).collect(Collectors.toUnmodifiableSet());

  private static final Set<String> ARTIFACT_OPTIONS = Arrays.stream(ArtifactKind.values())
      .flatMap(kind -> Arrays.stream(new String[] { kind.fileOption(), kind.iriOption() }))
      .collect(Collectors.toUnmodifiableSet());

  private static final Set<String> INPUT_FORMAT_OPTIONS = Set.of(YAML_INPUT_FORMAT_OPTION, JSON_INPUT_FORMAT_OPTION);

  private static final Set<String> OUTPUT_FORMAT_OPTIONS = Set.of(YAML_OUTPUT_FORMAT_OPTION, JSON_OUTPUT_FORMAT_OPTION);

  private static ObjectWriter PRETTY_OBJECT_WRITER;

  private static final ObjectMapper JSON_INPUT_MAPPER = new ObjectMapper();

  private static final ObjectMapper YAML_INPUT_MAPPER = new ObjectMapper(new YAMLFactory());

  static {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.registerModule(new JavaTimeModule());
    DefaultPrettyPrinter prettyPrinter = new CustomPrettyPrinter();
    PRETTY_OBJECT_WRITER = mapper.writer(prettyPrinter);
  }

  public static void main(String[] args) {
    CommandLineParser parser = new DefaultParser();
    Options options = buildCommandLineOptions();

    try {
      CommandLine command = parser.parse(options, args);

      checkCommandLine(command, options);

      ArtifactKind artifactKind = selectArtifactKind(command);
      boolean yamlInput = command.hasOption(YAML_INPUT_FORMAT_OPTION);
      boolean compactYaml = command.hasOption(COMPACT_YAML_OPTION);

      Artifact artifact = command.hasOption(artifactKind.fileOption()) ?
          readArtifactFromFile(command, artifactKind, yamlInput, compactYaml) :
          readArtifactFromRestApi(command, artifactKind, yamlInput, compactYaml);

      if (command.hasOption(YAML_OUTPUT_FORMAT_OPTION))
        writeYaml(artifact, command, compactYaml);
      else
        writeJson(artifact, artifactKind, command);
    } catch (ParseException e) {
      // The command line itself is wrong, so the option list is the useful thing to show.
      Usage(options, e.getMessage());
    } catch (JacksonException | ArtifactParseException | ArtifactRenderException | ConvertorException |
             UncheckedIOException e) {
      // The command line was well formed but the artifact it names was not, so the option list
      // would only mislead. A JacksonException here is malformed input, most often an input
      // format option that does not match the artifact.
      Fail(e.getMessage());
    } catch (IOException e) {
      Fail("Could not read or write file: " + e.getMessage());
    }
  }

  /**
   * A condition the invoker can correct: an artifact that is not the shape it was said to be, a
   * retrieval that failed, an output that could not be written. Distinct from an unchecked
   * exception escaping the library, which is a defect and keeps its stack trace.
   */
  private static class ConvertorException extends RuntimeException {
    ConvertorException(String message) {
      super(message);
    }
  }

  private static ArtifactKind selectArtifactKind(CommandLine command) {
    for (ArtifactKind artifactKind : ArtifactKind.values())
      if (command.hasOption(artifactKind.fileOption()) || command.hasOption(artifactKind.iriOption()))
        return artifactKind;

    throw new IllegalStateException("No artifact option present on an accepted command line");
  }

  private static Artifact readArtifactFromFile(CommandLine command, ArtifactKind artifactKind, boolean yamlInput,
                                               boolean compactYaml) throws IOException {
    Path artifactFilePath = Paths.get(command.getOptionValue(artifactKind.fileOption()));

    return readArtifact(Files.readString(artifactFilePath), artifactKind, yamlInput, compactYaml);
  }

  private static Artifact readArtifactFromRestApi(CommandLine command, ArtifactKind artifactKind, boolean yamlInput,
                                                  boolean compactYaml) throws IOException {
    String cedarApiKey = command.getOptionValue(CEDAR_APIKEY_OPTION);
    String artifactIri = command.getOptionValue(artifactKind.iriOption());
    String resourceServerBase = command.getOptionValue(CEDAR_RESOURCE_REST_API_BASE_OPTION);
    String requestURL = resourceServerBase + "/" + artifactKind.resourcePathExtension() + "/" + URLEncoder.encode(
        artifactIri, StandardCharsets.UTF_8);
    String acceptMediaType = yamlInput ? APPLICATION_YAML_MEDIA_TYPE : APPLICATION_JSON_MEDIA_TYPE;
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", requestURL, cedarApiKey,
        acceptMediaType);
    int responseCode = connection.getResponseCode();

    if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST)
      throw new ConvertorException("Error retrieving artifact at " + requestURL + ": " + responseCode);

    String artifactSource = ConnectionUtil.readResponseBody(connection.getInputStream());

    return readArtifact(artifactSource, artifactKind, yamlInput, compactYaml);
  }

  static Artifact readArtifact(String artifactSource, ArtifactKind artifactKind, boolean yamlInput,
                               boolean compactYaml) throws IOException {
    if (yamlInput)
      return readArtifact(new YamlArtifactReader(compactYaml), readYamlNode(artifactSource), artifactKind);
    else
      return readArtifact(new JsonArtifactReader(), readJsonNode(artifactSource), artifactKind);
  }

  private static <T> Artifact readArtifact(ArtifactReader<T> artifactReader, T sourceNode, ArtifactKind artifactKind) {
    return switch (artifactKind) {
      case TEMPLATE_SCHEMA -> artifactReader.readTemplateSchemaArtifact(sourceNode);
      case ELEMENT_SCHEMA -> artifactReader.readElementSchemaArtifact(sourceNode);
      case FIELD_SCHEMA -> artifactReader.readFieldSchemaArtifact(sourceNode);
      case TEMPLATE_INSTANCE -> artifactReader.readTemplateInstanceArtifact(sourceNode);
    };
  }

  private static ObjectNode readJsonNode(String artifactSource) throws IOException {
    JsonNode artifactJsonNode = JSON_INPUT_MAPPER.readTree(artifactSource);

    if (!artifactJsonNode.isObject())
      throw new ConvertorException("Expecting JSON object");

    return (ObjectNode) artifactJsonNode;
  }

  private static LinkedHashMap<String, Object> readYamlNode(String artifactSource) throws IOException {
    Object artifactYamlNode = YAML_INPUT_MAPPER.readValue(artifactSource, Object.class);

    if (!(artifactYamlNode instanceof LinkedHashMap<?, ?>))
      throw new ConvertorException("Expecting YAML mapping");

    @SuppressWarnings("unchecked") LinkedHashMap<String, Object> yamlNode =
        (LinkedHashMap<String, Object>) artifactYamlNode;

    return yamlNode;
  }

  private static void writeYaml(Artifact artifact, CommandLine command, boolean compactYaml) {
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

  private static void writeJson(Artifact artifact, ArtifactKind artifactKind, CommandLine command) {
    ObjectNode jsonRendering = renderJson(artifact, artifactKind);

    try {
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
    } catch (IOException e) {
      throw new ConvertorException("Error writing file: " + e.getMessage());
    }
  }

  private static ObjectNode renderJson(Artifact artifact, ArtifactKind artifactKind) {
    JsonArtifactRenderer jsonArtifactRenderer = new JsonArtifactRenderer();

    return switch (artifactKind) {
      case TEMPLATE_SCHEMA -> jsonArtifactRenderer.renderTemplateSchemaArtifact((TemplateSchemaArtifact) artifact);
      case ELEMENT_SCHEMA -> jsonArtifactRenderer.renderElementSchemaArtifact((ElementSchemaArtifact) artifact);
      case FIELD_SCHEMA -> jsonArtifactRenderer.renderFieldSchemaArtifact((FieldSchemaArtifact) artifact);
      case TEMPLATE_INSTANCE -> jsonArtifactRenderer.renderTemplateInstanceArtifact((TemplateInstanceArtifact) artifact);
    };
  }

  private static TerminologyServerClient createTerminologyServerClientIfPossible(CommandLine command) {
    if (command.hasOption(CEDAR_TERMINOLOGY_INTEGRATED_SEARCH_REST_API)) {
      String terminologyServerIntegratedSearchEndpoint = command.getOptionValue(
          CEDAR_TERMINOLOGY_INTEGRATED_SEARCH_REST_API);

      if (!command.hasOption(CEDAR_APIKEY_OPTION)) {
        throw new ConvertorException("no CEDAR API key provided for terminology server");
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

    Option yamlInputFormatOption = Option.builder(YAML_INPUT_FORMAT_OPTION)
        .argName("yaml-input-format")
        .desc("YAML input format")
        .build();

    Option jsonInputFormatOption = Option.builder(JSON_INPUT_FORMAT_OPTION)
        .argName("json-input-format")
        .desc("JSON input format")
        .build();

    Option yamlOutputFormatOption = Option.builder(YAML_OUTPUT_FORMAT_OPTION)
        .argName("yaml-output-format")
        .desc("YAML output format")
        .build();

    Option jsonOutputFormatOption = Option.builder(JSON_OUTPUT_FORMAT_OPTION)
        .argName("json-output-format")
        .desc("JSON output format")
        .build();

    Option compactYamlOption = Option.builder(COMPACT_YAML_OPTION)
        .argName("compact-yaml")
        .desc("Compact YAML, both when reading YAML input and when writing YAML output")
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

    OptionGroup inputFormatGroup = new OptionGroup();
    inputFormatGroup.addOption(yamlInputFormatOption);
    inputFormatGroup.addOption(jsonInputFormatOption);
    inputFormatGroup.setRequired(true);

    options.addOptionGroup(inputFormatGroup);

    OptionGroup outputFormatGroup = new OptionGroup();
    outputFormatGroup.addOption(yamlOutputFormatOption);
    outputFormatGroup.addOption(jsonOutputFormatOption);
    outputFormatGroup.setRequired(true);

    options.addOptionGroup(outputFormatGroup);

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

    if (INPUT_FORMAT_OPTIONS.stream().filter(o -> command.hasOption(o)).count() != 1) {
      Usage(options, "One input format should be specified");
    }

    if (OUTPUT_FORMAT_OPTIONS.stream().filter(o -> command.hasOption(o)).count() != 1) {
      Usage(options, "One output format should be specified");
    }

    if (ARTIFACT_IRI_OPTIONS.stream().anyMatch(o -> command.hasOption(o))) {
      if (!command.hasOption(CEDAR_RESOURCE_REST_API_BASE_OPTION) || !command.hasOption(CEDAR_APIKEY_OPTION)) {
        Usage(options,
            "A Resource Server REST API base and a CEDAR API key must be provided when an artifact IRI option is selected");
      }
    } else if (!ARTIFACT_FILE_OPTIONS.stream().anyMatch(o -> command.hasOption(o))) {
      Usage(options, "Please specify a template file path or a template IRI");
    }
  }

  /** Reports a command line the tool could not act on, and the options it accepts instead. */
  private static void Usage(Options options, String errorMessage) {

    String header = "CEDAR Artifact Convertor Tool";

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(ArtifactConvertor.class.getName(), header, options, errorMessage, true);

    System.exit(-1);
  }

  /**
   * Reports a failure of the conversion itself. The option list is deliberately not printed: the
   * command line was accepted, so showing it would point at the wrong thing.
   */
  private static void Fail(String errorMessage) {
    System.err.println(errorMessage);

    System.exit(-1);
  }

}
