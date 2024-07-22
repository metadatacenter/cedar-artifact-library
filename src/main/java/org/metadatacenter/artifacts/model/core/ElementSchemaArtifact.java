package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.ui.ElementUi;
import org.metadatacenter.artifacts.model.core.ui.ParentArtifactUi;

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
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContains;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.UI;

public non-sealed interface ElementSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact, ParentSchemaArtifact
{
  static ElementSchemaArtifact create(String jsonSchemaTitle, String jsonSchemaDescription,
    LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> instanceJsonLdType,
    String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas,
    boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
    Optional<URI> propertyUri, Optional<String> preferredLabel, Optional<String> language,
    ElementUi elementUi, Optional<Annotations> annotations)
  {
    return new ElementSchemaArtifactRecord(jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      instanceJsonLdType,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas,
      isMultiple, minItems, maxItems,
      propertyUri, preferredLabel, language, elementUi, annotations);
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

  static Builder builder(ElementSchemaArtifact elementSchemaArtifact) {
    return new Builder(elementSchemaArtifact);
  }

  class Builder
  {
    private LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    private List<URI> jsonLdTypes = List.of(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    private Optional<URI> jsonLdId = Optional.empty();
    private Optional<URI> instanceJsonLdType = Optional.empty();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
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
    private LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    private LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    private boolean isMultiple = false;
    private Optional<Integer> minItems = Optional.empty();
    private Optional<Integer> maxItems = Optional.empty();
    private Optional<URI> propertyUri = Optional.empty();
    private Optional<String> preferredLabel = Optional.empty();
    private Optional<String> language = Optional.empty();
    private ElementUi.Builder elementUiBuilder = ElementUi.builder();
    private Optional<Annotations> annotations = Optional.empty();

    private Builder() {
    }

    private Builder(ElementSchemaArtifact elementSchemaArtifact)
    {
      this.jsonLdContext = new LinkedHashMap<>(elementSchemaArtifact.jsonLdContext());
      this.jsonLdTypes = new ArrayList<>(elementSchemaArtifact.jsonLdTypes());
      this.jsonLdId = elementSchemaArtifact.jsonLdId();
      this.instanceJsonLdType = elementSchemaArtifact.instanceJsonLdType();
      this.createdBy = elementSchemaArtifact.createdBy();
      this.modifiedBy = elementSchemaArtifact.modifiedBy();
      this.createdOn = elementSchemaArtifact.createdOn();
      this.lastUpdatedOn = elementSchemaArtifact.lastUpdatedOn();
      this.jsonSchemaTitle = elementSchemaArtifact.jsonSchemaTitle();
      this.jsonSchemaDescription = elementSchemaArtifact.jsonSchemaDescription();
      this.name = elementSchemaArtifact.name();
      this.description = elementSchemaArtifact.description();
      this.identifier = elementSchemaArtifact.identifier();
      this.modelVersion = elementSchemaArtifact.modelVersion();
      this.version = elementSchemaArtifact.version();
      this.status = elementSchemaArtifact.status();
      this.previousVersion = elementSchemaArtifact.previousVersion();
      this.derivedFrom = elementSchemaArtifact.derivedFrom();
      this.fieldSchemas = new LinkedHashMap<>(elementSchemaArtifact.fieldSchemas());
      this.elementSchemas = new LinkedHashMap<>(elementSchemaArtifact.elementSchemas());
      this.preferredLabel = elementSchemaArtifact.preferredLabel();
      this.language = elementSchemaArtifact.language();
      this.elementUiBuilder = ElementUi.builder(elementSchemaArtifact.elementUi());
      this.annotations = elementSchemaArtifact.annotations();
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

    public Builder withInstanceJsonLdType(URI instanceJsonLdType)
    {
      this.instanceJsonLdType = Optional.ofNullable(instanceJsonLdType);
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

    public Builder withPreferredLabel(String preferredLabel)
    {
      this.preferredLabel = Optional.ofNullable(preferredLabel);
      return this;
    }

    public Builder withLanguage(String language)
    {
      this.language = Optional.ofNullable(language);
      return this;
    }

    public Builder withAnnotations(Annotations annotations)
    {
      this.annotations = Optional.ofNullable(annotations);
      return this;
    }

    public ElementSchemaArtifact build()
    {
      return new ElementSchemaArtifactRecord(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId,
        instanceJsonLdType,
        name, description, identifier,
        modelVersion, version, status, previousVersion, derivedFrom,
        createdBy, modifiedBy, createdOn, lastUpdatedOn,
        fieldSchemas, elementSchemas,
        isMultiple, minItems, maxItems, propertyUri, preferredLabel, language, elementUiBuilder.build(), annotations);
    }
  }
}

record ElementSchemaArtifactRecord(String jsonSchemaTitle, String jsonSchemaDescription,
                                   LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                   Optional<URI> instanceJsonLdType,
                                   String name, String description, Optional<String> identifier,
                                   Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                                   Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                   LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
                                   LinkedHashMap<String, ElementSchemaArtifact> elementSchemas,
                                   boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                                   Optional<URI> propertyUri,
                                   Optional<String> preferredLabel, Optional<String> language, ElementUi elementUi,
                                   Optional<Annotations> annotations)  implements ElementSchemaArtifact
{
  public ElementSchemaArtifactRecord
  {
    validateStringFieldNotNull(this, jsonSchemaTitle, JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(this, jsonSchemaDescription, JSON_SCHEMA_DESCRIPTION);
    validateStringFieldNotEmpty(this, name, SCHEMA_ORG_NAME);
    validateStringFieldNotNull(this, description, SCHEMA_ORG_DESCRIPTION);
    validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    validateUriListFieldContains(this, jsonLdTypes, JSON_LD_TYPE, URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, instanceJsonLdType, "instanceJsonLdType");
    validateMapFieldNotNull(this, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(this, elementSchemas, "elementSchemas");
    validateUiFieldNotNull(this, elementUi, UI);
    validateOptionalFieldNotNull(this, propertyUri, "propertyUri");
    validateOptionalFieldNotNull(this, language,  "prefLabel");
    validateOptionalFieldNotNull(this, language,  "language");
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(this, annotations, "annotations");

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name());

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name());

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be less than maxItems in element schema artifact " + name());

    Set<String> order = new HashSet<>(elementUi.order());
    Set<String> childNames = Stream.concat(fieldSchemas.keySet().stream(), elementSchemas.keySet().stream()).collect(toSet());

    if (!order.containsAll(childNames)) {
      childNames.removeAll(order); // Generate the names of children not in the order map
      order.removeAll(childNames); // Silently remove these extra children from the order
      for (String childToRemove: childNames) { // And from the
        fieldSchemas.remove(childToRemove);
        elementSchemas.remove(childToRemove);
      }
    }

    jsonLdContext = new LinkedHashMap<>(jsonLdContext);
    jsonLdTypes = new ArrayList<>(jsonLdTypes);
    fieldSchemas = new LinkedHashMap<>(fieldSchemas);
    elementSchemas = new LinkedHashMap<>(elementSchemas);
  }
}

