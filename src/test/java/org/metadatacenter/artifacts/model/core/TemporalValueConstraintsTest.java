package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
