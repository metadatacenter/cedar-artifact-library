package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.renderer.ArtifactRenderException;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.util.TerminologyServerClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;

public class YamlSerializer {

  private static ObjectMapper YAML_OBJECT_MAPPER;
  private static ObjectMapper YAML_OBJECT_MAPPER_FULL_QUOTES;

  static {
    // Both writers decide per string whether a quote is needed. Jackson's own answer leaves spellings
    // a reader turns into numbers, and characters a plain scalar cannot hold, unquoted; the full-quotes
    // writer quotes every value but still writes some names plain, so it needs the same answer.
    YAMLFactory yamlFactory = YAMLFactory.builder()
        .stringQuotingChecker(new YamlScalarQuotingChecker())
        .build()
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES) // omit quotes when unambiguous
        // ... but still quote Java Strings whose content looks like a number, otherwise
        // they'd round-trip as numbers and lose the String type (and the field's XSD
        // datatype declaration would no longer be authoritative for numeric defaults).
        .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
        .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
        .disable(YAMLGenerator.Feature.SPLIT_LINES) //enable this
        .disable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);

    YAMLFactory yamlFactoryFullQuotes = YAMLFactory.builder()
        .stringQuotingChecker(new YamlScalarQuotingChecker())
        .build()
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .disable(YAMLGenerator.Feature.MINIMIZE_QUOTES) // This is different
        .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
        .disable(YAMLGenerator.Feature.SPLIT_LINES) //enable this
        .disable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);

    YAML_OBJECT_MAPPER = new ObjectMapper(yamlFactory);
    YAML_OBJECT_MAPPER.registerModule(new Jdk8Module());
    YAML_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, false);

    YAML_OBJECT_MAPPER_FULL_QUOTES = new ObjectMapper(yamlFactoryFullQuotes);
    YAML_OBJECT_MAPPER_FULL_QUOTES.registerModule(new Jdk8Module());
    YAML_OBJECT_MAPPER_FULL_QUOTES.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, false);

    // Register custom serializer module
    SimpleModule module = new SimpleModule();
    module.addSerializer(Double.class, new CustomDoubleSerializer());
    module.addSerializer(Float.class, new CustomFloatSerializer());
    YAML_OBJECT_MAPPER.registerModule(module);
    YAML_OBJECT_MAPPER_FULL_QUOTES.registerModule(module);
  }

  // Custom serializer for Double
  private static class CustomDoubleSerializer extends JsonSerializer<Double> {
    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == value.intValue()) {
        gen.writeNumber(value.intValue());
      } else {
        gen.writeNumber(new BigDecimal(value.toString()).stripTrailingZeros().toPlainString());
      }
    }
  }

  // Custom serializer for Float
  private static class CustomFloatSerializer extends JsonSerializer<Float> {
    @Override
    public void serialize(Float value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value == value.intValue()) {
        gen.writeNumber(value.intValue());
      } else {
        gen.writeNumber(new BigDecimal(value.toString()).stripTrailingZeros().toPlainString());
      }
    }
  }

  public static void saveYAML(Artifact artifact, boolean compactYaml, boolean fullQuotes, Path outputFilePath) {
    saveYAML(artifact, compactYaml, fullQuotes, null, outputFilePath);
  }

  public static String getYAML(Artifact artifact, boolean compactYaml, boolean fullQuotes) {
    return getYAML(artifact, compactYaml, fullQuotes, null);
  }

  public static String getYAML(Artifact artifact, boolean compactYaml, boolean fullQuotes, TerminologyServerClient terminologyServerClient) {
    LinkedHashMap<String, Object> yamlSerialized = getSerializedYaml(artifact, compactYaml, terminologyServerClient);
    try {
      // The writer's escapes are returned as it wrote them. DEL and the C1 controls are not
      // printable characters in YAML, so a document can only carry them escaped: this method used
      // to substitute the characters back for \xNN, \N and \_, which produced a document no parser
      // accepts, this library's own reader included.
      ObjectMapper yamlMapper = fullQuotes ? YAML_OBJECT_MAPPER_FULL_QUOTES : YAML_OBJECT_MAPPER;
      return yamlMapper.writeValueAsString(yamlSerialized);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to serialize " + artifact.getClass().getName() + " as YAML", e);
    }
  }

  public static void saveYAML(Artifact artifact, boolean compactYaml, boolean fullQuotes, TerminologyServerClient terminologyServerClient, Path outputFilePath) {
    String content = getYAML(artifact, compactYaml, fullQuotes, terminologyServerClient);
    try {
      Files.writeString(outputFilePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write YAML artifact to " + outputFilePath.toAbsolutePath(), e);
    }
  }

  private static LinkedHashMap<String, Object> getSerializedYaml(Artifact artifact, boolean compactYaml, TerminologyServerClient terminologyServerClient) {
    YamlArtifactRenderer yamlArtifactRenderer = terminologyServerClient == null ?
        new YamlArtifactRenderer(compactYaml) :
        new YamlArtifactRenderer(compactYaml, terminologyServerClient);

    if (artifact == null)
      throw new ArtifactRenderException("Cannot render a null artifact as YAML");

    LinkedHashMap<String, Object> yamlSerialized;
    if (artifact instanceof FieldSchemaArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderFieldSchemaArtifact((FieldSchemaArtifact) artifact);
    } else if (artifact instanceof ElementSchemaArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderElementSchemaArtifact((ElementSchemaArtifact) artifact);
    } else if (artifact instanceof TemplateSchemaArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderTemplateSchemaArtifact((TemplateSchemaArtifact) artifact);
    } else if (artifact instanceof TemplateInstanceArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderTemplateInstanceArtifact((TemplateInstanceArtifact) artifact);
    } else if (artifact instanceof ElementInstanceArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderElementInstanceArtifact((ElementInstanceArtifact) artifact);
    } else {
      throw new ArtifactRenderException("Unsupported artifact type for YAML rendering: " + artifact.getClass().getName());
    }
    return yamlSerialized;
  }

  public static void outputYAML(Artifact artifact, boolean isCompact, boolean fullQuotes, TerminologyServerClient terminologyServerClient) {
    String content = getYAML(artifact, isCompact, fullQuotes, terminologyServerClient);
    System.out.println(content);
  }
}
