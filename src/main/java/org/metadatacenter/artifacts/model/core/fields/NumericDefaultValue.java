package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;
import org.metadatacenter.artifacts.model.core.fields.DefaultValueType;

public record NumericDefaultValue(@JsonValue Number value) implements DefaultValue<Number>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.NUMERIC;
  }
}
