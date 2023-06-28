package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.formula.functions.Mode;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.core.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.DefaultValue;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementUI;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUI;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.InstanceArtifact;
import org.metadatacenter.artifacts.model.core.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.NumberType;
import org.metadatacenter.artifacts.model.core.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.StringDefaultValue;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateUI;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.TemporalType;
import org.metadatacenter.artifacts.model.core.URIStringPairDefaultValue;
import org.metadatacenter.artifacts.model.core.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ArtifactReader
{
  private final ObjectMapper mapper;

  public ArtifactReader(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  public TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode objectNode)
  {
    return readTemplateSchemaArtifact(objectNode, "");
  }

  public ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode)
  {
    return readElementSchemaArtifact(objectNode, "");
  }

  public FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode objectNode)
  {
    return readFieldSchemaArtifact(objectNode, "");
  }

  public TemplateInstanceArtifact readTemplateInstanceArtifact(ObjectNode objectNode, String path)
  {
    InstanceArtifact instanceArtifact = readInstanceArtifact(objectNode, path);
    String isBasedOn = readRequiredIsBasedOnField(objectNode, path);
    Map<String, List<ElementInstanceArtifact>> elementInstances = new HashMap<>();
    Map<String, List<FieldInstanceArtifact>> fieldInstances = new HashMap<>();

    return new TemplateInstanceArtifact(instanceArtifact, isBasedOn, elementInstances, fieldInstances);
  }

  public ElementInstanceArtifact readElementInstanceArtifact(ObjectNode objectNode, String path)
  {
    InstanceArtifact instanceArtifact = readInstanceArtifact(objectNode, path);
    Map<String, List<FieldInstanceArtifact>> fieldInstances = new HashMap<>();
    Map<String, List<ElementInstanceArtifact>> elementInstances = new HashMap<>();

    readNestedInstanceArtifacts(objectNode, path, fieldInstances, elementInstances);

    ElementInstanceArtifact elementInstanceArtifact = new ElementInstanceArtifact(instanceArtifact, fieldInstances,
      elementInstances);

    return elementInstanceArtifact;
  }

  public FieldInstanceArtifact readFieldInstanceArtifact(ObjectNode objectNode, String path)
  {
    InstanceArtifact instanceArtifact = readInstanceArtifact(objectNode, path);
    String jsonLdValue = readJsonLDValueField(objectNode, path);
    Optional<String> rdfsLabel = readOptionalRDFSLabelField(objectNode, path);
    Optional<String> skosNotation = readSKOSNotationField(objectNode, path);
    Optional<String> skosPrefLabel = readSKOSPrefLabelField(objectNode, path);

    FieldInstanceArtifact fieldInstanceArtifact = new FieldInstanceArtifact(instanceArtifact, jsonLdValue, rdfsLabel,
      skosNotation, skosPrefLabel);

    return fieldInstanceArtifact;
  }

  private InstanceArtifact readInstanceArtifact(ObjectNode objectNode, String path)
  {
    Artifact artifact = readArtifact(objectNode, path);

    return new InstanceArtifact(artifact);
  }

  private TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode objectNode, String path)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, path);
    Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    Map<String, URI> childPropertyURIs = getChildPropertyURIs(objectNode, path);
    TemplateUI templateUI = readTemplateUI(objectNode, path);

    checkTemplateSchemaArtifactJSONLDType(schemaArtifact.getJsonLdTypes(), path);

    readNestedFieldAndElementSchemaArtifacts(objectNode, path, fieldSchemas, elementSchemas);

    // Get all names of nested field and element schemas
    // Then look inside properties/@Context for name->URI map


    return new TemplateSchemaArtifact(schemaArtifact, fieldSchemas, elementSchemas, childPropertyURIs, templateUI);
  }

  // A schema artifact's JSON Schema 'properties' object can contain a specification for a JSON-LD @context for
  // the corresponding instance; this @context maps field names to a URI that represents a property specifications for each field.
  //
  // e.g.,
  //
  //    "properties": {
  //      "@context": {
  //         "type": "object",
  //          ....
  //          "Study ID": { "enum": [ "http://semantic-dicom.org/dcm#StudyID" ]
  //          "Disease": { "enum": [ "http://semantic-dicom.org/dcm#Disease" ]
  //      }
  //    }
  private  Map<String, URI> getChildPropertyURIs(ObjectNode objectNode, String path)
  {
    Map<String, URI> fieldNames2URI = new HashMap<>();
    String contextPath = "/" + ModelNodeNames.JSON_SCHEMA_PROPERTIES + "/" + ModelNodeNames.JSON_LD_CONTEXT;
    JsonNode contextNode = objectNode.at(contextPath);
    Set<String> modelFields = new HashSet<>(ModelNodeNames.SCHEMA_ARTIFACT_KEYWORDS);

    if (contextNode != null || contextNode.isObject()) {
      ObjectNode jsonSchemaContextSpecificationNode = (ObjectNode)contextNode;
      Iterator<String> fieldNames = jsonSchemaContextSpecificationNode.fieldNames();

      while (fieldNames.hasNext()) {
        String fieldName = fieldNames.next();
        if (!modelFields.contains(fieldName)) {
          JsonNode enumNode = jsonSchemaContextSpecificationNode.get(fieldName);

          if (enumNode == null || !enumNode.isObject())
            throw new ArtifactParseException("Expecting object node with enum specification", fieldName, path + contextPath);

          JsonNode enumArray = enumNode.get(ModelNodeNames.JSON_SCHEMA_ENUM);

          if (enumArray == null || !enumArray.isArray())
            throw new ArtifactParseException("Expecting array for enum specification", ModelNodeNames.JSON_SCHEMA_ENUM,
              path + contextPath + fieldName);

          if (enumArray.size() != 1)
            throw new ArtifactParseException("Expecting exactly one value for enum specification, got " + enumArray.size(),
              ModelNodeNames.JSON_SCHEMA_ENUM, path + contextPath + fieldName);

          JsonNode elementNode = enumArray.get(0);

          if (!elementNode.isTextual())
            throw new ArtifactParseException("Expecting text node for enum specification, got " + elementNode.getNodeType(),
              ModelNodeNames.JSON_SCHEMA_ENUM, path + contextPath + fieldName);

          try {
            URI propertyURI = new URI(elementNode.asText());
            fieldNames2URI.put(fieldName, propertyURI);
          } catch (URISyntaxException e) {
            throw new ArtifactParseException("Invalid URI " + elementNode.asText() + " for enum specification",
              ModelNodeNames.JSON_SCHEMA_ENUM, path + contextPath + fieldName);
          }
        }
      }
    }
    return fieldNames2URI;
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode objectNode, String path)
  {
    ObjectNode fieldNode = getFieldNode(objectNode, path);

    SchemaArtifact schemaArtifact = readSchemaArtifact(fieldNode, path);
    FieldUI fieldUI = readFieldUI(fieldNode, path);
    Optional<ValueConstraints> valueConstraints = readValueConstraints(fieldNode, path);
    Optional<String> skosPrefLabel = readSKOSPrefLabelField(fieldNode, path);
    List<String> skosAlternateLabels = readSKOSAltLabelField(fieldNode, path);
    boolean isMultiple = false; // TODO

    checkFieldSchemaArtifactJSONLDType(schemaArtifact.getJsonLdTypes(), path);

    return new FieldSchemaArtifact(schemaArtifact, fieldUI, valueConstraints, skosPrefLabel, skosAlternateLabels, isMultiple);
  }

  /**
   * Attribute-value fields are defined inside the first element of an "items" array
   */
  private ObjectNode getFieldNode(ObjectNode objectNode, String path)
  {
    if (objectNode.isArray()) {
      JsonNode itemsNode = objectNode.get(ModelNodeNames.JSON_SCHEMA_ITEMS);
      if (itemsNode == null || !itemsNode.isArray() || !itemsNode.iterator().hasNext())
        throw new ArtifactParseException("Expecting array",  ModelNodeNames.JSON_SCHEMA_ITEMS, path);

      JsonNode itemNode = itemsNode.iterator().next();
      if (!itemNode.isObject())
        throw new ArtifactParseException("Expecting object as first element", ModelNodeNames.JSON_SCHEMA_ITEMS, path);
      return  (ObjectNode)itemNode;
    } else
      return objectNode;
  }

  private ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode, String path)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, path);

    Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    Map<String, URI> childPropertyURIs = new HashMap<>();
    ElementUI elementUI = readElementUI(objectNode, path);
    boolean isMultiple = false; // TODO

    checkElementSchemaArtifactJSONLDType(schemaArtifact.getJsonLdTypes(), path);

    readNestedFieldAndElementSchemaArtifacts(objectNode, path, fieldSchemas, elementSchemas);

    return new ElementSchemaArtifact(schemaArtifact, fieldSchemas, elementSchemas, childPropertyURIs, isMultiple, elementUI);
  }

  private Artifact readArtifact(ObjectNode objectNode, String path)
  {
    Optional<URI> jsonLdId = readOptionalJsonLDIDField(objectNode, path);
    Map<String, URI> jsonLdContext = readFieldNameURIValueMap(objectNode, path, ModelNodeNames.JSON_LD_CONTEXT);
    Optional<URI> createdBy = readCreatedByField(objectNode, path);
    Optional<URI> modifiedBy = readModifiedByField(objectNode, path);
    Optional<OffsetDateTime> createdOn = readCreatedOnField(objectNode, path);
    Optional<OffsetDateTime> lastUpdatedOn = readLastUpdatedOnField(objectNode, path);

    return new Artifact(jsonLdId, jsonLdContext, createdBy, modifiedBy, createdOn, lastUpdatedOn);
  }

  private SchemaArtifact readSchemaArtifact(ObjectNode objectNode, String path)
  {
    Artifact artifact = readArtifact(objectNode, path);

    URI jsonSchemaSchemaURI = readJsonSchemaSchemaURIField(objectNode, path);
    String jsonSchemaType = readJsonSchemaTypeField(objectNode, path);
    String jsonSchemaTitle = readJsonSchemaTitleField(objectNode, path);
    String jsonSchemaDescription = readJsonSchemaDescriptionField(objectNode, path);
    List<URI> jsonLdTypes = readJsonLDTypeField(objectNode, path);
    Version modelVersion = readSchemaOrgSchemaVersionField(objectNode, path);
    String schemaOrgName = readSchemaOrgNameField(objectNode, path);
    String schemaOrgDescription = readSchemaOrgDescriptionField(objectNode, path);
    Optional<Version> artifactVersion = readPAVVersionField(objectNode, path);
    Optional<Status> artifactVersionStatus = readBIBOStatusField(objectNode, path);
    Optional<Version> previousVersion = readPreviousVersionField(objectNode, path);
    Optional<URI> derivedFrom = readDerivedFromField(objectNode, path);

    return new SchemaArtifact(artifact,
      jsonSchemaSchemaURI, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdTypes,
      schemaOrgName, schemaOrgDescription, modelVersion, artifactVersion, artifactVersionStatus,
      previousVersion, derivedFrom);
  }

  private void readNestedFieldAndElementSchemaArtifacts(ObjectNode objectNode, String path,
    Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas)
  {
    JsonNode propertiesNode = objectNode.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);

    if (propertiesNode == null || !propertiesNode.isObject())
      throw new ArtifactParseException("Invalid JSON Schema properties node", ModelNodeNames.JSON_SCHEMA_PROPERTIES, path);

    Iterator<String> jsonFieldNames = propertiesNode.fieldNames();

    while (jsonFieldNames.hasNext()) {
      String jsonFieldName = jsonFieldNames.next();

      // The /properties field for each schema artifact contains entries constraining fields in instances
      if (!ModelNodeNames.FIELD_INSTANCE_ARTIFACT_KEYWORDS.contains(jsonFieldName)
        && !ModelNodeNames.ELEMENT_INSTANCE_ARTIFACT_KEYWORDS.contains(jsonFieldName)
        && !ModelNodeNames.TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS.contains(jsonFieldName)) {
        JsonNode jsonFieldOrElementSchemaArtifactNode = propertiesNode.get(jsonFieldName);
        String fieldOrElementPath = path + "/properties/" + jsonFieldName;

        if (jsonFieldOrElementSchemaArtifactNode.isObject()) {

          String jsonSchemaType = readJsonSchemaTypeField((ObjectNode)jsonFieldOrElementSchemaArtifactNode,
            fieldOrElementPath);

          if (jsonSchemaType.equals(ModelNodeNames.JSON_SCHEMA_ARRAY)) {
            jsonFieldOrElementSchemaArtifactNode = jsonFieldOrElementSchemaArtifactNode
              .get(ModelNodeNames.JSON_SCHEMA_ITEMS);
            if (jsonFieldOrElementSchemaArtifactNode == null)
              throw new ArtifactParseException("No items field in array", ModelNodeNames.JSON_SCHEMA_ITEMS, fieldOrElementPath);

            fieldOrElementPath += "/items";

            if (!jsonFieldOrElementSchemaArtifactNode.isObject())
              throw new ArtifactParseException("Non-object items content in array", ModelNodeNames.JSON_SCHEMA_ITEMS, fieldOrElementPath);
          } else if (!jsonSchemaType.equals(ModelNodeNames.JSON_SCHEMA_OBJECT)) {
            throw new ArtifactParseException("Expecting array or object, got " + jsonSchemaType, ModelNodeNames.JSON_SCHEMA_ITEMS, fieldOrElementPath);
          }

          List<URI> subSchemaArtifactJsonLDTypes = readJsonLDTypeField(
            (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);

          checkSchemaArtifactJSONLDType(subSchemaArtifactJsonLDTypes, fieldOrElementPath);

          URI subSchemaArtifactJsonLDType = subSchemaArtifactJsonLDTypes.get(0);

          if (subSchemaArtifactJsonLDType.toString().equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI)) {
            throw new ArtifactParseException("Invalid nesting of template schema artifact", jsonFieldName, fieldOrElementPath);

          } else if (subSchemaArtifactJsonLDType.toString().equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI)) {
            ElementSchemaArtifact elementSchemaArtifact = readElementSchemaArtifact(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);
            elementSchemas.put(jsonFieldName, elementSchemaArtifact);
          } else if (subSchemaArtifactJsonLDType.toString().equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
            FieldSchemaArtifact fieldSchemaArtifact = readFieldSchemaArtifact(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);
            fieldSchemas.put(jsonFieldName, fieldSchemaArtifact);
          } else if (subSchemaArtifactJsonLDType.toString().equals(ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
            // TODO: We do not yet handle these
          } else
            throw new ArtifactParseException(
              "Unknown JSON-LD @type " + subSchemaArtifactJsonLDType, jsonFieldName, fieldOrElementPath);

        } else {
          throw new ArtifactParseException("Unknown non-object schema artifact", jsonFieldName, fieldOrElementPath);
        }
      }
    }
  }

  private void readNestedInstanceArtifacts(ObjectNode instanceArtifactNode, String path,
    Map<String, List<FieldInstanceArtifact>> fields, Map<String, List<ElementInstanceArtifact>> elements)
  {
    Iterator<String> instanceArtifactFieldNames = instanceArtifactNode.fieldNames();

    while (instanceArtifactFieldNames.hasNext()) {
      String instanceArtifactFieldName = instanceArtifactFieldNames.next();

      if (!ModelNodeNames.INSTANCE_ARTIFACT_KEYWORDS.contains(instanceArtifactFieldName)) {
        JsonNode nestedNode = instanceArtifactNode.get(instanceArtifactFieldName);
        String nestedInstanceArtifactPath = path + "/" + instanceArtifactFieldName;

        if (nestedNode.isObject()) {
          ObjectNode nestedInstanceArtifactNode = (ObjectNode)nestedNode;

          readNestedInstanceArtifact(instanceArtifactFieldName, nestedInstanceArtifactPath, nestedInstanceArtifactNode,
            elements, fields);

        } else if (nestedNode.isArray()) {
          Iterator<JsonNode> nodeIterator = nestedNode.iterator();

          int arrayIndex = 0;
          while (nodeIterator.hasNext()) {
            String arrayEnclosedInstanceArtifactPath = nestedInstanceArtifactPath + "[" + arrayIndex + "]";
            JsonNode jsonNode = nodeIterator.next();
            if (jsonNode == null || jsonNode.isNull()) {
              throw new ArtifactParseException("Expecting field or element instance artifact entry in array, got null",
                instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath);
            } else {
              if (!jsonNode.isObject())
                throw new ArtifactParseException("Expecting nested field or element instance artifact in array",
                  instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath);

              ObjectNode arrayEnclosedInstanceArtifactNode = (ObjectNode)jsonNode;
              readNestedInstanceArtifact(instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath,
                arrayEnclosedInstanceArtifactNode, elements, fields);
            }
            arrayIndex++;
          }
        }
      } else
        throw new ArtifactParseException("Unknown non-object instance artifact", instanceArtifactFieldName, path);
    }
  }

  private void readNestedInstanceArtifact(String instanceArtifactFieldName, String instanceArtifactPath,
    ObjectNode instanceArtifactNode, Map<String, List<ElementInstanceArtifact>> elements,
    Map<String, List<FieldInstanceArtifact>> fields)
  {
    if (hasJSONLDContextField(instanceArtifactNode)) { // Element instance artifacts have @context fields
      ObjectNode elementInstanceArtifactNode = instanceArtifactNode;
      ElementInstanceArtifact elementInstanceArtifact = readElementInstanceArtifact(elementInstanceArtifactNode,
        instanceArtifactPath);
      elements.put(instanceArtifactFieldName, new ArrayList<>());
      elements.get(instanceArtifactFieldName).add(elementInstanceArtifact);
    } else { // Field instance artifact do not
      FieldInstanceArtifact fieldInstanceArtifact = readFieldInstanceArtifact(instanceArtifactNode,
        instanceArtifactPath);
      fields.put(instanceArtifactFieldName, new ArrayList<>());
      fields.get(instanceArtifactFieldName).add(fieldInstanceArtifact);
    }
  }

  private void checkSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    if (schemaArtifactJsonLDTypes.isEmpty())
      throw new ArtifactParseException("Unknown object - must be a JSON-LD type or array of types", ModelNodeNames.JSON_LD_TYPE, path);

    if (schemaArtifactJsonLDTypes.size() != 1)
      throw new ArtifactParseException(
        "Expecting single JSON-LD @type field for schema artifact, got " + schemaArtifactJsonLDTypes.size(),
        ModelNodeNames.JSON_LD_TYPE, path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!ModelNodeNames.SCHEMA_ARTIFACT_TYPE_IRIS.contains(schemaArtifactJsonLDType.toString()))
      throw new ArtifactParseException("Unexpected schema artifact JSON-LD @type " + schemaArtifactJsonLDType,
        ModelNodeNames.JSON_LD_TYPE, path);
  }

  private void checkTemplateSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.toString().equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new ArtifactParseException("Unexpected template schema artifact JSON-LD @type " + schemaArtifactJsonLDType,
        ModelNodeNames.JSON_LD_TYPE, path);
  }

  private void checkElementSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.toString().equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new ArtifactParseException("Unexpected element schema artifact JSON-LD @type " + schemaArtifactJsonLDType,
        ModelNodeNames.JSON_LD_TYPE, path);
  }

  private void checkFieldSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.toString().equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new ArtifactParseException("Unexpected field schema artifact JSON-LD @type " + schemaArtifactJsonLDType,
        ModelNodeNames.JSON_LD_TYPE, path);
  }

  private List<URI> readJsonLDTypeField(ObjectNode objectNode, String path)
  {
    return readURIFieldValues(objectNode, path, ModelNodeNames.JSON_LD_TYPE);
  }

  private String readJsonSchemaTypeField(ObjectNode objectNode, String path)
  {
    return readRequiredStringField(objectNode, path, ModelNodeNames.JSON_SCHEMA_TYPE);
  }

  private List<String> readRequiredJsonLDTypeField(ObjectNode objectNode, String path)
  {
    List<String> jsonLDTypes = readStringFieldValues(objectNode, path, ModelNodeNames.JSON_LD_TYPE);

    if (jsonLDTypes.isEmpty())
      throw new ArtifactParseException("No JSON-LD @type for artifact", ModelNodeNames.JSON_LD_TYPE, path);
    else
      return jsonLDTypes;
  }

  private Optional<ValueConstraints> readValueConstraints(ObjectNode objectNode, String path)
  {
    String vcPath = path + "/" + ModelNodeNames.VALUE_CONSTRAINTS;
    ObjectNode vcNode = readValueConstraintsNodeAtPath(objectNode, path);

    if (vcNode != null) {

      boolean requiredValue = readBooleanField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_REQUIRED_VALUE, false);
      boolean multipleChoice = readBooleanField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_MULTIPLE_CHOICE, false);
      Optional<NumberType> numberType = readNumberTypeField(vcNode, vcPath);
      Optional<String> unitOfMeasure = readStringField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_UNIT_OF_MEASURE);
      Optional<Number> minValue = readNumberField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE);
      Optional<Number> maxValue = readNumberField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE);
      Optional<Integer> decimalPlaces = readIntegerField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_DECIMAL_PLACE);
      Optional<Integer> minLength = readIntegerField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_MIN_STRING_LENGTH);
      Optional<Integer> maxLength = readIntegerField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_MAX_STRING_LENGTH);
      Optional<TemporalType> temporalType = readTemporalTypeField(vcNode, vcPath);
      List<OntologyValueConstraint> ontologies = readOntologyValueConstraints(vcNode, vcPath);
      List<ValueSetValueConstraint> valueSets = readValueSetValueConstraints(vcNode, vcPath);
      List<ClassValueConstraint> classes = readClassValueConstraints(vcNode, vcPath);
      List<BranchValueConstraint> branches = readBranchValueConstraints(vcNode, vcPath);
      List<LiteralValueConstraint> literals = readLiteralValueConstraints(vcNode, vcPath);
      Optional<DefaultValue> defaultValue = readDefaultValueField(vcNode, vcPath, ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE);

      return Optional.of(new ValueConstraints(requiredValue, multipleChoice, numberType, unitOfMeasure, minValue, maxValue,
        decimalPlaces, minLength, maxLength, temporalType, ontologies, valueSets, classes, branches, literals,
        defaultValue));
    } else
      return Optional.empty();
  }

  private Optional<DefaultValue> readDefaultValueField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();
    else if (jsonNode.isObject()) {
      String nestedPath = path + fieldName;
      ObjectNode defaultValueNode = (ObjectNode)jsonNode;
      URI termUri = readRequiredURIField(defaultValueNode, nestedPath, ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE_TERM_URI);
      String rdfsLabel = readRequiredStringField(defaultValueNode, nestedPath, ModelNodeNames.RDFS_LABEL);
      return Optional.of(new URIStringPairDefaultValue(Pair.of(termUri, rdfsLabel)));
    } else if (jsonNode.isNumber())
      return Optional.of(new NumericDefaultValue(jsonNode.asDouble()));
    else if (jsonNode.isTextual())
      return Optional.of(new StringDefaultValue(jsonNode.asText()));
    else
      throw new ArtifactParseException("default value must be a string, a number, or an object containing URI/string pair", fieldName, path);
  }

  private Optional<TemporalType> readTemporalTypeField(ObjectNode objectNode, String path)
  {
    Optional<String> temporalTypeValue = readOptionalStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_TEMPORAL_TYPE);

    if (temporalTypeValue.isPresent())
      return Optional.of(TemporalType.fromString(temporalTypeValue.get()));
    else
      return Optional.empty();
  }

  private Optional<NumberType> readNumberTypeField(ObjectNode objectNode, String path)
  {
    Optional<String> numberTypeValue = readOptionalStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE);

    if (numberTypeValue.isPresent())
      return Optional.of(NumberType.fromString(numberTypeValue.get()));
    else
      return Optional.empty();
  }

  private List<OntologyValueConstraint> readOntologyValueConstraints(ObjectNode objectNode, String path)
  {
    List<OntologyValueConstraint> ontologyValueConstraints = new ArrayList<>();

    JsonNode jsonNode = objectNode.get(ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES);

    if (jsonNode != null && !jsonNode.isNull() && jsonNode.isArray()) {
      Iterator<JsonNode> nodeIterator = jsonNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode valueConstraintNode = nodeIterator.next();
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES, path);
          OntologyValueConstraint ontologyValueConstraint = readOntologyValueConstraint((ObjectNode)valueConstraintNode,
            path + "/" + ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES);
          ontologyValueConstraints.add(ontologyValueConstraint);
        }
      }
    }
    return ontologyValueConstraints;
  }

  private List<ClassValueConstraint> readClassValueConstraints(ObjectNode objectNode, String path)
  {
    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    JsonNode jsonNode = objectNode.get(ModelNodeNames.VALUE_CONSTRAINTS_CLASSES);

    if (jsonNode != null && !jsonNode.isNull() && jsonNode.isArray()) {
      Iterator<JsonNode> nodeIterator = jsonNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode valueConstraintNode = nodeIterator.next();
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException(
              "Value in array must be an object", ModelNodeNames.VALUE_CONSTRAINTS_CLASSES, path);
          ClassValueConstraint classValueConstraint = readClassValueConstraint((ObjectNode)valueConstraintNode, path + "/" + ModelNodeNames.VALUE_CONSTRAINTS_CLASSES);
          classValueConstraints.add(classValueConstraint);
        }
      }
    }
    return classValueConstraints;
  }

  private List<ValueSetValueConstraint> readValueSetValueConstraints(ObjectNode objectNode, String path)
  {
    List<ValueSetValueConstraint> valueSetValueConstraints = new ArrayList<>();

    JsonNode jsonNode = objectNode.get(ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS);

    if (jsonNode != null && jsonNode.isArray()) {
      Iterator<JsonNode> nodeIterator = jsonNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode valueConstraintNode = nodeIterator.next();
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS, path);
          ValueSetValueConstraint valueSetValueConstraint = readValueSetValueConstraint((ObjectNode)valueConstraintNode,
            path + "/" + ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS);
          valueSetValueConstraints.add(valueSetValueConstraint);
        }
      }
    }
    return valueSetValueConstraints;
  }

  private List<BranchValueConstraint> readBranchValueConstraints(ObjectNode objectNode, String path)
  {
    List<BranchValueConstraint> branchValueConstraints = new ArrayList<>();

    JsonNode jsonNode = objectNode.get(ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES);

    if (jsonNode != null && jsonNode.isArray()) {
      Iterator<JsonNode> nodeIterator = jsonNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode valueConstraintNode = nodeIterator.next();
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException(
              "Value in array must be an object", ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES, path);
          BranchValueConstraint branchValueConstraint = readBranchValueConstraint((ObjectNode)valueConstraintNode, path + "/" + ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES);
          branchValueConstraints.add(branchValueConstraint);
        }
      }
    }

    return branchValueConstraints;
  }

  private List<LiteralValueConstraint> readLiteralValueConstraints(ObjectNode objectNode, String path)
  {
    List<LiteralValueConstraint> literalValueConstraints = new ArrayList<>();

    JsonNode jsonNode = objectNode.get(ModelNodeNames.VALUE_CONSTRAINTS_LITERALS);

    if (jsonNode != null && jsonNode.isArray()) {
      Iterator<JsonNode> nodeIterator = jsonNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode valueConstraintNode = nodeIterator.next();
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", ModelNodeNames.VALUE_CONSTRAINTS_LITERALS, path);
          LiteralValueConstraint literalValueConstraint = readLiteralValueConstraint((ObjectNode)valueConstraintNode,
            path + "/" + ModelNodeNames.VALUE_CONSTRAINTS_LITERALS);
          literalValueConstraints.add(literalValueConstraint);
        }
      }
    }
    return literalValueConstraints;
  }

  private OntologyValueConstraint readOntologyValueConstraint(ObjectNode objectNode, String path)
  {
    URI uri = readRequiredURIField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_URI);
    String acronym = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_ACRONYM);
    String name = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_NAME);
    Optional<Integer> numTerms = readIntegerField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS);

    return new OntologyValueConstraint(uri, acronym, name, numTerms);
  }

  private ClassValueConstraint readClassValueConstraint(ObjectNode objectNode, String path)
  {
    URI uri = readRequiredURIField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_URI);
    String prefLabel = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_PREFLABEL);
    String type = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_TYPE);
    String label = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_LABEL);
    String source = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_SOURCE);

    return new ClassValueConstraint(uri, prefLabel, type, label, source);
  }

  private ValueSetValueConstraint readValueSetValueConstraint(ObjectNode objectNode, String path)
  {
    String name = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_NAME);
    String vsCollection = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_VS_COLLECTION);
    URI uri = readRequiredURIField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_URI);
    int numTerms = readRequiredIntField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS);

    return new ValueSetValueConstraint(name, vsCollection, uri, numTerms);
  }
  private BranchValueConstraint readBranchValueConstraint(ObjectNode objectNode, String path)
  {
    String source = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_SOURCE);
    String acronym = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_ACRONYM);
    URI uri = readRequiredURIField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_URI);
    String name = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_NAME);
    int maxDepth = readRequiredIntField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_MAX_DEPTH);

    return new BranchValueConstraint(source, acronym, uri, name, maxDepth);
  }

  private LiteralValueConstraint readLiteralValueConstraint(ObjectNode objectNode, String path)
  {
    String label = readRequiredStringField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_LABEL);
    boolean selectedByDefault = readBooleanField(objectNode, path, ModelNodeNames.VALUE_CONSTRAINTS_SELECTED_BY_DEFAULT, false);

    return new LiteralValueConstraint(label, selectedByDefault);
  }

  private FieldUI readFieldUI(ObjectNode objectNode, String path)
  {
    ObjectNode uiNode = readUINodeAtPath(objectNode, path);
    String uiPath = path + "/" + ModelNodeNames.UI;

    FieldInputType fieldInputType = readFieldInputType(uiNode, uiPath);
    boolean isValueRecommendationEnabled = readBooleanField(uiNode, uiPath, ModelNodeNames.UI_VALUE_RECOMMENDATION_ENABLED, false);
    boolean hidden = readBooleanField(uiNode, uiPath, ModelNodeNames.UI_HIDDEN, false);
    Optional<Boolean> timeZoneEnabled = readOptionalBooleanField(uiNode, uiPath, ModelNodeNames.UI_TIMEZONE_ENABLED);

    Optional<TemporalGranularity> temporalGranularity = readTemporalGranularity(uiNode, uiPath);
    Optional<InputTimeFormat> inputTimeFormat = readInputTimeFormat(uiNode, uiPath);

    return new FieldUI(fieldInputType, isValueRecommendationEnabled, hidden, timeZoneEnabled, temporalGranularity, inputTimeFormat);
  }

  private ElementUI readElementUI(ObjectNode objectNode, String path)
  {
    ObjectNode uiNode = readUINodeAtPath(objectNode, path);
    String uiPath = path + "/" + ModelNodeNames.UI;

    List<String> order = readStringFieldValues(uiNode, uiPath, ModelNodeNames.UI_ORDER);
    Map<String, String> propertyLabels = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_LABELS);
    Map<String, String> propertyDescriptions = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_DESCRIPTIONS);
    Optional<String> header = readOptionalStringField(uiNode, uiPath, ModelNodeNames.UI_HEADER);
    Optional<String> footer = readOptionalStringField(uiNode, uiPath, ModelNodeNames.UI_FOOTER);

    return new ElementUI(order, propertyLabels, propertyDescriptions, header, footer);
  }


  private TemplateUI readTemplateUI(ObjectNode objectNode, String path)
  {
    ObjectNode uiNode = readUINodeAtPath(objectNode, path);
    String uiPath = path + "/" + ModelNodeNames.UI;

    List<String> order = readStringFieldValues(uiNode, uiPath, ModelNodeNames.UI_ORDER);
    List<String> pages = readStringFieldValues(uiNode, uiPath, ModelNodeNames.UI_PAGES);
    Map<String, String> propertyLabels = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_LABELS);
    Map<String, String> propertyDescriptions = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_DESCRIPTIONS);
    Optional<String> header = readOptionalStringField(uiNode, uiPath, ModelNodeNames.UI_HEADER);
    Optional<String> footer = readOptionalStringField(uiNode, uiPath, ModelNodeNames.UI_FOOTER);

    return new TemplateUI(order, pages, propertyLabels, propertyDescriptions, header, footer);
  }

  private Optional<String> readOptionalStringField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isTextual())
      throw new ArtifactParseException("Value must be a string", fieldName, path);

    return Optional.of(jsonNode.asText());
  }

  private Optional<Integer> readIntegerField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isInt())
      throw new ArtifactParseException("Value must be an integer", fieldName, path);

    return Optional.of(jsonNode.asInt());
  }

  private Optional<Number> readNumberField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isNumber())
      throw new ArtifactParseException("Value must be a number", fieldName, path);

    if (jsonNode.isIntegralNumber())
       return Optional.of(jsonNode.asLong());
    else
      return Optional.of(jsonNode.asDouble());
  }

  private Optional<Boolean> readOptionalBooleanField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isBoolean())
      throw new ArtifactParseException("Value must be a boolean", fieldName, path);

    return Optional.of(jsonNode.asBoolean());
  }

  private boolean readRequiredBooleanField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      throw new ArtifactParseException("Field must be present", fieldName, path);

    if (jsonNode.isNull())
      throw new ArtifactParseException("Field must not be null", fieldName, path);

    if (!jsonNode.isBoolean())
      throw new ArtifactParseException("Value must be boolean", fieldName, path);

    return jsonNode.asBoolean();
  }

  private boolean readBooleanField(ObjectNode objectNode, String path, String fieldName, boolean defaultValue)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return defaultValue;

    if (!jsonNode.isBoolean())
      throw new ArtifactParseException("Value must be boolean", fieldName, path);

    return jsonNode.asBoolean();
  }

  private FieldInputType readFieldInputType(ObjectNode objectNode, String path)
  {
    String inputType = readRequiredStringField(objectNode, path, ModelNodeNames.UI_FIELD_INPUT_TYPE);

    if (!ModelNodeNames.INPUT_TYPES.contains(inputType))
      throw new ArtifactParseException("Invalid field input type " + inputType, ModelNodeNames.UI_FIELD_INPUT_TYPE, path);

    return FieldInputType.fromString(inputType);
  }

  private Optional<TemporalGranularity> readTemporalGranularity(ObjectNode objectNode, String path)
  {
    Optional<String> granularity = readStringField(objectNode, path, ModelNodeNames.UI_TEMPORAL_GRANULARITY);

    if (!granularity.isPresent())
      return Optional.empty();

    if (!ModelNodeValues.TEMPORAL_GRANULARITIES.contains(granularity.get()))
      throw new ArtifactParseException("Invalid granularity " + granularity.get(), ModelNodeNames.UI_TEMPORAL_GRANULARITY, path);

    return Optional.of(TemporalGranularity.fromString(granularity.get()));
  }

  private Optional<InputTimeFormat> readInputTimeFormat(ObjectNode objectNode, String path)
  {
    Optional<String> timeFormat = readStringField(objectNode, path, ModelNodeNames.UI_INPUT_TIME_FORMAT);

    if (!timeFormat.isPresent())
      return Optional.empty();

    if (!ModelNodeValues.TIME_FORMATS.contains(timeFormat.get()))
      throw new ArtifactParseException("Invalid time format " + timeFormat.get(), ModelNodeNames.UI_INPUT_TIME_FORMAT, path);

    return Optional.of(InputTimeFormat.fromString(timeFormat.get()));
  }

  private String readDefaultValue(ObjectNode objectNode, String path)
  {
    ObjectNode valueConstraintsNode = readValueConstraintsNodeAtPath(objectNode, path);
    String subPath = path + "/" + ModelNodeNames.VALUE_CONSTRAINTS;

    return readRequiredStringField(valueConstraintsNode, subPath, ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE);
  }

  private ObjectNode readUINodeAtPath(ObjectNode objectNode, String path)
  {
    JsonNode jsonNode = objectNode.get(ModelNodeNames.UI);

    if (jsonNode == null)
      throw new ArtifactParseException("No " + ModelNodeNames.UI + " field", ModelNodeNames.UI, path);
    else if (jsonNode.isNull())
      throw new ArtifactParseException("Null " + ModelNodeNames.UI + " field", ModelNodeNames.UI, path);
    else if (!jsonNode.isObject())
      throw new ArtifactParseException("Value of field must be an object", ModelNodeNames.UI, path);

     return (ObjectNode)jsonNode;
  }

  private ObjectNode readValueConstraintsNodeAtPath(ObjectNode objectNode, String path)
  {
    JsonNode jsonNode = objectNode.get(ModelNodeNames.VALUE_CONSTRAINTS);

    if (jsonNode == null)
      return null;
    else if (jsonNode.isNull())
      return null;
    else if (!jsonNode.isObject())
      throw new ArtifactParseException("Value of field must be an object", ModelNodeNames.VALUE_CONSTRAINTS, path);

    return (ObjectNode)jsonNode;
  }

  private Map<String, String> readFieldNameStringValueMap(ObjectNode objectNode, String path, String fieldName)
  {
    Map<String, String> fieldNameStringValueMap = new HashMap<>();

    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode != null || jsonNode.isNull()) {

      if (!jsonNode.isObject())
        throw new ArtifactParseException("Value of field  must be an object", fieldName, path);

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = jsonNode.fields();

      while (fieldEntries.hasNext()) {
        Map.Entry<String, JsonNode> fieldEntry = fieldEntries.next();

        if (fieldEntry.getValue().isTextual()) {
          String currentFieldName = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          if (currentFieldValue != null && !currentFieldValue.isEmpty())
            fieldNameStringValueMap.put(currentFieldName, currentFieldValue);
        } else
            throw new ArtifactParseException("Object in field must contain string values", fieldName, path);
      }
    }
    return fieldNameStringValueMap;
  }

  private Map<String, URI> readFieldNameURIValueMap(ObjectNode objectNode, String path, String fieldName)
  {
    Map<String, URI> fieldNameStringValueMap = new HashMap<>();

    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode != null) {

      if (!jsonNode.isObject())
        throw new ArtifactParseException("Value of field must be an object", fieldName, path);

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = jsonNode.fields();

      while (fieldEntries.hasNext()) {
        Map.Entry<String, JsonNode> fieldEntry = fieldEntries.next();

        // We only record simple term->term URI entries
        if (fieldEntry.getValue().isTextual()) {
          String currentFieldName = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          try {
            URI currentFieldURIValue = new URI(currentFieldValue);
            fieldNameStringValueMap.put(currentFieldName, currentFieldURIValue);
          } catch (Exception e) {
            throw new ArtifactParseException("Object in field must contain URI values", fieldName, path);
          }
        }
      }
    }
    return fieldNameStringValueMap;
  }

  private String readJsonSchemaTitleField(ObjectNode objectNode, String path)
  {
    return readRequiredStringField(objectNode, path, ModelNodeNames.JSON_SCHEMA_TITLE);
  }

  private Optional<URI> readCreatedByField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, path, ModelNodeNames.PAV_CREATED_BY);
  }

  private Optional<URI> readModifiedByField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, path, ModelNodeNames.OSLC_MODIFIED_BY);
  }

  private String readJsonSchemaDescriptionField(ObjectNode objectNode, String path)
  {
    return readStringField(objectNode, path, ModelNodeNames.JSON_SCHEMA_DESCRIPTION, "");
  }

  private URI readJsonSchemaSchemaURIField(ObjectNode objectNode, String path)
  {
    return readRequiredURIField(objectNode, path, ModelNodeNames.JSON_SCHEMA_SCHEMA);
  }

  private Version readSchemaOrgSchemaVersionField(ObjectNode objectNode, String path)
  {
    return Version.fromString(readRequiredStringField(objectNode, path, ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION));
  }

  private String readSchemaOrgNameField(ObjectNode objectNode, String path)
  {
    return readRequiredStringField(objectNode, path, ModelNodeNames.SCHEMA_ORG_NAME);
  }

  private String readSchemaOrgDescriptionField(ObjectNode objectNode, String path)
  {
    return readRequiredStringField(objectNode, path, ModelNodeNames.SCHEMA_ORG_DESCRIPTION);
  }

  private Optional<Version> readPAVVersionField(ObjectNode objectNode, String path)
  {
    Optional<String> version = readStringField(objectNode, path, ModelNodeNames.PAV_VERSION);

    if (!version.isPresent())
      return Optional.empty();
    else
      return Optional.of(Version.fromString(version.get()));
  }

  private Optional<Version> readPreviousVersionField(ObjectNode objectNode, String path)
  {
    String previousVersion = readStringField(objectNode, path, ModelNodeNames.PAV_PREVIOUS_VERSION, null);

    if (previousVersion != null)
      return Optional.of(Version.fromString(previousVersion));
    else
      return Optional.empty();
  }

  private Optional<URI> readDerivedFromField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, path, ModelNodeNames.PAV_DERIVED_FROM);
  }

  private Optional<Status> readBIBOStatusField(ObjectNode objectNode, String path)
  {
    Optional<String> status = readStringField(objectNode, path, ModelNodeNames.BIBO_STATUS);

    if (status.isPresent())
      return Optional.of(Status.fromString(status.get()));
    else
      return Optional.empty();
  }

  private Optional<OffsetDateTime> readCreatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, path, ModelNodeNames.PAV_CREATED_ON);
  }

  private Optional<OffsetDateTime> readLastUpdatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, path, ModelNodeNames.PAV_LAST_UPDATED_ON);
  }

  private Optional<OffsetDateTime> readOffsetDateTimeField(ObjectNode objectNode, String path, String fieldName)
  {
    Optional<String> dateTimeValue = readStringField(objectNode, path, fieldName);

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

  private OffsetDateTime readRequiredOffsetDateTimeField(ObjectNode objectNode, String path, String fieldName)
  {
    String dateTimeValue = readRequiredStringField(objectNode, path, fieldName);

    try {
      return OffsetDateTime.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException(
        "Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(), fieldName, path);
    }
  }

  private Optional<URI> readURIField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isTextual())
      throw new ArtifactParseException("Value of URI field must be textual", fieldName, path);

    try {
      return Optional.of(new URI(jsonNode.asText()));
    } catch (Exception e) {
      throw new ArtifactParseException("Value of URI field must be a valid URI", fieldName, path);
    }
  }

  private Optional<String> readStringField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();
    else if (!jsonNode.isTextual())
      throw new ArtifactParseException("Value of text field must be textual", fieldName, path);
    else
      return Optional.of(jsonNode.asText());
  }

  private String readStringField(ObjectNode objectNode, String path, String fieldName, String defaultValue)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return defaultValue;
    else if (!jsonNode.isTextual())
      throw new ArtifactParseException("Value of text field must be textual", fieldName, path);
    else
      return jsonNode.asText();
  }

  private String readRequiredStringField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      throw new ArtifactParseException("No value for text field", fieldName, path);
    else if (jsonNode.isNull())
      throw new ArtifactParseException("Null value for text field", fieldName, path);
    else {
      if (!jsonNode.isTextual())
        throw new ArtifactParseException("Value of text field must be textual", fieldName, path);

      return jsonNode.asText();
    }
  }

  private int readRequiredIntField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      throw new ArtifactParseException("No value for int field", fieldName,  path);
    else if (jsonNode.isNull())
      throw new ArtifactParseException("Null value for int field", fieldName, path);
    else {
      if (!jsonNode.isInt())
        throw new ArtifactParseException("Value of int field must be an int", fieldName, path);

      return jsonNode.asInt();
    }
  }

  private URI readRequiredURIField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      throw new ArtifactParseException("No value for URI field", fieldName, path);
    else if (jsonNode.isNull())
      throw new ArtifactParseException("Null value for URI field", fieldName, path);
    else {
      if (!jsonNode.isTextual())
        throw new ArtifactParseException("Value of URI field must be textual", fieldName, path);

      try {
        return new URI(jsonNode.asText());
      } catch (Exception e) {
        throw new ArtifactParseException("Value of URI field must be a valid URI", fieldName, path);
      }
    }
  }

  private List<String> readStringFieldValues(ObjectNode objectNode, String path,  String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);
    List<String> textValues = new ArrayList<>();

    if (jsonNode != null && !jsonNode.isNull()) {
      if (jsonNode.isArray()) {
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();

        int arrayIndex = 0;
        while (nodeIterator.hasNext()) {
          JsonNode jsonValueNode = nodeIterator.next();
          if (jsonValueNode != null) {
            if (!jsonValueNode.isTextual())
              throw new ArtifactParseException("Value in text array at index " + arrayIndex + " must be textual", fieldName, path);
            String textValue = jsonValueNode.asText();
            if (!textValue.isEmpty())
              textValues.add(textValue);
          }
          arrayIndex++;
        }
      } else {
        String textValue = readStringField(objectNode, path, fieldName, "");
        if (textValue != null && !textValue.isEmpty())
          textValues.add(textValue);
      }
    }
    return textValues;
  }

  private List<URI> readURIFieldValues(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);
    List<URI> uriValues = new ArrayList<>();

    if (jsonNode != null && !jsonNode.isNull()) {
      if (jsonNode.isArray()) {
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();

        int arrayIndex = 0;
        while (nodeIterator.hasNext()) {
          JsonNode itemNode = nodeIterator.next();
          if (itemNode != null) {
            if (!itemNode.isTextual())
              throw new ArtifactParseException("Value in URI array at index " + arrayIndex + " must be textual", fieldName, path);
            try {
              URI uriValue = new URI(itemNode.asText());
              uriValues.add(uriValue);
            } catch (Exception e) {
              throw new ArtifactParseException("Value in URI array at index " + arrayIndex + " must a valid URI", fieldName, path);
            }
          }
          arrayIndex++;
        }
      } else {
        Optional<URI> uriValue = readURIField(objectNode, path, fieldName);
        if (uriValue.isPresent())
          uriValues.add(uriValue.get());
      }
    }
    return uriValues;
  }

  private List<String> readRequiredTextualFieldValues(ObjectNode objectNode, String path, String fieldName)
  {
    List<String> textValues = readStringFieldValues(objectNode, path, fieldName);

    if (textValues.isEmpty())
      throw new ArtifactParseException("No value for text field", fieldName, path);
    else
      return textValues;
  }

  private boolean hasJSONLDContextField(ObjectNode objectNode)
  {
    return objectNode.get(ModelNodeNames.JSON_LD_CONTEXT) != null;
  }

  private URI readIsBasedOnField(ObjectNode objectNode, String path)
  {
    return readRequiredURIField(objectNode, path, ModelNodeNames.SCHEMA_IS_BASED_ON);
  }

  private String readRequiredIsBasedOnField(ObjectNode objectNode, String path)
  {
    return readRequiredStringField(objectNode, ModelNodeNames.SCHEMA_IS_BASED_ON, path);
  }

  private Optional<URI> readOptionalJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, path, ModelNodeNames.JSON_LD_ID);
  }

  private URI readRequiredJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readRequiredURIField(objectNode, path, ModelNodeNames.JSON_LD_ID);
  }

  private String readJsonLDValueField(ObjectNode objectNode, String path)
  {
    return readStringField(objectNode, path, ModelNodeNames.JSON_LD_VALUE, null);
  }

  private Optional<String> readOptionalRDFSLabelField(ObjectNode objectNode, String path)
  {
    return readOptionalStringField(objectNode, path, ModelNodeNames.RDFS_LABEL);
  }

  private Optional<String> readSKOSNotationField(ObjectNode objectNode, String path)
  {
    return readOptionalStringField(objectNode, path, ModelNodeNames.SKOS_NOTATION);
  }

  private Optional<String> readSKOSPrefLabelField(ObjectNode objectNode, String path)
  {
    return readOptionalStringField(objectNode, path, ModelNodeNames.SKOS_PREFLABEL);
  }

  private List<String> readSKOSAltLabelField(ObjectNode objectNode, String path)
  {
    return readStringFieldValues(objectNode, path, ModelNodeNames.SKOS_ALTLABEL);
  }

  private String readSKOSPrefLabelValueField(ObjectNode objectNode, String path)
  {
    return readStringField(objectNode, path, ModelNodeNames.SKOS_PREFLABEL, null);
  }

  private ObjectMapper getMapper()
  {
    return this.mapper;
  }

}
