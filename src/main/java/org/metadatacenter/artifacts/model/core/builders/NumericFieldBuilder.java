package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.NumberType;
import org.metadatacenter.artifacts.model.core.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.NumericFieldUi;
import org.metadatacenter.artifacts.model.core.NumericValueConstraints;

public final class NumericFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final NumericFieldUi.Builder fieldUiBuilder = NumericFieldUi.builder();
  private final NumericValueConstraints.Builder valueConstraintsBuilder = NumericValueConstraints.builder();

  public NumericFieldBuilder() {
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public NumericFieldBuilder withNumberType(NumberType numberType) {
    valueConstraintsBuilder.withNumberType(numberType);
    return this;
  }

  public NumericFieldBuilder withMinValue(Number minValue) {
    valueConstraintsBuilder.withMinValue(minValue);
    return this;
  }

  public NumericFieldBuilder withMaxValue(Number maxValue) {
    valueConstraintsBuilder.withMaxValue(maxValue);
    return this;
  }

  public NumericFieldBuilder withDecimalPlaces(Integer decimalPlaces) {
    valueConstraintsBuilder.withDecimalPlaces(decimalPlaces);
    return this;
  }

  public NumericFieldBuilder withUnitOfMeasure(String unitOfMeasure) {
    valueConstraintsBuilder.withUnitOfMeasure(unitOfMeasure);
    return this;
  }

  public NumericFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public NumericFieldBuilder withDefaultValue(NumericDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public NumericFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public FieldSchemaArtifact build()
  {
    withFieldUi(fieldUiBuilder.build());
    withValueConstraints(valueConstraintsBuilder.build());
    return super.build();
  }
}