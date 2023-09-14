package org.metadatacenter.artifacts.model.core;

public record NumericDefaultValue(Number value) implements DefaultValue<Number>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.NUMERIC;
  }
}
