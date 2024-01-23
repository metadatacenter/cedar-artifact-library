package org.metadatacenter.artifacts.model.core.ui;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class NumericFieldUiTest
{
  @Test
  public void testCreate() {

    boolean hidden = false;
    boolean recommendedValue = false;
    boolean continuePreviousLine = false;

    NumericFieldUi numericFieldUi = NumericFieldUi.builder()
      .withHidden(hidden)
      .withRecommendedValue(recommendedValue)
      .withContinuePreviousLine(continuePreviousLine)
      .build();

    assertTrue(numericFieldUi.inputType().isNumeric());
    assertEquals(FieldInputType.NUMERIC, numericFieldUi.inputType());
    assertEquals(hidden, numericFieldUi.hidden());
    assertEquals(continuePreviousLine, numericFieldUi.continuePreviousLine());
    assertEquals(recommendedValue, numericFieldUi.recommendedValue());
  }

}