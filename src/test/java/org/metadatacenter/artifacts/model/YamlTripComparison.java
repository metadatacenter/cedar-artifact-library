package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.yaml.YamlConstants;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Compares the JSON this library writes straight from an artifact with the JSON it writes after the
 * artifact has been through its own YAML, and sorts every difference into a known family or none.
 *
 * <p>The YAML tests compare YAML with YAML, and the JSON tests compare JSON with JSON, so a reader and
 * a writer that agree on a wrong reading of an absent key pass them all. Writing JSON from both paths
 * and comparing it is what exposes such a reading. The TypeScript library makes the same comparison
 * in {@code itest/resources/JsonThroughYaml.ts}, with the same families.
 */
final class YamlTripComparison
{
  private YamlTripComparison() {}

  /** The differences the YAML form is known to produce. A difference outside them is a defect. */
  enum Family
  {
    /** YAML does not carry a schema artifact's JSON Schema {@code title} and {@code description}. */
    SCHEMA_TEXT("schemaText"),
    /** YAML names a BioPortal ontology by its acronym, and the reader rebuilds its address. */
    ONTOLOGY_ADDRESS("ontologyAddress"),
    /** YAML has no place for an absent {@code _ui} label or description entry, so the child's own fills it. */
    UI_ENTRY_FILLED("uiEntryFilled"),
    /**
     * A YAML instance states neither an empty value nor a property IRI, and it is completed against its
     * template on the way back. Empty content is dropped, or re-added where the stored instance omitted
     * a child, an empty value takes its datatype from the template, and every {@code @context} property
     * IRI is the template's. An element instance holding nothing but an identifier loses the identifier,
     * since the element itself is dropped.
     */
    INSTANCE_COMPLETION("instanceCompletion"),
    /**
     * YAML writes a number in its shortest spelling, so a number can come back with the same value written
     * another way: a decimal bound stored as {@code 5.0} comes back as {@code 5}. The two are equal as
     * decimals.
     */
    NUMBER_SPELLING("numberSpelling");

    private final String key;

    Family(String key) { this.key = key; }

    String key() { return key; }

    static Family of(String key)
    {
      for (Family family : values())
        if (family.key.equals(key)) return family;
      throw new IllegalArgumentException("Unknown YAML trip family: " + key);
    }
  }

  /** One place the two renderings differ. An absent value is {@code null}; {@code family} is empty when unexplained. */
  record Difference(String path, JsonNode direct, JsonNode viaYaml, Optional<Family> family)
  {
    @Override public String toString()
    {
      return path + ": direct " + shown(direct) + ", via YAML " + shown(viaYaml);
    }

    private static String shown(JsonNode node)
    {
      if (node == null) return "absent";
      String text = node.toString();
      return text.length() > 120 ? text.substring(0, 120) + "…" : text;
    }
  }


  static List<Difference> compareSchemas(JsonNode direct, JsonNode viaYaml)
  {
    List<Difference> out = new ArrayList<>();
    walkSchema(direct, viaYaml, List.of(), out);
    return out;
  }

  static List<Difference> compareInstances(JsonNode direct, JsonNode viaYaml)
  {
    List<Difference> out = new ArrayList<>();
    walkInstance(direct, viaYaml, List.of(), out);
    return out;
  }

  /** A literal value with no {@code @value}, or an IRI value with nothing in it. */
  static boolean isEmptyValue(JsonNode value)
  {
    if (!(value instanceof ObjectNode object)) return false;
    if (object.isEmpty()) return true;
    return object.has("@value") && object.get("@value").isNull()
      && names(object).stream().allMatch(key -> key.equals("@value") || key.equals("@type") || key.equals("@language"));
  }

  /**
   * What completion can drop or re-add: an empty value, a list of them (none included), or an element
   * instance with no identifier whose children are all empty in turn.
   */
  static boolean isEmptyContent(JsonNode value)
  {
    if (isEmptyValue(value)) return true;
    if (value instanceof ArrayNode array) return StreamSupport.stream(array.spliterator(), false).allMatch(YamlTripComparison::isEmptyContent);
    return value instanceof ObjectNode object && isElementInstance(object) && isAbsentOrNull(object.get("@id")) && holdsNothing(object);
  }

  // An element instance holds children by name. A value holds only JSON-LD keywords and prefixed terms.
  private static boolean isElementInstance(ObjectNode value)
  {
    return value.has("@context") || names(value).stream().anyMatch(key -> !key.startsWith("@") && !key.contains(":"));
  }

