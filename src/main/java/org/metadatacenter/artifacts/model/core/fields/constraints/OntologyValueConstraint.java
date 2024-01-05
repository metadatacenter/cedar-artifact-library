package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACRONYM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;

public record OntologyValueConstraint(URI uri, String acronym, String name, Optional<Integer> numTerms) {

  public OntologyValueConstraint {
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateStringFieldNotNull(this, acronym, VALUE_CONSTRAINTS_ACRONYM);
    validateStringFieldNotNull(this, name, VALUE_CONSTRAINTS_NAME);
    validateOptionalFieldNotNull(this, numTerms, VALUE_CONSTRAINTS_NUM_TERMS);
  }
}
