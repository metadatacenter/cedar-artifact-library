package org.metadatacenter.model.core;

public class SchemaArtifact extends Artifact
{
  private final String schema;
  private final String schemaVersion;
  private final String version, previousVersion, status;

  public SchemaArtifact(Artifact artifact, String schema, String schemaVersion, String version, String previousVersion,
    String status)
  {
    super(artifact);
    this.schema = schema;
    this.schemaVersion = schemaVersion;
    this.version = version;
    this.previousVersion = previousVersion;
    this.status = status;
  }

  public SchemaArtifact(SchemaArtifact schemaArtifact)
  {
    super(schemaArtifact);
    this.schema = schemaArtifact.schema;
    this.schemaVersion = schemaArtifact.schemaVersion;
    this.version = schemaArtifact.version;
    this.previousVersion = schemaArtifact.previousVersion;
    this.status = schemaArtifact.status;
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
