package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.TextValueConstraints;

public final class TextAreaFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public TextAreaFieldBuilder() {
    fieldUiBuilder.withInputType(FieldInputType.TEXTAREA);
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public TextAreaFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public TextAreaFieldBuilder withMinLength(Integer minLength)
  {
    valueConstraintsBuilder.withMinLength(minLength);
    return this;
  }

  public TextAreaFieldBuilder withMaxLength(Integer maxLength)
  {
    valueConstraintsBuilder.withMinLength(maxLength);
    return this;
  }

  public TextAreaFieldBuilder withHidden(boolean hidden)
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