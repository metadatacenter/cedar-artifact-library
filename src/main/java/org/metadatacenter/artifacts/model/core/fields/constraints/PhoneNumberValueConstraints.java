package org.metadatacenter.artifacts.model.core.fields.constraints;

import org.metadatacenter.artifacts.model.core.fields.PhoneNumberDefaultValue;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;

public non-sealed interface PhoneNumberValueConstraints extends ValueConstraints
{
  Optional<PhoneNumberDefaultValue> defaultValue();

  static PhoneNumberValueConstraints create(Optional<PhoneNumberDefaultValue> defaultValue,
    boolean requiredValue, boolean recommendedValue, boolean multipleChoice)
  {
    return new PhoneNumberValueConstraintsRecord(defaultValue, requiredValue, recommendedValue, multipleChoice);
  }

  static Builder builder() {
    return new Builder();
  }

  static Builder builder(PhoneNumberValueConstraints phoneNumberValueConstraints) {
    return new Builder(phoneNumberValueConstraints);
  }

  class Builder {
    private Optional<PhoneNumberDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private Builder() {
    }

    private Builder(PhoneNumberValueConstraints phoneNumberValueConstraints) {
      this.defaultValue = phoneNumberValueConstraints.defaultValue();
      this.requiredValue = phoneNumberValueConstraints.requiredValue();
      this.recommendedValue = phoneNumberValueConstraints.recommendedValue();
      this.multipleChoice = phoneNumberValueConstraints.multipleChoice();
    }

    public Builder withDefaultValue(String defaultValue) {
      this.defaultValue = Optional.of(new PhoneNumberDefaultValue(defaultValue));
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

    public PhoneNumberValueConstraints build()
    {
      return new PhoneNumberValueConstraintsRecord(defaultValue, requiredValue, recommendedValue, multipleChoice);
    }
  }
}

record PhoneNumberValueConstraintsRecord(Optional<PhoneNumberDefaultValue> defaultValue, boolean requiredValue,
                                   boolean recommendedValue, boolean multipleChoice)
  implements PhoneNumberValueConstraints
{
  public PhoneNumberValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
  }
}
