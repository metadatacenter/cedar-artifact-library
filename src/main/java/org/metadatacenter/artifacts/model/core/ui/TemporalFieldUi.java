package org.metadatacenter.artifacts.model.core.ui;

import org.apache.poi.sl.draw.geom.GuideIf;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;

import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.UI_TEMPORAL_GRANULARITY;

public non-sealed interface TemporalFieldUi extends FieldUi
{
  TemporalGranularity temporalGranularity();

  Optional<InputTimeFormat> inputTimeFormat();

  Optional<Boolean> timezoneEnabled();

  boolean hidden();

  default boolean valueRecommendationEnabled() { return false; }

  static TemporalFieldUi create(TemporalGranularity temporalGranularity,
    Optional<InputTimeFormat> inputTimeFormat, Optional<Boolean> timezoneEnabled,
    boolean hidden, boolean continuePreviousLine)
  {
    return new TemporalFieldUiRecord(FieldInputType.TEMPORAL, temporalGranularity, inputTimeFormat, timezoneEnabled,
      hidden, continuePreviousLine);
  }

  static Builder builder()
  {
    return new TemporalFieldUi.Builder();
  }

  static Builder builder(TemporalFieldUi temporalFieldUi)
  {
    return new TemporalFieldUi.Builder(temporalFieldUi);
  }

  class Builder
  {
    private FieldInputType inputType = FieldInputType.TEMPORAL;
    private TemporalGranularity temporalGranularity;
    private Optional<InputTimeFormat> inputTimeFormat = Optional.empty();
    private Optional<Boolean> timezoneEnabled = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private Builder()
    {
    }

    private Builder(TemporalFieldUi temporalFieldUi)
    {
      this.inputType = temporalFieldUi.inputType();
      this.temporalGranularity = temporalFieldUi.temporalGranularity();
      this.inputTimeFormat = temporalFieldUi.inputTimeFormat();
      this.timezoneEnabled = temporalFieldUi.timezoneEnabled();
      this.hidden = temporalFieldUi.hidden();
      this.continuePreviousLine = temporalFieldUi.continuePreviousLine();
    }

    public Builder withTemporalGranularity(TemporalGranularity temporalGranularity)
    {
      this.temporalGranularity = temporalGranularity;
      return this;
    }

    public Builder withInputTimeFormat(InputTimeFormat inputTimeFormat)
    {
      this.inputTimeFormat = Optional.ofNullable(inputTimeFormat);
      return this;
    }

    public Builder withTimezoneEnabled(boolean timezoneEnabled)
    {
      this.timezoneEnabled = Optional.ofNullable(timezoneEnabled);
      return this;
    }

    public Builder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public Builder withContinuePreviousLine(boolean continuePreviousLine)
    {
      this.continuePreviousLine = continuePreviousLine;
      return this;
    }

    public TemporalFieldUi build()
    {
      return new TemporalFieldUiRecord(inputType, temporalGranularity, inputTimeFormat, timezoneEnabled, hidden,
        continuePreviousLine);
    }
  }
}

record TemporalFieldUiRecord(FieldInputType inputType, TemporalGranularity temporalGranularity,
                             Optional<InputTimeFormat> inputTimeFormat, Optional<Boolean> timezoneEnabled,
                             boolean hidden, boolean continuePreviousLine) implements TemporalFieldUi
{
  public TemporalFieldUiRecord
  {
    if (temporalGranularity == null)
      throw new IllegalStateException("Field " + UI_TEMPORAL_GRANULARITY + " must set for temporal fields in " + this);

    // TODO We could possibly throw an error here rather than silently fix
    if (temporalGranularity == TemporalGranularity.DAY || temporalGranularity == TemporalGranularity.MONTH ||
      temporalGranularity == TemporalGranularity.YEAR) {
      inputTimeFormat = Optional.empty();
      timezoneEnabled = Optional.empty();
    }


    // TODO Test that inputTimeFormat and timeZoneEnables are empty for granularity of day, month, year.

    // TODO Disable for moment until verify with Matthew that he is adding this to temporal fields
    //      if (inputTimeFormat == null)
    //        throw new IllegalStateException(
    //          "Field " + UI_INPUT_TIME_FORMAT + " must be set for temporal fields in " + this);

  }
}