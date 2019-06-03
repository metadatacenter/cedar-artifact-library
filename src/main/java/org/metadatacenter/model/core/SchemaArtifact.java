package org.metadatacenter.model.core;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public abstract class SchemaArtifact extends Artifact
{
  private final String schema;
  private final String schemaVersion;
  private final String version, status;

  public SchemaArtifact(String jsonLDID, Set<String> jsonLDTypes, String name, String description, String createdBy,
    String modifiedBy, LocalDateTime createdOn, LocalDateTime lastUpdatedOn, String schema, String schemaVersion,
    String version, String status, Map<String, String> context)
  {
    super(jsonLDID, jsonLDTypes, name, description, createdBy, modifiedBy, createdOn, lastUpdatedOn, context);
    this.schema = schema;
    this.schemaVersion = schemaVersion;
    this.version = version;
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

  public String getStatus()
  {
    return status;
  }
}
