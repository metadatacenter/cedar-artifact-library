package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Optional;

public class FieldUITest {

  @Test
  public void testGetUIType() {
    FieldUI fieldUI = FieldUI.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .build();
    assertEquals(UIType.FIELD_UI, fieldUI.getUIType());
  }

  @Test
  public void testGetInputType() {
    FieldInputType inputType = FieldInputType.TEXTAREA;
    FieldUI fieldUI = FieldUI.builder()
      .withInputType(inputType)
      .build();
    assertEquals(inputType, fieldUI.getInputType());
  }

  @Test
  public void testIsTextField() {
    FieldUI fieldUI = FieldUI.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .build();
    assertTrue(fieldUI.isTextField());
    assertFalse(fieldUI.isTextarea());
  }

  // Add more tests for the other isX() methods

  @Test
  public void testIsValueRecommendationEnabled() {
    boolean valueRecommendationEnabled = true;
    FieldUI fieldUI = FieldUI.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .withValueRecommendationEnabled(valueRecommendationEnabled)
      .build();
    assertTrue(fieldUI.isValueRecommendationEnabled());
  }

  @Test
  public void testIsHidden() {
    boolean hidden = true;
    FieldUI fieldUI = FieldUI.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .withHidden(hidden)
      .build();
    assertTrue(fieldUI.isHidden());
  }

  @Test
  public void testGetTimeZoneEnabled() {
    boolean timeZoneEnabled = true;
    FieldUI fieldUI = FieldUI.builder()
      .withInputType(FieldInputType.TEMPORAL)
      .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR)
      .withTemporalGranularity(TemporalGranularity.SECOND)
      .withTimeZoneEnabled(timeZoneEnabled)
      .build();
    assertEquals(Optional.of(timeZoneEnabled), fieldUI.getTimeZoneEnabled());
  }

  @Test(expected = IllegalStateException.class)
  public void testInputTypeNotSet() {
   FieldUI.builder().build();
  }
}