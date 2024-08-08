package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;

public record PhoneNumberDefaultValue(@JsonValue String value) implements DefaultValue<String>
{
  public PhoneNumberDefaultValue {
    validateStringFieldNotNull(this, value, "value");
    validateStringFieldNotEmpty(this, value, "value");
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.PHONE_NUMBER;
  }
}
