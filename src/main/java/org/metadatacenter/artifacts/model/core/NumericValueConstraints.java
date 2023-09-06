package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DECIMAL_PLACE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_UNIT_OF_MEASURE;

public non-sealed interface NumericValueConstraints extends ValueConstraints
{
  NumberType numberType();

  Optional<Number> minValue();

  Optional<Number> maxValue();

  Optional<Integer> decimalPlaces();

  Optional<String> unitOfMeasure();

  Optional<NumericDefaultValue> defaultValue();

  boolean requiredValue();

  boolean multipleChoice();

  static NumericValueConstraints create(NumberType numberType,
    Optional<Number> minValue, Optional<Number> maxValue,
    Optional<Integer> decimalPlaces, Optional<String> unitOfMeasure, Optional<NumericDefaultValue> defaultValue,
    boolean requiredValue, boolean multipleChoice)
  {
    return new NumericValueConstraintsRecord(numberType, minValue, maxValue, decimalPlaces, unitOfMeasure, defaultValue,
      requiredValue, multipleChoice);
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private NumberType numberType;
    private Optional<Number> minValue = Optional.empty();
    private Optional<Number> maxValue = Optional.empty();
    private Optional<Integer> decimalPlaces = Optional.empty();
    private Optional<String> unitOfMeasure = Optional.empty();
    private Optional<NumericDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean multipleChoice = false;

    private Builder() {
    }

    public Builder withNumberType(NumberType numberType) {
      this.numberType = numberType;
      return this;
    }

    public Builder withMinValue(Number minValue) {
      this.minValue = Optional.ofNullable(minValue);
      return this;
    }

    public Builder withMaxValue(Number maxValue) {
      this.maxValue = Optional.ofNullable(maxValue);
      return this;
    }

    public Builder withDecimalPlaces(Integer decimalPlaces) {
      this.decimalPlaces = Optional.ofNullable(decimalPlaces);
      return this;
    }

    public Builder withUnitOfMeasure(String unitOfMeasure) {
      this.unitOfMeasure = Optional.ofNullable(unitOfMeasure);
      return this;
    }

    public Builder withDefaultValue(NumericDefaultValue defaultValue) {
      this.defaultValue = Optional.ofNullable(defaultValue);
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

    public NumericValueConstraints build()
    {
      return new NumericValueConstraintsRecord(numberType, minValue, maxValue,
        decimalPlaces, unitOfMeasure, defaultValue, requiredValue, multipleChoice);
    }
  }
}

record NumericValueConstraintsRecord(NumberType numberType,
                              Optional<Number> minValue, Optional<Number> maxValue, Optional<Integer> decimalPlaces,
                              Optional<String> unitOfMeasure, Optional<NumericDefaultValue> defaultValue,
                              boolean requiredValue, boolean multipleChoice)
  implements NumericValueConstraints
{

  public NumericValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, unitOfMeasure, VALUE_CONSTRAINTS_UNIT_OF_MEASURE);
    validateOptionalFieldNotNull(this, minValue, VALUE_CONSTRAINTS_MIN_NUMBER_VALUE);
    validateOptionalFieldNotNull(this, maxValue, VALUE_CONSTRAINTS_MAX_NUMBER_VALUE);
    validateOptionalFieldNotNull(this, decimalPlaces, VALUE_CONSTRAINTS_DECIMAL_PLACE);
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
  }
}
