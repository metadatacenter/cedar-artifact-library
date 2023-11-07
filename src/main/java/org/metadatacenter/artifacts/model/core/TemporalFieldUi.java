package org.metadatacenter.artifacts.model.core;

import static org.metadatacenter.model.ModelNodeNames.UI_TEMPORAL_GRANULARITY;

public non-sealed interface TemporalFieldUi extends FieldUi
{
  TemporalGranularity temporalGranularity();

  InputTimeFormat inputTimeFormat();

  boolean timezoneEnabled();

  boolean hidden();

  default boolean valueRecommendationEnabled() { return false; }

  static TemporalFieldUi create(TemporalGranularity temporalGranularity,
    InputTimeFormat inputTimeFormat, boolean timezoneEnabled,  boolean hidden)
  {
    return new TemporalFieldUiRecord(FieldInputType.TEMPORAL, temporalGranularity, inputTimeFormat, timezoneEnabled, hidden);
  }

  static Builder builder() {
    return new TemporalFieldUi.Builder();
  }

  class Builder
  {
    private FieldInputType inputType = FieldInputType.TEMPORAL;
    private TemporalGranularity temporalGranularity;
    private InputTimeFormat inputTimeFormat = InputTimeFormat.TWELVE_HOUR;
    boolean timezoneEnabled = false;
    private boolean hidden = false;

    private Builder()
    {
    }

    public Builder withTemporalGranularity(TemporalGranularity temporalGranularity)
    {
      this.temporalGranularity = temporalGranularity;
      return this;
    }

    public Builder withInputTimeFormat(InputTimeFormat inputTimeFormat)
    {
      this.inputTimeFormat = inputTimeFormat;
      return this;
    }

    public Builder withTimezoneEnabled(boolean timezoneEnabled)
    {
      this.timezoneEnabled = timezoneEnabled;
      return this;
    }

    public Builder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public TemporalFieldUi build()
    {
      return new TemporalFieldUiRecord(inputType, temporalGranularity, inputTimeFormat, timezoneEnabled, hidden);
    }
  }
}

record TemporalFieldUiRecord(FieldInputType inputType, TemporalGranularity temporalGranularity, InputTimeFormat inputTimeFormat,
                             boolean timezoneEnabled, boolean hidden) implements TemporalFieldUi
{
  public TemporalFieldUiRecord
  {
    if (temporalGranularity == null)

      throw new IllegalStateException("Field " + UI_TEMPORAL_GRANULARITY + " must set for temporal fields in " + this);

    // TODO Disable for moment until verify with Matthew that he is adding this to temporal fields
    //      if (inputTimeFormat == null)
    //        throw new IllegalStateException(
    //          "Field " + UI_INPUT_TIME_FORMAT + " must be set for temporal fields in " + this);

  }
}