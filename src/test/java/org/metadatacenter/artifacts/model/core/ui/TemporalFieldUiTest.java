package org.metadatacenter.artifacts.model.core.ui;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;

import static org.junit.Assert.*;

public class TemporalFieldUiTest
{
  @Test
  public void testCreate() {

    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timeZoneEnabled = false;
    boolean hidden = false;
    boolean recommendedValue = false;
    boolean continuePreviousLine = false;

    TemporalFieldUi temporalFieldUi = TemporalFieldUi.builder()
      .withTemporalGranularity(temporalGranularity)
      .withInputTimeFormat(inputTimeFormat)
      .withTimezoneEnabled(timeZoneEnabled)
      .withHidden(hidden)
      .withRecommendedValue(recommendedValue)
      .withContinuePreviousLine(continuePreviousLine)
      .build();

    assertTrue(temporalFieldUi.inputType().isTemporal());
    assertEquals(FieldInputType.TEMPORAL, temporalFieldUi.inputType());
    assertEquals(temporalGranularity, temporalFieldUi.temporalGranularity());
    assertEquals(inputTimeFormat, temporalFieldUi.inputTimeFormat().get());
    assertEquals(timeZoneEnabled, temporalFieldUi.timezoneEnabled().get());
    assertEquals(hidden, temporalFieldUi.hidden());
    assertEquals(continuePreviousLine, temporalFieldUi.continuePreviousLine());
    assertEquals(recommendedValue, temporalFieldUi.recommendedValue());
  }

}