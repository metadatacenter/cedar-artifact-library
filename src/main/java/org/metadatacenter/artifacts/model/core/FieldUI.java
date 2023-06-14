package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

public final class FieldUI implements UI
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

  private FieldUI(Builder builder) {
    this.inputType = builder.inputType;
    this.valueRecommendationEnabled = builder.valueRecommendationEnabled;
    this.hidden = builder.hidden;
    this.timeZoneEnabled = builder.timeZoneEnabled;
    this.temporalGranularity = builder.temporalGranularity;
    this.inputTimeFormat = builder.inputTimeFormat;
  }

  @Override public UIType getUIType() { return UIType.FIELD_UI; }

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

  public boolean isAttributeValue() { return inputType == FieldInputType.ATTRIBUTE_VALUE; }

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

  public static class Builder {
    private FieldInputType inputType;
    private boolean valueRecommendationEnabled;
    private boolean hidden;
    private Optional<Boolean> timeZoneEnabled = Optional.empty();
    private Optional<TemporalGranularity> temporalGranularity = Optional.empty();
    private Optional<InputTimeFormat> inputTimeFormat = Optional.empty();

    private Builder() {
    }

    public Builder withInputType(FieldInputType inputType) {
      this.inputType = inputType;
      return this;
    }

    public Builder withValueRecommendationEnabled(boolean valueRecommendationEnabled) {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
      return this;
    }

    public Builder withHidden(boolean hidden) {
      this.hidden = hidden;
      return this;
    }

    public Builder withTimeZoneEnabled(boolean timeZoneEnabled) {
      this.timeZoneEnabled = Optional.of(timeZoneEnabled);
      return this;
    }

    public Builder withTemporalGranularity(TemporalGranularity temporalGranularity) {
      this.temporalGranularity = Optional.ofNullable(temporalGranularity);
      return this;
    }

    public Builder withInputTimeFormat(InputTimeFormat inputTimeFormat) {
      this.inputTimeFormat = Optional.ofNullable(inputTimeFormat);
      return this;
    }

    public FieldUI build() {
      return new FieldUI(this);
    }
  }
}


