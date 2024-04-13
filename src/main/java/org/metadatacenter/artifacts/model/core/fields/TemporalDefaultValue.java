package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;

public record TemporalDefaultValue(@JsonValue String value) implements DefaultValue<String>
{
  public TemporalDefaultValue {
    validateStringFieldNotNull(this, value, "value");
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.TEMPORAL;
  }
}
