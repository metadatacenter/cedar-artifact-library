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

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public sealed interface ControlledTermField extends FieldSchemaArtifact
{
  static ControlledTermField create(String internalName, String internalDescription,
    LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, List<String> alternateLabels,
    Optional<String> language, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints,
    Optional<Annotations> annotations)
  {
    return new ControlledTermFieldRecord(internalName, internalDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier, version,
      status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
  }

  static ControlledTermFieldBuilder builder() { return new ControlledTermFieldBuilder(); }

  static ControlledTermFieldBuilder builder(ControlledTermField controlledTermField) {
    return new ControlledTermFieldBuilder(controlledTermField);
  }

  final class ControlledTermFieldBuilder extends FieldSchemaArtifactBuilder<ControlledTermField.ControlledTermFieldBuilder>
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final ControlledTermValueConstraints.ControlledTermValueConstraintsBuilder valueConstraintsBuilder;

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
      this.valueConstraintsBuilder = controlledTermField.valueConstraints()
        .map(vc -> ControlledTermValueConstraints.builder(vc.asControlledTermValueConstraints()))
        .orElseGet(ControlledTermValueConstraints::builder);
    }

    @Override public ControlledTermFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public ControlledTermFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public ControlledTermFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public ControlledTermFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public ControlledTermFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public ControlledTermFieldBuilder withOntologyValueConstraint(URI uri, String acronym, String name)
    {
      valueConstraintsBuilder.withOntologyValueConstraint(
        new OntologyValueConstraint(uri, acronym, name, Optional.empty()));
      return this;
    }

    public ControlledTermFieldBuilder withOntologyValueConstraint(URI uri, String acronym, String name,
      Integer numTerms)
    {
      valueConstraintsBuilder.withOntologyValueConstraint(
        new OntologyValueConstraint(uri, acronym, name, Optional.of(numTerms)));
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

    public ControlledTermFieldBuilder withDefaultValue(URI uri, String label)
    {
      valueConstraintsBuilder.withDefaultValue(uri, label);
      return this;
    }

    public ControlledTermField build()
    {
      withFieldUi(fieldUiBuilder.build());
      withValueConstraints(valueConstraintsBuilder.build());
      return create(internalName, internalDescription, jsonLdContext,
        jsonLdTypes, jsonLdId, name, description, identifier, version, status, previousVersion,
        derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    }
  }
}

record ControlledTermFieldRecord(String internalName, String internalDescription,
                                 LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                 String name, String description, Optional<String> identifier,
                                 Optional<Version> version, Optional<Status> status,
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
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
