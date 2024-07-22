package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public interface SchemaArtifact extends Artifact, JsonLdArtifact, JsonSchemaArtifact, SchemaOrgArtifact,
  VersionedArtifact, MonitoredArtifact
{
  String name();

  String description();

  Optional<String> identifier();

  Optional<Version> version();

  Optional<Status> status();

  Optional<URI> previousVersion();

  Optional<URI> derivedFrom();

  Optional<String> language();

  Optional<Annotations> annotations();

  String internalName();

  String internalDescription();
}
