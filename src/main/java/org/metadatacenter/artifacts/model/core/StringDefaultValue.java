package org.metadatacenter.artifacts.model.core;

public final class StringDefaultValue implements DefaultValue<String>
{
  private String value;
  public StringDefaultValue(String value)
  {
    this.value = value;
  }

  @Override public String getValue()
  {
    return value;
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.STRING;
  }
}
