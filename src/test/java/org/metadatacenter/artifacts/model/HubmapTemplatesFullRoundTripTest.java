package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.yaml.snakeyaml.Yaml;

import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Full-circle stress test: for every {@code *.yaml} fixture under
 * {@code src/test/resources/hubmap-golden/}, runs the round trip
 *
 * <pre>{@code
 *   originalYamlMap
 *     -> YamlArtifactReader  -> TemplateSchemaArtifact
 *     -> JsonArtifactRenderer -> ObjectNode (CEDAR JSON Schema)
 *     -> JsonArtifactReader  -> TemplateSchemaArtifact
 *     -> YamlArtifactRenderer -> regeneratedYamlMap
 * }</pre>
 *
 * and asserts that {@code originalYamlMap} and {@code regeneratedYamlMap} are
 * structurally equal — same set of keys, same value per key, same list elements
 * in the same order at every level of the tree. The renderer/reader pair on
 * each side is therefore exercised against its complement on the other side, and
 * any drift in either direction surfaces here.
 *
 * <p>Structural equality is computed by {@link Map#equals(Object)} on the
 * top-level {@code LinkedHashMap}s, which is order-independent for keys and
 * order-dependent for list elements — both of which match CEDAR's data model
 * (key insertion order is rendering-only; child ordering is semantic).
 *
 * <p>Sister tests:
 * <ul>
 *   <li>{@link HubmapTemplatesRoundTripTest} — YAML &rarr; JSON Schema +
 *       {@code CedarValidator}. Catches reader / renderer / validator
 *       regressions on the YAML &rarr; JSON Schema half of the pipeline.</li>
 *   <li><b>This test</b> — adds the JSON Schema &rarr; YAML half, closing the
 *       loop. Catches silent field-drops, normalization drift, default-value
 *       leaks, and asymmetric handling of optional fields in either direction.</li>
 * </ul>
 *
 * <p>A failure here means either the YAML reader didn't preserve some field, the
 * JSON renderer dropped it, the JSON reader didn't reconstruct it, or the YAML
 * renderer emitted something different. The diff on assertEquals identifies the
 * field; the test name identifies the template.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HubmapTemplatesFullRoundTripTest
{
  private static final String FIXTURES_RESOURCE = "hubmap-golden";

  private YamlArtifactReader yamlReader;
  private JsonArtifactRenderer jsonRenderer;
  private JsonArtifactReader jsonReader;
  private YamlArtifactRenderer yamlRenderer;

  private final List<String> passes = new ArrayList<>();
  private final List<String> failures = new ArrayList<>();

  @BeforeEach
  public void setUp()
  {
    yamlReader = new YamlArtifactReader();
    jsonRenderer = new JsonArtifactRenderer();
    jsonReader = new JsonArtifactReader();
    // Non-compact: emit modelVersion / version / status / id so the regenerated
    // map carries the same metadata the original fixture does.
    yamlRenderer = new YamlArtifactRenderer(false);
  }

  @BeforeAll
  public void announceBattery()
  {
    System.out.println("[HubmapTemplatesFullRoundTripTest] running against "
      + "src/test/resources/" + FIXTURES_RESOURCE + "/");
  }

  @AfterAll
  public void printSummary()
  {
    int total = passes.size() + failures.size();
    System.out.println("[HubmapTemplatesFullRoundTripTest] summary: "
      + passes.size() + "/" + total + " templates round-tripped structurally");
    if (!failures.isEmpty()) {
      System.out.println("[HubmapTemplatesFullRoundTripTest] failures:");
      for (String f : failures)
        System.out.println("  - " + f);
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("templates")
  public void roundTripYamlThroughJsonAndBackStructurallyEqual(String displayName, Path yamlFile) throws Exception
  {
    String yamlText = Files.readString(yamlFile);

    @SuppressWarnings("unchecked")
    LinkedHashMap<String, Object> originalYamlMap = (LinkedHashMap<String, Object>) new Yaml().load(yamlText);
    assertNotNull(originalYamlMap, "fixture parsed to null: " + yamlFile);

    TemplateSchemaArtifact fromYaml;
    try {
      fromYaml = yamlReader.readTemplateSchemaArtifact(originalYamlMap);
    } catch (Exception e) {
      failures.add(displayName + " — YamlArtifactReader rejected fixture: " + e.getMessage());
      throw e;
    }

    ObjectNode jsonSchema = jsonRenderer.renderTemplateSchemaArtifact(fromYaml);

    TemplateSchemaArtifact fromJson;
    try {
      fromJson = jsonReader.readTemplateSchemaArtifact(jsonSchema);
    } catch (Exception e) {
      failures.add(displayName + " — JsonArtifactReader rejected the rendered JSON Schema: " + e.getMessage());
      throw e;
    }

    LinkedHashMap<String, Object> regeneratedYamlMap = yamlRenderer.renderTemplateSchemaArtifact(fromJson);

    // Walk both trees and find the first structural difference (if any). The walk
    // applies numeric-value equality across Number subtypes, so Integer 0 and Long 0
    // are treated as equal — they represent the same value in CEDAR's data model.
    String diff = describeFirstDifference("", originalYamlMap, regeneratedYamlMap);
    if (diff != null) {
      failures.add(displayName + " — structural diff: " + diff);
      fail("YAML round-trip for " + yamlFile.getFileName() + " is not structurally equal.\n"
        + "First difference: " + diff);
    }

    passes.add(displayName);
  }

  // -----------------------------------------------------------------------
  // argument source
  // -----------------------------------------------------------------------

  public Stream<Arguments> templates() throws Exception
  {
    URL resource = getClass().getClassLoader().getResource(FIXTURES_RESOURCE);
    assertNotNull(resource, "fixtures folder not on classpath: src/test/resources/" + FIXTURES_RESOURCE);
    Path dir = Paths.get(resource.toURI());
    assertTrue(Files.isDirectory(dir), "fixtures path is not a directory: " + dir);

    List<Arguments> args = new ArrayList<>();
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.yaml")) {
      for (Path yaml : ds) {
        String stem = yaml.getFileName().toString();
        String displayName = stem.substring(0, stem.length() - ".yaml".length());
        args.add(Arguments.of(displayName, yaml));
      }
    }
    args.sort((a, b) -> ((String) a.get()[0]).compareTo((String) b.get()[0]));
    return args.stream();
  }

  // -----------------------------------------------------------------------
  // diff helper: walks two maps / lists / scalars and returns a JSON-Pointer-ish
  // path identifying the first structural divergence.
  // -----------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  private static String describeFirstDifference(String path, Object a, Object b)
  {
    if (a == null && b == null) return null;
    if (a == null) return path + " (missing in original; got: " + summarize(b) + ")";
    if (b == null) return path + " (missing in regenerated; original had: " + summarize(a) + ")";

    if (a instanceof Map<?, ?> && b instanceof Map<?, ?>) {
      Map<String, Object> ma = (Map<String, Object>) a;
      Map<String, Object> mb = (Map<String, Object>) b;
      for (String key : ma.keySet()) {
        if (!mb.containsKey(key))
          return path + "/" + key + " (key in original, missing in regenerated)";
        String childDiff = describeFirstDifference(path + "/" + key, ma.get(key), mb.get(key));
        if (childDiff != null) return childDiff;
      }
      for (String key : mb.keySet()) {
        if (!ma.containsKey(key))
          return path + "/" + key + " (key in regenerated, missing in original; value: "
            + summarize(mb.get(key)) + ")";
      }
      return null;
    }

    if (a instanceof List<?> && b instanceof List<?>) {
      List<Object> la = (List<Object>) a;
      List<Object> lb = (List<Object>) b;
      if (la.size() != lb.size())
        return path + " (list size differs: original=" + la.size() + ", regenerated=" + lb.size() + ")";
      for (int i = 0; i < la.size(); i++) {
        String childDiff = describeFirstDifference(path + "[" + i + "]", la.get(i), lb.get(i));
        if (childDiff != null) return childDiff;
      }
      return null;
    }

    // Number subtypes (Integer, Long, Double, ...) compare by numeric value, not by
    // class. The CEDAR data model doesn't distinguish Integer 0 from Long 0; the JSON
    // Schema round-trip can widen Integer to Long depending on the reader.
    if (a instanceof Number && b instanceof Number) {
      if (Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue()) == 0)
        return null;
    }
    if (!a.equals(b))
      return path + " (value differs: original=" + summarize(a) + " <" + a.getClass().getSimpleName()
        + ">, regenerated=" + summarize(b) + " <" + b.getClass().getSimpleName() + ">)";
    return null;
  }

  private static String summarize(Object v)
  {
    if (v == null) return "null";
    String s = String.valueOf(v);
    return s.length() <= 120 ? s : s.substring(0, 120) + "...";
  }
}
