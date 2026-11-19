package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A value can state {@code rdfs:label} as null, and a stored template can require the key. The model keeps the
 * null as no value, so the writer left the key out, and the written document failed the template the stored
 * one satisfied. The writer keeps a stated null exactly where the stored template requires its key.
 *
 * <p>The same holds for {@code skos:notation}, {@code skos:prefLabel} and {@code @language}, and for a field
 * at any depth: the second group of tests states each key as null in a field at the root, in a single
 * element and in a repeated element.
 */
public class StatedNullTest
{
  private static final URI PROPERTY = URI.create("https://schema.metadatacenter.org/properties/note");
  private static final URI PROPERTIES = URI.create("https://schema.metadatacenter.org/properties/notes");

  /** The template as stored, its Note field's value schema requiring the keys {@code note} names, if any. */
  private static TemplateSchemaArtifact storedTemplate(List<String> note, List<String> notes)
  {
    TemplateSchemaArtifact built = TemplateSchemaArtifact.builder().withName("Study")
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/stated-null"))
      .withFieldSchema(TextField.builder().withName("Note").withPropertyUri(PROPERTY).build())
      .withFieldSchema(TextField.builder().withName("Notes").withIsMultiple(true).withPropertyUri(PROPERTIES).build())
      .build();
    ObjectNode document = new JsonArtifactRenderer().renderTemplateSchemaArtifact(built);
    require((ObjectNode) document.path("properties").path("Note"), note);
    require((ObjectNode) document.path("properties").path("Notes").path("items"), notes);
    return new JsonArtifactReader().readTemplateSchemaArtifact(document);
  }

  private static void require(ObjectNode valueSchema, List<String> keys)
  {
    ArrayNode required = (ArrayNode) valueSchema.get("required");
    keys.forEach(required::add);
  }

  /** An instance of the template whose values state a null label: one Note, two Notes, the second unlabelled. */
  private static TemplateInstanceArtifact storedInstance(TemplateSchemaArtifact template)
  {
    TemplateInstanceArtifact built = TemplateInstanceArtifact.builder().withName("Study Instance")
      .withIsBasedOn(template.jsonLdId().orElseThrow())
      .withSingleInstanceFieldInstance("Note", literal("first"))
      .withMultiInstanceFieldInstances("Notes", List.of(literal("a"), literal("b"))).build();
    ObjectNode document = new JsonArtifactRenderer().renderTemplateInstanceArtifact(template, built);
    ((ObjectNode) document.get("Note")).putNull("rdfs:label");
    ((ObjectNode) document.get("Notes").get(0)).putNull("rdfs:label");
    return new JsonArtifactReader().readTemplateInstanceArtifact(document);
  }

