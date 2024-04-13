package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;

public record ControlledTermDefaultValue(URI termUri, @JsonProperty("rdfs:label") String label)
  implements DefaultValue<Pair<URI, String>>
{
  public ControlledTermDefaultValue {
    validateUriFieldNotNull(this, termUri, "termUri");
    validateStringFieldNotNull(this, label, "label");
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.CONTROLLED_TERM;
  }

  @Override public Pair<URI, String> value() { return new ImmutablePair<>(termUri, label); }
}
