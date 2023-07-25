package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

public interface SchemaOrgArtifact
{
  String getName();

  String getDescription();

  Optional<String> getIdentifier();
}
