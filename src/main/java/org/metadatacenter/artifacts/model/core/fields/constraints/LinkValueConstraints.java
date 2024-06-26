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
    boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  {
    return new LinkValueConstraintsRecord(defaultValue, requiredValue, recommendedValue, multipleChoice);
  }

  static Builder builder() {
    return new Builder();
  }

  static Builder builder(LinkValueConstraints linkValueConstraints) {
    return new Builder(linkValueConstraints);
  }

  class Builder {
    private Optional<LinkDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private Builder() {
    }

    private Builder(LinkValueConstraints linkValueConstraints) {
      this.defaultValue = linkValueConstraints.defaultValue();
      this.requiredValue = linkValueConstraints.requiredValue();
      this.recommendedValue = linkValueConstraints.recommendedValue();
      this.multipleChoice = linkValueConstraints.multipleChoice();
    }

    public Builder withDefaultValue(URI defaultValue) {
      this.defaultValue = Optional.of(new LinkDefaultValue(defaultValue));
      return this;
    }

    public Builder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public Builder withRecommendedValue(boolean recommendedValue) {
      this.recommendedValue = recommendedValue;
      return this;
    }

    public Builder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public LinkValueConstraints build()
    {
      return new LinkValueConstraintsRecord(defaultValue, requiredValue, recommendedValue, multipleChoice);
    }
  }
}

record LinkValueConstraintsRecord(Optional<LinkDefaultValue> defaultValue, boolean requiredValue,
                                  boolean recommendedValue, boolean multipleChoice)
  implements LinkValueConstraints
{
  public LinkValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
  }
}
