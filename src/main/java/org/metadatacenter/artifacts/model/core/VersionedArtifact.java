package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public interface VersionedArtifact
{
  Optional<Version> version();

  Optional<Status> status();

  Optional<URI> previousVersion();

  Optional<URI> derivedFrom();
}
