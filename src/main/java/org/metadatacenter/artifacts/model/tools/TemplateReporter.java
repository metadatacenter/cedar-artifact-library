package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;

import java.io.File;
import java.io.IOException;
public class TemplateReporter
{
  public static void main(String[] args) throws IOException
  {
    if (args.length != 1)
      Usage();

    ObjectMapper mapper = new ObjectMapper();
    File templateFile = new File(args[0]);

    JsonNode jsonNode = mapper.readTree(templateFile);

    if (!jsonNode.isObject())
      throw new RuntimeException("Expecting JSON object");

    ObjectNode templateObjectNode = (ObjectNode)jsonNode;
    ArtifactReader artifactReader = new ArtifactReader(mapper);

    try {
      TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);
      System.out.println("schema:name: " + templateSchemaArtifact.getName());
    } catch (ArtifactParseException e) {
      System.err.println(
        "Parse error '" + e.getParseErrorMessage() + "' processing field " + e.getFieldName() + " at path " + e.getPath());
    }
  }

  private static void Usage()
  {
    System.err.println("Usage: " + TemplateReporter.class.getName() + " [ <templateFileName> ]");
    System.exit(1);
  }
}
