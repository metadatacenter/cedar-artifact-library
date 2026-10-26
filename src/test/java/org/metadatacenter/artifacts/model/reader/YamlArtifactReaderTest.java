package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.model.ModelNodeNames;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ALT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONFIGURATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DRAFT_STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREF_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LANGUAGE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODEL_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTIPLE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REQUIRED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_RECOMMENDATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;

public class YamlArtifactReaderTest
{
  private YamlArtifactReader artifactReader;
  private ObjectMapper objectMapper;

  @BeforeEach public void setup()
  {
    artifactReader = new YamlArtifactReader();
    objectMapper = new ObjectMapper(new YAMLFactory());
  }

  @Test public void readTemplateSchemaArtifactTest()
  {
    String templateName = "Study";
    String description = "My study";
    String identifier = "ID1";
    URI id = URI.create("https://repo.metadatacenter.org/templates/66");
    Version modelVersion = Version.fromString(ModelNodeNames.MODEL_VERSION);
    Version version = Version.fromString("1.2.3");
    Status status = Status.DRAFT;
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/templates/4553");
    URI previousVersion = URI.create("https://repo.metadatacenter.org/templates/5465");
    URI createdBy = URI.create("https://repo.metadatacenter.org/users/22");
    URI modifiedBy = URI.create("https://repo.metadatacenter.org/users/33");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    String language = "en";

    LinkedHashMap<String, Object> yamlSource = new LinkedHashMap<>();
    yamlSource.put(TYPE, TEMPLATE);
    yamlSource.put(NAME, templateName);
    yamlSource.put(DESCRIPTION, description);
    yamlSource.put(IDENTIFIER, identifier);
    yamlSource.put(ID, id.toString());
    yamlSource.put(MODEL_VERSION, modelVersion.toString());
    yamlSource.put(VERSION, version.toString());
    yamlSource.put(STATUS, DRAFT_STATUS);
    yamlSource.put(DERIVED_FROM, derivedFrom.toString());
    yamlSource.put(PREVIOUS_VERSION, previousVersion.toString());
    yamlSource.put(CREATED_BY, createdBy.toString());
    yamlSource.put(MODIFIED_BY, modifiedBy.toString());
    yamlSource.put(CREATED_ON, createdOn.toString());
    yamlSource.put(MODIFIED_ON, lastUpdatedOn.toString());
    yamlSource.put(LANGUAGE, language);

    TemplateSchemaArtifact templateSchemaArtifact = artifactReader.readTemplateSchemaArtifact(yamlSource);

    assertEquals(templateName, templateSchemaArtifact.name());
    assertEquals(description, templateSchemaArtifact.description());
    assertEquals(identifier, templateSchemaArtifact.identifier().get());
    assertEquals(id, templateSchemaArtifact.jsonLdId().get());
    assertEquals(version, templateSchemaArtifact.version().get());
    assertEquals(status, templateSchemaArtifact.status().get());
    assertEquals(derivedFrom, templateSchemaArtifact.derivedFrom().get());
    assertEquals(previousVersion, templateSchemaArtifact.previousVersion().get());
    assertEquals(createdBy, templateSchemaArtifact.createdBy().get());
    assertEquals(modifiedBy, templateSchemaArtifact.modifiedBy().get());
    assertEquals(createdOn, templateSchemaArtifact.createdOn().get());
    assertEquals(lastUpdatedOn, templateSchemaArtifact.lastUpdatedOn().get());
    assertEquals(language, templateSchemaArtifact.language().get());
  }

