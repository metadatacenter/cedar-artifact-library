package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.ValueSetValueConstraint;

public final class ControlledTermFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final ControlledTermValueConstraints.Builder valueConstraintsBuilder = ControlledTermValueConstraints.builder();

  public ControlledTermFieldBuilder() {
    valueConstraintsBuilder.withMultipleChoice(false);
    fieldUiBuilder.withInputType(FieldInputType.TEXTFIELD);
  }

  public ControlledTermFieldBuilder withOntologyValueConstraint(OntologyValueConstraint constraint)
  {
    valueConstraintsBuilder.withOntologyValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withValueSetValueConstraint(ValueSetValueConstraint constraint)
  {
    valueConstraintsBuilder.withValueSetValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withClassValueConstraint(ClassValueConstraint constraint)
  {
    valueConstraintsBuilder.withClassValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withBranchValueConstraint(BranchValueConstraint constraint)
  {
    valueConstraintsBuilder.withBranchValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withValueConstraintsAction(ControlledTermValueConstraintsAction action)
  {
    valueConstraintsBuilder.withValueConstraintsAction(action);
    return this;
  }

  public ControlledTermFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public ControlledTermFieldBuilder withDefaultValue(ControlledTermDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public ControlledTermFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
  {
    fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
    return this;
  }

  public ControlledTermFieldBuilder withHidden(boolean hidden)
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