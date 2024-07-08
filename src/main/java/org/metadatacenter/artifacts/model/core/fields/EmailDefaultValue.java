package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;

public record EmailDefaultValue(@JsonValue String value) implements DefaultValue<String>
{
  public EmailDefaultValue {
    validateStringFieldNotNull(this, value, "value");
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.EMAIL;
  }
}
