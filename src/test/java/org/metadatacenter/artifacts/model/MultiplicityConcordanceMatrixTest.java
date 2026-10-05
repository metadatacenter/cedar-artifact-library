package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifactBuilder;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.ValidationHelper;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.InstanceInflater;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.report.CedarValidationReport;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every kind of repeated child, with every combination of stated bounds, in a template and in an
 * element, written by both writers and filled by the inflater; TypeScript reads these exact outputs.
 *
 * <p>The bounds a child states, and the ones it takes when it states none, were decided separately
 * by each writer and each inflater, and they disagreed. The TypeScript inflater gave a missing
 * repeated child an empty list where this one fills it to its starting count, so an instance it
 * inflated failed its own template. This library wrote an attribute-value field's bounds as 0 and no
 * maximum whatever the author stated, and TypeScript wrote them. TypeScript also raised a maximum
 * below the minimum to the minimum on the way out, where this library refuses such a child, and
 * that turned CEDAR's maximum of 0, which means no upper bound, into a limit of the minimum.
 *
 * <p>The expectations here are the model's, stated independently of any writer: a child marked
 * multiple starts with one occurrence, one multiple by nature with none, and a stated minimum
 * decides either way. The JSON states that bound and any stated maximum, the YAML carries them both
 * through, an inflated instance holds that many occurrences, and it validates against its template,
 * except where it cannot: an attribute-value field's attributes need names, which no inflater can
 * invent.
 */
public class MultiplicityConcordanceMatrixTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();
  private static final Path FIXTURE = Path.of("src/test/resources/concordance/multiplicity-matrix.json");
  private static final URI TEMPLATE_ID = URI.create("https://example.org/templates/multiplicity");
  private static final String CHILD = "child";
  private static final String GROUP = "group";

  /** A kind of child: whether it is a list by its own nature, and how to build one. */
  private record Kind(String name, boolean byNature, boolean canBeSingle) {
    ChildSchemaArtifact build(boolean multiple, Optional<Integer> min, Optional<Integer> max) {
      if (name.equals("element")) {
        var element = ElementSchemaArtifact.builder().withName("Child")
            .withJsonLdId(URI.create("https://example.org/elements/child"))
            .withFieldSchema("text", TextField.builder().withName("Text").build())
            .withIsMultiple(multiple);
        min.ifPresent(element::withMinItems);
        max.ifPresent(element::withMaxItems);
        return element.build();
      }
      FieldSchemaArtifactBuilder<?> field = switch (name) {
        case "text" -> TextField.builder();
        case "controlledTerm" -> ControlledTermField.builder();
        case "link" -> LinkField.builder();
        case "checkbox" -> CheckboxField.builder();
        case "multipleList" -> ListField.builder().withMultipleChoice(true);
        case "attributeValue" -> AttributeValueField.builder();
        default -> throw new IllegalArgumentException(name);
      };
      field.withName("Child").withJsonLdId(URI.create("https://example.org/fields/child"));
      if (!byNature) field.withIsMultiple(multiple);
      min.ifPresent(field::withMinItems);
      max.ifPresent(field::withMaxItems);
      return (ChildSchemaArtifact) field.build();
    }
  }

  private static final List<Kind> KINDS = List.of(
      new Kind("text", false, true), new Kind("controlledTerm", false, true), new Kind("link", false, true),
      new Kind("element", false, true), new Kind("checkbox", true, false), new Kind("multipleList", true, false),
      new Kind("attributeValue", true, false));

  private static final List<String> CONTAINERS = List.of("template", "element");

  private static TemplateSchemaArtifact template(ChildSchemaArtifact child, String container) {
    var template = TemplateSchemaArtifact.builder().withName("Matrix").withJsonLdId(TEMPLATE_ID);
    if (container.equals("template")) {
      add(template, CHILD, child);
    } else {
      var group = ElementSchemaArtifact.builder().withName("Group")
          .withJsonLdId(URI.create("https://example.org/elements/group"));
      if (child instanceof ElementSchemaArtifact element) group.withElementSchema(CHILD, element);
      else group.withFieldSchema(CHILD, (org.metadatacenter.artifacts.model.core.FieldSchemaArtifact) child);
      template.withElementSchema(GROUP, group.build());
    }
    return template.build();
  }

  private static void add(TemplateSchemaArtifact.Builder template, String key, ChildSchemaArtifact child) {
    if (child instanceof ElementSchemaArtifact element) template.withElementSchema(key, element);
    else template.withFieldSchema(key, (org.metadatacenter.artifacts.model.core.FieldSchemaArtifact) child);
  }

  /** The child's definition in a template's JSON Schema. */
  private static JsonNode schemaAt(JsonNode template, String container) {
    JsonNode properties = template.path("properties");
    return container.equals("template") ? properties.path(CHILD) : properties.path(GROUP).path("properties").path(CHILD);
  }

  /** The child's slot in an instance. */
  private static JsonNode slotAt(JsonNode instance, String container) {
    return container.equals("template") ? instance.path(CHILD) : instance.path(GROUP).path(CHILD);
  }

  /** The bounds a case gives its child, or null for a single child: the only thing a case varies. */
  private static JsonNode jsonBounds(JsonNode schema) {
    if (!schema.path("type").asText().equals("array")) return MAPPER.nullNode();
    ObjectNode bounds = MAPPER.createObjectNode();
    for (String key : List.of("minItems", "maxItems")) {
      if (schema.has(key)) bounds.set(key, schema.get(key));
    }
    return bounds;
  }

  /** The child's definition, unwrapped from the list a repeated child is written in. */
  private static JsonNode definition(JsonNode schema) {
    return schema.path("type").asText().equals("array") ? schema.get("items") : schema;
  }

  /**
   * A template as a case writes it: its kind's base, with the child wrapped in a list stating the
   * case's bounds, or left alone for a single child. Every case asserts that this is exactly what
   * Java writes, so a reader given it is given Java's own output.
   */
  private static ObjectNode withBounds(JsonNode base, String container, JsonNode bounds) {
    ObjectNode template = base.deepCopy();
    ObjectNode properties = (ObjectNode) (container.equals("template") ? template.path("properties")
        : template.path("properties").path(GROUP).path("properties"));
    JsonNode child = properties.get(CHILD);
    if (!bounds.isNull()) {
      ObjectNode wrapper = MAPPER.createObjectNode().put("type", "array");
      wrapper.setAll((ObjectNode) bounds);
      wrapper.set("items", child);
      properties.set(CHILD, wrapper);
    }
    return template;
  }

  /** The template with its child unwrapped, which every case of the kind is built from. */
  private static JsonNode base(JsonNode templateJson, String container) {
    ObjectNode base = templateJson.deepCopy();
    ObjectNode properties = (ObjectNode) (container.equals("template") ? base.path("properties")
        : base.path("properties").path(GROUP).path("properties"));
    properties.set(CHILD, definition(properties.get(CHILD)));
    return base;
  }

  private static ObjectNode renderCase(Kind kind, boolean multiple, Optional<Integer> min, Optional<Integer> max,
                                       String container, ObjectNode bases, JsonNode sparseJson) throws Exception {
    String id = kind.name() + " / " + (multiple ? "min " + min.map(String::valueOf).orElse("absent") + ", max "
        + max.map(String::valueOf).orElse("absent") : "single") + " / in " + container;
    ChildSchemaArtifact child = kind.build(multiple, min, max);
    TemplateSchemaArtifact template = template(child, container);
    ObjectNode templateJson = JSON.renderTemplateSchemaArtifact(template);
    int starting = min.orElse(kind.byNature() ? 0 : 1);

    JsonNode schema = schemaAt(templateJson, container);
    if (multiple) {
      assertEquals("array", schema.path("type").asText(), id);
      assertEquals(starting, schema.path("minItems").asInt(-1), id + ": the lower bound");
      assertEquals(max.isPresent(), schema.has("maxItems"), id + ": whether there is an upper bound");
      max.ifPresent(m -> assertEquals(m, schema.path("maxItems").asInt(), id + ": the upper bound"));
    } else {
      assertEquals("object", schema.path("type").asText(), id);
    }
    assertEquals(templateJson, JSON.renderTemplateSchemaArtifact(
        new JsonArtifactReader().readTemplateSchemaArtifact(templateJson)), id + ": JSON → model → JSON");

    String baseKey = kind.name() + " in " + container;
    if (!bases.has(baseKey)) bases.set(baseKey, base(templateJson, container));
    JsonNode bounds = jsonBounds(schema);
    assertEquals(templateJson, withBounds(bases.get(baseKey), container, bounds),
        id + ": the case is its kind's base with its bounds applied");

    LinkedHashMap<String, Object> yaml = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(template);
    ObjectNode fromYaml = JSON.renderTemplateSchemaArtifact(new YamlArtifactReader(false).readTemplateSchemaArtifact(yaml));
    assertEquals(bounds, jsonBounds(schemaAt(fromYaml, container)), id + ": the bounds survive YAML");

    TemplateInstanceArtifact sparse = new JsonArtifactReader().readTemplateInstanceArtifact((ObjectNode) sparseJson);
    ObjectNode inflated = JSON.renderTemplateInstanceArtifact(InstanceInflater.inflate(template, sparse));
    JsonNode slot = slotAt(inflated, container);
    boolean attributeValue = kind.name().equals("attributeValue");
    // Attributes need names, so an attribute-value field starts empty whatever its bound.
    int occurrences = attributeValue ? 0 : starting;
    if (multiple) {
      assertTrue(slot.isArray(), id + ": a repeated child's slot is a list: " + slot);
      assertEquals(occurrences, slot.size(), id + ": the inflated occurrences");
    } else {
      assertTrue(slot.isObject(), id + ": a single child's slot is one value: " + slot);
    }
    // A maximum of 0 is CEDAR's "no upper bound", which the model accepts whatever the minimum, but
    // the validator reads it as JSON Schema does, as "no items". Which is right is open, so those
    // cases record no verdict rather than assert either reading.
    if (!max.equals(Optional.of(ValidationHelper.UNBOUNDED_MAX_ITEMS))) {
      String verdict = new CedarValidator().validateTemplateInstance(inflated, templateJson).getValidationStatus();
      boolean valid = !(attributeValue && starting > 0);
      assertEquals(valid ? CedarValidationReport.IS_VALID : CedarValidationReport.IS_INVALID, verdict,
          id + ": the inflated instance against its template");
    }

    ObjectNode result = MAPPER.createObjectNode().put("id", id).put("base", baseKey).put("container", container)
        .put("refused", false);
    result.set("bounds", bounds);
    result.put("yaml", YamlSerializer.getYAML(template, false, true));
    if (multiple) result.put("inflatedOccurrences", occurrences); else result.putNull("inflatedOccurrences");
    return result;
  }

  /**
   * A child whose maximum is below its minimum, which no template can satisfy. The model refuses one,
   * so the case is its kind's base with those bounds applied, and the reader must refuse that too.
   */
  private static ObjectNode refusedCase(Kind kind, int min, String container, ObjectNode bases) {
    String id = kind.name() + " / min " + min + ", max " + (min - 1) + " / in " + container;
    assertThrows(IllegalStateException.class, () -> kind.build(true, Optional.of(min), Optional.of(min - 1)), id);
    String baseKey = kind.name() + " in " + container;
    ObjectNode bounds = MAPPER.createObjectNode().put("minItems", min).put("maxItems", min - 1);
    ObjectNode templateJson = withBounds(bases.get(baseKey), container, bounds);
    assertThrows(RuntimeException.class, () -> new JsonArtifactReader().readTemplateSchemaArtifact(templateJson),
        id + ": the reader refuses it");
    ObjectNode result = MAPPER.createObjectNode().put("id", id).put("base", baseKey).put("container", container)
        .put("refused", true);
    result.set("bounds", bounds);
    return result;
  }

  @Test
  public void multiplicityConcordanceMatrix() throws Exception {
    ObjectNode bases = MAPPER.createObjectNode();
    ObjectNode sparse = JSON.renderTemplateInstanceArtifact(TemplateInstanceArtifact.builder()
        .withName("Matrix instance").withIsBasedOn(TEMPLATE_ID).build());
    ArrayNode cases = MAPPER.createArrayNode();
    for (Kind kind : KINDS) {
      for (String container : CONTAINERS) {
        if (kind.canBeSingle()) {
          cases.add(renderCase(kind, false, Optional.empty(), Optional.empty(), container, bases, sparse));
        }
        for (Optional<Integer> min : List.of(Optional.<Integer>empty(), Optional.of(0), Optional.of(1), Optional.of(2))) {
          int starting = min.orElse(kind.byNature() ? 0 : 1);
          cases.add(renderCase(kind, true, min, Optional.empty(), container, bases, sparse));
          cases.add(renderCase(kind, true, min, Optional.of(ValidationHelper.UNBOUNDED_MAX_ITEMS), container, bases,
              sparse));
          if (starting > 0) {
            cases.add(renderCase(kind, true, min, Optional.of(starting), container, bases, sparse));
          }
          cases.add(renderCase(kind, true, min, Optional.of(starting + 3), container, bases, sparse));
          // A maximum of 1 below a minimum of 2; below a minimum of 1 the maximum would be 0.
          if (min.isPresent() && min.get() > 1) {
            cases.add(refusedCase(kind, min.get(), container, bases));
          }
        }
      }
    }
    ObjectNode fixture = MAPPER.createObjectNode();
    fixture.set("bases", bases);
    fixture.set("sparseInstance", sparse);
    fixture.set("cases", cases);
    if (Boolean.getBoolean("updateMultiplicityConcordance")) {
      Files.createDirectories(FIXTURE.getParent());
      Files.writeString(FIXTURE, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(fixture) + "\n");
    }
    assertTrue(Files.exists(FIXTURE), "Generate with -DupdateMultiplicityConcordance=true");
    assertEquals(MAPPER.readTree(Files.readString(FIXTURE)), fixture,
        "Java matrix fixtures are stale; regenerate and refresh the TypeScript concordance fixture");
    assertFalse(cases.isEmpty());
  }
}
