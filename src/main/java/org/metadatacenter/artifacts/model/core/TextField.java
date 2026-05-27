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

public sealed interface TextField extends FieldSchemaArtifact
{
  static TextField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new TextFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  static TextFieldBuilder builder() {return new TextFieldBuilder();}

  static TextFieldBuilder builder(TextField textField) {return new TextFieldBuilder(textField);}

  final class TextFieldBuilder extends FieldSchemaArtifactBuilder<TextField.TextFieldBuilder>
      implements LiteralStringDefaultableFieldBuilder
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final TextValueConstraints.TextValueConstraintsBuilder valueConstraintsBuilder;

    public TextFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.TEXTFIELD);
      this.valueConstraintsBuilder = TextValueConstraints.builder();
    }

    public TextFieldBuilder(TextField textField)
    {
      super(textField);
      this.fieldUiBuilder = FieldUi.builder(textField.fieldUi());
      this.valueConstraintsBuilder = textField.valueConstraints()
        .map(vc -> TextValueConstraints.builder(vc.asTextValueConstraints()))
        .orElseGet(TextValueConstraints::builder);
    }

    @Override public TextFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public TextFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public TextFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public TextFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public TextFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
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

    public TextFieldBuilder withRegex(String regex)
    {
      valueConstraintsBuilder.withRegex(regex);
      return this;
    }

    public TextField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
    }
  }
}

record TextFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                       String name, String description, Optional<String> identifier, Optional<Version> version,
                       Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                       boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                       Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                       Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                       Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language,
                       FieldUi fieldUi, Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                       String internalName, String internalDescription) implements TextField
{
  public TextFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
