package org.metadatacenter.artifacts.model.core.fields.constraints;

import org.metadatacenter.artifacts.model.core.fields.LinkDefaultValue;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;

public non-sealed interface LinkValueConstraints extends ValueConstraints
{
  Optional<LinkDefaultValue> defaultValue();

  static LinkValueConstraints create(Optional<LinkDefaultValue> defaultValue,
    boolean requiredValue, boolean multipleChoice)
  {
    return new LinkValueConstraintsRecord(defaultValue, requiredValue, multipleChoice);
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private Optional<LinkDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean multipleChoice = false;

    private Builder() {
    }

    public Builder withDefaultValue(URI defaultValue) {
      this.defaultValue = Optional.of(new LinkDefaultValue(defaultValue));
      return this;
    }

    public Builder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public Builder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public LinkValueConstraints build()
    {
      return new LinkValueConstraintsRecord(defaultValue, requiredValue, multipleChoice);
    }
  }
}

record LinkValueConstraintsRecord(Optional<LinkDefaultValue> defaultValue, boolean requiredValue, boolean multipleChoice)
  implements LinkValueConstraints
{
  public LinkValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
  }
}
