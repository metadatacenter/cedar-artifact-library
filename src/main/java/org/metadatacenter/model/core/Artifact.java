package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Artifact
{
  private final String jsonLDID;
  private final List<String> jsonLDTypes;
  private final String name;
  private final String description;
  private final String createdBy, modifiedBy;
  private final OffsetDateTime createdOn, lastUpdatedOn;
  private final Map<String, String> context;

  public Artifact(String jsonLDID, List<String> jsonLDTypes, String name, String description, String createdBy,
    String modifiedBy, OffsetDateTime createdOn, OffsetDateTime lastUpdatedOn, Map<String, String> context)
  {
    this.jsonLDID = jsonLDID;
    this.jsonLDTypes = Collections.unmodifiableList(jsonLDTypes);
    this.name = name;
    this.description = description;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;
    this.context = Collections.unmodifiableMap(context);
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLDID = artifact.jsonLDID;
    this.jsonLDTypes = Collections.unmodifiableList(artifact.jsonLDTypes);
    this.name = artifact.name;
    this.description = artifact.description;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;
    this.context = Collections.unmodifiableMap(artifact.context);
  }

  public String getJsonLDID()
  {
    return jsonLDID;
  }

  public List<String> getJsonLDTypes()
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

  public OffsetDateTime getCreatedOn()
  {
    return createdOn;
  }

  public OffsetDateTime getLastUpdatedOn()
  {
    return lastUpdatedOn;
  }

  public Map<String, String> getContext()
  {
    return context;
  }
}
