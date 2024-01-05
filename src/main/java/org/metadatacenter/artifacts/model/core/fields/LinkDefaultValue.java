package org.metadatacenter.artifacts.model.core.fields;

import java.net.URI;

public record LinkDefaultValue(URI termUri) implements DefaultValue<URI>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.LINK;
  }

  @Override public URI value() { return termUri; }
}
