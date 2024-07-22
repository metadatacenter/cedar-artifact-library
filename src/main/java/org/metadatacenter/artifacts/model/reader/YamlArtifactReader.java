package org.metadatacenter.artifacts.model.reader;

import org.metadatacenter.artifacts.model.core.Annotations;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TemporalDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.ui.ElementUi;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.NumericFieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.ui.TemplateUi;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ALT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTINUE_PREVIOUS_LINE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FOOTER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.GRANULARITY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEADER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEIGHT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_FORMAT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_ZONE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LANGUAGE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODEL_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTIPLE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ORDER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREF_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_LABELS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RECOMMENDED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_RECOMMENDATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.WIDTH;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.INPUT_TYPES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTIONS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_CLASSES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DECIMAL_PLACE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LITERALS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_STRING_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_STRING_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MULTIPLE_CHOICE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_RECOMMENDED_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_REQUIRED_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TEMPORAL_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_UNIT_OF_MEASURE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS;
import static org.metadatacenter.model.ModelNodeValues.TEMPORAL_GRANULARITIES;
import static org.metadatacenter.model.ModelNodeValues.TIME_FORMATS;

public class YamlArtifactReader implements ArtifactReader<LinkedHashMap<String, Object>>
{
  private final Version modelVersion = Version.fromString("1.6.0");

  public YamlArtifactReader() {}

  /**
   * Read a YAML specification of a template schema artifact
   * <p></p>
   * e.g.,
   * template: Study
   * description: Study template
   * identifier: SFY343
   * version: 1.0.0
   * status: published
   * <p>
   * children:
   * <p>
   * - key: study-name
   * type: text-field
   * name: Study Name
   * description: Study name field
   * configuration:
   * required: true
   * <p>
   * - type: text-field
   * name: Study ID
   * description: Study ID field
   * minLength: 2
   * configuration:
   * required: true
   * <p>
   * - key: address
   * type: element
   * name: Address
   * description: Address element
   * configuration:
   * isMultiple: true
   * minItems: 0
   * maxItems: 4
   * <p>
   * children:
   * - key: address-1
   * type: text-field
   * name: field: Address 1
   * - key: zip
   * type: text-field
   * name: field: ZIP
   * minLength: 5
   * maxLength: 5
   * </pre>
   */
  @Override public TemplateSchemaArtifact readTemplateSchemaArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    String name = readRequiredString(sourceNode, path, TEMPLATE, false);

