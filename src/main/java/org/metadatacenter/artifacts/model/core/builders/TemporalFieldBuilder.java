package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.TemporalDefaultValue;
import org.metadatacenter.artifacts.model.core.TemporalFieldUi;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.TemporalType;
import org.metadatacenter.artifacts.model.core.TemporalValueConstraints;

public final class TemporalFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final TemporalFieldUi.Builder fieldUiBuilder = TemporalFieldUi.builder();
  private final TemporalValueConstraints.Builder valueConstraintsBuilder = TemporalValueConstraints.builder();

  public TemporalFieldBuilder() {
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public TemporalFieldBuilder withTemporalType(TemporalType temporalType)
  {
    valueConstraintsBuilder.withTemporalType(temporalType);
    return this;
  }

  public TemporalFieldBuilder withTemporalGranularity(TemporalGranularity temporalGranularity)
  {
    fieldUiBuilder.withTemporalGranularity(temporalGranularity);
    return this;
  }

  public TemporalFieldBuilder withInputTimeFormat(InputTimeFormat inputTimeFormat)
  {
    fieldUiBuilder.withInputTimeFormat(inputTimeFormat);
    return this;
  }

  public TemporalFieldBuilder withTimeZoneEnabled(boolean timeZoneEnabled)
  {
    fieldUiBuilder.withTimeZoneEnabled(timeZoneEnabled);
    return this;
  }

  public TemporalFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public TemporalFieldBuilder withDefaultValue(TemporalDefaultValue defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public TemporalFieldBuilder withHidden(boolean hidden)
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