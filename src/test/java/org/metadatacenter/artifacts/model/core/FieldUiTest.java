package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class FieldUiTest
{
  @Test
  public void testGetUiType() {
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .build();
    assertEquals(FieldInputType.TEXTFIELD, fieldUi.inputType());
  }

  @Test
  public void testGetInputType() {
    FieldInputType inputType = FieldInputType.TEXTAREA;
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(inputType)
      .build();
    assertEquals(inputType, fieldUi.inputType());
  }

  @Test
  public void testIsTextField() {
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .build();
    assertTrue(fieldUi.isTextField());
    assertFalse(fieldUi.isTextarea());
  }

  @Test
  public void testIsValueRecommendationEnabled() {
    boolean valueRecommendationEnabled = true;
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .withValueRecommendationEnabled(valueRecommendationEnabled)
      .build();
    assertTrue(fieldUi.valueRecommendationEnabled());
  }
}