package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * Compares a stored schema artifact with the JSON this library writes after reading it, and sorts every
 * difference into a known normalization or none.
 *
 * <p>A round trip that reads its own output compares the reader with itself: a key the reader ignores is
 * absent on both sides. Comparing the stored source with its rendering exposes such a loss. The writer also
 * changes a source on purpose, and each change it intends is a {@link Family}, defined narrowly enough that
 * a lost child, bound, label or constraint stays unexplained.
 */
final class SourceRenderingComparison
{
  private SourceRenderingComparison() {}

  /** The changes the writer intends to make to a stored schema artifact. A difference outside them is a defect. */
  enum Family
  {
    /** The JSON Schema {@code title} and {@code description} of an artifact are written from its name. */
    SCHEMA_TEXT("schemaText"),
    /**
     * The JSON Schema the writer derives from the model is written afresh: every {@code required} and
     * {@code additionalProperties}, the schema of each JSON-LD keyword property such as {@code @type},
     * {@code @language} or {@code schema:name}, the prefix and term definitions of an artifact's own
     * {@code @context}, and the value schema of a static field.
     */
    GENERATED_SCHEMA("generatedSchema"),
    /** A value constraint stated at its default, {@code false}, an empty list or an empty default, is left out. */
    DEFAULT_CONSTRAINT_DROPPED("defaultConstraintDropped"),
    /** A constraint list or flag the model always holds is written at its default where the source omits it. */
    DEFAULT_CONSTRAINT_ADDED("defaultConstraintAdded"),
    /** An empty {@code skos:altLabel} list is left out. */
    EMPTY_LABELS_DROPPED("emptyLabelsDropped"),
    /** A key the model no longer has, {@code multiValued} or {@code _ui.pages}, is left out. */
    LEGACY_KEY_DROPPED("legacyKeyDropped"),
    /** A {@code _ui.hidden} stated as {@code false} is left out. */
    UI_DEFAULT_DROPPED("uiDefaultDropped"),
    /** A static field that states no {@code _ui._content} is written with it as null. */
    STATIC_CONTENT_SUPPLIED("staticContentSupplied"),
    /** A {@code multipleChoice} stated on a field that offers no choices is left out. */
    INAPPLICABLE_CONSTRAINT_DROPPED("inapplicableConstraintDropped"),
    /**
     * A key stated on the array that wraps a repeated child, rather than on the child in its {@code items},
     * is left out: the wrapper is JSON Schema, and the model holds the child.
     */
    ARRAY_WRAPPER_KEY_DROPPED("arrayWrapperKeyDropped"),
    /** A template has no preferred label in the model or the template meta-schema, so a stated one is left out. */
    TEMPLATE_PREF_LABEL_DROPPED("templatePrefLabelDropped"),
    /** An artifact with no {@code @id} is written with a null one. */
    NULL_ID_SUPPLIED("nullIdSupplied"),
    /** An empty {@code _ui} description or label entry is left out, and an absent map is written empty. */
    EMPTY_UI_ENTRY_DROPPED("emptyUiEntryDropped"),
    /**
     * A date field has no time, so its time format and time zone settings are left out, and a temporal
     * field's {@code _ui} has no room for value recommendation, so that is left out too.
     */
    INAPPLICABLE_TEMPORAL_UI("inapplicableTemporalUi"),
    /** An {@code _ui.order} entry that names no child is left out. */
    DANGLING_ORDER_ENTRY("danglingOrderEntry");

    private final String key;

    Family(String key) { this.key = key; }

    String key() { return key; }

    static Family of(String key)
    {
      for (Family family : values())
        if (family.key.equals(key)) return family;
      throw new IllegalArgumentException("Unknown source rendering family: " + key);
    }
  }

  /** One place the source and its rendering differ. An absent value is {@code null}; {@code family} is empty when unexplained. */
  record Difference(String path, JsonNode source, JsonNode rendering, Optional<Family> family)
  {
    @Override public String toString()
    {
      return path + ": source " + shown(source) + ", rendering " + shown(rendering);
    }

    private static String shown(JsonNode node)
    {
      if (node == null) return "absent";
      String text = node.toString();
      return text.length() > 120 ? text.substring(0, 120) + "…" : text;
    }
  }

  static List<Difference> compare(JsonNode source, JsonNode rendering)
  {
    List<Difference> out = new ArrayList<>();
    walk(new ArrayList<>(), source, rendering, new ArrayList<>(), out);
    return out;
  }

