package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TERM_URI;

public record ControlledTermDefaultValue(URI termUri, @JsonProperty("rdfs:label") String label)
  implements DefaultValue<Pair<URI, String>>
{
  public ControlledTermDefaultValue {
    validateUriFieldNotNull(this, termUri, VALUE_CONSTRAINTS_TERM_URI);
    validateStringFieldNotNull(this, label, VALUE_CONSTRAINTS_LABEL);
    validateStringFieldNotEmpty(this, label, VALUE_CONSTRAINTS_LABEL);
  }

  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.CONTROLLED_TERM;
  }

  @Override public Pair<URI, String> value() { return new ImmutablePair<>(termUri, label); }
}
