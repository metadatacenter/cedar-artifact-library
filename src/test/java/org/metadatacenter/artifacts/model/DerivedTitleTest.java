package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A schema artifact's {@code title} is composed from its name, not read from the document.
 *
 * <p>It names the JSON Schema that constrains instances of the artifact, and says nothing an author
 * decided: an artifact called Study has a template schema called "Study template schema" and there
 * is nothing else it could be called. A document supplying some other title describes the same
 * schema by another name, and reading that name back let two artifacts with the same name and kind
 * disagree about what their JSON Schema was called. The JSON reader used to do exactly that while
 * the YAML reader composed, so one artifact was read two ways depending on the format it arrived in.
 *
 * <p>{@code description} is not derived and is not touched here. An author writes it, and a custom
 * one survives every trip.
 */
public class DerivedTitleTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();

  private static ObjectNode withTitle(ObjectNode rendering, String title)
  {
    ObjectNode supplied = rendering.deepCopy();
    supplied.put("title", title);
    supplied.put("description", "A description an author wrote");
    return supplied;
  }

  @Test public void aTemplateTitleIsComposedFromItsName()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withDescription("Study help").build();
    ObjectNode supplied = withTitle(JSON.renderTemplateSchemaArtifact(template), "Something else");

    TemplateSchemaArtifact read = new JsonArtifactReader().readTemplateSchemaArtifact(supplied);

    assertEquals("Study template schema", read.internalName());
    assertEquals("A description an author wrote", read.internalDescription());
  }

  @Test public void anElementTitleIsComposedFromItsName()
  {
    ElementSchemaArtifact element = ElementSchemaArtifact.builder().withName("Address")
      .withDescription("Address help").build();
    ObjectNode supplied = withTitle(JSON.renderElementSchemaArtifact(element), "Something else");

    ElementSchemaArtifact read = new JsonArtifactReader().readElementSchemaArtifact(supplied);

    assertEquals("Address element schema", read.internalName());
  }

  @Test public void aFieldTitleIsComposedFromItsName()
  {
    FieldSchemaArtifact field = org.metadatacenter.artifacts.model.core.TextField.builder()
      .withName("Alias").withDescription("Alias help").build();
    ObjectNode supplied = withTitle(JSON.renderFieldSchemaArtifact(field), "Something else");

    FieldSchemaArtifact read = new JsonArtifactReader().readFieldSchemaArtifact(supplied);

    assertEquals("Alias field schema", read.internalName());
  }

  @Test public void jsonThroughTheModelAndBackWritesTheComposedTitle()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withDescription("Study help").build();
    ObjectNode supplied = withTitle(JSON.renderTemplateSchemaArtifact(template), "Something else");

    ObjectNode round = JSON.renderTemplateSchemaArtifact(
      new JsonArtifactReader().readTemplateSchemaArtifact(supplied));

    assertEquals("Study template schema", round.get("title").asText());
    assertEquals("A description an author wrote", round.get("description").asText());
  }

  @Test public void jsonThroughYamlAndBackWritesTheComposedTitle()
  {
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Study")
      .withDescription("Study help").build();
    ObjectNode supplied = withTitle(JSON.renderTemplateSchemaArtifact(template), "Something else");

    TemplateSchemaArtifact fromJson = new JsonArtifactReader().readTemplateSchemaArtifact(supplied);
    LinkedHashMap<String, Object> yaml = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(fromJson);
    TemplateSchemaArtifact fromYaml = new YamlArtifactReader().readTemplateSchemaArtifact(yaml);

    assertEquals("Study template schema", fromYaml.internalName());
    assertEquals("Study template schema",
      JSON.renderTemplateSchemaArtifact(fromYaml).get("title").asText());
  }

  @Test public void bothReadersComposeTheSameTitle()
  {
    for (SchemaArtifact.Kind kind : SchemaArtifact.Kind.values())
      assertEquals("Study " + kind.noun() + " schema",
        SchemaArtifact.internalNameFor("Study", kind));
  }
}
