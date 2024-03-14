package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class ControlledTermFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public ControlledTermFieldInstanceBuilder() {}

  public ControlledTermFieldInstanceBuilder withValue(URI value)
  {
    super.withJsonLdId(value);
    return this;
  }

  public ControlledTermFieldInstanceBuilder withLabel(String label)
  {
    super.withLabel(label);
    return this;
  }

  public ControlledTermFieldInstanceBuilder withPrefLabel(String prefLabel)
  {
    this.prefLabel = Optional.ofNullable(prefLabel);
    return this;
  }

  public ControlledTermFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public ControlledTermFieldInstanceBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  public ControlledTermFieldInstanceBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  public ControlledTermFieldInstanceBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  public ControlledTermFieldInstanceBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
