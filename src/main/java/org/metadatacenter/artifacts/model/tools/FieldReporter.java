package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;

import java.io.File;
import java.io.IOException;
public class FieldReporter
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

    ObjectNode fieldObjectNode = (ObjectNode)jsonNode;
    JsonArtifactReader artifactReader = new JsonArtifactReader();

    try {
      FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(fieldObjectNode);
      System.out.println("schema:name: " + fieldSchemaArtifact.name());
    } catch (ArtifactParseException e) {
      System.err.println(
        "Parse error '" + e.getParseErrorMessage() + "' processing field " + e.getFieldKey() + " at path " + e.getPath());
    }
  }

  private static void Usage()
  {
    System.err.println("Usage: " + FieldReporter.class.getName() + " [ <fieldFileName> ]");
    System.exit(1);
  }
}