  @Test public void readElementSchemaArtifactTest()
  {
    String elementName = "Address";
    String description = "My address";
    String identifier = "ID3";
    URI id = URI.create("https://repo.metadatacenter.org/template-elements/2323");
    Version version = Version.fromString("1.2.3");
    Version modelVersion = Version.fromString(ModelNodeNames.MODEL_VERSION);
    Status status = Status.DRAFT;
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/template-elements/4553");
    URI previousVersion = URI.create("https://repo.metadatacenter.org/template-elements/5465");
    URI createdBy = URI.create("https://repo.metadatacenter.org/users/22");
    URI modifiedBy = URI.create("https://repo.metadatacenter.org/users/33");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    String language = "en";

    LinkedHashMap<String, Object> yamlSource = new LinkedHashMap<>();
    yamlSource.put(TYPE, ELEMENT);
    yamlSource.put(NAME, elementName);
    yamlSource.put(DESCRIPTION, description);
    yamlSource.put(IDENTIFIER, identifier);
    yamlSource.put(ID, id.toString());
    yamlSource.put(MODEL_VERSION, modelVersion.toString());
    yamlSource.put(VERSION, version.toString());
    yamlSource.put(STATUS, DRAFT_STATUS);
    yamlSource.put(DERIVED_FROM, derivedFrom.toString());
    yamlSource.put(PREVIOUS_VERSION, previousVersion.toString());
    yamlSource.put(CREATED_BY, createdBy.toString());
    yamlSource.put(MODIFIED_BY, modifiedBy.toString());
    yamlSource.put(CREATED_ON, createdOn.toString());
    yamlSource.put(MODIFIED_ON, lastUpdatedOn.toString());
    yamlSource.put(LANGUAGE, language);

    ElementSchemaArtifact elementSchemaArtifact = artifactReader.readElementSchemaArtifact(yamlSource);

    assertEquals(elementName, elementSchemaArtifact.name());
    assertEquals(description, elementSchemaArtifact.description());
    assertEquals(identifier, elementSchemaArtifact.identifier().get());
    assertEquals(id, elementSchemaArtifact.jsonLdId().get());
    assertEquals(version, elementSchemaArtifact.version().get());
    assertEquals(status, elementSchemaArtifact.status().get());
    assertEquals(derivedFrom, elementSchemaArtifact.derivedFrom().get());
    assertEquals(previousVersion, elementSchemaArtifact.previousVersion().get());
    assertEquals(createdBy, elementSchemaArtifact.createdBy().get());
    assertEquals(modifiedBy, elementSchemaArtifact.modifiedBy().get());
    assertEquals(createdOn, elementSchemaArtifact.createdOn().get());
    assertEquals(lastUpdatedOn, elementSchemaArtifact.lastUpdatedOn().get());
    assertEquals(language, elementSchemaArtifact.language().get());
  }

