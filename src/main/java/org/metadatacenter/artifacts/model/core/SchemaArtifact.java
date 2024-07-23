package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

public interface SchemaArtifact
  extends Artifact, JsonLdArtifact, SchemaOrgArtifact, VersionedArtifact, MonitoredArtifact
{
  Optional<String> language();

  Optional<Annotations> annotations();

  String internalName();

  String internalDescription();
}
