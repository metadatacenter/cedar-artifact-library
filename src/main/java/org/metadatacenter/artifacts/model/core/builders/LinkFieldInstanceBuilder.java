package org.metadatacenter.artifacts.model.core.builders;

import org.apache.poi.sl.draw.geom.GuideIf;
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

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
