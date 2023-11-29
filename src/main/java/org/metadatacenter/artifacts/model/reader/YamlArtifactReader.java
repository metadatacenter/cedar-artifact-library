package org.metadatacenter.artifacts.model.reader;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementUi;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.NumericFieldUi;
import org.metadatacenter.artifacts.model.core.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateUi;
import org.metadatacenter.artifacts.model.core.TemporalFieldUi;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.ValueConstraints;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LAST_UPDATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODEL_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.INPUT_TYPES;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.UI_CONTENT;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;
import static org.metadatacenter.model.ModelNodeNames.UI_HIDDEN;
import static org.metadatacenter.model.ModelNodeNames.UI_TIMEZONE_ENABLED;
import static org.metadatacenter.model.ModelNodeNames.UI_VALUE_RECOMMENDATION_ENABLED;

public class YamlArtifactReader implements ArtifactReader<LinkedHashMap<String, Object>>
{
  public YamlArtifactReader() {}

  /**
   * Read a YAML specification of a template schema artifact
   * <p></p>
   * e.g.,
   * <pre>
   * template: Study
   * description: Study template
   * identifier: SFY343
   * version: 1.0.0
   * status: published
   *
   * children:
   *
   *   - field: Study Name
   *     description: Study name field
   *     inputType: textfield
   *     datatype: xsd:string
   *     required: true
   *
   *   - field: Study ID
   *     description: Study ID field
   *     inputType: textfield
   *     datatype: xsd:string
   *     required: true
   *     minLength: 2
   *
   *   - element: Address
   *     description: Address element
   *     isMultiple: true
   *     minItems: 0
   *     maxItems: 4
   *
   *     children:
   *       - field: Address 1
   *         inputType: textfield
   *         datatype: xsd:string
   *       - field: ZIP
   *         inputType: textfield
   *         datatype: xsd:string
   *         minLength: 5
   *         maxLength: 5
   * </pre>
   */
  @Override public TemplateSchemaArtifact readTemplateSchemaArtifact(LinkedHashMap<String, Object> yamlSource)
  {
    String path = "top level";
    String name = readRequiredString(yamlSource, path, TEMPLATE, false);

    return readTemplateSchemaArtifact(yamlSource, path, name);
   }

