package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUIFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListContains;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;

public non-sealed interface ElementSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact, ParentSchemaArtifact
{
  static ElementSchemaArtifact create(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas,
    ElementUI elementUI,
    boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyURI)
  {
    return new ElementSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, name, description,
      identifier, modelVersion, version, status, previousVersion, derivedFrom, fieldSchemas, elementSchemas, elementUI,
      isMultiple, minItems, maxItems, propertyURI);
  }

  default ParentArtifactUI getUI() { return elementUI(); }

  ElementUI elementUI();

  default LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas()
  {
    LinkedHashMap<String, FieldSchemaArtifact> orderedFieldSchemas = new LinkedHashMap<>();

    for (String fieldName: getUI().order()) {
      if (fieldSchemas().containsKey(fieldName))
        orderedFieldSchemas.put(fieldName, fieldSchemas().get(fieldName));
    }
    return orderedFieldSchemas;
  }

  default LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas()
  {
    LinkedHashMap<String, ElementSchemaArtifact> orderedElementSchemas = new LinkedHashMap<>();

    for (String elementName : getUI().order()) {
      if (elementSchemas().containsKey(elementName))
        orderedElementSchemas.put(elementName, elementSchemas().get(elementName));
    }
    return orderedElementSchemas;
  }

  default ElementSchemaArtifact getElementSchemaArtifact(String name)
  {
    if (elementSchemas().containsKey(name))
      return elementSchemas().get(name);
    else
      throw new IllegalArgumentException("Element " + name + "not present in template " + name());
  }

  default FieldSchemaArtifact getFieldSchemaArtifact(String name)
  {
    if (fieldSchemas().containsKey(name))
      return fieldSchemas().get(name);
    else
      throw new IllegalArgumentException("Field " + name + "not present in element " + name());
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder
  {
    private List<URI> jsonLdTypes = List.of(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
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
    private Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO Put 0.0.1 in ModelNodeNames
    private Optional<Status> status = Optional.of(Status.DRAFT);
    private Optional<URI> previousVersion = Optional.empty();
    private Optional<URI> derivedFrom = Optional.empty();
    private ElementUI elementUI;
    private Map<String, FieldSchemaArtifact> fieldSchemas = new HashMap<>();
    private Map<String, ElementSchemaArtifact> elementSchemas = new HashMap<>();
    private boolean isMultiple = false;
    private Optional<Integer> minItems = Optional.empty();
    private Optional<Integer> maxItems = Optional.empty();
    private Optional<URI> propertyURI = Optional.empty();

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

    public Builder withElementUI(ElementUI elementUI)
    {
      this.elementUI = elementUI;
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
      this.minItems = Optional.ofNullable(maxItems);
      return this;
    }

    public Builder withPropertyURI(URI propertyURI)
    {
      this.propertyURI = Optional.ofNullable(propertyURI);
      return this;
    }

    public ElementSchemaArtifact build()
    {
      return new ElementSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, name, description,
        identifier, modelVersion, version, status, previousVersion, derivedFrom, fieldSchemas, elementSchemas, elementUI,
        isMultiple, minItems, maxItems, propertyURI);
    }
  }
}

record ElementSchemaArtifactRecord(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
  Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
  URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
  String name, String description, Optional<String> identifier,
  Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
  Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas,
  ElementUI elementUI, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
  Optional<URI> propertyURI)  implements ElementSchemaArtifact
{
  public ElementSchemaArtifactRecord
  {
    validateUriListContains(this, jsonLdTypes, JSON_LD_TYPE, URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    validateMapFieldNotNull(this, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(this, elementSchemas, "elementSchemas");
    validateUIFieldNotNull(this, elementUI, UI);
    validateOptionalFieldNotNull(this, propertyURI, "propertyURI");
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name());

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name());

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be lass than maxItems in element schema artifact " + name());

    Set<String> order = elementUI.order().stream().collect(toSet());
    Set<String> childNames = Stream.concat(fieldSchemas.keySet().stream(), elementSchemas.keySet().stream()).collect(toSet());

    if (!order.equals(childNames))
      throw new IllegalStateException(
        "UI order field must contain an entry for all child fields and elements in " + "element schema artifact " + name() + "; missing fields: " + childNames.removeAll(order));

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    fieldSchemas = Map.copyOf(fieldSchemas);
    elementSchemas = Map.copyOf(elementSchemas);
  }
}

