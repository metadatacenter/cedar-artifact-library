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

  public NumericFieldInstanceBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  public NumericFieldInstanceBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  public NumericFieldInstanceBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  public NumericFieldInstanceBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
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