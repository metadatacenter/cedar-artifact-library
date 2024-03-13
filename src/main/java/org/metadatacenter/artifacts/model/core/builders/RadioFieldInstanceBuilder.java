package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class RadioFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public RadioFieldInstanceBuilder() {}

  public RadioFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public RadioFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public RadioFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }

  public RadioFieldInstanceBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  public RadioFieldInstanceBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  public RadioFieldInstanceBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  public RadioFieldInstanceBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
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
