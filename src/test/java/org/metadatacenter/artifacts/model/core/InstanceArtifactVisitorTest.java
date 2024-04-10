package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReaderTest;
import org.metadatacenter.artifacts.model.visitors.TemplateReporter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    FieldInstanceArtifact textField1 = FieldInstanceArtifact.textFieldInstanceBuilder().withValue("Value 1").build();
    ElementInstanceArtifact element1 = ElementInstanceArtifact.builder().withSingleInstanceFieldInstance(textFieldName1, textField1)
      .build();
    FieldInstanceArtifact textField2 = FieldInstanceArtifact.textFieldInstanceBuilder().withValue("Value 1").build();
    FieldInstanceArtifact attributeValueFieldInstance1 = FieldInstanceArtifact.textFieldInstanceBuilder().withValue("AV Value 1")
      .build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().withName(instanceName)
      .withIsBasedOn(isBasedOn).withSingleInstanceFieldInstance(textFieldName2, textField2)
      .withSingleInstanceElementInstance(element1Name, element1).withAttributeValueFieldGroup(attributeValueFieldName,
        Map.of(attributeValueFieldInstanceName, attributeValueFieldInstance1)).build();

    BasicInstanceReporter instanceReporter = new BasicInstanceReporter();

    templateInstanceArtifact.accept(instanceReporter);

    assertEquals(5, instanceReporter.getInstanceReport().size());
    assertTrue(instanceReporter.getInstanceReport().contains("/"));
    assertTrue(instanceReporter.getInstanceReport().contains("/" + textFieldName2));
    assertTrue(instanceReporter.getInstanceReport().contains("/" + element1Name));
    assertTrue(instanceReporter.getInstanceReport().contains("/" + element1Name + "/" + textFieldName1));
    assertTrue(instanceReporter.getInstanceReport().contains("/" + attributeValueFieldInstanceName));
  }

  @Test
  public void testReadInstanceWithAttributeValues()
  {
    ObjectNode objectNode = getJSONFileContentAsObjectNode("instances/SimpleInstanceWithAttributeValues.json");

    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(objectNode);

    BasicInstanceReporter instanceReporter = new BasicInstanceReporter();

    templateInstanceArtifact.accept(instanceReporter);

    assertEquals("Attribute-Value Field Test metadata", templateInstanceArtifact.name().get());
    assertEquals(5, instanceReporter.getInstanceReport().size());
    assertTrue(instanceReporter.getInstanceReport().contains("/"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 1"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 2"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 3"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 4"));
  }

  @Test
  public void testReadInstanceWithNestedAttributeValues()
  {
    ObjectNode objectNode = getJSONFileContentAsObjectNode("instances/SimpleInstanceWithAttributeValues.json");

    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(objectNode);

    BasicInstanceReporter instanceReporter = new BasicInstanceReporter();

    templateInstanceArtifact.accept(instanceReporter);

    assertEquals("Attribute-Value Field Test metadata", templateInstanceArtifact.name().get());
    assertEquals(5, instanceReporter.getInstanceReport().size());
    assertTrue(instanceReporter.getInstanceReport().contains("/"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 1"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 2"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 3"));
    assertTrue(instanceReporter.getInstanceReport().contains("/Attribute-value instance field 4"));
  }

  @Test
  public void testVisitorsOnRADxMetadata()
  {
    ObjectNode templateObjectNode = getJSONFileContentAsObjectNode("templates/RADxCLIGeneratedTemplate.json");
    ObjectNode instanceObjectNode = getJSONFileContentAsObjectNode("instances/RADxCLIGeneratedInstance.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(templateObjectNode);
    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(instanceObjectNode);

    TemplateReporter templateReporter = new TemplateReporter(templateSchemaArtifact);
    InstanceReporter instanceReporter = new InstanceReporter(templateReporter);

    templateInstanceArtifact.accept(instanceReporter);

    assertEquals("RADxMetadataSpecification", templateSchemaArtifact.name());
    assertEquals("Template Example Metadata Metadata", templateInstanceArtifact.name().get());
    assertTrue(Collections.frequency(instanceReporter.getInstanceReport().keySet(), null) == 0);
    assertTrue(Collections.frequency(instanceReporter.getInstanceReport().values(), null) == 0);
  }

  private class InstanceReporter implements InstanceArtifactVisitor
  {
    private final TemplateReporter templateReporter;
    private final Map<String, SchemaArtifact> instanceReport = new HashMap<>();

    public InstanceReporter(TemplateReporter templateReporter)
    {
      this.templateReporter = templateReporter;
    }

    public Map<String, SchemaArtifact> getInstanceReport()
    {
      return instanceReport;
    }

    @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
    {
      instanceReport.put("/", templateReporter.getTemplateSchema());
    }

    @Override public void visitElementInstanceArtifact(ElementInstanceArtifact childInstanceArtifact, String path)
    {
      instanceReport.put(path, templateReporter.getElementSchema(path).get());
    }

    @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path)
    {
      instanceReport.put(path, templateReporter.getFieldSchema(path).get());
    }

    @Override public void visitAttributeValueFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact,
      String path, String specificationPath)
    {
      instanceReport.put(path, templateReporter.getFieldSchema(specificationPath).get());
    }
  }

  private class BasicInstanceReporter implements InstanceArtifactVisitor
  {
    private List<String> instanceReport = new ArrayList<>();

    public List<String> getInstanceReport()
    {
      return instanceReport;
    }

    @Override public void visitTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
    {
      instanceReport.add("/");
    }

    @Override public void visitElementInstanceArtifact(ElementInstanceArtifact childInstanceArtifact, String path)
    {
      instanceReport.add(path);
    }

    @Override public void visitFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact, String path)
    {
      instanceReport.add(path);
    }

    @Override public void visitAttributeValueFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact,
      String path, String specificationPath)
    {
      instanceReport.add(path);
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