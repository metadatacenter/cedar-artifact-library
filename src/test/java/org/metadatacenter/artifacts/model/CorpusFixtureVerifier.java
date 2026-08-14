package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.CustomPrettyPrinter;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks that the Java-generated fixtures committed to the shared corpus are what this library writes
 * today.
 *
 * <p>The corpus at {@code metadatacenter/cedar-test-artifacts} carries, for every artifact, the output
 * of both model libraries. The TypeScript library's continuous integration compares its own live output
 * against the Java fixtures committed there, which is only meaningful while those fixtures are current:
 * a change to a writer here, unaccompanied by a regeneration, leaves that comparison measuring output
 * this library no longer produces. It went unnoticed for months once.
 *
 * <p>So this walks the corpus, renders each artifact the three ways the fixtures record — full YAML,
 * compact YAML, JSON — and reports every fixture whose committed bytes differ from what it produced.
 * The renderings go through the same calls the {@code ArtifactConvertor} makes for {@code -yof -yq},
 * {@code -yof -yq -cy} and {@code -jof}, since that is what generated them.
 *
 * <p>Not a unit test (no {@code @Test} methods): it needs the corpus, which is a separate repository
 * and not on the classpath. Run with:
 *
 * <pre>{@code
 *   mvn test-compile exec:java \
 *       -Dexec.classpathScope=test \
 *       -Dexec.mainClass=org.metadatacenter.artifacts.model.CorpusFixtureVerifier \
 *       -Dexec.args=/path/to/cedar-test-artifacts
 * }</pre>
 *
 * Exits non-zero when a fixture is stale, so continuous integration can stand on it.
 */
public final class CorpusFixtureVerifier
{
  // The same writer the ArtifactConvertor uses, since that is what wrote the JSON fixtures. Jackson's
  // own pretty printer lays out arrays differently, which made every JSON fixture look stale.
  private static final ObjectWriter PRETTY_OBJECT_WRITER = new ObjectMapper().writer(new CustomPrettyPrinter());

  /** The artifact kinds the corpus holds, by directory, with the reader each one needs. */
  private enum Kind
  {
    FIELDS("fields", "field"),
    ELEMENTS("elements", "element"),
    TEMPLATES("templates", "template"),
    INSTANCES("instances", "instance");

    private final String directory;
    private final String stem;

    Kind(String directory, String stem)
    {
      this.directory = directory;
      this.stem = stem;
    }
  }

  public static void main(String[] args) throws IOException
  {
    if (args.length < 1) {
      System.err.println("Usage: CorpusFixtureVerifier <path to cedar-test-artifacts>");
      System.exit(2);
    }
    Path artifacts = Path.of(args[0]).resolve("artifacts");
    if (!Files.isDirectory(artifacts)) {
      System.err.println("Not a corpus: " + artifacts.toAbsolutePath());
      System.exit(2);
    }

    List<String> stale = new ArrayList<>();
    int checked = 0;
    int unreadable = 0;

    for (Kind kind : Kind.values()) {
      Path kindDirectory = artifacts.resolve(kind.directory);
      if (!Files.isDirectory(kindDirectory))
        continue;
      try (var cases = Files.list(kindDirectory)) {
        for (Path caseDirectory : cases.filter(Files::isDirectory).sorted().toList()) {
          String name = kind.stem + "-" + caseDirectory.getFileName();
          Path source = caseDirectory.resolve(name + ".json");
          if (!Files.isRegularFile(source))
            continue;

          Artifact artifact;
          try {
            artifact = read(kind, Files.readString(source));
          } catch (Exception e) {
            // A corpus artifact this library cannot read is a divergence in its own right, recorded in
            // the parity survey rather than here. Counted so the run says how many it passed over.
            unreadable++;
            continue;
          }

          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.yaml"),
            YamlSerializer.getYAML(artifact, false, true), stale);
          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.compact.yaml"),
            YamlSerializer.getYAML(artifact, true, true), stale);
          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.json"),
            renderJson(kind, artifact), stale);
        }
      }
    }

    System.out.printf("corpus fixtures checked: %d   stale: %d   artifacts this library cannot read: %d%n",
      checked, stale.size(), unreadable);
    if (!stale.isEmpty()) {
      System.out.println("\nThese committed fixtures are not what this library writes now:");
      stale.forEach(fixture -> System.out.println("  " + fixture));
      System.out.println("\nRegenerate them in cedar-test-artifacts, and in the copy the TypeScript library carries,\n"
        + "or the comparison between the two libraries measures output this one no longer produces.");
      System.exit(1);
    }
  }

  /** Returns 1 when the fixture was present and compared, 0 when there was nothing to compare. */
  private static int compare(Path fixture, String rendered, List<String> stale) throws IOException
  {
    if (!Files.isRegularFile(fixture))
      return 0;
    if (!Files.readString(fixture).trim().equals(rendered.trim()))
      stale.add(fixture.getParent().getParent().getFileName() + "/" + fixture.getParent().getFileName() + "/"
        + fixture.getFileName());
    return 1;
  }

  private static Artifact read(Kind kind, String json)
  {
    JsonArtifactReader reader = new JsonArtifactReader();
    return switch (kind) {
      case FIELDS -> reader.readFieldSchemaArtifact(parse(json));
      case ELEMENTS -> reader.readElementSchemaArtifact(parse(json));
      case TEMPLATES -> reader.readTemplateSchemaArtifact(parse(json));
      case INSTANCES -> reader.readTemplateInstanceArtifact(parse(json));
    };
  }

  private static String renderJson(Kind kind, Artifact artifact) throws IOException
  {
    JsonArtifactRenderer renderer = new JsonArtifactRenderer();
    ObjectNode rendering = switch (kind) {
      case FIELDS -> renderer.renderFieldSchemaArtifact((org.metadatacenter.artifacts.model.core.FieldSchemaArtifact) artifact);
      case ELEMENTS -> renderer.renderElementSchemaArtifact((org.metadatacenter.artifacts.model.core.ElementSchemaArtifact) artifact);
      case TEMPLATES -> renderer.renderTemplateSchemaArtifact((org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact) artifact);
      case INSTANCES -> renderer.renderTemplateInstanceArtifact((org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact) artifact);
    };
    return PRETTY_OBJECT_WRITER.writeValueAsString(rendering);
  }

  private static ObjectNode parse(String json)
  {
    try {
      return (ObjectNode) new ObjectMapper().readTree(json);
    } catch (IOException e) {
      throw new IllegalArgumentException("not JSON", e);
    }
  }
}
