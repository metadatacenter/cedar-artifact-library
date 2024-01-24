package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReaderTest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InstanceArtifactVisitorTest
{
  private JsonSchemaArtifactReader artifactReader;
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    artifactReader = new JsonSchemaArtifactReader();
    mapper = new ObjectMapper();
  }

  @Test public void testVisitor()
  {
    String instanceName = "Template 1";
    String textFieldName1 = "Text Field 1";
    String element1Name = "Element 1";
    String textFieldName2 = "Text Field 2";
    String attributeValueFieldName = "Attribute-value Field A";
    String attributeValueFieldInstanceName = "Attribute-value Field Instance 1";
    URI isBasedOn = URI.create("https://repo.metadatacenter.org/templates/3232");

    FieldInstanceArtifact textField1 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();
    ElementInstanceArtifact element1 = ElementInstanceArtifact.builder().withFieldInstance(textFieldName1, textField1)
      .build();
    FieldInstanceArtifact textField2 = FieldInstanceArtifact.builder().withJsonLdValue("Value 1").build();
    FieldInstanceArtifact attributeValueFieldInstance1 = FieldInstanceArtifact.builder().withJsonLdValue("AV Value 1")
      .build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().withName(instanceName)
      .withIsBasedOn(isBasedOn).withFieldInstance(textFieldName2, textField2)
      .withElementInstance(element1Name, element1).withAttributeValueFieldInstances(attributeValueFieldName,
        Map.of(attributeValueFieldInstanceName, attributeValueFieldInstance1)).build();

    Reporter reporter = new Reporter();

    templateInstanceArtifact.accept(reporter);

    assertEquals(5, reporter.getReport().size());
    assertTrue(reporter.getReport().contains("/"));
    assertTrue(reporter.getReport().contains("/" + textFieldName2));
    assertTrue(reporter.getReport().contains("/" + element1Name));
    assertTrue(reporter.getReport().contains("/" + element1Name + "/" + textFieldName1));
    assertTrue(reporter.getReport().contains("/" + attributeValueFieldInstanceName));
  }

  @Test
  public void testReadInstanceWithNestedAttributeValues()
  {
    ObjectNode objectNode = getJSONFileContentAsObjectNode("instances/InstanceWithNestedAttributeValues.json");

    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(objectNode);

    Reporter reporter = new Reporter();

    templateInstanceArtifact.accept(reporter);

    assertEquals("Nested attribute-value instance", templateInstanceArtifact.name().get());
  }

  private class Reporter implements InstanceArtifactVisitor
  {
    private List<String> report = new ArrayList<>();

    public List<String> getReport()
    {
      return report;
    }

    @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
    {
      report.add("/");
    }

    @Override public void visitElementInstanceArtifact(ElementInstanceArtifact childInstanceArtifact, String path)
    {
      report.add(path);
    }

    @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path)
    {
      report.add(path);
    }

    @Override public void visitAttributeValueFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact,
      String path, String specificationPath)
    {
      report.add(path);
    }

  }

  private ObjectNode getJSONFileContentAsObjectNode(String jsonFileName)
  {
    try {
      JsonNode jsonNode = mapper.readTree(new File(
        JsonSchemaArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));

      if (jsonNode.isObject())
        return (ObjectNode)jsonNode;
      else
        throw new RuntimeException("Error reading JSON file " + jsonFileName + ": root node is not an ObjectNode");
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }

}