package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;

public final class ControlledTermFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final ControlledTermValueConstraints.Builder valueConstraintsBuilder = ControlledTermValueConstraints.builder();

  public ControlledTermFieldBuilder() {
    super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
    withJsonLdContext(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    valueConstraintsBuilder.withMultipleChoice(false);
    fieldUiBuilder.withInputType(FieldInputType.TEXTFIELD);
  }

  public ControlledTermFieldBuilder withOntologyValueConstraint(URI uri, String acronym, String name) {
    valueConstraintsBuilder.withOntologyValueConstraint(new OntologyValueConstraint(uri, acronym, name, Optional.empty()));
    return this;
  }

  public ControlledTermFieldBuilder withOntologyValueConstraint(URI uri, String acronym, String name, Optional<Integer> numTerms) {
    valueConstraintsBuilder.withOntologyValueConstraint(new OntologyValueConstraint(uri, acronym, name, numTerms));
    return this;
  }

  public ControlledTermFieldBuilder withValueSetValueConstraint(URI uri, String vsCollection, String name)
  {
    valueConstraintsBuilder.withValueSetValueConstraint(new ValueSetValueConstraint(uri, vsCollection, name, Optional.empty()));
    return this;
  }

  public ControlledTermFieldBuilder withValueSetValueConstraint(URI uri, String vsCollection, String name, Integer numberOfTerms)
  {
    valueConstraintsBuilder.withValueSetValueConstraint(new ValueSetValueConstraint(uri, vsCollection, name, Optional.of(numberOfTerms)));
    return this;
  }

  public ControlledTermFieldBuilder withClassValueConstraint(URI uri, String source, String label, String prefLabel, ValueType type)
  {
    valueConstraintsBuilder.withClassValueConstraint(new ClassValueConstraint(uri, source, label, prefLabel, type));
    return this;
  }

  public ControlledTermFieldBuilder withBranchValueConstraint(URI uri, String source, String acronym, String name, int maxDepth)
  {
    valueConstraintsBuilder.withBranchValueConstraint(new BranchValueConstraint(uri, source, acronym, name, maxDepth));
    return this;
  }

  public ControlledTermFieldBuilder withValueConstraintsAction(URI termUri, String source, ValueType type,
    ValueConstraintsActionType action, URI sourceUri, Integer to)
  {
    valueConstraintsBuilder.withValueConstraintsAction(new ControlledTermValueConstraintsAction(termUri, source, type, action, Optional.of(sourceUri), Optional.of(to)));
    return this;
  }

  public ControlledTermFieldBuilder withValueConstraintsAction(URI termUri, String source, ValueType type, ValueConstraintsActionType action)
  {
    valueConstraintsBuilder.withValueConstraintsAction(new ControlledTermValueConstraintsAction(termUri, source, type, action, Optional.empty(), Optional.empty()));
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
    fieldUiBuilder.withValueRecommendation(valueRecommendation);
    return this;
  }

  public ControlledTermFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public ControlledTermFieldBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    super.withJsonLdContext(jsonLdContext);
    return this;
  }

  @Override public ControlledTermFieldBuilder withJsonLdType(URI jsonLdType) {
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

  @Override public ControlledTermFieldBuilder withPreferredLabel(String skosPrefLabel)
  {
    super.withPreferredLabel(skosPrefLabel);
    return this;
  }

  @Override public ControlledTermFieldBuilder withAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withAlternateLabels(skosAlternateLabels);
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


  @Override public FieldSchemaArtifact build()
  {
    withFieldUi(fieldUiBuilder.build());
    withValueConstraints(valueConstraintsBuilder.build());
    return super.build();
  }
}