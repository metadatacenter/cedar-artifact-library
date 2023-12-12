package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public sealed interface ChildSchemaArtifact extends SchemaArtifact, ChildArtifact permits ElementSchemaArtifact, FieldSchemaArtifact
{
  String name();

  boolean isMultiple();

  Optional<Integer> minItems();

  Optional<Integer> maxItems();

  Optional<URI> propertyUri();

  void accept(SchemaArtifactVisitor visitor);
}
