package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;

public final class ImageFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final StaticFieldUi.ImageFieldUiBuilder fieldUiBuilder = StaticFieldUi.imageFieldUiBuilder();

  public ImageFieldBuilder() {
    super(ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI);
    withJsonLdContext(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
  }

  public ImageFieldBuilder withContent(String content)
  {
    fieldUiBuilder.withContent(content);
    return this;
  }

  public ImageFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public ImageFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public ImageFieldBuilder withJsonLdType(URI jsonLdType) {
    super.withJsonLdType(jsonLdType);
    return this;
  }

  @Override public ImageFieldBuilder withJsonLdId(URI jsonLdId)
  {
    super.withJsonLdId(jsonLdId);
    return this;
  }

  @Override public ImageFieldBuilder withName(String name)
  {
    super.withName(name);
    return this;
  }

  @Override public ImageFieldBuilder withDescription(String description)
  {
    super.withDescription(description);
    return this;
  }

  @Override public ImageFieldBuilder withIdentifier(String identifier)
  {
    super.withIdentifier(identifier);
    return this;
  }

  @Override public ImageFieldBuilder withModelVersion(Version modelVersion)
  {
    super.withModelVersion(modelVersion);
    return this;
  }

  @Override public ImageFieldBuilder withVersion(Version version)
  {
    super.withVersion(version);
    return this;
  }

  @Override public ImageFieldBuilder withStatus(Status status)
  {
    super.withStatus(status);
    return this;
  }

  @Override public ImageFieldBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  @Override public ImageFieldBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  @Override public ImageFieldBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  @Override public ImageFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  @Override public ImageFieldBuilder withPreviousVersion(URI previousVersion)
  {
    super.withPreviousVersion(previousVersion);
    return this;
  }

  @Override public ImageFieldBuilder withDerivedFrom(URI derivedFrom)
  {
    super.withDerivedFrom(derivedFrom);
    return this;
  }

  @Override public ImageFieldBuilder withPreferredLabel(String skosPrefLabel)
  {
    super.withPreferredLabel(skosPrefLabel);
    return this;
  }

  @Override public ImageFieldBuilder withAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withAlternateLabels(skosAlternateLabels);
    return this;
  }

  @Override public ImageFieldBuilder withIsMultiple(boolean isMultiple)
  {
    super.withIsMultiple(isMultiple);
    return this;
  }

  @Override public ImageFieldBuilder withMinItems(Integer minItems)
  {
    super.withMinItems(minItems);
    return this;
  }

  @Override public ImageFieldBuilder withMaxItems(Integer maxItems)
  {
    super.withMaxItems(maxItems);
    return this;
  }

  @Override public ImageFieldBuilder withPropertyUri(URI propertyUri)
  {
    super.withPropertyUri(propertyUri);
    return this;
  }

  @Override public ImageFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    super.withJsonSchemaTitle(jsonSchemaTitle);
    return this;
  }

  @Override public ImageFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
  {
    super.withJsonSchemaDescription(jsonSchemaDescription);
    return this;
  }

  @Override public FieldSchemaArtifact build()
  {
    withFieldUi(fieldUiBuilder.build());
    return super.build();
  }
}