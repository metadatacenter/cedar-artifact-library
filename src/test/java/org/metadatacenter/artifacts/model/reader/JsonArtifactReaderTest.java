package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;

public class JsonArtifactReaderTest
{
  private JsonArtifactReader artifactReader;
  private ObjectMapper mapper;

  @Before public void setup()
  {
    artifactReader = new JsonArtifactReader();
    mapper = new ObjectMapper();
  }

  @Test public void testReadSampleBlockTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/SampleBlock.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("Sample Block", templateSchemaArtifact.name());
  }

  @Test public void testReadSampleSectionTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/SampleSection.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("Sample Section", templateSchemaArtifact.name());
  }

  @Test public void testReadSampleSuspensionTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/SampleSuspension.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("Sample Suspension", templateSchemaArtifact.name());
  }

  @Test public void testReadADVANCETemplateSchemaArtifact()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/ADVANCETemplate.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("ADVANCE metadata template", templateSchemaArtifact.name());
  }

  @Test public void testReadDataCiteTemplateSchemaArtifact()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/DataCiteTemplate.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("DataCite V4.4 without OpenViewUrl field", templateSchemaArtifact.name());
  }

  @Test public void testReadMultiInstanceFieldTemplate()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/MultiInstanceFieldTemplate.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("TemplateWithMultiInstanceField", templateSchemaArtifact.name());

    Map<String, FieldSchemaArtifact> fieldSchemas = templateSchemaArtifact.fieldSchemas();

    assertEquals(fieldSchemas.size(), 1);

    FieldSchemaArtifact fieldSchemaArtifact = fieldSchemas.get("Aliases");
    assertNotNull(fieldSchemaArtifact);

    assertTrue((fieldSchemaArtifact.isMultiple()));
  }

  @Test public void testReadTemplateWithAttributeValues()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/SimpleTemplateWithAttributeValues.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals("Simple Template with Attribute-Value Field", templateSchemaArtifact.name());

    Map<String, FieldSchemaArtifact> fieldSchemas = templateSchemaArtifact.fieldSchemas();

    assertEquals(fieldSchemas.size(), 1);
    FieldSchemaArtifact fieldSchemaArtifact = fieldSchemas.get("Attribute values");
    assertNotNull(fieldSchemaArtifact);

    assertTrue(fieldSchemaArtifact.isMultiple());
    assertTrue(fieldSchemaArtifact.isAttributeValue());
  }

  @Test public void testReadTemplateWithOverrideLabels()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("templates/TemplateWithOverrideLabels.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals(3, templateSchemaArtifact.getUi().propertyLabels().size());
    assertEquals("Hardware", templateSchemaArtifact.getUi().propertyLabels().get("hardware"));
    assertEquals("Hardware1", templateSchemaArtifact.getUi().propertyLabels().get("hardware1"));
    assertEquals("Hardware Component", templateSchemaArtifact.getUi().propertyLabels().get("Hardware Component"));
  }

  @Test public void testReadTemplateSchemaArtifact()
  {
    ObjectNode objectNode = createBaseTemplateSchemaArtifact("Test name", "Test description");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    assertEquals(templateSchemaArtifact.name(), "Test name");
    assertEquals(templateSchemaArtifact.description(), "Test description");
    assertNotNull(templateSchemaArtifact.templateUi());
  }

  @Test public void testReadElementSchemaArtifact()
  {
    ObjectNode objectNode = createBaseElementSchemaArtifact("Test name", "Test description");

    ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(objectNode);

    assertEquals(elementSchemaArtifact.name(), "Test name");
    assertEquals(elementSchemaArtifact.description(), "Test description");
    assertNotNull(elementSchemaArtifact.elementUi());
  }

  @Test public void testReadFieldSchemaArtifact()
  {
    ObjectNode objectNode = createBaseFieldSchemaArtifact("Test name", "Test description");

    objectNode.with(UI).put(UI_FIELD_INPUT_TYPE, FIELD_INPUT_TYPE_TEXTFIELD);

    FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(objectNode);

    assertEquals(fieldSchemaArtifact.name(), "Test name");
    assertEquals(fieldSchemaArtifact.description(), "Test description");
    assertNotNull(fieldSchemaArtifact.fieldUi());
  }

  @Test public void testReadSimpleTemplateInstance()
  {
    String nameFieldName = "Name";
    String controlledTermsFieldName = "Controlled Terms";
    String sizeFieldName = "Size";
    URI brainActivityUri = URI.create(
      "http://www.semanticweb.org/dimitrios/ontologies/2013/2/untitled-ontology-2#BrainActivity");
    String heightFieldName = "Height";

    ObjectNode objectNode = getJsonFileContentAsObjectNode("instances/SimpleInstance.json");

    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(objectNode);

    assertEquals("Simple instance", templateInstanceArtifact.name().get());
    assertEquals(4, templateInstanceArtifact.singleInstanceFieldInstances().size());

    assertNotNull(templateInstanceArtifact.singleInstanceFieldInstances().get(nameFieldName));
    assertEquals("en", templateInstanceArtifact.singleInstanceFieldInstances().get(nameFieldName).language().get());

    assertNotNull(templateInstanceArtifact.singleInstanceFieldInstances().get(controlledTermsFieldName));
    assertEquals("BrainActivity",
      templateInstanceArtifact.singleInstanceFieldInstances().get(controlledTermsFieldName).label().get());
    assertEquals(brainActivityUri,
      templateInstanceArtifact.singleInstanceFieldInstances().get(controlledTermsFieldName).jsonLdId().get());

    assertNotNull(templateInstanceArtifact.singleInstanceFieldInstances().get(sizeFieldName));
    assertEquals("33", templateInstanceArtifact.singleInstanceFieldInstances().get(sizeFieldName).jsonLdValue().get());
    assertEquals(XsdDatatype.INT.toUri(),
      templateInstanceArtifact.singleInstanceFieldInstances().get(sizeFieldName).jsonLdTypes().get(0));

    assertNotNull(templateInstanceArtifact.singleInstanceFieldInstances().get(heightFieldName));
  }

  @Test public void testReadSimpleTemplateInstanceWithNesting()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("instances/SimpleInstanceWithNesting.json");

    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(objectNode);

    assertEquals("Read Instance Test metadata", templateInstanceArtifact.name().get());
    assertEquals(1, templateInstanceArtifact.singleInstanceFieldInstances().size());
    assertEquals(1, templateInstanceArtifact.multiInstanceFieldInstances().size());
    assertEquals(1, templateInstanceArtifact.singleInstanceElementInstances().size());
    assertEquals(1, templateInstanceArtifact.multiInstanceElementInstances().size());
  }

  @Test public void testReadSimpleTemplateInstanceWithAttributeValueField()
  {
    ObjectNode objectNode = getJsonFileContentAsObjectNode("instances/SimpleInstanceWithAttributeValues.json");

    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(objectNode);

    assertEquals("Attribute-Value Field Test metadata", templateInstanceArtifact.name().get());

    assertEquals(2, templateInstanceArtifact.attributeValueFieldInstanceGroups().size());
    assertNotNull(templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field A"));
    assertEquals(2, templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field A").size());
    assertTrue(templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field A")
      .containsKey("Attribute-value instance field 1"));
    assertTrue(templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field A")
      .containsKey("Attribute-value instance field 2"));
    assertNotNull(templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field B"));
    assertEquals(2, templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field B").size());
    assertTrue(templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field B")
      .containsKey("Attribute-value instance field 3"));
    assertTrue(templateInstanceArtifact.attributeValueFieldInstanceGroups().get("Attribute-value field B")
      .containsKey("Attribute-value instance field 4"));
  }

  private ObjectNode createBaseTemplateSchemaArtifact(String title, String description)
  {
    ObjectNode objectNode = createBaseSchemaArtifact(title, description);

    objectNode.put(JSON_LD_CONTEXT, createContextMap(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    objectNode.put(JSON_LD_TYPE, TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);

    return objectNode;
  }

  private ObjectNode createBaseElementSchemaArtifact(String title, String description)
  {
    ObjectNode objectNode = createBaseSchemaArtifact(title, description);

    objectNode.put(JSON_LD_CONTEXT, createContextMap(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    objectNode.put(JSON_LD_TYPE, ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI);

    return objectNode;
  }

  private ObjectNode createBaseFieldSchemaArtifact(String title, String description)
  {
    ObjectNode objectNode = createBaseSchemaArtifact(title, description);

    objectNode.put(JSON_LD_CONTEXT, createContextMap(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    objectNode.put(JSON_LD_TYPE, FIELD_SCHEMA_ARTIFACT_TYPE_IRI);

    return objectNode;
  }

  private ObjectNode createBaseSchemaArtifact(String name, String description)
  {
    ObjectNode objectNode = mapper.createObjectNode();

    objectNode.put(SCHEMA_ORG_SCHEMA_VERSION, "1.6.0");
    objectNode.put(SCHEMA_ORG_NAME, name);
    objectNode.put(SCHEMA_ORG_DESCRIPTION, description);

    objectNode.put(JSON_SCHEMA_SCHEMA, JSON_SCHEMA_SCHEMA_IRI);
    objectNode.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);
    objectNode.put(JSON_SCHEMA_TITLE, "Test JSON Schema title");
    objectNode.put(JSON_SCHEMA_DESCRIPTION, "Test JSON Schema description");
    objectNode.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    objectNode.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    objectNode.put(UI, mapper.createObjectNode());

    return objectNode;
  }

  private ObjectNode createContextMap(Map<String, URI> contextMap)
  {
    ObjectNode objectNode = mapper.createObjectNode();

    for (var entry : contextMap.entrySet())
      objectNode.put(entry.getKey(), entry.getValue().toString());

    return objectNode;
  }

  private ObjectNode getJsonFileContentAsObjectNode(String jsonFileName)
  {
    try {
      JsonNode jsonNode = mapper.readTree(
        new File(JsonArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));

      if (jsonNode.isObject())
        return (ObjectNode)jsonNode;
      else
        throw new RuntimeException("Error reading JSON file " + jsonFileName + ": root node is not an ObjectNode");
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }
}
