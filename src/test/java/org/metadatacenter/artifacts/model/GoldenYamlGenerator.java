package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

/**
 * One-shot regeneration utility for the golden YAML fixtures under
 * {@code src/test/resources/templates-yaml/}.
 *
 * <p>Real-world CEDAR JSON Schemas shipped by older toolchains are typically
 * non-canonical against the current artifact library — they omit required metadata
 * ({@code modelVersion}, {@code version}, {@code status}) or contain bare-integer
 * values where the reader expects strings. Rather than make consumers lenient,
 * this utility generates canonical YAML by round-tripping a directory of
 * authoritative JSON Schemas through:
 *
 * <pre>{@code
 *   JsonArtifactReader.readTemplateSchemaArtifact
 *     -> YamlArtifactRenderer.renderTemplateSchemaArtifact  (non-compact)
 *     -> SnakeYAML dump
 * }</pre>
 *
 * <p>One library asymmetry is accommodated here: {@link YamlArtifactRenderer}
 * suppresses {@code modelVersion} / {@code version} / {@code status} in compact
 * mode (its default), but the YAML reader requires them. Non-compact mode is
 * selected here. (Earlier versions of this generator also stringified raw URI /
 * OffsetDateTime / enum scalars left in the rendered map by the renderer — that
 * has since been fixed at the renderer; the map now contains only YAML-native
 * scalars.)
 *
 * <p>Only a golden that already exists is rewritten, so the source directory may hold other templates.
 * {@link TemplateSourceRenderingTest} holds each golden to what {@link #golden} writes from its source.
 *
 * <p>Not a unit test (no {@code @Test} methods). Run with:
 *
 * <pre>{@code
 *   mvn test-compile exec:java \
 *       -Dexec.classpathScope=test \
 *       -Dexec.mainClass=org.metadatacenter.artifacts.model.GoldenYamlGenerator \
 *       -Dexec.args="src/test/resources/templates src/test/resources/templates-yaml"
 * }</pre>
 */
public final class GoldenYamlGenerator
{
  private GoldenYamlGenerator() {}

  /** The golden YAML for a source: JSON -> model -> full YAML, dumped in block style. */
  static String golden(ObjectNode source)
  {
    DumperOptions opts = new DumperOptions();
    opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    opts.setPrettyFlow(true);
    opts.setIndent(2);
    opts.setSplitLines(false);
    TemplateSchemaArtifact template = new JsonArtifactReader().readTemplateSchemaArtifact(source);
    LinkedHashMap<String, Object> yamlMap = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(template);
    return new Yaml(opts).dump(yamlMap);
  }

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
    int ok = 0;
    int fail = 0;
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(inDir, "*.json")) {
      for (Path jsonFile : ds) {
        String stem = jsonFile.getFileName().toString();
        stem = stem.substring(0, stem.length() - ".json".length());
        Path outFile = outDir.resolve(stem + ".yaml");
        if (!Files.isRegularFile(outFile))
          continue;
        try {
          ObjectNode node = (ObjectNode) jackson.readTree(Files.newBufferedReader(jsonFile));
          Files.writeString(outFile, golden(node), StandardCharsets.UTF_8);
          System.out.println("ok  " + stem);
          ok++;
        } catch (Exception e) {
          System.err.println("FAIL " + stem + " -- " + e.getClass().getSimpleName() + ": " + e.getMessage());
          fail++;
        }
      }
    }
    System.out.println();
    System.out.println("Regenerated " + ok + " golden YAML files; " + fail + " failures.");
    if (fail > 0) System.exit(2);
  }
}