  private static FieldInstanceArtifact literal(String value)
  {
    return FieldInstanceArtifact.create(Collections.emptyList(), Optional.empty(), Optional.of(value),
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  private static ObjectNode written(TemplateSchemaArtifact template)
  {
    return new JsonArtifactRenderer().renderTemplateInstanceArtifact(template, storedInstance(template));
  }

  private static List<String> keys(JsonNode value)
  {
    List<String> keys = new ArrayList<>();
    value.fieldNames().forEachRemaining(keys::add);
    return keys;
  }

  @Test public void keepsAStatedNullTheStoredTemplateRequires()
  {
    ObjectNode document = written(storedTemplate(List.of("rdfs:label"), List.of("rdfs:label")));

    assertEquals(List.of("@value", "rdfs:label"), keys(document.get("Note")));
    assertTrue(document.get("Note").get("rdfs:label").isNull());
    assertEquals(List.of("@value", "rdfs:label"), keys(document.get("Notes").get(0)));
    // A value that stated no null gains none: only a stated null is kept.
    assertEquals(List.of("@value"), keys(document.get("Notes").get(1)));
  }

  @Test public void dropsAStatedNullTheStoredTemplateDoesNotRequire()
  {
    ObjectNode document = written(storedTemplate(List.of(), List.of()));

    assertEquals(List.of("@value"), keys(document.get("Note")));
    assertEquals(List.of("@value"), keys(document.get("Notes").get(0)));
  }

  @Test public void keepsEachStatedNullOnlyWhereItsOwnFieldRequiresIt()
  {
    ObjectNode document = written(storedTemplate(List.of(), List.of("rdfs:label")));

    assertEquals(List.of("@value"), keys(document.get("Note")));
    assertEquals(List.of("@value", "rdfs:label"), keys(document.get("Notes").get(0)));
  }

  @Test public void writesNoStatedNullWithoutTheTemplate()
  {
    TemplateSchemaArtifact template = storedTemplate(List.of("rdfs:label"), List.of("rdfs:label"));
    ObjectNode document = new JsonArtifactRenderer().renderTemplateInstanceArtifact(storedInstance(template));

    assertFalse(document.get("Note").has("rdfs:label"));
  }

  /** The keys a value can state as null. */
  private static final List<String> NULLABLE_KEYS = List.of("rdfs:label", "skos:notation", "skos:prefLabel", "@language");

  /**
   * A template whose Note field sits at the root, whose Street field sits in the single element Address and
   * whose Site field sits in the repeated element Visits, each field's value schema requiring {@code keys}.
   */
  private static TemplateSchemaArtifact nestedStoredTemplate(List<String> keys)
  {
    TemplateSchemaArtifact built = TemplateSchemaArtifact.builder().withName("Study")
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/stated-null-nested"))
      .withFieldSchema(TextField.builder().withName("Note").withPropertyUri(PROPERTY).build())
      .withElementSchema(ElementSchemaArtifact.builder().withName("Address")
        .withPropertyUri(URI.create("https://schema.metadatacenter.org/properties/address"))
        .withFieldSchema(TextField.builder().withName("Street")
          .withPropertyUri(URI.create("https://schema.metadatacenter.org/properties/street")).build()).build())
      .withElementSchema(ElementSchemaArtifact.builder().withName("Visits").withIsMultiple(true)
        .withPropertyUri(URI.create("https://schema.metadatacenter.org/properties/visits"))
        .withFieldSchema(TextField.builder().withName("Site")
          .withPropertyUri(URI.create("https://schema.metadatacenter.org/properties/site")).build()).build())
      .build();
    ObjectNode document = new JsonArtifactRenderer().renderTemplateSchemaArtifact(built);
    require((ObjectNode) document.at("/properties/Note"), keys);
    require((ObjectNode) document.at("/properties/Address/properties/Street"), keys);
    require((ObjectNode) document.at("/properties/Visits/items/properties/Site"), keys);
    return new JsonArtifactReader().readTemplateSchemaArtifact(document);
  }

  /** An instance stating {@code key} as null in Note, in Street and in the first of two Visits' Site. */
  private static TemplateInstanceArtifact nestedStoredInstance(TemplateSchemaArtifact template, String key)
  {
    TemplateInstanceArtifact built = TemplateInstanceArtifact.builder().withName("Study Instance")
      .withIsBasedOn(template.jsonLdId().orElseThrow())
      .withSingleInstanceFieldInstance("Note", literal("first"))
      .withSingleInstanceElementInstance("Address",
        ElementInstanceArtifact.builder().withSingleInstanceFieldInstance("Street", literal("Main")).build())
      .withMultiInstanceElementInstances("Visits", List.of(
        ElementInstanceArtifact.builder().withSingleInstanceFieldInstance("Site", literal("North")).build(),
        ElementInstanceArtifact.builder().withSingleInstanceFieldInstance("Site", literal("South")).build()))
      .build();
    ObjectNode document = new JsonArtifactRenderer().renderTemplateInstanceArtifact(template, built);
    ((ObjectNode) document.at("/Note")).putNull(key);
    ((ObjectNode) document.at("/Address/Street")).putNull(key);
    ((ObjectNode) document.at("/Visits/0/Site")).putNull(key);
    return new JsonArtifactReader().readTemplateInstanceArtifact(document);
  }

  private static ObjectNode writtenNested(List<String> required, String key)
  {
    TemplateSchemaArtifact template = nestedStoredTemplate(required);
    return new JsonArtifactRenderer().renderTemplateInstanceArtifact(template, nestedStoredInstance(template, key));
  }

  @ParameterizedTest(name = "{0}") @ValueSource(strings = { "rdfs:label", "skos:notation", "skos:prefLabel", "@language" })
  public void keepsAStatedNullOfEachKeyAtEveryDepth(String key)
  {
    ObjectNode document = writtenNested(NULLABLE_KEYS, key);

    for (String path : List.of("/Note", "/Address/Street", "/Visits/0/Site")) {
      assertEquals(List.of("@value", key), keys(document.at(path)), path);
      assertTrue(document.at(path).get(key).isNull(), path);
    }
    // The second visit stated no null, so it gains none.
    assertEquals(List.of("@value"), keys(document.at("/Visits/1/Site")));
  }

  @ParameterizedTest(name = "{0}") @ValueSource(strings = { "rdfs:label", "skos:notation", "skos:prefLabel", "@language" })
  public void dropsAStatedNullOfEachKeyAtEveryDepthTheTemplateDoesNotRequire(String key)
  {
    List<String> others = NULLABLE_KEYS.stream().filter(other -> !other.equals(key)).toList();
    ObjectNode document = writtenNested(others, key);

    for (String path : List.of("/Note", "/Address/Street", "/Visits/0/Site"))
      assertEquals(List.of("@value"), keys(document.at(path)), path);
  }

  @Test public void aStatedNullChangesNeitherTheModelNorItsEquality()
  {
    TemplateSchemaArtifact requiring = storedTemplate(List.of("rdfs:label"), List.of("rdfs:label"));
    TemplateSchemaArtifact plain = storedTemplate(List.of(), List.of());
    TemplateInstanceArtifact instance = storedInstance(plain);
    TemplateInstanceArtifact unstated = new JsonArtifactReader().readTemplateInstanceArtifact(
      new JsonArtifactRenderer().renderTemplateInstanceArtifact(plain, instance));

    assertEquals(plain, requiring);
    assertEquals(instance, unstated);
    assertEquals(InstanceInflater.inflate(requiring, instance), InstanceInflater.inflate(plain, instance));
    assertEquals(Optional.empty(), instance.singleInstanceFieldInstances().get("Note").label());
  }
}
