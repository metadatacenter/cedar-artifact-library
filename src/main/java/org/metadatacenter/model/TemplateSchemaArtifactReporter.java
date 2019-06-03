package org.metadatacenter.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TemplateSchemaArtifactReporter
{
  public static void main(String[] args) throws IOException
  {
    if (args.length != 1)
      Usage();

    ObjectMapper mapper = new ObjectMapper();
    File templateFile = new File(args[1]);
    JsonNode jsonNode = mapper.readTree(templateFile);


  }

  private static void Usage()
  {
    System.err.println("Usage: " + TemplateSchemaArtifactReporter.class.getName() + " [ <templateFileName> ]");
    System.exit(1);
  }
}
