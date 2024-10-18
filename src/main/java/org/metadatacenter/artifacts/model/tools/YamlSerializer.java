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
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.util.TerminologyServerClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;

public class YamlSerializer {

  private static ObjectMapper YAML_OBJECT_MAPPER;

  static {
    YAMLFactory yamlFactory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        //.enable(YAMLGenerator.Feature.USE_PLATFORM_LINE_BREAKS)
        .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
        .disable(YAMLGenerator.Feature.SPLIT_LINES) //enable this
        .disable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);

    YAML_OBJECT_MAPPER = new ObjectMapper(yamlFactory);
    YAML_OBJECT_MAPPER.registerModule(new Jdk8Module());
    YAML_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, false);

    // Register custom serializer module
    SimpleModule module = new SimpleModule();
    module.addSerializer(Double.class, new CustomDoubleSerializer());
    module.addSerializer(Float.class, new CustomFloatSerializer());
    YAML_OBJECT_MAPPER.registerModule(module);
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

  public static void saveYAML(Artifact artifact, boolean compactYaml, Path outputFilePath) {
    saveYAML(artifact, compactYaml, null, outputFilePath);
  }

  public static String getYAML(Artifact artifact, boolean compactYaml) {
    return getYAML(artifact, compactYaml, null);
  }

  public static String getYAML(Artifact artifact, boolean compactYaml, TerminologyServerClient terminologyServerClient) {
    LinkedHashMap<String, Object> yamlSerialized = getSerializedYaml(artifact, compactYaml, terminologyServerClient);
    try {
      String v = YAML_OBJECT_MAPPER.writeValueAsString(yamlSerialized);
      for (int i = 0x80; i <= 0x9f; i++) {
        String hexString = String.format("\\\\x%02x", i);
        String unicodeString = Character.toString(i);
        v = v.replaceAll(hexString, unicodeString);
      }
      v = v.replaceAll("\\\\N", Character.toString(0x85));
      v = v.replaceAll("\\\\_", "\u00a0");
      return v;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void saveYAML(Artifact artifact, boolean compactYaml, TerminologyServerClient terminologyServerClient, Path outputFilePath) {
    String content = getYAML(artifact, compactYaml, terminologyServerClient);
    try {
      Files.writeString(outputFilePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static LinkedHashMap<String, Object> getSerializedYaml(Artifact artifact, boolean compactYaml, TerminologyServerClient terminologyServerClient) {
    YamlArtifactRenderer yamlArtifactRenderer = terminologyServerClient == null ?
        new YamlArtifactRenderer(compactYaml) :
        new YamlArtifactRenderer(compactYaml, terminologyServerClient);

    LinkedHashMap<String, Object> yamlSerialized = null;
    if (artifact instanceof FieldSchemaArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderFieldSchemaArtifact((FieldSchemaArtifact) artifact);
    } else if (artifact instanceof ElementSchemaArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderElementSchemaArtifact((ElementSchemaArtifact) artifact);
    } else if (artifact instanceof TemplateSchemaArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderTemplateSchemaArtifact((TemplateSchemaArtifact) artifact);
    } else if (artifact instanceof TemplateInstanceArtifact) {
      yamlSerialized = yamlArtifactRenderer.renderTemplateInstanceArtifact((TemplateInstanceArtifact) artifact);
    }
    return yamlSerialized;
  }

  public static void outputYAML(Artifact artifact, boolean isCompact, TerminologyServerClient terminologyServerClient) {
    String content = getYAML(artifact, isCompact, terminologyServerClient);
    System.out.println(content);
  }
}
