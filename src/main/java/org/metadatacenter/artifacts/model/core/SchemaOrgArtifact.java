package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

public interface SchemaOrgArtifact
{
  String name();

  String description();

  Optional<String> identifier();
}
