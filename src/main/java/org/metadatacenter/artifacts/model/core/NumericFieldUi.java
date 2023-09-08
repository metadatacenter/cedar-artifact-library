
package org.metadatacenter.artifacts.model.core;

public non-sealed interface NumericFieldUi extends FieldUi
{
  default FieldInputType inputType() { return FieldInputType.NUMERIC; }

  boolean hidden();

  default boolean valueRecommendationEnabled() { return false; }

  static NumericFieldUi create(boolean hidden)
  {
    return new NumericFieldUiRecord(hidden);
  }

  static Builder builder() {
    return new NumericFieldUi.Builder();
  }

  class Builder
  {
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
      return new NumericFieldUiRecord(hidden);
    }
  }
}

record NumericFieldUiRecord(boolean hidden) implements NumericFieldUi
{
}