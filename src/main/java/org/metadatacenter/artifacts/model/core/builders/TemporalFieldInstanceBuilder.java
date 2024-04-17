package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemporalFieldInstance;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class TemporalFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public TemporalFieldInstanceBuilder() {}

  public TemporalFieldInstanceBuilder(TemporalFieldInstance temporalFieldInstance) {
    super(temporalFieldInstance);
  }

  public TemporalFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public TemporalFieldInstanceBuilder withType(XsdTemporalDatatype datatype)
  {
    super.withJsonLdType(datatype.toUri());
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}