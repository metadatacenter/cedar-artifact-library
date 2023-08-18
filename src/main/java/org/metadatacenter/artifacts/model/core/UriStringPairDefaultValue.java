package org.metadatacenter.artifacts.model.core;

import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;

public record UriStringPairDefaultValue(Pair<URI, String> value) implements DefaultValue<Pair<URI, String>>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.URI_STRING_PAIR;
  }
}
