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
  private final Map<String, URI> jsonLdContext;
  private final Optional<URI> createdBy, modifiedBy;
  private final Optional<OffsetDateTime> createdOn, lastUpdatedOn;

  public Artifact(Optional<URI> jsonLdId, Map<String, URI> jsonLdContext,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn)
  {
    this.jsonLdId = jsonLdId;
    this.jsonLdContext = Collections.unmodifiableMap(jsonLdContext);
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLdId = artifact.jsonLdId;
    this.jsonLdContext = artifact.jsonLdContext;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;
  }

  public Optional<URI> getJsonLdId() { return jsonLdId; }

  public Map<String, URI> getJsonLdContext()
  {
    return jsonLdContext;
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

  @Override public String toString()
  {
    return "Artifact{" + "jsonLdId=" + jsonLdId + ", jsonLdContext=" + jsonLdContext + ", createdBy=" + createdBy
      + ", modifiedBy=" + modifiedBy + ", createdOn=" + createdOn + ", lastUpdatedOn=" + lastUpdatedOn + '}';
  }
}
