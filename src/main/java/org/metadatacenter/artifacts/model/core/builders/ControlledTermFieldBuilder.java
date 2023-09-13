package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.Version;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class ControlledTermFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final FieldUi.Builder fieldUiBuilder = FieldUi.builder();
  private final ControlledTermValueConstraints.Builder valueConstraintsBuilder = ControlledTermValueConstraints.builder();

  public ControlledTermFieldBuilder() {
    valueConstraintsBuilder.withMultipleChoice(false);
    fieldUiBuilder.withInputType(FieldInputType.TEXTFIELD);
  }

  public ControlledTermFieldBuilder withOntologyValueConstraint(OntologyValueConstraint constraint)
  {
    valueConstraintsBuilder.withOntologyValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withValueSetValueConstraint(ValueSetValueConstraint constraint)
  {
    valueConstraintsBuilder.withValueSetValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withClassValueConstraint(ClassValueConstraint constraint)
  {
    valueConstraintsBuilder.withClassValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withBranchValueConstraint(BranchValueConstraint constraint)
  {
    valueConstraintsBuilder.withBranchValueConstraint(constraint);
    return this;
  }

  public ControlledTermFieldBuilder withValueConstraintsAction(ControlledTermValueConstraintsAction action)
  {
    valueConstraintsBuilder.withValueConstraintsAction(action);
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

  public ControlledTermFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
  {
    fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
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

  @Override public ControlledTermFieldBuilder withSkosPrefLabel(String skosPrefLabel)
  {
    super.withSkosPrefLabel(skosPrefLabel);
    return this;
  }

  @Override public ControlledTermFieldBuilder withSkosAlternateLabels(List<String> skosAlternateLabels)
  {
    super.withSkosAlternateLabels(skosAlternateLabels);
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