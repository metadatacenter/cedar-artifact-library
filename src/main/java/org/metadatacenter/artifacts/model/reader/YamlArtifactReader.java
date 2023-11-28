package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementUi;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateUi;
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

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
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
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;

public class YamlArtifactReader implements ArtifactReader<LinkedHashMap<String, Object>>
{
  public YamlArtifactReader()
  {}

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
    String name = getRequiredString(TEMPLATE, false, yamlSource, path);

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
    Optional<URI> jsonLdId = getOptionalUri(ID, yamlSource, path);

    String description = getStringWithDefaultValue(DESCRIPTION, "", yamlSource, path);
    Optional<String> identifier = getOptionalString(IDENTIFIER, true, yamlSource, path);
    Optional<Version> version = getOptionalVersion(VERSION, yamlSource, path);
    Version modelVersion = getRequiredVersion(MODEL_VERSION, yamlSource, path);
    Optional<Status> status = getOptionalStatus(STATUS, yamlSource, path);
    Optional<URI> previousVersion = getOptionalUri(PREVIOUS_VERSION, yamlSource, path);
    Optional<URI> derivedFrom = getOptionalUri(DERIVED_FROM, yamlSource, path);
    Optional<URI> createdBy = getOptionalUri(CREATED_BY, yamlSource, path);
    Optional<URI> modifiedBy = getOptionalUri(MODIFIED_BY, yamlSource, path);
    Optional<OffsetDateTime> createdOn = getOptionalOffsetDatetime(CREATED_ON, yamlSource, path);
    Optional<OffsetDateTime> lastUpdatedOn = getOptionalOffsetDatetime(LAST_UPDATED_ON, yamlSource, path);
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
    String name = getRequiredString(ELEMENT, false, yamlSource, path);

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
    Optional<URI> jsonLdId = getOptionalUri(ID, yamlSource, path);

    String description = getStringWithDefaultValue(DESCRIPTION, "", yamlSource, path);
    Optional<String> identifier = getOptionalString(IDENTIFIER, true, yamlSource, path);
    Optional<Version> version = getOptionalVersion(VERSION, yamlSource, path);
    Version modelVersion = getRequiredVersion(MODEL_VERSION, yamlSource, path);
    Optional<Status> status = getOptionalStatus(STATUS, yamlSource, path);
    Optional<URI> previousVersion = getOptionalUri(PREVIOUS_VERSION, yamlSource, path);
    Optional<URI> derivedFrom = getOptionalUri(DERIVED_FROM, yamlSource, path);
    Optional<URI> createdBy = getOptionalUri(CREATED_BY, yamlSource, path);
    Optional<URI> modifiedBy = getOptionalUri(MODIFIED_BY, yamlSource, path);
    Optional<OffsetDateTime> createdOn = getOptionalOffsetDatetime(CREATED_ON, yamlSource, path);
    Optional<OffsetDateTime> lastUpdatedOn = getOptionalOffsetDatetime(LAST_UPDATED_ON, yamlSource, path);
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

