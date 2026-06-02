package org.metadatacenter.artifacts.model.core.ui;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;

import static org.junit.jupiter.api.Assertions.*;

public class TemporalFieldUiTest
{
  @Test
  public void testCreate() {

    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timeZoneEnabled = false;
    boolean hidden = false;
    boolean continuePreviousLine = false;

    TemporalFieldUi temporalFieldUi = TemporalFieldUi.builder()
      .withTemporalGranularity(temporalGranularity)
      .withInputTimeFormat(inputTimeFormat)
      .withTimezoneEnabled(timeZoneEnabled)
      .withHidden(hidden)
      .withContinuePreviousLine(continuePreviousLine)
      .build();

    assertTrue(temporalFieldUi.inputType().isTemporal());
    assertEquals(FieldInputType.TEMPORAL, temporalFieldUi.inputType());
    assertEquals(temporalGranularity, temporalFieldUi.temporalGranularity());
    assertEquals(inputTimeFormat, temporalFieldUi.inputTimeFormat().get());
    assertEquals(timeZoneEnabled, temporalFieldUi.timezoneEnabled().get());
    assertEquals(hidden, temporalFieldUi.hidden());
    assertEquals(continuePreviousLine, temporalFieldUi.continuePreviousLine());
  }

  @Test
  public void testAllTemporalGranularities()
  {
    // Each TemporalGranularity must round-trip the builder without coercion. The renderer
    // emits granularity-conditional fields based on whether the granularity isYear/isMonth/isDay,
    // so the builder must preserve the exact constant.
    for (TemporalGranularity g : TemporalGranularity.values()) {
      TemporalFieldUi ui = TemporalFieldUi.builder()
        .withTemporalGranularity(g)
        .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR)
        .build();
      assertEquals(g, ui.temporalGranularity());
    }
  }

  @Test
  public void testInputTimeFormatTwelveHour()
  {
    TemporalFieldUi ui = TemporalFieldUi.builder()
      .withTemporalGranularity(TemporalGranularity.MINUTE)
      .withInputTimeFormat(InputTimeFormat.TWELVE_HOUR)
      .build();

    assertEquals(InputTimeFormat.TWELVE_HOUR, ui.inputTimeFormat().get());
  }

  @Test
  public void testTimezoneEnabledExplicitlyTrue()
  {
    // Default is Optional.empty(); setting true must surface as Optional.of(true).
    TemporalFieldUi ui = TemporalFieldUi.builder()
      .withTemporalGranularity(TemporalGranularity.MINUTE)
      .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR)
      .withTimezoneEnabled(true)
      .build();

    assertTrue(ui.timezoneEnabled().isPresent());
    assertTrue(ui.timezoneEnabled().get());
  }

  @Test
  public void testDateOnlyGranularitiesClearTimeSettings()
  {
    // inputTimeFormat and timezoneEnabled are meaningless at a date-only granularity and are
    // normalized away, even if a caller (or a read artifact) supplies them.
    for (TemporalGranularity g : new TemporalGranularity[] {
      TemporalGranularity.DAY, TemporalGranularity.MONTH, TemporalGranularity.YEAR }) {
      TemporalFieldUi ui = TemporalFieldUi.builder()
        .withTemporalGranularity(g)
        .withInputTimeFormat(InputTimeFormat.TWELVE_HOUR)
        .withTimezoneEnabled(true)
        .build();

      assertTrue(ui.inputTimeFormat().isEmpty(), "inputTimeFormat should be cleared for " + g);
      assertTrue(ui.timezoneEnabled().isEmpty(), "timezoneEnabled should be cleared for " + g);
    }
  }

  @Test
  public void testTimeBearingGranularitiesPreserveTimeSettings()
  {
    for (TemporalGranularity g : new TemporalGranularity[] {
      TemporalGranularity.HOUR, TemporalGranularity.MINUTE, TemporalGranularity.SECOND,
      TemporalGranularity.DECIMAL_SECOND }) {
      TemporalFieldUi ui = TemporalFieldUi.builder()
        .withTemporalGranularity(g)
        .withInputTimeFormat(InputTimeFormat.TWELVE_HOUR)
        .withTimezoneEnabled(true)
        .build();

      assertEquals(InputTimeFormat.TWELVE_HOUR, ui.inputTimeFormat().get(), "inputTimeFormat should survive for " + g);
      assertTrue(ui.timezoneEnabled().get(), "timezoneEnabled should survive for " + g);
    }
  }
}
