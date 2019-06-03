package org.metadatacenter.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ArtifactReader
{
  public static final String JSON_LD_ID_FIELD_NAME = "@id";
  public static final String JSON_LD_TYPE_FIELD_NAME = "@type";
  public static final String NAME_FIELD_NAME = "schema:name";
  public static final String DESCRIPTION_FIELD_NAME = "schema:description";
  public static final String CREATED_BY_FIELD_NAME = "pav:createdBy";
  public static final String MODIFIED_BY_FIELD_NAME = "pav:modifiedBy";
  public static final String SCHEMA_FIELD_NAME = "$schema";
  public static final String SCHEMA_VERSION_FIELD_NAME = "schema:version";
  public static final String VERSION_FIELD_NAME = "pav:version";
  public static final String STATUS_FIELD_NAME = "bobo:status";


  private final ObjectMapper mapper;

  public ArtifactReader(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  protected String readJsonLDIDField(ObjectNode objectNode, String path)
  {
    return readTextualField(objectNode, JSON_LD_ID_FIELD_NAME, path);
  }

  protected List<String> readJsonLDTypeField(ObjectNode objectNode, String path)
  {
    return readTextualFieldValues(objectNode, JSON_LD_TYPE_FIELD_NAME, path);
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

  protected ObjectMapper getMapper()
  {
    return this.mapper;
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
