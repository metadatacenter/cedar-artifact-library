package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class LinkFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public LinkFieldInstanceBuilder() {}

  public LinkFieldInstanceBuilder withValue(URI value)
  {
    super.withJsonLdId(value);
    return this;
  }

  public LinkFieldInstanceBuilder withLabel(String label)
  {
    super.withLabel(label);
    return this;
  }

  public LinkFieldInstanceBuilder withPrefLabel(String prefLabel)
  {
    this.prefLabel = Optional.ofNullable(prefLabel);
    return this;
  }

  public LinkFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public LinkFieldInstanceBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  public LinkFieldInstanceBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  public LinkFieldInstanceBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  public LinkFieldInstanceBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
