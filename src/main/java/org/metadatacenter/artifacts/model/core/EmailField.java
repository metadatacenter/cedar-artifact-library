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

public sealed interface EmailField extends FieldSchemaArtifact
{
  static EmailField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new EmailFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  static EmailFieldBuilder builder() {return new EmailFieldBuilder();}

  static EmailFieldBuilder builder(EmailField emailField) {return new EmailFieldBuilder(emailField);}

  final class EmailFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final TextValueConstraints.TextValueConstraintsBuilder valueConstraintsBuilder;

    public EmailFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.EMAIL);
      this.valueConstraintsBuilder = TextValueConstraints.builder();
    }

    public EmailFieldBuilder(EmailField emailField)
    {
      super(emailField);

      this.fieldUiBuilder = FieldUi.builder(emailField.fieldUi());
      if (emailField.valueConstraints().isPresent())
        this.valueConstraintsBuilder = TextValueConstraints.builder(
          emailField.valueConstraints().get().asTextValueConstraints());
      else
        this.valueConstraintsBuilder = TextValueConstraints.builder();
    }

    @Override public EmailFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    public EmailFieldBuilder withDefaultValue(String defaultValue)
    {
      valueConstraintsBuilder.withDefaultValue(defaultValue);
      return this;
    }

    public EmailFieldBuilder withMinLength(Integer minLength)
    {
      valueConstraintsBuilder.withMinLength(minLength);
      return this;
    }

    public EmailFieldBuilder withMaxLength(Integer maxLength)
    {
      valueConstraintsBuilder.withMaxLength(maxLength);
      return this;
    }

    @Override public EmailFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public EmailFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public EmailFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    @Override public EmailFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public EmailFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
    {
      super.withJsonLdContext(jsonLdContext);
      return this;
    }

    @Override public EmailFieldBuilder withJsonLdType(URI jsonLdType)
    {
      super.withJsonLdType(jsonLdType);
      return this;
    }

    @Override public EmailFieldBuilder withJsonLdId(URI jsonLdId)
    {
      super.withJsonLdId(jsonLdId);
      return this;
    }

    @Override public EmailFieldBuilder withName(String name)
    {
      super.withName(name);
      return this;
    }

    @Override public EmailFieldBuilder withDescription(String description)
    {
      super.withDescription(description);
      return this;
    }

    @Override public EmailFieldBuilder withIdentifier(String identifier)
    {
      super.withIdentifier(identifier);
      return this;
    }

    @Override public EmailFieldBuilder withVersion(Version version)
    {
      super.withVersion(version);
      return this;
    }

    @Override public EmailFieldBuilder withStatus(Status status)
    {
      super.withStatus(status);
      return this;
    }

    @Override public EmailFieldBuilder withCreatedBy(URI createdBy)
    {
      super.withCreatedBy(createdBy);
      return this;
    }

    @Override public EmailFieldBuilder withModifiedBy(URI modifiedBy)
    {
      super.withModifiedBy(modifiedBy);
      return this;
    }

    @Override public EmailFieldBuilder withCreatedOn(OffsetDateTime createdOn)
    {
      super.withCreatedOn(createdOn);
      return this;
    }

    @Override public EmailFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      super.withLastUpdatedOn(lastUpdatedOn);
      return this;
    }

    @Override public EmailFieldBuilder withPreviousVersion(URI previousVersion)
    {
      super.withPreviousVersion(previousVersion);
      return this;
    }

    @Override public EmailFieldBuilder withDerivedFrom(URI derivedFrom)
    {
      super.withDerivedFrom(derivedFrom);
      return this;
    }

    @Override public EmailFieldBuilder withPreferredLabel(String preferredLabel)
    {
      super.withPreferredLabel(preferredLabel);
      return this;
    }

    @Override public EmailFieldBuilder withAlternateLabels(List<String> alternateLabels)
    {
      super.withAlternateLabels(alternateLabels);
      return this;
    }

    @Override public EmailFieldBuilder withIsMultiple(boolean isMultiple)
    {
      super.withIsMultiple(isMultiple);
      return this;
    }

    @Override public EmailFieldBuilder withMinItems(Integer minItems)
    {
      super.withMinItems(minItems);
      return this;
    }

    @Override public EmailFieldBuilder withMaxItems(Integer maxItems)
    {
      super.withMaxItems(maxItems);
      return this;
    }

    @Override public EmailFieldBuilder withPropertyUri(URI propertyUri)
    {
      super.withPropertyUri(propertyUri);
      return this;
    }

    @Override public EmailFieldBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    @Override public EmailFieldBuilder withInternalName(String internalName)
    {
      super.withInternalName(internalName);
      return this;
    }

    @Override public EmailFieldBuilder withInternalDescription(String internalDescription)
    {
      super.withInternalDescription(internalDescription);
      return this;
    }

    @Override public EmailFieldBuilder withAnnotations(Annotations annotations)
    {
      super.withAnnotations(annotations);
      return this;
    }

    public EmailField build()
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

record EmailFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                        String name, String description, Optional<String> identifier, Optional<Version> version,
                        Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                        boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                        Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                        Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                        Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language,
                        FieldUi fieldUi, Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                        String internalName, String internalDescription) implements EmailField
{
  public EmailFieldRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(this, jsonLdTypes, JSON_LD_TYPE,
      Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, alternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(this, propertyUri, "propertyUri"); // TODO Add to ModelNodeNames
    validateOptionalFieldNotNull(this, language, "language");
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
      jsonLdContext = new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    else
      jsonLdContext = new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}