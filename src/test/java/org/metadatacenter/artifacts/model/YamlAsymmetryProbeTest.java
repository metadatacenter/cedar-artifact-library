package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ImageField;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.RorField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.fields.constraints.LinkValueConstraints;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.YouTubeField;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trip probes for individual field settings that the YAML renderer / reader pair has
 * historically dropped. A failure here means a silent-data-loss bug in the YAML pipeline.
 */
public class YamlAsymmetryProbeTest
{
  private YamlArtifactReader reader;
  private YamlArtifactRenderer renderer;

  @BeforeEach public void setup()
  {
    reader = new YamlArtifactReader();
    renderer = new YamlArtifactRenderer(false);
  }

  @Test public void testRoundTripPreservesYouTubeWidthHeight()
  {
    YouTubeField original = YouTubeField.builder()
      .withName("Demo Video").withContent("dQw4w9WgXcQ").withWidth(640).withHeight(480).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(640, roundTripped.fieldUi().asStaticFieldUi().width().get());
    assertEquals(480, roundTripped.fieldUi().asStaticFieldUi().height().get());
  }

  @Test public void testRoundTripPreservesValueRecommendationOnControlledTerm()
  {
    ControlledTermField original = ControlledTermField.builder()
      .withName("Disease").withValueRecommendationEnabled(true).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(true, roundTripped.fieldUi().valueRecommendationEnabled());
  }

  // A standalone (top-level) field has no `configuration:` block, so its field-level UI
  // flags must be emitted at the field level or they round-trip to their defaults.
  @Test public void testRoundTripPreservesHiddenOnStandaloneField()
  {
    TextField original = TextField.builder().withName("Secret").withHidden(true).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(true, roundTripped.fieldUi().hidden());
  }

  @Test public void testRoundTripPreservesContinuePreviousLineOnStandaloneField()
  {
    TextField original = TextField.builder().withName("Inline").withContinuePreviousLine(true).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(true, roundTripped.fieldUi().continuePreviousLine());
  }

  // The ext-* identifier fields (ROR/ORCID/PFAS/RRID/PubMed/NIH-grant/DOI) are IRI-valued
  // and carry LinkValueConstraints. The YAML reader must reconstruct those constraints (keyed
  // on isIri(), not == LINK) or the *Field builders throw ClassCastException on read-back.
  @Test public void testRoundTripPreservesRorFieldLinkConstraints()
  {
    RorField original = RorField.builder().withName("Affiliation ROR").build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertTrue(roundTripped instanceof RorField,
      "ext-ror-field must read back as a RorField, got " + roundTripped.getClass().getSimpleName());
    assertTrue(roundTripped.valueConstraints().get() instanceof LinkValueConstraints,
      "ext-ror-field must round-trip with LinkValueConstraints");
  }

  @Test public void testRoundTripPreservesNumericDecimalPlaces()
  {
    NumericField original = NumericField.builder().withName("pH")
      .withNumericType(XsdNumericDatatype.DECIMAL)
      .withDecimalPlaces(2).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    NumericValueConstraints vc = (NumericValueConstraints) roundTripped.valueConstraints().get();
    assertEquals(2, vc.decimalPlace().get());
  }

  @Test public void testRoundTripPreservesTemporalInputTimeFormat()
  {
    TemporalField original = TemporalField.builder().withName("When")
      .withTemporalType(XsdTemporalDatatype.DATETIME)
      .withTemporalGranularity(TemporalGranularity.MINUTE)
      .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(InputTimeFormat.TWENTY_FOUR_HOUR,
      roundTripped.fieldUi().asTemporalFieldUi().inputTimeFormat().get());
  }

  @Test public void testRoundTripPreservesTemporalGranularity()
  {
    // Each granularity has a different code path in the renderer; SECOND specifically must
    // round-trip without being widened to MINUTE.
    TemporalField original = TemporalField.builder().withName("When")
      .withTemporalType(XsdTemporalDatatype.DATETIME)
      .withTemporalGranularity(TemporalGranularity.SECOND)
      .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(TemporalGranularity.SECOND,
      roundTripped.fieldUi().asTemporalFieldUi().temporalGranularity());
  }

