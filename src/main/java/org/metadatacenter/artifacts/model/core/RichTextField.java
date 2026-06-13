package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
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

public sealed interface RichTextField extends FieldSchemaArtifact
{
  static RichTextField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> language,
    Optional<String> preferredLabel, StaticFieldUi fieldUi, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new RichTextFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel,
      Collections.emptyList(), language, fieldUi, Optional.empty(), annotations, internalName,
      internalDescription);
  }

  default boolean isMultiple() {return false;}

  default Optional<Integer> minItems() {return Optional.empty();}

  default Optional<Integer> maxItems() {return Optional.empty();}

  default Optional<URI> propertyUri() {return Optional.empty();}

  static RichTextFieldBuilder builder() {return new RichTextFieldBuilder();}

  static RichTextFieldBuilder builder(RichTextField richTextField)
  {
    return new RichTextFieldBuilder(richTextField);
  }

  final class RichTextFieldBuilder extends FieldSchemaArtifactBuilder<RichTextField.RichTextFieldBuilder>
  {
    private final StaticFieldUi.RichTextFieldUiBuilder fieldUiBuilder;

    public RichTextFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = StaticFieldUi.richTextFieldUiBuilder();
    }

    public RichTextFieldBuilder(RichTextField richTextField)
    {
      super(richTextField);
      this.fieldUiBuilder = StaticFieldUi.richTextFieldUiBuilder(richTextField.fieldUi().asStaticFieldUi());
    }

    @Override public RichTextFieldBuilder withRequiredValue(boolean requiredValue)
    {
      return this;
    }

    @Override public RichTextFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      return this;
    }

    @Override public RichTextFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public RichTextFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public RichTextFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public RichTextFieldBuilder withContent(String content)
    {
      fieldUiBuilder.withContent(content);
      return this;
    }

    public RichTextField build()
    {
      withFieldUi(fieldUiBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, language, preferredLabel,
        fieldUi.asStaticFieldUi(), annotations, internalName, internalDescription);
    }
  }
}

record RichTextFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                           String name, String description, Optional<String> identifier, Optional<Version> version,
                           Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                           Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                           Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                           List<String> alternateLabels, Optional<String> language, StaticFieldUi fieldUi,
                           Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                           String internalName, String internalDescription) implements RichTextField
{
  public RichTextFieldRecord
  {
    FieldSchemaArtifactInvariants.validateStatic(this, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.staticContext();
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
