package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;

public record ControlledTermDefaultValue(URI termUri, @JsonProperty("rdfs:label") String label)
  implements DefaultValue<Pair<URI, String>>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.CONTROLLED_TERM;
  }

  @Override public Pair<URI, String> value() { return new ImmutablePair<>(termUri, label); }
}
