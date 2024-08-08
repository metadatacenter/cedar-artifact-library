
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

  static NumericFieldUiBuilder builder() {
    return new NumericFieldUiBuilder();
  }

  static NumericFieldUiBuilder builder(NumericFieldUi numericFieldUi) {
    return new NumericFieldUiBuilder(numericFieldUi);
  }

  final class NumericFieldUiBuilder implements FieldUiBuilder
  {
    private FieldInputType inputType = FieldInputType.NUMERIC;
    private boolean hidden = false;
    private boolean continuePreviousLine = false;
    private boolean valueRecommendationEnabled;

    private NumericFieldUiBuilder()
    {
    }

    private NumericFieldUiBuilder(NumericFieldUi numericFieldUi)
    {
      this.inputType = numericFieldUi.inputType();
      this.hidden = numericFieldUi.hidden();
      this.continuePreviousLine = numericFieldUi.continuePreviousLine();
    }

    public NumericFieldUiBuilder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public NumericFieldUiBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      this.continuePreviousLine = continuePreviousLine;
      return this;
    }

    public NumericFieldUiBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
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