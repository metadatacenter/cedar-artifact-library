package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldUiTest
{
  @Test
  public void testUiType() {
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .build();

    assertEquals(FieldInputType.TEXTFIELD, fieldUi.inputType());
  }

  @Test
  public void testInputType() {
    FieldInputType inputType = FieldInputType.TEXTAREA;
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(inputType)
      .build();

    assertEquals(inputType, fieldUi.inputType());
  }

  @Test
  public void testTextField() {
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .build();

    assertTrue(fieldUi.isTextField());
    assertFalse(fieldUi.isTextarea());
  }

  @Test
  public void testValueRecommendationEnabled() {
    boolean valueRecommendationEnabled = true;
    boolean hidden = true;

    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .withValueRecommendationEnabled(valueRecommendationEnabled)
      .withHidden(hidden)
      .build();

    assertEquals(valueRecommendationEnabled, fieldUi.valueRecommendationEnabled());
    assertEquals(hidden, fieldUi.hidden());
  }
}