package org.metadatacenter.artifacts.model.core;

public record StringDefaultValue(String value) implements DefaultValue<String>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.STRING;
  }
}
