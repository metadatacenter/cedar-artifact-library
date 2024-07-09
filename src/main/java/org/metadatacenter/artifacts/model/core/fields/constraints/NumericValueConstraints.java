package org.metadatacenter.artifacts.model.core.fields.constraints;

import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateXsdNumericDatatypeFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DECIMAL_PLACE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_UNIT_OF_MEASURE;

public non-sealed interface NumericValueConstraints extends ValueConstraints
{
  XsdNumericDatatype numberType();

  Optional<Number> minValue();

  Optional<Number> maxValue();

  Optional<Integer> decimalPlace();

  Optional<String> unitOfMeasure();

  Optional<NumericDefaultValue> defaultValue();

  static NumericValueConstraints create(XsdNumericDatatype numericType,
    Optional<Number> minValue, Optional<Number> maxValue,
    Optional<Integer> decimalPlaces, Optional<String> unitOfMeasure, Optional<NumericDefaultValue> defaultValue,
    boolean requiredValue, boolean  recommendedValue, boolean multipleChoice)
  {
    return new NumericValueConstraintsRecord(numericType, minValue, maxValue, decimalPlaces, unitOfMeasure, defaultValue,
      requiredValue, recommendedValue, multipleChoice);
  }

  static NumericValueConstraintsBuilder builder() {
    return new NumericValueConstraintsBuilder();
  }

  static NumericValueConstraintsBuilder builder(NumericValueConstraints numericValueConstraints) {
    return new NumericValueConstraintsBuilder(numericValueConstraints);
  }

  final class NumericValueConstraintsBuilder implements ValueConstraintsBuilder
  {
    private XsdNumericDatatype numberType;
    private Optional<Number> minValue = Optional.empty();
    private Optional<Number> maxValue = Optional.empty();
    private Optional<Integer> decimalPlaces = Optional.empty();
    private Optional<String> unitOfMeasure = Optional.empty();
    private Optional<NumericDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private NumericValueConstraintsBuilder() {
    }

    private NumericValueConstraintsBuilder(NumericValueConstraints numericValueConstraints)
    {
      this.numberType = numericValueConstraints.numberType();
      this.minValue = numericValueConstraints.minValue();
      this.maxValue = numericValueConstraints.maxValue();
      this.decimalPlaces = numericValueConstraints.decimalPlace();
      this.unitOfMeasure = numericValueConstraints.unitOfMeasure();
      this.defaultValue = numericValueConstraints.defaultValue();
      this.requiredValue = numericValueConstraints.requiredValue();
      this.recommendedValue = numericValueConstraints.recommendedValue();
      this.multipleChoice = numericValueConstraints.multipleChoice();
    }

    public NumericValueConstraintsBuilder withNumberType(XsdNumericDatatype numberType) {
      this.numberType = numberType;
      return this;
    }

    public NumericValueConstraintsBuilder withMinValue(Number minValue) {
      this.minValue = Optional.ofNullable(minValue);
      return this;
    }

    public NumericValueConstraintsBuilder withMaxValue(Number maxValue) {
      this.maxValue = Optional.ofNullable(maxValue);
      return this;
    }

    public NumericValueConstraintsBuilder withDecimalPlaces(Integer decimalPlaces) {
      this.decimalPlaces = Optional.ofNullable(decimalPlaces);
      return this;
    }

    public NumericValueConstraintsBuilder withUnitOfMeasure(String unitOfMeasure) {
      this.unitOfMeasure = Optional.ofNullable(unitOfMeasure);
      return this;
    }

    public NumericValueConstraintsBuilder withDefaultValue(Number defaultValue) {
      this.defaultValue = Optional.of(new NumericDefaultValue(defaultValue));
      return this;
    }

    public NumericValueConstraintsBuilder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public NumericValueConstraintsBuilder withRecommendedValue(boolean recommendedValue) {
      this.recommendedValue = recommendedValue;
      return this;
    }

    public NumericValueConstraintsBuilder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public NumericValueConstraints build()
    {
      return new NumericValueConstraintsRecord(numberType, minValue, maxValue,
        decimalPlaces, unitOfMeasure, defaultValue, requiredValue, recommendedValue, multipleChoice);
    }
  }
}

record NumericValueConstraintsRecord(XsdNumericDatatype numberType,
                                     Optional<Number> minValue, Optional<Number> maxValue, Optional<Integer> decimalPlace,
                                     Optional<String> unitOfMeasure, Optional<NumericDefaultValue> defaultValue,
                                     boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  implements NumericValueConstraints
{

  public NumericValueConstraintsRecord
  {
    validateXsdNumericDatatypeFieldNotNull(this, numberType, VALUE_CONSTRAINTS_NUMBER_TYPE);
    validateOptionalFieldNotNull(this, unitOfMeasure, VALUE_CONSTRAINTS_UNIT_OF_MEASURE);
    validateOptionalFieldNotNull(this, minValue, VALUE_CONSTRAINTS_MIN_NUMBER_VALUE);
    validateOptionalFieldNotNull(this, maxValue, VALUE_CONSTRAINTS_MAX_NUMBER_VALUE);
    validateOptionalFieldNotNull(this, decimalPlace, VALUE_CONSTRAINTS_DECIMAL_PLACE);
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
  }
}
