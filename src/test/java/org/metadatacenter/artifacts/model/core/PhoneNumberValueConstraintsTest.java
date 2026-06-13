package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.PhoneNumberValueConstraints;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneNumberValueConstraintsTest
{
  @Test public void testBuilderWithAllSetters()
  {
    String defaultValue = "+1-555-0123";

    PhoneNumberValueConstraints constraints = PhoneNumberValueConstraints.builder()
      .withDefaultValue(defaultValue)
      .withRequiredValue(true)
      .withRecommendedValue(true)
      .withMultipleChoice(false)
      .build();

    assertEquals(defaultValue, constraints.defaultValue().get().value());
    assertEquals(true, constraints.requiredValue());
    assertEquals(true, constraints.recommendedValue());
    assertEquals(false, constraints.multipleChoice());
  }
}
