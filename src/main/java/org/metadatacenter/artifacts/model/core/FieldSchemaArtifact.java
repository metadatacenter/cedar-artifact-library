package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUIFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListContainsOneOf;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;

public non-sealed interface FieldSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact
{
  static FieldSchemaArtifact create(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription, String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    FieldUI fieldUI, Optional<ValueConstraints> valueConstraints, Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
    boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyURI)
  {
    return new FieldSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, name, description,
      identifier, modelVersion, version, status, previousVersion, derivedFrom, fieldUI, valueConstraints, skosPrefLabel,
      skosAlternateLabels, isMultiple, minItems, maxItems, propertyURI);
  }

  FieldUI fieldUI();

  Optional<ValueConstraints> valueConstraints();

  Optional<String> skosPrefLabel();

  List<String> skosAlternateLabels();

  default boolean isHidden() { return fieldUI().isHidden(); }

  default boolean isStatic() { return fieldUI().isStatic(); }

  default boolean hasIRIValue()
  {
    return (fieldUI().isTextField() && valueConstraints().isPresent() && valueConstraints().get().hasOntologyValueBasedConstraints()) || fieldUI().isLink() || fieldUI().isImage() || fieldUI().isYouTube();
  }

  static Builder builder() { return new Builder(); }

  class Builder
  {
    private List<URI> jsonLdTypes = List.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI));
    private Optional<URI> jsonLdId = Optional.empty();
    private Map<String, URI> jsonLdContext = new HashMap<>();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private URI jsonSchemaSchemaUri = URI.create(JSON_SCHEMA_SCHEMA_IRI);
    private String jsonSchemaType = JSON_SCHEMA_OBJECT;
    private String jsonSchemaTitle = "";
    private String jsonSchemaDescription = "";
    private String name;
    private String description = "";
    private Optional<String> identifier = Optional.empty();
    private Version modelVersion = new Version(1, 6, 0); // TODO Put 1.6.0 in ModelNodeNames
    private Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO Put 0.0.1. in ModelNodeNames
    private Optional<Status> status = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private FieldUI fieldUI;
    private Optional<ValueConstraints> valueConstraints = Optional.empty();
    private Optional<String> skosPrefLabel = Optional.empty();
    private List<String> skosAlternateLabels = Collections.emptyList();
    private boolean isMultiple = false;
    private Optional<Integer> minItems = Optional.empty();
    private Optional<Integer> maxItems = Optional.empty();
    private Optional<URI> propertyURI = Optional.empty();

    private Builder() {}

    public Builder withJsonLdId(URI jsonLdId)
    {
      this.jsonLdId = Optional.ofNullable(jsonLdId);
      return this;
    }

    public Builder withJsonLdContext(Map<String, URI> jsonLdContext)
    {
      this.jsonLdContext = jsonLdContext;
      return this;
    }

    public Builder withCreatedBy(URI createdBy)
    {
      this.createdBy = Optional.ofNullable(createdBy);
      return this;
    }

    public Builder withModifiedBy(URI modifiedBy)
    {
      this.modifiedBy = Optional.ofNullable(modifiedBy);
      return this;
    }

    public Builder withCreatedOn(OffsetDateTime createdOn)
    {
      this.createdOn = Optional.ofNullable(createdOn);
      return this;
    }

    public Builder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
    {
      this.lastUpdatedOn = Optional.ofNullable(lastUpdatedOn);
      return this;
    }

    public Builder withJsonSchemaSchemaUri(URI jsonSchemaSchemaUri)
    {
      this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
      return this;
    }

    public Builder withJsonSchemaType(String jsonSchemaType)
    {
      this.jsonSchemaType = jsonSchemaType;
      return this;
    }

    public Builder withJsonSchemaTitle(String jsonSchemaTitle)
    {
      this.jsonSchemaTitle = jsonSchemaTitle;
      return this;
    }

    public Builder withJsonSchemaDescription(String jsonSchemaDescription)
    {
      this.jsonSchemaDescription = jsonSchemaDescription;
      return this;
    }

    public Builder withJsonLdTypes(List<URI> jsonLdTypes)
    {
      this.jsonLdTypes = jsonLdTypes;
      return this;
    }

    public Builder withName(String name)
    {
      this.name = name;

      if (this.jsonSchemaTitle.isEmpty())
        this.jsonSchemaTitle = name + " field schema";

      if (this.jsonSchemaDescription.isEmpty())
        this.jsonSchemaDescription = name + " field schema generated by the CEDAR Artifact Library";

      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
      return this;
    }

    public Builder withIdentifier(String identifier)
    {
      this.identifier = Optional.ofNullable(identifier);
      return this;
    }

    public Builder withModelVersion(Version modelVersion)
    {
      this.modelVersion = modelVersion;
      return this;
    }

    public Builder withVersion(Version version)
    {
      this.version = Optional.ofNullable(version);
      return this;
    }

    public Builder withStatus(Status status)
    {
      this.status = Optional.ofNullable(status);
      return this;
    }

    public Builder withPreviousVersion(URI previousVersion)
    {
      this.previousVersion = Optional.ofNullable(previousVersion);
      return this;
    }

    public Builder withDerivedFrom(URI derivedFrom)
    {
      this.derivedFrom = Optional.ofNullable(derivedFrom);
      return this;
    }

    public Builder withFieldUI(FieldUI fieldUI)
    {
      this.fieldUI = fieldUI;
      return this;
    }

    public Builder withValueConstraints(ValueConstraints valueConstraints)
    {
      this.valueConstraints = Optional.ofNullable(valueConstraints);
      return this;
    }

    public Builder withSkosPrefLabel(String skosPrefLabel)
    {
      this.skosPrefLabel = Optional.ofNullable(skosPrefLabel);
      return this;
    }

    public Builder withSkosAlternateLabels(List<String> skosAlternateLabels)
    {
      this.skosAlternateLabels = skosAlternateLabels;
      return this;
    }

    public Builder withIsMultiple(boolean isMultiple)
    {
      this.isMultiple = isMultiple;
      return this;
    }

    public FieldSchemaArtifact.Builder withMinItems(Integer minItems)
    {
      this.minItems = Optional.ofNullable(minItems);
      return this;
    }

    public FieldSchemaArtifact.Builder withMaxItems(Integer maxItems)
    {
      this.minItems = Optional.ofNullable(maxItems);
      return this;
    }

    public Builder withPropertyURI(URI propertyURI)
    {
      this.propertyURI = Optional.ofNullable(propertyURI);
      return this;
    }

    public FieldSchemaArtifact build()
    {
      return new FieldSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, name, description,
        identifier, modelVersion, version, status, previousVersion, derivedFrom, fieldUI, valueConstraints,
        skosPrefLabel, skosAlternateLabels, isMultiple, minItems, maxItems, propertyURI);
    }
  }
}

record FieldSchemaArtifactRecord(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                 Optional<URI> createdBy, Optional<URI> modifiedBy,
                                 Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                 URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
                                 String name, String description, Optional<String> identifier,
                                 Version modelVersion, Optional<Version> version, Optional<Status> status,
                                 Optional<URI> previousVersion, Optional<URI> derivedFrom,
                                 FieldUI fieldUI, Optional<ValueConstraints> valueConstraints,
                                 Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
                                 boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                                 Optional<URI> propertyURI)
  implements FieldSchemaArtifact
{
  public FieldSchemaArtifactRecord
  {
    validateUriListContainsOneOf(this, jsonLdTypes, ModelNodeNames.JSON_LD_TYPE, Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateUIFieldNotNull(this, fieldUI, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);
    validateOptionalFieldNotNull(this, skosPrefLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, skosAlternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(this, propertyURI, "propertyURI"); // TODO Add to ModelNodeNames

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name);

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name);

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be lass than maxItems in element schema artifact " + name);

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