  // Every child is empty. An attribute-value field lists its attributes' names, and counts as empty when
  // every attribute it names is.
  private static boolean holdsNothing(ObjectNode element)
  {
    return names(element).stream().allMatch(key -> {
      if (key.equals("@context") || key.equals("@id")) return true;
      JsonNode child = element.get(key);
      if (isNameList(child))
        return StreamSupport.stream(child.spliterator(), false).allMatch(name -> isEmptyContent(element.get(name.asText())));
      return isEmptyContent(child);
    });
  }

  private static void walkSchema(JsonNode direct, JsonNode viaYaml, List<String> at, List<Difference> out)
  {
    if (Objects.equals(direct, viaYaml)) return;
    if (direct instanceof ObjectNode x && viaYaml instanceof ObjectNode y) {
      for (String key : keys(x, y)) {
        JsonNode a = x.get(key), b = y.get(key);
        if (Objects.equals(a, b)) continue;
        List<String> here = append(at, key);
        Optional<Family> family = schemaFamily(x, here, a, b);
        if (family.isPresent()) out.add(new Difference(pointer(here), a, b, family));
        else walkSchema(a, b, here, out);
      }
      return;
    }
    if (direct instanceof ArrayNode x && viaYaml instanceof ArrayNode y && x.size() == y.size()) {
      for (int i = 0; i < x.size(); i++) walkSchema(x.get(i), y.get(i), append(at, String.valueOf(i)), out);
      return;
    }
    out.add(new Difference(pointer(at), direct, viaYaml, valueFamily(direct, viaYaml)));
  }

  // Two values the walk cannot descend into, which differ as JSON.
  private static Optional<Family> valueFamily(JsonNode direct, JsonNode viaYaml)
  {
    return isNumber(direct) && isNumber(viaYaml) && direct.decimalValue().compareTo(viaYaml.decimalValue()) == 0
      ? Optional.of(Family.NUMBER_SPELLING) : Optional.empty();
  }

  private static Optional<Family> schemaFamily(ObjectNode parent, List<String> at, JsonNode direct, JsonNode viaYaml)
  {
    String key = at.get(at.size() - 1);
    if (parent.has("$schema") && (key.equals("title") || key.equals("description")) && isText(direct) && isText(viaYaml))
      return Optional.of(Family.SCHEMA_TEXT);
    if (endsWith(at, "_ui", "propertyLabels", "*") || endsWith(at, "_ui", "propertyDescriptions", "*"))
      return direct == null && isText(viaYaml) ? Optional.of(Family.UI_ENTRY_FILLED) : Optional.empty();
    if (endsWith(at, "_valueConstraints", "ontologies", "*", "uri") && isText(direct) && isText(viaYaml)) {
      var matcher = YamlConstants.BIOPORTAL_ONTOLOGY_ADDRESS.matcher(direct.asText());
      if (matcher.matches() && viaYaml.asText().equals("https://data.bioontology.org/ontologies/" + matcher.group(1)))
        return Optional.of(Family.ONTOLOGY_ADDRESS);
    }
    return Optional.empty();
  }

  private static void walkInstance(JsonNode direct, JsonNode viaYaml, List<String> at, List<Difference> out)
  {
    if (Objects.equals(direct, viaYaml)) return;
    if (direct instanceof ObjectNode x && viaYaml instanceof ObjectNode y) {
      for (String key : keys(x, y)) {
        JsonNode a = x.get(key), b = y.get(key);
        if (Objects.equals(a, b)) continue;
        List<String> here = append(at, key);
        if (key.equals("@context") && a instanceof ObjectNode context && b instanceof ObjectNode contextViaYaml)
          compareContext(x, y, context, contextViaYaml, here, out);
        else if ((a == null || isEmptyContent(a)) && (b == null || isEmptyContent(b)))
          out.add(new Difference(pointer(here), a, b, Optional.of(Family.INSTANCE_COMPLETION)));
        else if (key.equals("@id") && isAbsentOrNull(b) && isElementInstance(x) && holdsNothing(x) && holdsNothing(y))
          // An element holding nothing is dropped with its identifier, and completion re-adds it without one.
          out.add(new Difference(pointer(here), a, b, Optional.of(Family.INSTANCE_COMPLETION)));
        else if (isNameList(a) && b instanceof ArrayNode && StreamSupport.stream(b.spliterator(), false).allMatch(JsonNode::isTextual))
          compareAttributeNames(x, a, b, here, out);
        else
          walkInstance(a, b, here, out);
      }
      return;
    }
    if (direct instanceof ArrayNode x && viaYaml instanceof ArrayNode y) {
      List<Integer> empties = emptyAt(x), emptiesViaYaml = emptyAt(y);
      if (!empties.equals(emptiesViaYaml) || empties.stream().anyMatch(i -> !Objects.equals(x.get(i), y.get(i))))
        out.add(new Difference(pointer(at), x, y, Optional.of(Family.INSTANCE_COMPLETION)));
      List<JsonNode> kept = filled(x), keptViaYaml = filled(y);
      if (kept.size() != keptViaYaml.size()) {
        out.add(new Difference(pointer(at), x, y, Optional.empty()));
        return;
      }
      for (int i = 0; i < kept.size(); i++) walkInstance(kept.get(i), keptViaYaml.get(i), append(at, String.valueOf(i)), out);
      return;
    }
    out.add(new Difference(pointer(at), direct, viaYaml, valueFamily(direct, viaYaml)));
  }

