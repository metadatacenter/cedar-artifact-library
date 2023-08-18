package org.metadatacenter.artifacts.model.core;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;

public record LiteralValueConstraint(String label, boolean selectedByDefault)
{
  public LiteralValueConstraint
  {
    validateStringFieldNotNull(this, label, VALUE_CONSTRAINTS_LABEL);
  }
}
