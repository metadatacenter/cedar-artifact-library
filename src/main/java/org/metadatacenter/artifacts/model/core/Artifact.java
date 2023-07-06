package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

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

    validate();
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLdId = artifact.jsonLdId;
    this.jsonLdContext = artifact.jsonLdContext;
    this.createdBy = artifact.createdBy;
    this.modifiedBy = artifact.modifiedBy;
    this.createdOn = artifact.createdOn;
    this.lastUpdatedOn = artifact.lastUpdatedOn;

    validate();
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

  private void validate()
  {
    validateOptionalFieldNotNull(jsonLdId, ModelNodeNames.JSON_LD_ID);
    validateMapFieldNotNull(jsonLdContext, ModelNodeNames.JSON_LD_CONTEXT);
    validateOptionalFieldNotNull(createdBy, ModelNodeNames.PAV_CREATED_BY);
    validateOptionalFieldNotNull(modifiedBy, ModelNodeNames.OSLC_MODIFIED_BY);
    validateOptionalFieldNotNull(createdOn, ModelNodeNames.PAV_CREATED_ON);
    validateOptionalFieldNotNull(lastUpdatedOn, ModelNodeNames.PAV_LAST_UPDATED_ON);
  }

  protected void validateStringFieldNotNull(String field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("field " + fieldName + " is null in artifact " + this);
  }

  protected void validateStringFieldNotEmpty(String field, String fieldName)
  {
    validateStringFieldNotNull(field, fieldName);

    if (field.equals(""))
      throw new IllegalStateException("field " + fieldName + " is empty in artifact " + this);
  }

  protected void validateStringFieldEquals(String field, String fieldName, String fieldValue)
  {
    validateStringFieldNotNull(field, fieldName);

    if (!field.equals(fieldValue))
      throw new IllegalStateException("field " + fieldName + " must equal " + fieldValue + " in artifact " + this);
  }

  protected void validateURIFieldNotNull(URI field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("URI field " + fieldName + " is null in artifact " + this);
  }

  protected void validateURIFieldEquals(URI field, String fieldName, String fieldValue)
  {
    validateURIFieldNotNull(field, fieldName);

    if (!field.toString().equals(fieldValue))
      throw new IllegalStateException("URI field " + fieldName + " must equal " + fieldValue + " in artifact " + this);
  }

  protected void validateVersionFieldNotNull(Version field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Version field " + fieldName + " is null in artifact " + this);
  }

  protected <T> void validateOptionalFieldNotNull(Optional<T> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Optional field " + fieldName + " is null in artifact " + this);
  }

  protected <T> void validateOptionalFieldNotEmpty(Optional<T> field, String fieldName)
  {
    validateOptionalFieldNotNull(field, fieldName);

    if (field.isEmpty())
      throw new IllegalStateException("Required Optional field " + fieldName + " is empty in artifact " + this);
  }

  protected void validateUIFieldNotNull(UI field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("UI field " + fieldName + " is null in artifact " + this);
  }

  protected <K, V> void validateMapFieldNotNull(Map<K, V> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("Map field " + fieldName + " is null in artifact " + this);
  }

  protected <T> void validateListFieldNotNull(List<T> field, String fieldName)
  {
    if (field == null)
      throw new IllegalStateException("List field " + fieldName + " is null in artifact " + this);
  }

}
