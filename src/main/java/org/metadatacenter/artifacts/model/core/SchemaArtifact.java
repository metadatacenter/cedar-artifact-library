package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public class SchemaArtifact extends Artifact
{
  private final URI jsonSchemaSchemaUri;
  private final Version modelVersion;
  private final String name;
  private final String description;
  private final Optional<Version> version;
  private final Optional<Status> status;
  private final Optional<Version> previousVersion;
  private final Optional<URI> derivedFrom;

  public SchemaArtifact(Artifact artifact, URI jsonSchemaSchemaUri, Version modelVersion, String name,
    String description, Optional<Version> version, Optional<Status> status, Optional<Version> previousVersion,
    Optional<URI> derivedFrom)
  {
    super(artifact);
    this.jsonSchemaSchemaUri = jsonSchemaSchemaUri;
    this.name = name;
    this.description = description;
    this.modelVersion = modelVersion;
    this.version = version;
    this.status = status;
    this.previousVersion = previousVersion;
    this.derivedFrom = derivedFrom;
  }

  public SchemaArtifact(SchemaArtifact schemaArtifact)
  {
    super(schemaArtifact);
    this.jsonSchemaSchemaUri = schemaArtifact.jsonSchemaSchemaUri;
    this.name = schemaArtifact.name;
    this.description = schemaArtifact.description;
    this.modelVersion = schemaArtifact.modelVersion;
    this.version = schemaArtifact.version;
    this.status = schemaArtifact.status;
    this.previousVersion = schemaArtifact.previousVersion;
    this.derivedFrom = schemaArtifact.derivedFrom;
  }

  public URI getJsonSchemaSchemaUri()
  {
    return jsonSchemaSchemaUri;
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

  public Optional<Version> getVersion()
  {
    return version;
  }

  public Optional<Status> getStatus()
  {
    return status;
  }

  public Optional<Version> getPreviousVersion()
  {
    return previousVersion;
  }

  public Optional<URI> getDerivedFrom()
  {
    return derivedFrom;
  }

  @Override public String toString()
  {
    return "SchemaArtifact{" + "jsonSchemaSchemaURI=" + jsonSchemaSchemaUri + ", modelVersion=" + modelVersion
      + ", name='" + name + '\'' + ", description='" + description + '\'' + ", version=" + version + ", status="
      + status + ", previousVersion=" + previousVersion + ", derivedFrom=" + derivedFrom + '}';
  }
}
