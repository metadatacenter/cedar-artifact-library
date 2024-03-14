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

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
