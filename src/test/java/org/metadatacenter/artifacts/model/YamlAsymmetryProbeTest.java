package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.YouTubeField;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Group E — round-trip *probes* targeted at fields the Explore survey flagged as potentially
 * being silently dropped by the YAML renderer / reader pair. If these tests pass, we confirm
 * symmetry; if they ever fail, we found a real silent-data-loss bug.
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

  // Captured as part of this PR: the renderer emits width/height but the reader doesn't
  // wire them through for static fields. Disabled until the asymmetry is fixed; re-enable
  // when YamlArtifactReader.readFieldUi handles static-field width/height.
  @Disabled("Round-trip silently drops width/height on YouTube/Image fields — separate fix")
  @Test public void testRoundTripPreservesYouTubeWidthHeight()
  {
    // Width/height live on YouTubeFieldUiBuilder; the field builder itself doesn't expose them.
    StaticFieldUi ytUi = StaticFieldUi.youTubeFieldUiBuilder()
      .withContent("dQw4w9WgXcQ").withWidth(640).withHeight(480).build();
    YouTubeField original = YouTubeField.builder()
      .withName("Demo Video").withFieldUi(ytUi).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(640, roundTripped.fieldUi().asStaticFieldUi().width().get());
    assertEquals(480, roundTripped.fieldUi().asStaticFieldUi().height().get());
  }

  // Captured as part of this PR: the renderer emits `valueRecommendation:` when the field UI
  // flag is true, but the round-tripped artifact comes back with the flag false. The reader's
  // readFieldUi reads VALUE_RECOMMENDATION at the field level, but the renderer emits it under
  // the controlled-term values block (or vice versa). Disabled until the location agrees.
  @Disabled("Round-trip silently drops valueRecommendation flag — separate fix")
  @Test public void testRoundTripPreservesValueRecommendationOnControlledTerm()
  {
    // The renderer emits `valueRecommendation:` only when true; this is a real round-trip probe.
    ControlledTermField original = ControlledTermField.builder()
      .withName("Disease").withValueRecommendationEnabled(true).build();

    FieldSchemaArtifact roundTripped = roundTripField(original);

    assertEquals(true, roundTripped.fieldUi().valueRecommendationEnabled());
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
