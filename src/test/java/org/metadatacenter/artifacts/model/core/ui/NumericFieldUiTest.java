package org.metadatacenter.artifacts.model.core.ui;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericFieldUiTest
{
  @Test
  public void testCreate() {

    boolean hidden = false;
    boolean continuePreviousLine = false;

    NumericFieldUi numericFieldUi = NumericFieldUi.builder()
      .withHidden(hidden)
      .withContinuePreviousLine(continuePreviousLine)
      .build();

    assertTrue(numericFieldUi.inputType().isNumeric());
    assertEquals(FieldInputType.NUMERIC, numericFieldUi.inputType());
    assertEquals(hidden, numericFieldUi.hidden());
    assertEquals(continuePreviousLine, numericFieldUi.continuePreviousLine());
  }

  @Test
  public void testHiddenAndContinuePreviousLineSetters()
  {
    NumericFieldUi ui = NumericFieldUi.builder()
      .withHidden(true)
      .withContinuePreviousLine(true)
      .build();

    assertTrue(ui.hidden());
    assertTrue(ui.continuePreviousLine());
  }

  @Test
  public void testCopyBuilderPreservesSettings()
  {
    NumericFieldUi original = NumericFieldUi.builder()
      .withHidden(true)
      .withContinuePreviousLine(true)
      .build();

    NumericFieldUi copy = NumericFieldUi.builder(original).build();

    assertEquals(original.hidden(), copy.hidden());
    assertEquals(original.continuePreviousLine(), copy.continuePreviousLine());
    assertEquals(original.inputType(), copy.inputType());
  }
}