  // A property's IRI is the template's on the YAML path. Any other @context entry has to survive.
  private static void compareContext(ObjectNode container, ObjectNode containerViaYaml, ObjectNode context,
    ObjectNode contextViaYaml, List<String> at, List<Difference> out)
  {
    for (String key : keys(context, contextViaYaml)) {
      JsonNode a = context.get(key), b = contextViaYaml.get(key);
      if (Objects.equals(a, b)) continue;
      boolean property = (container.has(key) || containerViaYaml.has(key)) && (a == null || a.isTextual())
        && (b == null || b.isTextual());
      out.add(new Difference(pointer(append(at, key)), a, b, property ? Optional.of(Family.INSTANCE_COMPLETION) : Optional.empty()));
    }
  }

  // An attribute-value field lists the names of its attributes. One whose value is empty is dropped.
  private static void compareAttributeNames(ObjectNode container, JsonNode names, JsonNode namesViaYaml, List<String> at,
    List<Difference> out)
  {
    List<String> listed = texts(names), listedViaYaml = texts(namesViaYaml);
    List<String> dropped = listed.stream().filter(Predicate.not(listedViaYaml::contains)).toList();
    boolean onlyEmptiesDropped = dropped.stream().allMatch(name -> isEmptyContent(container.get(name)))
      && listed.stream().filter(Predicate.not(dropped::contains)).toList().equals(listedViaYaml);
    out.add(new Difference(pointer(at), names, namesViaYaml,
      onlyEmptiesDropped ? Optional.of(Family.INSTANCE_COMPLETION) : Optional.empty()));
  }

  private static List<Integer> emptyAt(ArrayNode values)
  {
    return IntStream.range(0, values.size()).filter(i -> isEmptyContent(values.get(i))).boxed().toList();
  }

  private static List<JsonNode> filled(ArrayNode values)
  {
    return StreamSupport.stream(values.spliterator(), false).filter(Predicate.not(YamlTripComparison::isEmptyContent)).toList();
  }

  private static boolean isNameList(JsonNode node)
  {
    return node instanceof ArrayNode array && !array.isEmpty()
      && StreamSupport.stream(array.spliterator(), false).allMatch(JsonNode::isTextual);
  }

  private static boolean isText(JsonNode node) { return node != null && node.isTextual(); }

  private static boolean isNumber(JsonNode node) { return node != null && node.isNumber(); }

  private static boolean isAbsentOrNull(JsonNode node) { return node == null || node.isNull(); }

  private static List<String> texts(JsonNode array)
  {
    return StreamSupport.stream(array.spliterator(), false).map(JsonNode::asText).toList();
  }

  private static List<String> names(ObjectNode object)
  {
    List<String> names = new ArrayList<>();
    object.fieldNames().forEachRemaining(names::add);
    return names;
  }

  private static Set<String> keys(ObjectNode a, ObjectNode b)
  {
    Set<String> keys = new LinkedHashSet<>(names(a));
    keys.addAll(names(b));
    return keys;
  }

  private static List<String> append(List<String> at, String key)
  {
    List<String> here = new ArrayList<>(at);
    here.add(key);
    return here;
  }

  private static boolean endsWith(List<String> at, String... tail)
  {
    if (at.size() < tail.length) return false;
    for (int i = 0; i < tail.length; i++)
      if (!tail[i].equals("*") && !tail[i].equals(at.get(at.size() - tail.length + i))) return false;
    return true;
  }

  private static String pointer(List<String> at)
  {
    StringBuilder pointer = new StringBuilder();
    for (String segment : at) pointer.append('/').append(segment.replace("~", "~0").replace("/", "~1"));
    return pointer.toString();
  }
}
