package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReaderTest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.*;

public class YamlArtifactRendererTest {

  private JsonArtifactReader artifactReader = new JsonArtifactReader();
  private YAMLFactory yamlFactory;
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    yamlFactory = new YAMLFactory().
        disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).
        enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).
        //enable(YAMLGenerator.Feature.USE_PLATFORM_LINE_BREAKS).
            enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR);

    mapper = new ObjectMapper(yamlFactory);
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, false);
  }

  @Test
  public void testRenderTemplateSchemaArtifact() {
    String name = "Study";
    String description = "Study template";
    String header = "Study header";
    String footer = "Study footer";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
        .withJsonLdId(java.net.URI.create("https://repo.metadatacenter.org/templates/123")).withName(name)
        .withDescription(description).withHeader(header).withFooter(footer).build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(
        templateSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(TYPE, TEMPLATE);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(HEADER, header);
    expectedRendering.put(FOOTER, footer);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderTemplateSchemaArtifactWithMapper() throws JsonProcessingException {
    String name = "Study";
    String description = "Study template";
    String header = "Study header";
    String footer = "Study footer";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder()
        .withJsonLdId(java.net.URI.create("https://repo.metadatacenter.org/templates/123")).withName(name)
        .withDescription(description).withHeader(header).withFooter(footer).build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(
        templateSchemaArtifact);

    String actualStringRendering = mapper.writeValueAsString(actualRendering);

    String expectedStringRendering = """
        type: template
        name: ${name}
        description: ${description}
        header: ${header}
        footer: ${footer}
        """
        .replace("${name}", name)
        .replace("${description}", description)
        .replace("${header}", header)
        .replace("${footer}", footer);

    assertEquals(expectedStringRendering, actualStringRendering);
  }

  @Test
  public void testRenderElementSchemaArtifact() {

    String name = "Address";
    String description = "Address element";

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().
        withJsonLdId(java.net.URI.create("https://repo.metadatacenter.org/template_elements/123")).
        withName(name).
        withDescription(description).
        build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderElementSchemaArtifact(elementSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(TYPE, ELEMENT);
    expectedRendering.put(DESCRIPTION, description);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderTextFieldCompact() throws JsonProcessingException {

    String name = "Study Name";
    String description = "Study name field";

    TextField textField = TextField.builder().
        withJsonLdId(java.net.URI.create("https://repo.metadatacenter.org/template_fields/123")).
        withName(name).
        withDescription(description).
        build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(textField);

    String expectedYamlRendering = """
        type: text-field
        name: ${name}
        description: ${description}
        """
        .replace("${name}", name)
        .replace("${description}", description);

    LinkedHashMap<String, Object> expectedRendering = mapper.readValue(expectedYamlRendering, LinkedHashMap.class);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderTextField() throws JsonProcessingException {
    String name = "Study Name";
    String description = "Study name field";

    TextField textField = TextField.builder().
        withName(name).
        withDescription(description).
        build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(textField);

    String expectedYamlRendering = """
        type: text-field
        name: ${name}
        description: ${description}
        """
        .replace("${name}", name)
        .replace("${description}", description);

    LinkedHashMap<String, Object> expectedRendering = mapper.readValue(expectedYamlRendering, LinkedHashMap.class);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderControlledTermField() {

    String name = "Disease";
    String description = "Disease field";
    URI fieldId = java.net.URI.create("https://repo.metadatacenter.org/template_fields/123");
    URI doidDiseaseBranchIri = java.net.URI.create("http://purl.obolibrary.org/obo/DOID_4");
    String doidSource = "DOID";
    String doidSourceAcronym = "DOID";
    String doidDiseaseBranchName = "Disease";
    Integer doidDiseaseBranchDepth = 0;
    URI pmrDiseaseBranchIri = java.net.URI.create("http://purl.bioontology.org/ontology/PMR.owl#Disease");
    String pmrSource = "Physical Medicine and Rehabilitation (PMR)";
    String pmrSourceAcronym = "PMR";
    String pmrDiseaseBranchName = "Disease";
    Integer pmrDiseaseBranchDepth = 0;
    ValueConstraintsActionType actionType = ValueConstraintsActionType.MOVE;
    URI actionTermUri = java.net.URI.create("http://purl.obolibrary.org/obo/DOID_0040022");
    String actionSourceAcronym = "DOID";
    ValueType actionValueType = ValueType.ONTOLOGY_CLASS;
    URI actionSourceIri = java.net.URI.create("http://purl.obolibrary.org/obo/DOID_4");
    Integer actionTo = 2;

    ControlledTermField controlledTermField = ControlledTermField.builder().
        withJsonLdId(fieldId).
        withName(name).
        withDescription(description).
        withBranchValueConstraint(doidDiseaseBranchIri, doidSource, doidSourceAcronym, doidDiseaseBranchName, 0).
        withBranchValueConstraint(pmrDiseaseBranchIri, pmrSource, pmrSourceAcronym, pmrDiseaseBranchName, 0).
        withValueConstraintsAction(actionTermUri, actionSourceAcronym, actionValueType, actionType, actionSourceIri,
            actionTo).
        build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(name,
        controlledTermField);

    LinkedHashMap<String, Object> expectedBaseFieldRendering = new LinkedHashMap<>();
    expectedBaseFieldRendering.put(KEY, name);
    expectedBaseFieldRendering.put(TYPE, CONTROLLED_TERM_FIELD);
    expectedBaseFieldRendering.put(NAME, name);
    expectedBaseFieldRendering.put(DESCRIPTION, description);
    expectedBaseFieldRendering.put(DATATYPE, IRI);

    List<LinkedHashMap<String, Object>> expectedValueConstraintsRendering = new ArrayList<>();

    LinkedHashMap<String, Object> doidDiseaseBranchRendering = new LinkedHashMap<>();
    doidDiseaseBranchRendering.put(TYPE, BRANCH);
    doidDiseaseBranchRendering.put(ONTOLOGY_NAME, doidSource);
    doidDiseaseBranchRendering.put(ACRONYM, doidSourceAcronym);
    doidDiseaseBranchRendering.put(TERM_LABEL, doidDiseaseBranchName);
    doidDiseaseBranchRendering.put(IRI, doidDiseaseBranchIri);
    doidDiseaseBranchRendering.put(MAX_DEPTH, doidDiseaseBranchDepth);

    expectedValueConstraintsRendering.add(doidDiseaseBranchRendering);

    LinkedHashMap<String, Object> pmrDiseaseBranchRendering = new LinkedHashMap<>();
    pmrDiseaseBranchRendering.put(TYPE, BRANCH);
    pmrDiseaseBranchRendering.put(ONTOLOGY_NAME, pmrSource);
    pmrDiseaseBranchRendering.put(ACRONYM, pmrSourceAcronym);
    pmrDiseaseBranchRendering.put(TERM_LABEL, pmrDiseaseBranchName);
    pmrDiseaseBranchRendering.put(IRI, pmrDiseaseBranchIri);
    pmrDiseaseBranchRendering.put(MAX_DEPTH, pmrDiseaseBranchDepth);

    expectedValueConstraintsRendering.add(pmrDiseaseBranchRendering);

    expectedBaseFieldRendering.put(VALUES, expectedValueConstraintsRendering);

    List<LinkedHashMap<String, Object>> expectedActionsRendering = new ArrayList<>();

    LinkedHashMap<String, Object> actionRendering = new LinkedHashMap<>();
    actionRendering.put(ACTION, actionType.toString());
    actionRendering.put(ACTION_TO, actionTo);
    actionRendering.put(TERM_IRI, actionTermUri);
    actionRendering.put(SOURCE_IRI, actionSourceIri);
    actionRendering.put(SOURCE_ACRONYM, actionSourceAcronym);
    actionRendering.put(TYPE, "class");

    expectedActionsRendering.add(actionRendering);

    expectedBaseFieldRendering.put(ACTIONS, expectedActionsRendering);

    assertEquals(expectedBaseFieldRendering.toString(), actualRendering.toString());
  }

  @Test
  public void testRenderLinkField() {

    String name = "Disease";
    String description = "Disease field";
    URI fieldId = java.net.URI.create("https://repo.metadatacenter.org/template_fields/123");

    LinkField linkField = LinkField.builder().
            withJsonLdId(fieldId).
            withName(name).
            withDescription(description).
            build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(name,
            linkField);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(KEY, name);
    expectedRendering.put(TYPE, LINK_FIELD);
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);

    assertEquals(expectedRendering.toString(), actualRendering.toString());
  }

  @Test
  public void testRenderRorField() {

    String name = "ROR";
    String description = "ROR field";
    URI fieldId = java.net.URI.create("https://repo.metadatacenter.org/template_fields/123");

    RorField rorField = RorField.builder().
            withJsonLdId(fieldId).
            withName(name).
            withDescription(description).
            build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(name,
            rorField);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(KEY, name);
    expectedRendering.put(TYPE, ROR_FIELD);
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);

    assertEquals(expectedRendering.toString(), actualRendering.toString());
  }

  @Test
  public void testRenderOrcidField() {

    String name = "ORCID";
    String description = "ORCID field";
    URI fieldId = java.net.URI.create("https://repo.metadatacenter.org/template_fields/123");

    OrcidField orcidField = OrcidField.builder().
            withJsonLdId(fieldId).
            withName(name).
            withDescription(description).
            build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(name,
            orcidField);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(KEY, name);
    expectedRendering.put(TYPE, ORCID_FIELD);
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);

    assertEquals(expectedRendering.toString(), actualRendering.toString());
  }

  @Test
  public void testRenderAnnotations() {

    String name = "Study";
    String description = "Study template";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
        withJsonLdId(java.net.URI.create("https://repo.metadatacenter.org/templates/123")).
        withName(name).
        withDescription(description).
        build();
  }

  @Test
  public void testCreateControlledTermFieldWithClassValueConstraint() throws JsonProcessingException {
    String fieldKey = "Field key";
    String description = "Field description";
    URI classUri = java.net.URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String classSource = "LOINC";
    String classLabel = "Human";
    String classPrefLabel = "Homo Sapiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;
    String expectedYaml = """
            type: controlled-term-field
            name: ${fieldName}
            description: ${description}
            datatype: iri
            values:
              - type: class
                label: ${classLabel}
                acronym: ${classSource}
                termType: class
                termLabel: ${classPrefLabel}
                iri: ${classUri}
        """
        .replace("${fieldName}", fieldKey)
        .replace("${description}", description)
        .replace("${classValueType}", classValueType.toString())
        .replace("${classLabel}", classLabel)
        .replace("${classSource}", classSource)
        .replace("${classPrefLabel}", classPrefLabel)
        .replace("${classUri}", classUri.toString());

    ControlledTermField controlledTermField =
        ControlledTermField.builder().withName(fieldKey).withDescription(description)
            .withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(controlledTermField);

    LinkedHashMap<String, Object> expectedRendering = mapper.readValue(expectedYaml, LinkedHashMap.class);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderSimpleInstance() throws JsonProcessingException {
    String expectedYaml = """
        type: instance
        name: Simple instance
        isBasedOn: https://repo.metadatacenter.org/templates/5c48700a-4163-436d-8daa-95af7311cded
        children:
          Controlled Terms:
            id: http://www.semanticweb.org/dimitrios/ontologies/2013/2/untitled-ontology-2#BrainActivity
            label: BrainActivity
          Size:
            datatype: xsd:int
            value: 33
          Name:
            value: Bobby
            language: en
        """;

    ObjectNode objectNode = getFileContentAsObjectNode("instances/SimpleInstance.json");

    TemplateInstanceArtifact templateInstanceArtifact = artifactReader.readTemplateInstanceArtifact(objectNode);

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);

    assertEquals(expectedYaml, actualYaml);
  }

  @Test
  public void testRenderTemplateInstanceWithSingleInstanceTextField() throws JsonProcessingException {
    String instanceName = "Instance 1";
    URI instanceUri = java.net.URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = java.net.URI.create("https://repo.metadatacenter.org/templates/3232");
    String textField1Name = "Text Field 1";
    String value1 = "Value 1";

    String expectedYaml = """
        type: instance
        name: {instanceName}
        id: {instanceUri}
        isBasedOn: {isBasedOn}
        children:
          {textField1Name}:
            value: {value1}
        """
        .replace("{isBasedOn}", isBasedOnTemplateUri.toString())
        .replace("{instanceName}", instanceName)
        .replace("{instanceUri}", instanceUri.toString())
        .replace("{textField1Name}", textField1Name)
        .replace("{value1}", value1);

    FieldInstanceArtifact textField1Instance = TextFieldInstance.builder().withValue(value1).build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
        .withName(instanceName)
        .withJsonLdId(instanceUri)
        .withIsBasedOn(isBasedOnTemplateUri)
        .withSingleInstanceFieldInstance(textField1Name, textField1Instance)
        .build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);
    assertEquals(expectedYaml, actualYaml);
  }

  @Test
  public void testRenderTemplateInstanceWithMultiInstanceTextField() throws JsonProcessingException {
    String instanceName = "Instance 1";
    URI instanceUri = java.net.URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = java.net.URI.create("https://repo.metadatacenter.org/templates/3232");
    String textField1Name = "Text Field 1";
    String value1 = "Value 1";
    String value2 = "Value 2";

    String expectedYaml = """
        type: instance
        name: {instanceName}
        id: {instanceUri}
        isBasedOn: {isBasedOn}
        children:
          {textField1Name}:
            - value: {value1}
            - value: {value2}
        """
        .replace("{isBasedOn}", isBasedOnTemplateUri.toString())
        .replace("{instanceName}", instanceName)
        .replace("{instanceUri}", instanceUri.toString())
        .replace("{textField1Name}", textField1Name)
        .replace("{value1}", value1)
        .replace("{value2}", value2);

    FieldInstanceArtifact textField1Instance1 = TextFieldInstance.builder().withValue(value1).build();
    FieldInstanceArtifact textField1Instance2 = TextFieldInstance.builder().withValue(value2).build();
    List<FieldInstanceArtifact> textField1Instances = new ArrayList<>();
    textField1Instances.add(textField1Instance1);
    textField1Instances.add(textField1Instance2);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
        .withName(instanceName)
        .withJsonLdId(instanceUri)
        .withIsBasedOn(isBasedOnTemplateUri)
        .withMultiInstanceFieldInstances(textField1Name, textField1Instances)
        .build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);
    assertEquals(expectedYaml, actualYaml);
  }


  @Test
  public void testRenderTemplateInstanceWithSingleInstanceControlledTermField() throws JsonProcessingException {
    String instanceName = "Instance 1";
    URI instanceUri = java.net.URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = java.net.URI.create("https://repo.metadatacenter.org/templates/3232");
    String controledTermField1Name = "Controlled Term Field 1";
    URI field1IriValue = java.net.URI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C3224");
    String field1LabelValue = "Melanoma";

    String expectedYaml = """
        type: instance
        name: {instanceName}
        id: {instanceUri}
        isBasedOn: {isBasedOn}
        children:
          {controlledTermField1Name}:
            id: {field1IriValue}
            label: {field1LabelValue}
        """
        .replace("{isBasedOn}", isBasedOnTemplateUri.toString())
        .replace("{instanceName}", instanceName)
        .replace("{instanceUri}", instanceUri.toString())
        .replace("{controlledTermField1Name}", controledTermField1Name)
        .replace("{field1IriValue}", field1IriValue.toString())
        .replace("{field1LabelValue}", field1LabelValue);

    FieldInstanceArtifact controlledTermField1Instance =
        ControlledTermFieldInstance.builder().withValue(field1IriValue).withLabel(field1LabelValue).build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
        .withName(instanceName)
        .withJsonLdId(instanceUri)
        .withIsBasedOn(isBasedOnTemplateUri)
        .withSingleInstanceFieldInstance(controledTermField1Name, controlledTermField1Instance)
        .build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);
    assertEquals(expectedYaml, actualYaml);
  }

  @Test
  public void testRenderTemplateInstanceWithMultiInstanceControlledTermField() throws JsonProcessingException {
    String instanceName = "Instance 1";
    URI instanceUri = java.net.URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = java.net.URI.create("https://repo.metadatacenter.org/templates/3232");
    String controledTermField1Name = "Controlled Term Field 1";
    URI field1IriValue1 = java.net.URI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C3224");
    String field1LabelValue1 = "Melanoma";
    URI field1IriValue2 = java.net.URI.create("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C3262");
    String field1LabelValue2 = "Neoplasm";

    String expectedYaml = """
        type: instance
        name: {instanceName}
        id: {instanceUri}
        isBasedOn: {isBasedOn}
        children:
          {controlledTermField1Name}:
            - id: {field1IriValue1}
              label: {field1LabelValue1}
            - id: {field1IriValue2}
              label: {field1LabelValue2}
        """
        .replace("{isBasedOn}", isBasedOnTemplateUri.toString())
        .replace("{instanceName}", instanceName)
        .replace("{instanceUri}", instanceUri.toString())
        .replace("{controlledTermField1Name}", controledTermField1Name)
        .replace("{field1IriValue1}", field1IriValue1.toString())
        .replace("{field1LabelValue1}", field1LabelValue1).replace("{field1IriValue2}", field1IriValue2.toString())
        .replace("{field1LabelValue2}", field1LabelValue2);

    FieldInstanceArtifact controlledTermField1Instance1 =
        ControlledTermFieldInstance.builder().withValue(field1IriValue1).withLabel(field1LabelValue1).build();
    FieldInstanceArtifact controlledTermField1Instance2 =
        ControlledTermFieldInstance.builder().withValue(field1IriValue2).withLabel(field1LabelValue2).build();
    List<FieldInstanceArtifact> controlledTermField1Instances = new ArrayList<>();
    controlledTermField1Instances.add(controlledTermField1Instance1);
    controlledTermField1Instances.add(controlledTermField1Instance2);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
        .withName(instanceName)
        .withJsonLdId(instanceUri)
        .withIsBasedOn(isBasedOnTemplateUri)
        .withMultiInstanceFieldInstances(controledTermField1Name, controlledTermField1Instances)
        .build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);
    assertEquals(expectedYaml, actualYaml);
  }

  @Test
  public void testRenderTemplateInstanceWithNestedChildren() throws JsonProcessingException {
    String instanceName = "Instance 1";
    URI instanceUri = java.net.URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = java.net.URI.create("https://repo.metadatacenter.org/templates/3232");
    String textField1Name = "Text Field 1";
    String value1 = "Value 1";
    String value2 = "Value 2";
    String value3 = "Value 3";
    String element1Name = "Element 1";
    String textField2Name = "Text Field 2";
    URI propertyIri = java.net.URI.create("https://example.com/p1");

    String expectedYaml = """
        type: instance
        name: {instanceName}
        id: {instanceUri}
        isBasedOn: {isBasedOn}
        children:
          {textField2Name}:
            - value: {value2}
            - value: {value3}
          {element1Name}:
            children:
              {textField1Name}:
                value: {value1}
        """
        .replace("{isBasedOn}", isBasedOnTemplateUri.toString())
        .replace("{instanceName}", instanceName)
        .replace("{instanceUri}", instanceUri.toString())
        .replace("{textField1Name}", textField1Name)
        .replace("{textField2Name}", textField2Name)
        .replace("{element1Name}", element1Name)
        .replace("{value1}", value1)
        .replace("{value2}", value2)
        .replace("{value3}", value3);

    FieldInstanceArtifact textField1Instance = TextFieldInstance.builder().withValue(value1).build();
    ElementInstanceArtifact element1Instance =
        ElementInstanceArtifact.builder().withSingleInstanceFieldInstance(textField1Name, textField1Instance).build();
    FieldInstanceArtifact textField2Instance1 = TextFieldInstance.builder().withValue(value2).build();
    FieldInstanceArtifact textField2Instance2 = TextFieldInstance.builder().withValue(value3).build();
    List<FieldInstanceArtifact> textField2Instances = new ArrayList<>();
    textField2Instances.add(textField2Instance1);
    textField2Instances.add(textField2Instance2);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
        .withName(instanceName)
        .withJsonLdContextEntry(element1Name, propertyIri)
        .withJsonLdId(instanceUri)
        .withIsBasedOn(isBasedOnTemplateUri)
        .withMultiInstanceFieldInstances(textField2Name, textField2Instances)
        .withSingleInstanceElementInstance(element1Name, element1Instance)
        .build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);
    assertEquals(expectedYaml, actualYaml);
  }

  @Test
  public void testRenderTemplateInstanceWithAttributeValues() throws JsonProcessingException {
    String instanceName = "Instance 1";
    URI instanceUri = java.net.URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = java.net.URI.create("https://repo.metadatacenter.org/templates/3232");
    String attributeValueFieldGroupName = "Attribute-value Field A";
    String attributeValueFieldInstanceName1 = "Attribute-value Field Instance 1";
    String attributeValueFieldInstanceName2 = "Attribute-value Field Instance 2";
    String attributeValueValue1 = "AV Value 1";
    String attributeValueValue2 = "AV Value 2";

    String expectedYaml = """
        type: instance
        name: {instanceName}
        id: {instanceUri}
        isBasedOn: {isBasedOn}
        {attributeValueFieldGroupName}:
          {attributeValueFieldInstanceName1}:
            value: {attributeValueValue1}
          {attributeValueFieldInstanceName2}:
            value: {attributeValueValue2}
        """
        .replace("{isBasedOn}", isBasedOnTemplateUri.toString())
        .replace("{instanceName}", instanceName)
        .replace("{instanceUri}", instanceUri.toString())
        .replace("{attributeValueFieldGroupName}", attributeValueFieldGroupName)
        .replace("{attributeValueFieldInstanceName1}", attributeValueFieldInstanceName1)
        .replace("{attributeValueFieldInstanceName2}", attributeValueFieldInstanceName2)
        .replace("{attributeValueValue1}", attributeValueValue1)
        .replace("{attributeValueValue2}", attributeValueValue2);

    FieldInstanceArtifact attributeValueFieldInstance1 =
        TextFieldInstance.builder().withValue(attributeValueValue1).build();
    FieldInstanceArtifact attributeValueFieldInstance2 =
        TextFieldInstance.builder().withValue(attributeValueValue2).build();
    LinkedHashMap<String, FieldInstanceArtifact> attributeValueFieldInstances = new LinkedHashMap<>();
    attributeValueFieldInstances.put(attributeValueFieldInstanceName1, attributeValueFieldInstance1);
    attributeValueFieldInstances.put(attributeValueFieldInstanceName2, attributeValueFieldInstance2);

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
        .withName(instanceName)
        .withJsonLdId(instanceUri)
        .withIsBasedOn(isBasedOnTemplateUri)
        .withAttributeValueFieldGroup(attributeValueFieldGroupName, attributeValueFieldInstances)
        .build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);
    assertEquals(expectedYaml, actualYaml);
  }

  @Test
  public void testRenderTemplateInstanceWithAnnotations() throws JsonProcessingException {
    String instanceName = "Instance 1";
    URI instanceUri = java.net.URI.create("https://repo.metadatacenter.org/template-instances/4343");
    URI isBasedOnTemplateUri = java.net.URI.create("https://repo.metadatacenter.org/templates/3232");
    String annotation1AttributeName = "Literal Annotation attribute name";
    String annotation2AttributeName = "IRI Annotation attribute name";
    String annotation1Value = "A literal value";
    URI annotation2Value = java.net.URI.create("https://example.com/c1");

    String expectedYaml = """
        type: instance
        name: {instanceName}
        id: {instanceUri}
        isBasedOn: {isBasedOn}
        annotations:
          {annotation1AttributeName}:
            value: {annotation1Value}
          {annotation2AttributeName}:
            id: {annotation2Value}
        """
        .replace("{isBasedOn}", isBasedOnTemplateUri.toString())
        .replace("{instanceName}", instanceName)
        .replace("{instanceUri}", instanceUri.toString())
        .replace("{annotation1AttributeName}", annotation1AttributeName)
        .replace("{annotation2AttributeName}", annotation2AttributeName)
        .replace("{annotation1Value}", annotation1Value)
        .replace("{annotation2Value}", annotation2Value.toString());

    Annotations annotations = Annotations.builder()
        .withLiteralAnnotation(annotation1AttributeName, annotation1Value)
        .withIriAnnotation(annotation2AttributeName, annotation2Value)
        .build();

    TemplateInstanceArtifact templateInstanceArtifact = TemplateInstanceArtifact.builder()
        .withName(instanceName)
        .withJsonLdId(instanceUri)
        .withIsBasedOn(isBasedOnTemplateUri)
        .withAnnotations(annotations)
        .build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> actualRendering =
        yamlArtifactRenderer.renderTemplateInstanceArtifact(templateInstanceArtifact);

    String actualYaml = mapper.writeValueAsString(actualRendering);
    assertEquals(expectedYaml, actualYaml);
  }

  private ObjectNode getFileContentAsObjectNode(String jsonFileName) {
    try {
      return (ObjectNode) mapper.readTree(new File(
          JsonArtifactReaderTest.class.getClassLoader().getResource(jsonFileName).getFile()));
    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file " + jsonFileName + ": " + e.getMessage());
    }
  }

}