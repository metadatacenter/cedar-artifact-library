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
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListFieldContains;
import static org.metadatacenter.model.ModelNodeNames.BIBO_STATUS;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.PAV_DERIVED_FROM;
import static org.metadatacenter.model.ModelNodeNames.PAV_PREVIOUS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;

public non-sealed interface TemplateSchemaArtifact extends SchemaArtifact, ParentSchemaArtifact
{
  static TemplateSchemaArtifact create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes,
    Optional<URI> jsonLdId, Optional<URI> instanceJsonLdType, String name, String description,
    Optional<String> identifier, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion,
    Optional<URI> derivedFrom, Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn,
    Optional<OffsetDateTime> lastUpdatedOn, LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas, Optional<String> language, TemplateUi templateUi,
    Optional<Annotations> annotations, String internalName, String internalDescription)
  {
    return new TemplateSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, instanceJsonLdType, name, description,
      identifier, version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas, language, templateUi, annotations, internalName, internalDescription);
  }

  TemplateUi templateUi();

  default ParentArtifactUi getUi() {return templateUi();}

  default void accept(SchemaArtifactVisitor visitor)
  {
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

  static Builder builder()
  {
    return new Builder();
  }

  static Builder builder(TemplateSchemaArtifact templateSchemaArtifact)
  {
    return new Builder(templateSchemaArtifact);
  }

  class Builder
  {
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
    private Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO
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

    private Builder()
    {
    }

    private Builder(TemplateSchemaArtifact templateSchemaArtifact)
    {
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

    public Builder withIdentifier(String schemaOrgIdentifier)
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

      if (this.internalName.isEmpty())
        this.internalName = name + " template schema";

      if (this.internalDescription.isEmpty())
        this.internalDescription = name + " template schema generated by the CEDAR Artifact Library";

      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;

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

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact)
    {
      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.templateUiBuilder.withOrder(fieldKey);
      this.templateUiBuilder.withPropertyLabel(fieldKey, fieldSchemaArtifact.name());
      this.templateUiBuilder.withPropertyDescription(fieldKey, fieldSchemaArtifact.description());
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact)
    {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact);
    }

    public Builder withFieldSchema(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel,
      String propertyDescription)
    {
      if (this.fieldSchemas.containsKey(fieldKey) || this.elementSchemas.containsKey(fieldKey))
        throw new IllegalArgumentException("Template already has a child " + fieldKey);

      this.fieldSchemas.put(fieldKey, fieldSchemaArtifact);
      this.templateUiBuilder.withOrder(fieldKey);
      this.templateUiBuilder.withPropertyLabel(fieldKey, propertyLabel);
      this.templateUiBuilder.withPropertyDescription(fieldKey, propertyDescription);
      return this;
    }

    public Builder withFieldSchema(FieldSchemaArtifact fieldSchemaArtifact, String propertyLabel,
      String propertyDescription)
    {
      return withFieldSchema(fieldSchemaArtifact.name(), fieldSchemaArtifact, propertyLabel, propertyDescription);
    }

    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact)
    {
      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.templateUiBuilder.withOrder(elementKey);
      this.templateUiBuilder.withPropertyLabel(elementKey, elementSchemaArtifact.name());
      this.templateUiBuilder.withPropertyDescription(elementKey, elementSchemaArtifact.description());
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact)
    {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact);
    }

    public Builder withElementSchema(String elementKey, ElementSchemaArtifact elementSchemaArtifact, String propertyLabel,
      String propertyDescription)
    {
      if (this.fieldSchemas.containsKey(elementKey) || this.elementSchemas.containsKey(elementKey))
        throw new IllegalArgumentException("Element already has a child " + elementKey);

      this.elementSchemas.put(elementKey, elementSchemaArtifact);
      this.templateUiBuilder.withOrder(elementKey);
      this.templateUiBuilder.withPropertyLabel(elementKey, propertyLabel);
      this.templateUiBuilder.withPropertyDescription(elementKey, propertyDescription);
      return this;
    }

    public Builder withElementSchema(ElementSchemaArtifact elementSchemaArtifact, String propertyLabel,
      String propertyDescription)
    {
      return withElementSchema(elementSchemaArtifact.name(), elementSchemaArtifact, propertyLabel, propertyDescription);
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
                                    Optional<String> language, TemplateUi templateUi, Optional<Annotations> annotations, String internalName, String internalDescription)
  implements TemplateSchemaArtifact
{
  public TemplateSchemaArtifactRecord
  {
    validateStringFieldNotNull(this, internalName, JSON_SCHEMA_TITLE);
    validateStringFieldNotNull(this, internalDescription, JSON_SCHEMA_DESCRIPTION);
    validateStringFieldNotEmpty(this, name, SCHEMA_ORG_NAME);
    validateStringFieldNotNull(this, description, SCHEMA_ORG_DESCRIPTION);
    validateOptionalFieldNotNull(this, version, PAV_VERSION);
    validateOptionalFieldNotNull(this, status, BIBO_STATUS);
    validateOptionalFieldNotNull(this, previousVersion, PAV_PREVIOUS_VERSION);
    validateOptionalFieldNotNull(this, derivedFrom, PAV_DERIVED_FROM);
    validateMapFieldContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    validateUriListFieldContains(this, jsonLdTypes, JSON_LD_TYPE, URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, instanceJsonLdType, "instanceJsonLdType");
    validateMapFieldNotNull(this, fieldSchemas, "fieldSchemas");
    validateMapFieldNotNull(this, elementSchemas, "elementSchemas");
    validateOptionalFieldNotNull(this, language, "language");
    validateUiFieldNotNull(this, templateUi, UI);
    validateOptionalFieldNotNull(this, annotations, "annotations");

    Set<String> order = new HashSet<>(templateUi.order());
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
    jsonLdTypes = List.copyOf(jsonLdTypes);
    fieldSchemas = new LinkedHashMap<>(fieldSchemas);
    elementSchemas = new LinkedHashMap<>(elementSchemas);
  }
}
