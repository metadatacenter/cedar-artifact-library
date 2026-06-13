package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;

public record TemporalDefaultValue(@JsonValue String value) implements DefaultValue<String>
{
  public TemporalDefaultValue {
    validateStringFieldNotNull(this, value, "value");
    validateStringFieldNotEmpty(this, value, "value");
    // Format is validated by TemporalValueConstraints, which carries the XsdTemporalDatatype this
    // bare value does not; here a temporal default can only be checked for presence.
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.TEMPORAL;
  }
}
