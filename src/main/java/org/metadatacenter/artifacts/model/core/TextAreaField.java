package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public sealed interface TextAreaField extends FieldSchemaArtifact
{
  static TextAreaField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new TextAreaFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  static TextAreaFieldBuilder builder() {return new TextAreaFieldBuilder();}

  static TextAreaFieldBuilder builder(TextAreaField textAreaField) {return new TextAreaFieldBuilder(textAreaField);}

  final class TextAreaFieldBuilder extends FieldSchemaArtifactBuilder<TextAreaField.TextAreaFieldBuilder>
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final TextValueConstraints.TextValueConstraintsBuilder valueConstraintsBuilder;

    public TextAreaFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.TEXTAREA);
      this.valueConstraintsBuilder = TextValueConstraints.builder();
    }

    public TextAreaFieldBuilder(TextAreaField textAreaField)
    {
      super(textAreaField);
      this.fieldUiBuilder = FieldUi.builder(textAreaField.fieldUi());
      this.valueConstraintsBuilder = textAreaField.valueConstraints()
        .map(vc -> TextValueConstraints.builder(vc.asTextValueConstraints()))
        .orElseGet(TextValueConstraints::builder);
    }

    @Override public TextAreaFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public TextAreaFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public TextAreaFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public TextAreaFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    @Override public TextAreaFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    public TextAreaFieldBuilder withDefaultValue(String defaultValue)
    {
      valueConstraintsBuilder.withDefaultValue(defaultValue);
      return this;
    }

    public TextAreaFieldBuilder withMinLength(Integer minLength)
    {
      valueConstraintsBuilder.withMinLength(minLength);
      return this;
    }

    public TextAreaFieldBuilder withMaxLength(Integer maxLength)
    {
      valueConstraintsBuilder.withMaxLength(maxLength);
      return this;
    }

    public TextAreaField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description,
        identifier, version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri,
        createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi,
        valueConstraints, annotations, internalName, internalDescription);
    }
  }
}

record TextAreaFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                           String name, String description, Optional<String> identifier, Optional<Version> version,
                           Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                           boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                           Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                           Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                           Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language,
                           FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
                           Optional<Annotations> annotations, String internalName, String internalDescription)
  implements TextAreaField
{
  public TextAreaFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
