package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public final class EmailFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public EmailFieldBuilder() {
    super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
    withJsonLdContext(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    fieldUiBuilder.withInputType(FieldInputType.EMAIL);
  }

  public EmailFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public EmailFieldBuilder withDefaultValue(String defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public EmailFieldBuilder withMinLength(Integer minLength)
  {
    valueConstraintsBuilder.withMinLength(minLength);
    return this;
  }

  public EmailFieldBuilder withMaxLength(Integer maxLength)
  {
    valueConstraintsBuilder.withMaxLength(maxLength);
    return this;
  }

  public EmailFieldBuilder withValueRecommendation(boolean valueRecommendation)
  {
    fieldUiBuilder.withValueRecommendation(valueRecommendation);
    return this;
  }

  public EmailFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public EmailFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public EmailFieldBuilder withJsonLdType(URI jsonLdType) {
    super.withJsonLdType(jsonLdType);
    return this;
  }

  @Override public EmailFieldBuilder withJsonLdId(URI jsonLdId)
  {
    super.withJsonLdId(jsonLdId);
    return this;
  }

  @Override public EmailFieldBuilder withName(String name)
  {
    super.withName(name);
    return this;
  }

  @Override public EmailFieldBuilder withDescription(String description)
  {
    super.withDescription(description);
    return this;
  }

  @Override public EmailFieldBuilder withIdentifier(String identifier)
  {
    super.withIdentifier(identifier);
    return this;
  }

  @Override public EmailFieldBuilder withModelVersion(Version modelVersion)
  {
    super.withModelVersion(modelVersion);
    return this;
  }

  @Override public EmailFieldBuilder withVersion(Version version)
  {
    super.withVersion(version);
    return this;
  }

  @Override public EmailFieldBuilder withStatus(Status status)
  {
    super.withStatus(status);
    return this;
  }

  @Override public EmailFieldBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  @Override public EmailFieldBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  @Override public EmailFieldBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  @Override public EmailFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  @Override public EmailFieldBuilder withPreviousVersion(URI previousVersion)
  {
    super.withPreviousVersion(previousVersion);
    return this;
  }

  @Override public EmailFieldBuilder withDerivedFrom(URI derivedFrom)
  {
    super.withDerivedFrom(derivedFrom);
    return this;
  }

  @Override public EmailFieldBuilder withPreferredLabel(String skosPrefLabel)
  {
    super.withPreferredLabel(skosPrefLabel);
    return this;
  }

  @Override public EmailFieldBuilder withAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withAlternateLabels(skosAlternateLabels);
    return this;
  }

  @Override public EmailFieldBuilder withIsMultiple(boolean isMultiple)
  {
    super.withIsMultiple(isMultiple);
    return this;
  }

  @Override public EmailFieldBuilder withMinItems(Integer minItems)
  {
    super.withMinItems(minItems);
    return this;
  }

  @Override public EmailFieldBuilder withMaxItems(Integer maxItems)
  {
    super.withMaxItems(maxItems);
    return this;
  }

  @Override public EmailFieldBuilder withPropertyUri(URI propertyUri)
  {
    super.withPropertyUri(propertyUri);
    return this;
  }

  @Override public EmailFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    super.withJsonSchemaTitle(jsonSchemaTitle);
    return this;
  }

  @Override public EmailFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
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