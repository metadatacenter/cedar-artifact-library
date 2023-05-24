package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Artifact
{
  private final Optional<URI> jsonLdId;
  private final List<URI> jsonLdTypes;
  private final String jsonSchemaType;
  private final String jsonSchemaTitle;
  private final String jsonSchemaDescription;
  private final Optional<URI> createdBy, modifiedBy;
  private final Optional<OffsetDateTime> createdOn, lastUpdatedOn;
  private final Map<String, URI> jsonLdContext;

  public Artifact(Optional<URI> jsonLdId, List<URI> jsonLdTypes, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn,
    Optional<OffsetDateTime> lastUpdatedOn, Map<String, URI> jsonLdContext)
  {
    this.jsonLdId = jsonLdId;
    this.jsonLdTypes = Collections.unmodifiableList(jsonLdTypes);
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;
    this.jsonLdContext = Collections.unmodifiableMap(jsonLdContext);
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLdId = artifact.jsonLdId;
    this.jsonLdTypes = artifact.jsonLdTypes;
    this.jsonSchemaType = artifact.jsonSchemaType;
    this.jsonSchemaTitle = artifact.jsonSchemaTitle;
    this.jsonSchemaDescription = artifact.jsonSchemaDescription;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;
    this.jsonLdContext = artifact.jsonLdContext;
  }

  public Optional<URI> getJsonLdId()
  {
    return jsonLdId;
  }

  public List<URI> getJsonLdTypes()
  {
    return jsonLdTypes;
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

  public Map<String, URI> getJsonLdContext()
  {
    return jsonLdContext;
  }

  @Override public String toString()
  {
    return "Artifact{" + "jsonLdId='" + jsonLdId + '\'' + ", jsonLdTypes=" + jsonLdTypes + ", jsonSchemaType='"
      + jsonSchemaType + '\'' + ", jsonSchemaTitle='" + jsonSchemaTitle + '\'' + ", jsonSchemaDescription='"
      + jsonSchemaDescription + '\'' + ", createdBy='" + createdBy + '\'' + ", modifiedBy='" + modifiedBy + '\''
      + ", createdOn=" + createdOn + ", lastUpdatedOn=" + lastUpdatedOn + ", jsonLdContext=" + jsonLdContext + '}';
  }
}
