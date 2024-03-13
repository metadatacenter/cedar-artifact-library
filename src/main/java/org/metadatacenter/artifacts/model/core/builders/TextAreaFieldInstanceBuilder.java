package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class TextAreaFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public TextAreaFieldInstanceBuilder() {}

  public TextAreaFieldInstanceBuilder withValue(String jsonLdValue)
  {
    super.withJsonLdValue(jsonLdValue);
    return this;
  }

  public TextAreaFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public TextAreaFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }

  public TextAreaFieldInstanceBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  public TextAreaFieldInstanceBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  public TextAreaFieldInstanceBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  public TextAreaFieldInstanceBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
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
