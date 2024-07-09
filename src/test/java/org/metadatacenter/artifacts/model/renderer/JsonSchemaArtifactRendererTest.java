package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.TextFieldInstance;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonSchemaArtifactReaderTest;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.ModelValidator;
import org.metadatacenter.model.validation.report.ValidationReport;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_IS_BASED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;

public class JsonSchemaArtifactRendererTest
{
  private JsonSchemaArtifactReader artifactReader = new JsonSchemaArtifactReader();
  private JsonSchemaArtifactRenderer jsonSchemaArtifactRenderer;
  private ObjectMapper mapper;
  private ModelValidator cedarModelValidator;

  @Before
  public void setUp() {
    artifactReader = new JsonSchemaArtifactReader();
    jsonSchemaArtifactRenderer = new JsonSchemaArtifactRenderer();
    mapper = new ObjectMapper();
    cedarModelValidator = new CedarValidator();
  }

  @Test
  public void testRenderTemplateSchemaArtifact() {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName("Study").
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), "Study");

    assertTrue(validateTemplateSchemaArtifact(rendering));
  }

  @Test public void testRenderTemporalField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    boolean requiredValue = false;
    TemporalGranularity granularity = TemporalGranularity.DAY;
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.DATE;

    TemporalField temporalField = TemporalField.builder().
            withName(fieldName).
            withDescription(fieldDescription).
            withRequiredValue(requiredValue).
            withTemporalGranularity(granularity).
            withTemporalType(temporalType).
            build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(temporalField);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    //assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).textValue(), defaultURI.toString());
    // CEDAR requires the follow keys to be present, even if the values are null
    assertTrue(rendering.has(PAV_CREATED_BY));
    assertTrue(rendering.has(PAV_CREATED_ON));
    assertTrue(rendering.has(PAV_LAST_UPDATED_ON));
    assertTrue(rendering.has(OSLC_MODIFIED_BY));
    assertTrue(rendering.has(JSON_LD_ID));
    // catch potential error case where "requiredValue": false does not render
    assertFalse(rendering.get("_valueConstraints").get("requiredValue").asBoolean());
    assertEquals(rendering.get("_valueConstraints").get("temporalType").textValue(), temporalType.getText());
    assertEquals(rendering.get("_ui").get("temporalGranularity").textValue(), granularity.getText());

    assertTrue(validateFieldSchemaArtifact(rendering));
  }

  @Test public void testCreateControlledTermField()
  {
    String name = "Field name";
    String description = "Field description";
    URI ontologyUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String ontologyAcronym = "DOID";
    String ontologyName = "Human Disease Ontology";
    URI branchUri = URI.create("http://purl.bioontology.org/ontology/SNOMEDCT/64572001");
    String branchAcronym = "SNOMEDCT";
    String branchName = "Disease";
    String branchSource = "SNOMEDCT";
    Integer branchMaxDepth = 3;
    URI classUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String classSource = "LOINC";
    String classLabel= "Human";
    String classPrefLabel = "Homo Spiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;
    URI valueSetUri = URI.create("https://cadsr.nci.nih.gov/metadata/CADSR-VS/77d61de250089d223d7153a4283e738043a15707");
    String valueSetCollection = "CADSR-VS";
    String valueSetName = "Stable Disease";
    Integer valueSetNumTerms = 1;
    URI actionTermUri = URI.create("http://purl.obolibrary.org/obo/NCBITaxon_51291");
    URI actionSourceUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String actionSource = "DOID";
    ValueType actionValueType = ValueType.ONTOLOGY_CLASS;
    Integer actionTo = 0;
    URI defaultUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String defaultLabel = "Human";

    ControlledTermField controlledTermField = ControlledTermField.builder().
      withName(name).
      withDescription(description).
      withOntologyValueConstraint(ontologyUri, ontologyAcronym, ontologyName).
      withBranchValueConstraint(branchUri, branchSource, branchAcronym, branchName, branchMaxDepth).
      withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).
      withValueSetValueConstraint(valueSetUri, valueSetCollection, valueSetName, valueSetNumTerms).
      withValueConstraintsAction(actionTermUri, actionSource, actionValueType, ValueConstraintsActionType.DELETE,
      actionSourceUri, actionTo).
      withDefaultValue(defaultUri, defaultLabel).
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(controlledTermField);

    assertTrue(validateJsonSchema(rendering));

    assertTrue(validateFieldSchemaArtifact(rendering));
  }

  @Test public void testCreateControlledTermFieldWithClassValueConstraint()
  {
    String name = "Field name";
    String description = "Field description";
    URI classUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String classSource = "LOINC";
    String classLabel= "Human";
    String classPrefLabel = "Homo Spiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;

    ControlledTermField controlledTermField = ControlledTermField.builder().
      withName(name).
      withDescription(description).
      withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(controlledTermField);

    assertTrue(validateJsonSchema(rendering));

    assertTrue(validateFieldSchemaArtifact(rendering));
  }

  // TODO Add defaultValue
  @Test public void testRenderNumericField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    boolean requiredValue = false;
    XsdNumericDatatype numericType = XsdNumericDatatype.DECIMAL;
    int decimalPlaces = 3;

    NumericField numericField = NumericField.builder().
            withName(fieldName).
            withDescription(fieldDescription).
            withRequiredValue(requiredValue).
            withNumericType(numericType).
            withDecimalPlaces(decimalPlaces).
            build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(numericField);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    //assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).textValue(), defaultURI.toString());
    // CEDAR requires the follow keys to be present, even if the values are null
    assertTrue(rendering.has(PAV_CREATED_BY));
    assertTrue(rendering.has(PAV_CREATED_ON));
    assertTrue(rendering.has(PAV_LAST_UPDATED_ON));
    assertTrue(rendering.has(OSLC_MODIFIED_BY));
    assertTrue(rendering.has(JSON_LD_ID));
    // catch potential error case where "requiredValue": false does not render
    assertFalse(rendering.get("_valueConstraints").get("requiredValue").asBoolean());
    assertEquals(rendering.get("_valueConstraints").get("numberType").textValue(), numericType.getText());
    assertEquals(rendering.get("_valueConstraints").get("decimalPlace").asInt(), decimalPlaces);

    assertTrue(validateFieldSchemaArtifact(rendering));
  }

  @Test public void testRenderTextField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    String defaultValue = "default value";
    String language = "en";

    TextField textField = TextField.builder().
      withName(fieldName).
      withDescription(fieldDescription).
      withDefaultValue(defaultValue).
      withLanguage(language).
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(textField);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    //assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).textValue(), defaultURI.toString());
    // CEDAR requires the follow keys to be present, even if the values are null
    assertTrue(rendering.has(PAV_CREATED_BY));
    assertTrue(rendering.has(PAV_CREATED_ON));
    assertTrue(rendering.has(PAV_LAST_UPDATED_ON));
    assertTrue(rendering.has(OSLC_MODIFIED_BY));
    assertTrue(rendering.has(JSON_LD_ID));
    // catch potential error case where "requiredValue": false (default) does not render
    assertFalse(rendering.get("_valueConstraints").get("requiredValue").asBoolean());

    assertTrue(validateFieldSchemaArtifact(rendering));
  }

  @Test public void testRenderStandaloneField()
  {
    String fieldName = "Field name";

    TextValueConstraints textValueConstraints = TextValueConstraints.builder().
      withRequiredValue(true).
      withRecommendedValue(true).
      build();

    FieldUi fieldUi = FieldUi.builder().
      withInputType(FieldInputType.TEXTFIELD).
      withContinuePreviousLine(true).
      withHidden(true).
      withValueRecommendationEnabled(true).
      build();

    TextField textField = TextField.builder().
      withName(fieldName).
      withFieldUi(fieldUi).
      withValueConstraints(textValueConstraints).
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(textField);

    assertTrue(validateJsonSchema(rendering));

    assertFalse(rendering.get("_valueConstraints").get("requiredValue").asBoolean());
    assertEquals(null, rendering.get("_valueConstraints").get("recommendedValue"));
    assertEquals(null, rendering.get("_ui").get("continuePreviousLine"));
    assertEquals(null, rendering.get("_ui").get("hidden"));
    assertEquals(null, rendering.get("_ui").get("valueRecommendationEnabled"));

    assertTrue(validateFieldSchemaArtifact(rendering));
  }

  @Test public void testRenderLinkField()
  {
    String fieldName = "Field name";
    String fieldDescription = "Field description";
    URI defaultIri = URI.create("https://example.com/Study");

    LinkField linkField = LinkField.builder().
      withName(fieldName).
      withDescription(fieldDescription).
      withDefaultValue(defaultIri).
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderFieldSchemaArtifact(linkField);

    assertTrue(validateJsonSchema(rendering));

    assertEquals(rendering.get(JSON_SCHEMA_SCHEMA).textValue(), JSON_SCHEMA_SCHEMA_IRI);
    assertEquals(rendering.get(JSON_SCHEMA_TYPE).textValue(), JSON_SCHEMA_OBJECT);
    assertEquals(rendering.get(JSON_LD_TYPE).textValue(), FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), fieldName);
    assertEquals(rendering.get(SCHEMA_ORG_DESCRIPTION).textValue(), fieldDescription);
    assertEquals(rendering.get(VALUE_CONSTRAINTS).get(VALUE_CONSTRAINTS_DEFAULT_VALUE).textValue(), defaultIri.toString());

    assertTrue(validateFieldSchemaArtifact(rendering));
  }

  @Test
  public void testRenderTemplateInstanceArtifact() {
    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().
      withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/123")).
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template-instances/333")).
      withName("SDY232").
      build();

    ObjectNode rendering = jsonSchemaArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    assertEquals(rendering.get(SCHEMA_ORG_NAME).textValue(), "SDY232");
  }

  @Test
  public void testValidateInstanceAgainstTemplateArtifact() {
    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withName("Study").
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      build();

    ObjectNode templateRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder().
      withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/123")).
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template-instances/333")).
      withName("SDY232").
      build();

    ObjectNode instanceRendering = jsonSchemaArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    assertTrue(validateJsonSchema(templateRendering, instanceRendering));
  }

  @Test
  public void testRenderHuBMAPSampleSection()
  {
    ObjectNode objectNode = getFileContentAsObjectNode("templates/SampleSection.json");

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(objectNode);

    ObjectNode templateRendering = jsonSchemaArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    assertTrue(validateJsonSchema(templateRendering));

    assertTrue(validateTemplateSchemaArtifact(templateRendering));
  }

  @Test
  public void testRenderBasicTemplateInstance()
  {
    String instanceName = "Template 1";
    URI instanceUri = URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = URI.create("https://repo.metadatacenter.org/templates/3232");
    String textField1Name = "Text Field 1";
    String element1Name = "Element 1";
    String textField2Name = "Text Field 2";
    String attributeValueFieldGroupName = "Attribute-value Field A";
    String attributeValueFieldInstanceName1 = "Attribute-value Field Instance 1";
    String attributeValueFieldInstanceName2 = "Attribute-value Field Instance 2";

    FieldInstanceArtifact textField1Instance = TextFieldInstance.builder().withValue("Value 1").build();
    ElementInstanceArtifact element1Instance = ElementInstanceArtifact.builder().withSingleInstanceFieldInstance(textField1Name, textField1Instance).build();
    FieldInstanceArtifact textField2Instance1 = TextFieldInstance.builder().withValue("Value 2").build();
    FieldInstanceArtifact textField2Instance2 = TextFieldInstance.builder().withValue("Value 3").build();
    List<FieldInstanceArtifact> textField2Instances = new ArrayList<>();
    textField2Instances.add(textField2Instance1);
    textField2Instances.add(textField2Instance2);
    FieldInstanceArtifact attributeValueFieldInstance1 = TextFieldInstance.builder().withValue("AV Value 1").build();
    FieldInstanceArtifact attributeValueFieldInstance2 = TextFieldInstance.builder().withValue("AV Value 2").build();
    LinkedHashMap<String, FieldInstanceArtifact> attributeValueFieldInstances = new LinkedHashMap<>();
    attributeValueFieldInstances.put(attributeValueFieldInstanceName1, attributeValueFieldInstance1);
    attributeValueFieldInstances.put(attributeValueFieldInstanceName2, attributeValueFieldInstance2);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
      .withName(instanceName)
      .withJsonLdContextEntry(element1Name, URI.create("https://example.com/p1"))
      .withJsonLdId(instanceUri)
      .withIsBasedOn(isBasedOnTemplateUri)
      .withMultiInstanceFieldInstances(textField2Name, textField2Instances)
      .withSingleInstanceElementInstance(element1Name, element1Instance)
      .withAttributeValueFieldGroup(attributeValueFieldGroupName, attributeValueFieldInstances)
      .build();

    ObjectNode templateInstanceRendering = jsonSchemaArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    assertEquals(instanceName, templateInstanceRendering.get(SCHEMA_ORG_NAME).asText());
    assertEquals(instanceUri, URI.create(templateInstanceRendering.get(JSON_LD_ID).asText()));
    assertEquals(isBasedOnTemplateUri, URI.create(templateInstanceRendering.get(SCHEMA_IS_BASED_ON).asText()));

    // TODO Need more comprehensive testing here
}

  @Test
  public void testRenderBasicElementInstance()
  {
    String instanceName = "Element 1";
    URI instanceUri = URI.create("https://repo.metadatacenter.org/template-element-instances/6643");
    String textField1Name = "Text Field 1";
    String element1Name = "Element 1";
    String textField2Name = "Text Field 2";
    String attributeValueFieldGroupName = "Attribute-value Field A";
    String attributeValueFieldInstanceName1 = "Attribute-value Field Instance 1";
    String attributeValueFieldInstanceName2 = "Attribute-value Field Instance 2";

    FieldInstanceArtifact textField1Instance = TextFieldInstance.builder().withValue("Value 1").build();
    ElementInstanceArtifact element1Instance = ElementInstanceArtifact.builder().withSingleInstanceFieldInstance(textField1Name, textField1Instance).build();
    FieldInstanceArtifact textField2Instance1 = TextFieldInstance.builder().withValue("Value 2").build();
    FieldInstanceArtifact textField2Instance2 = TextFieldInstance.builder().withValue("Value 3").build();
    List<FieldInstanceArtifact> textField2Instances = new ArrayList<>();
    textField2Instances.add(textField2Instance1);
    textField2Instances.add(textField2Instance2);
    FieldInstanceArtifact attributeValueFieldInstance1 = TextFieldInstance.builder().withValue("AV Value 1").build();
    FieldInstanceArtifact attributeValueFieldInstance2 = TextFieldInstance.builder().withValue("AV Value 2").build();
    LinkedHashMap<String, FieldInstanceArtifact> attributeValueFieldInstances = new LinkedHashMap<>();
    attributeValueFieldInstances.put(attributeValueFieldInstanceName1, attributeValueFieldInstance1);
    attributeValueFieldInstances.put(attributeValueFieldInstanceName2, attributeValueFieldInstance2);

    ElementInstanceArtifact elementInstanceArtifact = ElementInstanceArtifact.builder()
      .withName(instanceName)
      .withJsonLdContextEntry(element1Name, URI.create("https://example.com/p1"))
      .withJsonLdId(instanceUri)
      .withMultiInstanceFieldInstances(textField2Name, textField2Instances)
      .withSingleInstanceElementInstance(element1Name, element1Instance)
      .withAttributeValueFieldGroup(attributeValueFieldGroupName, attributeValueFieldInstances)
      .build();

    ObjectNode elementInstanceRendering = jsonSchemaArtifactRenderer.renderElementInstanceArtifact(elementInstanceArtifact);

    assertEquals(instanceName, elementInstanceRendering.get(SCHEMA_ORG_NAME).asText());
    assertEquals(instanceUri, URI.create(elementInstanceRendering.get(JSON_LD_ID).asText()));

    // TODO Need more comprehensive testing here
  }

  @Test
  public void testRenderElementSchemaArtifact() {
    // Similar test for renderElementSchemaArtifact() if needed
  }

  @Test
  public void testRenderFieldSchemaArtifact() {
    // Similar test for renderFieldSchemaArtifact() if needed
  }

  private boolean validateJsonSchema(ObjectNode schemaNode)
  {
    try {
      JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
      return true;
    } catch (ProcessingException e) {
      return false;
    }
  }

  private boolean validateJsonSchema(ObjectNode schemaNode, ObjectNode instanceNode)
  {
    try {
      JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
      schema.validate(instanceNode);
      return true;
    } catch (ProcessingException e) {
      return false;
    }
  }

  private ObjectNode getFileContentAsObjectNode(String jsonFileName)
  {
    try {
      return (ObjectNode)mapper.readTree(new File(
        JsonSchemaArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }

  private boolean validateTemplateSchemaArtifact(ObjectNode schemaNode)
  {
    try {
      ValidationReport validationReport = cedarModelValidator.validateTemplate(schemaNode);
      if (validationReport.getValidationStatus().equals("true"))
        return true;
      else
        return false;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean validateElementSchemaArtifact(ObjectNode schemaNode)
  {
    try {
      ValidationReport validationReport = cedarModelValidator.validateTemplateElement(schemaNode);
      if (validationReport.getValidationStatus().equals("true"))
        return true;
      else
        return false;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean validateFieldSchemaArtifact(ObjectNode schemaNode)
  {
    try {
      ValidationReport validationReport = cedarModelValidator.validateTemplateField(schemaNode);
      if (validationReport.getValidationStatus().equals("true"))
        return true;
      else
        return false;
    } catch (Exception e) {
      return false;
    }
  }

}
