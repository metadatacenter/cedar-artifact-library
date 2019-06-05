package org.metadatacenter.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.core.ElementSchemaArtifact;
import org.metadatacenter.model.core.FieldSchemaArtifact;
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

  protected String readJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.LD_ID, path);
  }

  /**
   * See {@link ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_KEYWORDS}
   */
  public TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode objectNode)
  {
    List<String> jsonLDTypes = readJsonLDTypeField(objectNode, "/");
    checkTemplateSchemaArtifactJSONLDType(jsonLDTypes, "/");

    String jsonLDID = readJsonLDIDField(objectNode, "/");
    String name = readNameField(objectNode, "/");
    String description = readDescriptionField(objectNode, "/");
    String createdBy = readCreatedByField(objectNode, "/");
    String modifiedBy = readModifiedByField(objectNode, "/");
    OffsetDateTime createdOn = readCreatedOnField(objectNode, "/");
    OffsetDateTime lastUpdatedOn = readLastUpdatedOnField(objectNode, "/");
    String schema = readSchemaField(objectNode, "/");
    String schemaVersion = readSchemaVersionField(objectNode, "/");
    String version = readVersionField(objectNode, "/");
    String previousVersion = readPreviousVersionField(objectNode, "/");
    String status = readStatusField(objectNode, "/");
    Map<String, String> context = readJsonLDContextField(objectNode, "/");
    Map<String, FieldSchemaArtifact> fields = new HashMap<>();
    Map<String, ElementSchemaArtifact> elements = new HashMap<>();

    // Validate that jsonLDTypes contains TemplateURI

    readSubFieldsAndElements(objectNode, "/", fields, elements);

  }

  private void readSubFieldsAndElements(ObjectNode objectNode, String path, Map<String, FieldSchemaArtifact> fields,
    Map<String, ElementSchemaArtifact> elements)
  {
    Iterator<String> jsonFieldNames = objectNode.fieldNames();

    while (jsonFieldNames.hasNext()) {
      String jsonFieldName = jsonFieldNames.next();

      if (!ModelNodeNames.SCHEMA_ARTIFACT_KEYWORDS.contains(jsonFieldName)) {
        JsonNode jsonFieldOrElementNode = objectNode.get(jsonFieldName);
        String fieldOrElementPath = path + "/" + jsonFieldName;

        if (jsonFieldOrElementNode.isObject()) {
          List<String> subArtifactJsonLDTypes = readJsonLDTypeField((ObjectNode)jsonFieldOrElementNode,
            fieldOrElementPath);

          checkSchemaArtifactJSONLDType(subArtifactJsonLDTypes, fieldOrElementPath);

          String subArtifactJsonLDType = subArtifactJsonLDTypes.get(0);

          if (subArtifactJsonLDType.equals(ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI)) {
            throw new RuntimeException("Invalid nesting of template schema artifact at location " + fieldOrElementPath);

          } else if (subArtifactJsonLDType.equals(ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI)) {
            ElementSchemaArtifact elementSchemaArtifact = readElementSchemaArtifact((ObjectNode)jsonFieldOrElementNode,
              fieldOrElementPath);
            elements.put(jsonFieldName, elementSchemaArtifact);
          } else if (subArtifactJsonLDType.equals(ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI)) {
            FieldSchemaArtifact fieldSchemaArtifact = readFieldSchemaArtifact((ObjectNode)jsonFieldOrElementNode,
              fieldOrElementPath);
            fields.put(jsonFieldName, fieldSchemaArtifact);
          } else
            throw new RuntimeException(
              "Unknown JSON-LD @type " + subArtifactJsonLDType + "for field " + jsonFieldName + " at location "
                + fieldOrElementPath);

        } else {
          throw new RuntimeException(
            "Unknown non-object field " + jsonFieldName + " at location " + fieldOrElementPath);
        }
      }
    }
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
            throw new RuntimeException(
              ModelNodeNames.LD_CONTEXT + " field contains duplicate entries for term " + term + " at location "
                + path);

          context.put(term, termURI);
        }
      }
    }
    return context;
  }

  protected String readNameField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SCHEMA_NAME, path);
  }

  protected String readCreatedByField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.PAV_CREATED_BY, path);
  }

  protected String readModifiedByField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.OSLC_MODIFIED_BY, path);
  }

  protected String readDescriptionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SCHEMA_DESCRIPTION, path);
  }

  protected String readSchemaField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames._SCHEMA, path);
  }

  protected String readSchemaVersionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.SCHEMA_SCHEMA_VERSION, path);
  }

  protected String readVersionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.PAV_VERSION, path);
  }

  protected String readPreviousVersionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.PAV_PREVIOUS_VERSION, path);
  }

  protected String readStatusField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, ModelNodeNames.BIBO_STATUS, path);
  }

  protected OffsetDateTime readCreatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, ModelNodeNames.PAV_CREATED_ON, path);
  }

  protected OffsetDateTime readLastUpdatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, ModelNodeNames.PAV_LAST_UPDATED_ON, path);
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

  private String readTextualField(ObjectNode objectNode, String fieldName, String path)
  {
    JsonNode jsonNode = objectNode.get(fieldName);

    if (jsonNode == null)
      return null;

    if (!jsonNode.isTextual())
      throw new RuntimeException("Value of field " + fieldName + " at location " + path + " must be textual");

    return jsonNode.asText();
  }

  private String readMandatoryTextualField(ObjectNode objectNode, String fieldName, String path)
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

  private List<String> readMandatoryTextualFieldValues(ObjectNode objectNode, String fieldName, String path)
  {
    List<String> textValues = readTextualFieldValues(objectNode, fieldName, path);

    if (textValues.isEmpty())
      throw new RuntimeException("No value for field " + fieldName + " at location " + path);
    else
      return textValues;
  }

}
