package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.BooleanValueConstraints;
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

public sealed interface BooleanField extends FieldSchemaArtifact
{
  static BooleanField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, Optional<Integer> minItems, Optional<Integer> maxItems,
    Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
    Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel, List<String> alternateLabels,
    Optional<String> language, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
    Optional<Annotations> annotations, String internalName, String internalDescription)
  {
    return new BooleanFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
      preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  default boolean isMultiple() {return false;}

  static BooleanFieldBuilder builder() {return new BooleanFieldBuilder();}

  static BooleanFieldBuilder builder(BooleanField booleanField) {return new BooleanFieldBuilder(booleanField);}

  final class BooleanFieldBuilder extends FieldSchemaArtifactBuilder<BooleanField.BooleanFieldBuilder>
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final BooleanValueConstraints.BooleanValueConstraintsBuilder valueConstraintsBuilder;

    public BooleanFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.BOOLEAN);
      this.valueConstraintsBuilder = BooleanValueConstraints.builder();
    }

    public BooleanFieldBuilder(BooleanField booleanField)
    {
      super(booleanField);
      this.fieldUiBuilder = FieldUi.builder(booleanField.fieldUi());
      this.valueConstraintsBuilder = booleanField.valueConstraints()
        .map(vc -> BooleanValueConstraints.builder(vc.asBooleanValueConstraints()))
        .orElseGet(BooleanValueConstraints::builder);
    }

    @Override public BooleanFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public BooleanFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public BooleanFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public BooleanFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public BooleanFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public BooleanFieldBuilder withDefaultValue(boolean defaultValue)
    {
      valueConstraintsBuilder.withDefaultValue(defaultValue);
      return this;
    }

    /** Set an explicit null default (distinct from having no default). */
    public BooleanFieldBuilder withNullDefaultValue()
    {
      valueConstraintsBuilder.withNullDefaultValue();
      return this;
    }

    public BooleanFieldBuilder withNullEnabled(boolean nullEnabled)
    {
      valueConstraintsBuilder.withNullEnabled(nullEnabled);
      return this;
    }

    public BooleanFieldBuilder withTrueLabel(String trueLabel)
    {
      valueConstraintsBuilder.withTrueLabel(trueLabel);
      return this;
    }

    public BooleanFieldBuilder withFalseLabel(String falseLabel)
    {
      valueConstraintsBuilder.withFalseLabel(falseLabel);
      return this;
    }

    public BooleanFieldBuilder withNullLabel(String nullLabel)
    {
      valueConstraintsBuilder.withNullLabel(nullLabel);
      return this;
    }

    public BooleanField build()
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

record BooleanFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                          String name, String description, Optional<String> identifier, Optional<Version> version,
                          Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                          Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri,
                          Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                          Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                          List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                          Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                          String internalName, String internalDescription) implements BooleanField
{
  public BooleanFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
