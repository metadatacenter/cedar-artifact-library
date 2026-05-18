package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

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
      Collections.emptyList(), Optional.empty(), fieldUi, Optional.empty(), annotations, internalName,
      internalDescription);
  }

  default boolean isMultiple() {return false;}

  default Optional<Integer> minItems() {return Optional.empty();}

  default Optional<Integer> maxItems() {return Optional.empty();}

  default Optional<URI> propertyUri() {return Optional.empty();}

  static YouTubeFieldBuilder builder() {return new YouTubeFieldBuilder();}

  static YouTubeFieldBuilder builder(YouTubeField youTubeField) {return new YouTubeFieldBuilder(youTubeField);}

  final class YouTubeFieldBuilder extends FieldSchemaArtifactBuilder<YouTubeField.YouTubeFieldBuilder>
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

    @Override public YouTubeFieldBuilder withRequiredValue(boolean requiredValue)
    {
      return this;
    }

    @Override public YouTubeFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
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

    public YouTubeFieldBuilder withContent(String content)
    {
      fieldUiBuilder.withContent(content);
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
    FieldSchemaArtifactInvariants.validateStatic(this, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.staticContext();
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
