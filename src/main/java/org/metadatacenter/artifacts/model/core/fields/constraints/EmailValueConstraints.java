package org.metadatacenter.artifacts.model.core.fields.constraints;

import org.metadatacenter.artifacts.model.core.fields.EmailDefaultValue;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;

public non-sealed interface EmailValueConstraints extends ValueConstraints
{
  Optional<EmailDefaultValue> defaultValue();

  static EmailValueConstraints create(Optional<EmailDefaultValue> defaultValue,
    boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  {
    return new EmailValueConstraintsRecord(defaultValue, requiredValue, recommendedValue, multipleChoice);
  }

  static Builder builder() {
    return new Builder();
  }

  static Builder builder(EmailValueConstraints emailValueConstraints) {
    return new Builder(emailValueConstraints);
  }

  class Builder {
    private Optional<EmailDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private Builder() {
    }

    private Builder(EmailValueConstraints emailValueConstraints) {
      this.defaultValue = emailValueConstraints.defaultValue();
      this.requiredValue = emailValueConstraints.requiredValue();
      this.recommendedValue = emailValueConstraints.recommendedValue();
      this.multipleChoice = emailValueConstraints.multipleChoice();
    }

    public Builder withDefaultValue(String defaultValue) {
      this.defaultValue = Optional.of(new EmailDefaultValue(defaultValue));
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

    public EmailValueConstraints build()
    {
      return new EmailValueConstraintsRecord(defaultValue, requiredValue, recommendedValue, multipleChoice);
    }
  }
}

record EmailValueConstraintsRecord(Optional<EmailDefaultValue> defaultValue, boolean requiredValue,
                                  boolean recommendedValue, boolean multipleChoice)
  implements EmailValueConstraints
{
  public EmailValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
  }
}
