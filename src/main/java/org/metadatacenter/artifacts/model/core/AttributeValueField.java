package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
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
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_STRING;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;

public sealed interface AttributeValueField extends FieldSchemaArtifact
{
  static AttributeValueField create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Version modelVersion, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, List<String> alternateLabels,
    Optional<String> language, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
    Optional<Annotations> annotations)
  {
    return new AttributeValueFieldRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
      previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
  }

  static AttributeValueFieldBuilder builder() { return new AttributeValueFieldBuilder(); }

  static AttributeValueFieldBuilder builder(AttributeValueField attributeValueField) {
    return new AttributeValueFieldBuilder(attributeValueField);
  }

  final class AttributeValueFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private final FieldUi.Builder fieldUiBuilder;

    public AttributeValueFieldBuilder()
    {
      super(JSON_SCHEMA_STRING, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.ATTRIBUTE_VALUE);
    }

    public AttributeValueFieldBuilder(AttributeValueField attributeValueField)
    {
      super(attributeValueField);

      this.fieldUiBuilder = FieldUi.builder(attributeValueField.fieldUi());
    }

    @Override public AttributeValueFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
    {
      super.withJsonLdContext(jsonLdContext);
      return this;
    }

    @Override public AttributeValueFieldBuilder withJsonLdType(URI jsonLdType)
    {
      super.withJsonLdType(jsonLdType);
      return this;
    }

    @Override public AttributeValueFieldBuilder withJsonLdId(URI jsonLdId)
    {
      super.withJsonLdId(jsonLdId);
      return this;
    }

    @Override public AttributeValueFieldBuilder withName(String name)
    {
      super.withName(name);
      return this;
    }

    @Override public AttributeValueFieldBuilder withDescription(String description)
    {
      super.withDescription(description);
      return this;
    }

    @Override public AttributeValueFieldBuilder withIdentifier(String identifier)
    {
      super.withIdentifier(identifier);
      return this;
    }

    @Override public AttributeValueFieldBuilder withModelVersion(Version modelVersion)
    {
      super.withModelVersion(modelVersion);
      return this;
    }

    @Override public AttributeValueFieldBuilder withVersion(Version version)
    {
      super.withVersion(version);
      return this;
    }

    @Override public AttributeValueFieldBuilder withStatus(Status status)
    {
      super.withStatus(status);
      return this;
    }

    @Override public AttributeValueFieldBuilder withCreatedBy(URI createdBy)
    {
      super.withCreatedBy(createdBy);
      return this;
    }

    @Override public AttributeValueFieldBuilder withModifiedBy(URI modifiedBy)
    {
      super.withModifiedBy(modifiedBy);
      return this;
    }

    @Override public AttributeValueFieldBuilder withCreatedOn(OffsetDateTime createdOn)
    {
      super.withCreatedOn(createdOn);
      return this;
    }

    @Override public AttributeValueFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      super.withLastUpdatedOn(lastUpdatedOn);
      return this;
    }

    @Override public AttributeValueFieldBuilder withPreviousVersion(URI previousVersion)
    {
      super.withPreviousVersion(previousVersion);
      return this;
    }

    @Override public AttributeValueFieldBuilder withDerivedFrom(URI derivedFrom)
    {
      super.withDerivedFrom(derivedFrom);
      return this;
    }

    @Override public AttributeValueFieldBuilder withMinItems(Integer minItems)
    {
      super.withMinItems(minItems);
      return this;
    }

    @Override public AttributeValueFieldBuilder withMaxItems(Integer maxItems)
    {
      super.withMaxItems(maxItems);
      return this;
    }

    @Override public AttributeValueFieldBuilder withPropertyUri(URI propertyUri)
    {
      super.withPropertyUri(propertyUri);
      return this;
    }


    @Override public AttributeValueFieldBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    @Override public AttributeValueFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
    {
      super.withJsonSchemaTitle(jsonSchemaTitle);
      return this;
    }

    @Override public AttributeValueFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
    {
      super.withJsonSchemaDescription(jsonSchemaDescription);
      return this;
    }

    @Override public AttributeValueFieldBuilder withAnnotations(Annotations annotations)
    {
      super.withAnnotations(annotations);
      return this;
    }

    @Override public AttributeValueFieldBuilder withPreferredLabel(String preferredLabel)
    {
      super.withPreferredLabel(preferredLabel);
      return this;
    }

    public AttributeValueField build()
    {
      withFieldUi(fieldUiBuilder.build());

      return create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, jsonLdContext,
        jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status, previousVersion,
        derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    }

  }
}

record AttributeValueFieldRecord(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
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
  implements AttributeValueField
{
  public AttributeValueFieldRecord
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
      jsonLdContext = new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    else
      jsonLdContext = new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
