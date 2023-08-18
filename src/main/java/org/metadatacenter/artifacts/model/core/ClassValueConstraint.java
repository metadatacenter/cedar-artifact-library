package org.metadatacenter.artifacts.model.core;

import java.net.URI;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;

public record ClassValueConstraint(URI uri, String prefLabel, String type, String label, String source) {

  public ClassValueConstraint {
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateStringFieldNotNull(this, prefLabel, VALUE_CONSTRAINTS_PREFLABEL);
    validateStringFieldNotNull(this, type, VALUE_CONSTRAINTS_TYPE);
    validateStringFieldNotNull(this, label, VALUE_CONSTRAINTS_LABEL);
    validateStringFieldNotNull(this, source, VALUE_CONSTRAINTS_SOURCE);
  }
}
