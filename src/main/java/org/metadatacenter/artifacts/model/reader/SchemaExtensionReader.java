package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.SchemaExtensions;
import java.net.URI;
import java.util.LinkedHashMap;

final class SchemaExtensionReader {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  static SchemaExtensions json(ObjectNode source) {
    var prefixes = new LinkedHashMap<String, URI>();
    var context = source.path("@context");
    context.fields().forEachRemaining(entry -> {
      if (SchemaExtensions.isExtensionPrefix(entry.getKey()) && entry.getValue().isTextual())
        prefixes.put(entry.getKey(), URI.create(entry.getValue().textValue()));
    });
    ObjectNode properties = MAPPER.createObjectNode();
    source.fields().forEachRemaining(entry -> {
      String key = entry.getKey(); int colon = key.indexOf(':');
      if (colon > 0 && prefixes.containsKey(key.substring(0, colon))) properties.set(key, entry.getValue());
    });
    return new SchemaExtensions(prefixes, properties);
  }
  static SchemaExtensions yaml(LinkedHashMap<String, Object> source) {
    if (!source.containsKey("extensions")) return SchemaExtensions.empty();
    JsonNode value = MAPPER.valueToTree(source.get("extensions"));
    if (!value.isObject() || !value.path("prefixes").isObject() || !value.path("properties").isObject()
        || value.size() != 2) throw new IllegalArgumentException("extensions requires prefixes and properties objects");
    var prefixes = new LinkedHashMap<String, URI>();
    value.get("prefixes").fields().forEachRemaining(entry -> {
      if (!entry.getValue().isTextual()) throw new IllegalArgumentException("Extension namespace must be an IRI string");
      prefixes.put(entry.getKey(), URI.create(entry.getValue().textValue()));
    });
    return new SchemaExtensions(prefixes, (ObjectNode) value.get("properties"));
  }
}
