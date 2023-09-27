package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;

public record TextDefaultValue(@JsonValue String value) implements DefaultValue<String>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.TEXT;
  }
}
