
package org.metadatacenter.artifacts.model.core.ui;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

public non-sealed interface NumericFieldUi extends FieldUi
{
  boolean hidden();

  default boolean valueRecommendationEnabled() { return false; }

  static NumericFieldUi create(boolean hidden, boolean recommendedValue, boolean continuePreviousLine)
  {
    return new NumericFieldUiRecord(FieldInputType.NUMERIC, hidden, recommendedValue, continuePreviousLine);
  }

  static Builder builder() {
    return new NumericFieldUi.Builder();
  }

  class Builder
  {
    private final FieldInputType inputType = FieldInputType.NUMERIC;
    private boolean hidden = false;
    private boolean recommendedValue = false;
    private boolean continuePreviousLine = false;

    private Builder()
    {
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

    public Builder withRecommendedValue(boolean recommendedValue)
    {
      this.recommendedValue = recommendedValue;
      return this;
    }

    public NumericFieldUi build()
    {
      return new NumericFieldUiRecord(inputType, hidden, recommendedValue, continuePreviousLine);
    }
  }
}

record NumericFieldUiRecord(FieldInputType inputType, boolean hidden, boolean recommendedValue, boolean continuePreviousLine) implements NumericFieldUi
{}