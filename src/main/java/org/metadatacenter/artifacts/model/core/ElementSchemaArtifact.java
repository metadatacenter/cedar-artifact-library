package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.ui.ElementUi;
import org.metadatacenter.artifacts.model.core.ui.ParentArtifactUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
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
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContains;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.UI;

public non-sealed interface ElementSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact, ParentSchemaArtifact
{
  static ElementSchemaArtifact create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas,
    ElementUi elementUi,
    boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri)
  {
    return new ElementSchemaArtifactRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas, elementUi,
      isMultiple, minItems, maxItems, propertyUri);
  }

  default ParentArtifactUi getUi() { return elementUi(); }

  ElementUi elementUi();

  @Override default void accept(SchemaArtifactVisitor visitor, String path) {
    visitor.visitElementSchemaArtifact(this, path);

    for (Map.Entry<String, FieldSchemaArtifact> entry : fieldSchemas().entrySet()) {
      String fieldName = entry.getKey();
      String childPath = path + "/" + fieldName;
      FieldSchemaArtifact fieldSchemaArtifact = entry.getValue();
      fieldSchemaArtifact.accept(visitor, childPath);
    }

    for (Map.Entry<String, ElementSchemaArtifact> entry : elementSchemas().entrySet()) {
      String fieldName = entry.getKey();
      String childPath = path + "/" + fieldName;
      ElementSchemaArtifact elementSchemaArtifact = entry.getValue();
      elementSchemaArtifact.accept(visitor, childPath);
    }
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder
  {
    private Map<String, URI> jsonLdContext = new HashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    private List<URI> jsonLdTypes = List.of(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
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
    private Version modelVersion = new Version(1, 6, 0); // TODO Put 1.6.0 in ModelNodeNames
    private Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO Put 0.0.1 in ModelNodeNames
    private Optional<Status> status = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private final Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    private final Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    private boolean isMultiple = false;
    private Optional<Integer> minItems = Optional.empty();
    private Optional<Integer> maxItems = Optional.empty();
    private Optional<URI> propertyUri = Optional.empty();
    private final ElementUi.Builder elementUiBuilder = ElementUi.builder();

    private Builder() {
    }

    public Builder withJsonLdType(URI jsonLdType)
    {
      this.jsonLdTypes.add(jsonLdType);
      return this;
    }

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
        this.jsonSchemaTitle = name + " element schema";

      if (this.jsonSchemaDescription.isEmpty())
        this.jsonSchemaDescription = name + " element schema generated by the CEDAR Artifact Library";

      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
      return this;
    }

    public Builder withSchemaOrgIdentifier(String identifier)
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

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact)
    {
      this.fieldSchemas.put(fieldSchemaArtifact.name(), fieldSchemaArtifact);
      this.elementUiBuilder.withOrder(fieldSchemaArtifact.name());
      this.elementUiBuilder.withPropertyLabel(fieldSchemaArtifact.name(), fieldSchemaArtifact.name());
      this.elementUiBuilder.withPropertyDescription(fieldSchemaArtifact.name(), fieldSchemaArtifact.description());
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel, String propertyDescription)
    {
      this.fieldSchemas.put(fieldSchemaArtifact.name(), fieldSchemaArtifact);
      this.elementUiBuilder.withOrder(fieldSchemaArtifact.name());
      this.elementUiBuilder.withPropertyLabel(fieldSchemaArtifact.name(), propertyLabel);
      this.elementUiBuilder.withPropertyDescription(fieldSchemaArtifact.name(), propertyDescription);
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact)
    {
      this.elementSchemas.put(elementSchemaArtifact.name(), elementSchemaArtifact);
      this.elementUiBuilder.withOrder(elementSchemaArtifact.name());
      this.elementUiBuilder.withPropertyLabel(elementSchemaArtifact.name(), elementSchemaArtifact.name());
      this.elementUiBuilder.withPropertyDescription(elementSchemaArtifact.name(), elementSchemaArtifact.description());
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact, String propertyLabel, String propertyDescription)
    {
      this.elementSchemas.put(elementSchemaArtifact.name(), elementSchemaArtifact);
      this.elementUiBuilder.withOrder(elementSchemaArtifact.name());
      this.elementUiBuilder.withPropertyLabel(elementSchemaArtifact.name(), propertyLabel);
      this.elementUiBuilder.withPropertyDescription(elementSchemaArtifact.name(), propertyDescription);
      return this;
    }

    public Builder withIsMultiple(boolean isMultiple)
    {
      this.isMultiple = isMultiple;
      return this;
    }

    public Builder withMinItems(Integer minItems)
    {
      this.minItems = Optional.ofNullable(minItems);
      return this;
    }

    public Builder withMaxItems(Integer maxItems)
    {
      this.maxItems = Optional.ofNullable(maxItems);
      return this;
    }

    public Builder withPropertyUri(URI propertyUri)
    {
      this.propertyUri = Optional.ofNullable(propertyUri);
      return this;
    }

    public ElementSchemaArtifact build()
    {
      return new ElementSchemaArtifactRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId,
        name, description, identifier,
        modelVersion, version, status, previousVersion, derivedFrom,
        createdBy, modifiedBy, createdOn, lastUpdatedOn,
        fieldSchemas, elementSchemas,
        elementUiBuilder.build(),
        isMultiple, minItems, maxItems, propertyUri);
    }
  }
}

record ElementSchemaArtifactRecord(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
                                   Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                   String name, String description, Optional<String> identifier,
                                   Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                                   Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                   Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas,
                                   ElementUi elementUi, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                                   Optional<URI> propertyUri)  implements ElementSchemaArtifact
{
  public ElementSchemaArtifactRecord
  {
    validateStringFieldNotNull(this, jsonSchemaType, JSON_SCHEMA_TYPE);
    validateStringFieldNotNull(this, jsonSchemaTitle, JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(this, jsonSchemaDescription, JSON_SCHEMA_DESCRIPTION);
    validateStringFieldNotEmpty(this, name, SCHEMA_ORG_NAME);
    validateStringFieldNotNull(this, description, SCHEMA_ORG_DESCRIPTION);
    validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    validateUriListFieldContains(this, jsonLdTypes, JSON_LD_TYPE, URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    validateMapFieldNotNull(this, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(this, elementSchemas, "elementSchemas");
    validateUiFieldNotNull(this, elementUi, UI);
    validateOptionalFieldNotNull(this, propertyUri, "propertyUri");
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name());

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name());

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be less than maxItems in element schema artifact " + name());

    Set<String> order = elementUi.order().stream().collect(toSet());
    Set<String> childNames = Stream.concat(fieldSchemas.keySet().stream(), elementSchemas.keySet().stream()).collect(toSet());

    if (!order.containsAll(childNames)) {
      childNames.removeAll(order);
      throw new IllegalStateException(
        "UI order field must contain an entry for all child fields and elements in element schema artifact " +
          name + "; missing fields: " + childNames);
    }

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    fieldSchemas = Map.copyOf(fieldSchemas);
    elementSchemas = Map.copyOf(elementSchemas);
  }
}

