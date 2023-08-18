package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;

public class Artifact implements JsonLdArtifact, MonitoredArtifact
{
  private final Map<String, URI> jsonLdContext;
  private final List<URI> jsonLdTypes;
  private final Optional<URI> jsonLdId;
  private final Optional<URI> createdBy, modifiedBy;
  private final Optional<OffsetDateTime> createdOn, lastUpdatedOn;

  public Artifact(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn)
  {
    this.jsonLdContext = Collections.unmodifiableMap(jsonLdContext);
    this.jsonLdTypes = Collections.unmodifiableList(jsonLdTypes);
    this.jsonLdId = jsonLdId;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;

    validate();
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLdContext = artifact.jsonLdContext;
    this.jsonLdTypes = artifact.getJsonLdTypes();
    this.jsonLdId = artifact.jsonLdId;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;

    validate();
  }

  @Override
  public Map<String, URI> getJsonLdContext()
  {
    return jsonLdContext;
  }

  @Override
  public List<URI> getJsonLdTypes() {return jsonLdTypes;}

  @Override
  public Optional<URI> getJsonLdId() {return jsonLdId;}

  @Override public Optional<URI> getCreatedBy()
  {
    return createdBy;
  }

  @Override public Optional<URI> getModifiedBy() { return modifiedBy; }

  @Override public Optional<OffsetDateTime> getCreatedOn() { return createdOn; }

  public Optional<OffsetDateTime> getLastUpdatedOn()
  {
    return lastUpdatedOn;
  }

  @Override public String toString()
  {
    return "Artifact{" + "jsonLdTypes=" + jsonLdTypes + ", jsonLdId=" + jsonLdId + ", jsonLdContext=" + jsonLdContext
      + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + ", createdOn=" + createdOn + ", lastUpdatedOn="
      + lastUpdatedOn + '}';
  }

  private void validate()
  {
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateMapFieldNotNull(this, jsonLdContext, JSON_LD_CONTEXT);
    validateOptionalFieldNotNull(this, createdBy, PAV_CREATED_BY);
    validateOptionalFieldNotNull(this, modifiedBy, OSLC_MODIFIED_BY);
    validateOptionalFieldNotNull(this, createdOn, PAV_CREATED_ON);
    validateOptionalFieldNotNull(this, lastUpdatedOn, PAV_LAST_UPDATED_ON);
  }
}
