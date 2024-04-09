package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateNumberFieldNotNull;

public record NumericDefaultValue(@JsonValue Number value) implements DefaultValue<Number>
{
  public NumericDefaultValue
  {
    validateNumberFieldNotNull(this, value, "value");
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.NUMERIC;
  }
}
