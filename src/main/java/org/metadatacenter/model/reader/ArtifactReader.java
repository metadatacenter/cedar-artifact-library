package org.metadatacenter.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
  public static final String JSON_LD_ID_FIELD_NAME = "@id";
  public static final String JSON_LD_TYPE_FIELD_NAME = "@type";
  public static final String JSON_LD_CONTEXT_FIELD_NAME = "@context";
  public static final String NAME_FIELD_NAME = "schema:name";
  public static final String DESCRIPTION_FIELD_NAME = "schema:description";
  public static final String CREATED_BY_FIELD_NAME = "pav:createdBy";
  public static final String MODIFIED_BY_FIELD_NAME = "pav:modifiedBy";
  public static final String SCHEMA_FIELD_NAME = "$schema";
  public static final String SCHEMA_VERSION_FIELD_NAME = "schema:version";
  public static final String VERSION_FIELD_NAME = "pav:version";
  public static final String STATUS_FIELD_NAME = "bibo:status";
  public static final String CREATED_ON_FIELD_NAME = "pav:createdOn";
  public static final String LAST_UPDATED_ON_FIELD_NAME = "pav:lastUpdatedOn";

  private final ObjectMapper mapper;

  public ArtifactReader(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  protected String readJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, JSON_LD_ID_FIELD_NAME, path);
  }

  public TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode objectNode)
  {
    String jsonLDID = readJsonLDIDField(objectNode, "/");
    List<String> jsonLDTypes = readJsonLDTypeField(objectNode, "/");
    String name = readNameField(objectNode, "/");
    String description = readDescriptionField(objectNode, "/");
    String createdBy = readCreatedByField(objectNode, "/");
    String modifiedBy = readModifiedByField(objectNode, "/");
    OffsetDateTime createdOn = readCreatedOnField(objectNode, "/");
    OffsetDateTime lastUpdatedOn = readLastUpdatedOnField(objectNode, "/");
    String schema = readSchemaField(objectNode, "/");
    String schemaVersion = readSchemaVersionField(objectNode, "/");
    String version = readVersionField(objectNode, "/");
    String status = readStatusField(objectNode, "/");
    Map<String, String> context = readJsonLDContextField(objectNode, "/");
    List<FieldSchemaArtifact> fields = readFieldSchemaArtifacts(objectNode, "/");
    List<ElementSchemaArtifact> elements = readElementSchemaArtifacts(objectNode, "/");

  }

  protected List<String> readJsonLDTypeField(ObjectNode objectNode, String path)
  {
    return readTextualFieldValues(objectNode, JSON_LD_TYPE_FIELD_NAME, path);
  }

  protected Map<String, String> readJsonLDContextField(ObjectNode objectNode, String path)
  {
    Map<String, String> context = new HashMap<>();

    JsonNode jsonNode = objectNode.get(JSON_LD_CONTEXT_FIELD_NAME);

    if (jsonNode != null) {

      if (!jsonNode.isObject())
        throw new RuntimeException(
          "Value of field " + JSON_LD_CONTEXT_FIELD_NAME + " at location " + path + " must be an object");

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = jsonNode.fields();

      while (fieldEntries.hasNext()) {
        Map.Entry<String, JsonNode> fieldEntry = fieldEntries.next();

        // We only record simple term->term URI @context entries
        if (fieldEntry.getValue().isTextual()) {
          String term = fieldEntry.getKey();
          String termURI = fieldEntry.getValue().textValue();

          if (context.containsKey(term))
            throw new RuntimeException(
              JSON_LD_CONTEXT_FIELD_NAME + " field contains duplicate entries for term " + term + " at location "
                + path);

          context.put(term, termURI);
        }
      }
    }
    return context;
  }

  protected String readNameField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, NAME_FIELD_NAME, path);
  }

  protected String readCreatedByField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, CREATED_BY_FIELD_NAME, path);
  }

  protected String readModifiedByField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, MODIFIED_BY_FIELD_NAME, path);
  }

  protected String readDescriptionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, DESCRIPTION_FIELD_NAME, path);
  }

  protected String readSchemaField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, SCHEMA_FIELD_NAME, path);
  }

  protected String readSchemaVersionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, SCHEMA_VERSION_FIELD_NAME, path);
  }

  protected String readVersionField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, VERSION_FIELD_NAME, path);
  }

  protected String readStatusField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, STATUS_FIELD_NAME, path);
  }

  protected OffsetDateTime readCreatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, CREATED_ON_FIELD_NAME, path);
  }

  protected OffsetDateTime readLastUpdatedOnField(ObjectNode objectNode, String path)
  {
    return readOffsetDateTimeField(objectNode, LAST_UPDATED_ON_FIELD_NAME, path);
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
}
