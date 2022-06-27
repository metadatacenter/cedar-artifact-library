package org.metadatacenter.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.usermodel.Workbook;
import org.metadatacenter.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.reader.ArtifactReader;
import org.metadatacenter.redcap.TemplateSchemaArtifact2REDCapConvertor;

import java.io.File;
import java.io.IOException;

public class TemplateSchemaArtifactReporter
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
    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);

    System.out.println("Template: " + templateSchemaArtifact);

    TemplateSchemaArtifact2REDCapConvertor convertor = new TemplateSchemaArtifact2REDCapConvertor(templateSchemaArtifact);

    Workbook workbook = convertor.generateREDCapSpreadsheet();

  }

  private static void Usage()
  {
    System.err.println("Usage: " + TemplateSchemaArtifactReporter.class.getName() + " [ <templateFileName> ]");
    System.exit(1);
  }
}
