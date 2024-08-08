package org.metadatacenter.artifacts.model.core.fields.constraints;

public sealed interface ValueConstraintsBuilder permits
  TextValueConstraints.TextValueConstraintsBuilder,
  NumericValueConstraints.NumericValueConstraintsBuilder,
  ControlledTermValueConstraints.ControlledTermValueConstraintsBuilder,
  TemporalValueConstraints.TemporalValueConstraintsBuilder,
  LinkValueConstraints.LinkValueConstraintsBuilder,
  EmailValueConstraints.EmailValueConstraintsBuilder,
  PhoneNumberValueConstraints.PhoneNumberValueConstraintsBuilder
{
  ValueConstraintsBuilder withRequiredValue(boolean requiredValue);

  ValueConstraintsBuilder withRecommendedValue(boolean recommendedValue);

  static ValueConstraintsBuilder builder(ValueConstraints valueConstraints)
  {
    // TODO Use typesafe switch when available
    if (valueConstraints instanceof TextValueConstraints)
      return TextValueConstraints.builder(valueConstraints.asTextValueConstraints());
    else if (valueConstraints instanceof NumericValueConstraints)
      return NumericValueConstraints.builder(valueConstraints.asNumericValueConstraints());
    else if (valueConstraints instanceof ControlledTermValueConstraints)
      return ControlledTermValueConstraints.builder(valueConstraints.asControlledTermValueConstraints());
    else if (valueConstraints instanceof TemporalValueConstraints)
      return TemporalValueConstraints.builder(valueConstraints.asTemporalValueConstraints());
    else if (valueConstraints instanceof LinkValueConstraints)
      return LinkValueConstraints.builder(valueConstraints.asLinkValueConstraints());
    else if (valueConstraints instanceof EmailValueConstraints)
      return EmailValueConstraints.builder(valueConstraints.asEmailValueConstraints());
    else if (valueConstraints instanceof PhoneNumberValueConstraints)
      return PhoneNumberValueConstraints.builder(valueConstraints.asPhoneNumberValueConstraints());
    else
      throw new IllegalArgumentException("class " + valueConstraints.getClass().getName() + " has no known builder");
  }

  ValueConstraints build();
}
