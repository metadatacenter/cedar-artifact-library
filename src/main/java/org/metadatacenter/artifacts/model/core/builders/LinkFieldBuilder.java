package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;

public final class LinkFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final ControlledTermValueConstraints.Builder valueConstraintsBuilder = ControlledTermValueConstraints.builder();

  public LinkFieldBuilder() {

    fieldUiBuilder.withInputType(FieldInputType.LINK);
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public LinkFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public LinkFieldBuilder withDefaultValue(ControlledTermDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public LinkFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
  {
    fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
    return this;
  }

  public LinkFieldBuilder withHidden(boolean hidden)
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