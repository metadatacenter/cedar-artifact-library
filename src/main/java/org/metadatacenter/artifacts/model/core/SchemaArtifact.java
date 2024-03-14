package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public interface SchemaArtifact extends Artifact, JsonLdArtifact, JsonSchemaArtifact, SchemaOrgArtifact,
  ModelSchemaArtifact, VersionedArtifact, MonitoredArtifact
{
  URI jsonSchemaSchemaUri();

  String jsonSchemaType();

  String jsonSchemaTitle();

  String jsonSchemaDescription();

  String name();

  String description();

  Optional<String> identifier();

  Version modelVersion();

  Optional<Version> version();

  Optional<Status> status();

  Optional<URI> previousVersion();

  Optional<URI> derivedFrom();
}
