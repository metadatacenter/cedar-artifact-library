package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public sealed interface ChildSchemaArtifact permits ElementSchemaArtifact, FieldSchemaArtifact
{
  String getName();

  boolean isMultiple();

  Optional<Integer> getMinItems();

  Optional<Integer> getMaxItems();

  Optional<URI> getPropertyURI();
}
