package org.metadatacenter.artifacts.model.core.ui;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

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
  public void testValueRecommendation() {
    boolean valueRecommendation = true;
    boolean hidden = true;

    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .withValueRecommendationEnabled(valueRecommendation)
      .withHidden(hidden)
      .build();

    assertEquals(valueRecommendation, fieldUi.valueRecommendationEnabled());
    assertEquals(hidden, fieldUi.hidden());
  }
}