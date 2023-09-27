package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;

public final class AttributeValueFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();

  public AttributeValueFieldBuilder() {
    withJsonLdContext(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    fieldUiBuilder.withInputType(FieldInputType.ATTRIBUTE_VALUE);
  }

  @Override public AttributeValueFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public AttributeValueFieldBuilder withJsonLdType(URI jsonLdType) {
    super.withJsonLdType(jsonLdType);
    return this;
  }

  @Override public AttributeValueFieldBuilder withJsonLdId(URI jsonLdId)
  {
    super.withJsonLdId(jsonLdId);
    return this;
  }

  @Override public AttributeValueFieldBuilder withName(String name)
  {
    super.withName(name);
    return this;
  }

  @Override public AttributeValueFieldBuilder withDescription(String description)
  {
    super.withDescription(description);
    return this;
  }

  @Override public AttributeValueFieldBuilder withIdentifier(String identifier)
  {
    super.withIdentifier(identifier);
    return this;
  }

  @Override public AttributeValueFieldBuilder withModelVersion(Version modelVersion)
  {
    super.withModelVersion(modelVersion);
    return this;
  }

  @Override public AttributeValueFieldBuilder withVersion(Version version)
  {
    super.withVersion(version);
    return this;
  }

  @Override public AttributeValueFieldBuilder withStatus(Status status)
  {
    super.withStatus(status);
    return this;
  }

  @Override public AttributeValueFieldBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  @Override public AttributeValueFieldBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  @Override public AttributeValueFieldBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  @Override public AttributeValueFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  @Override public AttributeValueFieldBuilder withPreviousVersion(URI previousVersion)
  {
    super.withPreviousVersion(previousVersion);
    return this;
  }

  @Override public AttributeValueFieldBuilder withDerivedFrom(URI derivedFrom)
  {
    super.withDerivedFrom(derivedFrom);
    return this;
  }

  @Override public AttributeValueFieldBuilder withPreferredLabel(String skosPrefLabel)
  {
    super.withPreferredLabel(skosPrefLabel);
    return this;
  }

  @Override public AttributeValueFieldBuilder withSkosAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withSkosAlternateLabels(skosAlternateLabels);
    return this;
  }

  @Override public AttributeValueFieldBuilder withIsMultiple(boolean isMultiple)
  {
    super.withIsMultiple(isMultiple);
    return this;
  }

  @Override public AttributeValueFieldBuilder withMinItems(Integer minItems)
  {
    super.withMinItems(minItems);
    return this;
  }

  @Override public AttributeValueFieldBuilder withMaxItems(Integer maxItems)
  {
    super.withMaxItems(maxItems);
    return this;
  }

  @Override public AttributeValueFieldBuilder withPropertyUri(URI propertyUri)
  {
    super.withPropertyUri(propertyUri);
    return this;
  }

  @Override public AttributeValueFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    super.withJsonSchemaTitle(jsonSchemaTitle);
    return this;
  }

  @Override public AttributeValueFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
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