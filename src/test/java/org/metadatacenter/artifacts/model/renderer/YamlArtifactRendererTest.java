package org.metadatacenter.artifacts.model.renderer;

import org.junit.Ignore;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION_TO;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.BRANCH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FOOTER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEADER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_DEPTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STRING;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;

public class YamlArtifactRendererTest {

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
    expectedRendering.put(TYPE, TEMPLATE);
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(HEADER, header);
    expectedRendering.put(FOOTER, footer);

    assertEquals(expectedRendering, actualRendering);
  }

  @Ignore @Test
  public void testRenderElementSchemaArtifact() {

    String name = "Address";
    String description = "Address element";

    ElementSchemaArtifact elementSchemaArtifact = ElementSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_elements/123")).
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderElementSchemaArtifact(elementSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(ELEMENT, name);
    expectedRendering.put(DESCRIPTION, description);

    assertEquals(expectedRendering, actualRendering);
  }

  @Test
  public void testRenderTextField() {

    String name = "Study Name";
    String description = "Study name field";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(TYPE, TEXT_FIELD);
    expectedRendering.put(DATATYPE, STRING);

    assertEquals(expectedRendering, actualRendering);
  }

  @Ignore // TODO See why actions fail the comparison test
  @Test
  public void testRenderControlledTermField() {

    String name = "Disease";
    String description = "Disease field";
    URI fieldId = URI.create("https://repo.metadatacenter.org/template_fields/123");
    URI doidDiseaseBranchIri = URI.create("http://purl.obolibrary.org/obo/DOID_4");
    String doidSource = "DOID";
    String doidSourceAcronym = "DOID";
    String doidDiseaseBranchName = "disease";
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

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.controlledTermFieldBuilder().
      withJsonLdId(fieldId).
      withName(name).
      withDescription(description).
      withBranchValueConstraint(doidDiseaseBranchIri, doidSource, doidSourceAcronym, doidDiseaseBranchName, 0).
      withBranchValueConstraint(pmrDiseaseBranchIri, pmrSource, pmrSourceAcronym, pmrDiseaseBranchName, 0).
      withValueConstraintsAction(actionTermUri, actionSourceAcronym, actionValueType, actionType, actionSourceIri, actionTo).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> actualRendering = yamlArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(TYPE, TEXT_FIELD);
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(DATATYPE, IRI);

    List<LinkedHashMap<String, Object>> expectedValueConstraintsRendering = new ArrayList<>();

    LinkedHashMap<String, Object>  doidDiseaseBranchRendering = new LinkedHashMap<>();
    doidDiseaseBranchRendering.put(TYPE, BRANCH);
    doidDiseaseBranchRendering.put(SOURCE, doidSource);
    doidDiseaseBranchRendering.put(SOURCE_NAME, doidDiseaseBranchName);
    doidDiseaseBranchRendering.put(SOURCE_ACRONYM, doidSourceAcronym);
    doidDiseaseBranchRendering.put(SOURCE_IRI, doidDiseaseBranchIri);
    doidDiseaseBranchRendering.put(MAX_DEPTH, doidDiseaseBranchDepth);

    expectedValueConstraintsRendering.add(doidDiseaseBranchRendering);

    LinkedHashMap<String, Object>  pmrDiseaseBranchRendering = new LinkedHashMap<>();
    pmrDiseaseBranchRendering.put(TYPE, BRANCH);
    pmrDiseaseBranchRendering.put(SOURCE, pmrSource);
    pmrDiseaseBranchRendering.put(SOURCE_NAME, pmrDiseaseBranchName);
    pmrDiseaseBranchRendering.put(SOURCE_ACRONYM, pmrSourceAcronym);
    pmrDiseaseBranchRendering.put(SOURCE_IRI, pmrDiseaseBranchIri);
    pmrDiseaseBranchRendering.put(MAX_DEPTH, pmrDiseaseBranchDepth);

    expectedValueConstraintsRendering.add(pmrDiseaseBranchRendering);

    expectedRendering.put(VALUES, expectedValueConstraintsRendering);

    List<LinkedHashMap<String, Object>> expectedActionsRendering = new ArrayList<>();

    LinkedHashMap<String, Object>  actionRendering = new LinkedHashMap<>();
    actionRendering.put(ACTION, actionType);
    actionRendering.put(TERM_IRI, actionTermUri);
    actionRendering.put(SOURCE_ACRONYM, actionSourceAcronym);
    actionRendering.put(SOURCE_IRI, actionSourceIri);
    actionRendering.put(ACTION_TO, actionTo);

    expectedActionsRendering.add(actionRendering);

    expectedRendering.put(ACTIONS, expectedActionsRendering);

    assertEquals(expectedRendering, actualRendering);
  }

}