package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldContainsAll;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContainsOneOf;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;

public sealed interface PhoneNumberField extends FieldSchemaArtifact
{
  static PhoneNumberField create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Version modelVersion, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
    FieldUi fieldUi, Optional<ValueConstraints> valueConstraints)
  {
    return new PhoneNumberFieldRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle,
      jsonSchemaDescription, jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version,
      status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
      createdOn, lastUpdatedOn, skosPrefLabel, skosAlternateLabels, fieldUi, valueConstraints);
  }

  static PhoneNumberFieldBuilder builder() { return new PhoneNumberFieldBuilder(); }

  static PhoneNumberFieldBuilder builder(PhoneNumberField phoneNumberField) {
    return new PhoneNumberFieldBuilder(phoneNumberField);
  }

  final class PhoneNumberFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
    private final TextValueConstraints.Builder valueConstraintsBuilder = TextValueConstraints.builder();

    public PhoneNumberFieldBuilder() {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
      fieldUiBuilder.withInputType(FieldInputType.PHONE_NUMBER);
      valueConstraintsBuilder.withMultipleChoice(false);
    }

    public PhoneNumberFieldBuilder(PhoneNumberField phoneNumberField)
    {
      super(phoneNumberField);
    }

    public PhoneNumberFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    public PhoneNumberFieldBuilder withDefaultValue(String defaultValue)
    {
      valueConstraintsBuilder.withDefaultValue(defaultValue);
      return this;
    }

    public PhoneNumberFieldBuilder withMinLength(Integer minLength)
    {
      valueConstraintsBuilder.withMinLength(minLength);
      return this;
    }

    public PhoneNumberFieldBuilder withMaxLength(Integer maxLength)
    {
      valueConstraintsBuilder.withMaxLength(maxLength);
      return this;
    }

    public PhoneNumberFieldBuilder withValueRecommendation(boolean valueRecommendation)
    {
      fieldUiBuilder.withValueRecommendation(valueRecommendation);
      return this;
    }

    public PhoneNumberFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
    {
      super.withJsonLdContext(jsonLdContext);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withJsonLdType(URI jsonLdType) {
      super.withJsonLdType(jsonLdType);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withJsonLdId(URI jsonLdId)
    {
      super.withJsonLdId(jsonLdId);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withName(String name)
    {
      super.withName(name);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withDescription(String description)
    {
      super.withDescription(description);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withIdentifier(String identifier)
    {
      super.withIdentifier(identifier);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withModelVersion(Version modelVersion)
    {
      super.withModelVersion(modelVersion);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withVersion(Version version)
    {
      super.withVersion(version);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withStatus(Status status)
    {
      super.withStatus(status);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withCreatedBy(URI createdBy)
    {
      super.withCreatedBy(createdBy);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withModifiedBy(URI modifiedBy)
    {
      super.withModifiedBy(modifiedBy);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withCreatedOn(OffsetDateTime createdOn)
    {
      super.withCreatedOn(createdOn);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      super.withLastUpdatedOn(lastUpdatedOn);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withPreviousVersion(URI previousVersion)
    {
      super.withPreviousVersion(previousVersion);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withDerivedFrom(URI derivedFrom)
    {
      super.withDerivedFrom(derivedFrom);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withPreferredLabel(String skosPrefLabel)
    {
      super.withPreferredLabel(skosPrefLabel);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withAlternateLabels(List<String> skosAlternateLabels)
    {
      super.withAlternateLabels(skosAlternateLabels);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withIsMultiple(boolean isMultiple)
    {
      super.withIsMultiple(isMultiple);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withMinItems(Integer minItems)
    {
      super.withMinItems(minItems);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withMaxItems(Integer maxItems)
    {
      super.withMaxItems(maxItems);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withPropertyUri(URI propertyUri)
    {
      super.withPropertyUri(propertyUri);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
    {
      super.withJsonSchemaTitle(jsonSchemaTitle);
      return this;
    }

    @Override public PhoneNumberFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
    {
      super.withJsonSchemaDescription(jsonSchemaDescription);
      return this;
    }

    public PhoneNumberField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, jsonLdContext,
        jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status, previousVersion,
        derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        skosPrefLabel, skosAlternateLabels, fieldUi, valueConstraints);
    }
  }
}

record PhoneNumberFieldRecord(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
                              Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                              String name, String description, Optional<String> identifier,
                              Version modelVersion, Optional<Version> version, Optional<Status> status,
                              Optional<URI> previousVersion, Optional<URI> derivedFrom,
                              boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                              Optional<URI> propertyUri,
                              Optional<URI> createdBy, Optional<URI> modifiedBy,
                              Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                              Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
                              FieldUi fieldUi, Optional<ValueConstraints> valueConstraints)
  implements PhoneNumberField
{
  public PhoneNumberFieldRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(this, jsonLdTypes, JSON_LD_TYPE,
      Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateOptionalFieldNotNull(this, skosPrefLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, skosAlternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(this, propertyUri, "propertyUri"); // TODO Add to ModelNodeNames
    validateUiFieldNotNull(this, fieldUi, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name);

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name);

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be lass than maxItems in element schema artifact " + name);

    if (fieldUi.isStatic())
      validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    else
      validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}