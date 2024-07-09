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

  static EmailValueConstraintsBuilder builder() {
    return new EmailValueConstraintsBuilder();
  }

  static EmailValueConstraintsBuilder builder(EmailValueConstraints emailValueConstraints) {
    return new EmailValueConstraintsBuilder(emailValueConstraints);
  }

  final class EmailValueConstraintsBuilder implements ValueConstraintsBuilder
  {
    private Optional<EmailDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private EmailValueConstraintsBuilder() {
    }

    private EmailValueConstraintsBuilder(EmailValueConstraints emailValueConstraints) {
      this.defaultValue = emailValueConstraints.defaultValue();
      this.requiredValue = emailValueConstraints.requiredValue();
      this.recommendedValue = emailValueConstraints.recommendedValue();
      this.multipleChoice = emailValueConstraints.multipleChoice();
    }

    public EmailValueConstraintsBuilder withDefaultValue(String defaultValue) {
      this.defaultValue = Optional.of(new EmailDefaultValue(defaultValue));
      return this;
    }

    public EmailValueConstraintsBuilder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public EmailValueConstraintsBuilder withRecommendedValue(boolean recommendedValue) {
      this.recommendedValue = recommendedValue;
      return this;
    }

    public EmailValueConstraintsBuilder withMultipleChoice(boolean multipleChoice) {
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
