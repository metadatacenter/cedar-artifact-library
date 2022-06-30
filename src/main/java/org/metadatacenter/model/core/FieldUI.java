package org.metadatacenter.model.core;

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


