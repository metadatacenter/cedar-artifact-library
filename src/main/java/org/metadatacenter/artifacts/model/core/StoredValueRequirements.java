package org.metadatacenter.artifacts.model.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The keys each child field's stored value schema lists as required, by child key.
 *
 * <p>A field's value schema is the object its template declares for the field's value in an instance
 * (the {@code items} of a repeated field), and its {@code required} list names the keys a value must
 * carry. The model does not keep that list, because the writer derives it from the field's kind. A stored
 * template can still require more, such as an {@code rdfs:label} the instance states as null, and the
 * server judges an instance against the template as stored. {@link
 * org.metadatacenter.artifacts.model.tools.InstanceInflater} reads these lists to keep a stated null the
 * stored template requires.
 *
 * <p>The reader records them; a template built or copied in code carries none. Equality ignores them,
 * so a template read from JSON still equals the same template read from YAML or built, and its
 * serializations are unchanged.
 */
public record StoredValueRequirements(Map<String, List<String>> byChildKey)
{
  public static final StoredValueRequirements NONE = new StoredValueRequirements(Map.of());

  public StoredValueRequirements
  {
    LinkedHashMap<String, List<String>> copy = new LinkedHashMap<>();
    byChildKey.forEach((key, required) -> copy.put(key, List.copyOf(required)));
    byChildKey = java.util.Collections.unmodifiableMap(copy);
  }

  /** The keys the stored value schema of the child field requires, or an empty list where none were recorded. */
  public List<String> of(String childKey)
  {
    return byChildKey.getOrDefault(childKey, List.of());
  }

  @Override public boolean equals(Object other)
  {
    return other instanceof StoredValueRequirements;
  }

  @Override public int hashCode()
  {
    return 0;
  }
}
