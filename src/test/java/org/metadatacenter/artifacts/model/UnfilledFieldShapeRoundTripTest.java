package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.InstanceInflater;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;

/**
 * What an unfilled field looks like in an instance, and that reading one back does not change it.
 *
 * The two shapes are not interchangeable, and the template says which belongs where. A literal field's
 * sub-schema requires {@code @value}, so an unfilled one is {@code {"@value": null}} and {@code {}} is
 * rejected. A controlled-term field's forbids {@code @value} — its properties are {@code @id} and
 * {@code rdfs:label} under {@code additionalProperties: false} — so an unfilled one is {@code {}} and
 * {@code {"@value": null}} is rejected.
 *
 * <p>An instance read on its own carries no template, so nothing in it says which kind a field is: the
 * shape the document chose is the only evidence, and the reader keeps it. It used to write
 * {@code @value: null} for both, which left every unfilled controlled-term field invalid against the
 * template it was filled from — 34 of the 38 such fields in the shared corpus.
 */
class UnfilledFieldShapeRoundTripTest
{
  private static final URI TEMPLATE_ID = URI.create("https://repo.metadatacenter.org/templates/probe");

  @Test void anUnfilledLiteralFieldKeepsItsNullValueKey()
  {
    ObjectNode rendered = renderTwice(templateWithA(TextField.builder().withName("Text").build()));
    ObjectNode field = (ObjectNode) rendered.get("Text");

    assertTrue(field.has(JSON_LD_VALUE), "a literal field's sub-schema requires @value");
    assertTrue(field.get(JSON_LD_VALUE).isNull());
    assertEquals(1, field.size());
  }

  @Test void anUnfilledControlledTermFieldKeepsItsEmptyNode()
  {
    ObjectNode rendered = renderTwice(templateWithA(ControlledTermField.builder().withName("Term").build()));
    ObjectNode field = (ObjectNode) rendered.get("Term");

    assertFalse(field.has(JSON_LD_VALUE), "a controlled-term field's sub-schema allows no @value");
    assertEquals(0, field.size());
  }

  /**
   * Builds an instance from the template, writes it, reads it back with no template in hand, and writes
   * it again — the path a conversion takes, and the one that used to change the shape.
   */
  private static ObjectNode renderTwice(TemplateSchemaArtifact template)
  {
    JsonArtifactRenderer renderer = new JsonArtifactRenderer();
    JsonArtifactReader reader = new JsonArtifactReader();

    TemplateInstanceArtifact built = InstanceInflater.inflate(template,
      TemplateInstanceArtifact.builder().withIsBasedOn(TEMPLATE_ID).withName("instance")
        .withDescription("").build());

    ObjectNode once = renderer.renderTemplateInstanceArtifact(built);
    return renderer.renderTemplateInstanceArtifact(reader.readTemplateInstanceArtifact(once));
  }

  private static TemplateSchemaArtifact templateWithA(
    org.metadatacenter.artifacts.model.core.FieldSchemaArtifact field)
  {
    return TemplateSchemaArtifact.builder().withJsonLdId(TEMPLATE_ID).withName("Template")
      .withFieldSchema(field).build();
  }
}
