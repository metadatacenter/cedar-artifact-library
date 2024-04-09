package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;

public record LinkDefaultValue(@JsonValue URI termUri) implements DefaultValue<URI>
{
  public LinkDefaultValue {
    validateUriFieldNotNull(this, termUri, "termUri");
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.LINK;
  }

  @Override public URI value() { return termUri; }
}
