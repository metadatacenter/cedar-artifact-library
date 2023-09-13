package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemporalDefaultValue;
import org.metadatacenter.artifacts.model.core.TemporalFieldUi;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.TemporalType;
import org.metadatacenter.artifacts.model.core.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.Version;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class TemporalFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final TemporalFieldUi.Builder fieldUiBuilder = TemporalFieldUi.builder();
  private final TemporalValueConstraints.Builder valueConstraintsBuilder = TemporalValueConstraints.builder();

  public TemporalFieldBuilder() {
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public TemporalFieldBuilder withTemporalType(TemporalType temporalType)
  {
    valueConstraintsBuilder.withTemporalType(temporalType);
    return this;
  }

  public TemporalFieldBuilder withTemporalGranularity(TemporalGranularity temporalGranularity)
  {
    fieldUiBuilder.withTemporalGranularity(temporalGranularity);
    return this;
  }

  public TemporalFieldBuilder withInputTimeFormat(InputTimeFormat inputTimeFormat)
  {
    fieldUiBuilder.withInputTimeFormat(inputTimeFormat);
    return this;
  }

  public TemporalFieldBuilder withTimeZoneEnabled(boolean timeZoneEnabled)
  {
    fieldUiBuilder.withTimeZoneEnabled(timeZoneEnabled);
    return this;
  }

  public TemporalFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public TemporalFieldBuilder withDefaultValue(String defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public TemporalFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public TemporalFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public TemporalFieldBuilder withJsonLdType(URI jsonLdType) {
    super.withJsonLdType(jsonLdType);
    return this;
  }

  @Override public TemporalFieldBuilder withJsonLdId(URI jsonLdId)
  {
    super.withJsonLdId(jsonLdId);
    return this;
  }

  @Override public TemporalFieldBuilder withName(String name)
  {
    super.withName(name);
    return this;
  }

  @Override public TemporalFieldBuilder withDescription(String description)
  {
    super.withDescription(description);
    return this;
  }

  @Override public TemporalFieldBuilder withIdentifier(String identifier)
  {
    super.withIdentifier(identifier);
    return this;
  }

  @Override public TemporalFieldBuilder withModelVersion(Version modelVersion)
  {
    super.withModelVersion(modelVersion);
    return this;
  }

  @Override public TemporalFieldBuilder withVersion(Version version)
  {
    super.withVersion(version);
    return this;
  }

  @Override public TemporalFieldBuilder withStatus(Status status)
  {
    super.withStatus(status);
    return this;
  }

  @Override public TemporalFieldBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  @Override public TemporalFieldBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  @Override public TemporalFieldBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  @Override public TemporalFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  @Override public TemporalFieldBuilder withPreviousVersion(URI previousVersion)
  {
    super.withPreviousVersion(previousVersion);
    return this;
  }

  @Override public TemporalFieldBuilder withDerivedFrom(URI derivedFrom)
  {
    super.withDerivedFrom(derivedFrom);
    return this;
  }

  @Override public TemporalFieldBuilder withSkosPrefLabel(String skosPrefLabel)
  {
    super.withSkosPrefLabel(skosPrefLabel);
    return this;
  }

  @Override public TemporalFieldBuilder withSkosAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withSkosAlternateLabels(skosAlternateLabels);
    return this;
  }

  @Override public TemporalFieldBuilder withIsMultiple(boolean isMultiple)
  {
    super.withIsMultiple(isMultiple);
    return this;
  }

  @Override public TemporalFieldBuilder withMinItems(Integer minItems)
  {
    super.withMinItems(minItems);
    return this;
  }

  @Override public TemporalFieldBuilder withMaxItems(Integer maxItems)
  {
    super.withMaxItems(maxItems);
    return this;
  }

  @Override public TemporalFieldBuilder withPropertyUri(URI propertyUri)
  {
    super.withPropertyUri(propertyUri);
    return this;
  }

  @Override public TemporalFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    super.withJsonSchemaTitle(jsonSchemaTitle);
    return this;
  }

  @Override public TemporalFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
  {
    super.withJsonSchemaDescription(jsonSchemaDescription);
    return this;
  }

  @Override public FieldSchemaArtifact build()
  {
    withFieldUi(fieldUiBuilder.build());
    withValueConstraints(valueConstraintsBuilder.build());
    return super.build();
  }
}