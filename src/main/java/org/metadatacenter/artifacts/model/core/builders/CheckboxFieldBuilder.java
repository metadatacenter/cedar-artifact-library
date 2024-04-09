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

public final class CheckboxFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public CheckboxFieldBuilder() {
    super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
    withJsonLdContext(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    fieldUiBuilder.withInputType(FieldInputType.CHECKBOX);
    valueConstraintsBuilder.withMultipleChoice(true);
  }

  public CheckboxFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public CheckboxFieldBuilder withDefaultValue(String defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public CheckboxFieldBuilder withOption(String choice, boolean selectedByDefault)
  {
    valueConstraintsBuilder.withChoice(choice, selectedByDefault);
    return this;
  }

  public CheckboxFieldBuilder withOption(String choice)
  {
    valueConstraintsBuilder.withChoice(choice, false);
    return this;
  }

  public CheckboxFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public CheckboxFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public CheckboxFieldBuilder withJsonLdType(URI jsonLdType) {
    super.withJsonLdType(jsonLdType);
    return this;
  }

  @Override public CheckboxFieldBuilder withJsonLdId(URI jsonLdId)
  {
    super.withJsonLdId(jsonLdId);
    return this;
  }

  @Override public CheckboxFieldBuilder withName(String name)
  {
    super.withName(name);
    return this;
  }

  @Override public CheckboxFieldBuilder withDescription(String description)
  {
    super.withDescription(description);
    return this;
  }

  @Override public CheckboxFieldBuilder withIdentifier(String identifier)
  {
    super.withIdentifier(identifier);
    return this;
  }

  @Override public CheckboxFieldBuilder withModelVersion(Version modelVersion)
  {
    super.withModelVersion(modelVersion);
    return this;
  }

  @Override public CheckboxFieldBuilder withVersion(Version version)
  {
    super.withVersion(version);
    return this;
  }

  @Override public CheckboxFieldBuilder withStatus(Status status)
  {
    super.withStatus(status);
    return this;
  }

  @Override public CheckboxFieldBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  @Override public CheckboxFieldBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  @Override public CheckboxFieldBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  @Override public CheckboxFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  @Override public CheckboxFieldBuilder withPreviousVersion(URI previousVersion)
  {
    super.withPreviousVersion(previousVersion);
    return this;
  }

  @Override public CheckboxFieldBuilder withDerivedFrom(URI derivedFrom)
  {
    super.withDerivedFrom(derivedFrom);
    return this;
  }

  @Override public CheckboxFieldBuilder withPreferredLabel(String skosPrefLabel)
  {
    super.withPreferredLabel(skosPrefLabel);
    return this;
  }

  @Override public CheckboxFieldBuilder withAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withAlternateLabels(skosAlternateLabels);
    return this;
  }

  @Override public CheckboxFieldBuilder withIsMultiple(boolean isMultiple)
  {
    super.withIsMultiple(isMultiple);
    return this;
  }

  @Override public CheckboxFieldBuilder withMinItems(Integer minItems)
  {
    super.withMinItems(minItems);
    return this;
  }

  @Override public CheckboxFieldBuilder withMaxItems(Integer maxItems)
  {
    super.withMaxItems(maxItems);
    return this;
  }

  @Override public CheckboxFieldBuilder withPropertyUri(URI propertyUri)
  {
    super.withPropertyUri(propertyUri);
    return this;
  }

  @Override public CheckboxFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    super.withJsonSchemaTitle(jsonSchemaTitle);
    return this;
  }

  @Override public CheckboxFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
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