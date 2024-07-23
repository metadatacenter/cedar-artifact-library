package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

public interface SchemaArtifact extends Artifact, JsonLdArtifact, VersionedArtifact, MonitoredArtifact
{
  String name();

  String description();

  Optional<String> identifier();

  Optional<String> language();

  Optional<Annotations> annotations();

  String internalName();

  String internalDescription();
}
