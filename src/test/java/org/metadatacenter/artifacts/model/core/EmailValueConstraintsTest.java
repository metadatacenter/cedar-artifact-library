package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.EmailValueConstraints;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailValueConstraintsTest
{
  @Test public void testBuilderWithAllSetters()
  {
    String defaultValue = "alice@example.org";

    EmailValueConstraints constraints = EmailValueConstraints.builder()
      .withDefaultValue(defaultValue)
      .withRequiredValue(false)
      .withRecommendedValue(true)
      .withMultipleChoice(false)
      .build();

    assertEquals(defaultValue, constraints.defaultValue().get().value());
    assertEquals(false, constraints.requiredValue());
    assertEquals(true, constraints.recommendedValue());
    assertEquals(false, constraints.multipleChoice());
  }
}
