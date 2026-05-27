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

public sealed interface RadioField extends FieldSchemaArtifact
{
  static RadioField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Optional<Integer> minItems, Optional<Integer> maxItems,
    Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
    Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel, List<String> alternateLabels,
    Optional<String> language, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
    Optional<Annotations> annotations, String internalName, String internalDescription)
  {
    return new RadioFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
      preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  default boolean isMultiple() {return false;}

  static RadioFieldBuilder builder() {return new RadioFieldBuilder();}

  static RadioFieldBuilder builder(RadioField radioField) {return new RadioFieldBuilder(radioField);}

  final class RadioFieldBuilder extends FieldSchemaArtifactBuilder<RadioField.RadioFieldBuilder>
      implements LiteralStringDefaultableFieldBuilder
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final TextValueConstraints.TextValueConstraintsBuilder valueConstraintsBuilder;

    public RadioFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.RADIO);
      this.valueConstraintsBuilder = TextValueConstraints.builder().withMultipleChoice(false);
    }

    public RadioFieldBuilder(RadioField radioField)
    {
      super(radioField);
      this.fieldUiBuilder = FieldUi.builder(radioField.fieldUi());
      this.valueConstraintsBuilder = radioField.valueConstraints()
        .map(vc -> TextValueConstraints.builder(vc.asTextValueConstraints()))
        .orElseGet(() -> TextValueConstraints.builder().withMultipleChoice(true));
    }

    @Override public RadioFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public RadioFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public RadioFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public RadioFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public RadioFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public RadioFieldBuilder withDefaultValue(String defaultValue)
    {
      valueConstraintsBuilder.withDefaultValue(defaultValue);
      return this;
    }

    public RadioFieldBuilder withOption(String choice, boolean selectedByDefault)
    {
      valueConstraintsBuilder.withChoice(choice, selectedByDefault);
      return this;
    }

    public RadioFieldBuilder withOption(String choice)
    {
      valueConstraintsBuilder.withChoice(choice, false);
      return this;
    }

    public RadioField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    }
  }
}

record RadioFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                        String name, String description, Optional<String> identifier, Optional<Version> version,
                        Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                        Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri,
                        Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                        Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                        List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                        Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                        String internalName, String internalDescription) implements RadioField
{
  public RadioFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
