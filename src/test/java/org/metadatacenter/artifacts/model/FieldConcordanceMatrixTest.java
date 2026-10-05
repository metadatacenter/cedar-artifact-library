package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.core.fields.*;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/** Independent feature expectations plus live fixture verification; TS consumes these exact outputs. */
public class FieldConcordanceMatrixTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();
  private static final Path FIXTURE = Path.of("src/test/resources/concordance/field-matrix.json");
  private static final List<String> FEATURES = List.of("baseline", "identifier", "language", "annotations", "labels",
    "version", "published", "derivedFrom", "previousVersion", "provenance", "allMetadata", "schemaText");

  private static LinkedHashMap<String, Supplier<FieldSchemaArtifactBuilder<?>>> types() {
    var types = new LinkedHashMap<String, Supplier<FieldSchemaArtifactBuilder<?>>>();
    types.put("text", TextField::builder);
    types.put("textarea", TextAreaField::builder);
    types.put("phone", PhoneNumberField::builder);
    types.put("email", EmailField::builder);
    types.put("numeric", () -> NumericField.builder().withNumericType(XsdNumericDatatype.DECIMAL));
    types.put("temporal", () -> TemporalField.builder().withTemporalType(XsdTemporalDatatype.DATE)
      .withTemporalGranularity(TemporalGranularity.DAY));
    types.put("radio", RadioField::builder);
    types.put("checkbox", CheckboxField::builder);
    types.put("singleList", ListField::builder);
    types.put("multipleList", () -> ListField.builder().withMultipleChoice(true));
    types.put("attributeValue", AttributeValueField::builder);
    types.put("controlledTerm", ControlledTermField::builder);
    types.put("link", LinkField::builder);
    types.put("ror", RorField::builder);
    types.put("orcid", OrcidField::builder);
    types.put("pfas", PfasField::builder);
    types.put("rrid", RridField::builder);
    types.put("pubmed", PubMedField::builder);
    types.put("nihGrant", NihGrantIdField::builder);
    types.put("doi", DoiField::builder);
    types.put("image", ImageField::builder);
    types.put("youtube", YouTubeField::builder);
    types.put("richText", RichTextField::builder);
    types.put("sectionBreak", SectionBreakField::builder);
    types.put("pageBreak", PageBreakField::builder);
    return types;
  }

  private static boolean has(String feature, String name) {
    return feature.equals(name) || feature.equals("allMetadata") && !name.equals("published") && !name.equals("schemaText");
  }

  private static FieldSchemaArtifact build(Supplier<FieldSchemaArtifactBuilder<?>> factory, String feature) {
    var b = factory.get().withName("Field").withDescription("Field help")
      .withJsonLdId(URI.create("https://example.org/fields/field"));
    if (has(feature, "identifier")) b.withIdentifier("ID-42");
    if (has(feature, "language")) b.withLanguage("fr");
    if (has(feature, "annotations")) b.withAnnotations(Annotations.builder()
      .withLiteralAnnotation("note", "Résumé – preserved")
      .withIriAnnotation("reference", URI.create("https://example.org/reference")).build());
    if (has(feature, "labels")) b.withPreferredLabel("Preferred").withAlternateLabels(List.of("Alternate", "Autre"));
    if (has(feature, "version")) b.withVersion(Version.fromString("2.3.4")).withStatus(Status.DRAFT);
    if (feature.equals("published")) b.withVersion(Version.fromString("2.3.4")).withStatus(Status.PUBLISHED);
    if (has(feature, "derivedFrom")) b.withDerivedFrom(URI.create("https://example.org/fields/source"));
    if (has(feature, "previousVersion")) b.withPreviousVersion(URI.create("https://example.org/fields/previous"));
    if (has(feature, "provenance")) b.withCreatedBy(URI.create("https://example.org/users/creator"))
      .withModifiedBy(URI.create("https://example.org/users/editor"))
      .withCreatedOn(OffsetDateTime.parse("2026-01-01T01:02:03Z"))
      .withLastUpdatedOn(OffsetDateTime.parse("2026-02-01T04:05:06Z"));
    // Only the description: `title` is derived from the name, so a custom one does not survive a
    // round trip and DerivedTitleTest is where that is asserted.
    if (feature.equals("schemaText")) b.withInternalDescription("Custom schema description");
    if (feature.equals("default")) {
      if (b instanceof LiteralDefaultableFieldBuilder d) d.withDefaultValue("Example");
      else if (b instanceof IriDefaultableFieldBuilder d) d.withDefaultValue(URI.create("https://example.org/value"));
      else if (b instanceof NumericDefaultableFieldBuilder d) d.withDefaultValue(0);
      else if (b instanceof TemporalDefaultableFieldBuilder d) d.withDefaultValue("2026-09-09");
      else if (b instanceof ControlledTermDefaultableFieldBuilder d) d.withDefaultValue(URI.create("https://example.org/term"), "Term");
      else throw new IllegalArgumentException("Unsupported default");
    }
    if (feature.startsWith("size-")) {
      Integer width = feature.contains("width") ? 320 : null;
      Integer height = feature.contains("height") ? 180 : null;
      if (b instanceof ImageField.ImageFieldBuilder i) i.withWidth(width).withHeight(height);
      else if (b instanceof YouTubeField.YouTubeFieldBuilder y) y.withWidth(width).withHeight(height);
      else throw new IllegalArgumentException("Unsupported dimensions");
    }
    return b.build();
  }

  private static JsonNode definition(JsonNode json) { return json.has("items") ? json.get("items") : json; }
  private static void expect(JsonNode json, String pointer, String expected) {
    assertEquals(expected, json.at(pointer).asText(), pointer);
  }

  /** Expected values come from the test contract, never from another renderer's output. */
  private static void assertFeature(JsonNode json, String feature) {
    expect(json, "/schema:name", "Field");
    expect(json, "/schema:description", "Field help");
    expect(json, "/@id", "https://example.org/fields/field");
    if (has(feature, "identifier")) expect(json, "/schema:identifier", "ID-42");
    if (has(feature, "language")) expect(json, "/@context/@language", "fr");
    if (has(feature, "annotations")) {
      expect(json, "/_annotations/note/@value", "Résumé – preserved");
      expect(json, "/_annotations/reference/@id", "https://example.org/reference");
    }
    if (has(feature, "labels")) {
      expect(json, "/skos:prefLabel", "Preferred");
      assertEquals(MAPPER.valueToTree(List.of("Alternate", "Autre")), json.get("skos:altLabel"));
    }
    if (has(feature, "version") || feature.equals("published")) expect(json, "/pav:version", "2.3.4");
    expect(json, "/bibo:status", feature.equals("published") ? "bibo:published" : "bibo:draft");
    if (has(feature, "derivedFrom")) expect(json, "/pav:derivedFrom", "https://example.org/fields/source");
    if (has(feature, "previousVersion")) expect(json, "/pav:previousVersion", "https://example.org/fields/previous");
    if (has(feature, "provenance")) {
      expect(json, "/pav:createdBy", "https://example.org/users/creator");
      expect(json, "/oslc:modifiedBy", "https://example.org/users/editor");
      expect(json, "/pav:createdOn", "2026-01-01T01:02:03Z");
      expect(json, "/pav:lastUpdatedOn", "2026-02-01T04:05:06Z");
    }
    if (feature.equals("schemaText")) {
      // The title is composed from the name whatever a document supplies; the description is the
      // author's and survives. DerivedTitleTest asserts the composition itself.
      expect(json, "/title", "Field field schema");
      expect(json, "/description", "Custom schema description");
    }
    if (feature.equals("default")) {
      String input = json.at("/_ui/inputType").asText();
      JsonNode value = json.at("/_valueConstraints/defaultValue");
      if (input.equals("numeric")) assertEquals(0, value.intValue());
      else if (input.equals("temporal")) assertEquals("2026-09-09", value.asText());
      else if (input.equals("textfield") && json.at("/properties").has("@id")) {
        expect(value, "/termUri", "https://example.org/term");
        expect(value, "/rdfs:label", "Term");
      } else assertEquals(json.at("/properties").has("@id") ? "https://example.org/value" : "Example", value.asText());
      assertFalse(value.isMissingNode(), "defaultValue is missing");
    }
    if (feature.startsWith("size-")) {
      if (feature.contains("width")) expect(json, "/_ui/_size/width", "320");
      else assertTrue(json.at("/_ui/_size/width").isMissingNode());
      if (feature.contains("height")) expect(json, "/_ui/_size/height", "180");
      else assertTrue(json.at("/_ui/_size/height").isMissingNode());
    }
  }

  private static ObjectNode renderCase(String type, Supplier<FieldSchemaArtifactBuilder<?>> factory, String feature) {
    FieldSchemaArtifact field = build(factory, feature);
    ObjectNode result = MAPPER.createObjectNode().put("id", type + "/" + feature).put("type", type).put("feature", feature);
    ObjectNode json = JSON.renderFieldSchemaArtifact(field);
    assertFeature(definition(json), feature);
    assertEquals(json, JSON.renderFieldSchemaArtifact(new JsonArtifactReader().readFieldSchemaArtifact(json)), type + "/" + feature);
    result.set("json", json);
    for (boolean compact : List.of(false, true)) {
      result.put(compact ? "compactYaml" : "yaml", YamlSerializer.getYAML(field, compact, true));
      var yaml = new YamlArtifactRenderer(compact).renderFieldSchemaArtifact(field);
      var restored = new YamlArtifactReader(compact).readFieldSchemaArtifact(yaml);
      if (!compact && !feature.equals("schemaText")) assertFeature(definition(JSON.renderFieldSchemaArtifact(restored)), feature);
      result.set(compact ? "jsonFromCompactYaml" : "jsonFromYaml", JSON.renderFieldSchemaArtifact(restored));
    }
    var template = TemplateSchemaArtifact.builder().withName("Matrix").withDescription("Matrix template")
      .withJsonLdId(URI.create("https://example.org/templates/matrix"))
      .withFieldSchema("property-key", field, "Display label", "Display description").build();
    ObjectNode templateJson = JSON.renderTemplateSchemaArtifact(template);
    assertFeature(definition(templateJson.at("/properties/property-key")), feature);
    result.set("templateJson", templateJson);
    for (boolean compact : List.of(false, true)) {
      result.put(compact ? "templateCompactYaml" : "templateYaml", YamlSerializer.getYAML(template, compact, true));
      var yaml = new YamlArtifactRenderer(compact).renderTemplateSchemaArtifact(template);
      var restored = new YamlArtifactReader(compact).readTemplateSchemaArtifact(yaml);
      ObjectNode restoredJson = JSON.renderTemplateSchemaArtifact(restored);
      if (!compact && !feature.equals("schemaText")) assertFeature(definition(restoredJson.at("/properties/property-key")), feature);
      result.set(compact ? "templateJsonFromCompactYaml" : "templateJsonFromYaml", restoredJson);
    }
    return result;
  }

  @Test public void fieldConcordanceMatrix() throws Exception {
    var factories = types();
    Set<Class<?>> covered = new HashSet<>();
    factories.values().forEach(factory -> covered.add(factory.get().getClass()));
    assertEquals(Set.of(FieldSchemaArtifactBuilder.class.getPermittedSubclasses()), covered, "Every Java field builder needs a matrix row");
    ArrayNode cases = MAPPER.createArrayNode();
    factories.forEach((type, factory) -> {
      for (String feature : FEATURES) cases.add(renderCase(type, factory, feature));
      var b = factory.get();
      if (b instanceof LiteralDefaultableFieldBuilder || b instanceof IriDefaultableFieldBuilder || b instanceof NumericDefaultableFieldBuilder
        || b instanceof TemporalDefaultableFieldBuilder || b instanceof ControlledTermDefaultableFieldBuilder)
        cases.add(renderCase(type, factory, "default"));
      if (type.equals("image") || type.equals("youtube"))
        for (String size : List.of("size-none", "size-width", "size-height", "size-width-height")) cases.add(renderCase(type, factory, size));
    });
    ObjectNode fixture = compress(cases);
    if (Boolean.getBoolean("updateFieldConcordance")) {
      Files.createDirectories(FIXTURE.getParent());
      Files.writeString(FIXTURE, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(fixture) + "\n");
    }
    assertTrue(Files.exists(FIXTURE), "Generate with -DupdateFieldConcordance=true");
    assertEquals(MAPPER.readTree(Files.readString(FIXTURE)), fixture, "Java matrix fixtures are stale; regenerate and refresh TS concordance fixtures");
  }

  // The fixture records each type's baseline whole, and every other case as what it changes: in its
  // JSON, the keys it sets and removes; in its YAML, the lines it replaces. A document read back from
  // YAML is recorded against the case's own JSON, which it mostly equals. Every case asserts that
  // what it records rebuilds exactly what Java wrote, so a reader of the fixture reads Java's output.

  private static final List<String> JSON_DOCUMENTS = List.of("json", "templateJson");
  private static final List<String> YAML_DOCUMENTS = List.of("yaml", "compactYaml", "templateYaml", "templateCompactYaml");
  /** A document read back from YAML, and the document of the case it is recorded against. */
  private static final Map<String, String> READ_BACK = Map.of(
    "jsonFromYaml", "json", "jsonFromCompactYaml", "json",
    "templateJsonFromYaml", "templateJson", "templateJsonFromCompactYaml", "templateJson");

  private static ObjectNode compress(ArrayNode cases) {
    ObjectNode bases = MAPPER.createObjectNode();
    for (JsonNode c : cases) {
      if (!c.path("feature").asText().equals("baseline")) continue;
      ObjectNode base = MAPPER.createObjectNode();
      for (String document : JSON_DOCUMENTS) base.set(document, c.get(document));
      for (String document : YAML_DOCUMENTS) base.set(document, c.get(document));
      bases.set(c.path("type").asText(), base);
    }
    ArrayNode compressed = MAPPER.createArrayNode();
    for (JsonNode c : cases) {
      JsonNode base = bases.get(c.path("type").asText());
      ObjectNode row = MAPPER.createObjectNode().put("id", c.path("id").asText()).put("type", c.path("type").asText())
        .put("feature", c.path("feature").asText());
      for (String document : JSON_DOCUMENTS) row.set(document, changes(base.get(document), c.get(document)));
      for (String document : YAML_DOCUMENTS) row.set(document, lineChanges(base.get(document).asText(), c.get(document).asText()));
      READ_BACK.forEach((document, against) -> row.set(document, changes(c.get(against), c.get(document))));
      assertEquals(c, expand(row, base), c.path("id").asText() + ": the fixture rebuilds what Java wrote");
      compressed.add(row);
    }
    ObjectNode fixture = MAPPER.createObjectNode();
    fixture.set("bases", bases);
    fixture.set("cases", compressed);
    return fixture;
  }

  /** The case a fixture row records, rebuilt from its type's baseline. */
  private static ObjectNode expand(JsonNode row, JsonNode base) {
    ObjectNode c = MAPPER.createObjectNode().put("id", row.path("id").asText()).put("type", row.path("type").asText())
      .put("feature", row.path("feature").asText());
    for (String document : JSON_DOCUMENTS) c.set(document, applyChanges(base.get(document), row.get(document)));
    for (String document : YAML_DOCUMENTS) c.put(document, applyLineChanges(base.get(document).asText(), row.get(document)));
    READ_BACK.forEach((document, against) -> c.set(document, applyChanges(c.get(against), row.get(document))));
    return c;
  }

  /** The keys `to` sets or removes relative to `from`, each at its path; a list or a scalar changes whole. */
  private static ArrayNode changes(JsonNode from, JsonNode to) {
    ArrayNode changes = MAPPER.createArrayNode();
    collectChanges(from, to, MAPPER.createArrayNode(), changes);
    return changes;
  }

  private static void collectChanges(JsonNode from, JsonNode to, ArrayNode path, ArrayNode changes) {
    if (from.equals(to)) return;
    if (from.isObject() && to.isObject()) {
      from.fieldNames().forEachRemaining(key -> {
        if (!to.has(key)) changes.add(MAPPER.createObjectNode().<ObjectNode>set("at", path.deepCopy().add(key)).put("remove", true));
      });
      to.fieldNames().forEachRemaining(key -> {
        if (from.has(key)) collectChanges(from.get(key), to.get(key), path.deepCopy().add(key), changes);
        else changes.add(MAPPER.createObjectNode().<ObjectNode>set("at", path.deepCopy().add(key)).set("set", to.get(key)));
      });
      return;
    }
    changes.add(MAPPER.createObjectNode().<ObjectNode>set("at", path).set("set", to));
  }

  private static JsonNode applyChanges(JsonNode from, JsonNode changes) {
    JsonNode result = from.deepCopy();
    for (JsonNode change : changes) {
      JsonNode at = change.get("at");
      if (at.isEmpty()) {
        result = change.get("set").deepCopy();
        continue;
      }
      ObjectNode parent = (ObjectNode) result;
      for (int i = 0; i < at.size() - 1; i++) parent = (ObjectNode) parent.get(at.get(i).asText());
      String key = at.get(at.size() - 1).asText();
      if (change.has("remove")) parent.remove(key);
      else parent.set(key, change.get("set").deepCopy());
    }
    return result;
  }

  /** The lines `to` replaces in `from`: how many to keep at each end, and what goes between. */
  private static ObjectNode lineChanges(String from, String to) {
    String[] a = from.split("\n", -1);
    String[] b = to.split("\n", -1);
    int prefix = 0;
    while (prefix < a.length && prefix < b.length && a[prefix].equals(b[prefix])) prefix++;
    int suffix = 0;
    while (suffix < a.length - prefix && suffix < b.length - prefix
      && a[a.length - 1 - suffix].equals(b[b.length - 1 - suffix])) suffix++;
    ArrayNode lines = MAPPER.createArrayNode();
    for (int i = prefix; i < b.length - suffix; i++) lines.add(b[i]);
    ObjectNode changes = MAPPER.createObjectNode();
    changes.set("keep", MAPPER.createArrayNode().add(prefix).add(suffix));
    changes.set("lines", lines);
    return changes;
  }

  private static String applyLineChanges(String from, JsonNode changes) {
    String[] a = from.split("\n", -1);
    int prefix = changes.get("keep").get(0).asInt();
    int suffix = changes.get("keep").get(1).asInt();
    List<String> lines = new ArrayList<>(Arrays.asList(a).subList(0, prefix));
    changes.get("lines").forEach(line -> lines.add(line.asText()));
    lines.addAll(Arrays.asList(a).subList(a.length - suffix, a.length));
    return String.join("\n", lines);
  }
}
