package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class PhoneNumberFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public PhoneNumberFieldInstanceBuilder() {}

  public PhoneNumberFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public PhoneNumberFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public PhoneNumberFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }


  public PhoneNumberFieldInstanceBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  public PhoneNumberFieldInstanceBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  public PhoneNumberFieldInstanceBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  public PhoneNumberFieldInstanceBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
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
