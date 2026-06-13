package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateNumberFieldNotNull;

public record NumericDefaultValue(Number value) implements DefaultValue<Number>
{
  public NumericDefaultValue
  {
    validateNumberFieldNotNull(this, value, "value");
  }

  // The CEDAR JSON Schema for _valueConstraints.defaultValue allows only a string or a
  // URI/label object; a bare JSON number is rejected by the validator. Serialize the
  // numeric default as its string representation. The reader recognises numeric strings
  // when the enclosing field input type is NUMERIC and re-hydrates a NumericDefaultValue.
  @JsonValue
  public String jsonValue()
  {
    return value.toString();
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.NUMERIC;
  }

  // Compare by the canonical string form so that two NumericDefaultValues holding
  // numerically equal but differently boxed Numbers (e.g. Integer 7 vs Long 7) are
  // treated as equal. Without this, JSON round-trips fail equality checks because the
  // reader emits Long for integral and Double for non-integral, regardless of the
  // boxed subtype the caller originally supplied.
  @Override public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof NumericDefaultValue other)) return false;
    return value.toString().equals(other.value.toString());
  }

  @Override public int hashCode()
  {
    return value.toString().hashCode();
  }
}
