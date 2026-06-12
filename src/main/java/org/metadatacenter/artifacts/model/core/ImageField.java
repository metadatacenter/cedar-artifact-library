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

public sealed interface ImageField extends FieldSchemaArtifact
{
  static ImageField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> language,
    Optional<String> preferredLabel, FieldUi fieldUi, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new ImageFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel,
      Collections.emptyList(), language, fieldUi, Optional.empty(), annotations, internalName,
      internalDescription);
  }

  default boolean isMultiple() {return false;}

  default Optional<Integer> minItems() {return Optional.empty();}

  default Optional<Integer> maxItems() {return Optional.empty();}

  default Optional<URI> propertyUri() {return Optional.empty();}

  static ImageFieldBuilder builder() {return new ImageFieldBuilder();}

  static ImageFieldBuilder builder(ImageField imageField)
  {
    return new ImageFieldBuilder(imageField);
  }

  final class ImageFieldBuilder extends FieldSchemaArtifactBuilder<ImageField.ImageFieldBuilder>
  {
    private final StaticFieldUi.ImageFieldUiBuilder fieldUiBuilder;

    public ImageFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = StaticFieldUi.imageFieldUiBuilder();
    }

    public ImageFieldBuilder(ImageField imageField)
    {
      super(imageField);
      this.fieldUiBuilder = StaticFieldUi.imageFieldUiBuilder(imageField.fieldUi().asStaticFieldUi());
    }

    @Override public ImageFieldBuilder withRequiredValue(boolean requiredValue)
    {
      return this;
    }

    @Override public ImageFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      return this;
    }

    @Override public ImageFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public ImageFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public ImageFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public ImageFieldBuilder withWidth(Integer width)
    {
      fieldUiBuilder.withWidth(width);
      return this;
    }

    public ImageFieldBuilder withHeight(Integer height)
    {
      fieldUiBuilder.withHeight(height);
      return this;
    }

    public ImageFieldBuilder withContent(String content)
    {
      fieldUiBuilder.withContent(content);
      return this;
    }

    public ImageField build()
    {
      withFieldUi(fieldUiBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, language, preferredLabel,
        fieldUi, annotations, internalName, internalDescription);
    }
  }
}

record ImageFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                        String name, String description, Optional<String> identifier, Optional<Version> version,
                        Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                        Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                        Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                        List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                        Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                        String internalName, String internalDescription) implements ImageField
{
  public ImageFieldRecord
  {
    FieldSchemaArtifactInvariants.validateStatic(this, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.staticContext();
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
