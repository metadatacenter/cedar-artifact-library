package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.model.validation.NumericPolicy;
import org.metadatacenter.model.policy.JsonSourcePolicy;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/** Raw artifact boundary: one document, unique string keys, exact numbers and YAML 1.2 core scalars. */
public final class ArtifactSourceParser {
  private ArtifactSourceParser() {}
  private static final ObjectMapper JSON = JsonSourcePolicy.apply(new ObjectMapper());

  public static ObjectNode json(String source) throws IOException {
    var node = JSON.readTree(source);
    if (!(node instanceof ObjectNode object)) throw new IOException("Expected a JSON object");
    return object;
  }

  public static LinkedHashMap<String, Object> yaml(String source) throws IOException {
    try {
      var loading = new LoaderOptions();
      var dumping = new DumperOptions();
      // Compose syntax only. We interpret scalar spellings ourselves, without SnakeYAML's
      // YAML 1.1 octal, yes/no, timestamp or Java object construction rules.
      var parser = new Yaml(new SafeConstructor(loading), new Representer(dumping), dumping, loading,
          new CoreResolver());
      Node root = parser.compose(new StringReader(source));
      Object result = new Values().read(root);
      if (!(result instanceof LinkedHashMap<?, ?>)) throw new IllegalArgumentException("Expected a YAML mapping");
      @SuppressWarnings("unchecked") var map = (LinkedHashMap<String, Object>) result;
      return map;
    } catch (RuntimeException invalid) {
      throw new IOException("Invalid artifact YAML: " + invalid.getMessage(), invalid);
    }
  }

  /**
   * The text a number or boolean in a parsed YAML mapping was written as, for a position that holds a
   * literal rather than a number, while the mapping still holds the value read from that text.
   */
  static Optional<String> spelling(Map<String, Object> mapping, String key) {
    if (!(mapping instanceof Mapping parsed)) return Optional.empty();
    var spelling = parsed.spellings.get(key);
    return spelling != null && spelling.getKey() == mapping.get(key) ? Optional.of(spelling.getValue()) : Optional.empty();
  }

  /** A YAML mapping that also keeps the source text of each number and boolean it holds. */
  private static final class Mapping extends LinkedHashMap<String, Object> {
    private final Map<String, Map.Entry<Object, String>> spellings = new HashMap<>();
  }

  private static final class CoreResolver extends Resolver {
    @Override protected void addImplicitResolvers() {
      addImplicitResolver(Tag.NULL, Pattern.compile("^(?:~|null|Null|NULL|)$"), "~nN\0");
      addImplicitResolver(Tag.BOOL, Pattern.compile("^(?:true|True|TRUE|false|False|FALSE)$"), "tTfF");
      addImplicitResolver(Tag.INT, Pattern.compile("^(?:[-+]?[0-9]+|0o[0-7]+|0x[0-9a-fA-F]+)$"), "-+0123456789");
      addImplicitResolver(Tag.FLOAT, Pattern.compile(
          "^(?:[-+]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][-+]?[0-9]+)?"
          + "|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$"), "-+0123456789.");
    }
  }

  private static final class Values {
    private final Set<Node> active = Collections.newSetFromMap(new IdentityHashMap<>());
    private int remaining = 1_000_000;

    Object read(Node node) {
      if (node == null) return null;
      if (--remaining < 0 || !active.add(node)) throw new IllegalArgumentException("YAML alias expansion limit or cycle");
      try {
        if (node instanceof MappingNode map && Tag.MAP.equals(node.getTag())) {
          var result = new Mapping();
          for (var pair : map.getValue()) {
            if (!(pair.getKeyNode() instanceof ScalarNode key))
              throw new IllegalArgumentException("Artifact mapping keys must be scalar strings");
            if (!Set.of(Tag.STR, Tag.NULL, Tag.BOOL, Tag.INT, Tag.FLOAT).contains(key.getTag()))
              throw new IllegalArgumentException("Unsupported artifact YAML key tag: " + key.getTag());
            String name = key.getValue();
            if (result.containsKey(name)) throw new IllegalArgumentException("Duplicate mapping key: " + name);
            Object value = read(pair.getValueNode());
            result.put(name, value);
            if ((value instanceof Number || value instanceof Boolean) && pair.getValueNode() instanceof ScalarNode scalar)
              result.spellings.put(name, new AbstractMap.SimpleImmutableEntry<>(value, scalar.getValue()));
          }
          return result;
        }
        if (node instanceof SequenceNode sequence && Tag.SEQ.equals(node.getTag())) {
          var result = new ArrayList<Object>();
          for (var child : sequence.getValue()) result.add(read(child));
          return result;
        }
        if (node instanceof ScalarNode scalar) {
          String value = scalar.getValue();
          Tag tag = scalar.getTag();
          if (Tag.STR.equals(tag)) return value;
          if (Tag.NULL.equals(tag) && value.matches("~|null|Null|NULL|")) return null;
          if (Tag.BOOL.equals(tag) && value.matches("true|True|TRUE|false|False|FALSE"))
            return value.equalsIgnoreCase("true");
          if (Tag.INT.equals(tag)) {
            BigInteger integer;
            if (value.matches("0x[0-9a-fA-F]+")) integer = new BigInteger(value.substring(2), 16);
            else if (value.matches("0o[0-7]+")) integer = new BigInteger(value.substring(2), 8);
            else if (value.matches("[-+]?[0-9]+")) integer = new BigInteger(value);
            else throw new IllegalArgumentException("Invalid YAML integer");
            try { return integer.intValueExact(); } catch (ArithmeticException large) { /* try long */ }
            try { return integer.longValueExact(); } catch (ArithmeticException large) { return integer; }
          }
          if (Tag.FLOAT.equals(tag)) return NumericPolicy.parse(value);
        }
        throw new IllegalArgumentException("Unsupported artifact YAML tag: " + node.getTag());
      } finally { active.remove(node); }
    }
  }
}
