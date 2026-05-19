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

  // Known bug: YamlArtifactReader.readFieldUi doesn't wire width/height through when
  // constructing a StaticFieldUi, so the round-trip silently drops them.
  @Disabled("YAML round-trip drops width/height on YouTube/Image fields")
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

  // Known bug: the renderer and reader disagree on where `valueRecommendation:` lives
  // (field-level vs. under the controlled-term values block), so the flag silently
  // round-trips to false.
  @Disabled("YAML round-trip drops valueRecommendation flag")
  @Test public void testRoundTripPreservesValueRecommendationOnControlledTerm()
  {
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
