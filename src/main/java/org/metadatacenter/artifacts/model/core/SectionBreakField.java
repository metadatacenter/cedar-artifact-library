package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContainsOneOf;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;

public sealed interface SectionBreakField extends FieldSchemaArtifact
{
  static SectionBreakField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes,
    Optional<URI> jsonLdId, String name, String description, Optional<String> identifier, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, Optional<String> language, FieldUi fieldUi, Optional<Annotations> annotations,
    String internalName, String internalDescription)
  {
    return new SectionBreakFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
      status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel,
      Collections.emptyList(), language, fieldUi, Optional.empty(), annotations, internalName,
      internalDescription);
  }

  default boolean isMultiple() {return false;}

  default Optional<Integer> minItems() {return Optional.empty();}

  default Optional<Integer> maxItems() {return Optional.empty();}

  default Optional<URI> propertyUri() {return Optional.empty();}

  static SectionBreakFieldBuilder builder() {return new SectionBreakFieldBuilder();}

  static SectionBreakFieldBuilder builder(SectionBreakField sectionBreakField)
  {
    return new SectionBreakFieldBuilder(sectionBreakField);
  }

  final class SectionBreakFieldBuilder extends FieldSchemaArtifactBuilder
  {
    private final StaticFieldUi.SectionBreakFieldUiBuilder fieldUiBuilder;

    public SectionBreakFieldBuilder()
    {
      super(JSON_SCHEMA_OBJECT, STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_URI);
      withJsonLdContext(new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
      this.fieldUiBuilder = StaticFieldUi.sectionBreakFieldUiBuilder();
    }

    public SectionBreakFieldBuilder(SectionBreakField sectionBreakField)
    {
      super(sectionBreakField);

      this.fieldUiBuilder = StaticFieldUi.sectionBreakFieldUiBuilder(sectionBreakField.fieldUi().asStaticFieldUi());
    }

    public SectionBreakFieldBuilder withContent(String content)
    {
      fieldUiBuilder.withContent(content);
      return this;
    }


    @Override public SectionBreakFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
    {
      super.withJsonLdContext(jsonLdContext);
      return this;
    }

    @Override public SectionBreakFieldBuilder withJsonLdType(URI jsonLdType)
    {
      super.withJsonLdType(jsonLdType);
      return this;
    }

    @Override public SectionBreakFieldBuilder withJsonLdId(URI jsonLdId)
    {
      super.withJsonLdId(jsonLdId);
      return this;
    }

    @Override public SectionBreakFieldBuilder withName(String name)
    {
      super.withName(name);
      return this;
    }

    @Override public SectionBreakFieldBuilder withDescription(String description)
    {
      super.withDescription(description);
      return this;
    }

    @Override public SectionBreakFieldBuilder withIdentifier(String identifier)
    {
      super.withIdentifier(identifier);
      return this;
    }

    @Override public SectionBreakFieldBuilder withPreferredLabel(String preferredLabel)
    {
      super.withPreferredLabel(preferredLabel);
      return this;
    }

    @Override public SectionBreakFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public SectionBreakFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public SectionBreakFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    @Override public SectionBreakFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      return this;
    }

    @Override public SectionBreakFieldBuilder withRequiredValue(boolean requiredValue)
    {
      return this;
    }

    @Override public SectionBreakFieldBuilder withVersion(Version version)
    {
      super.withVersion(version);
      return this;
    }

    @Override public SectionBreakFieldBuilder withStatus(Status status)
    {
      super.withStatus(status);
      return this;
    }

    @Override public SectionBreakFieldBuilder withCreatedBy(URI createdBy)
    {
      super.withCreatedBy(createdBy);
      return this;
    }

    @Override public SectionBreakFieldBuilder withModifiedBy(URI modifiedBy)
    {
      super.withModifiedBy(modifiedBy);
      return this;
    }

    @Override public SectionBreakFieldBuilder withCreatedOn(OffsetDateTime createdOn)
    {
      super.withCreatedOn(createdOn);
      return this;
    }

    @Override public SectionBreakFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      super.withLastUpdatedOn(lastUpdatedOn);
      return this;
    }

    @Override public SectionBreakFieldBuilder withPreviousVersion(URI previousVersion)
    {
      super.withPreviousVersion(previousVersion);
      return this;
    }

    @Override public SectionBreakFieldBuilder withDerivedFrom(URI derivedFrom)
    {
      super.withDerivedFrom(derivedFrom);
      return this;
    }

    @Override public SectionBreakFieldBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    @Override public SectionBreakFieldBuilder withInternalName(String internalName)
    {
      super.withInternalName(internalName);
      return this;
    }

    @Override public SectionBreakFieldBuilder withInternalDescription(String internalDescription)
    {
      super.withInternalDescription(internalDescription);
      return this;
    }

    @Override public SectionBreakFieldBuilder withAnnotations(Annotations annotations)
    {
      super.withAnnotations(annotations);
      return this;
    }

    public SectionBreakField build()
    {
      withFieldUi(fieldUiBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, language,
        fieldUi, annotations, internalName, internalDescription);
    }
  }
}

record SectionBreakFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                               String name, String description, Optional<String> identifier, Optional<Version> version,
                               Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                               Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                               Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                               List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                               Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                               String internalName, String internalDescription) implements SectionBreakField
{
  public SectionBreakFieldRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateUriListFieldContainsOneOf(this, jsonLdTypes, JSON_LD_TYPE,
      Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, alternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, language, "language");
    validateUiFieldNotNull(this, fieldUi, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(this, annotations, "annotations");

    jsonLdContext = new LinkedHashMap<>(STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);

    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}