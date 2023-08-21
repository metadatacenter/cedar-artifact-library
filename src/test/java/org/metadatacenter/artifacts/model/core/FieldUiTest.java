package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Optional;

public class FieldUiTest
{

  @Test
  public void testGetUIType() {
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .build();
    assertEquals(UiType.FIELD_UI, fieldUi.getUiType());
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

  // Add more tests for the other isX() methods

  @Test
  public void testIsValueRecommendationEnabled() {
    boolean valueRecommendationEnabled = true;
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .withValueRecommendationEnabled(valueRecommendationEnabled)
      .build();
    assertTrue(fieldUi.isValueRecommendationEnabled());
  }

  @Test
  public void testIsHidden() {
    boolean hidden = true;
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEXTFIELD)
      .withHidden(hidden)
      .build();
    assertTrue(fieldUi.isHidden());
  }

  @Test
  public void testGetTimeZoneEnabled() {
    boolean timeZoneEnabled = true;
    FieldUi fieldUi = FieldUi.builder()
      .withInputType(FieldInputType.TEMPORAL)
      .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR)
      .withTemporalGranularity(TemporalGranularity.SECOND)
      .withTimeZoneEnabled(timeZoneEnabled)
      .build();
    assertEquals(Optional.of(timeZoneEnabled), fieldUi.getTimeZoneEnabled());
  }

  @Test(expected = IllegalStateException.class)
  public void testInputTypeNotSet() {
   FieldUi.builder().build();
  }
}