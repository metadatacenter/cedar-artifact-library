package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One-shot regeneration utility for the golden YAML fixtures under
 * {@code src/test/resources/hubmap-golden/}.
 *
 * <p>The HuBMAP YAML files (as originally shipped) are non-canonical against the
 * current artifact library — they omit required metadata ({@code modelVersion},
 * {@code version}, {@code status}) and contain bare-integer values where the
 * reader expects strings. Rather than make consumers lenient, this utility
 * generates canonical YAML by round-tripping the paired authoritative JSON
 * Schemas through:
 *
 * <pre>{@code
 *   JsonArtifactReader.readTemplateSchemaArtifact
 *     -> YamlArtifactRenderer.renderTemplateSchemaArtifact  (non-compact)
 *     -> SnakeYAML dump (with non-YAML-native scalars stringified first)
 * }</pre>
 *
 * <p>Two libraryasymmetries are accommodated here, not by the consuming tool:
 * <ol>
 *   <li>{@link YamlArtifactRenderer} suppresses {@code modelVersion} / {@code version}
 *       / {@code status} in compact mode (its default); the YAML reader requires
 *       them. Non-compact mode is selected here.</li>
 *   <li>{@link YamlArtifactRenderer} leaves {@link URI}, {@link OffsetDateTime}, and
 *       field-datatype enums as live Java objects in the output map. SnakeYAML's
 *       default representer emits those with Java type tags
 *       ({@code !!java.net.URI {}}) that the YAML reader cannot ingest. This
 *       utility walks the map and stringifies any non-YAML-native scalar before
 *       handing it to SnakeYAML.</li>
 * </ol>
 *
 * <p>Both deserve a separate fix in the renderer; this utility unblocks downstream
 * consumers in the meantime.
 *
 * <p>Not a unit test (no {@code @Test} methods). Run with:
 *
 * <pre>{@code
 *   mvn test-compile exec:java \
 *       -Dexec.classpathScope=test \
 *       -Dexec.mainClass=org.metadatacenter.artifacts.model.GoldenYamlGenerator \
 *       -Dexec.args="/path/to/source-json-dir src/test/resources/hubmap-golden"
 * }</pre>
 */
public final class GoldenYamlGenerator
{
  private GoldenYamlGenerator() {}

  public static void main(String[] args) throws IOException
  {
    if (args.length != 2) {
      System.err.println("usage: GoldenYamlGenerator <input-dir-with-json> <output-dir-for-yaml>");
      System.exit(1);
    }
    Path inDir = Paths.get(args[0]);
    Path outDir = Paths.get(args[1]);
    if (!Files.isDirectory(inDir))
      throw new IllegalArgumentException("input dir does not exist: " + inDir);
    Files.createDirectories(outDir);

    ObjectMapper jackson = new ObjectMapper();
    JsonArtifactReader reader = new JsonArtifactReader();
    YamlArtifactRenderer renderer = new YamlArtifactRenderer(false);

    DumperOptions opts = new DumperOptions();
    opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    opts.setPrettyFlow(true);
    opts.setIndent(2);
    opts.setSplitLines(false);

    int ok = 0;
    int fail = 0;
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(inDir, "*.json")) {
      for (Path jsonFile : ds) {
        String stem = jsonFile.getFileName().toString();
        stem = stem.substring(0, stem.length() - ".json".length());
        Path outFile = outDir.resolve(stem + ".yaml");
        try {
          ObjectNode node = (ObjectNode) jackson.readTree(Files.newBufferedReader(jsonFile));
          TemplateSchemaArtifact template = reader.readTemplateSchemaArtifact(node);
          LinkedHashMap<String, Object> yamlMap = renderer.renderTemplateSchemaArtifact(template);
          stringifyJavaScalars(yamlMap);
          String yamlText = new Yaml(opts).dump(yamlMap);
          Files.writeString(outFile, yamlText, StandardCharsets.UTF_8);
          System.out.println("ok  " + stem);
          ok++;
        } catch (Exception e) {
          System.err.println("FAIL " + stem + " -- " + e.getClass().getSimpleName() + ": " + e.getMessage());
          fail++;
        }
      }
    }
    System.out.println();
    System.out.println("Generated " + ok + " golden YAML files; " + fail + " failures.");
    if (fail > 0) System.exit(2);
  }

  /**
   * Walks a YAML-shaped Object tree and replaces any non-YAML-native scalar with
   * its {@code toString()} form. YAML-native means {@code null}, {@code String},
   * {@code Number}, {@code Boolean}, plus the recursable {@code Map} / {@code List}
   * containers. Everything else (URI, OffsetDateTime, library enums, …) gets
   * stringified.
   */
  @SuppressWarnings("unchecked")
  private static Object stringifyJavaScalars(Object node)
  {
    if (node == null) return null;
    if (node instanceof Map<?, ?>) {
      Map<String, Object> map = (Map<String, Object>) node;
      for (Map.Entry<String, Object> entry : map.entrySet())
        entry.setValue(stringifyJavaScalars(entry.getValue()));
      return map;
    }
    if (node instanceof List<?>) {
      List<Object> list = (List<Object>) node;
      for (int i = 0; i < list.size(); i++) list.set(i, stringifyJavaScalars(list.get(i)));
      return list;
    }
    if (node instanceof String || node instanceof Number || node instanceof Boolean)
      return node;
    return node.toString();
  }
}
