package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_URI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_STRING;

public sealed interface AttributeValueField extends FieldSchemaArtifact
{
  static AttributeValueField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes,
    Optional<URI> jsonLdId, String name, String description, Optional<String> identifier, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations, String internalName,
    String internalDescription)
  {
    return new AttributeValueFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
      status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
      createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
      internalName, internalDescription);
  }

  static AttributeValueFieldBuilder builder() {return new AttributeValueFieldBuilder();}

  static AttributeValueFieldBuilder builder(AttributeValueField attributeValueField)
  {
    return new AttributeValueFieldBuilder(attributeValueField);
  }

  final class AttributeValueFieldBuilder extends FieldSchemaArtifactBuilder<AttributeValueField.AttributeValueFieldBuilder>
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

    @Override public AttributeValueFieldBuilder withRequiredValue(boolean requiredValue)
    {
      return this;
    }

    @Override public AttributeValueFieldBuilder withRecommendedValue(boolean recommendedValue)
    {
      return this;
    }

    @Override public AttributeValueFieldBuilder withHidden(boolean hidden)
    {
      fieldUiBuilder.withHidden(hidden);
      return this;
    }

    @Override public AttributeValueFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
      return this;
    }

    @Override public AttributeValueFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
      return this;
    }

    public AttributeValueField build()
    {
      withFieldUi(fieldUiBuilder.build());
      return create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
    }
  }
}

record AttributeValueFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes,
                                 Optional<URI> jsonLdId, String name, String description, Optional<String> identifier,
                                 Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion,
                                 Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
                                 Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
                                 Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
                                 Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                                 List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                                 Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                                 String internalName, String internalDescription) implements AttributeValueField
{
  public AttributeValueFieldRecord
  {
    FieldSchemaArtifactInvariants.validate(this, name, jsonLdContext, jsonLdTypes,
      preferredLabel, alternateLabels, minItems, maxItems, propertyUri, language,
      fieldUi, valueConstraints, annotations);

    if (!fieldUi.isAttributeValue())
      throw new IllegalStateException("field UI must specify attribute-value type in attribute-value field " + name);

    jsonLdContext = FieldSchemaArtifactInvariants.canonicalContext(fieldUi);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
