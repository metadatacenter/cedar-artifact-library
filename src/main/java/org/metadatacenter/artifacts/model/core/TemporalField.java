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

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public sealed interface TemporalField extends FieldSchemaArtifact
{
  static TemporalField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, TemporalFieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new TemporalFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  static TemporalFieldBuilder builder() {return new TemporalFieldBuilder();}

  static TemporalFieldBuilder builder(TemporalField temporalField) {return new TemporalFieldBuilder(temporalField);}

  final class TemporalFieldBuilder extends FieldSchemaArtifactBuilder<TemporalField.TemporalFieldBuilder>
  {
    private final TemporalFieldUi.TemporalFieldUiBuilder fieldUiBuilder;
    private final TemporalValueConstraints.TemporalValueConstraintsBuilder valueConstraintsBuilder;

    public TemporalFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      this.fieldUiBuilder = TemporalFieldUi.builder();
      this.valueConstraintsBuilder = TemporalValueConstraints.builder();
    }

    public TemporalFieldBuilder(TemporalField temporalField)
    {
      super(temporalField);
      this.fieldUiBuilder = TemporalFieldUi.builder(temporalField.fieldUi().asTemporalFieldUi());
      this.valueConstraintsBuilder = temporalField.valueConstraints()
        .map(vc -> TemporalValueConstraints.builder(vc.asTemporalValueConstraints()))
        .orElseGet(TemporalValueConstraints::builder);
    }

    @Override public TemporalFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public TemporalFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
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

    @Override public TemporalFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    public TemporalFieldBuilder withTemporalType(XsdTemporalDatatype temporalType)
    {
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
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
      fieldUiBuilder.withTimezoneEnabled(timeZoneEnabled);
      return this;
    }

    public TemporalFieldBuilder withDefaultValue(String defaultValue)
    {
      valueConstraintsBuilder.withDefaultValue(defaultValue);
      return this;
    }

    public TemporalField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asTemporalFieldUi(), valueConstraints,
        annotations, internalName, internalDescription);
    }
  }
}

record TemporalFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                           String name, String description, Optional<String> identifier, Optional<Version> version,
                           Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                           boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                           Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                           Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                           Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language,
                           TemporalFieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
                           Optional<Annotations> annotations, String internalName, String internalDescription)
  implements TemporalField
{
  public TemporalFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
