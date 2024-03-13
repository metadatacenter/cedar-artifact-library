package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class EmailFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public EmailFieldInstanceBuilder() {}

  public EmailFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public EmailFieldInstanceBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  public EmailFieldInstanceBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  public EmailFieldInstanceBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  public EmailFieldInstanceBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    if (jsonLdValue.isEmpty())
      jsonLdValue = Optional.of(null);

    return super.build();
  }
}
