package org.metadatacenter.model.core;

public class SchemaArtifact extends Artifact
{
  private final String jsonSchemaSchema;
  private final String schemaOrgSchemaVersion;
  private final String pavVersion, pavPreviousVersion, biboStatus;

  public SchemaArtifact(Artifact artifact, String jsonSchemaSchema, String schemaOrgSchemaVersion, String pavVersion,
    String pavPreviousVersion, String biboStatus)
  {
    super(artifact);
    this.jsonSchemaSchema = jsonSchemaSchema;
    this.schemaOrgSchemaVersion = schemaOrgSchemaVersion;
    this.pavVersion = pavVersion;
    this.pavPreviousVersion = pavPreviousVersion;
    this.biboStatus = biboStatus;
  }

  public SchemaArtifact(SchemaArtifact schemaArtifact)
  {
    super(schemaArtifact);
    this.jsonSchemaSchema = schemaArtifact.jsonSchemaSchema;
    this.schemaOrgSchemaVersion = schemaArtifact.schemaOrgSchemaVersion;
    this.pavVersion = schemaArtifact.pavVersion;
    this.pavPreviousVersion = schemaArtifact.pavPreviousVersion;
    this.biboStatus = schemaArtifact.biboStatus;
  }

  public String getJsonSchemaSchema()
  {
    return jsonSchemaSchema;
  }

  public String getSchemaOrgSchemaVersion()
  {
    return schemaOrgSchemaVersion;
  }

  public String getPavVersion()
  {
    return pavVersion;
  }

  public String getPavPreviousVersion()
  {
    return pavPreviousVersion;
  }

  public String getBiboStatus()
  {
    return biboStatus;
  }
}
