package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_TEMPORAL_GRANULARITY;

public non-sealed interface TemporalFieldUi extends FieldUi
{
  default FieldInputType inputType() { return FieldInputType.TEMPORAL; }

  TemporalGranularity temporalGranularity();

  Optional<InputTimeFormat> inputTimeFormat();

  boolean timeZoneEnabled();

  boolean hidden();

  default boolean valueRecommendationEnabled() { return false; }

  static TemporalFieldUi create(TemporalGranularity temporalGranularity,
    Optional<InputTimeFormat> inputTimeFormat, boolean timeZoneEnabled,  boolean hidden)
  {
    return new TemporalFieldUiRecord(temporalGranularity, inputTimeFormat, timeZoneEnabled, hidden);
  }

  static Builder builder() {
    return new TemporalFieldUi.Builder();
  }

  class Builder
  {
    private TemporalGranularity temporalGranularity;
    private Optional<InputTimeFormat> inputTimeFormat;
    boolean timeZoneEnabled = false;
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
      this.inputTimeFormat = Optional.of(inputTimeFormat);
      return this;
    }

    public Builder withTimeZoneEnabled(boolean timeZoneEnabled)
    {
      this.timeZoneEnabled = timeZoneEnabled;
      return this;
    }

    public Builder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public TemporalFieldUi build()
    {
      return new TemporalFieldUiRecord(temporalGranularity, inputTimeFormat, timeZoneEnabled, hidden);
    }
  }
}

record TemporalFieldUiRecord(TemporalGranularity temporalGranularity, Optional<InputTimeFormat> inputTimeFormat,
                             boolean timeZoneEnabled, boolean hidden) implements TemporalFieldUi
{
  public TemporalFieldUiRecord
  {
    validateOptionalFieldNotNull(this, inputTimeFormat, ModelNodeNames.UI_INPUT_TIME_FORMAT);
    if (temporalGranularity == null)

      throw new IllegalStateException("Field " + UI_TEMPORAL_GRANULARITY + " must set for temporal fields in " + this);

    // TODO Disable for moment until verify with Matthew that he is adding this to temporal fields
    //      if (inputTimeFormat == null)
    //        throw new IllegalStateException(
    //          "Field " + UI_INPUT_TIME_FORMAT + " must be set for temporal fields in " + this);

  }
}