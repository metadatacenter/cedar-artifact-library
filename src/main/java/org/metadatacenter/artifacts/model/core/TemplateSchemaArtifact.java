package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.ui.ParentArtifactUi;
import org.metadatacenter.artifacts.model.core.ui.TemplateUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldContainsAll;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotEmpty;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldEquals;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContains;
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
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.PAV_DERIVED_FROM;
import static org.metadatacenter.model.ModelNodeNames.PAV_PREVIOUS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;

public non-sealed interface TemplateSchemaArtifact extends SchemaArtifact, ParentSchemaArtifact
{
  static TemplateSchemaArtifact create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription,
    LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas, LinkedHashMap<String, ElementSchemaArtifact> elementSchemas,
    Optional<String> language, TemplateUi templateUi, Optional<Annotations> annotations)
  {
    return new TemplateSchemaArtifactRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas, language, templateUi, annotations);
  }

  TemplateUi templateUi();

  default ParentArtifactUi getUi() { return templateUi(); }

  default void accept(SchemaArtifactVisitor visitor) {
    String path = "/";

    visitor.visitTemplateSchemaArtifact(this);

    for (Map.Entry<String, FieldSchemaArtifact> entry : fieldSchemas().entrySet()) {
      String fieldName = entry.getKey();
      String childPath = path + fieldName;
      FieldSchemaArtifact fieldSchemaArtifact = entry.getValue();
      fieldSchemaArtifact.accept(visitor, childPath);
    }

    for (Map.Entry<String, ElementSchemaArtifact> entry : elementSchemas().entrySet()) {
      String fieldName = entry.getKey();
      String childPath = path + fieldName;
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

  class Builder
  {
    private LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
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
    private Optional<String> identifier = Optional.empty();
    private Version modelVersion = new Version(1, 6, 0); // TODO
    private Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO
    private Optional<Status> status = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    private LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    private Optional<String> language = Optional.empty();
    private TemplateUi.Builder templateUiBuilder = TemplateUi.builder();
    private Optional<Annotations> annotations = Optional.empty();

    private Builder()
    {
    }

    private Builder(TemplateSchemaArtifact templateSchemaArtifact)
    {
      this.jsonLdContext = new LinkedHashMap<>(templateSchemaArtifact.jsonLdContext());
      this.jsonLdTypes = new ArrayList<>(templateSchemaArtifact.jsonLdTypes());
      this.jsonLdId = templateSchemaArtifact.jsonLdId();
      this.createdBy = templateSchemaArtifact.createdBy();
      this.modifiedBy = templateSchemaArtifact.modifiedBy();
      this.createdOn = templateSchemaArtifact.createdOn();
      this.lastUpdatedOn = templateSchemaArtifact.lastUpdatedOn();
      this.jsonSchemaSchemaUri = templateSchemaArtifact.jsonSchemaSchemaUri();
      this.jsonSchemaType = templateSchemaArtifact.jsonSchemaType();
      this.jsonSchemaTitle = templateSchemaArtifact.jsonSchemaTitle();
      this.jsonSchemaDescription = templateSchemaArtifact.jsonSchemaDescription();
      this.name = templateSchemaArtifact.name();
      this.description = templateSchemaArtifact.description();
      this.identifier = templateSchemaArtifact.identifier();
      this.modelVersion = templateSchemaArtifact.modelVersion();
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

    public Builder withJsonLdId(URI jsonLdId)
    {
      this.jsonLdId = Optional.ofNullable(jsonLdId);
      return this;
    }

    public Builder withJsonLdType(URI jsonLdType)
    {
      this.jsonLdTypes.add(jsonLdType);
      return this;
    }

    public Builder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
    {
      this.jsonLdContext = new LinkedHashMap<>(jsonLdContext);
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

    public Builder withSchemaOrgIdentifier(String schemaOrgIdentifier)
    {
      this.identifier = Optional.ofNullable(schemaOrgIdentifier);
      return this;
    }

    public Builder withJsonLdTypes(List<URI> jsonLdTypes)
    {
      this.jsonLdTypes = List.copyOf(jsonLdTypes);
      return this;
    }

    public Builder withName(String name)
    {
      this.name = name;

      if (this.jsonSchemaTitle.isEmpty())
        this.jsonSchemaTitle = name + " template schema";

      if (this.jsonSchemaDescription.isEmpty())
        this.jsonSchemaDescription = name + " template schema generated by the CEDAR Artifact Library";

      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;

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

    public Builder withLanguage(String language)
    {
      this.language = Optional.ofNullable(language);
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact)
    {
      this.fieldSchemas.put(fieldSchemaArtifact.name(), fieldSchemaArtifact);
      this.templateUiBuilder.withOrder(fieldSchemaArtifact.name());
      this.templateUiBuilder.withPropertyLabel(fieldSchemaArtifact.name(), fieldSchemaArtifact.name());
      this.templateUiBuilder.withPropertyDescription(fieldSchemaArtifact.name(), fieldSchemaArtifact.description());
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel, String propertyDescription)
    {
      this.fieldSchemas.put(fieldSchemaArtifact.name(), fieldSchemaArtifact);
      this.templateUiBuilder.withOrder(fieldSchemaArtifact.name());
      this.templateUiBuilder.withPropertyLabel(fieldSchemaArtifact.name(), propertyLabel);
      this.templateUiBuilder.withPropertyDescription(fieldSchemaArtifact.name(), propertyDescription);
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact)
    {
      this.elementSchemas.put(elementSchemaArtifact.name(), elementSchemaArtifact);
      this.templateUiBuilder.withOrder(elementSchemaArtifact.name());
      this.templateUiBuilder.withPropertyLabel(elementSchemaArtifact.name(), elementSchemaArtifact.name());
      this.templateUiBuilder.withPropertyDescription(elementSchemaArtifact.name(), elementSchemaArtifact.description());
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact, String propertyLabel, String propertyDescription)
    {
      this.elementSchemas.put(elementSchemaArtifact.name(), elementSchemaArtifact);
      this.templateUiBuilder.withOrder(elementSchemaArtifact.name());
      this.templateUiBuilder.withPropertyLabel(elementSchemaArtifact.name(), propertyLabel);
      this.templateUiBuilder.withPropertyDescription(elementSchemaArtifact.name(), propertyDescription);
      return this;
    }

    public Builder withHeader(String header)
    {
      this.templateUiBuilder.withHeader(header);
      return this;
    }

    public Builder withFooter(String footer)
    {
      this.templateUiBuilder.withFooter(footer);
      return this;
    }

    public Builder withAnnotations(Annotations annotations)
    {
      this.annotations = Optional.ofNullable(annotations);
      return this;
    }

    public TemplateSchemaArtifact build()
    {
      return new TemplateSchemaArtifactRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId,
        name, description, identifier,
        modelVersion, version, status, previousVersion, derivedFrom,
        createdBy, modifiedBy, createdOn, lastUpdatedOn,
        fieldSchemas, elementSchemas,
        language, templateUiBuilder.build(), annotations);
    }
  }
}

record TemplateSchemaArtifactRecord(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
                                    LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                    String name, String description, Optional<String> identifier,
                                    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                                    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
                                    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas,
                                    Optional<String> language, TemplateUi templateUi, Optional<Annotations> annotations)
  implements TemplateSchemaArtifact
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
    validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    validateUriListFieldContains(this, jsonLdTypes, JSON_LD_TYPE, URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    validateMapFieldNotNull(this, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(this, elementSchemas, "elementSchemas");
    validateOptionalFieldNotNull(this, language,  "language");
    validateUiFieldNotNull(this, templateUi, UI);
    validateOptionalFieldNotNull(this, annotations, "annotations");

    Set<String> order = new HashSet<>(templateUi.order());
    Set<String> childNames = Stream.concat(fieldSchemas.keySet().stream(), elementSchemas.keySet().stream()).collect(toSet());

    if (!order.containsAll(childNames)) {
      childNames.removeAll(order);
      throw new IllegalStateException(
        "UI order field must contain an entry for all child fields and elements in " + "template schema artifact " +
          name + "; missing fields: " + childNames);
    }

    jsonLdContext = new LinkedHashMap<>(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    fieldSchemas = new LinkedHashMap<>(fieldSchemas);
    elementSchemas = new LinkedHashMap<>(elementSchemas);
  }
}
