package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
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
