package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.TextValueConstraints;

public final class EmailFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public EmailFieldBuilder() {
    fieldUiBuilder.withInputType(FieldInputType.EMAIL);
  }

  public EmailFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public EmailFieldBuilder withDefaultValue(TextDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public EmailFieldBuilder withMinLength(Integer minLength)
  {
    valueConstraintsBuilder.withMinLength(minLength);
    return this;
  }

  public EmailFieldBuilder withMaxLength(Integer maxLength)
  {
    valueConstraintsBuilder.withMinLength(maxLength);
    return this;
  }

  public EmailFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
  {
    fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
    return this;
  }

  public EmailFieldBuilder withHidden(boolean hidden)
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