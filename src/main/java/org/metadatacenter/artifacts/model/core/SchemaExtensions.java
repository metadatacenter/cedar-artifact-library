package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.util.*;

/** Namespace-bound schema metadata, separate from CEDAR properties and instance constraints. */
public record SchemaExtensions(LinkedHashMap<String, URI> prefixes, ObjectNode properties) {
  private static final Set<String> RESERVED = Set.of("xsd", "pav", "bibo", "oslc", "schema", "skos", "rdfs");
  public SchemaExtensions {
    Objects.requireNonNull(prefixes); Objects.requireNonNull(properties);
    var sorted = new LinkedHashMap<String, URI>();
    for (String key : new TreeSet<>(prefixes.keySet())) sorted.put(key, prefixes.get(key));
    for (var entry : sorted.entrySet()) {
      if (!isExtensionPrefix(entry.getKey()) || entry.getValue() == null || !entry.getValue().isAbsolute())
        throw new IllegalArgumentException("Invalid schema extension prefix: " + entry.getKey());
    }
    prefixes = sorted;
    for (var names = properties.fieldNames(); names.hasNext();) {
      String name = names.next(); int colon = name.indexOf(':');
      if (colon < 1 || colon == name.length() - 1 || !prefixes.containsKey(name.substring(0, colon)))
        throw new IllegalArgumentException("Unbound schema extension property: " + name);
    }
    properties = (ObjectNode) ordered(properties);
  }
  public static boolean isExtensionPrefix(String key) {
    return key.matches("[A-Za-z_][A-Za-z0-9_-]*") && !RESERVED.contains(key) && !key.equals("_annotations");
  }
  public static SchemaExtensions empty() {
    return new SchemaExtensions(new LinkedHashMap<>(), JsonNodeFactory.instance.objectNode());
  }
  public boolean isEmpty() { return prefixes.isEmpty() && properties.isEmpty(); }
  @Override public LinkedHashMap<String, URI> prefixes() { return new LinkedHashMap<>(prefixes); }
  @Override public ObjectNode properties() { return properties.deepCopy(); }
  private static long index(String key) {
    if (!key.matches("0|[1-9][0-9]{0,9}")) return Long.MAX_VALUE;
    long value = Long.parseLong(key); return value < 4294967295L ? value : Long.MAX_VALUE;
  }
  /** JSON's integer-index keys precede other keys, which are ordered lexically in both libraries. */
  public static JsonNode ordered(JsonNode node) {
    if (node.isObject()) {
      ObjectNode result = JsonNodeFactory.instance.objectNode(); List<String> keys = new ArrayList<>();
      node.fieldNames().forEachRemaining(keys::add);
      keys.sort(Comparator.comparingLong(SchemaExtensions::index).thenComparing(Comparator.naturalOrder()));
      keys.forEach(key -> result.set(key, ordered(node.get(key)))); return result;
    }
    if (node.isArray()) {
      var result = JsonNodeFactory.instance.arrayNode(); node.forEach(value -> result.add(ordered(value))); return result;
    }
    return node.deepCopy();
  }
}
