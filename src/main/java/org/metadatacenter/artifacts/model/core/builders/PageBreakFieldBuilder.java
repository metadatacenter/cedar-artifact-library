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

public final class PageBreakFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final StaticFieldUi.PageBreakFieldUiBuilder fieldUiBuilder = StaticFieldUi.pageBreakFieldUiBuilder();

  public PageBreakFieldBuilder() {
    super(ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI);
    withJsonLdContext(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
  }

  public PageBreakFieldBuilder withContent(String content)
  {
    fieldUiBuilder.withContent(content);
    return this;
  }

  public PageBreakFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public PageBreakFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public PageBreakFieldBuilder withJsonLdType(URI jsonLdType) {
    super.withJsonLdType(jsonLdType);
    return this;
  }

  @Override public PageBreakFieldBuilder withJsonLdId(URI jsonLdId)
  {
    super.withJsonLdId(jsonLdId);
    return this;
  }

  @Override public PageBreakFieldBuilder withName(String name)
  {
    super.withName(name);
    return this;
  }

  @Override public PageBreakFieldBuilder withDescription(String description)
  {
    super.withDescription(description);
    return this;
  }

  @Override public PageBreakFieldBuilder withIdentifier(String identifier)
  {
    super.withIdentifier(identifier);
    return this;
  }

  @Override public PageBreakFieldBuilder withModelVersion(Version modelVersion)
  {
    super.withModelVersion(modelVersion);
    return this;
  }

  @Override public PageBreakFieldBuilder withVersion(Version version)
  {
    super.withVersion(version);
    return this;
  }

  @Override public PageBreakFieldBuilder withStatus(Status status)
  {
    super.withStatus(status);
    return this;
  }

  @Override public PageBreakFieldBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  @Override public PageBreakFieldBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  @Override public PageBreakFieldBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  @Override public PageBreakFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  @Override public PageBreakFieldBuilder withPreviousVersion(URI previousVersion)
  {
    super.withPreviousVersion(previousVersion);
    return this;
  }

  @Override public PageBreakFieldBuilder withDerivedFrom(URI derivedFrom)
  {
    super.withDerivedFrom(derivedFrom);
    return this;
  }

  @Override public PageBreakFieldBuilder withPreferredLabel(String skosPrefLabel)
  {
    super.withPreferredLabel(skosPrefLabel);
    return this;
  }

  @Override public PageBreakFieldBuilder withAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withAlternateLabels(skosAlternateLabels);
    return this;
  }

  @Override public PageBreakFieldBuilder withIsMultiple(boolean isMultiple)
  {
    super.withIsMultiple(isMultiple);
    return this;
  }

  @Override public PageBreakFieldBuilder withMinItems(Integer minItems)
  {
    super.withMinItems(minItems);
    return this;
  }

  @Override public PageBreakFieldBuilder withMaxItems(Integer maxItems)
  {
    super.withMaxItems(maxItems);
    return this;
  }

  @Override public PageBreakFieldBuilder withPropertyUri(URI propertyUri)
  {
    super.withPropertyUri(propertyUri);
    return this;
  }

  @Override public PageBreakFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    super.withJsonSchemaTitle(jsonSchemaTitle);
    return this;
  }

  @Override public PageBreakFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
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