  @Test public void readFieldSchemaArtifactTest()
  {
    String fieldName = "Study Name";
    // The YAML `type:` discriminator is the kebab-case wire name (text-field),
    // not the Java enum's default toString (TEXTFIELD). Use the constant.
    String fieldType = TEXT_FIELD;
    String description = "Please enter a study name";
    String identifier = "ID4";
    Version version = Version.fromString("1.2.3");
    Version modelVersion = Version.fromString(ModelNodeNames.MODEL_VERSION);
    Status status = Status.DRAFT;
    URI derivedFrom = URI.create("https://repo.metadatacenter.org/template-fields/4553");
    URI previousVersion = URI.create("https://repo.metadatacenter.org/template-fields/5465");
    URI createdBy = URI.create("https://repo.metadatacenter.org/users/22");
    URI modifiedBy = URI.create("https://repo.metadatacenter.org/users/33");
    OffsetDateTime createdOn = OffsetDateTime.now();
    OffsetDateTime lastUpdatedOn = OffsetDateTime.now();
    String preferredLabel = "Study";
    List<String> altLabels = List.of("Label 1", "Label 2");
    FieldInputType fieldInputType = FieldInputType.TEXTFIELD;
    boolean requiredValue = true;
    boolean valueRecommendation = false;
    boolean hidden = false;
    boolean isMultiple = true;
    Integer minItems = 3;
    Integer maxItems = 5;
    String language = "en";

    LinkedHashMap<String, Object> yamlSource = new LinkedHashMap<>();
    yamlSource.put(TYPE, fieldType);
    yamlSource.put(NAME, fieldName);
    yamlSource.put(DESCRIPTION, description);
    yamlSource.put(IDENTIFIER, identifier);
    yamlSource.put(MODEL_VERSION, modelVersion.toString());
    yamlSource.put(VERSION, version.toString());
    yamlSource.put(STATUS, DRAFT_STATUS);
    yamlSource.put(DERIVED_FROM, derivedFrom.toString());
    yamlSource.put(PREVIOUS_VERSION, previousVersion.toString());
    yamlSource.put(CREATED_BY, createdBy.toString());
    yamlSource.put(MODIFIED_BY, modifiedBy.toString());
    yamlSource.put(CREATED_ON, createdOn.toString());
    yamlSource.put(MODIFIED_ON, lastUpdatedOn.toString());
    yamlSource.put(PREF_LABEL, preferredLabel);
    yamlSource.put(ALT_LABEL, altLabels);
    yamlSource.put(VALUE_RECOMMENDATION, valueRecommendation);
    yamlSource.put(HIDDEN, hidden);
    yamlSource.put(LANGUAGE, language);
    // The canonical YAML form (matching what YamlArtifactRenderer emits for nested
    // fields) puts required / multiple / minItems / maxItems inside a configuration:
    // sub-block. The reader looks there for these flags.
    LinkedHashMap<String, Object> configuration = new LinkedHashMap<>();
    configuration.put(REQUIRED, requiredValue);
    configuration.put(MULTIPLE, isMultiple);
    configuration.put(MIN_ITEMS, minItems);
    configuration.put(MAX_ITEMS, maxItems);
    yamlSource.put(CONFIGURATION, configuration);

    FieldSchemaArtifact fieldSchemaArtifact = artifactReader.readFieldSchemaArtifact(yamlSource);

    assertEquals(fieldName, fieldSchemaArtifact.name());
    assertEquals(description, fieldSchemaArtifact.description());
    assertEquals(identifier, fieldSchemaArtifact.identifier().get());
    assertEquals(version, fieldSchemaArtifact.version().get());
    assertEquals(status, fieldSchemaArtifact.status().get());
    assertEquals(derivedFrom, fieldSchemaArtifact.derivedFrom().get());
    assertEquals(previousVersion, fieldSchemaArtifact.previousVersion().get());
    assertEquals(createdBy, fieldSchemaArtifact.createdBy().get());
    assertEquals(modifiedBy, fieldSchemaArtifact.modifiedBy().get());
    assertEquals(createdOn, fieldSchemaArtifact.createdOn().get());
    assertEquals(lastUpdatedOn, fieldSchemaArtifact.lastUpdatedOn().get());
    assertEquals(preferredLabel, fieldSchemaArtifact.preferredLabel().get());
    assertEquals(altLabels, fieldSchemaArtifact.alternateLabels());
    assertEquals(fieldInputType, fieldSchemaArtifact.fieldUi().inputType());
    assertEquals(requiredValue, fieldSchemaArtifact.valueConstraints().get().requiredValue());
    assertEquals(valueRecommendation, fieldSchemaArtifact.fieldUi().valueRecommendationEnabled());
    assertEquals(hidden, fieldSchemaArtifact.fieldUi().hidden());
    assertEquals(isMultiple, fieldSchemaArtifact.isMultiple());
    assertEquals(minItems, fieldSchemaArtifact.minItems().get());
    assertEquals(maxItems, fieldSchemaArtifact.maxItems().get());
    assertEquals(language, fieldSchemaArtifact.language().get());
  }

  @Test public void readDefaultsTopLevelVersionAndStatusButNotNestedChildren()
  {
    // version/status default to 0.0.1 / draft on the top-level artifact (matching the builder),
    // but a nested child that omits them is left untouched — so real templates with version-less
    // child fields still round-trip unchanged.
    LinkedHashMap<String, Object> childField = new LinkedHashMap<>();
    childField.put("key", "patient_name");
    childField.put("type", "text-field");
    childField.put("name", "Patient name");
    childField.put("modelVersion", "1.6.0");

    LinkedHashMap<String, Object> template = new LinkedHashMap<>();
    template.put("type", "template");
    template.put("name", "Patient");
    template.put("modelVersion", "1.6.0");
    template.put("children", List.of(childField));

    TemplateSchemaArtifact read = artifactReader.readTemplateSchemaArtifact(template);

    assertEquals(Version.DEFAULT, read.version().orElseThrow(),
        "top-level version defaults to 0.0.1");
    assertEquals(Status.DRAFT, read.status().orElseThrow(),
        "top-level status defaults to draft");

    FieldSchemaArtifact child = read.getFieldSchemaArtifact("patient_name");
    assertTrue(child.version().isEmpty(),
        "a nested child that omitted version must not be defaulted");
    assertTrue(child.status().isEmpty(),
        "a nested child that omitted status must not be defaulted");
  }