  // ancestors holds the source objects that contain the current pair, outermost first.
  private static void walk(List<JsonNode> ancestors, JsonNode source, JsonNode rendering, List<String> at,
    List<Difference> out)
  {
    if (Objects.equals(source, rendering)) return;
    if (source instanceof ObjectNode x && rendering instanceof ObjectNode y) {
      ancestors.add(x);
      for (String key : keys(x, y)) {
        JsonNode a = x.get(key), b = y.get(key);
        if (Objects.equals(a, b)) continue;
        List<String> here = append(at, key);
        Optional<Family> family = family(ancestors, x, here, a, b);
        if (family.isPresent()) out.add(new Difference(pointer(here), a, b, family));
        else walk(ancestors, a, b, here, out);
      }
      ancestors.remove(ancestors.size() - 1);
      return;
    }
    if (source instanceof ArrayNode x && rendering instanceof ArrayNode y && x.size() == y.size()) {
      for (int i = 0; i < x.size(); i++) walk(ancestors, x.get(i), y.get(i), append(at, String.valueOf(i)), out);
      return;
    }
    out.add(new Difference(pointer(at), source, rendering, Optional.empty()));
  }

  private static Optional<Family> family(List<JsonNode> ancestors, ObjectNode parent, List<String> at, JsonNode source,
    JsonNode rendering)
  {
    String key = at.get(at.size() - 1);
    String parentKey = at.size() > 1 ? at.get(at.size() - 2) : "";
    String grandparentKey = at.size() > 2 ? at.get(at.size() - 3) : "";

    if (parent.has("$schema") && (key.equals("title") || key.equals("description")) && isText(source) && isText(rendering))
      return Optional.of(Family.SCHEMA_TEXT);

    if (isSchema(parent) && (key.equals("required") || key.equals("additionalProperties")))
      return Optional.of(Family.GENERATED_SCHEMA);
    if (parentKey.equals("properties") && isKeyword(key) && !key.equals("@context"))
      return Optional.of(Family.GENERATED_SCHEMA);
    if (parentKey.equals("@context") && grandparentObject(ancestors).map(o -> o.has("$schema")).orElse(false))
      return Optional.of(Family.GENERATED_SCHEMA);
    if (isStaticField(parent) && (key.equals("properties") || key.equals("@context")))
      return Optional.of(Family.GENERATED_SCHEMA);
    if (key.equals("properties") && parent.path("_ui").path("inputType").asText().equals("attribute-value"))
      return Optional.of(Family.GENERATED_SCHEMA);

    if (parentKey.equals("_valueConstraints") && rendering == null && isDefault(source))
      return Optional.of(Family.DEFAULT_CONSTRAINT_DROPPED);
    if (key.equals("_valueConstraints") && rendering == null && source instanceof ObjectNode constraints
      && StreamSupport.stream(constraints.spliterator(), false).allMatch(SourceRenderingComparison::isDefault))
      return Optional.of(Family.DEFAULT_CONSTRAINT_DROPPED);

    if (grandparentKey.equals("_ui") && (parentKey.equals("propertyDescriptions") || parentKey.equals("propertyLabels"))
      && rendering == null && isText(source) && source.asText().isEmpty())
      return Optional.of(Family.EMPTY_UI_ENTRY_DROPPED);
    if (parentKey.equals("_ui") && (key.equals("propertyDescriptions") || key.equals("propertyLabels"))
      && source == null && rendering instanceof ObjectNode map && map.isEmpty())
      return Optional.of(Family.EMPTY_UI_ENTRY_DROPPED);

    if (parentKey.equals("_ui") && (key.equals("inputTimeFormat") || key.equals("timezoneEnabled")) && rendering == null
      && grandparentObject(ancestors).map(field -> field.path("_valueConstraints").path("temporalType").asText())
      .orElse("").equals("xsd:date"))
      return Optional.of(Family.INAPPLICABLE_TEMPORAL_UI);
    if (parentKey.equals("_ui") && key.equals("valueRecommendationEnabled") && rendering == null
      && parent.path("inputType").asText().equals("temporal"))
      return Optional.of(Family.INAPPLICABLE_TEMPORAL_UI);

    if (parentKey.equals("_valueConstraints") && key.equals("defaultValue") && rendering == null && isText(source)
      && source.asText().isEmpty())
      return Optional.of(Family.DEFAULT_CONSTRAINT_DROPPED);
    if (parentKey.equals("_valueConstraints") && source == null && isDefault(rendering))
      return Optional.of(Family.DEFAULT_CONSTRAINT_ADDED);
    if (key.equals("skos:altLabel") && rendering == null && source instanceof ArrayNode labels && labels.isEmpty())
      return Optional.of(Family.EMPTY_LABELS_DROPPED);
    if ((key.equals("multiValued") || (parentKey.equals("_ui") && key.equals("pages"))) && rendering == null)
      return Optional.of(Family.LEGACY_KEY_DROPPED);
    if (parentKey.equals("_ui") && key.equals("hidden") && rendering == null && isDefault(source))
      return Optional.of(Family.UI_DEFAULT_DROPPED);
    if (parentKey.equals("_ui") && key.equals("_content") && source == null && rendering != null && rendering.isNull()
      && grandparentObject(ancestors).map(SourceRenderingComparison::isStaticField).orElse(false))
      return Optional.of(Family.STATIC_CONTENT_SUPPLIED);
    if (parentKey.equals("_valueConstraints") && key.equals("multipleChoice") && rendering == null
      && !Set.of("checkbox", "list", "radio").contains(grandparentObject(ancestors)
      .map(field -> field.path("_ui").path("inputType").asText()).orElse("")))
      return Optional.of(Family.INAPPLICABLE_CONSTRAINT_DROPPED);
    if (rendering == null && parent.path("type").asText().equals("array") && parent.has("items")
      && !Set.of("type", "minItems", "maxItems", "items").contains(key))
      return Optional.of(Family.ARRAY_WRAPPER_KEY_DROPPED);
    if (at.size() == 1 && key.equals("skos:prefLabel") && rendering == null
      && parent.path("@type").asText().endsWith("/Template"))
      return Optional.of(Family.TEMPLATE_PREF_LABEL_DROPPED);
    if (key.equals("@id") && source == null && rendering != null && rendering.isNull() && parent.has("$schema"))
      return Optional.of(Family.NULL_ID_SUPPLIED);

    if (parentKey.equals("_ui") && key.equals("order") && source instanceof ArrayNode order && rendering instanceof ArrayNode kept) {
      JsonNode children = grandparentObject(ancestors).map(artifact -> artifact.path("properties")).orElse(null);
      List<JsonNode> named = StreamSupport.stream(order.spliterator(), false)
        .filter(name -> children != null && children.has(name.asText())).toList();
      if (named.size() < order.size() && named.equals(StreamSupport.stream(kept.spliterator(), false).toList()))
        return Optional.of(Family.DANGLING_ORDER_ENTRY);
    }
    return Optional.empty();
  }

