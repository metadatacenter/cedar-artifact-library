package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
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
    String name, String description,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI isBasedOn,
    Map<String, List<FieldInstanceArtifact>> fieldInstances,
    Map<String, List<ElementInstanceArtifact>> elementInstances)
  {
    return new TemplateInstanceArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, isBasedOn, fieldInstances, elementInstances);
  }

  URI isBasedOn();

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
    private String name;
    private String description = "";
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
      this.name = name;
      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
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

    public Builder withChildElementInstances(String childElementName, List<ElementInstanceArtifact> childElementInstances)
    {
      this.elementInstances.put(childElementName, List.copyOf(childElementInstances));
      return this;
    }

    public Builder withChildFieldInstances(String childFieldName, List<FieldInstanceArtifact> childFieldInstances)
    {
      this.fieldInstances.put(childFieldName, List.copyOf(childFieldInstances));
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
                                      String name, String description,
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
    validateStringFieldNotNull(this, name, SCHEMA_ORG_NAME);
    validateStringFieldNotNull(this, description, SCHEMA_ORG_DESCRIPTION);
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
