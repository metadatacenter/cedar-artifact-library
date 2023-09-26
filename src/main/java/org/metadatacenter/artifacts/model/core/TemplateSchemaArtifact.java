package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapContainsAll;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldEquals;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListContains;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateVersionFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.BIBO_STATUS;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.PAV_DERIVED_FROM;
import static org.metadatacenter.model.ModelNodeNames.PAV_PREVIOUS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;

public non-sealed interface TemplateSchemaArtifact extends SchemaArtifact, ParentSchemaArtifact
{
  static TemplateSchemaArtifact create(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom,
    Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas,
    TemplateUi templateUi)
  {
    return new TemplateSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, name,
      description, identifier, modelVersion, version, status, previousVersion,
      derivedFrom, fieldSchemas, elementSchemas, templateUi);
  }

  TemplateUi templateUi();

  default ParentArtifactUi getUi() { return templateUi(); }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private Map<String, URI> jsonLdContext = new HashMap<>(SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    private List<URI> jsonLdTypes = List.of(URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    private Optional<URI> jsonLdId = Optional.empty();
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
    private Optional<String> schemaOrgIdentifier = Optional.empty();
    private Version modelVersion = new Version(1, 6, 0); // TODO
    private Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO
    private Optional<Status> status = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    private LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    private TemplateUi templateUi = TemplateUi.builder().build();

    private Builder() {
    }

    public Builder withJsonLdId(URI jsonLdId) {
      this.jsonLdId = Optional.ofNullable(jsonLdId);
      return this;
    }

    public Builder withJsonLdType(URI jsonLdType) {
      this.jsonLdTypes.add(jsonLdType);
      return this;
    }

    public Builder withJsonLdContext(Map<String, URI> jsonLdContext) {
      this.jsonLdContext = Map.copyOf(jsonLdContext);
      return this;
    }

    public Builder withCreatedBy(URI createdBy) {
      this.createdBy = Optional.ofNullable(createdBy);
      return this;
    }

    public Builder withModifiedBy(URI modifiedBy) {
      this.modifiedBy = Optional.ofNullable(modifiedBy);
      return this;
    }

    public Builder withCreatedOn(OffsetDateTime createdOn) {
      this.createdOn = Optional.ofNullable(createdOn);
      return this;
    }

    public Builder withLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
      this.lastUpdatedOn = Optional.ofNullable(lastUpdatedOn);
      return this;
    }

    public Builder withJsonSchemaSchemaUri(URI jsonSchemaSchemaUri) {
      this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
      return this;
    }

    public Builder withJsonSchemaType(String jsonSchemaType) {
      this.jsonSchemaType = jsonSchemaType;
      return this;
    }

    public Builder withJsonSchemaTitle(String jsonSchemaTitle) {
      this.jsonSchemaTitle = jsonSchemaTitle;
      return this;
    }

    public Builder withJsonSchemaDescription(String jsonSchemaDescription) {
      this.jsonSchemaDescription = jsonSchemaDescription;
      return this;
    }

    public Builder withSchemaOrgIdentifier(String schemaOrgIdentifier) {
      this.schemaOrgIdentifier = Optional.ofNullable(schemaOrgIdentifier);
      return this;
    }

    public Builder withJsonLdTypes(List<URI> jsonLdTypes) {
      this.jsonLdTypes = jsonLdTypes;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;

      if (this.jsonSchemaTitle.isEmpty())
        this.jsonSchemaTitle = name + " template schema";

      if (this.jsonSchemaDescription.isEmpty())
        this.jsonSchemaDescription = name + " template schema generated by the CEDAR Artifact Library";

      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;

      return this;
    }

    public Builder withModelVersion(Version modelVersion) {
      this.modelVersion = modelVersion;
      return this;
    }

    public Builder withVersion(Version version) {
      this.version = Optional.ofNullable(version);
      return this;
    }

    public Builder withStatus(Status status) {
      this.status = Optional.ofNullable(status);
      return this;
    }

    public Builder withPreviousVersion(URI previousVersion) {
      this.previousVersion = Optional.ofNullable(previousVersion);
      return this;
    }

    public Builder withDerivedFrom(URI derivedFrom) {
      this.derivedFrom = Optional.ofNullable(derivedFrom);
      return this;
    }

    public Builder withFieldSchema(String fieldName, FieldSchemaArtifact fieldSchemaArtifact)
    {
      this.fieldSchemas.put(fieldName, fieldSchemaArtifact);
      return this;
    }

    public Builder withElementSchema(String elementName, ElementSchemaArtifact elementSchemaArtifact)
    {
      this.elementSchemas.put(elementName, elementSchemaArtifact);
      return this;
    }

    public Builder withTemplateUi(TemplateUi templateUi) {
      this.templateUi = templateUi;
      return this;
    }

    public TemplateSchemaArtifact build()
    {
      return new TemplateSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, name, description, schemaOrgIdentifier, modelVersion,
        version, status, previousVersion,
        derivedFrom, fieldSchemas, elementSchemas, templateUi);
    }
  }
}

record TemplateSchemaArtifactRecord(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
                                    String name, String description, Optional<String> identifier,
                                    Version modelVersion, Optional<Version> version, Optional<Status> status,
                                    Optional<URI> previousVersion, Optional<URI> derivedFrom,
                                    Map<String, FieldSchemaArtifact> fieldSchemas,
                                    Map<String, ElementSchemaArtifact> elementSchemas,
                                    TemplateUi templateUi) implements TemplateSchemaArtifact
{
  public TemplateSchemaArtifactRecord {
    validateUriFieldEquals(this, jsonSchemaSchemaUri, JSON_SCHEMA_SCHEMA, URI.create(JSON_SCHEMA_SCHEMA_IRI));
    validateStringFieldNotNull(this, jsonSchemaType, JSON_SCHEMA_TYPE);
    validateStringFieldNotNull(this, jsonSchemaTitle, JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(this, jsonSchemaDescription, JSON_SCHEMA_DESCRIPTION);
    validateStringFieldNotEmpty(this, name, SCHEMA_ORG_NAME);
    validateStringFieldNotNull(this, description, SCHEMA_ORG_DESCRIPTION);
    validateVersionFieldNotNull(this, modelVersion, SCHEMA_ORG_SCHEMA_VERSION);
    validateOptionalFieldNotNull(this, version, PAV_VERSION);
    validateOptionalFieldNotNull(this, status, BIBO_STATUS);
    validateOptionalFieldNotNull(this, previousVersion, PAV_PREVIOUS_VERSION);
    validateOptionalFieldNotNull(this, derivedFrom, PAV_DERIVED_FROM);
    validateMapContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    validateUriListContains(this, jsonLdTypes, JSON_LD_TYPE, URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    validateMapFieldNotNull(this, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(this, elementSchemas, "elementSchemas");
    validateUiFieldNotNull(this, templateUi, UI);

    Set<String> order = templateUi.order().stream().collect(toSet());
    Set<String> childNames = Stream.concat(fieldSchemas.keySet().stream(), elementSchemas.keySet().stream()).collect(toSet());

    if (!order.containsAll(childNames)) {
      childNames.removeAll(order);
      throw new IllegalStateException(
        "UI order field must contain an entry for all child fields and elements in " + "template schema artifact " +
          name + "; missing fields: " + childNames);
    }

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    fieldSchemas = Map.copyOf(fieldSchemas);
    elementSchemas = Map.copyOf(elementSchemas);
  }
}
