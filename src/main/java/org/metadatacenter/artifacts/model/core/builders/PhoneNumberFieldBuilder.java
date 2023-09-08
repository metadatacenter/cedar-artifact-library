package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.TextValueConstraints;

public final class PhoneNumberFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public PhoneNumberFieldBuilder() {
    fieldUiBuilder.withInputType(FieldInputType.PHONE_NUMBER);
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public PhoneNumberFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public PhoneNumberFieldBuilder withDefaultValue(TextDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public PhoneNumberFieldBuilder withMinLength(Integer minLength)
  {
    valueConstraintsBuilder.withMinLength(minLength);
    return this;
  }

  public PhoneNumberFieldBuilder withMaxLength(Integer maxLength)
  {
    valueConstraintsBuilder.withMinLength(maxLength);
    return this;
  }

  public PhoneNumberFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
  {
    fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
    return this;
  }

  public PhoneNumberFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public FieldSchemaArtifact build()
  {
    withFieldUi(fieldUiBuilder.build());
    withValueConstraints(valueConstraintsBuilder.build());
    return super.build();
  }
}