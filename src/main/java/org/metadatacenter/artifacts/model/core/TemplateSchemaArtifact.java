package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.ui.ParentArtifactUi;
import org.metadatacenter.artifacts.model.core.ui.TemplateUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;

public non-sealed interface TemplateSchemaArtifact extends SchemaArtifact, ParentSchemaArtifact {
  static TemplateSchemaArtifact create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes,
                                       Optional<URI> jsonLdId, Optional<URI> instanceJsonLdType, String name,
                                       String description,
                                       Optional<String> identifier, Optional<Version> version,
                                       Optional<Status> status, Optional<URI> previousVersion,
                                       Optional<URI> derivedFrom, Optional<URI> createdBy, Optional<URI> modifiedBy,
                                       Optional<OffsetDateTime> createdOn,
                                       Optional<OffsetDateTime> lastUpdatedOn, LinkedHashMap<String,
      FieldSchemaArtifact> fieldSchemas,
                                       LinkedHashMap<String, ElementSchemaArtifact> elementSchemas,
                                       Optional<String> language, TemplateUi templateUi,
                                       Optional<Annotations> annotations, String internalName,
                                       String internalDescription) {
    return new TemplateSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, instanceJsonLdType, name, description,
        identifier, version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        fieldSchemas, elementSchemas, language, templateUi, annotations, internalName, internalDescription);
  }

  TemplateUi templateUi();

  default ParentArtifactUi getUi() {
    return templateUi();
  }

  default void accept(SchemaArtifactVisitor visitor) {
    String path = "/";

    visitor.visitTemplateSchemaArtifact(this);

    for (Map.Entry<String, FieldSchemaArtifact> entry : fieldSchemas().entrySet()) {
      String fieldKey = entry.getKey();
      String childPath = path + fieldKey;
      FieldSchemaArtifact fieldSchemaArtifact = entry.getValue();
      fieldSchemaArtifact.accept(visitor, childPath);
    }

    for (Map.Entry<String, ElementSchemaArtifact> entry : elementSchemas().entrySet()) {
      String fieldKey = entry.getKey();
      String childPath = path + fieldKey;
      ElementSchemaArtifact elementSchemaArtifact = entry.getValue();
      elementSchemaArtifact.accept(visitor, childPath);
    }
  }

  static Builder builder() {
    return new Builder();
  }

  static Builder builder(TemplateSchemaArtifact templateSchemaArtifact) {
    return new Builder(templateSchemaArtifact);
  }

  class Builder {
    private LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(
        PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    private List<URI> jsonLdTypes = List.of(URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    private Optional<URI> jsonLdId = Optional.empty();
    private Optional<URI> instanceJsonLdType = Optional.empty();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private String name;
    private String description = "";
    private Optional<String> identifier = Optional.empty();
    private Optional<Version> version = Optional.of(Version.DEFAULT);
    private Optional<Status> status = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    private LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    private Optional<String> language = Optional.empty();
    private TemplateUi.Builder templateUiBuilder = TemplateUi.builder();
    private Optional<Annotations> annotations = Optional.empty();
    private String internalName = "";
    private String internalDescription = "";

    private Builder() {
    }

    private Builder(TemplateSchemaArtifact templateSchemaArtifact) {
      this.jsonLdContext = new LinkedHashMap<>(templateSchemaArtifact.jsonLdContext());
      this.jsonLdTypes = new ArrayList<>(templateSchemaArtifact.jsonLdTypes());
      this.jsonLdId = templateSchemaArtifact.jsonLdId();
      this.instanceJsonLdType = templateSchemaArtifact.instanceJsonLdType();
      this.createdBy = templateSchemaArtifact.createdBy();
      this.modifiedBy = templateSchemaArtifact.modifiedBy();
      this.createdOn = templateSchemaArtifact.createdOn();
      this.lastUpdatedOn = templateSchemaArtifact.lastUpdatedOn();
      this.internalName = templateSchemaArtifact.internalName();
      this.internalDescription = templateSchemaArtifact.internalDescription();
      this.name = templateSchemaArtifact.name();
      this.description = templateSchemaArtifact.description();
      this.identifier = templateSchemaArtifact.identifier();
      this.version = templateSchemaArtifact.version();
      this.status = templateSchemaArtifact.status();
      this.previousVersion = templateSchemaArtifact.previousVersion();
      this.derivedFrom = templateSchemaArtifact.derivedFrom();
      this.fieldSchemas = new LinkedHashMap<>(templateSchemaArtifact.fieldSchemas());
      this.elementSchemas = new LinkedHashMap<>(templateSchemaArtifact.elementSchemas());
      this.language = templateSchemaArtifact.language();
      this.templateUiBuilder = TemplateUi.builder(templateSchemaArtifact.templateUi());
      this.annotations = templateSchemaArtifact.annotations();
    }

    public Builder withJsonLdId(URI jsonLdId) {
      this.jsonLdId = Optional.ofNullable(jsonLdId);
      return this;
    }

    public Builder withJsonLdType(URI jsonLdType) {
      this.jsonLdTypes.add(jsonLdType);
      return this;
    }

    public Builder withInstanceJsonLdType(URI instanceJsonLdType) {
      this.instanceJsonLdType = Optional.ofNullable(instanceJsonLdType);
      return this;
    }

    public Builder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext) {
      this.jsonLdContext = new LinkedHashMap<>(jsonLdContext);
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

    public Builder withInternalName(String internalName) {
      this.internalName = internalName;
      return this;
    }

    public Builder withInternalDescription(String internalDescription) {
      this.internalDescription = internalDescription;
      return this;
    }

    public Builder withIdentifier(String schemaOrgIdentifier) {
      this.identifier = Optional.ofNullable(schemaOrgIdentifier);
      return this;
    }

    public Builder withJsonLdTypes(List<URI> jsonLdTypes) {
      this.jsonLdTypes = List.copyOf(jsonLdTypes);
      return this;
    }

    public Builder withName(String name) {
      this.name = name;

      if (this.internalName.isEmpty()) {
        this.internalName = name + " template schema";
      }

      if (this.internalDescription.isEmpty()) {
        this.internalDescription = name + " template schema generated by the CEDAR Artifact Library";
      }

      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;

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

    public Builder withLanguage(String language) {
      this.language = Optional.ofNullable(language);
      return this;
    }

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact) {
      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.templateUiBuilder.withOrder(fieldKey);
      this.templateUiBuilder.withPropertyLabel(fieldKey, fieldSchemaArtifact.name());
      this.templateUiBuilder.withPropertyDescription(fieldKey, fieldSchemaArtifact.description());
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact) {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact);
    }

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel) {
      if (this.fieldSchemas.containsKey(fieldKey) || this.elementSchemas.containsKey(fieldKey)) {
        throw new IllegalArgumentException("Template already has a child " + fieldKey);
      }

      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.templateUiBuilder.withOrder(fieldKey);
      this.templateUiBuilder.withPropertyLabel(fieldKey, propertyLabel);
      return this;
    }

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel,
                                   String propertyDescription) {
      if (this.fieldSchemas.containsKey(fieldKey) || this.elementSchemas.containsKey(fieldKey)) {
        throw new IllegalArgumentException("Template already has a child " + fieldKey);
      }

      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.templateUiBuilder.withOrder(fieldKey);
      this.templateUiBuilder.withPropertyLabel(fieldKey, propertyLabel);
      this.templateUiBuilder.withPropertyDescription(fieldKey, propertyDescription);
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel) {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact, propertyLabel);
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel,
                                   String propertyDescription) {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact, propertyLabel, propertyDescription);
    }


    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact) {
      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.templateUiBuilder.withOrder(elementKey);
      this.templateUiBuilder.withPropertyLabel(elementKey, elementSchemaArtifact.name());
      this.templateUiBuilder.withPropertyDescription(elementKey, elementSchemaArtifact.description());
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact) {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact);
    }

    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact,
                                     String propertyLabel) {
      if (this.fieldSchemas.containsKey(elementKey) || this.elementSchemas.containsKey(elementKey)) {
        throw new IllegalArgumentException("Element already has a child " + elementKey);
      }

      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.templateUiBuilder.withOrder(elementKey);
      this.templateUiBuilder.withPropertyLabel(elementKey, propertyLabel);
      return this;
    }

    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact,
                                     String propertyLabel,
                                     String propertyDescription) {
      if (this.fieldSchemas.containsKey(elementKey) || this.elementSchemas.containsKey(elementKey)) {
        throw new IllegalArgumentException("Element already has a child " + elementKey);
      }

      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.templateUiBuilder.withOrder(elementKey);
      this.templateUiBuilder.withPropertyLabel(elementKey, propertyLabel);
      this.templateUiBuilder.withPropertyDescription(elementKey, propertyDescription);
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact, String propertyLabel) {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact, propertyLabel);
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact, String propertyLabel,
                                     String propertyDescription) {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact, propertyLabel, propertyDescription);
    }

    public Builder withoutFieldSchema(String fieldKey) {
      if (!this.fieldSchemas.containsKey(fieldKey)) {
        throw new IllegalArgumentException("Template has no field child " + fieldKey);
      }
      this.fieldSchemas.remove(fieldKey);
      this.templateUiBuilder.withoutOrder(fieldKey);
      this.templateUiBuilder.withoutPropertyLabel(fieldKey);
      this.templateUiBuilder.withoutPropertyDescription(fieldKey);
      return this;
    }

    public Builder withoutElementSchema(String elementKey) {
      if (!this.elementSchemas.containsKey(elementKey)) {
        throw new IllegalArgumentException("Template has no element child " + elementKey);
      }
      this.elementSchemas.remove(elementKey);
      this.templateUiBuilder.withoutOrder(elementKey);
      this.templateUiBuilder.withoutPropertyLabel(elementKey);
      this.templateUiBuilder.withoutPropertyDescription(elementKey);
      return this;
    }

    public Builder withHeader(String header) {
      this.templateUiBuilder.withHeader(header);
      return this;
    }

    public Builder withFooter(String footer) {
      this.templateUiBuilder.withFooter(footer);
      return this;
    }

    public Builder withAnnotations(Annotations annotations) {
      this.annotations = Optional.ofNullable(annotations);
      return this;
    }

    public TemplateSchemaArtifact build() {
      return new TemplateSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId,
          instanceJsonLdType, name, description, identifier, version, status, previousVersion, derivedFrom, createdBy,
          modifiedBy, createdOn, lastUpdatedOn, fieldSchemas, elementSchemas, language, templateUiBuilder.build(),
          annotations, internalName, internalDescription);
    }
  }
}

record TemplateSchemaArtifactRecord(
    LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes,
    Optional<URI> jsonLdId, Optional<URI> instanceJsonLdType, String name,
    String description, Optional<String> identifier, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas,
    Optional<String> language, TemplateUi templateUi, Optional<Annotations> annotations, String internalName,
    String internalDescription)
    implements TemplateSchemaArtifact {
  public TemplateSchemaArtifactRecord {
    ParentSchemaArtifactInvariants.validate(this, internalName, internalDescription, name, description,
      jsonLdContext, jsonLdTypes, URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI), jsonLdId, instanceJsonLdType,
      version, status, previousVersion, derivedFrom, fieldSchemas, elementSchemas, language, templateUi, annotations);

    ParentSchemaArtifactInvariants.pruneChildrenNotInOrder(fieldSchemas, elementSchemas, templateUi.order());

    jsonLdContext = new LinkedHashMap<>(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    fieldSchemas = new LinkedHashMap<>(fieldSchemas);
    elementSchemas = new LinkedHashMap<>(elementSchemas);
  }
}
