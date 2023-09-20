package org.metadatacenter.artifacts.model.core;

import java.net.URI;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateIntegerFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACRONYM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;

public record BranchValueConstraint(URI uri, String source, String acronym, String name, Integer maxDepth) {

  public BranchValueConstraint
  {
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateStringFieldNotNull(this, source, VALUE_CONSTRAINTS_SOURCE);
    validateStringFieldNotNull(this, acronym, VALUE_CONSTRAINTS_ACRONYM);
    validateStringFieldNotNull(this, name, VALUE_CONSTRAINTS_NAME);
    validateIntegerFieldNotNull(this, maxDepth, VALUE_CONSTRAINTS_NAME);
  }
}

