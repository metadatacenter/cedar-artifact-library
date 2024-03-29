package org.metadatacenter.artifacts.model.renderer;

import org.junit.Ignore;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STRING;

public class YamlArtifactRendererTest {

  @Test
  public void testRenderTemplateSchemaArtifact() {

    String name = "Study";
    String description = "Study template";

    TemplateSchemaArtifact templateSchemaArtifact = TemplateSchemaArtifact.builder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/123")).
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderTemplateSchemaArtifact(templateSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(TEMPLATE, name);
    expectedRendering.put(DESCRIPTION, description);

    assertEquals(expectedRendering, rendering);
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

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderElementSchemaArtifact(elementSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(ELEMENT, name);
    expectedRendering.put(DESCRIPTION, description);

    assertEquals(expectedRendering, rendering);
  }

  @Ignore @Test
  public void testRenderTextField() {

    String name = "Study Name";
    String description = "Study name field";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).
      withName(name).
      withDescription(description).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(true);

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(TYPE, TEXT_FIELD);
    expectedRendering.put(DATATYPE, STRING);

    assertEquals(expectedRendering, rendering);
  }

  @Ignore @Test
  public void testRenderControlledTermField() {

    String name = "Disease";
    String description = "Disease field";
    URI doidDiseaseBranchURI = URI.create("http://purl.obolibrary.org/obo/DOID_4");
    String doidSource = "DOID";
    String doidAcronym = "DOID";
    String doidDiseaseBranchName = "disease";
    URI pmrDiseaseBranchURI = URI.create("http://purl.bioontology.org/ontology/PMR.owl#Disease");
    String pmrSource = "Physical Medicine and Rehabilitation (PMR)";
    String pmrAcronym = "PMR";
    String pmrDiseaseBranchName = "Disease";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.controlledTermFieldBuilder().
      withJsonLdId(URI.create("https://repo.metadatacenter.org/template_fields/123")).
      withName(name).
      withDescription(description).
      withBranchValueConstraint(doidDiseaseBranchURI, doidSource, doidAcronym, doidDiseaseBranchName, 0).
      withBranchValueConstraint(pmrDiseaseBranchURI, pmrSource, pmrAcronym, pmrDiseaseBranchName, 0).
      build();

    YamlArtifactRenderer yamlArtifactRenderer = new YamlArtifactRenderer(false);

    LinkedHashMap<String, Object> rendering = yamlArtifactRenderer.renderFieldSchemaArtifact(fieldSchemaArtifact);

    LinkedHashMap<String, Object> expectedRendering = new LinkedHashMap<>();
    expectedRendering.put(NAME, name);
    expectedRendering.put(DESCRIPTION, description);
    expectedRendering.put(TYPE, FieldInputType.TEXTFIELD);
    expectedRendering.put(DATATYPE, STRING);

    List<LinkedHashMap<String, Object>> expectedValueConstraintsRendering = new ArrayList<>();


    expectedRendering.put(VALUES, expectedValueConstraintsRendering);

    assertEquals(expectedRendering, rendering);
  }



}