package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public interface VersionedArtifact
{
  Optional<Version> getVersion();

  Optional<Status> getStatus();

  Optional<URI> getPreviousVersion();

  Optional<URI> getDerivedFrom();
}
