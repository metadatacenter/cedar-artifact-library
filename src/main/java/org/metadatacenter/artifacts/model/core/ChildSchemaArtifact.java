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

  /**
   * Whether this child is multiple because of what it is, rather than because its author said so.
   *
   * A checkbox is always multiple, and a list is whenever its constraints say multiple choice. No
   * one declared that, so no one declared how many occurrences it starts with either, and holding
   * nothing is a state such a field can mean: nothing was ticked.
   */
  default boolean isMultipleByNature() {return false;}

  /**
   * How many occurrences this child starts with when nothing is chosen for it.
   *
   * A stated bound decides. Absent one, a child someone marked multiple takes
   * {@link #DEFAULT_MIN_ITEMS}, for the reason above; a child that is multiple by nature takes
   * none, because an occupant there would stand for a selection nobody made.
   */
  default int startingOccurrences()
  {
    return minItems().orElse(isMultipleByNature() ? 0 : DEFAULT_MIN_ITEMS);
  }

  Optional<Integer> maxItems();

  Optional<URI> propertyUri();

  void accept(SchemaArtifactVisitor visitor, String path);
}