  @Override public FieldSchemaArtifact readFieldSchemaArtifact(LinkedHashMap<String, Object> yamlSource)
  {
    {
      String path = "top level";
      String name = getRequiredString(FIELD, false, yamlSource, path);

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
    Optional<URI> jsonLdId = getOptionalUri(ID, yamlSource, path);

    String description = getStringWithDefaultValue(DESCRIPTION, "", yamlSource, path);
    Optional<String> identifier = getOptionalString(IDENTIFIER, true, yamlSource, path);
    Optional<Version> version = getOptionalVersion(VERSION, yamlSource, path);
    Version modelVersion = getRequiredVersion(MODEL_VERSION, yamlSource, path);
    Optional<Status> status = getOptionalStatus(STATUS, yamlSource, path);
    Optional<URI> previousVersion = getOptionalUri(PREVIOUS_VERSION, yamlSource, path);
    Optional<URI> derivedFrom = getOptionalUri(DERIVED_FROM, yamlSource, path);
    Optional<URI> createdBy = getOptionalUri(CREATED_BY, yamlSource, path);
    Optional<URI> modifiedBy = getOptionalUri(MODIFIED_BY, yamlSource, path);
    Optional<OffsetDateTime> createdOn = getOptionalOffsetDatetime(CREATED_ON, yamlSource, path);
    Optional<OffsetDateTime> lastUpdatedOn = getOptionalOffsetDatetime(LAST_UPDATED_ON, yamlSource, path);
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

  private String getRequiredString(String fieldName, boolean allowEmpty, LinkedHashMap<String, Object> yamlSource, String path)
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

  private String getStringWithDefaultValue(String fieldName, String defaultValue, LinkedHashMap<String, Object> yamlSource, String path)
  {
    Optional<String> optionalString = getOptionalString(fieldName, true, yamlSource, path);

    if (optionalString.isPresent())
      return optionalString.get();
    else
      return defaultValue;
  }

  private Optional<String> getOptionalString(String fieldName, boolean allowEmpty, LinkedHashMap<String, Object> yamlSource, String path)
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

  private Version getRequiredVersion(String fieldName, LinkedHashMap<String, Object> yamlSource, String path)
  {
    String versionString = getRequiredString(fieldName, false, yamlSource, path);

    if (Version.isValidVersion(versionString))
      return Version.fromString(versionString);
    else
      throw new ArtifactParseException("Invalid version " + versionString, fieldName, path);
  }

  private Optional<Version> getOptionalVersion(String fieldName, LinkedHashMap<String, Object> yamlSource, String path)
  {
    Optional<String> versionString = getOptionalString(fieldName, false, yamlSource, path);

    if (!versionString.isPresent())
      return Optional.empty();

    if (Version.isValidVersion(versionString.get()))
      return Optional.of(Version.fromString(versionString.get()));
    else
      throw new ArtifactParseException("Invalid version " + versionString.get(), fieldName, path);
  }

  private Status getRequiredStatus(String fieldName, LinkedHashMap<String, Object> yamlSource, String path)
  {
    String statusString = getRequiredString(fieldName, false, yamlSource, path);

    if (Status.isValidStatus(statusString))
      return Status.fromString(statusString);
    else
      throw new ArtifactParseException("Invalid status " + statusString, fieldName, path);
  }

  private Optional<Status> getOptionalStatus(String fieldName, LinkedHashMap<String, Object> yamlSource, String path)
  {
    Optional<String> statusString = getOptionalString(fieldName, false, yamlSource, path);

    if (!statusString.isPresent())
      return Optional.empty();

    if (Status.isValidStatus(statusString.get()))
      return Optional.of(Status.fromString(statusString.get()));
    else
      throw new ArtifactParseException("Invalid status " + statusString.get(), fieldName, path);
  }

  private URI getRequiredUri(String fieldName, LinkedHashMap<String, Object> yamlSource, String path)
  {
    String uriString = getRequiredString(fieldName, false, yamlSource, path);

    try {
      return new URI(uriString);
    } catch (Exception e) {
      throw new ArtifactParseException("Invalid URI " + uriString, fieldName, path);
    }
  }

  private Optional<URI> getOptionalUri(String fieldName, LinkedHashMap<String, Object> yamlSource, String path)
  {
    Optional<String> uriString = getOptionalString(fieldName, false, yamlSource, path);

    if (!uriString.isPresent())
      return Optional.empty();

    try {
      return Optional.of(new URI(uriString.get()));
    } catch (Exception e) {
      throw new ArtifactParseException("Invalid URI " + uriString.get(), fieldName, path);
    }
  }

  private Optional<OffsetDateTime> getOptionalOffsetDatetime(String fieldName, LinkedHashMap<String, Object> yamlSource, String path)
  {
    Optional<String> dateTimeValue = getOptionalString(fieldName, false, yamlSource, path);

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
    return FieldUi.builder().withInputType(FieldInputType.TEXTFIELD).build(); // TODO
  }

  private Optional<ValueConstraints> readValueConstraints(LinkedHashMap<String, Object> yamlSource, String path)
  {
    return Optional.empty(); // TODO
  }
}