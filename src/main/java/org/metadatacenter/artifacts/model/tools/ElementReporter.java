package org.metadatacenter.artifacts.model.tools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.ArtifactReader;

import java.io.File;
import java.io.IOException;

public class ElementReporter
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

    ObjectNode elementObjectNode = (ObjectNode)jsonNode;
    ArtifactReader artifactReader = new ArtifactReader(mapper);

    try {
      ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(elementObjectNode);
      System.out.println("schema:name: " + elementSchemaArtifact.getName());
    } catch (ArtifactParseException e) {
      System.err.println(
        "Parse error '" + e.getParseErrorMessage() + "' processing field " + e.getFieldName() + " at path " + e.getPath());
    }
  }

  private static void Usage()
  {
    System.err.println("Usage: " + ElementReporter.class.getName() + " [ <elementFileName> ]");
    System.exit(1);
  }
}