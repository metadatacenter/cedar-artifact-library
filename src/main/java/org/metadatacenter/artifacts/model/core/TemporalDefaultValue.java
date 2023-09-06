package org.metadatacenter.artifacts.model.core;

public record TemporalDefaultValue(String value) implements DefaultValue<String>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.TEMPORAL;
  }
}
