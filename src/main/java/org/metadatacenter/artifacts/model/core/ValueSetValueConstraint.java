package org.metadatacenter.artifacts.model.core;

import java.net.URI;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VS_COLLECTION;

public record ValueSetValueConstraint(String name, String valueSetCollection, URI uri, int numberOfTerms) {

  public ValueSetValueConstraint {
    validateStringFieldNotNull(this, name, VALUE_CONSTRAINTS_NAME);
    validateStringFieldNotNull(this, valueSetCollection, VALUE_CONSTRAINTS_VS_COLLECTION);
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
  }
}
