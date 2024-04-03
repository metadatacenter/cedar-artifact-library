package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;

public record LinkDefaultValue(@JsonValue URI termUri) implements DefaultValue<URI>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.LINK;
  }

  @Override public URI value() { return termUri; }
}
