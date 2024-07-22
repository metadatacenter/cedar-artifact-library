package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
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

public sealed interface TemporalField extends FieldSchemaArtifact
{
  static TemporalField create(String jsonSchemaTitle,
    String jsonSchemaDescription, LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, List<String> alternateLabels,
    Optional<String> language, TemporalFieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
    Optional<Annotations> annotations)
  {
    return new TemporalFieldRecord(jsonSchemaTitle,
      jsonSchemaDescription, jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
      status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
      createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
  }

  static TemporalFieldBuilder builder() { return new TemporalFieldBuilder(); }

  static TemporalFieldBuilder builder(TemporalField temporalField) { return new TemporalFieldBuilder(temporalField); }

  final class TemporalFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private final TemporalFieldUi.TemporalFieldUiBuilder fieldUiBuilder;
    private final TemporalValueConstraints.TemporalValueConstraintsBuilder valueConstraintsBuilder;

    public TemporalFieldBuilder() {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      this.fieldUiBuilder = TemporalFieldUi.builder();
      this.valueConstraintsBuilder = TemporalValueConstraints.builder();
    }

    public TemporalFieldBuilder(TemporalField temporalField)
    {
      super(temporalField);

      this.fieldUiBuilder = TemporalFieldUi.builder(temporalField.fieldUi().asTemporalFieldUi());
      if (temporalField.valueConstraints().isPresent())
        this.valueConstraintsBuilder = TemporalValueConstraints.builder(temporalField.valueConstraints().get().asTemporalValueConstraints());
      else
        this.valueConstraintsBuilder = TemporalValueConstraints.builder();
    }

    public TemporalFieldBuilder withTemporalType(
      XsdTemporalDatatype temporalType)
    {
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      valueConstraintsBuilder.withTemporalType(temporalType);
      return this;
    }

    public TemporalFieldBuilder withTemporalGranularity(
      TemporalGranularity temporalGranularity)
    {
      fieldUiBuilder.withTemporalGranularity(temporalGranularity);
      return this;
    }

    public TemporalFieldBuilder withInputTimeFormat(
      InputTimeFormat inputTimeFormat)
    {
      fieldUiBuilder.withInputTimeFormat(inputTimeFormat);
      return this;
    }

    public TemporalFieldBuilder withTimeZoneEnabled(boolean timeZoneEnabled)
    {
      fieldUiBuilder.withTimezoneEnabled(timeZoneEnabled);
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

    @Override public TemporalFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public TemporalFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    @Override public TemporalFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public TemporalFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public TemporalFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
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

    @Override public TemporalFieldBuilder withPreferredLabel(String preferredLabel)
    {
      super.withPreferredLabel(preferredLabel);
      return this;
    }

    @Override public TemporalFieldBuilder withAlternateLabels(List<String> alternateLabels)
    {
      super.withAlternateLabels(alternateLabels);
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

    @Override public TemporalFieldBuilder withLanguage(String language)
    {
      super.withLanguage(language);
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

    @Override public TemporalFieldBuilder withAnnotations(Annotations annotations)
    {
      super.withAnnotations(annotations);
      return this;
    }

    public TemporalField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonSchemaTitle, jsonSchemaDescription, jsonLdContext,
        jsonLdTypes, jsonLdId, name, description, identifier, version, status, previousVersion,
        derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi.asTemporalFieldUi(), valueConstraints, annotations);
    }
  }
}

record TemporalFieldRecord(String jsonSchemaTitle, String jsonSchemaDescription,
                           LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                           String name, String description, Optional<String> identifier,
                           Optional<Version> version, Optional<Status> status,
                           Optional<URI> previousVersion, Optional<URI> derivedFrom,
                           boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                           Optional<URI> propertyUri,
                           Optional<URI> createdBy, Optional<URI> modifiedBy,
                           Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                           Optional<String> preferredLabel, List<String> alternateLabels,
                           Optional<String> language, TemporalFieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
                           Optional<Annotations> annotations)
  implements TemporalField
{
  public TemporalFieldRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(this, jsonLdTypes, JSON_LD_TYPE,
      Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, alternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(this, propertyUri, "propertyUri"); // TODO Add to ModelNodeNames
    validateOptionalFieldNotNull(this, language,  "language");
    validateUiFieldNotNull(this, fieldUi, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(this, annotations, "annotations");

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name);

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name);

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be lass than maxItems in element schema artifact " + name);

    if (fieldUi.isStatic())
      jsonLdContext = new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    else
      jsonLdContext = new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}