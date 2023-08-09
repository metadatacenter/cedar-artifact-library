package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.model.ModelNodeNames;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ENUM;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.UI;

public class ArtifactReaderTest {
  private ArtifactReader artifactReader;
  private ObjectMapper mapper;

  @Before
  public void setup() {
    artifactReader = new ArtifactReader();
    mapper = new ObjectMapper();

  }

  @Test
  public void testReadSampleBlockTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("SampleBlock.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("HuBMAP Sample Block", templateSchemaArtifact.getName());
  }

  @Test
  public void testReadSampleSectionTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("SampleSection.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("Sample Section", templateSchemaArtifact.getName());
  }

  @Test
  public void testReadSampleSuspensionTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("SampleSuspension.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("HuBMAP Sample Suspension", templateSchemaArtifact.getName());
  }

  @Test
  public void testReadADVANCETemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("ADVANCETemplate.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("ADVANCE metadata template", templateSchemaArtifact.getName());
  }

  @Test
  public void testReadDataCiteTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("DataCiteTemplate.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("DataCite V4.4 without OpenViewUrl field", templateSchemaArtifact.getName());
  }

  @Test
  public void testReadRADxMetadataSpecificationTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("RADxMetadataSpecification.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("RADx Metadata Specification", templateSchemaArtifact.getName());
  }

  @Test
  public void testReadMultiInstanceFieldTemplate()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("MultiInstanceFieldTemplate.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("TemplateWithMultiInstanceField", templateSchemaArtifact.getName());

    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = templateSchemaArtifact.getFieldSchemas();

    assertEquals(fieldSchemas.size(), 1);

    FieldSchemaArtifact fieldSchemaArtifact = fieldSchemas.get("Aliases");
    assertNotNull(fieldSchemaArtifact);

    assertTrue((fieldSchemaArtifact.isMultiple()));
  }

  @Test
  public void testReadTemplateSchemaArtifact()
  {
    ObjectNode objectNode = createBaseTemplateArtifact("Test name", "Test description");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals(templateSchemaArtifact.getName(), "Test name");
    assertEquals(templateSchemaArtifact.getDescription(), "Test description");
    assertEquals(templateSchemaArtifact.getModelVersion(), new Version(1, 6, 0));
    assertNotNull(templateSchemaArtifact.getTemplateUI());
  }

  @Test
  public void testReadElementSchemaArtifact()
  {
    ObjectNode objectNode = createBaseElementArtifact("Test name", "Test description");

    ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(objectNode);

    assertEquals(elementSchemaArtifact.getName(), "Test name");
    assertEquals(elementSchemaArtifact.getDescription(), "Test description");
    assertEquals(elementSchemaArtifact.getModelVersion(), new Version(1, 6, 0));
    assertNotNull(elementSchemaArtifact.getElementUI());
  }

  @Test
  public void testReadFieldSchemaArtifact()
  {
    ObjectNode objectNode = createBaseFieldArtifact("Test name", "Test description");

    objectNode.with(UI).put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD);

    FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(objectNode);

    assertEquals(fieldSchemaArtifact.getName(), "Test name");
    assertEquals(fieldSchemaArtifact.getDescription(), "Test description");
    assertEquals(fieldSchemaArtifact.getModelVersion(), new Version(1, 6, 0));
    assertNotNull(fieldSchemaArtifact.getFieldUI());
  }

  private ObjectNode createBaseTemplateArtifact(String title, String description)
  {
    ObjectNode objectNode = createBaseSchemaArtifact(title, description);

    objectNode.put(JSON_LD_TYPE, ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);

    return objectNode;
  }

  private ObjectNode createBaseElementArtifact(String title, String description)
  {
    ObjectNode objectNode = createBaseSchemaArtifact(title, description);

    objectNode.put(JSON_LD_TYPE, ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI);

    return objectNode;
  }

  private ObjectNode createBaseFieldArtifact(String title, String description)
  {
    ObjectNode objectNode = createBaseSchemaArtifact(title, description);

    objectNode.put(JSON_LD_TYPE, FIELD_SCHEMA_ARTIFACT_TYPE_IRI);

    return objectNode;
  }

  private ObjectNode createBaseSchemaArtifact(String name, String description)
  {
    ObjectNode objectNode = mapper.createObjectNode();

    objectNode.put(JSON_SCHEMA_SCHEMA, ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    objectNode.put(JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    objectNode.put(JSON_SCHEMA_TITLE, "Test JSON Schema title");
    objectNode.put(JSON_SCHEMA_DESCRIPTION, "Test JSON Schema description");
    objectNode.put(SCHEMA_ORG_SCHEMA_VERSION, "1.6.0");
    objectNode.put(SCHEMA_ORG_NAME, name);
    objectNode.put(SCHEMA_ORG_DESCRIPTION, description);
    objectNode.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    objectNode.put(UI, mapper.createObjectNode());

    objectNode.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());

    return objectNode;
  }

  private ObjectNode getFileContentAsObjectNode(String jsonFileName)
  {
    try {
      return (ObjectNode)mapper.readTree(new File(ArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }
}