package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;

public class Artifact
{
  private final List<URI> jsonLdTypes;
  private final Optional<URI> jsonLdId;
  private final Map<String, URI> jsonLdContext;
  private final Optional<URI> createdBy, modifiedBy;
  private final Optional<OffsetDateTime> createdOn, lastUpdatedOn;

  public Artifact(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Map<String, URI> jsonLdContext, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn)
  {
    this.jsonLdTypes = Collections.unmodifiableList(jsonLdTypes);
    this.jsonLdId = jsonLdId;
    this.jsonLdContext = Collections.unmodifiableMap(jsonLdContext);
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;

    validate();
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLdTypes = artifact.getJsonLdTypes();
    this.jsonLdId = artifact.jsonLdId;
    this.jsonLdContext = artifact.jsonLdContext;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;

    validate();
  }

  public List<URI> getJsonLdTypes() {return jsonLdTypes;}

  public Optional<URI> getJsonLdId() {return jsonLdId;}

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
    return "Artifact{" + "jsonLdTypes=" + jsonLdTypes + ", jsonLdId=" + jsonLdId + ", jsonLdContext=" + jsonLdContext
      + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + ", createdOn=" + createdOn + ", lastUpdatedOn="
      + lastUpdatedOn + '}';
  }

  private void validate()
  {
    validateListFieldNotNull(this, jsonLdTypes, ModelNodeNames.JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdId, ModelNodeNames.JSON_LD_ID);
    validateMapFieldNotNull(this, jsonLdContext, ModelNodeNames.JSON_LD_CONTEXT);
    validateOptionalFieldNotNull(this, createdBy, ModelNodeNames.PAV_CREATED_BY);
    validateOptionalFieldNotNull(this, modifiedBy, ModelNodeNames.OSLC_MODIFIED_BY);
    validateOptionalFieldNotNull(this, createdOn, ModelNodeNames.PAV_CREATED_ON);
    validateOptionalFieldNotNull(this, lastUpdatedOn, ModelNodeNames.PAV_LAST_UPDATED_ON);
  }
}
