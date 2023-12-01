package org.metadatacenter.artifacts.model.reader;

import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LAST_UPDATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODEL_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTIPLE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REQUIRED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SKOS_ALT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SKOS_PREF_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_RECOMMENDATION_ENABLED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;

public class YamlArtifactReaderTest
{
  private YamlArtifactReader artifactReader;

  @Before
  public void setup() {
    artifactReader = new YamlArtifactReader();
  }

  @Test public void readTemplateSchemaArtifactTest()
  {
    String name = "Study";
    String description = "My study";
    String identifier = "ID1";
    URI id = URI.create("https://repo.metadatacenter.org/templates/66");
    Version version = Version.fromString("1.2.3");
    Version modelVersion = Version.fromString("1.6.0");
    Status status = Status.DRAFT;
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/templates/4553");
    URI previousVersion = URI.create("https://repo.metadatacenter.org/templates/5465");
    URI createdBy = URI.create("https://repo.metadatacenter.org/users/22");
    URI modifiedBy = URI.create("https://repo.metadatacenter.org/users/33");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();

    LinkedHashMap<String, Object> yamlSource = new LinkedHashMap<>();
    yamlSource.put(TEMPLATE, name);
    yamlSource.put(DESCRIPTION, description);
    yamlSource.put(IDENTIFIER, identifier);
    yamlSource.put(ID, id.toString());
    yamlSource.put(VERSION, version.toString());
    yamlSource.put(MODEL_VERSION, modelVersion.toString());
    yamlSource.put(STATUS, status.toString());
    yamlSource.put(DERIVED_FROM, derivedFrom.toString());
    yamlSource.put(PREVIOUS_VERSION, previousVersion.toString());
    yamlSource.put(CREATED_BY, createdBy.toString());
    yamlSource.put(MODIFIED_BY, modifiedBy.toString());
    yamlSource.put(CREATED_ON, createdOn.toString());
    yamlSource.put(LAST_UPDATED_ON, lastUpdatedOn.toString());

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(yamlSource);

    assertEquals(name, templateSchemaArtifact.name());
    assertEquals(description, templateSchemaArtifact.description());
    assertEquals(identifier, templateSchemaArtifact.identifier().get());
    assertEquals(id, templateSchemaArtifact.jsonLdId().get());
    assertEquals(version, templateSchemaArtifact.version().get());
    assertEquals(modelVersion, templateSchemaArtifact.modelVersion());
    assertEquals(status, templateSchemaArtifact.status().get());
    assertEquals(derivedFrom, templateSchemaArtifact.derivedFrom().get());
    assertEquals(previousVersion, templateSchemaArtifact.previousVersion().get());
    assertEquals(createdBy, templateSchemaArtifact.createdBy().get());
    assertEquals(modifiedBy, templateSchemaArtifact.modifiedBy().get());
    assertEquals(createdOn, templateSchemaArtifact.createdOn().get());
    assertEquals(lastUpdatedOn, templateSchemaArtifact.lastUpdatedOn().get());
  }

  @Test public void readElementSchemaArtifactTest()
  {
    String name = "Address";
    String description = "My address";
    String identifier = "ID3";
    URI id = URI.create("https://repo.metadatacenter.org/template-elements/2323");
    Version version = Version.fromString("1.2.3");
    Version modelVersion = Version.fromString("1.6.0");
    Status status = Status.DRAFT;
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/template-elements/4553");
    URI previousVersion = URI.create("https://repo.metadatacenter.org/template-elements/5465");
    URI createdBy = URI.create("https://repo.metadatacenter.org/users/22");
    URI modifiedBy = URI.create("https://repo.metadatacenter.org/users/33");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    boolean isMultiple = true;
    Integer minItems = 3;
    Integer maxItems = 5;

    LinkedHashMap<String, Object> yamlSource = new LinkedHashMap<>();
    yamlSource.put(ELEMENT, name);
    yamlSource.put(DESCRIPTION, description);
    yamlSource.put(IDENTIFIER, identifier);
    yamlSource.put(ID, id.toString());
    yamlSource.put(VERSION, version.toString());
    yamlSource.put(MODEL_VERSION, modelVersion.toString());
    yamlSource.put(STATUS, status.toString());
    yamlSource.put(DERIVED_FROM, derivedFrom.toString());
    yamlSource.put(PREVIOUS_VERSION, previousVersion.toString());
    yamlSource.put(CREATED_BY, createdBy.toString());
    yamlSource.put(MODIFIED_BY, modifiedBy.toString());
    yamlSource.put(CREATED_ON, createdOn.toString());
    yamlSource.put(LAST_UPDATED_ON, lastUpdatedOn.toString());
    yamlSource.put(MULTIPLE, isMultiple);
    yamlSource.put(MIN_ITEMS, minItems);
    yamlSource.put(MAX_ITEMS, maxItems);

    ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(yamlSource);

    assertEquals(name, elementSchemaArtifact.name());
    assertEquals(description, elementSchemaArtifact.description());
    assertEquals(identifier, elementSchemaArtifact.identifier().get());
    assertEquals(id, elementSchemaArtifact.jsonLdId().get());
    assertEquals(version, elementSchemaArtifact.version().get());
    assertEquals(modelVersion, elementSchemaArtifact.modelVersion());
    assertEquals(status, elementSchemaArtifact.status().get());
    assertEquals(derivedFrom, elementSchemaArtifact.derivedFrom().get());
    assertEquals(previousVersion, elementSchemaArtifact.previousVersion().get());
    assertEquals(createdBy, elementSchemaArtifact.createdBy().get());
    assertEquals(modifiedBy, elementSchemaArtifact.modifiedBy().get());
    assertEquals(createdOn, elementSchemaArtifact.createdOn().get());
    assertEquals(lastUpdatedOn, elementSchemaArtifact.lastUpdatedOn().get());
    assertEquals(isMultiple, elementSchemaArtifact.isMultiple());
    assertEquals(minItems, elementSchemaArtifact.minItems().get());
    assertEquals(maxItems, elementSchemaArtifact.maxItems().get());
  }

