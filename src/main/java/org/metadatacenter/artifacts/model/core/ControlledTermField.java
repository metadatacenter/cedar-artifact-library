package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
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

public sealed interface ControlledTermField extends FieldSchemaArtifact
{
  static ControlledTermField create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Version modelVersion, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, List<String> alternateLabels,
    Optional<String> language, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
    Optional<Annotations> annotations)
  {
    return new ControlledTermFieldRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle,
      jsonSchemaDescription, jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version,
      status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
      createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
  }

  static ControlledTermFieldBuilder builder() { return new ControlledTermFieldBuilder(); }

  static ControlledTermFieldBuilder builder(ControlledTermField controlledTermField) {
    return new ControlledTermFieldBuilder(controlledTermField);
  }

  final class ControlledTermFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final ControlledTermValueConstraints.Builder valueConstraintsBuilder;

    public ControlledTermFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.valueConstraintsBuilder = ControlledTermValueConstraints.builder();
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.TEXTFIELD);
    }

    public ControlledTermFieldBuilder(ControlledTermField controlledTermField)
    {
      super(controlledTermField);

      this.fieldUiBuilder = FieldUi.builder(controlledTermField.fieldUi());
      if (controlledTermField.valueConstraints().isPresent())
        this.valueConstraintsBuilder = ControlledTermValueConstraints.builder(controlledTermField.valueConstraints().get().asControlledTermValueConstraints());
      else
        this.valueConstraintsBuilder = ControlledTermValueConstraints.builder();
    }

    public ControlledTermFieldBuilder withOntologyValueConstraint(URI uri, String acronym, String name)
    {
      valueConstraintsBuilder.withOntologyValueConstraint(
        new OntologyValueConstraint(uri, acronym, name, Optional.empty()));
      return this;
    }

    public ControlledTermFieldBuilder withOntologyValueConstraint(URI uri, String acronym, String name,
      Optional<Integer> numTerms)
    {
      valueConstraintsBuilder.withOntologyValueConstraint(new OntologyValueConstraint(uri, acronym, name, numTerms));
      return this;
    }

    public ControlledTermFieldBuilder withValueSetValueConstraint(URI uri, String vsCollection, String name)
    {
      valueConstraintsBuilder.withValueSetValueConstraint(
        new ValueSetValueConstraint(uri, vsCollection, name, Optional.empty()));
      return this;
    }

    public ControlledTermFieldBuilder withValueSetValueConstraint(URI uri, String vsCollection, String name,
      Integer numberOfTerms)
    {
      valueConstraintsBuilder.withValueSetValueConstraint(
        new ValueSetValueConstraint(uri, vsCollection, name, Optional.of(numberOfTerms)));
      return this;
    }

    public ControlledTermFieldBuilder withClassValueConstraint(URI uri, String source, String label, String prefLabel,
      ValueType type)
    {
      valueConstraintsBuilder.withClassValueConstraint(new ClassValueConstraint(uri, source, label, prefLabel, type));
      return this;
    }

    public ControlledTermFieldBuilder withBranchValueConstraint(URI uri, String source, String acronym, String name,
      int maxDepth)
    {
      valueConstraintsBuilder.withBranchValueConstraint(
        new BranchValueConstraint(uri, source, acronym, name, maxDepth));
      return this;
    }

    public ControlledTermFieldBuilder withValueConstraintsAction(URI termUri, String source, ValueType type,
      ValueConstraintsActionType action, URI sourceUri, Integer to)
    {
      valueConstraintsBuilder.withValueConstraintsAction(
        new ControlledTermValueConstraintsAction(termUri, source, type, action, Optional.of(sourceUri),
          Optional.of(to)));
      return this;
    }

    public ControlledTermFieldBuilder withValueConstraintsAction(URI termUri, String source, ValueType type,
      ValueConstraintsActionType action)
    {
      valueConstraintsBuilder.withValueConstraintsAction(
        new ControlledTermValueConstraintsAction(termUri, source, type, action, Optional.empty(), Optional.empty()));
      return this;
    }

    public ControlledTermFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    public ControlledTermFieldBuilder withDefaultValue(URI uri, String label)
    {
      valueConstraintsBuilder.withDefaultValue(uri, label);
      return this;
    }

    public ControlledTermFieldBuilder withValueRecommendation(boolean valueRecommendation)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendation);
      return this;
    }

    public ControlledTermFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public ControlledTermFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
    {
      super.withJsonLdContext(jsonLdContext);
      return this;
    }

    @Override public ControlledTermFieldBuilder withJsonLdType(URI jsonLdType)
    {
      super.withJsonLdType(jsonLdType);
      return this;
    }

    @Override public ControlledTermFieldBuilder withJsonLdId(URI jsonLdId)
    {
      super.withJsonLdId(jsonLdId);
      return this;
    }

    @Override public ControlledTermFieldBuilder withName(String name)
    {
      super.withName(name);
      return this;
    }

    @Override public ControlledTermFieldBuilder withDescription(String description)
    {
      super.withDescription(description);
      return this;
    }

    @Override public ControlledTermFieldBuilder withIdentifier(String identifier)
    {
      super.withIdentifier(identifier);
      return this;
    }

    @Override public ControlledTermFieldBuilder withModelVersion(Version modelVersion)
    {
      super.withModelVersion(modelVersion);
      return this;
    }

    @Override public ControlledTermFieldBuilder withVersion(Version version)
    {
      super.withVersion(version);
      return this;
    }

    @Override public ControlledTermFieldBuilder withStatus(Status status)
    {
      super.withStatus(status);
      return this;
    }

    @Override public ControlledTermFieldBuilder withCreatedBy(URI createdBy)
    {
      super.withCreatedBy(createdBy);
      return this;
    }

    @Override public ControlledTermFieldBuilder withModifiedBy(URI modifiedBy)
    {
      super.withModifiedBy(modifiedBy);
      return this;
    }

    @Override public ControlledTermFieldBuilder withCreatedOn(OffsetDateTime createdOn)
    {
      super.withCreatedOn(createdOn);
      return this;
    }

    @Override public ControlledTermFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      super.withLastUpdatedOn(lastUpdatedOn);
      return this;
    }

    @Override public ControlledTermFieldBuilder withPreviousVersion(URI previousVersion)
    {
      super.withPreviousVersion(previousVersion);
      return this;
    }

    @Override public ControlledTermFieldBuilder withDerivedFrom(URI derivedFrom)
    {
      super.withDerivedFrom(derivedFrom);
      return this;
    }

    @Override public ControlledTermFieldBuilder withPreferredLabel(String preferredLabel)
    {
      super.withPreferredLabel(preferredLabel);
      return this;
    }

    @Override public ControlledTermFieldBuilder withAlternateLabels(List<String> alternateLabels)
    {
      super.withAlternateLabels(alternateLabels);
      return this;
    }

    @Override public ControlledTermFieldBuilder withIsMultiple(boolean isMultiple)
    {
      super.withIsMultiple(isMultiple);
      return this;
    }

    @Override public ControlledTermFieldBuilder withMinItems(Integer minItems)
    {
      super.withMinItems(minItems);
      return this;
    }

    @Override public ControlledTermFieldBuilder withMaxItems(Integer maxItems)
    {
      super.withMaxItems(maxItems);
      return this;
    }

    @Override public ControlledTermFieldBuilder withPropertyUri(URI propertyUri)
    {
      super.withPropertyUri(propertyUri);
      return this;
    }

    @Override public ControlledTermFieldBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    @Override public ControlledTermFieldBuilder withJsonSchemaTitle(String jsonSchemaTitle)
    {
      super.withJsonSchemaTitle(jsonSchemaTitle);
      return this;
    }

    @Override public ControlledTermFieldBuilder withJsonSchemaDescription(String jsonSchemaDescription)
    {
      super.withJsonSchemaDescription(jsonSchemaDescription);
      return this;
    }

    @Override public ControlledTermFieldBuilder withAnnotations(Annotations annotations)
    {
      super.withAnnotations(annotations);
      return this;
    }

    public ControlledTermField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, jsonLdContext,
        jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status, previousVersion,
        derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    }
  }
}

record ControlledTermFieldRecord(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
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
implements ControlledTermField
{
  public ControlledTermFieldRecord
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
      throw new IllegalStateException("minItems must be less than or equal to maxItems in element schema artifact " + name);

    if (fieldUi.isStatic())
      jsonLdContext = new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    else
      jsonLdContext = new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}