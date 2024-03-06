package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_IS_BASED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;

public non-sealed interface TemplateInstanceArtifact extends InstanceArtifact, ParentInstanceArtifact
{
  static TemplateInstanceArtifact create(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<String> name, Optional<String> description, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, URI isBasedOn,
    List<String> childNames,
    Map<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    Map<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
    Map<String, ElementInstanceArtifact> singleInstanceElementInstances,
    Map<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
    Map<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances)
  {
    return new TemplateInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, isBasedOn,
      childNames, singleInstanceFieldInstances, multiInstanceFieldInstances,
      singleInstanceElementInstances, multiInstanceElementInstances,
      attributeValueFieldInstances);
  }

  URI isBasedOn();

  default void accept(InstanceArtifactVisitor visitor)
  {
    String path = "/";

    visitor.visitTemplateInstanceArtifact(this);

    for (Map.Entry<String, ElementInstanceArtifact> entry : singleInstanceElementInstances().entrySet()) {
      String elementName = entry.getKey();
      String childBasePath = path + elementName;
      ElementInstanceArtifact elementInstanceArtifact = entry.getValue();

      elementInstanceArtifact.accept(visitor, childBasePath);
    }

    for (Map.Entry<String, List<ElementInstanceArtifact>> entry : multiInstanceElementInstances().entrySet()) {
      String elementName = entry.getKey();
      String childBasePath = path + elementName;
      List<ElementInstanceArtifact> elementInstanceArtifacts = entry.getValue();

      int childNumber = 0;
      for (ElementInstanceArtifact elementInstanceArtifact : elementInstanceArtifacts) {
        elementInstanceArtifact.accept(visitor, childBasePath + "[" + childNumber + "]");
        childNumber++;
      }
    }

    for (Map.Entry<String, FieldInstanceArtifact> entry : singleInstanceFieldInstances().entrySet()) {
      String fieldName = entry.getKey();
      String childBasePath = path + fieldName;
      FieldInstanceArtifact fieldInstanceArtifact = entry.getValue();

      fieldInstanceArtifact.accept(visitor, childBasePath);
    }

    for (Map.Entry<String, List<FieldInstanceArtifact>> entry : multiInstanceFieldInstances().entrySet()) {
      String fieldName = entry.getKey();
      String childBasePath = path + fieldName;
      List<FieldInstanceArtifact> fieldInstanceArtifacts = entry.getValue();

      int childNumber = 0;
      for (FieldInstanceArtifact fieldInstanceArtifact : fieldInstanceArtifacts) {
        fieldInstanceArtifact.accept(visitor, childBasePath + "[" + childNumber + "]");
        childNumber++;
      }
    }

    for (Map.Entry<String, Map<String, FieldInstanceArtifact>> entry : attributeValueFieldInstances().entrySet()) {
      String attributeValueFieldName = entry.getKey();
      Map<String, FieldInstanceArtifact> perAttributeValueFieldInstances = entry.getValue();

      for (Map.Entry<String, FieldInstanceArtifact> perAttributeValueFieldInstanceNameAndInstance : perAttributeValueFieldInstances.entrySet()) {
        String attributeValueFieldInstanceName = perAttributeValueFieldInstanceNameAndInstance.getKey();
        FieldInstanceArtifact fieldInstanceArtifact = perAttributeValueFieldInstanceNameAndInstance.getValue();
        String attributeValueFieldSpecificationPath = path + attributeValueFieldName;
        String attributeValueFieldInstancePath = path + attributeValueFieldInstanceName;

        fieldInstanceArtifact.accept(visitor, attributeValueFieldInstancePath, attributeValueFieldSpecificationPath);
      }
    }
  }

  static Builder builder()
  {
    return new Builder();
  }

  class Builder
  {
    private final List<URI> jsonLdTypes = Collections.emptyList();
    private Optional<URI> jsonLdId = Optional.empty();
    private Map<String, URI> jsonLdContext = new HashMap<>();
    private URI isBasedOn;
    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private final List<String> childNames = new ArrayList<>();
    private final Map<String, FieldInstanceArtifact> singleInstanceFieldInstances = new HashMap<>();
    private final Map<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances = new HashMap<>();
    private final Map<String, ElementInstanceArtifact> singleInstanceElementInstances = new HashMap<>();
    private final Map<String, List<ElementInstanceArtifact>> multiInstanceElementInstances = new HashMap<>();
    private final Map<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances = new HashMap<>();

    private Builder()
    {
    }

    public Builder withJsonLdContext(Map<String, URI> jsonLdContext)
    {
      this.jsonLdContext = Map.copyOf(jsonLdContext);
      return this;
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

    public Builder withName(String name)
    {
      this.name = Optional.ofNullable(name);
      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = Optional.ofNullable(description);
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

    public Builder withIsBasedOn(URI isBasedOn)
    {
      this.isBasedOn = isBasedOn;
      return this;
    }

    public Builder withSingleInstanceFieldInstance(String childFieldName, FieldInstanceArtifact fieldInstance)
    {
      if (childNames.contains(childFieldName))
        throw new IllegalArgumentException("child " + childFieldName + " already present in instance");

      childNames.add(childFieldName);

      singleInstanceFieldInstances.put(childFieldName, fieldInstance);

      return this;
    }

    public Builder withElementInstance(String childElementName, ElementInstanceArtifact elementInstance)
    {
      if (childNames.contains(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " already present in instance");

      childNames.add(childElementName);

      singleInstanceElementInstances.put(childElementName, elementInstance);

      return this;
    }

    public Builder withMultiInstanceFieldInstances(String childFieldName, List<FieldInstanceArtifact> fieldInstances)
    {
      if (childNames.contains(childFieldName))
        throw new IllegalArgumentException("child " + childFieldName + " already present in instance");

      childNames.add(childFieldName);

      this.multiInstanceFieldInstances.put(childFieldName, List.copyOf(fieldInstances));

      return this;
    }

    public Builder withMultiInstanceElementInstances(String childElementName, List<ElementInstanceArtifact> elementInstances)
    {
      if (childNames.contains(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " already present in instance");

      childNames.add(childElementName);

      this.multiInstanceElementInstances.put(childElementName, List.copyOf(elementInstances));

      return this;
    }

    public Builder withAttributeValueFieldInstances(String attributeValueFieldName,
      Map<String, FieldInstanceArtifact> attributeValueFieldInstances)
    {
      this.attributeValueFieldInstances.put(attributeValueFieldName,
        Map.copyOf(attributeValueFieldInstances));
      return this;
    }

    public TemplateInstanceArtifact build()
    {
      return new TemplateInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, isBasedOn, childNames,
        singleInstanceFieldInstances, multiInstanceFieldInstances,
        singleInstanceElementInstances, multiInstanceElementInstances,
        attributeValueFieldInstances);
    }
  }
}

record TemplateInstanceArtifactRecord(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                      Optional<String> name, Optional<String> description,
                                      Optional<URI> createdBy, Optional<URI> modifiedBy,
                                      Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                      URI isBasedOn,
                                      List<String> childNames,
                                      Map<String, FieldInstanceArtifact> singleInstanceFieldInstances,
                                      Map<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
                                      Map<String, ElementInstanceArtifact> singleInstanceElementInstances,
                                      Map<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
                                      Map<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances)
  implements TemplateInstanceArtifact
{
  public TemplateInstanceArtifactRecord
  {
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, name, SCHEMA_ORG_NAME);
    validateOptionalFieldNotNull(this, description, SCHEMA_ORG_DESCRIPTION);
    validateOptionalFieldNotNull(this, createdBy, PAV_CREATED_BY);
    validateOptionalFieldNotNull(this, modifiedBy, OSLC_MODIFIED_BY);
    validateOptionalFieldNotNull(this, createdOn, PAV_CREATED_ON);
    validateOptionalFieldNotNull(this, lastUpdatedOn, PAV_LAST_UPDATED_ON);
    validateUriFieldNotNull(this, isBasedOn, SCHEMA_IS_BASED_ON);
    validateListFieldNotNull(this, childNames, "childNames");
    validateMapFieldNotNull(this, singleInstanceFieldInstances, "singleInstanceFieldInstances");
    validateMapFieldNotNull(this, multiInstanceFieldInstances, "multiInstanceFieldInstances");
    validateMapFieldNotNull(this, singleInstanceElementInstances, "singleInstanceElementInstances");
    validateMapFieldNotNull(this, multiInstanceElementInstances, "multiInstanceElementInstances");
    validateMapFieldNotNull(this, attributeValueFieldInstances, "attributeValueFieldInstances");

    // TODO check that all childFieldNames present in child instances maps and that there are no extra fields in maps

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    childNames = List.copyOf(childNames);
    singleInstanceFieldInstances = Map.copyOf(singleInstanceFieldInstances);
    multiInstanceFieldInstances = Map.copyOf(multiInstanceFieldInstances);
    singleInstanceElementInstances = Map.copyOf(singleInstanceElementInstances);
    multiInstanceElementInstances = Map.copyOf(multiInstanceElementInstances);
    attributeValueFieldInstances = Map.copyOf(attributeValueFieldInstances);
  }
}
