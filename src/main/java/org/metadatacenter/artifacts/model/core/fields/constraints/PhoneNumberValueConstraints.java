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

  static PhoneNumberValueConstraintsBuilder builder() {
    return new PhoneNumberValueConstraintsBuilder();
  }

  static PhoneNumberValueConstraintsBuilder builder(PhoneNumberValueConstraints phoneNumberValueConstraints) {
    return new PhoneNumberValueConstraintsBuilder(phoneNumberValueConstraints);
  }

  final class PhoneNumberValueConstraintsBuilder implements ValueConstraintsBuilder
  {
    private Optional<PhoneNumberDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean recommendedValue = false;
    private boolean multipleChoice = false;

    private PhoneNumberValueConstraintsBuilder() {
    }

    private PhoneNumberValueConstraintsBuilder(PhoneNumberValueConstraints phoneNumberValueConstraints) {
      this.defaultValue = phoneNumberValueConstraints.defaultValue();
      this.requiredValue = phoneNumberValueConstraints.requiredValue();
      this.recommendedValue = phoneNumberValueConstraints.recommendedValue();
      this.multipleChoice = phoneNumberValueConstraints.multipleChoice();
    }

    public PhoneNumberValueConstraintsBuilder withDefaultValue(String defaultValue) {
      this.defaultValue = Optional.of(new PhoneNumberDefaultValue(defaultValue));
      return this;
    }

    public PhoneNumberValueConstraintsBuilder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public PhoneNumberValueConstraintsBuilder withRecommendedValue(boolean recommendedValue) {
      this.recommendedValue = recommendedValue;
      return this;
    }

    public PhoneNumberValueConstraintsBuilder withMultipleChoice(boolean multipleChoice) {
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
