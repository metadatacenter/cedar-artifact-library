package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.LinkValueConstraints;
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

public sealed interface DoiField extends FieldSchemaArtifact
{
  static DoiField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new DoiFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
      previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
      internalDescription);
  }

  static DoiFieldBuilder builder() {return new DoiFieldBuilder();}

  static DoiFieldBuilder builder(DoiField doiField) {return new DoiFieldBuilder(doiField);}

  final class DoiFieldBuilder extends FieldSchemaArtifactBuilder<DoiField.DoiFieldBuilder>
      implements IriDefaultableFieldBuilder
  {
    private final FieldUi.Builder fieldUiBuilder;
    private final LinkValueConstraints.LinkValueConstraintsBuilder valueConstraintsBuilder;

    public DoiFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.DOI);
      this.valueConstraintsBuilder = LinkValueConstraints.builder();
    }

    public DoiFieldBuilder(DoiField doiField)
    {
      super(doiField);
      this.fieldUiBuilder = FieldUi.builder(doiField.fieldUi());
      this.valueConstraintsBuilder = doiField.valueConstraints()
        .map(vc -> LinkValueConstraints.builder(vc.asLinkValueConstraints()))
        .orElseGet(LinkValueConstraints::builder);
    }

    @Override public DoiFieldBuilder withRequiredValue(boolean requiredValue)
    {
      valueConstraintsBuilder.withRequiredValue(requiredValue);
      return this;
    }

    @Override public DoiFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      valueConstraintsBuilder.withRecommendedValue(recommendedValue);
      return this;
    }

    @Override public DoiFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public DoiFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public DoiFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public DoiFieldBuilder withDefaultValue(URI uri)
    {
      valueConstraintsBuilder.withDefaultValue(uri);
      return this;
    }

    public DoiField build()
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

record DoiFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                       String name, String description, Optional<String> identifier, Optional<Version> version,
                       Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                       boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                       Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                       Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                       Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language,
                       FieldUi fieldUi, Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                       String internalName, String internalDescription) implements DoiField
{
  public DoiFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);
    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
