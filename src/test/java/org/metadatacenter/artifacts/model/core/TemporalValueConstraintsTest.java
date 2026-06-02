package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemporalValueConstraintsTest
{
  @Test public void testBuilderWithAllSetters()
  {
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.DATE;
    String defaultValue = "2026-05-18";

    TemporalValueConstraints constraints = TemporalValueConstraints.builder()
      .withTemporalType(temporalType)
      .withDefaultValue(defaultValue)
      .withRequiredValue(true)
      .withRecommendedValue(true)
      .withMultipleChoice(true)
      .build();

    assertEquals(temporalType, constraints.temporalType());
    assertEquals(defaultValue, constraints.defaultValue().get().value());
    assertEquals(true, constraints.requiredValue());
    assertEquals(true, constraints.recommendedValue());
    assertEquals(true, constraints.multipleChoice());
  }

  @Test public void testDefaultValueRoundTripsThroughTemporalDefaultValueWrapper()
  {
    // Setter accepts a String; getter exposes a TemporalDefaultValue wrapper. Guard against
    // future refactors that drop the wrapping step or change the string encoding.
    TemporalValueConstraints constraints = TemporalValueConstraints.builder()
      .withTemporalType(XsdTemporalDatatype.DATETIME)
      .withDefaultValue("2026-05-18T12:00:00Z")
      .build();

    assertEquals("2026-05-18T12:00:00Z", constraints.defaultValue().get().value());
  }

  // ---- Default-value format validation (keyed off the temporal datatype) ----

  private static TemporalValueConstraints build(XsdTemporalDatatype temporalType, String defaultValue)
  {
    return TemporalValueConstraints.builder().withTemporalType(temporalType).withDefaultValue(defaultValue).build();
  }

  @Test public void acceptsDateAtEveryGranularity()
  {
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATE, "2026"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATE, "2026-05"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATE, "2026-05-18"));
  }

  @Test public void rejectsTimeSuppliedForDate()
  {
    IllegalStateException ex =
      assertThrows(IllegalStateException.class, () -> build(XsdTemporalDatatype.DATE, "12:00:00"));
    assertTrue(ex.getMessage().contains("xsd:date"), ex.getMessage());
  }

  @Test public void acceptsTimeAtEveryGranularityWithOptionalOffset()
  {
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.TIME, "12"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.TIME, "12:30"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.TIME, "12:30:45"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.TIME, "12:30:45.500"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.TIME, "12:30:45Z"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.TIME, "12:30:45-05:00"));
  }

  @Test public void rejectsDateSuppliedForTime()
  {
    assertThrows(IllegalStateException.class, () -> build(XsdTemporalDatatype.TIME, "2026-05-18"));
  }

  @Test public void acceptsDateTimeWithAndWithoutZoneAndTime()
  {
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATETIME, "2026-05-18"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATETIME, "2026-05-18T12"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATETIME, "2026-05-18T12:00:00"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATETIME, "2026-05-18T12:00:00Z"));
    assertDoesNotThrow(() -> build(XsdTemporalDatatype.DATETIME, "2026-05-18T12:00:00-05:00"));
  }

  @Test public void rejectsTimeOnlyForDateTime()
  {
    assertThrows(IllegalStateException.class, () -> build(XsdTemporalDatatype.DATETIME, "12:00:00"));
  }

  @Test public void rejectsGarbageTemporalValue()
  {
    assertThrows(IllegalStateException.class, () -> build(XsdTemporalDatatype.DATE, "not-a-date"));
  }
}
