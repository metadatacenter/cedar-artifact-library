package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class NumericFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public NumericFieldInstanceBuilder withValue(Number value)
  {
    super.withJsonLdValue(value.toString());
    return this;
  }

  public NumericFieldInstanceBuilder withType(XsdNumericDatatype datatype)
  {
    super.withJsonLdType(datatype.toUri());
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}