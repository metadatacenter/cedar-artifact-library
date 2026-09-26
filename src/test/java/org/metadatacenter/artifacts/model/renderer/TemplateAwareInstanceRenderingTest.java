package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.TextFieldInstance;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.tools.InstanceInflater;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.report.ValidationReport;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;

/**
 * The renderer overloads that take the schema an instance is based on.
 *
 * A JSON instance must carry every child its template defines, because the template's JSON Schema
 * marks each one required. A sparse instance, such as one read from YAML, omits the unset ones, so
 * rendering it alone produces JSON its own template rejects. The overloads complete the instance
 * against the schema and then render it, so a caller holding a sparse instance and its template gets
 * a valid JSON instance from one call.
 */
public class TemplateAwareInstanceRenderingTest
{
  private static final URI TEMPLATE_ID =
    URI.create("https://repo.metadatacenter.org/templates/5d1c7a3e-0000-4000-8000-000000000001");

  private final JsonArtifactRenderer renderer = new JsonArtifactRenderer();

  @Test public void aSparseInstanceRendersAsJsonItsTemplateAccepts() throws IOException
  {
    TemplateSchemaArtifact template = template();
    TemplateInstanceArtifact sparse = sparseInstance();

    ObjectNode alone = renderer.renderTemplateInstanceArtifact(sparse);
    ObjectNode completed = renderer.renderTemplateInstanceArtifact(template, sparse);

    assertEquals("false", validate(alone, template).getValidationStatus(),
      "the instance alone omits children its template requires");
    ValidationReport report = validate(completed, template);
    assertEquals("true", report.getValidationStatus(), "errors: " + report.getErrors());
  }

  @Test public void everyChildTheTemplateDefinesIsPresentAndSetValuesAreKept()
  {
    ObjectNode completed = renderer.renderTemplateInstanceArtifact(template(), sparseInstance());

    assertEquals("A study", completed.get("Title").get(JSON_LD_VALUE).asText());
    assertTrue(completed.get("Disease").isEmpty(), "an unset controlled-term field renders as {}");
    assertEquals(1, completed.get("Keyword").size(), "a repeated field is filled to its lower bound");
    assertTrue(completed.get("Funder").get("Name").get(JSON_LD_VALUE).isNull(),
      "an omitted element is added with its own children");
  }

  @Test public void theResultIsTheCompletedInstanceRendered()
  {
    TemplateSchemaArtifact template = template();
    TemplateInstanceArtifact completed = InstanceInflater.inflate(template, sparseInstance());

    assertEquals(renderer.renderTemplateInstanceArtifact(completed),
      renderer.renderTemplateInstanceArtifact(template, sparseInstance()));
    assertEquals(renderer.renderTemplateInstanceArtifact(completed),
      renderer.renderTemplateInstanceArtifact(template, completed),
      "an instance that is already complete renders as the one-argument method renders it");
  }

  @Test public void anInstanceReadFromYamlRendersAsAValidJsonInstance() throws IOException
  {
    TemplateSchemaArtifact template = template();
    LinkedHashMap<String, Object> yaml = new YamlArtifactRenderer(false)
      .renderTemplateInstanceArtifact(InstanceInflater.inflate(template, sparseInstance()));
    TemplateInstanceArtifact read = new YamlArtifactReader().readTemplateInstanceArtifact(yaml);

    ValidationReport report = validate(renderer.renderTemplateInstanceArtifact(template, read), template);
    assertEquals("true", report.getValidationStatus(), "errors: " + report.getErrors());
  }

  @Test public void aSparseElementInstanceRendersWithEveryChildItsSchemaDefines()
  {
    ElementSchemaArtifact funder = funder();
    ElementInstanceArtifact sparse = ElementInstanceArtifact.builder().build();

    ObjectNode alone = renderer.renderElementInstanceArtifact(sparse);
    ObjectNode completed = renderer.renderElementInstanceArtifact(funder, sparse);

    assertFalse(alone.has("Name"), "the instance alone carries only what it was given");
    assertTrue(completed.get("Name").get(JSON_LD_VALUE).isNull());
    assertEquals(renderer.renderElementInstanceArtifact(InstanceInflater.inflateElement(funder, sparse)),
      completed);
  }

  private ValidationReport validate(ObjectNode instance, TemplateSchemaArtifact template) throws IOException
  {
    return new CedarValidator().validateTemplateInstance(instance, renderer.renderTemplateSchemaArtifact(template));
  }

  private static TemplateSchemaArtifact template()
  {
    return TemplateSchemaArtifact.builder().withJsonLdId(TEMPLATE_ID).withName("Study")
      .withFieldSchema(TextField.builder().withName("Title").build())
      .withFieldSchema(ControlledTermField.builder().withName("Disease").build())
      .withFieldSchema(TextField.builder().withName("Keyword").withIsMultiple(true).withMinItems(1).build())
      .withElementSchema(funder())
      .build();
  }

  private static ElementSchemaArtifact funder()
  {
    return ElementSchemaArtifact.builder().withName("Funder")
      .withFieldSchema(TextField.builder().withName("Name").build()).build();
  }

  private static TemplateInstanceArtifact sparseInstance()
  {
    return TemplateInstanceArtifact.builder().withIsBasedOn(TEMPLATE_ID).withName("Study metadata")
      .withDescription("")
      .withSingleInstanceFieldInstance("Title", TextFieldInstance.builder().withValue("A study").build())
      .build();
  }
}
