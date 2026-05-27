package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.NumericFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public sealed interface NumericField extends FieldSchemaArtifact
{
  static NumericField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, NumericFieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new NumericFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  static NumericFieldBuilder builder() {return new NumericFieldBuilder();}

  static NumericFieldBuilder builder(NumericField numericField) {return new NumericFieldBuilder(numericField);}

  final class NumericFieldBuilder extends FieldSchemaArtifactBuilder<NumericField.NumericFieldBuilder>
      implements NumericDefaultableFieldBuilder
  {
    private final NumericFieldUi.NumericFieldUiBuilder fieldUiBuilder;
    private final NumericValueConstraints.NumericValueConstraintsBuilder valueConstraintsBuilder;

    public NumericFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = NumericFieldUi.builder();
      this.valueConstraintsBuilder = NumericValueConstraints.builder();
    }

    public NumericFieldBuilder(NumericField numericField)
    {
      super(numericField);
      this.fieldUiBuilder = NumericFieldUi.builder(numericField.fieldUi().asNumericFieldUi());
      this.valueConstraintsBuilder = numericField.valueConstraints()
        .map(vc -> NumericValueConstraints.builder(vc.asNumericValueConstraints()))
        .orElseGet(NumericValueConstraints::builder);
    }

    @Override public NumericFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public NumericFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public NumericFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public NumericFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public NumericFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public NumericFieldBuilder withNumericType(XsdNumericDatatype numericType)
    {
      valueConstraintsBuilder.withNumberType(numericType);
      return this;
    }

    public NumericFieldBuilder withMinValue(Number minValue)
    {
      valueConstraintsBuilder.withMinValue(minValue);
      return this;
    }

    public NumericFieldBuilder withMaxValue(Number maxValue)
    {
      valueConstraintsBuilder.withMaxValue(maxValue);
      return this;
    }

    public NumericFieldBuilder withDecimalPlaces(Integer decimalPlaces)
    {
      valueConstraintsBuilder.withDecimalPlaces(decimalPlaces);
      return this;
    }

    public NumericFieldBuilder withUnitOfMeasure(String unitOfMeasure)
    {
      valueConstraintsBuilder.withUnitOfMeasure(unitOfMeasure);
      return this;
    }

    public NumericFieldBuilder withDefaultValue(Number defaultValue)
    {
      valueConstraintsBuilder.withDefaultValue(defaultValue);
      return this;
    }

    public NumericField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asNumericFieldUi(), valueConstraints,
        annotations, internalName, internalDescription);
    }
  }
}

record NumericFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                          String name, String description, Optional<String> identifier, Optional<Version> version,
                          Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                          boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                          Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                          Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                          Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language,
                          NumericFieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
                          Optional<Annotations> annotations, String internalName, String internalDescription)
  implements NumericField
{
  public NumericFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
