package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

public non-sealed interface TemporalValueConstraints extends ValueConstraints
{
  TemporalType temporalType();

  boolean requiredValue();

  boolean multipleChoice();

  Optional<TemporalDefaultValue> defaultValue();

  static TemporalValueConstraints create(TemporalType temporalType, Optional<TemporalDefaultValue> defaultValue,
    boolean requiredValue, boolean multipleChoice)
  {
    return new TemporalValueConstraintsRecord(temporalType, defaultValue, requiredValue, multipleChoice);
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private TemporalType temporalType;
    private Optional<TemporalDefaultValue> defaultValue;
    private boolean requiredValue = false;
    private boolean multipleChoice = false;

    private Builder() {
    }

    public Builder withTemporalType(TemporalType temporalType) {
      this.temporalType = temporalType;
      return this;
    }

    public Builder withDefaultValue(String defaultValue) {
      this.defaultValue = Optional.of(new TemporalDefaultValue(defaultValue));
      return this;
    }

    public Builder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public Builder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public TemporalValueConstraints build()
    {
      return new TemporalValueConstraintsRecord(temporalType, defaultValue, requiredValue, multipleChoice);
    }
  }
}

record TemporalValueConstraintsRecord(TemporalType temporalType, Optional<TemporalDefaultValue> defaultValue,
                                      boolean requiredValue, boolean multipleChoice)
  implements TemporalValueConstraints
{
}
