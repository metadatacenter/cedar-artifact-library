package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class Template2Yaml
{
  public static void main(String[] args) throws IOException
  {
    if (args.length != 2)
      Usage();

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
    File templateFile = new File(args[0]);
    File yamlFile = new File(args[1]);

    JsonNode jsonNode = mapper.readTree(templateFile);

    if (!jsonNode.isObject())
      throw new RuntimeException("Expecting JSON object");

    ObjectNode templateObjectNode = (ObjectNode)jsonNode;

    ArtifactReader artifactReader = new ArtifactReader();
    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

    YamlArtifactRenderer yamlRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> yamlRendering = yamlRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    try {
      mapper.writeValue(yamlFile, yamlRendering);
      System.out.println("Successfully generated YAML file " + yamlFile.getAbsolutePath());
    } catch (IOException e) {
      throw new RuntimeException("Error saving YAML file: " + e.getMessage());
    }
  }

  private static void Usage()
  {
    System.err.println("Usage: " + Template2Yaml.class.getName() + " <templateFileName> <yamlFileName>");
    System.exit(1);
  }

}