    return readTemplateSchemaArtifact(sourceNode, path, name);
  }

  /**
   * Read a YAML specification of an element schema artifact
   * <p></p>
   * e.g.,
   * <pre>
   *   - key: address
   *     type: element
   *     name: Address
   *     description: Address element
   *     configuration:
   *       isMultiple: true
   *       minItems: 0
   *       maxItems: 4
   *
   *     children:
   *       - key: address-1
   *         type: text-field
   *         name: field: Address 1
   *       - key: zip
   *         type: text-field
   *         name: field: ZIP
   *         minLength: 5
   *         maxLength: 5
   *
   * </pre>
   */
  @Override public ElementSchemaArtifact readElementSchemaArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    String name = readRequiredString(sourceNode, path, ELEMENT, false);
    boolean isMultiple = readBoolean(sourceNode, path, MULTIPLE, false);
    Optional<Integer> minItems = readInteger(sourceNode, path, MIN_ITEMS);
    Optional<Integer> maxItems = readInteger(sourceNode, path, MAX_ITEMS);
    Optional<URI> propertyUri = readUri(sourceNode, path, PROPERTY_IRI);

    return readElementSchemaArtifact(sourceNode, path, name, isMultiple, minItems, maxItems, propertyUri);
  }

  /**
   * Read a YAML specification of a field schema artifact
   * <p>
   * e.g.,
   * <pre>
   * type: controlled-term-field
   * name: Disease
   * values:
   *   - branch: Disease
   *     acronym: DPCO
   *     termUri: "http://purl.org/twc/dpo/ont/Disease"
   *   - type: ontology
   *     source: DOID
   *     name: Human Disease Ontology
   *     acronym: DOID
   *     iri: "https://data.bioontology.org/ontologies/DOID"
   *   - class: Translated Title
   *     source: DATACITE-VOCAB
   *     termUri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   *     type: OntologyClass
   * </pre>
   */
  @Override public FieldSchemaArtifact readFieldSchemaArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    String name = readRequiredString(sourceNode, path, NAME, false);
    boolean isMultiple = readBoolean(sourceNode, path, MULTIPLE, false);
    Optional<Integer> minItems = readInteger(sourceNode, path, MIN_ITEMS);
    Optional<Integer> maxItems = readInteger(sourceNode, path, MAX_ITEMS);
    Optional<URI> propertyUri = readUri(sourceNode, path, PROPERTY_IRI);

    return readFieldSchemaArtifact(sourceNode, path, name, isMultiple, minItems, maxItems, propertyUri);
  }

  @Override public TemplateInstanceArtifact readTemplateInstanceArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    return null; // TODO Read template instance artifacts
  }

  private TemplateSchemaArtifact readTemplateSchemaArtifact(LinkedHashMap<String, Object> sourceNode, String path,
    String name)
  {
    String internalName = name + " template";
    String internalDescription = name + " template generated from YAML";

    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readUri(sourceNode, path, ID);
    Optional<URI> instanceJsonLdType = Optional.empty(); // TODO Read instance JSON-LD type
    String description = readString(sourceNode, path, DESCRIPTION, "");
    Optional<String> identifier = readString(sourceNode, path, IDENTIFIER, true);
    Optional<Version> version = readVersion(sourceNode, path, VERSION);
    Optional<Status> status = readStatus(sourceNode, path, STATUS);
    Optional<URI> previousVersion = readUri(sourceNode, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, DERIVED_FROM);
    Optional<URI> createdBy = readUri(sourceNode, path, CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDatetime(sourceNode, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDatetime(sourceNode, path, MODIFIED_ON);
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>(); // TODO Read child elements
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>(); // TODO Read child fields
    Optional<String> language = readString(sourceNode, path, LANGUAGE);
    TemplateUi templateUi = readTemplateUi(sourceNode, path);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path);

    checkSchemaArtifactModelVersion(sourceNode, path);

    return TemplateSchemaArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, instanceJsonLdType, name, description,
      identifier, version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas, language, templateUi, annotations, internalName, internalDescription);
  }

  private ElementSchemaArtifact readElementSchemaArtifact(LinkedHashMap<String, Object> sourceNode, String path,
    String name, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri)
  {
    String internalName = name + " element";
    String internalDescription = name + " element generated from YAML";
    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readUri(sourceNode, path, ID);
    Optional<URI> instanceJsonLdType = Optional.empty(); // TODO Read instance JSON-LD type
    String description = readString(sourceNode, path, DESCRIPTION, "");
    Optional<String> identifier = readString(sourceNode, path, IDENTIFIER, true);
    Optional<Version> version = readVersion(sourceNode, path, VERSION);
    Optional<Status> status = readStatus(sourceNode, path, STATUS);
    Optional<URI> previousVersion = readUri(sourceNode, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, DERIVED_FROM);
    Optional<URI> createdBy = readUri(sourceNode, path, CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDatetime(sourceNode, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDatetime(sourceNode, path, MODIFIED_ON);
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>(); // TODO  Read child elements
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>(); // TODO  Read child fields
    Optional<String> preferredLabel = readString(sourceNode, path, PREF_LABEL);
    Optional<String> language = readString(sourceNode, path, LANGUAGE);
    ElementUi elementUi = readElementUi(sourceNode, path);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path);

    checkSchemaArtifactModelVersion(sourceNode, path);

    return ElementSchemaArtifact.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId,
      instanceJsonLdType, name, description, identifier, version, status, previousVersion, derivedFrom, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, fieldSchemas, elementSchemas, isMultiple, minItems, maxItems, propertyUri,
      preferredLabel, language, elementUi, annotations);
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(LinkedHashMap<String, Object> sourceNode, String path,
    String name, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri)
  {
    String internalName = name + " field";
    String internalDescription = name + " field generated from YAML";

    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readUri(sourceNode, path, ID);
    String description = readString(sourceNode, path, DESCRIPTION, "");
    Optional<String> identifier = readString(sourceNode, path, IDENTIFIER, true);
    Optional<Version> version = readVersion(sourceNode, path, VERSION);
    Optional<Status> status = readStatus(sourceNode, path, STATUS);
    Optional<URI> previousVersion = readUri(sourceNode, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, DERIVED_FROM);
    Optional<URI> createdBy = readUri(sourceNode, path, CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDatetime(sourceNode, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDatetime(sourceNode, path, MODIFIED_ON);
    Optional<XsdDatatype> datatype = readXsdDatatype(sourceNode, path, DATATYPE);
    FieldUi fieldUi = readFieldUi(sourceNode, path);
    Optional<ValueConstraints> valueConstraints = readValueConstraints(sourceNode, path, VALUES, fieldUi.inputType());
    Optional<String> preferredLabel = readString(sourceNode, path, PREF_LABEL);
    List<String> alternateLabels = readStringArray(sourceNode, path, ALT_LABEL);
    Optional<String> language = readString(sourceNode, path, LANGUAGE);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path);

    checkSchemaArtifactModelVersion(sourceNode, path);

    return FieldSchemaArtifact.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId, name,
      description, identifier, version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems,
      propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi,
      valueConstraints, annotations);
  }

  private TemplateUi readTemplateUi(LinkedHashMap<String, Object> sourceNode, String path)
  {
    List<String> order = readStringArray(sourceNode, path, ORDER);
    LinkedHashMap<String, String> propertyLabels = readString2StringMap(sourceNode, path, PROPERTY_LABELS);
    LinkedHashMap<String, String> propertyDescriptions = readString2StringMap(sourceNode, path, PROPERTY_DESCRIPTIONS);
    Optional<String> header = readString(sourceNode, path, HEADER);
    Optional<String> footer = readString(sourceNode, path, FOOTER);

    return TemplateUi.create(order, propertyLabels, propertyDescriptions, header, footer);
  }

  private ElementUi readElementUi(LinkedHashMap<String, Object> sourceNode, String path)
  {
    List<String> order = readStringArray(sourceNode, path, ORDER);
    LinkedHashMap<String, String> propertyLabels = readString2StringMap(sourceNode, path, PROPERTY_LABELS);
    LinkedHashMap<String, String> propertyDescriptions = readString2StringMap(sourceNode, path, PROPERTY_DESCRIPTIONS);

    return ElementUi.create(order, propertyLabels, propertyDescriptions);
  }

  private Optional<Annotations> readAnnotations(LinkedHashMap<String, Object> sourceNode, String path)
  {
    return Optional.empty(); // TODO Implement readAnnotations in YAML reader
  }

  private FieldUi readFieldUi(LinkedHashMap<String, Object> sourceNode, String path)
  {
    FieldInputType fieldInputType = readFieldInputType(sourceNode, path, TYPE);
    boolean valueRecommendationEnabled = readBoolean(sourceNode, path, VALUE_RECOMMENDATION, false);
    boolean hidden = readBoolean(sourceNode, path, HIDDEN, false);
    boolean recommendedValue = readBoolean(sourceNode, path, RECOMMENDED, false);
    boolean continuePreviousLine = readBoolean(sourceNode, path, CONTINUE_PREVIOUS_LINE, false);
    Optional<Integer> width = readInteger(sourceNode, path, WIDTH);
    Optional<Integer> height = readInteger(sourceNode, path, HEIGHT);

    if (fieldInputType.isTemporal()) {
      TemporalGranularity temporalGranularity = readTemporalGranularity(sourceNode, path, GRANULARITY);
      Optional<InputTimeFormat> inputTimeFormat = readInputTimeFormat(sourceNode, path, INPUT_TIME_FORMAT);
      Optional<Boolean> timeZoneEnabled = readBoolean(sourceNode, path, INPUT_TIME_ZONE);

      return TemporalFieldUi.create(temporalGranularity, inputTimeFormat, timeZoneEnabled, hidden,
        continuePreviousLine);
    } else if (fieldInputType.isNumeric()) {
      return NumericFieldUi.create(hidden, continuePreviousLine);
    } else if (fieldInputType.isStatic()) {
      Optional<String> content = readString(sourceNode, path, CONTENT, true);
      return StaticFieldUi.create(fieldInputType, content, hidden, continuePreviousLine, width, height);
    } else
      return FieldUi.create(fieldInputType, hidden, continuePreviousLine, valueRecommendationEnabled);
  }

  private Optional<ValueConstraints> readValueConstraints(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName, FieldInputType fieldInputType)
  {
    String vcPath = path + "/" + fieldName;
    LinkedHashMap<String, Object> vcNode = readChildNode(sourceNode, path, fieldName);

    if (vcNode != null) {
      boolean requiredValue = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_REQUIRED_VALUE, false);
      boolean recommendedValue = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_RECOMMENDED_VALUE, false);
      boolean multipleChoice = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_MULTIPLE_CHOICE, false);
      Optional<XsdNumericDatatype> numberType = readNumberType(vcNode, vcPath, VALUE_CONSTRAINTS_NUMBER_TYPE);
      Optional<XsdTemporalDatatype> temporalType = readTemporalType(vcNode, vcPath, VALUE_CONSTRAINTS_TEMPORAL_TYPE);
      Optional<String> unitOfMeasure = readString(vcNode, vcPath, VALUE_CONSTRAINTS_UNIT_OF_MEASURE);
      Optional<Number> minValue = readNumber(vcNode, vcPath, VALUE_CONSTRAINTS_MIN_NUMBER_VALUE);
      Optional<Number> maxValue = readNumber(vcNode, vcPath, VALUE_CONSTRAINTS_MAX_NUMBER_VALUE);
      Optional<Integer> decimalPlaces = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_DECIMAL_PLACE);
      Optional<Integer> minLength = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_MIN_STRING_LENGTH);
      Optional<Integer> maxLength = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_MAX_STRING_LENGTH);
      Optional<? extends DefaultValue> defaultValue = readDefaultValue(vcNode, vcPath, VALUE_CONSTRAINTS_DEFAULT_VALUE);
      Optional<String> regex = readString(vcNode, vcPath, "regex"); // TODO Add 'regex' to ModelNodeNames
      List<OntologyValueConstraint> ontologies = readOntologyValueConstraints(vcNode, vcPath,
        VALUE_CONSTRAINTS_ONTOLOGIES);
      List<ValueSetValueConstraint> valueSets = readValueSetValueConstraints(vcNode, vcPath,
        VALUE_CONSTRAINTS_VALUE_SETS);
      List<ClassValueConstraint> classes = readClassValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_CLASSES);
      List<BranchValueConstraint> branches = readBranchValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_BRANCHES);
      List<LiteralValueConstraint> literals = readLiteralValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_LITERALS);
      List<ControlledTermValueConstraintsAction> actions = readValueConstraintsActions(vcNode, vcPath,
        VALUE_CONSTRAINTS_ACTIONS);

      if (fieldInputType == FieldInputType.NUMERIC) {
        Optional<NumericDefaultValue> numericDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asNumericDefaultValue()) :
          Optional.empty();
        return Optional.of(
          NumericValueConstraints.create(numberType.get(), minValue, maxValue, decimalPlaces, unitOfMeasure,
            numericDefaultValue, requiredValue, recommendedValue, multipleChoice));
      } else if (fieldInputType == FieldInputType.TEMPORAL) {
        Optional<TemporalDefaultValue> temporalDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asTemporalDefaultValue()) :
          Optional.empty();
        return Optional.of(
          TemporalValueConstraints.create(temporalType.get(), temporalDefaultValue, requiredValue, recommendedValue,
            multipleChoice));

      } else if (fieldInputType == FieldInputType.LINK || (fieldInputType == FieldInputType.TEXTFIELD && (
        !ontologies.isEmpty() || !valueSets.isEmpty() || !classes.isEmpty() || !branches.isEmpty()))) {
        Optional<ControlledTermDefaultValue> controlledTermDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asControlledTermDefaultValue()) :
          Optional.empty();
        return Optional.of(
          ControlledTermValueConstraints.create(ontologies, valueSets, classes, branches, controlledTermDefaultValue,
            actions, requiredValue, recommendedValue, multipleChoice));
      } else {
        Optional<TextDefaultValue> textDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asTextDefaultValue()) :
          Optional.empty();
        return Optional.of(
          TextValueConstraints.create(minLength, maxLength, textDefaultValue, literals, requiredValue, recommendedValue,
            multipleChoice, regex));
      }
    } else
      return Optional.empty();
  }

  private String readRequiredString(LinkedHashMap<String, Object> sourceNode, String path, String fieldName,
    boolean allowEmpty)
  {
    if (!sourceNode.containsKey(fieldName))
      throw new ArtifactParseException("No field present", fieldName, path);

    Object rawValue = sourceNode.get(fieldName);

    if (rawValue == null)
      throw new ArtifactParseException("No value", fieldName, path);

    if (!(rawValue instanceof String))
      throw new ArtifactParseException("Expecting string value, got " + rawValue.getClass(), fieldName, path);

    String value = (String)rawValue;

    if (value.isEmpty() && !allowEmpty)
      throw new ArtifactParseException("Expecting non-empty string value", fieldName, path);

    return value;
  }

  private String readString(LinkedHashMap<String, Object> sourceNode, String path, String fieldName,
    String defaultValue)
  {
    Optional<String> optionalString = readString(sourceNode, path, fieldName, true);

    return optionalString.orElse(defaultValue);
  }

  private Optional<String> readString(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    return readString(sourceNode, path, fieldName, true);
  }

  private Optional<String> readString(LinkedHashMap<String, Object> sourceNode, String path, String fieldName,
    boolean allowEmpty)
  {
    if (!sourceNode.containsKey(fieldName))
      return Optional.empty();

    Object rawValue = sourceNode.get(fieldName);

    if (rawValue == null)
      return Optional.empty();

    if (!(rawValue instanceof String))
      throw new ArtifactParseException("Expecting string value, got " + rawValue.getClass(), fieldName, path);

    String value = (String)rawValue;

    if (value.isEmpty() && !allowEmpty)
      return Optional.empty();

    return Optional.of(value);
  }

  private Optional<Integer> readInteger(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    Object rawValue = sourceNode.get(fieldName);

    if (rawValue == null)
      return Optional.empty();

    if (!(rawValue instanceof Integer))
      throw new ArtifactParseException("Value must be an integer", fieldName, path);

    return Optional.of((Integer)rawValue);
  }

  private Optional<Number> readNumber(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    Object rawValue = sourceNode.get(fieldName);

    if (rawValue == null)
      return Optional.empty();

    if (!(rawValue instanceof Number))
      throw new ArtifactParseException("Value must be a number", fieldName, path);

    return Optional.of((Number)rawValue);
  }

  private boolean readBoolean(LinkedHashMap<String, Object> sourceNode, String path, String fieldName,
    boolean defaultValue)
  {
    if (!sourceNode.containsKey(fieldName))
      return defaultValue;

    Object rawValue = sourceNode.get(fieldName);

    if (rawValue == null)
      return defaultValue;

    if (!(rawValue instanceof Boolean))
      throw new ArtifactParseException("Expecting Boolean value, got " + rawValue.getClass(), fieldName, path);

    return (Boolean)rawValue;
  }

  private Optional<Boolean> readBoolean(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    if (!sourceNode.containsKey(fieldName))
      return Optional.empty();

    Object rawValue = sourceNode.get(fieldName);

    if (rawValue == null)
      return Optional.empty();

    if (!(rawValue instanceof Boolean))
      throw new ArtifactParseException("Expecting Boolean value, got " + rawValue.getClass(), fieldName, path);

    return Optional.of((Boolean)rawValue);
  }

  private List<String> readStringArray(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    Object rawValue = sourceNode.get(fieldName);
    List<String> stringValues = new ArrayList<>();

    if (rawValue != null) {
      if (rawValue instanceof List<?>) {
        List<?> rawListValues = (List<?>)rawValue;
        Iterator<?> rawListValueIterator = rawListValues.iterator();
        int arrayIndex = 0;
        while (rawListValueIterator.hasNext()) {
          Object rawListValue = rawListValueIterator.next();
          if (rawListValue instanceof String) {
            String stringValue = (String)rawListValue;
            if (!stringValue.isEmpty())
              stringValues.add(stringValue);
          } else
            throw new ArtifactParseException("Value in array at index " + arrayIndex + " must be a string", fieldName,
              path);
          arrayIndex++;
        }
      }
    }
    return stringValues;
  }

  private TemporalGranularity readTemporalGranularity(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    String granularityString = readRequiredString(sourceNode, path, fieldName, false);

    if (!TEMPORAL_GRANULARITIES.contains(granularityString))
      throw new ArtifactParseException("Invalid granularity" + granularityString, TYPE, path);

    return TemporalGranularity.fromString(granularityString);
  }

  private Optional<InputTimeFormat> readInputTimeFormat(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    Optional<String> inputTimeFormatString = readString(sourceNode, path, fieldName);

    if (inputTimeFormatString.isEmpty())
      return Optional.empty();

    if (!TIME_FORMATS.contains(inputTimeFormatString.get()))
      throw new ArtifactParseException("Invalid input time format" + inputTimeFormatString.get(), TYPE, path);

    return Optional.of(InputTimeFormat.fromString(inputTimeFormatString.get()));
  }

  private FieldInputType readFieldInputType(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    String inputTypeString = readRequiredString(sourceNode, path, fieldName, false);

    if (!INPUT_TYPES.contains(inputTypeString))
      throw new ArtifactParseException("Invalid field input type" + inputTypeString, TYPE, path);

    return FieldInputType.fromString(inputTypeString);
  }

  private Optional<Version> readVersion(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    Optional<String> versionString = readString(sourceNode, path, fieldName, false);

    if (versionString.isEmpty())
      return Optional.empty();

    if (Version.isValidVersion(versionString.get()))
      return Optional.of(Version.fromString(versionString.get()));
    else
      throw new ArtifactParseException("Invalid version " + versionString.get(), fieldName, path);
  }

  private Version readRequiredVersion(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    String versionString = readRequiredString(sourceNode, path, fieldName, false);

    if (Version.isValidVersion(versionString))
      return Version.fromString(versionString);
    else
      throw new ArtifactParseException("Invalid version " + versionString, fieldName, path);
  }

  private Optional<XsdNumericDatatype> readNumberType(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    Optional<String> numberTypeValue = readString(sourceNode, path, fieldName);

    if (numberTypeValue.isPresent())
      return Optional.of(XsdNumericDatatype.fromString(numberTypeValue.get()));
    else
      return Optional.empty();
  }

  private Optional<XsdTemporalDatatype> readTemporalType(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    Optional<String> temporalTypeValue = readString(sourceNode, path, fieldName);

    if (temporalTypeValue.isPresent())
      return Optional.of(XsdTemporalDatatype.fromString(temporalTypeValue.get()));
    else
      return Optional.empty();
  }

  private Status readRequiredStatus(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    String statusString = readRequiredString(sourceNode, path, fieldName, false);

    if (Status.isValidStatus(statusString))
      return Status.fromString(statusString);
    else
      throw new ArtifactParseException("Invalid status " + statusString, fieldName, path);
  }

  private Optional<Status> readStatus(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    Optional<String> statusString = readString(sourceNode, path, fieldName, false);

    if (statusString.isEmpty())
      return Optional.empty();

    if (Status.isValidStatus(statusString.get()))
      return Optional.of(Status.fromString(statusString.get()));
    else
      throw new ArtifactParseException("Invalid status " + statusString.get(), fieldName, path);
  }

  private Optional<XsdDatatype> readXsdDatatype(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    Optional<String> xsdDatatypeString = readString(sourceNode, path, fieldName, false);

    if (xsdDatatypeString.isEmpty())
      return Optional.empty();

    if (XsdDatatype.isKnownXsdDatatype(xsdDatatypeString.get()))
      return Optional.of(XsdDatatype.fromString(xsdDatatypeString.get()));
    else
      throw new ArtifactParseException("Invalid status " + xsdDatatypeString.get(), fieldName, path);
  }

  private URI readRequiredUri(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    String uriString = readRequiredString(sourceNode, path, fieldName, false);

    try {
      return new URI(uriString);
    } catch (Exception e) {
      throw new ArtifactParseException("Invalid URI " + uriString, fieldName, path);
    }
  }

  private Optional<URI> readUri(LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    Optional<String> uriString = readString(sourceNode, path, fieldName, false);

    if (uriString.isEmpty())
      return Optional.empty();

    try {
      return Optional.of(new URI(uriString.get()));
    } catch (Exception e) {
      throw new ArtifactParseException("Invalid URI " + uriString.get(), fieldName, path);
    }
  }

  private Optional<OffsetDateTime> readOffsetDatetime(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    Optional<String> dateTimeValue = readString(sourceNode, path, fieldName, false);

    try {
      if (dateTimeValue.isPresent())
        return Optional.of(OffsetDateTime.parse(dateTimeValue.get()));
      else
        return Optional.empty();
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException("Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(),
        fieldName, path);
    }
  }

  private Optional<DefaultValue> readDefaultValue(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    return Optional.empty(); // TODO Implement read default value
  }

  private List<OntologyValueConstraint> readOntologyValueConstraints(LinkedHashMap<String, Object> sourceNode,
    String path, String fieldName)
  {
    return Collections.emptyList(); // TODO Implement read ontology value constraints
  }

  private List<ClassValueConstraint> readClassValueConstraints(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    return Collections.emptyList(); // TODO Implement read class value constraints
  }

  private List<ValueSetValueConstraint> readValueSetValueConstraints(LinkedHashMap<String, Object> sourceNode,
    String path, String fieldName)
  {
    return Collections.emptyList(); // TODO Implement read value set value constraints
  }

  private List<BranchValueConstraint> readBranchValueConstraints(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    return Collections.emptyList(); // TODO Implement read branch value constraints
  }

  private List<LiteralValueConstraint> readLiteralValueConstraints(LinkedHashMap<String, Object> sourceNode,
    String path, String fieldName)
  {
    return Collections.emptyList(); // TODO Implement read literal value constraints
  }

  private List<ControlledTermValueConstraintsAction> readValueConstraintsActions(
    LinkedHashMap<String, Object> sourceNode, String path, String fieldName)
  {
    return Collections.emptyList(); // TODO Implement read actions value constraints
  }

  private LinkedHashMap<String, String> readString2StringMap(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldName)
  {
    return new LinkedHashMap<>(); // TODO Implement readString2StringMap
  }

  private LinkedHashMap<String, Object> readChildNode(LinkedHashMap<String, Object> parentNode, String path,
    String fieldName)
  {
    return new LinkedHashMap<>(); // TODO Implement read child node
  }

  private void checkSchemaArtifactModelVersion(LinkedHashMap<String, Object> sourceNode, String path)
  {
    Version artifactModelVersion = readRequiredVersion(sourceNode, path, MODEL_VERSION);

    if (!artifactModelVersion.equals(modelVersion))
      throw new ArtifactParseException("Expecting model version " + modelVersion + ", got " + artifactModelVersion,
        MODEL_VERSION, path);
  }

}
