package org.metadatacenter.artifacts.model.core;

import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;

public final class UriStringPairDefaultValue implements DefaultValue<Pair<URI, String>>
{
  private Pair<URI, String> value;

  public UriStringPairDefaultValue(Pair<URI, String> value)
  {
    this.value = value;
  }

  @Override public Pair<URI, String> getValue()
  {
    return value;
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.URI_STRING_PAIR;
  }
}
