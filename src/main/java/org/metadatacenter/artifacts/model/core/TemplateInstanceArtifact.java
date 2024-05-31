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
  static TemplateInstanceArtifact create(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<String> name, Optional<String> description, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, URI isBasedOn,
    List<String> childNames,
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
    LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances,
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups,
    Optional<Annotations> annotations)
  {
    return new TemplateInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, isBasedOn,
      childNames, singleInstanceFieldInstances, multiInstanceFieldInstances,
      singleInstanceElementInstances, multiInstanceElementInstances,
      attributeValueFieldInstanceGroups, annotations);
  }

  URI isBasedOn();

  Optional<Annotations> annotations();

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

    for (Map.Entry<String, Map<String, FieldInstanceArtifact>> entry : attributeValueFieldInstanceGroups().entrySet()) {
      String attributeValueFieldGroupName = entry.getKey();
      Map<String, FieldInstanceArtifact> perAttributeValueFieldInstances = entry.getValue();

      for (Map.Entry<String, FieldInstanceArtifact> perAttributeValueFieldInstanceNameAndInstance : perAttributeValueFieldInstances.entrySet()) {
        String attributeValueFieldInstanceName = perAttributeValueFieldInstanceNameAndInstance.getKey();
        FieldInstanceArtifact fieldInstanceArtifact = perAttributeValueFieldInstanceNameAndInstance.getValue();
        String attributeValueFieldSpecificationPath = path + attributeValueFieldGroupName;
        String attributeValueFieldInstancePath = path + attributeValueFieldInstanceName;

        fieldInstanceArtifact.accept(visitor, attributeValueFieldInstancePath, attributeValueFieldSpecificationPath);
      }
    }
  }

  static Builder builder()
  {
    return new Builder();
  }

  static Builder builder(TemplateInstanceArtifact templateInstanceArtifact)
  {
    return new Builder(templateInstanceArtifact);
  }

  class Builder
  {
    private List<URI> jsonLdTypes = Collections.emptyList();
    private Optional<URI> jsonLdId = Optional.empty();
    private LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>();
    private URI isBasedOn;
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
    private Optional<Annotations> annotations = Optional.empty();

    private Builder()
    {
    }

    private Builder(TemplateInstanceArtifact templateInstanceArtifact)
    {
      this.jsonLdTypes = new ArrayList<>(templateInstanceArtifact.jsonLdTypes());
      this.jsonLdId = templateInstanceArtifact.jsonLdId();
      this.jsonLdContext = new LinkedHashMap<>(templateInstanceArtifact.jsonLdContext());
      this.isBasedOn = templateInstanceArtifact.isBasedOn();
      this.name = templateInstanceArtifact.name();
      this.description = templateInstanceArtifact.description();
      this.createdBy = templateInstanceArtifact.createdBy();
      this.modifiedBy = templateInstanceArtifact.modifiedBy();
      this.createdOn = templateInstanceArtifact.createdOn();
      this.lastUpdatedOn = templateInstanceArtifact.lastUpdatedOn();
      this.childNames = new ArrayList<>(templateInstanceArtifact.childNames());
      this.singleInstanceFieldInstances = new LinkedHashMap<>(templateInstanceArtifact.singleInstanceFieldInstances());
      this.multiInstanceFieldInstances = new LinkedHashMap<>(templateInstanceArtifact.multiInstanceFieldInstances());
      this.singleInstanceElementInstances = new LinkedHashMap<>(templateInstanceArtifact.singleInstanceElementInstances());
      this.multiInstanceElementInstances = new LinkedHashMap<>(templateInstanceArtifact.multiInstanceElementInstances());
      this.attributeValueFieldInstanceGroups = new LinkedHashMap<>(templateInstanceArtifact.attributeValueFieldInstanceGroups());
      this.annotations = templateInstanceArtifact.annotations();
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
      if (!childNames.contains(childElementName) || !singleInstanceElementInstances.containsKey(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " not present in instance");

      childNames.remove(childElementName);

      singleInstanceElementInstances.remove(childElementName);

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

    public Builder withoutMultiInstanceFieldInstances(String childFieldName)
    {
      if (!childNames.contains(childFieldName) || !multiInstanceFieldInstances.containsKey(childFieldName))
        throw new IllegalArgumentException("child " + childFieldName + " not present in instance");

      childNames.remove(childFieldName);

      this.multiInstanceFieldInstances.remove(childFieldName);

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

    public Builder withoutMultiInstanceElementInstances(String childElementName)
    {
      if (!childNames.contains(childElementName) || !multiInstanceElementInstances.containsKey(childElementName))
        throw new IllegalArgumentException("child " + childElementName + " not present in instance");

      childNames.remove(childElementName);

      this.multiInstanceElementInstances.remove(childElementName);

      return this;
    }

    public Builder withAttributeValueFieldGroup(String attributeValueFieldGroupName,
      Map<String, FieldInstanceArtifact> attributeValueFieldInstances)
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

    public Builder withAnnotations(Annotations annotations)
    {
      this.annotations = Optional.ofNullable(annotations);
      return this;
    }

    // TODO Add withoutAttributeValueFieldGroup

    public TemplateInstanceArtifact build()
    {
      return new TemplateInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, isBasedOn, childNames,
        singleInstanceFieldInstances, multiInstanceFieldInstances,
        singleInstanceElementInstances, multiInstanceElementInstances, attributeValueFieldInstanceGroups,
        annotations);
    }
  }
}

record TemplateInstanceArtifactRecord(LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                      Optional<String> name, Optional<String> description,
                                      Optional<URI> createdBy, Optional<URI> modifiedBy,
                                      Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                      URI isBasedOn,
                                      List<String> childNames,
                                      LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
                                      LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
                                      LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances,
                                      LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
                                      LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups,
                                      Optional<Annotations> annotations)
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
    validateMapFieldNotNull(this, attributeValueFieldInstanceGroups, "attributeValueFieldInstanceGroups");
    validateOptionalFieldNotNull(this, annotations, "annotations");

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
