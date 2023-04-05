package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Artifact
{
  private final Optional<URI> jsonLDID;
  private final List<URI> jsonLDTypes;
  private final String jsonSchemaType;
  private final String jsonSchemaTitle;
  private final String jsonSchemaDescription;
  private final Optional<URI> createdBy, modifiedBy;
  private final Optional<OffsetDateTime> createdOn, lastUpdatedOn;
  private final Map<String, URI> jsonLDContext;

  public Artifact(Optional<URI> jsonLDID, List<URI> jsonLDTypes, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn,
    Optional<OffsetDateTime> lastUpdatedOn, Map<String, URI> jsonLDContext)
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
    this.jsonLDTypes = artifact.jsonLDTypes;
    this.jsonSchemaType = artifact.jsonSchemaType;
    this.jsonSchemaTitle = artifact.jsonSchemaTitle;
    this.jsonSchemaDescription = artifact.jsonSchemaDescription;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;
    this.jsonLDContext = artifact.jsonLDContext;
  }

  public Optional<URI> getJsonLDID()
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

  public Optional<URI> getCreatedBy()
  {
    return createdBy;
  }

  public Optional<URI> getModifiedBy()
  {
    return modifiedBy;
  }

  public Optional<OffsetDateTime> getCreatedOn()
  {
    return createdOn;
  }

  public Optional<OffsetDateTime> getLastUpdatedOn()
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
