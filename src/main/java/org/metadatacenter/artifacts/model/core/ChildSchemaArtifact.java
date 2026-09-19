package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public sealed interface ChildSchemaArtifact extends SchemaArtifact, ChildArtifact permits ElementSchemaArtifact,
  FieldSchemaArtifact
{
  /**
   * What a multi-instance child takes when it states no lower bound of its own: one instance.
   * <p>
   * A rule of the model rather than of a serialization, so a reader that fills it in and a writer
   * that leaves it out agree on it. Zero would say the array may be empty, which is a different
   * contract and one the meta-schema takes literally, and it is not what the system stores: every
   * such child in production carries one.
   */
  int DEFAULT_MIN_ITEMS = 1;

  String name();

  boolean isMultiple();

  Optional<Integer> minItems();

  Optional<Integer> maxItems();

  Optional<URI> propertyUri();

  void accept(SchemaArtifactVisitor visitor, String path);
}
