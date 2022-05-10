package org.metadatacenter.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Artifact
{
  private final URI jsonLDID;
  private final List<URI> jsonLDTypes;
  private final String jsonSchemaType;
  private final String jsonSchemaTitle;
  private final String jsonSchemaDescription;
  private final URI createdBy, modifiedBy;
  private final OffsetDateTime createdOn, lastUpdatedOn;
  private final Map<String, URI> jsonLDContext;

  public Artifact(URI jsonLDID, List<URI> jsonLDTypes, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, URI createdBy, URI modifiedBy, OffsetDateTime createdOn,
    OffsetDateTime lastUpdatedOn, Map<String, URI> jsonLDContext)
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

  public URI getJsonLDID()
  {
    return jsonLDID;
  }

  public List<URI> getJsonLDTypes()
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

  public URI getCreatedBy()
  {
    return createdBy;
  }

  public URI getModifiedBy()
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

  public Map<String, URI> getJsonLDContext()
  {
    return jsonLDContext;
  }

  @Override public String toString()
  {
    return "Artifact{" + "jsonLDID='" + jsonLDID + '\'' + ", jsonLDTypes=" + jsonLDTypes + ", jsonSchemaType='"
      + jsonSchemaType + '\'' + ", jsonSchemaTitle='" + jsonSchemaTitle + '\'' + ", jsonSchemaDescription='"
      + jsonSchemaDescription + '\'' + ", createdBy='" + createdBy + '\'' + ", modifiedBy='" + modifiedBy + '\''
      + ", createdOn=" + createdOn + ", lastUpdatedOn=" + lastUpdatedOn + ", jsonLDContext=" + jsonLDContext + '}';
  }
}
