package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.reader.JsonArtifactShapeChecks.*;
import static org.metadatacenter.artifacts.model.reader.JsonNodeReaders.*;
import static org.metadatacenter.artifacts.model.reader.JsonValueConstraintsReader.*;
import static org.metadatacenter.model.ModelNodeNames.*;

final class JsonNodeReaders {
  private JsonNodeReaders() {}

  static Optional<Integer> readInteger(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null || jsonNode.isNull()) {
      return Optional.empty();
    }

    if (!jsonNode.isInt()) {
      throw new ArtifactParseException("Value must be an integer", fieldKey, path);
    }

    return Optional.of(jsonNode.asInt());
  }


  static Optional<Number> readNumber(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null || jsonNode.isNull()) {
      return Optional.empty();
    }

    if (!jsonNode.isNumber()) {
      throw new ArtifactParseException("Value must be a number", fieldKey, path);
    }

    if (jsonNode.isIntegralNumber()) {
      return Optional.of(jsonNode.asLong());
    } else {
      return Optional.of(jsonNode.asDouble());
    }
  }


  static boolean readBoolean(ObjectNode sourceNode, String path, String fieldKey, boolean defaultValue) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null || jsonNode.isNull()) {
      return defaultValue;
    }

    if (!jsonNode.isBoolean()) {
      throw new ArtifactParseException("Value must be boolean", fieldKey, path);
    }

    return jsonNode.asBoolean();
  }


  static Optional<Boolean> readOptionalBoolean(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null || jsonNode.isNull()) {
      return Optional.empty();
    }

    if (!jsonNode.isBoolean()) {
      throw new ArtifactParseException("Value must be boolean", fieldKey, path);
    }

    return Optional.of(jsonNode.asBoolean());
  }


  static ObjectNode readChildNode(ObjectNode parentNode, String path, String fieldKey) {
    JsonNode childNode = parentNode.get(fieldKey);

    if (childNode == null) {
      throw new ArtifactParseException("No " + fieldKey + " field", fieldKey, path);
    } else if (childNode.isNull()) {
      throw new ArtifactParseException("Null " + fieldKey + " field", fieldKey, path);
    } else if (!childNode.isObject()) {
      throw new ArtifactParseException("Value must be an object", fieldKey, path);
    }

    return (ObjectNode) childNode;
  }


  static ObjectNode readValueConstraintsNode(ObjectNode parentNode, String path, String fieldKey) {
    JsonNode childNode = parentNode.get(fieldKey);

    if (childNode == null) {
      return null;
    } else if (childNode.isNull()) {
      return null;
    } else if (!childNode.isObject()) {
      throw new ArtifactParseException("Value must be an object", fieldKey, path);
    }

    return (ObjectNode) childNode;
  }


  static ObjectNode readAnnotationsNode(ObjectNode parentNode, String path, String fieldKey) {
    JsonNode childNode = parentNode.get(fieldKey);

    if (childNode == null) {
      return null;
    } else if (childNode.isNull()) {
      return null;
    } else if (!childNode.isObject()) {
      throw new ArtifactParseException("Value must be an object", fieldKey, path);
    }

    return (ObjectNode) childNode;
  }


  static LinkedHashMap<String, String> readString2StringMap(ObjectNode parentNode, String path, String fieldKey) {
    LinkedHashMap<String, String> string2StringMap = new LinkedHashMap<>();

    JsonNode childNode = parentNode.get(fieldKey);

    if (childNode != null && !childNode.isNull()) {

      if (!childNode.isObject()) {
        throw new ArtifactParseException("Value of field must be an object", fieldKey, path);
      }

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = childNode.fields();

      while (fieldEntries.hasNext()) {
        var fieldEntry = fieldEntries.next();

        if (fieldEntry.getValue().isTextual()) {
          String currentFieldKey = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          if (currentFieldValue != null) {
            string2StringMap.put(currentFieldKey, currentFieldValue);
          }
        } else {
          throw new ArtifactParseException("Object in field must contain string values", fieldKey, path);
        }
      }
    }
    return string2StringMap;
  }


  static LinkedHashMap<String, String> readSimpleContextEntries(ObjectNode parentNode, String path) {
    LinkedHashMap<String, String> string2StringMap = new LinkedHashMap<>();

    JsonNode childNode = parentNode.get(JSON_LD_CONTEXT);

    if (childNode != null && !childNode.isNull()) {

      if (!childNode.isObject()) {
        throw new ArtifactParseException("Value of field must be an object", JSON_LD_CONTEXT, path);
      }

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = childNode.fields();

      while (fieldEntries.hasNext()) {
        var fieldEntry = fieldEntries.next();

        if (fieldEntry.getValue().isTextual()) {
          String currentFieldKey = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          if (currentFieldValue != null) {
            string2StringMap.put(currentFieldKey, currentFieldValue);
          }
        }
      }
    }
    return string2StringMap;
  }


  static LinkedHashMap<String, URI> readString2UriMap(ObjectNode parentNode, String path, String fieldKey) {
    LinkedHashMap<String, URI> string2UriMap = new LinkedHashMap<>();

    JsonNode childNode = parentNode.get(fieldKey);

    if (childNode != null) {

      if (!childNode.isObject()) {
        throw new ArtifactParseException("Value of field must be an object", fieldKey, path);
      }

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = childNode.fields();

      while (fieldEntries.hasNext()) {
        var fieldEntry = fieldEntries.next();

        if (fieldEntry.getValue().isTextual()) { // We only record simple term->term URI entries
          String currentFieldKey = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          try {
            URI currentFieldUriValue = new URI(currentFieldValue);
            string2UriMap.put(currentFieldKey, currentFieldUriValue);
          } catch (URISyntaxException e) {
            throw new ArtifactParseException("Object in field must contain URI values", fieldKey, path);
          }
        }
      }
    }
    return string2UriMap;
  }


  static ObjectNode getFieldNode(ObjectNode sourceNode, String path) {
    if (sourceNode.isArray()) {
      JsonNode itemsNode = sourceNode.get(JSON_SCHEMA_ITEMS);
      if (itemsNode == null || !itemsNode.isArray() || !itemsNode.iterator().hasNext()) {
        throw new ArtifactParseException("Expecting array", JSON_SCHEMA_ITEMS, path);
      }

      JsonNode itemNode = itemsNode.iterator().next();
      if (!itemNode.isObject()) {
        throw new ArtifactParseException("Expecting object as first element", JSON_SCHEMA_ITEMS, path);
      }
      return (ObjectNode) itemNode;
    } else {
      return sourceNode;
    }
  }


  static Optional<Version> readVersion(ObjectNode sourceNode, String path, String fieldKey) {
    Optional<String> version = readString(sourceNode, path, fieldKey);

    if (version.isEmpty())
      return Optional.empty();

    if (!Version.isValidVersion(version.get()))
      throw new ArtifactParseException("Invalid version " + version.get(), fieldKey, path);

    return Optional.of(Version.fromString(version.get()));
  }


  static Optional<Status> readStatus(ObjectNode sourceNode, String path, String fieldKey) {
    Optional<String> status = readString(sourceNode, path, fieldKey);

    if (status.isPresent()) {
      return Optional.of(Status.fromString(status.get()));
    } else {
      return Optional.empty();
    }
  }


  static Optional<OffsetDateTime> readOffsetDateTime(ObjectNode sourceNode, String path, String fieldKey) {
    Optional<String> dateTimeValue = readString(sourceNode, path, fieldKey);
    try {
      if (dateTimeValue.isPresent()) {
        return Optional.of(OffsetDateTimes.parse(dateTimeValue.get()));
      } else {
        return Optional.empty();
      }
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException("Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(),
          fieldKey, path);
    }
  }


  static OffsetDateTime readRequiredOffsetDateTime(ObjectNode sourceNode, String path, String fieldKey) {
    String dateTimeValue = readRequiredString(sourceNode, path, fieldKey);

    try {
      return OffsetDateTimes.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException("Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(),
          fieldKey, path);
    }
  }


  static Optional<URI> readUri(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null || jsonNode.isNull()) {
      return Optional.empty();
    }

    if (!jsonNode.isTextual()) {
      throw new ArtifactParseException("Value of URI field must be textual", fieldKey, path);
    }

    String uriValue = jsonNode.asText();

    if (uriValue.isEmpty()) {
      return Optional.empty();
    }

    if (XsdDatatype.isKnownXsdDatatype(uriValue)) {
      return Optional.of(XsdDatatype.fromString(uriValue).toUri());
    } else {
      try {
        return Optional.of(new URI(uriValue));
      } catch (URISyntaxException e) {
        throw new ArtifactParseException("Value " + uriValue + " in URI field must be a valid URI", fieldKey, path);
      }
    }
  }


  static Optional<String> readString(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null || jsonNode.isNull()) {
      return Optional.empty();
    } else if (!jsonNode.isTextual()) {
      throw new ArtifactParseException("Value of text field must be textual", fieldKey, path);
    } else {
      return Optional.of(jsonNode.asText());
    }
  }


  static Optional<String> readPossiblyNullString(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null) {
      return Optional.empty();
    } else if (jsonNode.isNull()) {
      return Optional.empty();
    } else if (!jsonNode.isTextual()) {
      throw new ArtifactParseException("Value of text field must be textual", fieldKey, path);
    } else {
      return Optional.of(jsonNode.asText());
    }
  }


  static String readString(ObjectNode sourceNode, String path, String fieldKey, String defaultValue) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null || jsonNode.isNull()) {
      return defaultValue;
    } else if (!jsonNode.isTextual()) {
      throw new ArtifactParseException("Value of text field must be textual", fieldKey, path);
    } else {
      return jsonNode.asText();
    }
  }


  static String readRequiredString(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null) {
      throw new ArtifactParseException("No text value present", fieldKey, path);
    } else if (jsonNode.isNull()) {
      throw new ArtifactParseException("Null value present", fieldKey, path);
    } else {
      if (!jsonNode.isTextual()) {
        throw new ArtifactParseException("Value must be textual", fieldKey, path);
      }

      return jsonNode.asText();
    }
  }


  static int readRequiredInt(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null) {
      throw new ArtifactParseException("No int value present", fieldKey, path);
    } else if (jsonNode.isNull()) {
      throw new ArtifactParseException("Null value present", fieldKey, path);
    } else {
      if (!jsonNode.isInt()) {
        throw new ArtifactParseException("Value must be an int", fieldKey, path);
      }

      return jsonNode.asInt();
    }
  }


  static URI readRequiredUri(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);

    if (jsonNode == null) {
      throw new ArtifactParseException("No URI value present", fieldKey, path);
    } else if (jsonNode.isNull()) {
      throw new ArtifactParseException("Null value present", fieldKey, path);
    } else {
      if (!jsonNode.isTextual()) {
        throw new ArtifactParseException("Value must be a URI", fieldKey, path);
      }

      try {
        return new URI(jsonNode.asText());
      } catch (URISyntaxException e) {
        throw new ArtifactParseException("Value must be a valid URI", fieldKey, path);
      }
    }
  }


  static List<String> readStringArray(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);
    List<String> stringValues = new ArrayList<>();

    if (jsonNode != null && !jsonNode.isNull()) {
      if (jsonNode.isArray()) {
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();

        int arrayIndex = 0;
        while (nodeIterator.hasNext()) {
          JsonNode jsonValueNode = nodeIterator.next();
          if (jsonValueNode != null) {
            if (!jsonValueNode.isTextual()) {
              throw new ArtifactParseException("Value in array at index " + arrayIndex + " must be a string", fieldKey,
                  path);
            }
            String stringValue = jsonValueNode.asText();
            if (!stringValue.isEmpty()) {
              stringValues.add(stringValue);
            }
          }
          arrayIndex++;
        }
      } else {
        String textValue = readString(sourceNode, path, fieldKey, "");
        if (textValue != null && !textValue.isEmpty()) {
          stringValues.add(textValue);
        }
      }
    }
    return stringValues;
  }


  static List<URI> readUriArray(ObjectNode sourceNode, String path, String fieldKey) {
    JsonNode jsonNode = sourceNode.get(fieldKey);
    List<URI> uriValues = new ArrayList<>();

    if (jsonNode != null && !jsonNode.isNull()) {
      if (jsonNode.isArray()) {
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();

        int arrayIndex = 0;
        while (nodeIterator.hasNext()) {
          JsonNode itemNode = nodeIterator.next();
          if (itemNode != null) {
            if (!itemNode.isTextual()) {
              throw new ArtifactParseException("Value in URI array at index " + arrayIndex + " must be textual",
                  fieldKey, path);
            }
            try {
              URI uriValue = new URI(itemNode.asText());
              uriValues.add(uriValue);
            } catch (URISyntaxException e) {
              throw new ArtifactParseException("Value in URI array at index " + arrayIndex + " must a valid URI",
                  fieldKey, path);
            }
          }
          arrayIndex++;
        }
      } else {
        Optional<URI> uriValue = readUri(sourceNode, path, fieldKey);
        if (uriValue.isPresent()) {
          uriValues.add(uriValue.get());
        }
      }
    }
    return uriValues;
  }


  static Optional<Version> readModelVersion(ObjectNode sourceNode, String path) {
    Optional<String> versionString = readString(sourceNode, path, SCHEMA_ORG_SCHEMA_VERSION);

    if (versionString.isEmpty())
      return Optional.empty();

    if (!Version.isValidVersion(versionString.get()))
      throw new ArtifactParseException("Invalid model version " + versionString.get(),
        SCHEMA_ORG_SCHEMA_VERSION, path);

    return Optional.of(Version.fromString(versionString.get()));
  }


  static Optional<String> readLanguage(ObjectNode sourceNode, String path) {
    Map<String, String> contextEntries = readSimpleContextEntries(sourceNode, path);

    if (contextEntries.containsKey(JSON_LD_LANGUAGE)) {
      return Optional.of(contextEntries.get(JSON_LD_LANGUAGE));
    } else {
      return Optional.empty();
    }
  }


  static boolean hasJsonLdContextField(ObjectNode sourceNode) {
    return sourceNode.get(JSON_LD_CONTEXT) != null;
  }

}
