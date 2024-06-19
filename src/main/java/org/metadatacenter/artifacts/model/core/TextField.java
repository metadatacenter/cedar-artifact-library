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
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldContainsAll;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContainsOneOf;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;

public sealed interface TextField extends FieldSchemaArtifact
{
  static TextField create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Version modelVersion, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, List<String> alternateLabels,
    Optional<String> language, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
    Optional<Annotations> annotations)
  {
    return new TextFieldRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle,
      jsonSchemaDescription, jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version,
      status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
      createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
  }

  static TextFieldBuilder builder() { return new TextFieldBuilder(); }

  static TextFieldBuilder builder(TextField textField) { return new TextFieldBuilder(textField); }

  final class TextFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private FieldUi.Builder fieldUiBuilder;
    private TextValueConstraints.Builder valueConstraintsBuilder;

    public TextFieldBuilder() {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.TEXTFIELD);
      this.valueConstraintsBuilder = TextValueConstraints.builder();
    }

    public TextFieldBuilder(TextField textField)
    {
      super(textField);

      this.fieldUiBuilder = FieldUi.builder(textField.fieldUi());
      if (textField.valueConstraints().isPresent())
        this.valueConstraintsBuilder = TextValueConstraints.builder(textField.valueConstraints().get().asTextValueConstraints());
      else
        this.valueConstraintsBuilder = TextValueConstraints.builder();
    }

    public TextFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    public TextFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
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

    public TextFieldBuilder withValueRecommendation(boolean valueRecommendation)
    {
      fieldUiBuilder.withValueRecommendation(valueRecommendation);
      return this;
    }

    public TextFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    public TextFieldBuilder withRegex(String regex)
    {
      valueConstraintsBuilder.withRegex(regex);
      return this;
    }

    @Override public TextFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
    {
      super.withJsonLdContext(jsonLdContext);
      return this;
    }

    @Override public TextFieldBuilder withJsonLdType(URI jsonLdType) {
      super.withJsonLdType(jsonLdType);
      return this;
    }

    @Override public TextFieldBuilder withJsonLdId(URI jsonLdId)
    {
      super.withJsonLdId(jsonLdId);
      return this;
    }

    @Override public TextFieldBuilder withName(String name)
    {
      super.withName(name);
      return this;
    }

    @Override public TextFieldBuilder withDescription(String description)
    {
      super.withDescription(description);
      return this;
    }

    @Override public TextFieldBuilder withIdentifier(String identifier)
    {
      super.withIdentifier(identifier);
      return this;
    }

    @Override public TextFieldBuilder withModelVersion(Version modelVersion)
    {
      super.withModelVersion(modelVersion);
      return this;
    }

    @Override public TextFieldBuilder withVersion(Version version)
    {
      super.withVersion(version);
      return this;
    }

    @Override public TextFieldBuilder withStatus(Status status)
    {
      super.withStatus(status);
      return this;
    }

    @Override public TextFieldBuilder withCreatedBy(URI createdBy)
    {
      super.withCreatedBy(createdBy);
      return this;
    }

    @Override public TextFieldBuilder withModifiedBy(URI modifiedBy)
    {
      super.withModifiedBy(modifiedBy);
      return this;
    }

    @Override public TextFieldBuilder withCreatedOn(OffsetDateTime createdOn)
    {
      super.withCreatedOn(createdOn);
      return this;
    }

    @Override public TextFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      super.withLastUpdatedOn(lastUpdatedOn);
      return this;
    }

    @Override public TextFieldBuilder withPreviousVersion(URI previousVersion)
    {
      super.withPreviousVersion(previousVersion);
      return this;
    }

    @Override public TextFieldBuilder withDerivedFrom(URI derivedFrom)
    {
      super.withDerivedFrom(derivedFrom);
      return this;
    }

    @Override public TextFieldBuilder withPreferredLabel(String preferredLabel)
    {
      super.withPreferredLabel(preferredLabel);
      return this;
    }

    @Override public TextFieldBuilder withAlternateLabels(List<String> alternateLabels)
    {
      super.withAlternateLabels(alternateLabels);
      return this;
    }

    @Override public TextFieldBuilder withIsMultiple(boolean isMultiple)
    {
      super.withIsMultiple(isMultiple);
      return this;
    }

    @Override public TextFieldBuilder withMinItems(Integer minItems)
    {
      super.withMinItems(minItems);
      return this;
    }

    @Override public TextFieldBuilder withMaxItems(Integer maxItems)
    {
      super.withMaxItems(maxItems);
      return this;
    }

    @Override public TextFieldBuilder withPropertyUri(URI propertyUri)
    {
      super.withPropertyUri(propertyUri);
      return this;
    }

    @Override public TextFieldBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    @Override public TextFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
    {
      super.withJsonSchemaTitle(jsonSchemaTitle);
      return this;
    }

    @Override public TextFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
    {
      super.withJsonSchemaDescription(jsonSchemaDescription);
      return this;
    }

    @Override public TextFieldBuilder withAnnotations(Annotations annotations)
    {
      super.withAnnotations(annotations);
      return this;
    }

    public TextField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle,
        jsonSchemaDescription, jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    }
  }
}

record TextFieldRecord(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
                       LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                       String name, String description, Optional<String> identifier,
                       Version modelVersion, Optional<Version> version, Optional<Status> status,
                       Optional<URI> previousVersion, Optional<URI> derivedFrom,
                       boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                       Optional<URI> propertyUri,
                       Optional<URI> createdBy, Optional<URI> modifiedBy,
                       Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                       Optional<String> preferredLabel, List<String> alternateLabels,
                       Optional<String> language, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
                       Optional<Annotations> annotations)
  implements TextField
{
  public TextFieldRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(this, jsonLdTypes, JSON_LD_TYPE,
      Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, alternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(this, propertyUri, "propertyUri"); // TODO Add to ModelNodeNames
    validateOptionalFieldNotNull(this, language,  "language");
    validateUiFieldNotNull(this, fieldUi, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(this, annotations, "annotations");

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name);

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name);

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be lass than maxItems in element schema artifact " + name);

    if (fieldUi.isStatic())
      validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    else
      validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdContext = new LinkedHashMap<>(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}