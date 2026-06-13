package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.RorField;
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

  private FieldSchemaArtifact roundTripField(FieldSchemaArtifact original)
  {
    LinkedHashMap<String, Object> rendering = renderer.renderFieldSchemaArtifact(original);
    return reader.readFieldSchemaArtifact(rendering);
  }
}
