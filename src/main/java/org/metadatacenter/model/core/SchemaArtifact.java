package org.metadatacenter.model.core;

public class SchemaArtifact extends Artifact
{
  private final String jsonSchemaSchema;
  private final String modelVersion;
  private final String version, previousVersion, status;

  public SchemaArtifact(Artifact artifact, String jsonSchemaSchema, String modelVersion, String version,
    String previousVersion, String status)
  {
    super(artifact);
    this.jsonSchemaSchema = jsonSchemaSchema;
    this.modelVersion = modelVersion;
    this.version = version;
    this.previousVersion = previousVersion;
    this.status = status;
  }

  public SchemaArtifact(SchemaArtifact schemaArtifact)
  {
    super(schemaArtifact);
    this.jsonSchemaSchema = schemaArtifact.jsonSchemaSchema;
    this.modelVersion = schemaArtifact.modelVersion;
    this.version = schemaArtifact.version;
    this.previousVersion = schemaArtifact.previousVersion;
    this.status = schemaArtifact.status;
  }

  public String getJsonSchemaSchema()
  {
    return jsonSchemaSchema;
  }

  public String getModelVersion()
  {
    return modelVersion;
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
