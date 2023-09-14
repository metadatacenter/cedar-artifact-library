package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VS_COLLECTION;

public record ValueSetValueConstraint(URI uri, String vsCollection, String name, Optional<Integer> numberOfTerms) {

  public ValueSetValueConstraint {
    validateStringFieldNotNull(this, name, VALUE_CONSTRAINTS_NAME);
    validateStringFieldNotNull(this, vsCollection, VALUE_CONSTRAINTS_VS_COLLECTION);
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateOptionalFieldNotNull(this, numberOfTerms, VALUE_CONSTRAINTS_NUM_TERMS);
  }
}
