package org.metadatacenter.artifacts.model.core;

public record NumericDefaultValue(Double value) implements DefaultValue<Double>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.NUMERIC;
  }
}
