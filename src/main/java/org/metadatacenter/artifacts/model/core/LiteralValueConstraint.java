package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;

public record LiteralValueConstraint(String label, @JsonInclude(JsonInclude.Include.NON_DEFAULT) boolean selectedByDefault)
{
  public LiteralValueConstraint
  {
    validateStringFieldNotNull(this, label, VALUE_CONSTRAINTS_LABEL);
  }
}
