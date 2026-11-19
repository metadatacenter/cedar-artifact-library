package org.metadatacenter.artifacts.model.core;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The optional value keys a document stated as null, and those of them the writer keeps.
 *
 * <p>A field value can state {@code rdfs:label}, {@code skos:notation}, {@code skos:prefLabel} or
 * {@code @language} as null. The model keeps such a key as no value, so the writer leaves it out. That is
 * right unless the stored template requires the key, when the document the writer produces would fail the
 * template the stored one satisfied. The reader records every stated null; {@link
 * org.metadatacenter.artifacts.model.tools.InstanceInflater} keeps those the stored template requires; the
 * JSON writer writes the kept ones.
 *
 * <p>Equality ignores both sets, as it ignores every other difference between a document and the model it
 * was read into.
 */
public record StatedNulls(Set<String> stated, Set<String> kept)
{
  public static final StatedNulls NONE = new StatedNulls(Set.of(), Set.of());

  public StatedNulls
  {
    stated = java.util.Collections.unmodifiableSet(new LinkedHashSet<>(stated));
    kept = java.util.Collections.unmodifiableSet(new LinkedHashSet<>(kept));
    if (!stated.containsAll(kept))
      throw new IllegalArgumentException("Only a stated null can be kept: " + kept + " of " + stated);
  }

  public static StatedNulls of(Set<String> stated)
  {
    return stated.isEmpty() ? NONE : new StatedNulls(stated, Set.of());
  }

  /** These stated nulls, keeping each one the stored value schema requires. */
  public StatedNulls keeping(Collection<String> required)
  {
    LinkedHashSet<String> keep = new LinkedHashSet<>(stated);
    keep.retainAll(required);
    return keep.equals(kept) ? this : new StatedNulls(stated, keep);
  }

  @Override public boolean equals(Object other)
  {
    return other instanceof StatedNulls;
  }

  @Override public int hashCode()
  {
    return 0;
  }
}
