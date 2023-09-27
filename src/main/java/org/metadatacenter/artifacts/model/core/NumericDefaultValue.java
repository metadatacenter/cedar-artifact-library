package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;

public record NumericDefaultValue(@JsonValue Number value) implements DefaultValue<Number>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.NUMERIC;
  }
}