  @Test public void readUnquotedInstanceValueKeepsTheTextTheAuthorWrote() throws IOException
  {
    // A YAML 1.2 core resolver reads these plain scalars as numbers or booleans. The field holds a
    // literal, so the value read must be the text written: a ZIP code of 007 is not 7.
    for (String spelling : List.of("007", "0x1F", "0o17", "1e3", "+1", ".5", "-0", "1.50",
      "9007199254740993", "True", "TRUE", "false")) {
      String yaml = "type: instance\nname: X\nisBasedOn: \"https://repo.metadatacenter.org/templates/abc\"\n"
        + "children:\n  Count:\n    value: " + spelling + "\n  Counts:\n    - value: " + spelling + "\n";
      TemplateInstanceArtifact instance =
        artifactReader.readTemplateInstanceArtifact(ArtifactSourceParser.yaml(yaml));

      assertEquals(spelling, instance.singleInstanceFieldInstances().get("Count").jsonLdValue().orElseThrow(),
        spelling);
      assertEquals(spelling,
        instance.multiInstanceFieldInstances().get("Counts").get(0).jsonLdValue().orElseThrow(), spelling);
    }
  }

  @Test public void readInstanceValueNullSpellingsStayNull() throws IOException
  {
    // A null is not a literal: it is still refused, and beside a label it still reads as no value.
    String document = "type: instance\nname: X\nisBasedOn: \"https://repo.metadatacenter.org/templates/abc\"\n"
      + "children:\n  Term:\n";
    for (String spelling : List.of("~", "null", "Null", "NULL")) {
      assertThrows(ArtifactParseException.class, () -> artifactReader.readTemplateInstanceArtifact(
        ArtifactSourceParser.yaml(document + "    value: " + spelling + "\n")), spelling);
      TemplateInstanceArtifact instance = artifactReader.readTemplateInstanceArtifact(
        ArtifactSourceParser.yaml(document + "    value: " + spelling + "\n    label: \"Lung\"\n"));
      assertTrue(instance.singleInstanceFieldInstances().get("Term").jsonLdValue().isEmpty(), spelling);
    }
  }

  @Test public void readSchemaArtifactsWithWhitespaceOnlyName() throws IOException
  {
    // The builders refuse a name made only of whitespace. The YAML reader reads one, from YAML text.
    YamlArtifactRenderer renderer = new YamlArtifactRenderer(false);
    LinkedHashMap<String, Object> template = renderer.renderTemplateSchemaArtifact(
      TemplateSchemaArtifact.builder().withName("Study").build());
    LinkedHashMap<String, Object> element = renderer.renderElementSchemaArtifact(
      ElementSchemaArtifact.builder().withName("Address").build());
    LinkedHashMap<String, Object> field = renderer.renderFieldSchemaArtifact(TextField.builder().withName("Title")
      .build());

    for (String name : List.of(" ", "   ", "\t", "\n", " \t\r\n ")) {
      template.put(NAME, name);
      element.put(NAME, name);
      field.put(NAME, name);

      assertEquals(name, artifactReader.readTemplateSchemaArtifact(
        ArtifactSourceParser.yaml(objectMapper.writeValueAsString(template))).name());
      assertEquals(name, artifactReader.readElementSchemaArtifact(
        ArtifactSourceParser.yaml(objectMapper.writeValueAsString(element))).name());
      assertEquals(name, artifactReader.readFieldSchemaArtifact(
        ArtifactSourceParser.yaml(objectMapper.writeValueAsString(field))).name());
    }
  }

  private LinkedHashMap<String, Object> readYamlAsMap(String yamlFilePath)
  {
    try {
      return objectMapper.readValue(new File(yamlFilePath), LinkedHashMap.class);
    } catch (IOException e) {
      throw new RuntimeException("Error reading YAML file: " + yamlFilePath, e);
    }
  }
}