  // A field nested in a parent carries its UI flags in a `configuration:` block rather than at
  // the field level, so it exercises a different reader path than the standalone probes above.
  // Reading the static width / height from the field level alone drops the _ui._size box of
  // every static image and YouTube field embedded in a template or element.
  @Test public void testRoundTripPreservesImageWidthHeightNestedInTemplate()
  {
    ImageField logo = ImageField.builder()
      .withName("Logo").withContent("https://example.org/logo.png").withWidth(300).withHeight(200).build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder()
      .withName("Study").withFieldSchema(logo).build();

    TemplateSchemaArtifact roundTripped = roundTripTemplate(original);

    StaticFieldUi ui = roundTripped.getFieldSchemaArtifact("Logo").fieldUi().asStaticFieldUi();
    assertEquals(300, ui.width().get());
    assertEquals(200, ui.height().get());
  }

  @Test public void testRoundTripPreservesYouTubeWidthHeightNestedInTemplate()
  {
    YouTubeField video = YouTubeField.builder()
      .withName("Demo Video").withContent("dQw4w9WgXcQ").withWidth(640).withHeight(480).build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder()
      .withName("Study").withFieldSchema(video).build();

    TemplateSchemaArtifact roundTripped = roundTripTemplate(original);

    StaticFieldUi ui = roundTripped.getFieldSchemaArtifact("Demo Video").fieldUi().asStaticFieldUi();
    assertEquals(640, ui.width().get());
    assertEquals(480, ui.height().get());
  }

  // Two levels down: the child of an element that is itself a child of a template.
  @Test public void testRoundTripPreservesImageWidthHeightNestedInElement()
  {
    ImageField logo = ImageField.builder()
      .withName("Logo").withContent("https://example.org/logo.png").withWidth(120).withHeight(60).build();
    ElementSchemaArtifact branding = ElementSchemaArtifact.builder()
      .withName("Branding").withFieldSchema(logo).build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder()
      .withName("Study").withElementSchema(branding).build();

    TemplateSchemaArtifact roundTripped = roundTripTemplate(original);

    StaticFieldUi ui = roundTripped.getElementSchemaArtifact("Branding")
      .getFieldSchemaArtifact("Logo").fieldUi().asStaticFieldUi();
    assertEquals(120, ui.width().get());
    assertEquals(60, ui.height().get());
  }

  // A static field with no size set must stay unset rather than picking up a default.
  @Test public void testRoundTripLeavesAbsentWidthHeightAbsentNestedInTemplate()
  {
    ImageField logo = ImageField.builder()
      .withName("Logo").withContent("https://example.org/logo.png").build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder()
      .withName("Study").withFieldSchema(logo).build();

    TemplateSchemaArtifact roundTripped = roundTripTemplate(original);

    StaticFieldUi ui = roundTripped.getFieldSchemaArtifact("Logo").fieldUi().asStaticFieldUi();
    assertTrue(ui.width().isEmpty());
    assertTrue(ui.height().isEmpty());
  }

