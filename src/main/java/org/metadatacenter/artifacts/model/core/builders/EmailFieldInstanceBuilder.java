package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class EmailFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public EmailFieldInstanceBuilder() {}

  public EmailFieldInstanceBuilder(FieldInstanceArtifact fieldInstanceArtifact) {
    super(fieldInstanceArtifact);
  }

  public EmailFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