  @Test public void readFieldSchemaArtifactTest()
  {
    String name = "Study Name";
    String description = "Please enter a study name";
    String identifier = "ID4";
    Version version = Version.fromString("1.2.3");
    Version modelVersion = Version.fromString("1.6.0");
    Status status = Status.DRAFT;
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/template-fields/4553");
    URI previousVersion = URI.create("https://repo.metadatacenter.org/template-fields/5465");
    URI createdBy = URI.create("https://repo.metadatacenter.org/users/22");
    URI modifiedBy = URI.create("https://repo.metadatacenter.org/users/33");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    String prefLabel = "Study";
    List<String> altLabels = List.of("Label 1", "Label 2");
    FieldInputType fieldInputType = FieldInputType.TEXTFIELD;
    boolean requiredValue = true;
    boolean valueRecommendationEnabled = false;
    boolean hidden = false;
    boolean isMultiple = true;
    Integer minItems = 3;
    Integer maxItems = 5;

    LinkedHashMap<String, Object> yamlSource = new LinkedHashMap<>();
    yamlSource.put(FIELD, name);
    yamlSource.put(DESCRIPTION, description);
    yamlSource.put(IDENTIFIER, identifier);
    yamlSource.put(VERSION, version.toString());
    yamlSource.put(MODEL_VERSION, modelVersion.toString());
    yamlSource.put(STATUS, status.toString());
    yamlSource.put(DERIVED_FROM, derivedFrom.toString());
    yamlSource.put(PREVIOUS_VERSION, previousVersion.toString());
    yamlSource.put(CREATED_BY, createdBy.toString());
    yamlSource.put(MODIFIED_BY, modifiedBy.toString());
    yamlSource.put(CREATED_ON, createdOn.toString());
    yamlSource.put(LAST_UPDATED_ON, lastUpdatedOn.toString());
    yamlSource.put(SKOS_PREF_LABEL, prefLabel);
    yamlSource.put(SKOS_ALT_LABEL, altLabels);
    yamlSource.put(INPUT_TYPE, FieldInputType.TEXTFIELD.toString());
    yamlSource.put(REQUIRED, requiredValue);
    yamlSource.put(VALUE_RECOMMENDATION_ENABLED, valueRecommendationEnabled);
    yamlSource.put(HIDDEN, hidden);
    yamlSource.put(MULTIPLE, isMultiple);
    yamlSource.put(MIN_ITEMS, minItems);
    yamlSource.put(MAX_ITEMS, maxItems);

    FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(yamlSource);

    assertEquals(name, fieldSchemaArtifact.name());
    assertEquals(description, fieldSchemaArtifact.description());
    assertEquals(identifier, fieldSchemaArtifact.identifier().get());
    assertEquals(version, fieldSchemaArtifact.version().get());
    assertEquals(modelVersion, fieldSchemaArtifact.modelVersion());
    assertEquals(status, fieldSchemaArtifact.status().get());
    assertEquals(derivedFrom, fieldSchemaArtifact.derivedFrom().get());
    assertEquals(previousVersion, fieldSchemaArtifact.previousVersion().get());
    assertEquals(createdBy, fieldSchemaArtifact.createdBy().get());
    assertEquals(modifiedBy, fieldSchemaArtifact.modifiedBy().get());
    assertEquals(createdOn, fieldSchemaArtifact.createdOn().get());
    assertEquals(lastUpdatedOn, fieldSchemaArtifact.lastUpdatedOn().get());
    assertEquals(prefLabel, fieldSchemaArtifact.skosPrefLabel().get());
    assertEquals(altLabels, fieldSchemaArtifact.skosAlternateLabels());
    assertEquals(fieldInputType, fieldSchemaArtifact.fieldUi().inputType());
    // TODO assertEquals(requiredValue, fieldSchemaArtifact.valueConstraints().get().requiredValue());
    assertEquals(valueRecommendationEnabled, fieldSchemaArtifact.fieldUi().valueRecommendationEnabled());
    assertEquals(hidden, fieldSchemaArtifact.fieldUi().hidden());
    assertEquals(isMultiple, fieldSchemaArtifact.isMultiple());
    assertEquals(minItems, fieldSchemaArtifact.minItems().get());
    assertEquals(maxItems, fieldSchemaArtifact.maxItems().get());
  }

}