  /**
   * The one asymmetry this file records rather than repairs.
   *
   * <p>An attribute-value field records no requirement: the JSON form gives it no
   * {@code _valueConstraints} node to keep one in, because its property names come from whoever
   * fills the form and the template describes them through {@code additionalProperties} instead.
   * The YAML form keeps a requirement on the child, in a {@code configuration:} block every kind of
   * child has, so it had somewhere to write what JSON could not carry — and did, which left one
   * template saying the field was required in one serialization and saying nothing in the other.
   *
   * <p>{@code AttributeValueField.Builder} has always refused a requirement — {@code
   * withRequiredValue} and {@code withRecommendedValue} are no-ops on it — so this library's own
   * model already held the rule. What did not was the YAML reader, which builds value constraints
   * from the document rather than through that builder, so it accepted a requirement the builder
   * would have dropped, and the renderer wrote it back out. The reader refuses one now as well,
   * which leaves nothing that can put one in the model and so nothing for the renderer to write;
   * that is why the renderer has no guard of its own.
   *
   * <p>Both halves are asserted below: no requirement survives a build and a render, and none is
   * honoured when a document written before this declares one. The loss is the point, which is why
   * it is asserted rather than probed for preservation like everything else here.
   */
  @Test public void testAttributeValueFieldRecordsNoRequirement()
  {
    AttributeValueField original = AttributeValueField.builder()
      .withName("Attribute").withRequiredValue(true).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder()
      .withJsonLdId(java.net.URI.create("https://repo.metadatacenter.org/templates/probe"))
      .withName("Template").withDescription("").withFieldSchema(original).build();

    assertFalse(template.getFieldSchemaArtifact("Attribute").requiredValue(),
      "the builder refuses a requirement on this type, and has for as long as it has existed");

    LinkedHashMap<String, Object> rendering = renderer.renderTemplateSchemaArtifact(template);
    assertFalse(renderedChildConfiguration(rendering, "Attribute").containsKey("required"),
      "an attribute-value field's requirement has nowhere to go in JSON, so YAML writes none");

    // A document written before this change says required; reading it must not resurrect one.
    renderedChildConfiguration(rendering, "Attribute").put("required", true);
    TemplateSchemaArtifact readBack = reader.readTemplateSchemaArtifact(rendering);
    assertFalse(readBack.getFieldSchemaArtifact("Attribute").requiredValue(),
      "a requirement read from an older document is not honoured either");
  }

  @Test public void testOrdinaryFieldStillRecordsItsRequirement()
  {
    TextField original = TextField.builder().withName("Title").withRequiredValue(true).build();
    TemplateSchemaArtifact template = TemplateSchemaArtifact.builder()
      .withJsonLdId(java.net.URI.create("https://repo.metadatacenter.org/templates/probe"))
      .withName("Template").withDescription("").withFieldSchema(original).build();

    LinkedHashMap<String, Object> rendering = renderer.renderTemplateSchemaArtifact(template);
    assertEquals(true, renderedChildConfiguration(rendering, "Title").get("required"));
    assertTrue(reader.readTemplateSchemaArtifact(rendering).getFieldSchemaArtifact("Title").requiredValue());
  }

  /**
   * The child's {@code configuration:} block in a rendering, created and attached when the child has
   * none.
   *
   * Attached rather than returned loose, so that a caller putting a key into it changes the
   * rendering. Returning a throwaway map made the injection below a no-op and the assertion after it
   * vacuous — it passed with the guards it was written to exercise removed.
   */
  @SuppressWarnings("unchecked")
  private LinkedHashMap<String, Object> renderedChildConfiguration(LinkedHashMap<String, Object> rendering, String key)
  {
    for (Object child : (java.util.List<Object>) rendering.get("children")) {
      LinkedHashMap<String, Object> childRendering = (LinkedHashMap<String, Object>) child;
      if (key.equals(childRendering.get("key"))) {
        LinkedHashMap<String, Object> configuration =
          (LinkedHashMap<String, Object>) childRendering.get("configuration");
        if (configuration == null) {
          configuration = new LinkedHashMap<>();
          childRendering.put("configuration", configuration);
        }
        return configuration;
      }
    }
    throw new AssertionError("no child rendered under key " + key);
  }

  private FieldSchemaArtifact roundTripField(FieldSchemaArtifact original)
  {
    LinkedHashMap<String, Object> rendering = renderer.renderFieldSchemaArtifact(original);
    return reader.readFieldSchemaArtifact(rendering);
  }

  private TemplateSchemaArtifact roundTripTemplate(TemplateSchemaArtifact original)
  {
    LinkedHashMap<String, Object> rendering = renderer.renderTemplateSchemaArtifact(original);
    return reader.readTemplateSchemaArtifact(rendering);
  }
}
