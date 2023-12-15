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
    Optional<String> name, Optional<String> description,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI isBasedOn,
    Map<String, List<FieldInstanceArtifact>> fieldInstances,
    Map<String, List<ElementInstanceArtifact>> elementInstances)
  {
    return new TemplateInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, isBasedOn, fieldInstances, elementInstances);
  }

  URI isBasedOn();

  default void accept(InstanceArtifactVisitor visitor) {
    String path = "/";

    visitor.visitTemplateInstanceArtifact(this);

    for (Map.Entry<String, List<FieldInstanceArtifact>> entry : fieldInstances().entrySet()) {
      String fieldName = entry.getKey();
      String childBasePath = path + fieldName;
      List<FieldInstanceArtifact> fieldInstanceArtifacts = entry.getValue();

      if (fieldInstanceArtifacts.size() == 1) {
        FieldInstanceArtifact fieldInstanceArtifact = fieldInstanceArtifacts.get(0);
        fieldInstanceArtifact.accept(visitor, childBasePath);
      } else {
        int childNumber = 0;
        for (FieldInstanceArtifact fieldInstanceArtifact : fieldInstanceArtifacts) {
          fieldInstanceArtifact.accept(visitor, childBasePath + "[" + childNumber + "]");
          childNumber++;
        }
      }
    }

    for (Map.Entry<String, List<ElementInstanceArtifact>> entry : elementInstances().entrySet()) {
      String elementName = entry.getKey();
      String childBasePath = path + elementName;
      List<ElementInstanceArtifact> elementInstanceArtifacts = entry.getValue();

      if (elementInstanceArtifacts.size() == 1) {
        ElementInstanceArtifact elementInstanceArtifact = elementInstanceArtifacts.get(0);
        elementInstanceArtifact.accept(visitor, childBasePath);
      } else {
        int childNumber = 0;
        for (ElementInstanceArtifact elementInstanceArtifact : elementInstanceArtifacts) {
          elementInstanceArtifact.accept(visitor, childBasePath + "[" + childNumber + "]");
          childNumber++;
        }
      }
    }
  }

  static Builder builder()
  {
    return new Builder();
  }

  class Builder
  {
    private List<URI> jsonLdTypes = Collections.emptyList();
    private Optional<URI> jsonLdId = Optional.empty();
    private Map<String, URI> jsonLdContext = new HashMap<>();
    private URI isBasedOn;
    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<URI> createdBy = Optional.empty();
    private Optional<URI> modifiedBy = Optional.empty();
    private Optional<OffsetDateTime> createdOn = Optional.empty();
    private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
    private Map<String, List<FieldInstanceArtifact>> fieldInstances = new HashMap<>();
    private Map<String, List<ElementInstanceArtifact>> elementInstances = new HashMap<>();

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

    public Builder withFieldInstance(String childFieldName, FieldInstanceArtifact fieldInstance)
    {
      if (fieldInstances.containsKey(childFieldName))
        fieldInstances.get(childFieldName).add(fieldInstance);
      else {
        List<FieldInstanceArtifact> childFieldInstances = new ArrayList<>();
        childFieldInstances.add(fieldInstance);
        fieldInstances.put(childFieldName, childFieldInstances);
      }
      return this;
    }

    public Builder withElementInstance(String childElementName, ElementInstanceArtifact elementInstance)
    {
      if (elementInstances.containsKey(childElementName))
        elementInstances.get(childElementName).add(elementInstance);
      else {
        List<ElementInstanceArtifact> childElementInstances = new ArrayList<>();
        childElementInstances.add(elementInstance);
        elementInstances.put(childElementName, childElementInstances);
      }
      return this;
    }

    public Builder withFieldInstances(String childFieldName, List<FieldInstanceArtifact> fieldInstances)
    {
      this.fieldInstances.put(childFieldName, List.copyOf(fieldInstances));
      return this;
    }

    public Builder withElementInstances(String childElementName, List<ElementInstanceArtifact> elementInstances)
    {
      this.elementInstances.put(childElementName, List.copyOf(elementInstances));
      return this;
    }


    public TemplateInstanceArtifact build()
    {
      return new TemplateInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, isBasedOn, fieldInstances, elementInstances);
    }
  }
}

record TemplateInstanceArtifactRecord(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                      Optional<String> name, Optional<String> description,
                                      Optional<URI> createdBy, Optional<URI> modifiedBy,
                                      Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                      URI isBasedOn,
                                      Map<String, List<FieldInstanceArtifact>> fieldInstances,
                                      Map<String, List<ElementInstanceArtifact>> elementInstances) implements TemplateInstanceArtifact
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
    validateMapFieldNotNull(this, fieldInstances, "fieldInstances");
    validateMapFieldNotNull(this, elementInstances, "elementInstances");

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
    fieldInstances = Map.copyOf(fieldInstances);
    elementInstances = Map.copyOf(elementInstances);
  }
}
