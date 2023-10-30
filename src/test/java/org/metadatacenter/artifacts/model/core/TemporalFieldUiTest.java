package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class TemporalFieldUiTest
{
  @Test
  public void testCreate() {

    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timeZoneEnabled = false;
    boolean hidden = false;

    TemporalFieldUi temporalFieldUi = TemporalFieldUi.builder()
      .withTemporalGranularity(temporalGranularity)
      .withInputTimeFormat(inputTimeFormat)
      .withTimezoneEnabled(timeZoneEnabled)
      .withHidden(hidden)
      .build();

    assertTrue(temporalFieldUi.inputType().isTemporal());
    assertEquals(FieldInputType.TEMPORAL, temporalFieldUi.inputType());
    assertEquals(temporalGranularity, temporalFieldUi.temporalGranularity());
    assertEquals(inputTimeFormat, temporalFieldUi.inputTimeFormat());
    assertEquals(timeZoneEnabled, temporalFieldUi.timezoneEnabled());
    assertEquals(hidden, temporalFieldUi.hidden());
  }

}