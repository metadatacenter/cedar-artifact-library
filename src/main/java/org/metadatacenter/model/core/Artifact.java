package org.metadatacenter.model.core;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class Artifact
{
  private final String jsonLDID;
  private final Set<String> jsonLDTypes;
  private final String name;
  private final String description;
  private final String createdBy, modifiedBy;
  private final LocalDateTime createdOn, lastUpdatedOn;
  private final Map<String, String> context;

  public Artifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description, String createdBy,
    String modifiedBy, LocalDateTime createdOn, LocalDateTime lastUpdatedOn, Map<String, String> context)
  {
    this.jsonLDID = jsonLDID;
    this.jsonLDTypes = Collections.unmodifiableSet(jsonLDTypes);
    this.name = name;
    this.description = description;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;
    this.context = Collections.unmodifiableMap(context);
  }

  public String getJsonLDID()
  {
    return jsonLDID;
  }

  public Set<String> getJsonLDTypes()
  {
    return jsonLDTypes;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public LocalDateTime getCreatedOn()
  {
    return createdOn;
  }

  public LocalDateTime getLastUpdatedOn()
  {
    return lastUpdatedOn;
  }

  public Map<String, String> getContext()
  {
    return context;
  }
}
