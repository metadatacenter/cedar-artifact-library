package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.TextField;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public final class TextFieldBuilder extends FieldSchemaArtifactBuilder
{
  private FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

  public TextFieldBuilder() {
    super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
    withJsonLdContext(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    fieldUiBuilder.withInputType(FieldInputType.TEXTFIELD);
    valueConstraintsBuilder.withMultipleChoice(false);
  }

  public TextFieldBuilder(TextField textField)
  {
    super(textField);
  }

  public TextFieldBuilder withRequiredValue(boolean requiredValue)
  {
    valueConstraintsBuilder.withRequiredValue(requiredValue);
    return this;
  }

  public TextFieldBuilder withRecommendedValue(boolean recommendedValue)
  {
    valueConstraintsBuilder.withRecommendedValue(recommendedValue);
    return this;
  }

  public TextFieldBuilder withDefaultValue(String defaultValue)
  {
    valueConstraintsBuilder.withDefaultValue(defaultValue);
    return this;
  }

  public TextFieldBuilder withMinLength(Integer minLength)
  {

    valueConstraintsBuilder.withMinLength(minLength);
    return this;
  }

  public TextFieldBuilder withMaxLength(Integer maxLength)
  {
    valueConstraintsBuilder.withMaxLength(maxLength);
    return this;
  }

  public TextFieldBuilder withValueRecommendation(boolean valueRecommendation)
  {
    fieldUiBuilder.withValueRecommendation(valueRecommendation);
    return this;
  }

  public TextFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  public TextFieldBuilder withRegex(String regex)
  {
    valueConstraintsBuilder.withRegex(regex);
    return this;
  }

  @Override public TextFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public TextFieldBuilder withJsonLdType(URI jsonLdType) {
    super.withJsonLdType(jsonLdType);
    return this;
  }

  @Override public TextFieldBuilder withJsonLdId(URI jsonLdId)
  {
    super.withJsonLdId(jsonLdId);
    return this;
  }

  @Override public TextFieldBuilder withName(String name)
  {
    super.withName(name);
    return this;
  }

  @Override public TextFieldBuilder withDescription(String description)
  {
    super.withDescription(description);
    return this;
  }

  @Override public TextFieldBuilder withIdentifier(String identifier)
  {
    super.withIdentifier(identifier);
    return this;
  }

  @Override public TextFieldBuilder withModelVersion(Version modelVersion)
  {
    super.withModelVersion(modelVersion);
    return this;
  }

  @Override public TextFieldBuilder withVersion(Version version)
  {
    super.withVersion(version);
    return this;
  }

  @Override public TextFieldBuilder withStatus(Status status)
  {
    super.withStatus(status);
    return this;
  }

  @Override public TextFieldBuilder withCreatedBy(URI createdBy)
  {
    super.withCreatedBy(createdBy);
    return this;
  }

  @Override public TextFieldBuilder withModifiedBy(URI modifiedBy)
  {
    super.withModifiedBy(modifiedBy);
    return this;
  }

  @Override public TextFieldBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    super.withCreatedOn(createdOn);
    return this;
  }

  @Override public TextFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    super.withLastUpdatedOn(lastUpdatedOn);
    return this;
  }

  @Override public TextFieldBuilder withPreviousVersion(URI previousVersion)
  {
    super.withPreviousVersion(previousVersion);
    return this;
  }

  @Override public TextFieldBuilder withDerivedFrom(URI derivedFrom)
  {
    super.withDerivedFrom(derivedFrom);
    return this;
  }

  @Override public TextFieldBuilder withPreferredLabel(String skosPrefLabel)
  {
    super.withPreferredLabel(skosPrefLabel);
    return this;
  }

  @Override public TextFieldBuilder withAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withAlternateLabels(skosAlternateLabels);
    return this;
  }

  @Override public TextFieldBuilder withIsMultiple(boolean isMultiple)
  {
    super.withIsMultiple(isMultiple);
    return this;
  }

  @Override public TextFieldBuilder withMinItems(Integer minItems)
  {
    super.withMinItems(minItems);
    return this;
  }

  @Override public TextFieldBuilder withMaxItems(Integer maxItems)
  {
    super.withMaxItems(maxItems);
    return this;
  }

  @Override public TextFieldBuilder withPropertyUri(URI propertyUri)
  {
    super.withPropertyUri(propertyUri);
    return this;
  }

  @Override public TextFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    super.withJsonSchemaTitle(jsonSchemaTitle);
    return this;
  }

  @Override public TextFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
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