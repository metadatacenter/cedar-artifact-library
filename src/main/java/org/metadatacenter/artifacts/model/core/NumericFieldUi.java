
package org.metadatacenter.artifacts.model.core;

public non-sealed interface NumericFieldUi extends FieldUi
{
  boolean hidden();

  default boolean valueRecommendationEnabled() { return false; }

  static NumericFieldUi create(boolean hidden)
  {
    return new NumericFieldUiRecord(FieldInputType.NUMERIC, hidden);
  }

  static Builder builder() {
    return new NumericFieldUi.Builder();
  }

  class Builder
  {
    private FieldInputType inputType = FieldInputType.NUMERIC;
    private boolean hidden = false;

    private Builder()
    {
    }

    public Builder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public NumericFieldUi build()
    {
      return new NumericFieldUiRecord(inputType, hidden);
    }
  }
}

record NumericFieldUiRecord(FieldInputType inputType, boolean hidden) implements NumericFieldUi
{
}