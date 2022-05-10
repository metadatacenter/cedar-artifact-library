package org.metadatacenter.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.core.Artifact;
import org.metadatacenter.model.core.ElementInstanceArtifact;
import org.metadatacenter.model.core.ElementSchemaArtifact;
import org.metadatacenter.model.core.ElementUI;
import org.metadatacenter.model.core.FieldInstanceArtifact;
import org.metadatacenter.model.core.FieldSchemaArtifact;
import org.metadatacenter.model.core.FieldUI;
import org.metadatacenter.model.core.InstanceArtifact;
import org.metadatacenter.model.core.SchemaArtifact;
import org.metadatacenter.model.core.TemplateInstanceArtifact;
import org.metadatacenter.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.core.TemplateUI;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ArtifactReader
{
  private final ObjectMapper mapper;

  public ArtifactReader(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  public TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode objectNode)
  {
    return readTemplateSchemaArtifact(objectNode, "/");
  }

  public ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode)
  {
    return readElementSchemaArtifact(objectNode, "/");
  }

  public FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode objectNode)
  {
    return readFieldSchemaArtifact(objectNode, "/");
  }


  public InstanceArtifact readInstanceArtifact(ObjectNode objectNode, String path)
  {
    Artifact artifact = readArtifact(objectNode, path);

    return new InstanceArtifact(artifact);
  }

  public InstanceArtifact readTemplateInstanceArtifact(ObjectNode objectNode, String path)
  {
    InstanceArtifact instanceArtifact = readInstanceArtifact(objectNode, path);
    String isBasedOn = readRequiredIsBasedOnField(objectNode, path);
    Map<String, List<ElementInstanceArtifact>> elementInstances = new HashMap<>();
    Map<String, List<FieldInstanceArtifact>> fieldInstances = new HashMap<>();

    return new TemplateInstanceArtifact(instanceArtifact, isBasedOn, elementInstances, fieldInstances);
  }

  private TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode objectNode, String path)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, path);
    Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    TemplateUI templateUI = null; // TODO

    checkTemplateSchemaArtifactJSONLDType(schemaArtifact.getJsonLDTypes(), path);

    readNestedFieldAndElementSchemaArtifacts(objectNode, path, fieldSchemas, elementSchemas);

    return new TemplateSchemaArtifact(schemaArtifact, fieldSchemas, elementSchemas, templateUI);
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode objectNode, String path)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, path);
    String skosPrefLabel = readSKOSPrefLabelField(objectNode, path);
    String defaultValue = readDefaultValue(objectNode, path);
    List<String> skosAlternateLabels = readSKOSAltLabelField(objectNode, path);
    FieldUI fieldUI = readFieldUI(objectNode, path);

    checkFieldSchemaArtifactJSONLDType(schemaArtifact.getJsonLDTypes(), path);

    return new FieldSchemaArtifact(schemaArtifact, skosPrefLabel, defaultValue, skosAlternateLabels, fieldUI);
  }

  private ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode, String path)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, path);

    Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    ElementUI elementUI = readElementUI(objectNode, path);
    boolean isMultiple = false; // TODO

    checkElementSchemaArtifactJSONLDType(schemaArtifact.getJsonLDTypes(), path);

    readNestedFieldAndElementSchemaArtifacts(objectNode, path, fieldSchemas, elementSchemas);

    return new ElementSchemaArtifact(schemaArtifact, fieldSchemas, elementSchemas, isMultiple, elementUI);
  }

  private Artifact readArtifact(ObjectNode objectNode, String path)
  {
    Map<String, String> context = readFieldNameStringValueMap(objectNode, path, ModelNodeNames.JSON_LD_CONTEXT);
    List<URI> jsonLDTypes = readJsonLDTypeField(objectNode, path);
    URI jsonLDID = readJsonLDIDField(objectNode, path);
    String jsonSchemaType = readJsonSchemaTypeField(objectNode, path);
    String name = readJsonSchemaTitleField(objectNode, path);
    String description = readJsonSchemaDescriptionField(objectNode, path);
    URI createdBy = readCreatedByField(objectNode, path);
    URI modifiedBy = readModifiedByField(objectNode, path);
    OffsetDateTime createdOn = readCreatedOnField(objectNode, path);
    OffsetDateTime lastUpdatedOn = readLastUpdatedOnField(objectNode, path);

    return new Artifact(jsonLDID, jsonLDTypes, jsonSchemaType, name, description, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, context);
  }

  private SchemaArtifact readSchemaArtifact(ObjectNode objectNode, String path)
  {
    Artifact artifact = readArtifact(objectNode, path);

    URI schema = readRequiredJsonSchemaSchemaField(objectNode, path);
    String schemaVersion = readRequiredSchemaVersionField(objectNode, path);
    String version = readRequiredVersionField(objectNode, path);
    String previousVersion = readPreviousVersionField(objectNode, path);
    String status = readRequiredStatusField(objectNode, path);

    return new SchemaArtifact(artifact, schema, schemaVersion, version, previousVersion, status);
  }

  private void readNestedFieldAndElementSchemaArtifacts(ObjectNode objectNode, String path,
    Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas)
  {
    JsonNode propertiesNode = objectNode.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);

    if (propertiesNode == null || !propertiesNode.isObject())
      throw new RuntimeException("Invalid JSON Schema properties node at " + path);

    Iterator<String> jsonFieldNames = propertiesNode.fieldNames();

    while (jsonFieldNames.hasNext()) {
      String jsonFieldName = jsonFieldNames.next();

      // The /properties field for each schema artifact contains entries constraining fields in instances
      if (!ModelNodeNames.FIELD_INSTANCE_ARTIFACT_KEYWORDS.contains(jsonFieldName)
        && !ModelNodeNames.ELEMENT_INSTANCE_ARTIFACT_KEYWORDS.contains(jsonFieldName)
        && !ModelNodeNames.TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS.contains(jsonFieldName)) {
        JsonNode jsonFieldOrElementSchemaArtifactNode = propertiesNode.get(jsonFieldName);
        String fieldOrElementPath = path + "properties/" + jsonFieldName;

        if (jsonFieldOrElementSchemaArtifactNode.isObject()) {

          String jsonSchemaType = readJsonSchemaTypeField((ObjectNode)jsonFieldOrElementSchemaArtifactNode,
            fieldOrElementPath);

          if (jsonSchemaType.equals(ModelNodeNames.JSON_SCHEMA_ARRAY)) {
            jsonFieldOrElementSchemaArtifactNode = jsonFieldOrElementSchemaArtifactNode
              .get(ModelNodeNames.JSON_SCHEMA_ITEMS);
            if (jsonFieldOrElementSchemaArtifactNode == null)
              throw new RuntimeException("No items field in array at " + fieldOrElementPath);

            fieldOrElementPath += "/items";

            if (!jsonFieldOrElementSchemaArtifactNode.isObject())
              throw new RuntimeException("Non-object items content in array at " + fieldOrElementPath);
          } else if (!jsonSchemaType.equals(ModelNodeNames.JSON_SCHEMA_OBJECT)) {
            throw new RuntimeException(
              "Expecting array or object at location " + fieldOrElementPath + ", got " + jsonSchemaType);
          }

          List<URI> subSchemaArtifactJsonLDTypes = readJsonLDTypeField(
            (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);

          checkSchemaArtifactJSONLDType(subSchemaArtifactJsonLDTypes, fieldOrElementPath);

          URI subSchemaArtifactJsonLDType = subSchemaArtifactJsonLDTypes.get(0);

          if (subSchemaArtifactJsonLDType.toString().equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI)) {
            throw new RuntimeException("Invalid nesting of template schema artifact at location " + fieldOrElementPath);

          } else if (subSchemaArtifactJsonLDType.toString().equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI)) {
            ElementSchemaArtifact elementSchemaArtifact = readElementSchemaArtifact(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);
            elementSchemas.put(jsonFieldName, elementSchemaArtifact);
          } else if (subSchemaArtifactJsonLDType.toString().equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
            FieldSchemaArtifact fieldSchemaArtifact = readFieldSchemaArtifact(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);
            fieldSchemas.put(jsonFieldName, fieldSchemaArtifact);
          } else
            throw new RuntimeException(
              "Unknown JSON-LD @type " + subSchemaArtifactJsonLDType + "for field " + jsonFieldName + " at location "
                + fieldOrElementPath);

        } else {
          throw new RuntimeException(
            "Unknown non-object schema artifact field " + jsonFieldName + " at location " + fieldOrElementPath);
        }
      }
    }
  }

  protected ElementInstanceArtifact readElementInstanceArtifact(ObjectNode objectNode, String path)
  {
    InstanceArtifact instanceArtifact = readInstanceArtifact(objectNode, path);
    Map<String, List<FieldInstanceArtifact>> fieldInstances = new HashMap<>();
    Map<String, List<ElementInstanceArtifact>> elementInstances = new HashMap<>();

    readNestedInstanceArtifacts(objectNode, path, fieldInstances, elementInstances);

    ElementInstanceArtifact elementInstanceArtifact = new ElementInstanceArtifact(instanceArtifact, fieldInstances,
      elementInstances);

    return elementInstanceArtifact;
  }

  protected FieldInstanceArtifact readFieldInstanceArtifact(ObjectNode objectNode, String path)
  {
    InstanceArtifact instanceArtifact = readInstanceArtifact(objectNode, path);
    String jsonLDValue = readJsonLDValueField(objectNode, path);
    String rdfsLabel = readRDFSLabelField(objectNode, path);
    String skosNotation = readSKOSNotationField(objectNode, path);
    String skosPrefLabel = readSKOSPrefLabelField(objectNode, path);

    FieldInstanceArtifact fieldInstanceArtifact = new FieldInstanceArtifact(instanceArtifact, jsonLDValue, rdfsLabel,
      skosNotation, skosPrefLabel);

    return fieldInstanceArtifact;
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
            if (jsonNode == null) {
              throw new RuntimeException("Expecting field or element instance artifact entry in array at location "
                + arrayEnclosedInstanceArtifactPath + "], got null");
            } else {
              if (!jsonNode.isObject())
                throw new RuntimeException("Expecting nested field or element instance artifact in array at location "
                  + arrayEnclosedInstanceArtifactPath);

              ObjectNode arrayEnclosedInstanceArtifactNode = (ObjectNode)jsonNode;
              readNestedInstanceArtifact(instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath,
                arrayEnclosedInstanceArtifactNode, elements, fields);
            }
          }
          arrayIndex++;
        }
      } else
        throw new RuntimeException(
          "Unknown non-object instance artifact field " + instanceArtifactFieldName + " at location "
            + instanceArtifactFieldName);
    }
  }

  private void readNestedInstanceArtifact(String instanceArtifactFieldName, String instanceArtifactPath,
    ObjectNode instanceArtifactNode, Map<String, List<ElementInstanceArtifact>> elements,
    Map<String, List<FieldInstanceArtifact>> fields)
  {
    if (hasJSONLDContextField(instanceArtifactNode)) { // With @context is an element instance artifact
      ObjectNode elementInstanceArtifactNode = instanceArtifactNode;
      ElementInstanceArtifact elementInstanceArtifact = readElementInstanceArtifact(elementInstanceArtifactNode,
        instanceArtifactPath);
      elements.put(instanceArtifactFieldName, new ArrayList<>());
      elements.get(instanceArtifactFieldName).add(elementInstanceArtifact);
    } else { // Default to field instance artifact
      FieldInstanceArtifact fieldInstanceArtifact = readFieldInstanceArtifact(instanceArtifactNode,
        instanceArtifactPath);
      fields.put(instanceArtifactFieldName, new ArrayList<>());
      fields.get(instanceArtifactFieldName).add(fieldInstanceArtifact);
    }
  }

  protected boolean hasJSONLDContextField(ObjectNode objectNode)
  {
    return objectNode.get(ModelNodeNames.JSON_LD_CONTEXT) != null;
  }

  protected URI readIsBasedOnField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, path, ModelNodeNames.SCHEMA_IS_BASED_ON);
  }

  protected String readRequiredIsBasedOnField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.SCHEMA_IS_BASED_ON, path);
  }

  protected URI readJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, ModelNodeNames.JSON_LD_ID, path);
  }

  protected URI readRequiredJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readRequiredURIField(objectNode, ModelNodeNames.JSON_LD_ID, path);
  }

  protected String readJsonLDValueField(ObjectNode objectNode, String path)
  {
    return readOptionalTextualField(objectNode, path, ModelNodeNames.JSON_LD_VALUE, null);
  }

  protected String readRDFSLabelField(ObjectNode objectNode, String path)
  {
    return readOptionalTextualField(objectNode, path, ModelNodeNames.RDFS_LABEL, null);
  }

  protected String readSKOSNotationField(ObjectNode objectNode, String path)
  {
    return readOptionalTextualField(objectNode, path, ModelNodeNames.SKOS_NOTATION, null);
  }

  protected String readSKOSPrefLabelField(ObjectNode objectNode, String path)
  {
    return readOptionalTextualField(objectNode, path, ModelNodeNames.SKOS_PREFLABEL, null);
  }

  protected List<String> readSKOSAltLabelField(ObjectNode objectNode, String path)
  {
    return readTextualFieldValues(objectNode, path, ModelNodeNames.SKOS_ALTLABEL);
  }

  protected String readSKOSPrefLabelValueField(ObjectNode objectNode, String path)
  {
    return readOptionalTextualField(objectNode, path, ModelNodeNames.SKOS_PREFLABEL, null);
  }

  private void checkSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    if (schemaArtifactJsonLDTypes.isEmpty())
      throw new RuntimeException("Unknown object at location " + path + " - must be a JSON-LD type or array of types");

    if (schemaArtifactJsonLDTypes.size() != 1)
      throw new RuntimeException(
        "Expecting single JSON-LD @type field for schema artifact, got " + schemaArtifactJsonLDTypes.size()
          + " at location " + path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!ModelNodeNames.SCHEMA_ARTIFACT_TYPE_IRIS.contains(schemaArtifactJsonLDType.toString()))
      throw new RuntimeException(
        "Unexpected schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  private void checkTemplateSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.toString().equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected template schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);

  }

  private void checkElementSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.toString().equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected element schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  private void checkFieldSchemaArtifactJSONLDType(List<URI> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    URI schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.toString().equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected field schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  protected List<URI> readJsonLDTypeField(ObjectNode objectNode, String path)
  {
    return readURIFieldValues(objectNode, path, ModelNodeNames.JSON_LD_TYPE);
  }

  protected String readJsonSchemaTypeField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, path, ModelNodeNames.JSON_SCHEMA_TYPE);
  }

  protected List<String> readRequiredJsonLDTypeField(ObjectNode objectNode, String path)
  {
    List<String> jsonLDTypes = readTextualFieldValues(objectNode, path, ModelNodeNames.JSON_LD_TYPE);

    if (jsonLDTypes.isEmpty())
      throw new RuntimeException("No JSON-LD @type for artifact at location " + path);
    else
      return jsonLDTypes;
  }

  protected FieldUI readFieldUI(ObjectNode objectNode, String path)
  {
    ObjectNode uiNode = readUINodeAtPath(objectNode, path);
    String uiPath = path + "/" + ModelNodeNames.UI;

    String fieldInputType = readFieldInputType(uiNode, uiPath);
    boolean isValueRecommendationEnabled = readOptionalBooleanField(uiNode, uiPath, ModelNodeNames.UI_VALUE_RECOMMENDATION_ENABLED, false);
    boolean hidden = readOptionalBooleanField(uiNode, uiPath, ModelNodeNames.UI_HIDDEN, false);

    return new FieldUI(fieldInputType, isValueRecommendationEnabled, hidden);
  }

  protected ElementUI readElementUI(ObjectNode objectNode, String path)
  {
    ObjectNode uiNode = readUINodeAtPath(objectNode, path);
    String uiPath = path + "/" + ModelNodeNames.UI;

    List<String> order = readTextualFieldValues(uiNode, uiPath, ModelNodeNames.UI_ORDER);
    Map<String, String> propertyLabels = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_LABELS);
    Map<String, String> propertyDescriptions = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_DESCRIPTIONS);
    String header = readOptionalTextualField(uiNode, uiPath, ModelNodeNames.UI_HEADER, "");
    String footer = readOptionalTextualField(uiNode, uiPath, ModelNodeNames.UI_FOOTER, "");

    return new ElementUI(order, propertyLabels, propertyDescriptions, header, footer);
  }


  protected TemplateUI readTemplateUI(ObjectNode objectNode, String path)
  {
    ObjectNode uiNode = readUINodeAtPath(objectNode, path);
    String uiPath = path + "/" + ModelNodeNames.UI;

    List<String> order = readTextualFieldValues(uiNode, uiPath, ModelNodeNames.UI_ORDER);
    List<String> pages = readTextualFieldValues(uiNode, uiPath, ModelNodeNames.UI_PAGES);
    Map<String, String> propertyLabels = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_LABELS);
    Map<String, String> propertyDescriptions = readFieldNameStringValueMap(uiNode, uiPath, ModelNodeNames.UI_PROPERTY_DESCRIPTIONS);
    String header = readOptionalTextualField(uiNode, uiPath, ModelNodeNames.UI_HEADER, "");
    String footer = readOptionalTextualField(uiNode, uiPath, ModelNodeNames.UI_FOOTER, "");

    return new TemplateUI(order, pages, propertyLabels, propertyDescriptions, header, footer);
  }

  protected boolean readOptionalBooleanField(ObjectNode objectNode, String path, String fieldName, boolean defaultValue)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      return defaultValue;

    if (!jsonNode.isBoolean())
      throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be boolean");

    return jsonNode.asBoolean();
  }

  protected boolean readRequiredBooleanField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      throw new RuntimeException("Field " + fieldName + " at location " + path + " must be present");

    if (!jsonNode.isBoolean())
      throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be boolean");

    return jsonNode.asBoolean();
  }

  protected String readFieldInputType(ObjectNode objectNode, String path)
  {
    String inputType = readRequiredTextualField(objectNode, path, ModelNodeNames.UI_FIELD_INPUT_TYPE);

    if (!ModelNodeNames.INPUT_TYPES.contains(inputType))
      throw new RuntimeException("Invalid field input type " + inputType + " at location " + path);

    return inputType;
  }

  protected String readDefaultValue(ObjectNode objectNode, String path)
  {
    ObjectNode valueConstraintsNode = readValueConstraintsNodeAtPath(objectNode, path);
    String subPath = path + "/" + ModelNodeNames.VALUE_CONSTRAINTS;

    return readOptionalTextualField(valueConstraintsNode, subPath, ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE, null);
  }

  protected ObjectNode readUINodeAtPath(ObjectNode objectNode, String path)
  {
    JsonNode jsonNode = objectNode.get(ModelNodeNames.UI);

    if (jsonNode == null)
      throw new RuntimeException("No " + ModelNodeNames.UI + " field at location " + path);
    else if (!jsonNode.isObject())
      throw new RuntimeException(
        "Value of field " + ModelNodeNames.UI + " at location " + path + " must be an object");

     return (ObjectNode)jsonNode;
  }

  protected ObjectNode readValueConstraintsNodeAtPath(ObjectNode objectNode, String path)
  {
    JsonNode jsonNode = objectNode.get(ModelNodeNames.VALUE_CONSTRAINTS);

    if (jsonNode == null)
      throw new RuntimeException("No " + ModelNodeNames.VALUE_CONSTRAINTS + " field at location " + path);
    else if (!jsonNode.isObject())
      throw new RuntimeException(
        "Value of field " + ModelNodeNames.VALUE_CONSTRAINTS + " at location " + path + " must be an object");

    return (ObjectNode)jsonNode;
  }

  protected Map<String, String> readFieldNameStringValueMap(ObjectNode objectNode, String path, String fieldName)
  {
    Map<String, String> fieldNameStringValueMap = new HashMap<>();

    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode != null) {

      if (!jsonNode.isObject())
        throw new RuntimeException(
          "Value of field " + fieldName + " at location " + path + " must be an object");

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = jsonNode.fields();

      while (fieldEntries.hasNext()) {
        Map.Entry<String, JsonNode> fieldEntry = fieldEntries.next();

        // We only record simple term->term URI entries
        if (fieldEntry.getValue().isTextual()) {
          String currentFieldName = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          if (currentFieldValue != null && !currentFieldValue.isEmpty())
            fieldNameStringValueMap.put(currentFieldName, currentFieldValue);
        } else
          if (!jsonNode.isObject())
            throw new RuntimeException("Object in field " + fieldName + " at location " + path + " must contain string values");
      }
    }
    return fieldNameStringValueMap;
  }

  protected String readJsonSchemaTitleField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, path, ModelNodeNames.JSON_SCHEMA_TITLE);
  }

  protected URI readCreatedByField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, path, ModelNodeNames.PAV_CREATED_BY);
  }

  protected URI readRequiredCreatedByField(ObjectNode objectNode, String path)
  {
    return readRequiredURIField(objectNode, path, ModelNodeNames.PAV_CREATED_BY);
  }

  protected URI readModifiedByField(ObjectNode objectNode, String path)
  {
    return readURIField(objectNode, path, ModelNodeNames.OSLC_MODIFIED_BY);
  }

  protected URI readRequiredURIModifiedByField(ObjectNode objectNode, String path)
  {
    return readRequiredURIField(objectNode, path, ModelNodeNames.OSLC_MODIFIED_BY);
  }

  protected String readJsonSchemaDescriptionField(ObjectNode objectNode, String path)
  {
    return readOptionalTextualField(objectNode, path, ModelNodeNames.JSON_SCHEMA_DESCRIPTION, "");
  }

  protected String readJsonSchemaSchemaField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, path, ModelNodeNames.JSON_SCHEMA_SCHEMA);
  }

  protected URI readRequiredJsonSchemaSchemaField(ObjectNode objectNode, String path)
  {
    return readRequiredURIField(objectNode, path, ModelNodeNames.JSON_SCHEMA_SCHEMA);
  }

  protected String readRequiredSchemaVersionField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, path, ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION);
  }

  protected String readRequiredVersionField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, path, ModelNodeNames.PAV_VERSION);
  }

  protected String readPreviousVersionField(ObjectNode objectNode, String path)
  {
    return readOptionalTextualField(objectNode, path, ModelNodeNames.PAV_PREVIOUS_VERSION, null);
  }

  protected String readRequiredStatusField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, path, ModelNodeNames.BIBO_STATUS);
  }

  protected OffsetDateTime readCreatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, path, ModelNodeNames.PAV_CREATED_ON);
  }

  protected OffsetDateTime readRequiredCreatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, path, ModelNodeNames.PAV_CREATED_ON);
  }

  protected OffsetDateTime readLastUpdatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, path, ModelNodeNames.PAV_LAST_UPDATED_ON);
  }

  protected OffsetDateTime readRequiredLastUpdatedOnField(ObjectNode objectNode, String path)
  {
    return readRequiredOffsetDateTimeField(objectNode, path, ModelNodeNames.PAV_LAST_UPDATED_ON);
  }

  protected ObjectMapper getMapper()
  {
    return this.mapper;
  }

  private OffsetDateTime readOffsetDateTimeField(ObjectNode objectNode, String path, String fieldName)
  {
    String dateTimeValue = readRequiredTextualField(objectNode, path, fieldName);

    try {
      return OffsetDateTime.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new RuntimeException(
        "Invalid offset datetime value " + dateTimeValue + " in field " + fieldName + " at location " + path + ":" + e
          .getMessage());
    }
  }

  private OffsetDateTime readRequiredOffsetDateTimeField(ObjectNode objectNode, String path, String fieldName)
  {
    String dateTimeValue = readRequiredTextualField(objectNode, path, fieldName);

    try {
      return OffsetDateTime.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new RuntimeException(
        "Invalid offset datetime value " + dateTimeValue + " in field " + fieldName + " at location " + path + ":" + e
          .getMessage());
    }
  }

  private URI readURIField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      return null;

    if (!jsonNode.isTextual())
      throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be textual");

    try {
      return new URI(jsonNode.asText());
    } catch (Exception e) {
      throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be a URI");
    }
  }

  private String readOptionalTextualField(ObjectNode objectNode, String path, String fieldName, String defaultValue)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      return defaultValue;
    else if (!jsonNode.isTextual())
      throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be textual");
    else
      return jsonNode.asText();
  }

  private String readRequiredTextualField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      throw new RuntimeException("No value for field " + fieldName + " at location " + path);
    else {
      if (!jsonNode.isTextual())
        throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be textual");

      return jsonNode.asText();
    }
  }

  private URI readRequiredURIField(ObjectNode objectNode, String path, String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      throw new RuntimeException("No value for field " + fieldName + " at location " + path);
    else {
      if (!jsonNode.isTextual())
        throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be textual");

      try {
        return new URI(jsonNode.asText());
      } catch (Exception e) {
        throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be a URI");
      }
    }
  }

  private List<String> readTextualFieldValues(ObjectNode objectNode, String path,  String fieldName)
  {
    JsonNode jsonNode = objectNode.get(fieldName);
    List<String> textValues = new ArrayList<>();

    if (jsonNode.isArray()) {
      Iterator<JsonNode> nodeIterator = jsonNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode jsonValueNode = nodeIterator.next();
        if (jsonValueNode != null) {
          if (!jsonValueNode.isTextual())
            throw new RuntimeException(
              "Value in array field " + fieldName + " at location " + path + " must be textual");
          String textValue = jsonValueNode.asText();
          if (!textValue.isEmpty())
            textValues.add(textValue);
        }
      }
    } else {
      String textValue = readOptionalTextualField(objectNode, path, fieldName, "");
      if (textValue != null && !textValue.isEmpty())
        textValues.add(textValue);
    }
    return textValues;
  }

  private List<URI> readURIFieldValues(ObjectNode objectNode, String path, String fieldName)
  {
    List<URI> uriValues = new ArrayList<>();

    if (objectNode.isArray()) {
      Iterator<JsonNode> nodeIterator = objectNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode jsonNode = nodeIterator.next();
        if (jsonNode != null) {
          if (!jsonNode.isTextual())
            throw new RuntimeException(
              "Value in array field " + fieldName + " at location " + path + " must be textual");
          try {
            URI uriValue = new URI(jsonNode.asText());
            uriValues.add(uriValue);
          } catch (Exception e) {
            throw new RuntimeException(
              "Value in array field " + fieldName + " at location " + path + " must be textual");
          }
        }
      }
    } else {
      URI uriValue = readURIField(objectNode, path, fieldName);
      uriValues.add(uriValue);
    }
    return uriValues;
  }

  private List<String> readRequiredTextualFieldValues(ObjectNode objectNode, String path, String fieldName)
  {
    List<String> textValues = readTextualFieldValues(objectNode, path, fieldName);

    if (textValues.isEmpty())
      throw new RuntimeException("No value for field " + fieldName + " at location " + path);
    else
      return textValues;
  }

}
