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
  static ElementSchemaArtifact create(String internalName, String internalDescription,
    LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> instanceJsonLdType, String name, String description, Optional<String> identifier,
    Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
    Optional<OffsetDateTime> lastUpdatedOn, LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<String> language, ElementUi elementUi,
    Optional<Annotations> annotations)
  {
    return new ElementSchemaArtifactRecord(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId,
      instanceJsonLdType, name, description, identifier, version, status, previousVersion, derivedFrom, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, fieldSchemas, elementSchemas, isMultiple, minItems, maxItems, propertyUri,
      language, elementUi, annotations);
  }

  default ParentArtifactUi getUi() {return elementUi();}

  ElementUi elementUi();

  @Override default void accept(SchemaArtifactVisitor visitor, String path)
  {
    visitor.visitElementSchemaArtifact(this, path);

    for (Map.Entry<String, FieldSchemaArtifact> entry : fieldSchemas().entrySet()) {
      String fieldKey = entry.getKey();
      String childPath = path + "/" + fieldKey;
      FieldSchemaArtifact fieldSchemaArtifact = entry.getValue();
      fieldSchemaArtifact.accept(visitor, childPath);
    }

    for (Map.Entry<String, ElementSchemaArtifact> entry : elementSchemas().entrySet()) {
      String fieldKey = entry.getKey();
      String childPath = path + "/" + fieldKey;
      ElementSchemaArtifact elementSchemaArtifact = entry.getValue();
      elementSchemaArtifact.accept(visitor, childPath);
    }
  }

  static Builder builder()
  {
    return new Builder();
  }

  static Builder builder(ElementSchemaArtifact elementSchemaArtifact)
  {
    return new Builder(elementSchemaArtifact);
  }

  class Builder
  {
    private LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(
      PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    private List<URI> jsonLdTypes = List.of(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    private Optional<URI> jsonLdId = Optional.empty();
    private Optional<URI> instanceJsonLdType = Optional.empty();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private String internalName = "";
    private String internalDescription = "";
    private String name;
    private String description = "";
    private Optional<String> identifier = Optional.empty();
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
    private Optional<String> language = Optional.empty();
    private ElementUi.Builder elementUiBuilder = ElementUi.builder();
    private Optional<Annotations> annotations = Optional.empty();

    private Builder()
    {
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
      this.internalName = elementSchemaArtifact.internalName();
      this.internalDescription = elementSchemaArtifact.internalDescription();
      this.name = elementSchemaArtifact.name();
      this.description = elementSchemaArtifact.description();
      this.identifier = elementSchemaArtifact.identifier();
      this.version = elementSchemaArtifact.version();
      this.status = elementSchemaArtifact.status();
      this.previousVersion = elementSchemaArtifact.previousVersion();
      this.derivedFrom = elementSchemaArtifact.derivedFrom();
      this.fieldSchemas = new LinkedHashMap<>(elementSchemaArtifact.fieldSchemas());
      this.elementSchemas = new LinkedHashMap<>(elementSchemaArtifact.elementSchemas());
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

    public Builder withInternalName(String internalName)
    {
      this.internalName = internalName;
      return this;
    }

    public Builder withInternalDescription(String internalDescription)
    {
      this.internalDescription = internalDescription;
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

      if (this.internalName.isEmpty())
        this.internalName = name + " element schema";

      if (this.internalDescription.isEmpty())
        this.internalDescription = name + " element schema generated by the CEDAR Artifact Library";

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

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact)
    {
      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.elementUiBuilder.withOrder(fieldKey);
      this.elementUiBuilder.withPropertyLabel(fieldKey, fieldSchemaArtifact.name());
      this.elementUiBuilder.withPropertyDescription(fieldKey, fieldSchemaArtifact.description());
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact)
    {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact);
    }

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel)
    {
      if (this.fieldSchemas.containsKey(fieldKey) || this.elementSchemas.containsKey(fieldKey))
        throw new IllegalArgumentException("Element already has a child " + fieldKey);

      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.elementUiBuilder.withOrder(fieldKey);
      this.elementUiBuilder.withPropertyLabel(fieldKey, propertyLabel);
      return this;
    }

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel,
      String propertyDescription)
    {
      if (this.fieldSchemas.containsKey(fieldKey) || this.elementSchemas.containsKey(fieldKey))
        throw new IllegalArgumentException("Element already has a child " + fieldKey);

      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.elementUiBuilder.withOrder(fieldKey);
      this.elementUiBuilder.withPropertyLabel(fieldKey, propertyLabel);
      this.elementUiBuilder.withPropertyDescription(fieldKey, propertyDescription);
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel)
    {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact, propertyLabel);
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel,
      String propertyDescription)
    {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact, propertyLabel, propertyDescription);
    }

    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact)
    {
      if (this.fieldSchemas.containsKey(elementKey) || this.elementSchemas.containsKey(elementKey))
        throw new IllegalArgumentException("Element already has a child " + elementKey);

      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.elementUiBuilder.withOrder(elementKey);
      this.elementUiBuilder.withPropertyLabel(elementKey, elementSchemaArtifact.name());
      this.elementUiBuilder.withPropertyDescription(elementKey, elementSchemaArtifact.description());
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact)
    {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact);
    }

    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact,
      String propertyLabel)
    {
      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.elementUiBuilder.withOrder(elementKey);
      this.elementUiBuilder.withPropertyLabel(elementKey, propertyLabel);
      return this;
    }

    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact,
                                     String propertyLabel, String propertyDescription)
    {
      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.elementUiBuilder.withOrder(elementKey);
      this.elementUiBuilder.withPropertyLabel(elementKey, propertyLabel);
      this.elementUiBuilder.withPropertyDescription(elementKey, propertyDescription);
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact, String propertyLabel)
    {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact, propertyLabel);
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact, String propertyLabel,
      String propertyDescription)
    {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact, propertyLabel, propertyDescription);
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
      return new ElementSchemaArtifactRecord(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId,
        instanceJsonLdType, name, description, identifier, version, status, previousVersion, derivedFrom, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, fieldSchemas, elementSchemas, isMultiple, minItems, maxItems, propertyUri,
        language, elementUiBuilder.build(), annotations);
    }
  }
}

record ElementSchemaArtifactRecord(String internalName, String internalDescription,
                                   LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes,
                                   Optional<URI> jsonLdId, Optional<URI> instanceJsonLdType, String name,
                                   String description, Optional<String> identifier, Optional<Version> version,
                                   Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
                                   Optional<URI> createdBy, Optional<URI> modifiedBy,
                                   Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                   LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
                                   LinkedHashMap<String, ElementSchemaArtifact> elementSchemas, boolean isMultiple,
                                   Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri,
                                   Optional<String> language, ElementUi elementUi, Optional<Annotations> annotations)
  implements ElementSchemaArtifact
{
  public ElementSchemaArtifactRecord
  {
    validateStringFieldNotNull(this, internalName, JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(this, internalDescription, JSON_SCHEMA_DESCRIPTION);
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
    validateOptionalFieldNotNull(this, language, "prefLabel");
    validateOptionalFieldNotNull(this, language, "language");
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
    Set<String> childKeys = Stream.concat(fieldSchemas.keySet().stream(), elementSchemas.keySet().stream())
      .collect(toSet());

    if (!order.containsAll(childKeys)) {
      childKeys.removeAll(order); // Generate the names of children not in the order map
      order.removeAll(childKeys); // Silently remove these extra children from the order
      for (String childToRemove : childKeys) { // And from the
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