  private TemplateSchemaArtifact readTemplateSchemaArtifact(LinkedHashMap<String, Object> yamlSource, String path, String name)
  {
    URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
    String jsonSchemaTitle = name + " template";
    String jsonSchemaDescription = name + " template generated from YAML";

    Map<String, URI> jsonLdContext = new HashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readOptionalUri(yamlSource, path, ID);

    String description = readStringWithDefaultValue(yamlSource, path, DESCRIPTION, "");
    Optional<String> identifier = readOptionalString(yamlSource, path, IDENTIFIER, true);
    Optional<Version> version = readOptionalVersion(yamlSource, path, VERSION);
    Version modelVersion = readRequiredVersion(yamlSource, path, MODEL_VERSION);
    Optional<Status> status = readOptionalStatus(yamlSource, path, STATUS);
    Optional<URI> previousVersion = readOptionalUri(yamlSource, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readOptionalUri(yamlSource, path, DERIVED_FROM);
    Optional<URI> createdBy = readOptionalUri(yamlSource, path, CREATED_BY);
    Optional<URI> modifiedBy = readOptionalUri(yamlSource, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOptionalOffsetDatetime(yamlSource, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOptionalOffsetDatetime(yamlSource, path, LAST_UPDATED_ON);
    Map<String, ElementSchemaArtifact> elementSchemas = Collections.EMPTY_MAP; // TODO
    Map<String, FieldSchemaArtifact> fieldSchemas = Collections.EMPTY_MAP; // TODO
    TemplateUi templateUi = readTemplateUi(yamlSource, path);

    return TemplateSchemaArtifact.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas, templateUi);
  }

  /**
   * Read a YAML specification of an element schema artifact
   * <p></p>
   * e.g.,
   * <pre>
   * element: Address
   * description: Address element
   * identifier: SFY343
   * version: 1.0.0
   * status: published
   * isMultiple: true
   * minItems: 0
   * maxItems: 4
   *
   * children:
   *  - field: Address 1
   *    inputType: textfield
   *    datatype: xsd:string
   *  - field: ZIP
   *    inputType: textfield
   *    datatype: xsd:string
   *    minLength: 5
   *    maxLength: 5
   * </pre>
   */
  @Override public ElementSchemaArtifact readElementSchemaArtifact(LinkedHashMap<String, Object> yamlSource)
  {
    String path = "top level";
    String name = readRequiredString(yamlSource, path, ELEMENT, false);

    return readElementSchemaArtifact(yamlSource, path, name, false,
      Optional.empty(), Optional.empty(), Optional.empty());
  }

  private ElementSchemaArtifact readElementSchemaArtifact(LinkedHashMap<String, Object> yamlSource, String path,
    String name, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri)
  {
    URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
    String jsonSchemaTitle = name + " element";
    String jsonSchemaDescription = name + " element generated from YAML";

    Map<String, URI> jsonLdContext = new HashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readOptionalUri(yamlSource, path, ID);

    String description = readStringWithDefaultValue(yamlSource, path, DESCRIPTION, "");
    Optional<String> identifier = readOptionalString(yamlSource, path, IDENTIFIER, true);
    Optional<Version> version = readOptionalVersion(yamlSource, path, VERSION);
    Version modelVersion = readRequiredVersion(yamlSource, path, MODEL_VERSION);
    Optional<Status> status = readOptionalStatus(yamlSource, path, STATUS);
    Optional<URI> previousVersion = readOptionalUri(yamlSource, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readOptionalUri(yamlSource, path, DERIVED_FROM);
    Optional<URI> createdBy = readOptionalUri(yamlSource, path, CREATED_BY);
    Optional<URI> modifiedBy = readOptionalUri(yamlSource, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOptionalOffsetDatetime(yamlSource, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOptionalOffsetDatetime(yamlSource, path, LAST_UPDATED_ON);
    Map<String, ElementSchemaArtifact> elementSchemas = Collections.EMPTY_MAP; // TODO
    Map<String, FieldSchemaArtifact> fieldSchemas = Collections.EMPTY_MAP; // TODO
    ElementUi elementUi = readElementUi(yamlSource, path);

    return ElementSchemaArtifact.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas, elementUi,
      isMultiple, minItems, maxItems, propertyUri);
  }

  /**
   * Read a YAML specification of a field schema artifact
   *
   * e.g.,
   * <pre>
   * field: Disease
   * inputType: textfield
   * datatype: xsd:anyURI
   * values:
   *       - ontology: Human Disease Ontology
   *         acronym: DOID
   *         termUri: "https://data.bioontology.org/ontologies/DOID"
   *       - branch: Disease
   *         acronym: DPCO
   *         termUri: "http://purl.org/twc/dpo/ont/Disease"
   *       - class: Translated Title
   *         source: DATACITE-VOCAB
   *         termUri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   *         type: OntologyClass
   * </pre>
   */
  @Override public FieldSchemaArtifact readFieldSchemaArtifact(LinkedHashMap<String, Object> yamlSource)
  {
    {
      String path = "top level";
      String name = readRequiredString(yamlSource, path, FIELD, false);

      return readFieldSchemaArtifact(yamlSource, path, name, false,
        Optional.empty(), Optional.empty(), Optional.empty());
    }
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(LinkedHashMap<String, Object> yamlSource, String path,
    String name, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri)
  {
    URI jsonSchemaSchemaUri = URI.create(ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI);
    String jsonSchemaType = FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
    String jsonSchemaTitle = name + " field";
    String jsonSchemaDescription = name + " field generated from YAML";

    Map<String, URI> jsonLdContext = new HashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readOptionalUri(yamlSource, path, ID);

    String description = readStringWithDefaultValue(yamlSource, path, DESCRIPTION, "");
    Optional<String> identifier = readOptionalString(yamlSource, path, IDENTIFIER, true);
    Optional<Version> version = readOptionalVersion(yamlSource, path, VERSION);
    Version modelVersion = readRequiredVersion(yamlSource, path, MODEL_VERSION);
    Optional<Status> status = readOptionalStatus(yamlSource, path, STATUS);
    Optional<URI> previousVersion = readOptionalUri(yamlSource, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readOptionalUri(yamlSource, path, DERIVED_FROM);
    Optional<URI> createdBy = readOptionalUri(yamlSource, path, CREATED_BY);
    Optional<URI> modifiedBy = readOptionalUri(yamlSource, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOptionalOffsetDatetime(yamlSource, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOptionalOffsetDatetime(yamlSource, path, LAST_UPDATED_ON);
    FieldUi fieldUi = readFieldUi(yamlSource, path);
    Optional<ValueConstraints> valueConstraints = readValueConstraints(yamlSource, path);
    Optional<String> skosPrefLabel = Optional.empty(); // TODO
    List<String> skosAlternateLabels = Collections.emptyList(); // TODO

    return FieldSchemaArtifact.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      isMultiple, minItems, maxItems, propertyUri,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldUi, skosPrefLabel, skosAlternateLabels, valueConstraints);
  }

  @Override public TemplateInstanceArtifact readTemplateInstanceArtifact(LinkedHashMap<String, Object> yamlSource)
  {
    return null; // TODO
  }

  private TemplateUi readTemplateUi(LinkedHashMap<String, Object> yamlSource, String path)
  {

    return TemplateUi.builder().build(); // TODO
    // TODO Read YAML for header/footer
    // TODO Read YAML for UI.propertyLabels, UI.propertyDescriptions
    // TODO Read YAML for childPropertyUris
  }

  private ElementUi readElementUi(LinkedHashMap<String, Object> yamlSource, String path)
  {

    return ElementUi.builder().build(); // TODO
    // TODO Read YAML for UI.propertyLabels, UI.propertyDescriptions
    // TODO Read YAML for childPropertyUris
  }

  private FieldUi readFieldUi(LinkedHashMap<String, Object> yamlSource, String path)
  {
    String uiPath = path + "/" + UI;

//    FieldInputType fieldInputType = readFieldInputType(yamlSource, uiPath, INPUT_TYPE);
//    boolean valueRecommendationEnabled = readBoolean(yamlSource, uiPath, UI_VALUE_RECOMMENDATION_ENABLED, false);
//    boolean hidden = readBoolean(yamlSource, uiPath, UI_HIDDEN, false);
//
//    if (fieldInputType.isTemporal()) {
//      TemporalGranularity temporalGranularity = readTemporalGranularity(yamlSource, uiPath);
//      InputTimeFormat inputTimeFormat = readInputTimeFormat(yamlSource, uiPath, InputTimeFormat.TWELVE_HOUR);
//      boolean timeZoneEnabled = readBoolean(yamlSource, uiPath, UI_TIMEZONE_ENABLED, false);
//
//      return TemporalFieldUi.create(temporalGranularity, inputTimeFormat, timeZoneEnabled, hidden);
//    } else if (fieldInputType.isNumeric()) {
//      return NumericFieldUi.create(hidden);
//    } else if (fieldInputType.isStatic()) {
//      String content = readRequiredString(yamlSource, uiPath, CONTENT);
//      return StaticFieldUi.create(fieldInputType, content, hidden);
//    } else
//      return FieldUi.create(fieldInputType, hidden, valueRecommendationEnabled);
    return FieldUi.create(FieldInputType.EMAIL, false, false); // TODO
  }

  private Optional<ValueConstraints> readValueConstraints(LinkedHashMap<String, Object> yamlSource, String path)
  {
    return Optional.empty(); // TODO
  }

  private String readRequiredString(LinkedHashMap<String, Object> yamlSource, String path, String fieldName, boolean allowEmpty)
  {
    if (!yamlSource.containsKey(fieldName))
      throw new ArtifactParseException("No keyword", fieldName, path);

    Object rawValue = yamlSource.get(fieldName);

    if (rawValue == null)
      throw new ArtifactParseException("No value", fieldName, path);

    if (!(rawValue instanceof String))
      throw new ArtifactParseException("Expecting string value, got " + rawValue.getClass(), fieldName, path);

    String value = (String)rawValue;

    if (value.isEmpty() && !allowEmpty)
      throw new ArtifactParseException("Expecting non-empty string value", fieldName, path);

    return value;
  }

  private String readStringWithDefaultValue(LinkedHashMap<String, Object> yamlSource, String path, String fieldName, String defaultValue)
  {
    Optional<String> optionalString = readOptionalString(yamlSource, path, fieldName, true);

    if (optionalString.isPresent())
      return optionalString.get();
    else
      return defaultValue;
  }

  private Optional<String> readOptionalString(LinkedHashMap<String, Object> yamlSource, String path, String fieldName, boolean allowEmpty)
  {
    if (!yamlSource.containsKey(fieldName))
      return Optional.empty();

    Object rawValue = yamlSource.get(fieldName);

    if (rawValue == null)
      return Optional.empty();

    if (!(rawValue instanceof String))
      throw new ArtifactParseException("Expecting string value, got " + rawValue.getClass(), fieldName, path);

    String value = (String)rawValue;

    if (value.isEmpty() && !allowEmpty)
      return Optional.empty();

    return Optional.of(value);
  }

  private Version readRequiredVersion(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    String versionString = readRequiredString(yamlSource, path, fieldName, false);

    if (Version.isValidVersion(versionString))
      return Version.fromString(versionString);
    else
      throw new ArtifactParseException("Invalid version " + versionString, fieldName, path);
  }

  private FieldInputType readFieldInputType(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    String inputTypeString = readRequiredString(yamlSource, path, fieldName, false);

    if (!INPUT_TYPES.contains(inputTypeString))
      throw new ArtifactParseException("Invalid field input type " + inputTypeString, UI_FIELD_INPUT_TYPE, path);

    return FieldInputType.fromString(inputTypeString);
  }

  private Optional<Version> readOptionalVersion(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    Optional<String> versionString = readOptionalString(yamlSource, path, fieldName, false);

    if (!versionString.isPresent())
      return Optional.empty();

    if (Version.isValidVersion(versionString.get()))
      return Optional.of(Version.fromString(versionString.get()));
    else
      throw new ArtifactParseException("Invalid version " + versionString.get(), fieldName, path);
  }

  private Status readRequiredStatus(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    String statusString = readRequiredString(yamlSource, path, fieldName, false);

    if (Status.isValidStatus(statusString))
      return Status.fromString(statusString);
    else
      throw new ArtifactParseException("Invalid status " + statusString, fieldName, path);
  }

  private Optional<Status> readOptionalStatus(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    Optional<String> statusString = readOptionalString(yamlSource, path, fieldName, false);

    if (!statusString.isPresent())
      return Optional.empty();

    if (Status.isValidStatus(statusString.get()))
      return Optional.of(Status.fromString(statusString.get()));
    else
      throw new ArtifactParseException("Invalid status " + statusString.get(), fieldName, path);
  }

  private URI readRequiredUri(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    String uriString = readRequiredString(yamlSource, path, fieldName, false);

    try {
      return new URI(uriString);
    } catch (Exception e) {
      throw new ArtifactParseException("Invalid URI " + uriString, fieldName, path);
    }
  }

  private Optional<URI> readOptionalUri(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    Optional<String> uriString = readOptionalString(yamlSource, path, fieldName, false);

    if (!uriString.isPresent())
      return Optional.empty();

    try {
      return Optional.of(new URI(uriString.get()));
    } catch (Exception e) {
      throw new ArtifactParseException("Invalid URI " + uriString.get(), fieldName, path);
    }
  }

  private Optional<OffsetDateTime> readOptionalOffsetDatetime(LinkedHashMap<String, Object> yamlSource, String path, String fieldName)
  {
    Optional<String> dateTimeValue = readOptionalString(yamlSource, path, fieldName, false);

    try {
      if (dateTimeValue.isPresent())
        return Optional.of(OffsetDateTime.parse(dateTimeValue.get()));
      else
        return Optional.empty();
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException(
        "Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(), fieldName, path);
    }
  }

}