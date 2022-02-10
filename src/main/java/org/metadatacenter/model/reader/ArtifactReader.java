package org.metadatacenter.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.core.Artifact;
import org.metadatacenter.model.core.ElementInstanceArtifact;
import org.metadatacenter.model.core.ElementSchemaArtifact;
import org.metadatacenter.model.core.FieldInstanceArtifact;
import org.metadatacenter.model.core.FieldSchemaArtifact;
import org.metadatacenter.model.core.InstanceArtifact;
import org.metadatacenter.model.core.SchemaArtifact;
import org.metadatacenter.model.core.TemplateInstanceArtifact;
import org.metadatacenter.model.core.TemplateSchemaArtifact;

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
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, "/");

    Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();

    checkTemplateSchemaArtifactJSONLDType(schemaArtifact.getJsonLDTypes(), "/");

    readNestedFieldAndElementSchemaArtifacts(objectNode, "/", fieldSchemas, elementSchemas);

    return new TemplateSchemaArtifact(schemaArtifact, fieldSchemas, elementSchemas);
  }

  public ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, "/");

    Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    boolean isMultiple = false; // TODO

    checkElementSchemaArtifactJSONLDType(schemaArtifact.getJsonLDTypes(), "/");

    readNestedFieldAndElementSchemaArtifacts(objectNode, "/", fieldSchemas, elementSchemas);

    return new ElementSchemaArtifact(schemaArtifact, fieldSchemas, elementSchemas, isMultiple);
  }

  public Artifact readArtifact(ObjectNode objectNode, String path)
  {
    Map<String, String> context = readJsonLDContextField(objectNode, path);
    List<String> jsonLDTypes = readJsonLDTypeField(objectNode, path);
    String jsonLDID = readJsonLDIDField(objectNode, path);
    String jsonSchemaType = readJsonSchemaTypeField(objectNode, "/");
    String name = readJsonSchemaTitleField(objectNode, "/");
    String description = readJsonSchemaDescriptionField(objectNode, path);
    String createdBy = readCreatedByField(objectNode, "/");
    String modifiedBy = readModifiedByField(objectNode, path);
    OffsetDateTime createdOn = readCreatedOnField(objectNode, path);
    OffsetDateTime lastUpdatedOn = readLastUpdatedOnField(objectNode, path);

    return new Artifact(jsonLDID, jsonLDTypes, jsonSchemaType, name, description, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, context);
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

  public SchemaArtifact readSchemaArtifact(ObjectNode objectNode, String path)
  {
    Artifact artifact = readArtifact(objectNode, path);

    String schema = readRequiredJsonSchemaSchemaField(objectNode, path);
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

          List<String> subSchemaArtifactJsonLDTypes = readJsonLDTypeField(
            (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);

          checkSchemaArtifactJSONLDType(subSchemaArtifactJsonLDTypes, fieldOrElementPath);

          String subSchemaArtifactJsonLDType = subSchemaArtifactJsonLDTypes.get(0);

          if (subSchemaArtifactJsonLDType.equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI)) {
            throw new RuntimeException("Invalid nesting of template schema artifact at location " + fieldOrElementPath);

          } else if (subSchemaArtifactJsonLDType.equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI)) {
            ElementSchemaArtifact elementSchemaArtifact = readElementSchemaArtifact(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);
            elementSchemas.put(jsonFieldName, elementSchemaArtifact);
          } else if (subSchemaArtifactJsonLDType.equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
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
    String skosAltLabel = readSKOSAltLabelField(objectNode, path);

    FieldInstanceArtifact fieldInstanceArtifact = new FieldInstanceArtifact(instanceArtifact, jsonLDValue, rdfsLabel,
      skosNotation, skosPrefLabel, skosAltLabel);

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

  protected String readIsBasedOnField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SCHEMA_IS_BASED_ON, path);
  }

  protected String readRequiredIsBasedOnField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.SCHEMA_IS_BASED_ON, path);
  }

  protected String readJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.JSON_LD_ID, path);
  }

  protected String readRequiredJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.JSON_LD_ID, path);
  }

  protected String readJsonLDValueField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.JSON_LD_VALUE, path);
  }

  protected String readRDFSLabelField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.RDFS_LABEL, path);
  }

  protected String readSKOSNotationField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SKOS_NOTATION, path);
  }

  protected String readSKOSPrefLabelField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SKOS_PREFLABEL, path);
  }

  protected String readSKOSAltLabelField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SKOS_ALTLABEL, path);
  }

  protected String readSKOSPrefLabelValueField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SKOS_PREFLABEL, path);
  }

  private void checkSchemaArtifactJSONLDType(List<String> schemaArtifactJsonLDTypes, String path)
  {
    if (schemaArtifactJsonLDTypes.isEmpty())
      throw new RuntimeException("Unknown object at location " + path + " - must be a element or field artifact");

    if (schemaArtifactJsonLDTypes.size() != 1)
      throw new RuntimeException(
        "Expecting single JSON-LD @type field for schema artifact, got " + schemaArtifactJsonLDTypes.size()
          + " at location " + path);

    String schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!ModelNodeNames.SCHEMA_ARTIFACT_TYPE_IRIS.contains(schemaArtifactJsonLDType))
      throw new RuntimeException(
        "Unexpected schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  private void checkTemplateSchemaArtifactJSONLDType(List<String> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    String schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected template schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);

  }

  private void checkElementSchemaArtifactJSONLDType(List<String> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    String schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected element schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  private void checkFieldSchemaArtifactJSONLDType(List<String> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    String schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (!schemaArtifactJsonLDType.equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected field schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode objectNode, String path)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, path);
    String fieldInputType = ""; // TODO
    boolean isMultiple = false; // TODO

    // TODO More

    return new FieldSchemaArtifact(schemaArtifact, fieldInputType, isMultiple);
  }

  private ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode, String path)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, path);
    Map<String, FieldSchemaArtifact> fields = new HashMap<>();
    Map<String, ElementSchemaArtifact> elements = new HashMap<>();
    boolean isMultiple = false; // TODO

    // TODO Read fields and elements

    return new ElementSchemaArtifact(schemaArtifact, fields, elements, isMultiple);
  }

  protected List<String> readJsonLDTypeField(ObjectNode objectNode, String path)
  {
    return readTextualFieldValues(objectNode, ModelNodeNames.JSON_LD_TYPE, path);
  }

  protected String readJsonSchemaTypeField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.JSON_SCHEMA_TYPE, path);
  }

  protected List<String> readMandatoryJsonLDTypeField(ObjectNode objectNode, String path)
  {
    List<String> jsonLDTypes = readTextualFieldValues(objectNode, ModelNodeNames.JSON_LD_TYPE, path);

    if (jsonLDTypes.isEmpty())
      throw new RuntimeException("No JSON-LD @type for artifact at location " + path);
    else
      return jsonLDTypes;
  }

  protected Map<String, String> readJsonLDContextField(ObjectNode objectNode, String path)
  {
    Map<String, String> context = new HashMap<>();

    JsonNode jsonNode = objectNode.get(ModelNodeNames.JSON_LD_CONTEXT);

    if (jsonNode != null) {

      if (!jsonNode.isObject())
        throw new RuntimeException(
          "Value of field " + ModelNodeNames.JSON_LD_CONTEXT + " at location " + path + " must be an object");

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = jsonNode.fields();

      while (fieldEntries.hasNext()) {
        Map.Entry<String, JsonNode> fieldEntry = fieldEntries.next();

        // We only record simple term->term URI entries
        if (fieldEntry.getValue().isTextual()) {
          String term = fieldEntry.getKey();
          String termURI = fieldEntry.getValue().textValue();

          if (!context.containsKey(term))
            context.put(term, termURI);
        }
      }
    }
    return context;
  }

  protected String readJsonSchemaTitleField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.JSON_SCHEMA_TITLE, path);
  }

  protected String readCreatedByField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.PAV_CREATED_BY, path);
  }

  protected String readRequiredCreatedByField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.PAV_CREATED_BY, path);
  }

  protected String readModifiedByField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.OSLC_MODIFIED_BY, path);
  }

  protected String readRequiredModifiedByField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.OSLC_MODIFIED_BY, path);
  }

  protected String readJsonSchemaDescriptionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.JSON_SCHEMA_DESCRIPTION, path);
  }

  protected String readJsonSchemaSchemaField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.JSON_SCHEMA_SCHEMA, path);
  }

  protected String readRequiredJsonSchemaSchemaField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.JSON_SCHEMA_SCHEMA, path);
  }

  protected String readRequiredSchemaVersionField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION, path);
  }

  protected String readRequiredVersionField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.PAV_VERSION, path);
  }

  protected String readPreviousVersionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.PAV_PREVIOUS_VERSION, path);
  }

  protected String readRequiredStatusField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.BIBO_STATUS, path);
  }

  protected OffsetDateTime readCreatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, ModelNodeNames.PAV_CREATED_ON, path);
  }

  protected OffsetDateTime readRequiredCreatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, ModelNodeNames.PAV_CREATED_ON, path);
  }

  protected OffsetDateTime readLastUpdatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, ModelNodeNames.PAV_LAST_UPDATED_ON, path);
  }

  protected OffsetDateTime readRequiredLastUpdatedOnField(ObjectNode objectNode, String path)
  {
    return readRequiredOffsetDateTimeField(objectNode, ModelNodeNames.PAV_LAST_UPDATED_ON, path);
  }

  protected ObjectMapper getMapper()
  {
    return this.mapper;
  }

  private OffsetDateTime readOffsetDateTimeField(ObjectNode objectNode, String fieldName, String path)
  {
    String dateTimeValue = readTextualField(objectNode, fieldName, path);

    try {
      return OffsetDateTime.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new RuntimeException(
        "Invalid offset datetime value " + dateTimeValue + " in field " + fieldName + " at location " + path + ":" + e
          .getMessage());
    }
  }

  private OffsetDateTime readRequiredOffsetDateTimeField(ObjectNode objectNode, String fieldName, String path)
  {
    String dateTimeValue = readRequiredTextualField(objectNode, fieldName, path);

    try {
      return OffsetDateTime.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new RuntimeException(
        "Invalid offset datetime value " + dateTimeValue + " in field " + fieldName + " at location " + path + ":" + e
          .getMessage());
    }
  }

  private String readTextualField(ObjectNode objectNode, String fieldName, String path)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      return null;

    if (!jsonNode.isTextual())
      throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be textual");

    return jsonNode.asText();
  }

  private String readRequiredTextualField(ObjectNode objectNode, String fieldName, String path)
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

  private List<String> readTextualFieldValues(ObjectNode objectNode, String fieldName, String path)
  {
    List<String> textValues = new ArrayList<>();

    if (objectNode.isArray()) {
      Iterator<JsonNode> nodeIterator = objectNode.iterator();

      while (nodeIterator.hasNext()) {
        JsonNode jsonNode = nodeIterator.next();
        if (jsonNode != null) {
          if (!jsonNode.isTextual())
            throw new RuntimeException(
              "Value in array field " + fieldName + " at location " + path + " must be textual");
          String textValue = jsonNode.asText();
          if (!textValue.isEmpty())
            textValues.add(textValue);
        }
      }
    } else {
      String textValue = readTextualField(objectNode, fieldName, path);
      if (textValue != null)
        textValues.add(textValue);
    }
    return textValues;
  }

  private List<String> readRequiredTextualFieldValues(ObjectNode objectNode, String fieldName, String path)
  {
    List<String> textValues = readTextualFieldValues(objectNode, fieldName, path);

    if (textValues.isEmpty())
      throw new RuntimeException("No value for field " + fieldName + " at location " + path);
    else
      return textValues;
  }

}
