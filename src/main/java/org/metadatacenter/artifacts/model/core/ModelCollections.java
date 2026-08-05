package org.metadatacenter.artifacts.model.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Collection-copy helpers used at immutable model boundaries. */
final class ModelCollections
{
  private ModelCollections() {}

  static <K, V> LinkedHashMap<K, V> copyMap(Map<? extends K, ? extends V> source)
  {
    return new LinkedHashMap<>(source);
  }

  static <K, V> LinkedHashMap<K, List<V>> copyListValuedMap(
    Map<? extends K, ? extends List<? extends V>> source)
  {
    LinkedHashMap<K, List<V>> copy = new LinkedHashMap<>();
    source.forEach((key, values) -> copy.put(key, List.copyOf(values)));
    return copy;
  }

  static <K1, K2, V> LinkedHashMap<K1, Map<K2, V>> copyMapValuedMap(
    Map<? extends K1, ? extends Map<? extends K2, ? extends V>> source)
  {
    LinkedHashMap<K1, Map<K2, V>> copy = new LinkedHashMap<>();
    source.forEach((key, values) ->
      copy.put(key, Collections.unmodifiableMap(new LinkedHashMap<>(values))));
    return copy;
  }
}
