package org.metadatacenter.artifacts.model.core;

public final class NumericDefaultValue implements DefaultValue<Double>
{
  private Double value;

  public NumericDefaultValue(Double value)
  {
    this.value = value;
  }

  @Override public Double getValue()
  {
    return value;
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.NUMERIC;
  }
}