  // The object that holds the object holding the current key.
  private static Optional<ObjectNode> grandparentObject(List<JsonNode> ancestors)
  {
    return ancestors.size() > 1 && ancestors.get(ancestors.size() - 2) instanceof ObjectNode object
      ? Optional.of(object) : Optional.empty();
  }

  // A JSON-LD keyword or a prefixed term, as opposed to the name of a child.
  private static boolean isKeyword(String key)
  {
    return key.startsWith("@") || key.contains(":") || key.equals("_annotations");
  }

  private static boolean isSchema(ObjectNode node)
  {
    return node.has("$schema") || node.has("properties") || node.has("type");
  }

  private static boolean isStaticField(JsonNode node)
  {
    return node.path("@type").asText().endsWith("/StaticTemplateField");
  }

  private static boolean isDefault(JsonNode value)
  {
    return value != null && ((value.isBoolean() && !value.asBoolean()) || (value.isArray() && value.isEmpty()));
  }

  private static boolean isText(JsonNode node) { return node != null && node.isTextual(); }

  private static Set<String> keys(ObjectNode a, ObjectNode b)
  {
    Set<String> keys = new LinkedHashSet<>();
    a.fieldNames().forEachRemaining(keys::add);
    b.fieldNames().forEachRemaining(keys::add);
    return keys;
  }

  private static List<String> append(List<String> at, String key)
  {
    List<String> path = new ArrayList<>(at);
    path.add(key);
    return path;
  }

  private static String pointer(List<String> at)
  {
    return "/" + String.join("/", at.stream().map(s -> s.replace("~", "~0").replace("/", "~1")).toList());
  }
}
