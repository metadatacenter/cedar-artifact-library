package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ControlledTermFieldInstance;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.LinkFieldInstance;
import org.metadatacenter.artifacts.model.core.ReservedNames;
import org.metadatacenter.artifacts.model.core.ReservedNames.AttributeValueFieldParent;
import org.metadatacenter.artifacts.model.core.SectionBreakField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.TextFieldInstance;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.reader.ArtifactParseException;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.IriReference;
import org.metadatacenter.model.validation.report.CedarValidationReport;
import org.metadatacenter.model.ResourceVersion;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every name, version and IRI rule, at each position the rule governs, as each Java checker judges
 * it; TypeScript reads the same cases.
 *
 * <p>Three rules are each implemented several times over: which names a child or an invented
 * attribute may take, what a version looks like, and what an IRI is. The validator, this library's
 * JSON and YAML readers, the server's {@code ResourceVersion} and the TypeScript library each hold
 * their own copy. When the copies disagree, one component stores what another cannot read, or a
 * read silently changes what was stored.
 *
 * <p>Each case puts one input into one position of an artifact every checker accepts, and asks
 * each checker whether it takes the result unchanged, rewrites it or refuses it. The expected
 * verdict comes from the rule, not from any checker: a name {@link ReservedNames} reserves is
 * refused, a version is three numbers without leading zeros, and an IRI is an absolute RFC 3987
 * IRI kept as spelled. Where the rule has not been decided, the case records the verdicts and
 * expects nothing. A disagreement fails the matrix unless it is listed in {@link #KNOWN}, and a
 * listed one that no longer occurs fails it too.
 */
public class RuleConcordanceMatrixTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final ObjectMapper FIXTURE_WRITER = JsonMapper.builder()
      .enable(JsonWriteFeature.ESCAPE_NON_ASCII).build();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();
  private static final Path FIXTURE = Path.of("src/test/resources/concordance/rule-matrix.json");

  private static final String ACCEPTED = "accepted";
  private static final String REWRITTEN = "rewritten";
  private static final String REFUSED = "refused";
  /** Refused, but with an exception outside the reader's {@link ArtifactParseException} contract. */
  private static final String CRASHED = "crashed";

  private static final String TEMPLATE_ID = "https://example.org/slot/template";
  private static final String ELEMENT_ID = "https://example.org/slot/element";
  private static final String INSTANCE_ID = "https://example.org/slot/instance";
  private static final String ELEMENT_INSTANCE_ID = "https://example.org/slot/element-instance";
  private static final String PROPERTY = "https://example.org/slot/property";
  private static final String CLASS = "https://example.org/slot/class";
  private static final String ONTOLOGY = "https://example.org/slot/ontology";
  private static final String DEFAULT_IRI = "https://example.org/slot/default";
  private static final String DERIVED_FROM = "https://example.org/slot/derived-from";
  private static final String VALUE_IRI = "https://example.org/slot/value";
  private static final String OCCURRENCE = "https://example.org/slot/occurrence";

  /** A kind of artifact the matrix holds one example of, as JSON and as YAML. */
  private record Base(ObjectNode json, LinkedHashMap<String, Object> yaml) {}

  /**
   * Where a rule applies: an artifact of a kind, and the value it holds there, which a case replaces
   * with its input wherever that value occurs as a key or a string.
   */
  private record Position(String rule, String name, String base, String slot, AttributeValueFieldParent parent,
                          String sibling) {}

  private static final List<Position> POSITIONS = List.of(
      new Position("names", "template child key", "template", "slotchild", null, null),
      new Position("names", "element child key", "element", "slotchild", null, null),
      new Position("names", "attribute-value field key in a template", "template", "slotav",
          AttributeValueFieldParent.TEMPLATE, null),
      new Position("names", "attribute-value field key in an element", "element", "slotav",
          AttributeValueFieldParent.ELEMENT, null),
      new Position("names", "attribute name in a template instance", "templateInstance", "slotattr", null,
          "slotchild"),
      new Position("names", "attribute name in a nested element instance", "templateInstance", "slotnestedattr", null,
          "slotelementchild"),
      new Position("names", "attribute name in an element instance", "elementInstance", "slotelementattr", null,
          "slotchild"),
      new Position("versions", "template version", "template", "4.5.6", null, null),
      new Position("versions", "element version", "element", "4.5.7", null, null),
      new Position("versions", "field version", "field", "4.5.8", null, null),
      new Position("versions", "static field version", "staticField", "4.5.9", null, null),
      new Position("iris", "template identifier", "template", TEMPLATE_ID, null, null),
      new Position("iris", "derived from", "template", DERIVED_FROM, null, null),
      new Position("iris", "property IRI", "template", PROPERTY, null, null),
      new Position("iris", "ontology constraint", "template", ONTOLOGY, null, null),
      new Position("iris", "class constraint", "template", CLASS, null, null),
      new Position("iris", "default value", "template", DEFAULT_IRI, null, null),
      new Position("iris", "instance identifier", "templateInstance", INSTANCE_ID, null, null),
      new Position("iris", "based on", "templateInstance", TEMPLATE_ID, null, null),
      new Position("iris", "element occurrence identifier", "templateInstance", OCCURRENCE, null, null),
      new Position("iris", "field value", "templateInstance", VALUE_IRI, null, null));

  /** Inputs that stand for another name in the same position, resolved when a case is built. */
  private static final String SIBLING = "<sibling child key>";
  private static final String DUPLICATE = "<another attribute in the same field>";

  private static final List<String> NAMES = List.of("ordinary", "Ordinary Name", "Gr\u00f6\u00dfe", "a/b", "@foo", "@id",
      "@context", "@type", "@value", "@language", "schema:name", "schema:description", "schema:identifier",
      "schema:isBasedOn", "pav:derivedFrom", "pav:createdOn", "oslc:modifiedBy", "rdfs:label", "skos:prefLabel",
      "skos:notation", "skos:altLabel", "_annotations", "__proto__", "constructor", "prototype", "name",
      "description", "id", "type", "children", "isBasedOn", "derivedFrom", "annotations", "createdOn", "", " ",
      SIBLING, DUPLICATE);

  private static final List<String> VERSIONS = List.of("1.0.0", "0.0.1", "10.20.30", "0.0.0", "01.2.3", "1.02.3",
      "1.0", "1", "1.2.3.4", "1.2.3-rc1", "v1.0.0", " 1.0.0", "1.0.0 ", "", "banana", "2147483647.0.0",
      "2147483648.0.0", "99999999999.0.0");

  private static final List<String> IRIS = List.of("https://example.org/plain", "https://example.org/Niger\u00a0NER",
      "https://example.org/Niger%C2%A0NER", "https://example.org/caf\u00e9", "https://example.org/\u2003term",
      "https://example.org/\ud83d\ude00", "https://example.org/?q=\ue000", "urn:example:Niger\u00a0NER", "",
      "relative/path", "https://example.org/a b", "https://example.org/a\tb", "https://example.org/\u0085",
      "https://example.org/%xx", "https://example.org/a#b#c", "://example.org/a", "https://example.org/\ud800",
      "https://example.org/\uffff", "https://example.org/\ue000", "https://example.org/#\ue000");

  /** One checker's verdict on one case, where it differs from the rule's or, with no rule, from the JSON reader's. */
  private record Disagreement(Position position, String input, String checker, String verdict) {}

  /** A class of disagreement that is open on purpose, with why. */
  private record Known(String name, String reason, Predicate<Disagreement> matches) {}

  private static final Set<String> RELATIVE = Set.of("", "relative/path");
  private static final Set<String> SPACE_SEPARATORS = Set.of("https://example.org/Niger\u00a0NER",
      "https://example.org/\u2003term", "urn:example:Niger\u00a0NER");

  /**
   * The disagreements that are decisions still to be made. The matrix fails on a disagreement no
   * entry matches and on an entry that matches nothing, so each one goes when it is decided.
   */
  private static final List<Known> KNOWN = List.of(
      new Known("relative references", "Every reader, and the format the validator applies, takes an empty or "
          + "relative reference in most identifier positions, which a stored artifact should not hold. Refusing "
          + "one needs a count of how many production holds first.",
          d -> d.position().rule().equals("iris") && RELATIVE.contains(d.input())
              && !d.verdict().equals(REFUSED)),
      new Known("space separators outside a field value", "RFC 3987 allows a no-break space and other space "
          + "separators, and a field value keeps one as spelled. Everywhere else the model holds a java.net.URI, "
          + "which cannot, so the readers refuse one, as the validator's absolute-IRI check does in three "
          + "positions and its format check does not elsewhere. Accepting one means keeping each spelling in the "
          + "model; refusing one everywhere means a stricter validator.",
          d -> d.position().rule().equals("iris") && SPACE_SEPARATORS.contains(d.input())
              && d.verdict().equals(REFUSED)),
      new Known("unruled versions", "ResourceVersion, which the server versions an artifact with, refuses 0.0.0 "
          + "and a leading zero. The readers accept both, rewriting a leading zero away, and the validator passes "
          + "both. One rule needs a count of how many production holds.",
          d -> d.position().rule().equals("versions") && Set.of("0.0.0", "01.2.3", "1.02.3").contains(d.input())),
      new Known("versions past an int", "The meta-schema's pattern cannot bound a part to an int, as the readers "
          + "do; a pattern that refuses a leading zero can bound its length, so this goes with that rule.",
          d -> d.position().rule().equals("versions") && d.checker().equals("validator")
              && Set.of("2147483648.0.0", "99999999999.0.0").contains(d.input())),
      new Known("whitespace keys", "Every checker takes a child key of spaces alone, while the validator refuses "
          + "an attribute name of spaces alone. Refusing the key needs a count of how many production holds.",
          d -> d.position().rule().equals("names") && d.position().base().matches("template|element")
              && d.input().equals(" ")));

  private static Map<String, Base> bases() {
    // Every child states its property, as the editors' children do.
    var nestedText = TextField.builder().withName("Nested text")
        .withJsonLdId(URI.create("https://example.org/fields/nested-text"))
        .withPropertyUri(URI.create(property("slotelementchild"))).build();
    var nestedAttributes = AttributeValueField.builder().withName("Nested attributes")
        .withJsonLdId(URI.create("https://example.org/fields/nested-attributes")).build();
    var group = ElementSchemaArtifact.builder().withName("Group")
        .withJsonLdId(URI.create("https://example.org/elements/group")).withPropertyUri(URI.create(property("group")))
        .withFieldSchema("slotelementchild", nestedText).withFieldSchema("slotelementav", nestedAttributes).build();
    var text = TextField.builder().withName("Text").withJsonLdId(URI.create("https://example.org/fields/text"))
        .withPropertyUri(URI.create(PROPERTY)).build();
    var attributes = AttributeValueField.builder().withName("Attributes")
        .withJsonLdId(URI.create("https://example.org/fields/attributes")).build();
    var term = ControlledTermField.builder().withName("Term").withJsonLdId(URI.create("https://example.org/fields/term"))
        .withPropertyUri(URI.create(property("term")))
        .withOntologyValueConstraint(URI.create(ONTOLOGY), "SLOT", "Slot ontology")
        .withClassValueConstraint(URI.create(CLASS), "SLOT", "Slot class", "Slot class", ValueType.ONTOLOGY_CLASS)
        .build();
    var link = LinkField.builder().withName("Link").withJsonLdId(URI.create("https://example.org/fields/link"))
        .withPropertyUri(URI.create(property("link")))
        .withDefaultValue(URI.create(DEFAULT_IRI)).build();
    var template = TemplateSchemaArtifact.builder().withName("Rules").withJsonLdId(URI.create(TEMPLATE_ID))
        .withVersion(Version.fromString("4.5.6")).withDerivedFrom(URI.create(DERIVED_FROM))
        .withFieldSchema("slotchild", text).withElementSchema("group", group).withFieldSchema("slotav", attributes)
        .withFieldSchema("term", term).withFieldSchema("link", link).build();

    var element = ElementSchemaArtifact.builder().withName("Rules element").withJsonLdId(URI.create(ELEMENT_ID))
        .withVersion(Version.fromString("4.5.7"))
        .withFieldSchema("slotchild", TextField.builder().withName("Element text")
            .withJsonLdId(URI.create("https://example.org/fields/element-text"))
            .withPropertyUri(URI.create(property("element-text"))).build())
        .withFieldSchema("slotav", AttributeValueField.builder().withName("Element attributes")
            .withJsonLdId(URI.create("https://example.org/fields/element-attributes")).build())
        .build();
    var field = TextField.builder().withName("Rules field").withJsonLdId(URI.create("https://example.org/slot/field"))
        .withVersion(Version.fromString("4.5.8")).build();
    var staticField = SectionBreakField.builder().withName("Rules section")
        .withJsonLdId(URI.create("https://example.org/slot/static-field")).withVersion(Version.fromString("4.5.9"))
        .build();

    var nested = ElementInstanceArtifact.builder().withJsonLdId(URI.create(OCCURRENCE))
        .withJsonLdContextEntry("slotelementchild", URI.create(property("slotelementchild")))
        .withSingleInstanceFieldInstance("slotelementchild", TextFieldInstance.builder().withValue("nested").build())
        .withAttributeValueFieldGroup("slotelementav", attributes("slotnestedattr")).build();
    var instance = TemplateInstanceArtifact.builder().withName("Rules instance").withJsonLdId(URI.create(INSTANCE_ID))
        .withIsBasedOn(URI.create(TEMPLATE_ID)).withJsonLdContextEntry("slotchild", URI.create(PROPERTY))
        .withJsonLdContextEntry("group", URI.create(property("group")))
        .withJsonLdContextEntry("term", URI.create(property("term")))
        .withJsonLdContextEntry("link", URI.create(property("link")))
        .withSingleInstanceFieldInstance("slotchild", TextFieldInstance.builder().withValue("text").build())
        .withSingleInstanceElementInstance("group", nested)
        .withAttributeValueFieldGroup("slotav", attributes("slotattr", "slotattrtwo"))
        .withSingleInstanceFieldInstance("term", ControlledTermFieldInstance.builder().build())
        .withSingleInstanceFieldInstance("link", LinkFieldInstance.builder().withValue(URI.create(VALUE_IRI)).build())
        .build();
    var elementInstance = ElementInstanceArtifact.builder().withJsonLdId(URI.create(ELEMENT_INSTANCE_ID))
        .withJsonLdContextEntry("slotchild", URI.create(property("element-text")))
        .withSingleInstanceFieldInstance("slotchild", TextFieldInstance.builder().withValue("text").build())
        .withAttributeValueFieldGroup("slotav", attributes("slotelementattr")).build();

    var yaml = new YamlArtifactRenderer(false);
    Map<String, Base> bases = new LinkedHashMap<>();
    bases.put("template", new Base(JSON.renderTemplateSchemaArtifact(template), yaml.renderTemplateSchemaArtifact(template)));
    bases.put("element", new Base(JSON.renderElementSchemaArtifact(element), yaml.renderElementSchemaArtifact(element)));
    bases.put("field", new Base(JSON.renderFieldSchemaArtifact(field), yaml.renderFieldSchemaArtifact(field)));
    bases.put("staticField", new Base(JSON.renderFieldSchemaArtifact(staticField),
        yaml.renderFieldSchemaArtifact(staticField)));
    bases.put("templateInstance", new Base(JSON.renderTemplateInstanceArtifact(instance),
        yaml.renderTemplateInstanceArtifact(instance)));
    bases.put("elementInstance", new Base(JSON.renderElementInstanceArtifact(elementInstance),
        yaml.renderElementInstanceArtifact(elementInstance)));
    return bases;
  }

  private static String property(String child) {
    return "https://example.org/properties/" + child;
  }

  private static LinkedHashMap<String, FieldInstanceArtifact> attributes(String... names) {
    var attributes = new LinkedHashMap<String, FieldInstanceArtifact>();
    for (String name : names) attributes.put(name, TextFieldInstance.builder().withValue("value of " + name).build());
    return attributes;
  }

  /** A copy of a JSON tree with every key and string equal to {@code from} replaced by {@code to}. */
  private static JsonNode replaceJson(JsonNode node, String from, String to) {
    if (node.isTextual()) return node.asText().equals(from) ? TextNode.valueOf(to) : node;
    if (node.isArray()) {
      ArrayNode copy = MAPPER.createArrayNode();
      node.forEach(item -> copy.add(replaceJson(item, from, to)));
      return copy;
    }
    if (node.isObject()) {
      ObjectNode copy = MAPPER.createObjectNode();
      node.fields().forEachRemaining(entry ->
          copy.set(entry.getKey().equals(from) ? to : entry.getKey(), replaceJson(entry.getValue(), from, to)));
      return copy;
    }
    return node;
  }

  /** The same replacement in a YAML tree. */
  private static Object replaceYaml(Object node, String from, String to) {
    if (node instanceof String text) return text.equals(from) ? to : text;
    if (node instanceof List<?> list) {
      List<Object> copy = new ArrayList<>();
      list.forEach(item -> copy.add(replaceYaml(item, from, to)));
      return copy;
    }
    if (node instanceof Map<?, ?> map) {
      LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
      map.forEach((key, value) -> copy.put(key.equals(from) ? to : (String) key, replaceYaml(value, from, to)));
      return copy;
    }
    return node;
  }

  private static boolean holds(JsonNode node, String value) {
    if (node.isTextual()) return node.asText().equals(value);
    if (node.isObject()) {
      var fields = node.fields();
      while (fields.hasNext()) {
        var entry = fields.next();
        if (entry.getKey().equals(value) || holds(entry.getValue(), value)) return true;
      }
      return false;
    }
    if (node.isArray()) for (JsonNode item : node) if (holds(item, value)) return true;
    return false;
  }

  private static String validator(String base, ObjectNode document, Map<String, Base> bases) throws Exception {
    var report = report(base, document, bases);
    return report.getValidationStatus().equals(CedarValidationReport.IS_VALID) ? ACCEPTED : REFUSED;
  }

  private static org.metadatacenter.model.validation.report.ValidationReport report(String base, ObjectNode document,
                                                                                  Map<String, Base> bases)
      throws Exception {
    var validator = new CedarValidator();
    return switch (base) {
      case "template" -> validator.validateTemplate(document);
      case "element" -> validator.validateTemplateElement(document);
      case "field", "staticField" -> validator.validateTemplateField(document);
      case "templateInstance" -> validator.validateTemplateInstance(document, bases.get("template").json());
      case "elementInstance" -> validator.validateElementInstance(document, bases.get("element").json());
      default -> throw new IllegalArgumentException(base);
    };
  }

  private static String jsonReader(String base, ObjectNode document) {
    var reader = new JsonArtifactReader();
    ObjectNode written;
    try {
      written = switch (base) {
        case "template" -> JSON.renderTemplateSchemaArtifact(reader.readTemplateSchemaArtifact(document.deepCopy()));
        case "element" -> JSON.renderElementSchemaArtifact(reader.readElementSchemaArtifact(document.deepCopy()));
        case "field", "staticField" -> JSON.renderFieldSchemaArtifact(reader.readFieldSchemaArtifact(document.deepCopy()));
        case "templateInstance" ->
            JSON.renderTemplateInstanceArtifact(reader.readTemplateInstanceArtifact(document.deepCopy()));
        case "elementInstance" ->
            JSON.renderElementInstanceArtifact(reader.readElementInstanceArtifact(document.deepCopy()));
        default -> throw new IllegalArgumentException(base);
      };
    } catch (ArtifactParseException | IllegalArgumentException | IllegalStateException refused) {
      return REFUSED;
    } catch (RuntimeException crashed) {
      return CRASHED;
    }
    return written.equals(document) ? ACCEPTED : REWRITTEN;
  }

  @SuppressWarnings("unchecked")
  private static String yamlReader(String base, LinkedHashMap<String, Object> document) {
    var reader = new YamlArtifactReader(false);
    var renderer = new YamlArtifactRenderer(false);
    LinkedHashMap<String, Object> written;
    try {
      written = switch (base) {
        case "template" -> renderer.renderTemplateSchemaArtifact(reader.readTemplateSchemaArtifact(document));
        case "element" -> renderer.renderElementSchemaArtifact(reader.readElementSchemaArtifact(document));
        case "field", "staticField" -> renderer.renderFieldSchemaArtifact(reader.readFieldSchemaArtifact(document));
        case "templateInstance" -> renderer.renderTemplateInstanceArtifact(reader.readTemplateInstanceArtifact(document));
        case "elementInstance" -> renderer.renderElementInstanceArtifact(reader.readElementInstanceArtifact(document));
        default -> throw new IllegalArgumentException(base);
      };
    } catch (ArtifactParseException | IllegalArgumentException | IllegalStateException refused) {
      return REFUSED;
    } catch (RuntimeException crashed) {
      return CRASHED;
    }
    return MAPPER.valueToTree(written).equals(MAPPER.valueToTree(document)) ? ACCEPTED : REWRITTEN;
  }

  /** The verdict the rule gives, or null where the rule has not been decided. */
  private static String expected(Position position, String input) {
    return switch (position.rule()) {
      case "names" -> {
        if (input.isBlank() || input.equals(SIBLING) || input.equals(DUPLICATE)) yield REFUSED;
        boolean reserved = position.parent() == null ? ReservedNames.isReservedName(input)
            : ReservedNames.isReservedAttributeValueFieldName(input, position.parent());
        yield reserved ? REFUSED : ACCEPTED;
      }
      case "versions" -> {
        if (!input.matches("\\d+\\.\\d+\\.\\d+")) yield REFUSED;
        // Leading zeros and 0.0.0 are what the checkers disagree on, and nobody has ruled.
        if (input.matches(".*\\b0\\d.*") || input.equals("0.0.0")) yield null;
        boolean fits = true;
        for (String part : input.split("\\.")) fits &= part.length() < 10 || Long.parseLong(part) <= Integer.MAX_VALUE;
        yield fits ? ACCEPTED : REFUSED;
      }
      case "iris" -> {
        try {
          yield !input.isEmpty() && IriReference.toUri(input).isAbsolute() ? ACCEPTED : REFUSED;
        } catch (Exception notAnIri) {
          yield REFUSED;
        }
      }
      default -> throw new IllegalArgumentException(position.rule());
    };
  }

  private static String resolve(Position position, String input) {
    if (input.equals(SIBLING)) return position.sibling();
    if (input.equals(DUPLICATE)) return "slotattrtwo";
    return input;
  }

  private static List<String> inputs(Position position) {
    return switch (position.rule()) {
      case "names" -> NAMES.stream().filter(name -> !(name.equals(SIBLING) && position.sibling() == null))
          .filter(name -> !(name.equals(DUPLICATE) && !position.slot().equals("slotattr"))).toList();
      case "versions" -> VERSIONS;
      case "iris" -> IRIS;
      default -> throw new IllegalArgumentException(position.rule());
    };
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ruleConcordanceMatrix() throws Exception {
    Map<String, Base> bases = bases();
    for (var entry : bases.entrySet()) {
      Base base = entry.getValue();
      assertEquals(ACCEPTED, validator(entry.getKey(), base.json(), bases), entry.getKey() + ": the validator: "
          + MAPPER.valueToTree(report(entry.getKey(), base.json(), bases).getErrors()));
      assertEquals(ACCEPTED, jsonReader(entry.getKey(), base.json()), entry.getKey() + ": the JSON reader");
      assertEquals(ACCEPTED, yamlReader(entry.getKey(), base.yaml()), entry.getKey() + ": the YAML reader");
    }

    ArrayNode positions = MAPPER.createArrayNode();
    Map<String, Disagreement> disagreements = new TreeMap<>();
    for (Position position : POSITIONS) {
      ArrayNode cases = positions.addObject().put("rule", position.rule()).put("name", position.name())
          .put("base", position.base()).put("slot", position.slot()).putArray("cases");
      Base base = bases.get(position.base());
      assertTrue(holds(base.json(), position.slot()), position.name() + ": the JSON holds the slot");
      boolean inYaml = holds(MAPPER.valueToTree(base.yaml()), position.slot());
      for (String input : inputs(position)) {
        String value = resolve(position, input);
        String id = position.name() + " / " + FIXTURE_WRITER.writeValueAsString(input);
        ObjectNode json = (ObjectNode) replaceJson(base.json(), position.slot(), value);
        var yaml = (LinkedHashMap<String, Object>) replaceYaml(base.yaml(), position.slot(), value);

        ObjectNode verdicts = MAPPER.createObjectNode();
        verdicts.put("validator", validator(position.base(), json, bases));
        // A reader that has no template cannot tell an attribute from the sibling it collides with,
        // and a YAML mapping cannot hold one key twice, so those lanes have nothing to say.
        if (!input.equals(SIBLING)) verdicts.put("json", jsonReader(position.base(), json));
        if (inYaml && !input.equals(DUPLICATE)) verdicts.put("yaml", yamlReader(position.base(), yaml));
        if (position.rule().equals("versions")) {
          verdicts.put("resourceVersion", ResourceVersion.forValueWithValidation(value).isValid() ? ACCEPTED : REFUSED);
        }

        String expected = expected(position, input);
        ObjectNode result = cases.addObject().put("input", input);
        // An input standing for another name says which.
        if (!value.equals(input)) result.put("value", value);
        if (expected == null) result.putNull("expected"); else result.put("expected", expected);
        result.set("java", verdicts);

        verdicts.fields().forEachRemaining(verdict -> {
          boolean agrees = expected == null ? verdict.getValue().asText().equals(verdicts.path("json").asText())
              : verdict.getValue().asText().equals(expected);
          if (!agrees) disagreements.put(id + " / " + verdict.getKey(),
              new Disagreement(position, input, verdict.getKey(), verdict.getValue().asText()));
        });
      }
    }

    ObjectNode fixture = MAPPER.createObjectNode();
    ObjectNode fixtureBases = fixture.putObject("bases");
    bases.forEach((name, base) -> fixtureBases.putObject(name).<ObjectNode>set("json", base.json())
        .set("yaml", MAPPER.valueToTree(base.yaml())));
    fixture.set("positions", positions);

    List<String> opened = disagreements.entrySet().stream()
        .filter(entry -> KNOWN.stream().noneMatch(known -> known.matches().test(entry.getValue())))
        .map(entry -> entry.getKey() + ": " + entry.getValue().verdict()).toList();
    List<String> closed = KNOWN.stream()
        .filter(known -> disagreements.values().stream().noneMatch(known.matches())).map(Known::name).toList();
    Files.writeString(Path.of("target/rule-matrix-disagreements.txt"), String.join("\n", opened) + "\n");
    assertTrue(opened.isEmpty() && closed.isEmpty(), "Unlisted disagreements:\n  " + String.join("\n  ", opened)
        + "\nListed disagreements that no longer occur:\n  " + String.join("\n  ", closed));

    if (Boolean.getBoolean("updateRuleConcordance")) {
      Files.createDirectories(FIXTURE.getParent());
      Files.writeString(FIXTURE, FIXTURE_WRITER.writerWithDefaultPrettyPrinter().writeValueAsString(fixture) + "\n");
    }
    assertTrue(Files.exists(FIXTURE), "Generate with -DupdateRuleConcordance=true");
    assertEquals(MAPPER.readTree(Files.readString(FIXTURE)), fixture,
        "Java matrix fixtures are stale; regenerate and refresh the TypeScript concordance fixture");
  }
}
