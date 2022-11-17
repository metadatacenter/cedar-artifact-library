package org.metadatacenter.model.core;

import java.net.URI;
import java.util.Optional;

public class SchemaArtifact extends Artifact
{
  private final URI jsonSchemaSchemaURI;
  private final Version modelVersion;
  private final String name;
  private final String description;
  private final Version version;
  private final Status status;
  private final Optional<Version> previousVersion;

  public SchemaArtifact(Artifact artifact, URI jsonSchemaSchemaURI, Version modelVersion, String name,
    String description, Version version, Status status, Optional<Version> previousVersion)
  {
    super(artifact);
    this.jsonSchemaSchemaURI = jsonSchemaSchemaURI;
    this.name = name;
    this.description = description;
    this.modelVersion = modelVersion;
    this.version = version;
    this.previousVersion = previousVersion;
    this.status = status;
  }

  public SchemaArtifact(SchemaArtifact schemaArtifact)
  {
    super(schemaArtifact);
    this.jsonSchemaSchemaURI = schemaArtifact.jsonSchemaSchemaURI;
    this.name = schemaArtifact.name;
    this.description = schemaArtifact.description;
    this.modelVersion = schemaArtifact.modelVersion;
    this.version = schemaArtifact.version;
    this.status = schemaArtifact.status;
    this.previousVersion = schemaArtifact.previousVersion;
  }

  public URI getJsonSchemaSchemaURI()
  {
    return jsonSchemaSchemaURI;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public Version getModelVersion()
  {
    return modelVersion;
  }

  public Version getVersion()
  {
    return version;
  }

  public Optional<Version> getPreviousVersion()
  {
    return previousVersion;
  }

  public Status getStatus()
  {
    return status;
  }

  @Override public String toString()
  {
    return super.toString() + "\n SchemaArtifact{" + "jsonSchemaSchema='" + jsonSchemaSchemaURI + '\'' + ", modelVersion='" + modelVersion + '\''
      + ", version='" + version + '\'' + ", previousVersion='" + previousVersion + '\'' + ", status='" + status + '\''
      + '}';
  }
}
