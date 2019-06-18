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
  private final String pavCreatedBy, oslcModifiedBy;
  private final OffsetDateTime pavCreatedOn, pavLastUpdatedOn;
  private final Map<String, String> jsonLDContext;

  public Artifact(String jsonLDID, List<String> jsonLDTypes, String jsonSchemaType, String jsonSchemaTitle,
    String jsonSchemaDescription, String pavCreatedBy, String oslcModifiedBy, OffsetDateTime pavCreatedOn,
    OffsetDateTime pavLastUpdatedOn, Map<String, String> jsonLDContext)
  {
    this.jsonLDID = jsonLDID;
    this.jsonLDTypes = Collections.unmodifiableList(jsonLDTypes);
    this.jsonSchemaType = jsonSchemaType;
    this.jsonSchemaTitle = jsonSchemaTitle;
    this.jsonSchemaDescription = jsonSchemaDescription;
    this.pavCreatedBy = pavCreatedBy;
    this.oslcModifiedBy = oslcModifiedBy;
    this.pavCreatedOn = pavCreatedOn;
    this.pavLastUpdatedOn = pavLastUpdatedOn;
    this.jsonLDContext = Collections.unmodifiableMap(jsonLDContext);
  }

  public Artifact(Artifact artifact)
  {
    this.jsonLDID = artifact.jsonLDID;
    this.jsonLDTypes = Collections.unmodifiableList(artifact.jsonLDTypes);
    this.jsonSchemaType = artifact.jsonSchemaType;
    this.jsonSchemaTitle = artifact.jsonSchemaTitle;
    this.jsonSchemaDescription = artifact.jsonSchemaDescription;
    this.pavCreatedBy = artifact.pavCreatedBy;
    this.oslcModifiedBy = artifact.oslcModifiedBy;
    this.pavCreatedOn = artifact.pavCreatedOn;
    this.pavLastUpdatedOn = artifact.pavLastUpdatedOn;
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

  public String getPavCreatedBy()
  {
    return pavCreatedBy;
  }

  public String getOslcModifiedBy()
  {
    return oslcModifiedBy;
  }

  public OffsetDateTime getPavCreatedOn()
  {
    return pavCreatedOn;
  }

  public OffsetDateTime getPavLastUpdatedOn()
  {
    return pavLastUpdatedOn;
  }

  public Map<String, String> getJsonLDContext()
  {
    return jsonLDContext;
  }
}
