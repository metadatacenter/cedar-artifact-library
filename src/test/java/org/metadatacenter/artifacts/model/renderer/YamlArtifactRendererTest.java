package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION_TO;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.BRANCH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTROLLED_TERM_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FOOTER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEADER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.KEY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_DEPTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ONTOLOGY_NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREF_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;

public class YamlArtifactRendererTest {

  private YAMLFactory yamlFactory;
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    yamlFactory = new YAMLFactory().
      disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).
      enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).
      enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR).
      disable(YAMLGenerator.Feature.SPLIT_LINES);
    mapper = new ObjectMapper(yamlFactory);
  }
  @Test
  public void testRenderTemplateSchemaArtifact() {

    String name = "Study";
    String description = "Study template";
    String header = "Study header";
    String footer = "Study footer";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName(name).
      withDescription(description).
      withHeader(header).
      withFooter(footer).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(TYPE, TEMPLATE);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(HEADER, header);
    expectedRendering.put(FOOTER, footer);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderElementSchemaArtifact() {

    String name = "Address";
    String description = "Address element";
    String preferredLabel = "preferred label";

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123")).
      withName(name).
      withDescription(description).
      withPreferredLabel(preferredLabel).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderElementSchemaArtifact(elementSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(TYPE, ELEMENT);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(PREF_LABEL, preferredLabel);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderTextFieldCompact() {

    String name = "Study Name";
    String description = "Study name field";

    TextField textField = TextField.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(textField);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(TYPE, TEXT_FIELD);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderTextField() throws JsonProcessingException
  {
    String name = "Study Name";
    String description = "Study name field";

    TextField textField = TextField.builder().
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(textField);

    String expectedYaml = """
        type: text-field
        name: ${name}
        description: ${description}
    """.replace("${name}", name).replace("${description}", description);

    LinkedHashMap<String, Object> expectedRendering = mapper.readValue(expectedYaml, LinkedHashMap.class);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderControlledTermField() {

    String name = "Disease";
    String description = "Disease field";
    URI fieldId = URI.create("https://repo.metadatacenter.org/template_fields/123");
    URI doidDiseaseBranchIri = URI.create("http://purl.obolibrary.org/obo/DOID_4");
    String doidSource = "DOID";
    String doidSourceAcronym = "DOID";
    String doidDiseaseBranchName = "Disease";
    Integer doidDiseaseBranchDepth = 0;
    URI pmrDiseaseBranchIri = URI.create("http://purl.bioontology.org/ontology/PMR.owl#Disease");
    String pmrSource = "Physical Medicine and Rehabilitation (PMR)";
    String pmrSourceAcronym = "PMR";
    String pmrDiseaseBranchName = "Disease";
    Integer pmrDiseaseBranchDepth = 0;
    ValueConstraintsActionType actionType = ValueConstraintsActionType.MOVE;
    URI actionTermUri = URI.create("http://purl.obolibrary.org/obo/DOID_0040022");
    String actionSourceAcronym = "DOID";
    ValueType actionValueType = ValueType.ONTOLOGY_CLASS;
    URI actionSourceIri = URI.create("http://purl.obolibrary.org/obo/DOID_4");
    Integer actionTo = 2;

    ControlledTermField controlledTermField = ControlledTermField.builder().
      withJsonLdId(fieldId).
      withName(name).
      withDescription(description).
      withBranchValueConstraint(doidDiseaseBranchIri, doidSource, doidSourceAcronym, doidDiseaseBranchName, 0).
      withBranchValueConstraint(pmrDiseaseBranchIri, pmrSource, pmrSourceAcronym, pmrDiseaseBranchName, 0).
      withValueConstraintsAction(actionTermUri, actionSourceAcronym, actionValueType, actionType, actionSourceIri, actionTo).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(name, controlledTermField);

    LinkedHashMap<String, Object> expectedBaseFieldRendering = new LinkedHashMap<>();
    expectedBaseFieldRendering.put(KEY, name);
    expectedBaseFieldRendering.put(TYPE, CONTROLLED_TERM_FIELD);
    expectedBaseFieldRendering.put(NAME, name);
    expectedBaseFieldRendering.put(DESCRIPTION, description);
    expectedBaseFieldRendering.put(DATATYPE, IRI);

    List<LinkedHashMap<String, Object>> expectedValueConstraintsRendering = new ArrayList<>();

    LinkedHashMap<String, Object>  doidDiseaseBranchRendering = new LinkedHashMap<>();
    doidDiseaseBranchRendering.put(TYPE, BRANCH);
    doidDiseaseBranchRendering.put(ONTOLOGY_NAME, doidSource);
    doidDiseaseBranchRendering.put(ACRONYM, doidSourceAcronym);
    doidDiseaseBranchRendering.put(TERM_LABEL, doidDiseaseBranchName);
    doidDiseaseBranchRendering.put(IRI, doidDiseaseBranchIri);
    doidDiseaseBranchRendering.put(MAX_DEPTH, doidDiseaseBranchDepth);

    expectedValueConstraintsRendering.add(doidDiseaseBranchRendering);

    LinkedHashMap<String, Object>  pmrDiseaseBranchRendering = new LinkedHashMap<>();
    pmrDiseaseBranchRendering.put(TYPE, BRANCH);
    pmrDiseaseBranchRendering.put(ONTOLOGY_NAME, pmrSource);
    pmrDiseaseBranchRendering.put(ACRONYM, pmrSourceAcronym);
    pmrDiseaseBranchRendering.put(TERM_LABEL, pmrDiseaseBranchName);
    pmrDiseaseBranchRendering.put(IRI, pmrDiseaseBranchIri);
    pmrDiseaseBranchRendering.put(MAX_DEPTH, pmrDiseaseBranchDepth);

    expectedValueConstraintsRendering.add(pmrDiseaseBranchRendering);

    expectedBaseFieldRendering.put(VALUES, expectedValueConstraintsRendering);

    List<LinkedHashMap<String, Object>> expectedActionsRendering = new ArrayList<>();

    LinkedHashMap<String, Object>  actionRendering = new LinkedHashMap<>();
    actionRendering.put(ACTION, actionType.toString());
    actionRendering.put(ACTION_TO, actionTo);
    actionRendering.put(TERM_IRI, actionTermUri);
    actionRendering.put(SOURCE_IRI, actionSourceIri);
    actionRendering.put(SOURCE_ACRONYM, actionSourceAcronym);

    expectedActionsRendering.add(actionRendering);

    expectedBaseFieldRendering.put(ACTIONS, expectedActionsRendering);

    assertEquals(expectedBaseFieldRendering.toString(), actualRendering.toString());
  }

  @Test
  public void testRenderAnnotations() {

    String name = "Study";
    String description = "Study template";
    String literalAnnotationName = "foo";
    String literalAnnotationValue = "bar";
    String iriAnnotationName = "A";
    String iriAnnotationValue = "https://example.com/A";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName(name).
      withDescription(description).
      build();

  }

  @Test public void testCreateControlledTermFieldWithClassValueConstraint() throws JsonProcessingException
  {
    String fieldName = "Field name";
    String description = "Field description";
    URI classUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
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
              termType: ${classValueType}
              termLabel: ${classPrefLabel}
              iri: ${classUri}
      """.replace("${fieldName}", fieldName)
      .replace("${description}", description)
      .replace("${classValueType}", classValueType.toString())
      .replace("${classLabel}", classLabel)
      .replace("${classSource}", classSource)
      .replace("${classPrefLabel}", classPrefLabel)
      .replace("${classUri}", classUri.toString());

    ControlledTermField controlledTermField = ControlledTermField.builder().withName(fieldName).withDescription(description)
      .withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(controlledTermField);

    LinkedHashMap<String, Object> expectedRendering = mapper.readValue(expectedYaml, LinkedHashMap.class);

    assertEquals(expectedRendering, actualRendering);
  }

}