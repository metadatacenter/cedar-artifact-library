package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.PhoneNumberFieldInstance;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class PhoneNumberFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public PhoneNumberFieldInstanceBuilder() {}

  public PhoneNumberFieldInstanceBuilder(PhoneNumberFieldInstance phoneNumberFieldInstance) {
    super(phoneNumberFieldInstance);
  }

  public PhoneNumberFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
