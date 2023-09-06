package org.metadatacenter.artifacts.model.core;

public record TextDefaultValue(String value) implements DefaultValue<String>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.TEXT;
  }
}
