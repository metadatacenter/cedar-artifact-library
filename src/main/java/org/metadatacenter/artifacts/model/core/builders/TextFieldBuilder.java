package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.TextValueConstraints;

public final class TextFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public TextFieldBuilder() {
    fieldUiBuilder.withInputType(FieldInputType.TEXTFIELD);
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public TextFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public TextFieldBuilder withDefaultValue(TextDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public TextFieldBuilder withMinLength(Integer minLength)
  {
    valueConstraintsBuilder.withMinLength(minLength);
    return this;
  }

  public TextFieldBuilder withMaxLength(Integer maxLength)
  {
    valueConstraintsBuilder.withMinLength(maxLength);
    return this;
  }

  public TextFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
  {
    fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
    return this;
  }

  public TextFieldBuilder withHidden(boolean hidden)
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