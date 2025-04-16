package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.*;
import static org.metadatacenter.model.ModelNodeNames.*;

public sealed interface YouTubeField extends FieldSchemaArtifact
{
  static YouTubeField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    FieldUi fieldUi, Optional<Annotations> annotations, String internalName, String internalDescription)
  {
    return new YouTubeFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel,
      Collections.emptyList(), Optional.empty(), fieldUi, Optional.empty(), annotations, internalName, internalDescription);
  }

  default boolean isMultiple() { return false; }

  default Optional<Integer> minItems() { return Optional.empty(); }

  default Optional<Integer> maxItems() { return Optional.empty(); }

  default Optional<URI> propertyUri() { return Optional.empty(); }

  static YouTubeFieldBuilder builder() { return new YouTubeFieldBuilder(); }

  static YouTubeFieldBuilder builder(YouTubeField youTubeField) {return new YouTubeFieldBuilder(youTubeField);}

  final class YouTubeFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private final StaticFieldUi.YouTubeFieldUiBuilder fieldUiBuilder;

    public YouTubeFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = StaticFieldUi.youTubeFieldUiBuilder();
    }

    public YouTubeFieldBuilder(YouTubeField youTubeField)
    {
      super(youTubeField);

      this.fieldUiBuilder = StaticFieldUi.youTubeFieldUiBuilder(youTubeField.fieldUi().asStaticFieldUi());
    }

    @Override public YouTubeFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
    {
      super.withJsonLdContext(jsonLdContext);
      return this;
    }

    @Override public YouTubeFieldBuilder withJsonLdType(URI jsonLdType)
    {
      super.withJsonLdType(jsonLdType);
      return this;
    }

    @Override public YouTubeFieldBuilder withJsonLdId(URI jsonLdId)
    {
      super.withJsonLdId(jsonLdId);
      return this;
    }

    @Override public YouTubeFieldBuilder withName(String name)
    {
      super.withName(name);
      return this;
    }

    @Override public YouTubeFieldBuilder withDescription(String description)
    {
      super.withDescription(description);
      return this;
    }

    @Override public YouTubeFieldBuilder withIdentifier(String identifier)
    {
      super.withIdentifier(identifier);
      return this;
    }

    @Override public YouTubeFieldBuilder withPreferredLabel(String preferredLabel)
    {
      super.withPreferredLabel(preferredLabel);
      return this;
    }

    @Override public YouTubeFieldBuilder withVersion(Version version)
    {
      super.withVersion(version);
      return this;
    }

    @Override public YouTubeFieldBuilder withStatus(Status status)
    {
      super.withStatus(status);
      return this;
    }

    public YouTubeFieldBuilder withContent(String content)
    {
      fieldUiBuilder.withContent(content);
      return this;
    }

    @Override public YouTubeFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public YouTubeFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public YouTubeFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    @Override public YouTubeFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      return this;
    }

    @Override public YouTubeFieldBuilder withRequiredValue(boolean requiredValue)
    {
      return this;
    }


    @Override public YouTubeFieldBuilder withCreatedBy(URI createdBy)
    {
      super.withCreatedBy(createdBy);
      return this;
    }

    @Override public YouTubeFieldBuilder withModifiedBy(URI modifiedBy)
    {
      super.withModifiedBy(modifiedBy);
      return this;
    }

    @Override public YouTubeFieldBuilder withCreatedOn(OffsetDateTime createdOn)
    {
      super.withCreatedOn(createdOn);
      return this;
    }

    @Override public YouTubeFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      super.withLastUpdatedOn(lastUpdatedOn);
      return this;
    }

    @Override public YouTubeFieldBuilder withPreviousVersion(URI previousVersion)
    {
      super.withPreviousVersion(previousVersion);
      return this;
    }

    @Override public YouTubeFieldBuilder withDerivedFrom(URI derivedFrom)
    {
      super.withDerivedFrom(derivedFrom);
      return this;
    }

    @Override public YouTubeFieldBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    @Override public YouTubeFieldBuilder withInternalName(String internalName)
    {
      super.withInternalName(internalName);
      return this;
    }

    @Override public YouTubeFieldBuilder withInternalDescription(String internalDescription)
    {
      super.withInternalDescription(internalDescription);
      return this;
    }

    @Override public YouTubeFieldBuilder withAnnotations(Annotations annotations)
    {
      super.withAnnotations(annotations);
      return this;
    }

    public YouTubeField build()
    {
      withFieldUi(fieldUiBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description,
        identifier, version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, fieldUi, annotations, internalName, internalDescription);
    }
  }
}

record YouTubeFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                          String name, String description, Optional<String> identifier, Optional<Version> version,
                          Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                          Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                          Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                          List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                          Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                          String internalName, String internalDescription) implements YouTubeField
{
  public YouTubeFieldRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(this, jsonLdTypes, JSON_LD_TYPE,
      Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, alternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, language, "language");
    validateUiFieldNotNull(this, fieldUi, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(this, annotations, "annotations");

    jsonLdContext = new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}