package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public sealed interface ChildSchemaArtifact extends SchemaArtifact, ChildArtifact permits ElementSchemaArtifact,
  FieldSchemaArtifact
{
  /**
   * The lower bound a repeated child takes when it states none of its own: zero, which is what an
   * absent {@code minItems} means in JSON Schema.
   * <p>
   * A rule of the model rather than of a serialization, so the JSON and YAML forms mean the same
   * thing by an absent bound. Both writers state the bound of every repeated child, so only a
   * document written by hand relies on it. An authoring tool that wants a repeated child to start
   * with an occurrence states that bound itself.
   */
  int DEFAULT_MIN_ITEMS = 0;

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
   * The lower bound implied for a child that states none: {@link #DEFAULT_MIN_ITEMS}, whether its
   * author marked it multiple or its type makes it so.
   */
  default int defaultOccurrences()
  {
    return DEFAULT_MIN_ITEMS;
  }

  /**
   * How many occurrences this child starts with when nothing is chosen for it.
   *
   * A stated bound decides; absent one, {@link #defaultOccurrences()} does. The same number is the
   * lower bound a rendered JSON Schema states, so a template written from the model demands exactly
   * what filling an instance from it produces.
   */
  default int startingOccurrences()
  {
    return minItems().orElse(defaultOccurrences());
  }

  Optional<Integer> maxItems();

  Optional<URI> propertyUri();

  void accept(SchemaArtifactVisitor visitor, String path);
}
