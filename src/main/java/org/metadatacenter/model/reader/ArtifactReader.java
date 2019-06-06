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

    Map<String, FieldSchemaArtifact> fields = new HashMap<>();
    Map<String, ElementSchemaArtifact> elements = new HashMap<>();

    checkTemplateSchemaArtifactJSONLDType(schemaArtifact.getJsonLDTypes(), "/");

    readNestedFieldsAndElementSchemaArtifacts(objectNode, "/", fields, elements);

    return new TemplateSchemaArtifact(schemaArtifact, fields, elements);
  }

  public ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode)
  {
    SchemaArtifact schemaArtifact = readSchemaArtifact(objectNode, "/");

    Map<String, FieldSchemaArtifact> fields = new HashMap<>();
    Map<String, ElementSchemaArtifact> elements = new HashMap<>();
    boolean isMultiple = false; // TODO

    checkElementSchemaArtifactJSONLDType(schemaArtifact.getJsonLDTypes(), "/");

    readNestedFieldsAndElementSchemaArtifacts(objectNode, "/", fields, elements);

    return new ElementSchemaArtifact(schemaArtifact, fields, elements, isMultiple);
  }

  public Artifact readArtifact(ObjectNode objectNode, String path)
  {
    Map<String, String> context = readJsonLDContextField(objectNode, path);
    List<String> jsonLDTypes = readJsonLDTypeField(objectNode, path);
    String jsonLDID = readJsonLDIDField(objectNode, path);
    String name = readNameField(objectNode, "/");
    String description = readDescriptionField(objectNode, path);
    String createdBy = readCreatedByField(objectNode, "/");
    String modifiedBy = readModifiedByField(objectNode, path);
    OffsetDateTime createdOn = readCreatedOnField(objectNode, path);
    OffsetDateTime lastUpdatedOn = readLastUpdatedOnField(objectNode, path);

    return new Artifact(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn,
      context);
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

    String schema = readRequiredSchemaField(objectNode, path);
    String schemaVersion = readRequiredSchemaVersionField(objectNode, path);
    String version = readRequiredVersionField(objectNode, path);
    String previousVersion = readPreviousVersionField(objectNode, path);
    String status = readRequiredStatusField(objectNode, path);

    return new SchemaArtifact(artifact, schema, schemaVersion, version, previousVersion, status);
  }

  private void readNestedFieldsAndElementSchemaArtifacts(ObjectNode objectNode, String path,
    Map<String, FieldSchemaArtifact> fields, Map<String, ElementSchemaArtifact> elements)
  {
    Iterator<String> jsonFieldNames = objectNode.fieldNames();

    while (jsonFieldNames.hasNext()) {
      String jsonFieldName = jsonFieldNames.next();

      if (!ModelNodeNames.SCHEMA_ARTIFACT_KEYWORDS.contains(jsonFieldName)) {
        JsonNode jsonFieldOrElementSchemaArtifactNode = objectNode.get(jsonFieldName);
        String fieldOrElementPath = path + "/" + jsonFieldName;

        if (jsonFieldOrElementSchemaArtifactNode.isObject()) {
          List<String> subSchemaArtifactJsonLDTypes = readJsonLDTypeField(
            (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);

          checkSchemaArtifactJSONLDType(subSchemaArtifactJsonLDTypes, fieldOrElementPath);

          String subSchemaArtifactJsonLDType = subSchemaArtifactJsonLDTypes.get(0);

          if (subSchemaArtifactJsonLDType.equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI)) {
            throw new RuntimeException("Invalid nesting of template schema artifact at location " + fieldOrElementPath);

          } else if (subSchemaArtifactJsonLDType.equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI)) {
            ElementSchemaArtifact elementSchemaArtifact = readElementSchemaArtifact(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);
            elements.put(jsonFieldName, elementSchemaArtifact);
          } else if (subSchemaArtifactJsonLDType.equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
            FieldSchemaArtifact fieldSchemaArtifact = readFieldSchemaArtifact(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath);
            fields.put(jsonFieldName, fieldSchemaArtifact);
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

    readNestedFieldsAndElementInstanceArtifacts(objectNode, path, fieldInstances, elementInstances);

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

  private void readNestedFieldsAndElementInstanceArtifacts(ObjectNode objectNode, String path,
    Map<String, List<FieldInstanceArtifact>> fields, Map<String, List<ElementInstanceArtifact>> elements)
  {
    Iterator<String> jsonFieldNames = objectNode.fieldNames();

    while (jsonFieldNames.hasNext()) {
      String jsonFieldName = jsonFieldNames.next();

      if (!ModelNodeNames.INSTANCE_ARTIFACT_KEYWORDS.contains(jsonFieldName)) {
        JsonNode jsonFieldOrElementInstanceArtifactNode = objectNode.get(jsonFieldName);
        String fieldOrElementPath = path + "/" + jsonFieldName;

        if (jsonFieldOrElementInstanceArtifactNode.isObject()) {

          if (hasJSONLDContextField((ObjectNode)jsonFieldOrElementInstanceArtifactNode)) {
            FieldInstanceArtifact fieldInstanceArtifact = readFieldInstanceArtifact(objectNode, fieldOrElementPath);
            if (fields.containsKey(jsonFieldName)) {
              fields.get(jsonFieldName).add(fieldInstanceArtifact);
            } else {
              fields.put(jsonFieldName, new ArrayList<>());
              fields.get(jsonFieldName).add(fieldInstanceArtifact);
            }
          } else if (jsonFieldOrElementInstanceArtifactNode.isArray()) {
            // TODO Nested array of fields or instances
            Iterator<JsonNode> nodeIterator = objectNode.iterator();

            int arrayIndex = 0;
            while (nodeIterator.hasNext()) {
              JsonNode jsonNode = nodeIterator.next();
              if (jsonNode == null) {
                throw new RuntimeException(
                  "Expecting field or element instance entry in array at location " + path + "[" + arrayIndex
                    + "], got null");
              } else {
                if (!jsonNode.isObject())
                  throw new RuntimeException(
                    "Expecting nested field or element artifact instance in array at location " + path + "["
                      + arrayIndex + "]");

                
              }
              arrayIndex++;
            }
          } else { // Assume it is element instance artifact
            ObjectNode elementInstanceArtifactNode = (ObjectNode)jsonFieldOrElementInstanceArtifactNode;
            ElementInstanceArtifact elementInstanceArtifact = readElementInstanceArtifact(objectNode,
              fieldOrElementPath);
            if (elements.containsKey(jsonFieldName))
              throw new RuntimeException();
            else
              elementInstanceArtifactNode.put(jsonFieldName, elementInstanceArtifactNode);

          }
        } else {
          throw new RuntimeException(
            "Unknown non-object instance artifact field " + jsonFieldName + " at location " + fieldOrElementPath);
        }
      }
    }
  }

  protected boolean hasJSONLDContextField(ObjectNode objectNode)
  {
    return objectNode.get(ModelNodeNames.LD_CONTEXT) != null;
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
    return readTextualField(objectNode, ModelNodeNames.LD_ID, path);
  }

  protected String readRequiredJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.LD_ID, path);
  }

  protected String readJsonLDValueField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.LD_VALUE, path);
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

    if (schemaArtifactJsonLDType.equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected template schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);

  }

  private void checkElementSchemaArtifactJSONLDType(List<String> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    String schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (schemaArtifactJsonLDType.equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected element schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  private void checkFieldSchemaArtifactJSONLDType(List<String> schemaArtifactJsonLDTypes, String path)
  {
    checkSchemaArtifactJSONLDType(schemaArtifactJsonLDTypes, path);

    String schemaArtifactJsonLDType = schemaArtifactJsonLDTypes.get(0);

    if (schemaArtifactJsonLDType.equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new RuntimeException(
        "Unexpected field schema artifact JSON-LD @type " + schemaArtifactJsonLDType + " at location " + path);
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode objectNode, String path)
  {
    return null;
  }

  private ElementSchemaArtifact readElementSchemaArtifact(ObjectNode objectNode, String path)
  {
    return null;

  }

  protected List<String> readJsonLDTypeField(ObjectNode objectNode, String path)
  {
    return readTextualFieldValues(objectNode, ModelNodeNames.LD_TYPE, path);
  }

  protected List<String> readMandatoryJsonLDTypeField(ObjectNode objectNode, String path)
  {
    List<String> jsonLDTypes = readTextualFieldValues(objectNode, ModelNodeNames.LD_TYPE, path);

    if (jsonLDTypes.isEmpty())
      throw new RuntimeException("No JSON-LD @type for artifact at location " + path);
    else
      return jsonLDTypes;
  }

  protected Map<String, String> readJsonLDContextField(ObjectNode objectNode, String path)
  {
    Map<String, String> context = new HashMap<>();

    JsonNode jsonNode = objectNode.get(ModelNodeNames.LD_CONTEXT);

    if (jsonNode != null) {

      if (!jsonNode.isObject())
        throw new RuntimeException(
          "Value of field " + ModelNodeNames.LD_CONTEXT + " at location " + path + " must be an object");

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = jsonNode.fields();

      while (fieldEntries.hasNext()) {
        Map.Entry<String, JsonNode> fieldEntry = fieldEntries.next();

        // We only record simple term->term URI @context entries
        if (fieldEntry.getValue().isTextual()) {
          String term = fieldEntry.getKey();
          String termURI = fieldEntry.getValue().textValue();

          if (context.containsKey(term))
            4 context.put(term, termURI);
        }
      }
    } return context;
  }

  protected String readNameField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SCHEMA_NAME, path);
  }

  protected String readRequiredNameField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.SCHEMA_NAME, path);
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

  protected String readDescriptionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SCHEMA_DESCRIPTION, path);
  }

  protected String readRequiredDescriptionField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.SCHEMA_DESCRIPTION, path);
  }

  protected String readSchemaField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames._SCHEMA, path);
  }

  protected String readRequiredSchemaField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames._SCHEMA, path);
  }

  protected String readRequiredSchemaVersionField(ObjectNode objectNode, String path)
  {
    return readRequiredTextualField(objectNode, ModelNodeNames.SCHEMA_SCHEMA_VERSION, path);
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
        "Invalid offset datetime value " + dateTimeValue + " in field " + fieldName + " at location " + path);
    }
  }

  private OffsetDateTime readRequiredOffsetDateTimeField(ObjectNode objectNode, String fieldName, String path)
  {
    String dateTimeValue = readRequiredTextualField(objectNode, fieldName, path);

    try {
      return OffsetDateTime.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new RuntimeException(
        "Invalid offset datetime value " + dateTimeValue + " in field " + fieldName + " at location " + path);
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
