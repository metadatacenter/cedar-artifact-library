package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The two positions outside a field's value that may hold a relative reference, which the rule
 * matrix does not reach: a nested child's temporary {@code @id}, which the server replaces on a
 * write, and an action's {@code sourceUri}, where the legacy editor writes {@code "template"}.
 */
public class IdentifierPositionTest
{
  private static final String TEMPORARY = "tmp-1542056961440-10793276";

  private final JsonArtifactReader jsonReader = new JsonArtifactReader();
  private final JsonArtifactRenderer jsonRenderer = new JsonArtifactRenderer();

  private ObjectNode templateWithChild()
  {
    TextField text = TextField.builder().withName("Text").withJsonLdId(URI.create("https://example.org/fields/text"))
      .build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder().withName("Identifiers")
      .withJsonLdId(URI.create("https://example.org/templates/identifiers")).withFieldSchema("text", text).build();
    return jsonRenderer.renderTemplateSchemaArtifact(template);
  }

  @Test
  public void readsAChildsTemporaryIdentifier()
  {
    ObjectNode rendering = templateWithChild();
    ((ObjectNode) rendering.get("properties").get("text")).put("@id", TEMPORARY);

    TemplateSchemaArtifact read = jsonReader.readTemplateSchemaArtifact(rendering);

    assertEquals(Optional.of(URI.create(TEMPORARY)), read.getFieldSchemaArtifact("text").jsonLdId());
  }

  @Test
  public void refusesATemporaryArtifactIdentifier()
  {
    ObjectNode rendering = templateWithChild();
    rendering.put("@id", TEMPORARY);

    assertThrows(ArtifactParseException.class, () -> jsonReader.readTemplateSchemaArtifact(rendering));
  }

  private static ControlledTermField fieldWithAction(URI sourceUri)
  {
    return ControlledTermField.builder().withName("Term").withJsonLdId(URI.create("https://example.org/fields/term"))
      .withClassValueConstraint(URI.create("https://example.org/classes/kept"), "SLOT", "Kept", "Kept",
        ValueType.ONTOLOGY_CLASS)
      .withValueConstraintsAction(URI.create("https://example.org/classes/moved"), "SLOT", ValueType.ONTOLOGY_CLASS,
        ValueConstraintsActionType.MOVE, sourceUri, 0)
      .build();
  }

  @Test
  public void readsAnActionSourceUriOfTemplate()
  {
    ControlledTermField field = fieldWithAction(URI.create("template"));

    FieldSchemaArtifact json = jsonReader.readFieldSchemaArtifact(jsonRenderer.renderFieldSchemaArtifact(field));
    FieldSchemaArtifact yaml = new YamlArtifactReader(false).readFieldSchemaArtifact(
      new YamlArtifactRenderer(false).renderFieldSchemaArtifact(field));

    assertEquals(field, json);
    assertEquals(jsonRenderer.renderFieldSchemaArtifact(field), jsonRenderer.renderFieldSchemaArtifact(yaml));
  }

  @Test
  public void refusesARelativeActionTermUri()
  {
    ObjectNode rendering = jsonRenderer.renderFieldSchemaArtifact(fieldWithAction(URI.create("template")));
    ((ObjectNode) rendering.get("_valueConstraints").get("actions").get(0)).put("termUri", "relative/path");

    assertThrows(ArtifactParseException.class, () -> jsonReader.readFieldSchemaArtifact(rendering));
  }
}
