package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Artifact
{
  private final String jsonLDID;
  private final List<String> jsonLDTypes;
  private final String jsonSchemaType;
  private final String jsonSchemaTitle;
  private final String jsonSchemaDescription;
  private final String createdBy, modifiedBy;
  private final OffsetDateTime createdOn, lastUpdatedOn;
  private final Map<String, String> jsonLDContext;

  public Artifact(String jsonLDID, List<String> jsonLDTypes, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, String createdBy, String modifiedBy, OffsetDateTime createdOn,
    OffsetDateTime lastUpdatedOn, Map<String, String> jsonLDContext)
  {
    this.jsonLDID = jsonLDID;
    this.jsonLDTypes = Collections.unmodifiableList(jsonLDTypes);
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;
    this.jsonLDContext = Collections.unmodifiableMap(jsonLDContext);
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLDID = artifact.jsonLDID;
    this.jsonLDTypes = Collections.unmodifiableList(artifact.jsonLDTypes);
    this.jsonSchemaType = artifact.jsonSchemaType;
    this.jsonSchemaTitle = artifact.jsonSchemaTitle;
    this.jsonSchemaDescription = artifact.jsonSchemaDescription;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;
    this.jsonLDContext = Collections.unmodifiableMap(artifact.jsonLDContext);
  }

  public String getJsonLDID()
  {
    return jsonLDID;
  }

  public List<String> getJsonLDTypes()
  {
    return jsonLDTypes;
  }

  public String getJsonSchemaType()
  {
    return jsonSchemaType;
  }

  public String getJsonSchemaTitle()
  {
    return jsonSchemaTitle;
  }

  public String getJsonSchemaDescription()
  {
    return jsonSchemaDescription;
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

  public Map<String, String> getJsonLDContext()
  {
    return jsonLDContext;
  }
}
