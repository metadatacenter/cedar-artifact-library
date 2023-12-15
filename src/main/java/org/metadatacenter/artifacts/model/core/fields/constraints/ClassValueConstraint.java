package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.net.URI;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;

public record ClassValueConstraint(URI uri, String source, String label, String prefLabel, ValueType type) {

  public ClassValueConstraint {
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateStringFieldNotNull(this, prefLabel, VALUE_CONSTRAINTS_PREFLABEL);
    validateStringFieldNotNull(this, label, VALUE_CONSTRAINTS_LABEL);
    validateStringFieldNotNull(this, source, VALUE_CONSTRAINTS_SOURCE);
  }
}
