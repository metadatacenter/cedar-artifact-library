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

public sealed interface OrcidField extends FieldSchemaArtifact
{
    static OrcidField create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                           String name, String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
                           Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
                           Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                           Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
                           List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
                           Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                           String internalName, String internalDescription)
    {
        return new OrcidFieldRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
                previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
                lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
                internalDescription);
    }

    static OrcidFieldBuilder builder() { return new OrcidFieldBuilder(); }

    static OrcidFieldBuilder builder(OrcidField OrcidField) { return new OrcidFieldBuilder(OrcidField); }

    final class OrcidFieldBuilder extends FieldSchemaArtifactBuilder
    {
        private final FieldUi.Builder fieldUiBuilder;
        private final LinkValueConstraints.LinkValueConstraintsBuilder valueConstraintsBuilder;

        public OrcidFieldBuilder()
        {
            super(JSON_SCHEMA_OBJECT, FIELD_SCHEMA_ARTIFACT_TYPE_URI);
            withJsonLdContext(new LinkedHashMap<>(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
            this.fieldUiBuilder = FieldUi.builder().withInputType(FieldInputType.ORCID);
            this.valueConstraintsBuilder = LinkValueConstraints.builder();
        }

        public OrcidFieldBuilder(OrcidField orcidField)
        {
            super(orcidField);

            this.fieldUiBuilder = FieldUi.builder(orcidField.fieldUi());
            if (orcidField.valueConstraints().isPresent())
                this.valueConstraintsBuilder = LinkValueConstraints.builder(
                        orcidField.valueConstraints().get().asLinkValueConstraints());
            else
                this.valueConstraintsBuilder = LinkValueConstraints.builder();
        }

        public OrcidFieldBuilder withRequiredValue(boolean requiredValue)
        {
            valueConstraintsBuilder.withRequiredValue(requiredValue);
            return this;
        }

        public OrcidFieldBuilder withDefaultValue(URI uri)
        {
            valueConstraintsBuilder.withDefaultValue(uri);
            return this;
        }

        @Override public OrcidFieldBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
        {
            fieldUiBuilder.withValueRecommendationEnabled(valueRecommendationEnabled);
            return this;
        }

        @Override public OrcidFieldBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
        {
            super.withJsonLdContext(jsonLdContext);
            return this;
        }

        @Override public OrcidFieldBuilder withJsonLdType(URI jsonLdType)
        {
            super.withJsonLdType(jsonLdType);
            return this;
        }

        @Override public OrcidFieldBuilder withJsonLdId(URI jsonLdId)
        {
            super.withJsonLdId(jsonLdId);
            return this;
        }

        @Override public OrcidFieldBuilder withName(String name)
        {
            super.withName(name);
            return this;
        }

        @Override public OrcidFieldBuilder withDescription(String description)
        {
            super.withDescription(description);
            return this;
        }

        @Override public OrcidFieldBuilder withIdentifier(String identifier)
        {
            super.withIdentifier(identifier);
            return this;
        }

        @Override public OrcidFieldBuilder withVersion(Version version)
        {
            super.withVersion(version);
            return this;
        }

        @Override public OrcidFieldBuilder withStatus(Status status)
        {
            super.withStatus(status);
            return this;
        }

        @Override public OrcidFieldBuilder withCreatedBy(URI createdBy)
        {
            super.withCreatedBy(createdBy);
            return this;
        }

        @Override public OrcidFieldBuilder withModifiedBy(URI modifiedBy)
        {
            super.withModifiedBy(modifiedBy);
            return this;
        }

        @Override public OrcidFieldBuilder withCreatedOn(OffsetDateTime createdOn)
        {
            super.withCreatedOn(createdOn);
            return this;
        }

        @Override public OrcidFieldBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
        {
            super.withLastUpdatedOn(lastUpdatedOn);
            return this;
        }

        @Override public OrcidFieldBuilder withPreviousVersion(URI previousVersion)
        {
            super.withPreviousVersion(previousVersion);
            return this;
        }

        @Override public OrcidFieldBuilder withDerivedFrom(URI derivedFrom)
        {
            super.withDerivedFrom(derivedFrom);
            return this;
        }

        @Override public OrcidFieldBuilder withPreferredLabel(String preferredLabel)
        {
            super.withPreferredLabel(preferredLabel);
            return this;
        }

        @Override public OrcidFieldBuilder withAlternateLabels(List<String> alternateLabels)
        {
            super.withAlternateLabels(alternateLabels);
            return this;
        }

        @Override public OrcidFieldBuilder withIsMultiple(boolean isMultiple)
        {
            super.withIsMultiple(isMultiple);
            return this;
        }

        @Override public OrcidFieldBuilder withMinItems(Integer minItems)
        {
            super.withMinItems(minItems);
            return this;
        }

        @Override public OrcidFieldBuilder withMaxItems(Integer maxItems)
        {
            super.withMaxItems(maxItems);
            return this;
        }

        @Override public OrcidFieldBuilder withPropertyUri(URI propertyUri)
        {
            super.withPropertyUri(propertyUri);
            return this;
        }

        @Override public OrcidFieldBuilder withLanguage(String language)
        {
            super.withLanguage(language);
            return this;
        }

        @Override public OrcidFieldBuilder withInternalName(String internalName)
        {
            super.withInternalName(internalName);
            return this;
        }

        @Override public OrcidFieldBuilder withInternalDescription(String internalDescription)
        {
            super.withInternalDescription(internalDescription);
            return this;
        }

        @Override public OrcidFieldBuilder withAnnotations(Annotations annotations)
        {
            super.withAnnotations(annotations);
            return this;
        }

        @Override public OrcidFieldBuilder withHidden(boolean hidden)
        {
            fieldUiBuilder.withHidden(hidden);
            return this;
        }

        @Override public OrcidFieldBuilder withContinuePreviousLine(boolean continuePreviousLine)
        {
            fieldUiBuilder.withContinuePreviousLine(continuePreviousLine);
            return this;
        }

        @Override public OrcidFieldBuilder withRecommendedValue(boolean recommendedValue)
        {
            valueConstraintsBuilder.withRecommendedValue(recommendedValue);
            return this;
        }

        public OrcidField build()
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

record OrcidFieldRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                      String name, String description, Optional<String> identifier, Optional<Version> version,
                      Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                      boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                      Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
                      Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                      Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language,
                      FieldUi fieldUi, Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations,
                      String internalName, String internalDescription) implements OrcidField
{
    public OrcidFieldRecord
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