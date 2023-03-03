package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import java.util.Optional;

public class FieldUI
{
  private final FieldInputType inputType;
  private final boolean valueRecommendationEnabled;
  private final boolean hidden;
  private final Optional<Boolean> timeZoneEnabled;
  private final Optional<TemporalGranularity> temporalGranularity;
  private final Optional<InputTimeFormat> inputTimeFormat;

  public FieldUI(FieldInputType inputType, boolean valueRecommendationEnabled, boolean hidden,
    Optional<Boolean> timeZoneEnabled, Optional<TemporalGranularity> temporalGranularity,
    Optional<InputTimeFormat> inputTimeFormat)
  {
    this.inputType = inputType;
    this.valueRecommendationEnabled = valueRecommendationEnabled;
    this.hidden = hidden;
    this.timeZoneEnabled = timeZoneEnabled;
    this.temporalGranularity = temporalGranularity;
    this.inputTimeFormat = inputTimeFormat;
  }

  public FieldInputType getInputType()
  {
    return inputType;
  }

  public boolean isTextField() { return inputType == FieldInputType.TEXTFIELD; }
  public boolean isTextarea() { return inputType == FieldInputType.TEXTAREA; }
  public boolean isRadio() { return inputType == FieldInputType.RADIO; }
  public boolean isCheckbox() { return inputType == FieldInputType.CHECKBOX; }
  public boolean isTemporal() { return inputType == FieldInputType.TEMPORAL; }
  public boolean isEmail() { return inputType == FieldInputType.EMAIL; }
  public boolean isList() { return inputType == FieldInputType.LIST; }
  public boolean isNumeric() { return inputType == FieldInputType.NUMERIC; }
  public boolean isPhoneNumber() { return inputType == FieldInputType.PHONE_NUMBER; }
  public boolean isSectionBreak() { return inputType == FieldInputType.SECTION_BREAK; }
  public boolean isRichText() { return inputType == FieldInputType.RICHTEXT; }
  public boolean isImage() { return inputType == FieldInputType.IMAGE; }
  public boolean isLink() { return inputType == FieldInputType.LINK; }
  public boolean isYouTube() { return inputType == FieldInputType.YOUTUBE; }

  public boolean isValueRecommendationEnabled()
  {
    return valueRecommendationEnabled;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public Optional<Boolean> getTimeZoneEnabled()
  {
    return timeZoneEnabled;
  }

  public Optional<TemporalGranularity> getTemporalGranularity()
  {
    return temporalGranularity;
  }

  public Optional<InputTimeFormat> getInputTimeFormat()
  {
    return inputTimeFormat;
  }

  @Override public String toString()
  {
    return "FieldUI{" + "inputType=" + inputType + ", valueRecommendationEnabled=" + valueRecommendationEnabled
      + ", hidden=" + hidden + ", timeZoneEnabled=" + timeZoneEnabled + ", temporalGranularity=" + temporalGranularity
      + ", inputTimeFormat=" + inputTimeFormat + '}';
  }
}


