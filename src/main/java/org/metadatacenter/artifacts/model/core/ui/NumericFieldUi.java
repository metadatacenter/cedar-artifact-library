
package org.metadatacenter.artifacts.model.core.ui;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

public non-sealed interface NumericFieldUi extends FieldUi
{
  boolean hidden();

  default boolean valueRecommendationEnabled() { return false; }

  static NumericFieldUi create(boolean hidden, boolean continuePreviousLine)
  {
    return new NumericFieldUiRecord(FieldInputType.NUMERIC, hidden, continuePreviousLine);
  }

  static Builder builder() {
    return new NumericFieldUi.Builder();
  }

  static Builder builder(NumericFieldUi numericFieldUi) {
    return new NumericFieldUi.Builder(numericFieldUi);
  }

  class Builder
  {
    private FieldInputType inputType = FieldInputType.NUMERIC;
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private Builder()
    {
    }

    private Builder(NumericFieldUi numericFieldUi)
    {
      this.inputType = numericFieldUi.inputType();
      this.hidden = numericFieldUi.hidden();
      this.continuePreviousLine = numericFieldUi.continuePreviousLine();
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

    public NumericFieldUi build()
    {
      return new NumericFieldUiRecord(inputType, hidden, continuePreviousLine);
    }
  }
}

record NumericFieldUiRecord(FieldInputType inputType, boolean hidden, boolean continuePreviousLine) implements NumericFieldUi
{}