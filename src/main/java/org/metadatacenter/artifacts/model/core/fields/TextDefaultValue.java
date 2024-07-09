package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;

public record TextDefaultValue(@JsonValue String value) implements DefaultValue<String>
{
  public TextDefaultValue {
    validateStringFieldNotNull(this, value, "value");
    // TODO Add this when we have removed all empties from production: validateStringFieldNotEmpty(this, value, "value");
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.TEXT;
  }
}
