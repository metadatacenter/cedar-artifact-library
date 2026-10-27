package org.metadatacenter.artifacts.model.reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHILDREN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.KEY;

/**
 * Which keys of a YAML document the reader used, so that a key it never used is refused rather than
 * silently dropped.
 *
 * <p>A YAML author who misspells a key, {@code descripton:} for {@code description:}, would otherwise
 * see the document accepted and the value gone, where the same mistake in JSON is refused with its
 * path. Every mapping of the document is copied into a {@link Tracked} mapping that records the keys
 * the reader asks for. A mapping whose keys the reader iterates, such as {@code children}, annotations
 * or an attribute-value group, has all its keys counted as used, since those keys are names the
 * document chooses. After the read, the first key nothing used is reported with its path, in the
 * notation the reader's other errors use.
 */
final class YamlKeyTracking
{
  private static final ThreadLocal<Integer> PAUSED = ThreadLocal.withInitial(() -> 0);

  private YamlKeyTracking() {}

  /**
   * A deep copy of {@code source} in which every mapping records the keys read from it, or {@code
   * source} itself when it is such a copy already.
   */
  @SuppressWarnings("unchecked")
  static LinkedHashMap<String, Object> track(Map<String, Object> source)
  {
    return source instanceof Tracked tracked ? tracked : (LinkedHashMap<String, Object>) copy(source);
  }

  /**
   * The keys asked for in each mapping of a tracked document, by its JSON Pointer in the document. The
   * parity test compares them with the TypeScript library's.
   */
  static Map<String, Set<String>> askedKeys(Object root)
  {
    Map<String, Set<String>> asked = new java.util.TreeMap<>();
    askedKeys(root, "", asked);
    return asked;
  }

  private static void askedKeys(Object node, String pointer, Map<String, Set<String>> asked)
  {
    if (node instanceof Tracked tracked) {
      asked.put(pointer, new java.util.TreeSet<>(tracked.used));
      untracked(() -> tracked.forEach((key, value) -> askedKeys(value, pointer + "/" + key.replace("~", "~0")
        .replace("/", "~1"), asked)));
    } else if (node instanceof List<?> list) {
      for (int i = 0; i < list.size(); i++)
        askedKeys(list.get(i), pointer + "/" + i, asked);
    }
  }

  private static Object copy(Object node)
  {
    if (node instanceof Map<?, ?> map) {
      Tracked tracked = new Tracked(ArtifactSourceParser.spellings(map));
      map.forEach((key, value) -> tracked.put(String.valueOf(key), copy(value)));
      return tracked;
    }
    if (node instanceof List<?> list) {
      List<Object> copied = new ArrayList<>(list.size());
      for (Object item : list)
        copied.add(copy(item));
      return copied;
    }
    return node;
  }

  /** Run {@code work} without counting the keys it reads, for a walk that checks values rather than reading them. */
  static <T> T untracked(Supplier<T> work)
  {
    PAUSED.set(PAUSED.get() + 1);
    try {
      return work.get();
    } finally {
      PAUSED.set(PAUSED.get() - 1);
    }
  }

  static void untracked(Runnable work)
  {
    untracked(() -> {
      work.run();
      return null;
    });
  }

  /** Count {@code key} of {@code map} as used, for a reader that inspects a mapping without asking for that key. */
  static void use(Map<String, Object> map, String key)
  {
    if (map instanceof Tracked tracked)
      tracked.used.add(key);
  }

  /** Refuse the first key of {@code root} that the reader did not use. */
  static void requireUsed(Object root)
  {
    requireUsed(root, "/");
  }

  // Paths follow the reader's own: a child is at its parent's path and its key, without the
  // children: level, and a list entry adds its index.
  private static void requireUsed(Object node, String path)
  {
    if (node instanceof Tracked tracked) {
      for (Map.Entry<String, Object> entry : untracked(() -> new ArrayList<>(tracked.entrySet()))) {
        String key = entry.getKey();
        if (!tracked.used.contains(key))
          throw new ArtifactParseException("unknown key; it is not part of this artifact's YAML form", key, path);
        Object value = entry.getValue();
        if (key.equals(CHILDREN) && value instanceof Tracked children)
          untracked(() -> children.forEach((name, child) -> requireUsed(child, below(path, name))));
        else if (key.equals(CHILDREN) && value instanceof List<?> children)
          for (Object child : children)
            requireUsed(child, below(path, untracked(() -> child instanceof Tracked named
              && named.get(KEY) instanceof String name ? name : CHILDREN)));
        else
          requireUsed(value, below(path, key));
      }
    } else if (node instanceof List<?> list) {
      for (int i = 0; i < list.size(); i++)
        requireUsed(list.get(i), path + "[" + i + "]");
    }
  }

  private static String below(String path, String key)
  {
    return path + (path.endsWith("/") ? "" : "/") + key;
  }

  /** A mapping that records which of its keys were read. */
  static final class Tracked extends LinkedHashMap<String, Object>
  {
    private final Set<String> used = new LinkedHashSet<>();
    final Map<String, Map.Entry<Object, String>> spellings;

    private Tracked(Map<String, Map.Entry<Object, String>> spellings)
    {
      this.spellings = spellings;
    }

    private boolean tracking()
    {
      return PAUSED.get() == 0;
    }

    @Override public Object get(Object key)
    {
      if (tracking() && key instanceof String name)
        used.add(name);
      return super.get(key);
    }

    @Override public Object getOrDefault(Object key, Object defaultValue)
    {
      if (tracking() && key instanceof String name)
        used.add(name);
      return super.getOrDefault(key, defaultValue);
    }

    @Override public boolean containsKey(Object key)
    {
      if (tracking() && key instanceof String name)
        used.add(name);
      return super.containsKey(key);
    }

    @Override public Set<Map.Entry<String, Object>> entrySet()
    {
      if (tracking())
        used.addAll(super.keySet());
      return super.entrySet();
    }

    @Override public Set<String> keySet()
    {
      if (tracking())
        used.addAll(super.keySet());
      return super.keySet();
    }

    @Override public Collection<Object> values()
    {
      if (tracking())
        used.addAll(super.keySet());
      return super.values();
    }

    @Override public void forEach(BiConsumer<? super String, ? super Object> action)
    {
      if (tracking())
        used.addAll(super.keySet());
      super.forEach(action);
    }
  }
}
