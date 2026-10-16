package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.core.fields.*;
import org.metadatacenter.artifacts.model.core.fields.constraints.*;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/** Independent feature expectations plus live fixture verification; TS consumes these exact outputs. */
public class FieldConcordanceMatrixTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();
  private static final Path FIXTURE = Path.of("src/test/resources/concordance/field-matrix.json");
  private static final List<String> FEATURES = List.of("baseline", "identifier", "language", "annotations", "labels",
    "version", "published", "derivedFrom", "previousVersion", "provenance", "allMetadata", "schemaText");

  /**
   * The keys that say where a controlled-term constraint's terms come from: `iri`, the source's
   * canonical identity; `sourceSystem`, the system that serves it; and `version`, the snapshot it is
   * pinned to, which is absent or the string "latest" for the newest one. Each kind of constraint has
   * a cell carrying all three with the full version. Each library reads and writes these keys through
   * one routine that every kind shares, so the other forms a version can take are varied on the
   * ontology constraint alone. Those cells carry `iri` and `sourceSystem` too.
   */
  private static final List<String> SOURCE_KINDS = List.of("ontology", "class", "branch", "valueSet");
  private static final List<String> VERSION_FORMS = List.of("absent", "id", "id-effectiveDate", "id-declaredVersion");
  private static final URI SOURCE_IRI = URI.create("http://purl.obolibrary.org/obo/doid.owl");
  private static final URI VALUE_SET_SOURCE_IRI = URI.create("https://example.org/vocabularies/VS");
  private static final String SOURCE_SYSTEM = "bioportal";
  private static final String VERSION_ID = "e1dc041e5f2a";
  private static final String VERSION_EFFECTIVE_DATE = "2026-07-01";
  private static final String VERSION_DECLARED_VERSION = "2026-06-30";

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

  /**
   * The settings a field type has beyond the metadata every field shares, each one a case of its own:
   * its value constraints, its rendering, and the deployment flags a dynamic field carries. The
   * paragraph lengths had a Java fixture and a TypeScript conformance test of their own; they are
   * the length rows here.
   */
  private static List<String> settings(FieldSchemaArtifactBuilder<?> b) {
    List<String> settings = new ArrayList<>();
    if (b instanceof TextField.TextFieldBuilder || b instanceof TextAreaField.TextAreaFieldBuilder)
      settings.addAll(List.of("length-none", "length-zero", "length-min", "length-max", "length-both"));
    if (b instanceof TextField.TextFieldBuilder) settings.add("regex");
    if (b instanceof NumericField.NumericFieldBuilder) settings.addAll(List.of("numericBounds", "decimalPlaces", "unit"));
    if (b instanceof TemporalField.TemporalFieldBuilder) settings.add("timePrecision");
    if (b instanceof RadioField.RadioFieldBuilder || b instanceof CheckboxField.CheckboxFieldBuilder
        || b instanceof ListField.ListFieldBuilder) settings.add("options");
    if (b instanceof ControlledTermField.ControlledTermFieldBuilder) {
      settings.addAll(List.of("ontology", "class", "branch", "valueSet", "actions"));
      for (String kind : SOURCE_KINDS) settings.add(kind + "-version-full");
      for (String form : VERSION_FORMS) settings.add("ontology-version-" + form);
    }
    if (isDynamic(b)) {
      // An attribute-value field carries no value constraints, and its builder takes the two flags
      // that would be kept there and does nothing with them.
      if (!(b instanceof AttributeValueField.AttributeValueFieldBuilder)) settings.addAll(List.of("required", "recommended"));
      settings.addAll(List.of("hidden", "continuePreviousLine"));
      if (b instanceof TextField.TextFieldBuilder || b instanceof ControlledTermField.ControlledTermFieldBuilder)
        settings.add("valueRecommendation");
    }
    return settings;
  }

  /** Whether the builder makes a field that takes a value, which is what has line placement. */
  private static boolean isDynamic(FieldSchemaArtifactBuilder<?> b) {
    try {
      b.getClass().getMethod("withContinuePreviousLine", boolean.class);
      return true;
    } catch (NoSuchMethodException staticField) {
      return false;
    }
  }

  /** A dynamic builder's setter that no common type declares, by name. */
  private static void set(FieldSchemaArtifactBuilder<?> b, String setter) {
    try {
      b.getClass().getMethod(setter, boolean.class).invoke(b, true);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(setter, e);
    }
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
    if (feature.startsWith("length-")) {
      Integer min = feature.equals("length-zero") ? Integer.valueOf(0)
        : feature.equals("length-min") || feature.equals("length-both") ? Integer.valueOf(20) : null;
      Integer max = feature.equals("length-zero") ? Integer.valueOf(0)
        : feature.equals("length-max") || feature.equals("length-both") ? Integer.valueOf(500) : null;
      if (b instanceof TextField.TextFieldBuilder t) {
        if (min != null) t.withMinLength(min);
        if (max != null) t.withMaxLength(max);
      } else if (b instanceof TextAreaField.TextAreaFieldBuilder t) {
        if (min != null) t.withMinLength(min);
        if (max != null) t.withMaxLength(max);
      }
    }
    if (feature.equals("regex")) ((TextField.TextFieldBuilder) b).withRegex("^[A-Z]+$");
    if (feature.equals("numericBounds")) ((NumericField.NumericFieldBuilder) b).withMinValue(1).withMaxValue(10.5);
    if (feature.equals("decimalPlaces")) ((NumericField.NumericFieldBuilder) b).withDecimalPlaces(2);
    if (feature.equals("unit")) ((NumericField.NumericFieldBuilder) b).withUnitOfMeasure("mg");
    if (feature.equals("timePrecision")) ((TemporalField.TemporalFieldBuilder) b).withTemporalType(XsdTemporalDatatype.DATETIME)
      .withTemporalGranularity(TemporalGranularity.SECOND).withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR)
      .withTimeZoneEnabled(true);
    if (feature.equals("options")) {
      if (b instanceof RadioField.RadioFieldBuilder r) r.withOption("First", true).withOption("Second", false);
      else if (b instanceof CheckboxField.CheckboxFieldBuilder c) c.withOption("First", true).withOption("Second", false);
      else if (b instanceof ListField.ListFieldBuilder l) l.withOption("First", true).withOption("Second", false);
    }
    if (b instanceof ControlledTermField.ControlledTermFieldBuilder ct) {
      if (feature.equals("ontology")) ct.withOntologyValueConstraint(URI.create("https://data.bioontology.org/ontologies/DOID"), "DOID", "Disease Ontology");
      if (feature.equals("class")) ct.withClassValueConstraint(URI.create("http://purl.obolibrary.org/obo/DOID_530"), "DOID",
        "eyelid disease", "eyelid disease", ValueType.ONTOLOGY_CLASS);
      if (feature.equals("branch")) ct.withBranchValueConstraint(URI.create("http://purl.obolibrary.org/obo/DOID_4"), "DOID",
        "DOID", "disease", 3);
      if (feature.equals("valueSet")) ct.withValueSetValueConstraint(URI.create("https://example.org/value-sets/colours"),
        "VS", "Colours");
      if (feature.equals("actions")) ct.withOntologyValueConstraint(URI.create("https://data.bioontology.org/ontologies/DOID"), "DOID",
          "Disease Ontology")
        .withValueConstraintsAction(URI.create("http://purl.obolibrary.org/obo/DOID_530"), "DOID", ValueType.ONTOLOGY_CLASS,
          ValueConstraintsActionType.MOVE, URI.create("https://data.bioontology.org/ontologies/DOID"), 0);
    }
    if (feature.equals("required")) b.withRequiredValue(true);
    if (feature.equals("recommended")) b.withRecommendedValue(true);
    if (feature.equals("hidden")) b.withHidden(true);
    if (feature.equals("continuePreviousLine")) set(b, "withContinuePreviousLine");
    if (feature.equals("valueRecommendation")) set(b, "withValueRecommendationEnabled");
    if (feature.startsWith("size-")) {
      Integer width = feature.contains("width") ? 320 : null;
      Integer height = feature.contains("height") ? 180 : null;
      if (b instanceof ImageField.ImageFieldBuilder i) i.withWidth(width).withHeight(height);
      else if (b instanceof YouTubeField.YouTubeFieldBuilder y) y.withWidth(width).withHeight(height);
      else throw new IllegalArgumentException("Unsupported dimensions");
    }
    FieldSchemaArtifact field = b.build();
    return feature.contains("-version-") ? sourced((ControlledTermField) field, feature) : field;
  }

  /** The constraint kind a source-explicit feature names, and the form its version takes. */
  private static String sourceKind(String feature) { return feature.substring(0, feature.indexOf("-version-")); }
  private static String versionForm(String feature) { return feature.substring(feature.indexOf("-version-") + 9); }

  private static Optional<VersionSpec> version(String form) {
    return switch (form) {
      case "absent" -> Optional.empty();
      case "id" -> Optional.of(new VersionSpec(VERSION_ID, Optional.empty(), Optional.empty()));
      case "id-effectiveDate" -> Optional.of(new VersionSpec(VERSION_ID, Optional.of(VERSION_EFFECTIVE_DATE), Optional.empty()));
      case "id-declaredVersion" -> Optional.of(new VersionSpec(VERSION_ID, Optional.empty(), Optional.of(VERSION_DECLARED_VERSION)));
      case "full" -> Optional.of(new VersionSpec(VERSION_ID, Optional.of(VERSION_EFFECTIVE_DATE), Optional.of(VERSION_DECLARED_VERSION)));
      default -> throw new IllegalArgumentException(form);
    };
  }

  /**
   * The field with one constraint of the feature's kind carrying the source-explicit keys. The field
   * builder takes a constraint built in advance only for an ontology, so the field is assembled with
   * its constraints in place of the empty ones the builder gave it.
   */
  private static ControlledTermField sourced(ControlledTermField f, String feature) {
    Optional<URI> iri = Optional.of(sourceKind(feature).equals("valueSet") ? VALUE_SET_SOURCE_IRI : SOURCE_IRI);
    Optional<String> sourceSystem = Optional.of(SOURCE_SYSTEM);
    Optional<VersionSpec> version = version(versionForm(feature));
    var constraints = ControlledTermValueConstraints.builder();
    switch (sourceKind(feature)) {
      case "ontology" -> constraints.withOntologyValueConstraint(new OntologyValueConstraint(
        URI.create("https://data.bioontology.org/ontologies/DOID"), "DOID", "Disease Ontology", Optional.empty(), iri,
        sourceSystem, version));
      case "class" -> constraints.withClassValueConstraint(new ClassValueConstraint(
        URI.create("http://purl.obolibrary.org/obo/DOID_530"), "DOID", "eyelid disease", "eyelid disease",
        ValueType.ONTOLOGY_CLASS, iri, sourceSystem, version));
      case "branch" -> constraints.withBranchValueConstraint(new BranchValueConstraint(
        URI.create("http://purl.obolibrary.org/obo/DOID_4"), "DOID", "DOID", "disease", 3, iri, sourceSystem, version));
      case "valueSet" -> constraints.withValueSetValueConstraint(new ValueSetValueConstraint(
        URI.create("https://example.org/value-sets/colours"), "VS", "Colours", Optional.empty(), iri, sourceSystem, version));
      default -> throw new IllegalArgumentException(feature);
    }
    return ControlledTermField.create(f.internalName(), f.internalDescription(), f.jsonLdContext(), f.jsonLdTypes(),
      f.jsonLdId(), f.name(), f.description(), f.identifier(), f.version(), f.status(), f.previousVersion(),
      f.derivedFrom(), f.isMultiple(), f.minItems(), f.maxItems(), f.propertyUri(), f.createdBy(), f.modifiedBy(),
      f.createdOn(), f.lastUpdatedOn(), f.preferredLabel(), f.alternateLabels(), f.language(), f.fieldUi(),
      Optional.of(constraints.build()), f.annotations());
  }

  private static JsonNode definition(JsonNode json) { return json.has("items") ? json.get("items") : json; }

  /**
   * The document as JSON says it, whatever number types the model held. A bound the reader holds as a
   * long or a decimal is written as the same text as one held as an int, and only the text reaches
   * anyone, so documents are compared as their text reads back.
   */
  private static JsonNode normal(JsonNode json) {
    try {
      return MAPPER.readTree(MAPPER.writeValueAsString(json));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
  private static void expect(JsonNode json, String pointer, String expected) {
    assertEquals(expected, json.at(pointer).asText(), pointer);
  }

  /** Expected values come from the test contract, never from another renderer's output. */
  private static void assertFeature(JsonNode json, String feature) {
    try {
      assertFeatureOf(json, feature);
    } catch (AssertionError e) {
      throw new AssertionError(feature + ": " + e.getMessage() + " in " + json.path("_ui").path("inputType").asText()
        + " " + json.path("_valueConstraints") + " " + json.path("_ui"), e);
    }
  }

  private static void assertFeatureOf(JsonNode json, String feature) {
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
    if (feature.startsWith("length-")) {
      boolean zero = feature.equals("length-zero");
      String min = zero ? "0" : feature.equals("length-min") || feature.equals("length-both") ? "20" : null;
      String max = zero ? "0" : feature.equals("length-max") || feature.equals("length-both") ? "500" : null;
      if (min != null) expect(json, "/_valueConstraints/minLength", min);
      else assertTrue(json.at("/_valueConstraints/minLength").isMissingNode(), "minLength");
      if (max != null) expect(json, "/_valueConstraints/maxLength", max);
      else assertTrue(json.at("/_valueConstraints/maxLength").isMissingNode(), "maxLength");
    }
    if (feature.equals("regex")) expect(json, "/_valueConstraints/regex", "^[A-Z]+$");
    if (feature.equals("numericBounds")) {
      expect(json, "/_valueConstraints/minValue", "1");
      expect(json, "/_valueConstraints/maxValue", "10.5");
    }
    if (feature.equals("decimalPlaces")) expect(json, "/_valueConstraints/decimalPlace", "2");
    if (feature.equals("unit")) expect(json, "/_valueConstraints/unitOfMeasure", "mg");
    if (feature.equals("timePrecision")) {
      expect(json, "/_valueConstraints/temporalType", "xsd:dateTime");
      expect(json, "/_ui/temporalGranularity", "second");
      expect(json, "/_ui/inputTimeFormat", "24h");
      expect(json, "/_ui/timezoneEnabled", "true");
    }
    if (feature.equals("options")) {
      expect(json, "/_valueConstraints/literals/0/label", "First");
      expect(json, "/_valueConstraints/literals/0/selectedByDefault", "true");
      expect(json, "/_valueConstraints/literals/1/label", "Second");
    }
    if (feature.equals("ontology") || feature.equals("actions"))
      expect(json, "/_valueConstraints/ontologies/0/uri", "https://data.bioontology.org/ontologies/DOID");
    if (feature.equals("class")) expect(json, "/_valueConstraints/classes/0/uri", "http://purl.obolibrary.org/obo/DOID_530");
    if (feature.equals("branch")) {
      expect(json, "/_valueConstraints/branches/0/uri", "http://purl.obolibrary.org/obo/DOID_4");
      expect(json, "/_valueConstraints/branches/0/maxDepth", "3");
    }
    if (feature.equals("valueSet")) expect(json, "/_valueConstraints/valueSets/0/uri", "https://example.org/value-sets/colours");
    if (feature.equals("actions")) {
      expect(json, "/_valueConstraints/actions/0/termUri", "http://purl.obolibrary.org/obo/DOID_530");
      expect(json, "/_valueConstraints/actions/0/action", "move");
    }
    if (feature.contains("-version-")) {
      String kind = sourceKind(feature);
      String list = "/_valueConstraints/" + Map.of("ontology", "ontologies", "class", "classes", "branch", "branches",
        "valueSet", "valueSets").get(kind) + "/0";
      expect(json, list + "/uri", Map.of("ontology", "https://data.bioontology.org/ontologies/DOID",
        "class", "http://purl.obolibrary.org/obo/DOID_530", "branch", "http://purl.obolibrary.org/obo/DOID_4",
        "valueSet", "https://example.org/value-sets/colours").get(kind));
      expect(json, list + "/iri", (kind.equals("valueSet") ? VALUE_SET_SOURCE_IRI : SOURCE_IRI).toString());
      expect(json, list + "/sourceSystem", SOURCE_SYSTEM);
      String form = versionForm(feature);
      if (form.equals("absent")) assertTrue(json.at(list + "/version").isMissingNode(), "version");
      else {
        expect(json, list + "/version/id", VERSION_ID);
        if (form.equals("full") || form.equals("id-effectiveDate"))
          expect(json, list + "/version/effectiveDate", VERSION_EFFECTIVE_DATE);
        else assertTrue(json.at(list + "/version/effectiveDate").isMissingNode(), "effectiveDate");
        if (form.equals("full") || form.equals("id-declaredVersion"))
          expect(json, list + "/version/declaredVersion", VERSION_DECLARED_VERSION);
        else assertTrue(json.at(list + "/version/declaredVersion").isMissingNode(), "declaredVersion");
      }
    }
    if (feature.equals("required")) expect(json, "/_valueConstraints/requiredValue", "true");
    if (feature.equals("recommended")) expect(json, "/_valueConstraints/recommendedValue", "true");
    if (feature.equals("hidden")) expect(json, "/_ui/hidden", "true");
    if (feature.equals("continuePreviousLine")) expect(json, "/_ui/continuePreviousLine", "true");
    if (feature.equals("valueRecommendation")) expect(json, "/_ui/valueRecommendationEnabled", "true");
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
    assertEquals(normal(json), normal(JSON.renderFieldSchemaArtifact(new JsonArtifactReader().readFieldSchemaArtifact(json))),
      type + "/" + feature);
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
    // The same placement in an element, which an element's own writers and readers carry, so a field
    // setting kept in a template can still be lost in one.
    var element = ElementSchemaArtifact.builder().withName("Matrix element").withDescription("Matrix element")
      .withJsonLdId(URI.create("https://example.org/template-elements/matrix"))
      .withFieldSchema("property-key", field, "Display label", "Display description").build();
    ObjectNode elementJson = JSON.renderElementSchemaArtifact(element);
    assertFeature(definition(elementJson.at("/properties/property-key")), feature);
    result.set("elementJson", elementJson);
    for (boolean compact : List.of(false, true)) {
      result.put(compact ? "elementCompactYaml" : "elementYaml", YamlSerializer.getYAML(element, compact, true));
      var yaml = new YamlArtifactRenderer(compact).renderElementSchemaArtifact(element);
      var restored = new YamlArtifactReader(compact).readElementSchemaArtifact(yaml);
      ObjectNode restoredJson = JSON.renderElementSchemaArtifact(restored);
      if (!compact && !feature.equals("schemaText")) assertFeature(definition(restoredJson.at("/properties/property-key")), feature);
      result.set(compact ? "elementJsonFromCompactYaml" : "elementJsonFromYaml", restoredJson);
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
      for (String setting : settings(b)) cases.add(renderCase(type, factory, setting));
    });
    ObjectNode fixture = compress(cases);
    fixture.set("inputs", inputs(cases));
    if (Boolean.getBoolean("updateFieldConcordance")) {
      Files.createDirectories(FIXTURE.getParent());
      Files.writeString(FIXTURE, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(fixture) + "\n");
    }
    assertTrue(Files.exists(FIXTURE), "Generate with -DupdateFieldConcordance=true");
    assertEquals(MAPPER.readTree(Files.readString(FIXTURE)), normal(fixture), "Java matrix fixtures are stale; regenerate and refresh TS concordance fixtures");
  }

  private static final ObjectMapper YAML_INPUT = new ObjectMapper(new YAMLFactory());
  /** The version block of the cell pinned to an `id` alone, in its full YAML. */
  private static final Pattern PINNED_VERSION = Pattern.compile("(?m)^( *)version:\n\\1  id: \"" + VERSION_ID + "\"$");

  /**
   * Documents that state a version in a form no writer produces. The meta-schema admits the string
   * "latest", which both of this library's readers take to mean the newest snapshot, as they take an
   * absent version, so a reader given it must produce the documents of the cell with no version. It
   * refuses a version object with no `id`, or with an empty one, and both readers refuse them too.
   * Each input is the cell pinned to an `id` alone with its version stated otherwise, in its JSON and
   * in its full YAML, and is recorded, as a case is, against the cell whose documents a reader must
   * produce from it, or against the pinned cell where every reader must refuse it.
   */
  private static ArrayNode inputs(ArrayNode cases) throws Exception {
    JsonNode pinned = caseOf(cases, "controlledTerm/ontology-version-id");
    JsonNode absent = caseOf(cases, "controlledTerm/ontology-version-absent");
    ArrayNode inputs = MAPPER.createArrayNode();
    inputs.add(input(pinned, absent, "ontology-version-latest", MAPPER.getNodeFactory().textNode("latest"),
      "$1version: \"latest\""));
    inputs.add(input(pinned, null, "ontology-version-empty-object", MAPPER.createObjectNode(), "$1version: {}"));
    inputs.add(input(pinned, null, "ontology-version-empty-id", MAPPER.createObjectNode().put("id", ""),
      "$1version:\n$1  id: \"\""));
    return inputs;
  }

  private static JsonNode caseOf(ArrayNode cases, String id) {
    for (JsonNode c : cases) if (c.path("id").asText().equals(id)) return c;
    throw new IllegalArgumentException(id);
  }

  private static ObjectNode input(JsonNode pinned, JsonNode readsAs, String feature, JsonNode version,
                                  String yamlVersion) throws Exception {
    String id = "controlledTerm/" + feature;
    ObjectNode json = pinned.get("json").deepCopy();
    ((ObjectNode) json.at("/_valueConstraints/ontologies/0")).set("version", version);
    String pinnedYaml = pinned.get("yaml").asText();
    Matcher block = PINNED_VERSION.matcher(pinnedYaml);
    assertTrue(block.find(), id + ": the pinned cell's YAML states its version");
    assertFalse(block.find(), id + ": the pinned cell's YAML states one version");
    String yaml = PINNED_VERSION.matcher(pinnedYaml).replaceFirst(yamlVersion);
    @SuppressWarnings("unchecked")
    LinkedHashMap<String, Object> yamlMap = YAML_INPUT.readValue(yaml, LinkedHashMap.class);
    if (readsAs == null) {
      var fromJson = assertThrows(ArtifactParseException.class, () -> new JsonArtifactReader().readFieldSchemaArtifact(json),
        id + ": the JSON reader refuses it");
      assertTrue(fromJson.getMessage().contains("version"), id + ": " + fromJson.getMessage());
      var fromYaml = assertThrows(ArtifactParseException.class,
        () -> new YamlArtifactReader(false).readFieldSchemaArtifact(yamlMap), id + ": the YAML reader refuses it");
      assertTrue(fromYaml.getMessage().contains("version"), id + ": " + fromYaml.getMessage());
    } else {
      FieldSchemaArtifact fromJson = new JsonArtifactReader().readFieldSchemaArtifact(json);
      assertEquals(normal(readsAs.get("json")), normal(JSON.renderFieldSchemaArtifact(fromJson)), id + ": JSON → model → JSON");
      assertEquals(readsAs.get("yaml").asText(), YamlSerializer.getYAML(fromJson, false, true), id + ": JSON → model → YAML");
      FieldSchemaArtifact fromYaml = new YamlArtifactReader(false).readFieldSchemaArtifact(yamlMap);
      assertEquals(normal(readsAs.get("jsonFromYaml")), normal(JSON.renderFieldSchemaArtifact(fromYaml)),
        id + ": YAML → model → JSON");
      assertEquals(readsAs.get("yaml").asText(), YamlSerializer.getYAML(fromYaml, false, true), id + ": YAML → model → YAML");
    }
    JsonNode against = readsAs != null ? readsAs : pinned;
    ObjectNode row = MAPPER.createObjectNode().put("id", id).put("type", "controlledTerm").put("feature", feature)
      .put("against", against.path("id").asText()).put("refused", readsAs == null);
    row.set("json", changes(against.get("json"), json));
    row.set("yaml", lineChanges(against.get("yaml").asText(), yaml));
    assertEquals(json, applyChanges(against.get("json"), row.get("json")), id + ": the fixture rebuilds the input");
    assertEquals(yaml, applyLineChanges(against.get("yaml").asText(), row.get("yaml")), id + ": the fixture rebuilds the input");
    return row;
  }

  // The fixture records each type's baseline whole, and every other case as what it changes: in its
  // JSON, the keys it sets and removes; in its YAML, the lines it replaces. A document read back from
  // YAML is recorded against the case's own JSON, which it mostly equals. Every case asserts that
  // what it records rebuilds exactly what Java wrote, so a reader of the fixture reads Java's output.

  private static final List<String> JSON_DOCUMENTS = List.of("json", "templateJson", "elementJson");
  private static final List<String> YAML_DOCUMENTS = List.of("yaml", "compactYaml", "templateYaml", "templateCompactYaml",
    "elementYaml", "elementCompactYaml");
  /**
   * A document read back from YAML, and the document of the case it is recorded against, in the order
   * the fixture records them. A {@code Map.of} iterates in an order that differs from one JVM run to
   * the next, so each regeneration reordered every case's keys.
   */
  private static final Map<String, String> READ_BACK = new LinkedHashMap<>();
  static {
    READ_BACK.put("elementJsonFromYaml", "elementJson");
    READ_BACK.put("templateJsonFromYaml", "templateJson");
    READ_BACK.put("jsonFromCompactYaml", "json");
    READ_BACK.put("jsonFromYaml", "json");
    READ_BACK.put("templateJsonFromCompactYaml", "templateJson");
    READ_BACK.put("elementJsonFromCompactYaml", "elementJson");
  }

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
