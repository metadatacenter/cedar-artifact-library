package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;

/**
 * While element instances may not necessarily have a JSON-LD identifier or provenance fields (name, description,
 * createdBy, modifiedBy, createdOn, lastUpdatedOn), the model allows them.
 */
public non-sealed interface ElementInstanceArtifact extends InstanceArtifact, ParentInstanceArtifact
{
  static ElementInstanceArtifact create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<String> name, Optional<String> description, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    List<String> childNames,
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
    LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances,
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances)
  {
    return new ElementInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, childNames,
      singleInstanceFieldInstances, multiInstanceFieldInstances,
      singleInstanceElementInstances, multiInstanceElementInstances,
      attributeValueFieldInstances);
  }

  // NOTE: Even though this method looks almost identical to the equivalent method in the TemplateInstanceArtifact
  // its path handling is different.
  default void accept(InstanceArtifactVisitor visitor, String path)
  {
    visitor.visitElementInstanceArtifact(this, path);

    for (Map.Entry<String, FieldInstanceArtifact> entry : singleInstanceFieldInstances().entrySet()) {
      String fieldName = entry.getKey();
      String childBasePath = path + "/" + fieldName;
      FieldInstanceArtifact fieldInstanceArtifact = entry.getValue();

      fieldInstanceArtifact.accept(visitor, childBasePath);
    }

    for (Map.Entry<String, List<FieldInstanceArtifact>> entry : multiInstanceFieldInstances().entrySet()) {
      String fieldName = entry.getKey();
      String childBasePath = path + "/" + fieldName;
      List<FieldInstanceArtifact> fieldInstanceArtifacts = entry.getValue();

      int childNumber = 0;
      for (FieldInstanceArtifact fieldInstanceArtifact : fieldInstanceArtifacts) {
        fieldInstanceArtifact.accept(visitor, childBasePath + "[" + childNumber + "]");
        childNumber++;
      }
    }

    for (Map.Entry<String, ElementInstanceArtifact> entry : singleInstanceElementInstances().entrySet()) {
      String elementName = entry.getKey();
      String childBasePath = path + "/" + elementName;
      ElementInstanceArtifact elementInstanceArtifact = entry.getValue();

      elementInstanceArtifact.accept(visitor, childBasePath);
    }

    for (Map.Entry<String, List<ElementInstanceArtifact>> entry : multiInstanceElementInstances().entrySet()) {
      String elementName = entry.getKey();
      String childBasePath = path + "/" + elementName;
      List<ElementInstanceArtifact> elementInstanceArtifacts = entry.getValue();

      int childNumber = 0;
      for (ElementInstanceArtifact elementInstanceArtifact : elementInstanceArtifacts) {
        elementInstanceArtifact.accept(visitor, childBasePath + "[" + childNumber + "]");
        childNumber++;
      }
    }

    for (Map.Entry<String, Map<String, FieldInstanceArtifact>> entry : attributeValueFieldInstanceGroups().entrySet()) {
      String attributeValueFieldGroupName = entry.getKey();
      Map<String, FieldInstanceArtifact> perAttributeValueFieldInstances = entry.getValue();

      for (Map.Entry<String, FieldInstanceArtifact> perAttributeValueFieldInstanceNameAndInstance : perAttributeValueFieldInstances.entrySet()) {
        String attributeValueFieldInstanceName = perAttributeValueFieldInstanceNameAndInstance.getKey();
        FieldInstanceArtifact fieldInstanceArtifact = perAttributeValueFieldInstanceNameAndInstance.getValue();
        String attributeValueFieldSpecificationPath = path + "/" + attributeValueFieldGroupName;
        String attributeValueFieldInstancePath = path + "/" + attributeValueFieldInstanceName;

        fieldInstanceArtifact.accept(visitor, attributeValueFieldInstancePath, attributeValueFieldSpecificationPath);
      }
    }
  }

  static Builder builder()
  {
    return new Builder();
  }

  static Builder builder(ElementInstanceArtifact elementInstanceArtifact)
  {
    return new Builder(elementInstanceArtifact);
  }

  class Builder
  {
    private LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>();
    private List<URI> jsonLdTypes = Collections.emptyList();
    private Optional<URI> jsonLdId = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private List<String> childNames = new ArrayList<>();
    private LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances = new LinkedHashMap<>();
    private LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances = new LinkedHashMap<>();
    private LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances = new LinkedHashMap<>();
    private LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances = new LinkedHashMap<>();
    private LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups = new LinkedHashMap<>();

    private Builder()
    {
    }

    private Builder(ElementInstanceArtifact elementInstanceArtifact)
    {
      this.jsonLdTypes = new ArrayList<>(elementInstanceArtifact.jsonLdTypes());
      this.jsonLdId = elementInstanceArtifact.jsonLdId();
      this.jsonLdContext = new LinkedHashMap<>(elementInstanceArtifact.jsonLdContext());
      this.name = elementInstanceArtifact.name();
      this.description = elementInstanceArtifact.description();
      this.createdBy = elementInstanceArtifact.createdBy();
      this.modifiedBy = elementInstanceArtifact.modifiedBy();
      this.createdOn = elementInstanceArtifact.createdOn();
      this.lastUpdatedOn = elementInstanceArtifact.lastUpdatedOn();
      this.childNames = new ArrayList<>(elementInstanceArtifact.childNames());
      this.singleInstanceFieldInstances = new LinkedHashMap<>(elementInstanceArtifact.singleInstanceFieldInstances());
      this.multiInstanceFieldInstances = new LinkedHashMap<>(elementInstanceArtifact.multiInstanceFieldInstances());
      this.singleInstanceElementInstances = new LinkedHashMap<>(elementInstanceArtifact.singleInstanceElementInstances());
      this.multiInstanceElementInstances = new LinkedHashMap<>(elementInstanceArtifact.multiInstanceElementInstances());
      this.attributeValueFieldInstanceGroups = new LinkedHashMap<>(elementInstanceArtifact.attributeValueFieldInstanceGroups());
    }

    public Builder withJsonLdContextEntry(String name, URI property)
    {
      this.jsonLdContext.put(name, property);

      return this;
    }

    public Builder withoutJsonLdContextEntry(String name){
      if (!this.jsonLdContext.containsKey(name))
        throw new IllegalArgumentException("Entry " + name + " not present in @context");

      this.jsonLdContext.remove(name);

      return this;
    }

    public Builder withJsonLdType(URI jsonLdType)
    {
      this.jsonLdTypes.add(jsonLdType);
      return this;
    }

    public Builder withJsonLdTypes(List<URI> jsonLdTypes)
    {
      this.jsonLdTypes = List.copyOf(jsonLdTypes);
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

    public Builder withSingleInstanceFieldInstance(String childFieldName, FieldInstanceArtifact fieldInstance)
    {
      if (childNames.contains(childFieldName))
        throw new IllegalArgumentException("child " + childFieldName + " already present in instance");

      childNames.add(childFieldName);

      singleInstanceFieldInstances.put(childFieldName, fieldInstance);

      return this;
    }

    public Builder withoutSingleInstanceFieldInstance(String childFieldName)
    {
      if (!childNames.contains(childFieldName) || !singleInstanceFieldInstances.containsKey(childFieldName))
        throw new IllegalArgumentException("child " + childFieldName + " not present in instance");

      childNames.remove(childFieldName);

      singleInstanceFieldInstances.remove(childFieldName);

      return this;
    }

    public Builder withSingleInstanceElementInstance(String childElementName, ElementInstanceArtifact elementInstance)
    {
      if (childNames.contains(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " already present in instance");

      childNames.add(childElementName);

      singleInstanceElementInstances.put(childElementName, elementInstance);

      return this;
    }

    public Builder withoutSingleInstanceElementInstance(String childElementName)
    {
      if (!this.childNames.contains(childElementName) || !singleInstanceElementInstances.containsKey(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " not present in instance");

      this.childNames.remove(childElementName);

      this.singleInstanceElementInstances.remove(childElementName);

      return this;
    }

    public Builder withMultiInstanceFieldInstances(String childFieldName, List<FieldInstanceArtifact> childFieldInstances)
    {
      if (this.childNames.contains(childFieldName))
        throw new IllegalArgumentException("child " + childFieldName + " already present in instance");

      this.childNames.add(childFieldName);

      this.multiInstanceFieldInstances.put(childFieldName, List.copyOf(childFieldInstances));

      return this;
    }

    public Builder withoutMultiInstanceFieldInstances(String childFieldName)
    {
      if (!childNames.contains(childFieldName) || !multiInstanceFieldInstances.containsKey(childFieldName))
        throw new IllegalArgumentException("child " + childFieldName + " not present in instance");

      this.childNames.remove(childFieldName);

      this.multiInstanceFieldInstances.remove(childFieldName);

      return this;
    }

    public Builder withMultiInstanceElementInstances(String childElementName, List<ElementInstanceArtifact> childElementInstances)
    {
      if (childNames.contains(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " already present in instance");

      childNames.add(childElementName);

      this.multiInstanceElementInstances.put(childElementName, List.copyOf(childElementInstances));

      return this;
    }

    public Builder withoutMultiInstanceElementInstances(String childElementName)
    {
      if (!childNames.contains(childElementName) || !multiInstanceElementInstances.containsKey(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " not present in instance");

      this.childNames.remove(childElementName);

      this.multiInstanceElementInstances.remove(childElementName);

      return this;
    }

    public Builder withAttributeValueFieldGroup(String attributeValueFieldGroupName,
      LinkedHashMap<String, FieldInstanceArtifact> attributeValueFieldInstances)
    {
      Set<String> attributeValueFieldInstanceNames = attributeValueFieldInstances.keySet();

      if (childNames.contains(attributeValueFieldGroupName))
        throw new IllegalArgumentException("child " + attributeValueFieldGroupName + " already present in instance");

      childNames.add(attributeValueFieldGroupName);

      Set<String> overlappingChildNames = attributeValueFieldInstanceNames.stream()
        .filter(childName -> childNames.contains(childName))
        .collect(Collectors.toSet());

      if (!overlappingChildNames.isEmpty())
        throw new IllegalArgumentException("at least one of field instance names " +
          overlappingChildNames + " of attribute-value field " + attributeValueFieldGroupName +
          " already present in parent instance");

      childNames.addAll(attributeValueFieldInstanceNames);

      this.attributeValueFieldInstanceGroups.put(attributeValueFieldGroupName, Map.copyOf(attributeValueFieldInstances));

      return this;
    }

    // TODO Add withoutAttributeValueFieldGroup

    public ElementInstanceArtifact build()
    {
      return new ElementInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, childNames,
        singleInstanceFieldInstances, multiInstanceFieldInstances,
        singleInstanceElementInstances, multiInstanceElementInstances,
        attributeValueFieldInstanceGroups);
    }
  }
}

record ElementInstanceArtifactRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                     Optional<String> name, Optional<String> description,
                                     Optional<URI> createdBy, Optional<URI> modifiedBy,
                                     Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                     List<String> childNames,
                                     LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
                                     LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
                                     LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances,
                                     LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
                                     LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups)
  implements ElementInstanceArtifact
{
  public ElementInstanceArtifactRecord
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
    validateListFieldNotNull(this, childNames, "childNames");
    validateMapFieldNotNull(this, singleInstanceFieldInstances, "singleInstanceFieldInstances");
    validateMapFieldNotNull(this, multiInstanceFieldInstances, "multiInstanceFieldInstances");
    validateMapFieldNotNull(this, singleInstanceElementInstances, "singleInstanceElementInstances");
    validateMapFieldNotNull(this, multiInstanceElementInstances, "multiInstanceElementInstances");
    validateMapFieldNotNull(this, attributeValueFieldInstanceGroups, "attributeValueFieldInstanceGroups");

    // TODO check that all childFieldNames present in child instances maps and that there are no extra fields in maps

    jsonLdContext = new LinkedHashMap<>(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    childNames = List.copyOf(childNames);
    singleInstanceFieldInstances = new LinkedHashMap<>(singleInstanceFieldInstances);
    multiInstanceFieldInstances = new LinkedHashMap<>(multiInstanceFieldInstances);
    singleInstanceElementInstances = new LinkedHashMap<>(singleInstanceElementInstances);
    multiInstanceElementInstances = new LinkedHashMap<>(multiInstanceElementInstances);
    attributeValueFieldInstanceGroups = new LinkedHashMap<>(attributeValueFieldInstanceGroups);
  }
}

