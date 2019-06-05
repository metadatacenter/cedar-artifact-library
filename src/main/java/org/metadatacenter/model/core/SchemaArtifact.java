package org.metadatacenter.model.core;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

public abstract class SchemaArtifact extends Artifact
{
  private final String schema;
  private final String schemaVersion;
  private final String version, previousVersion, status;

  public SchemaArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description, String createdBy,
    String modifiedBy, OffsetDateTime createdOn, OffsetDateTime lastUpdatedOn, String schema, String schemaVersion,
    String version, String previousVersion, String status, Map<String, String> context)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, context);
    this.schema = schema;
    this.schemaVersion = schemaVersion;
    this.version = version;
    this.previousVersion = previousVersion;
    this.status = status;
  }

  public String getSchema()
  {
    return schema;
  }

  public String getSchemaVersion()
  {
    return schemaVersion;
  }

  public String getVersion()
  {
    return version;
  }

  public String getPreviousVersion()
  {
    return previousVersion;
  }

  public String getStatus()
  {
    return status;
  }
}
