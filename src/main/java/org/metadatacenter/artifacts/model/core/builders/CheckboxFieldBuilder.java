package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.TextValueConstraints;

import java.util.List;

public final class CheckboxFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public CheckboxFieldBuilder() {

    fieldUiBuilder.withInputType(FieldInputType.CHECKBOX);
    valueConstraintsBuilder.withMultipleChoice(true);
  }

  public CheckboxFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public CheckboxFieldBuilder withDefaultValue(TextDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public CheckboxFieldBuilder withLiterals(List<LiteralValueConstraint> literals)
  {
    valueConstraintsBuilder.withLiterals(List.copyOf(literals));
    return this;
  }

  public CheckboxFieldBuilder withHidden(boolean hidden)
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