package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.CustomPrettyPrinter;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.ValidationPolicy;
import org.metadatacenter.model.validation.report.ValidationReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * Exits non-zero when a fixture is stale or an artifact is unexpectedly unreadable. Historic invalid
 * definitions are recorded in {@code /concordance/corpus-expected-rejections.json}: their exact source
 * bytes, reader failure and blocking validation finding must all match. They must remain rejected;
 * their old renderings are retained as historical evidence, not compared as current output. A changed
 * or missing source, changed rejection, or unexpected acceptance fails the check. Pass {@code --write} before the
 * path to rewrite the stale fixtures instead of reporting them, which keeps a regeneration and the
 * check that judges it to the same rendering call.
 */
public final class CorpusFixtureVerifier
{
  // The same writer the ArtifactConvertor uses, since that is what wrote the JSON fixtures. Jackson's
  // own pretty printer lays out arrays differently, which made every JSON fixture look stale.
  private static final ObjectWriter PRETTY_OBJECT_WRITER = new ObjectMapper().writer(new CustomPrettyPrinter());

  record ExpectedRejection(String sha256, String code, String path, String exceptionClass,
                           String exceptionMessage, String reason) {}

  record VerificationResult(int checked, List<String> stale, List<String> expectedRejections,
                            List<String> problems) {
    boolean successful(boolean write) { return problems.isEmpty() && (write || stale.isEmpty()); }
  }

  static Map<String, ExpectedRejection> expectedRejections() throws IOException
  {
    var result = new LinkedHashMap<String, ExpectedRejection>();
    try (var input = CorpusFixtureVerifier.class.getResourceAsStream("/concordance/corpus-expected-rejections.json")) {
      if (input == null) throw new IOException("Missing expected-rejection metadata");
      for (var row : new ObjectMapper().readTree(input).path("cases")) {
        String source = row.path("source").asText();
        var expectation = new ExpectedRejection(row.path("sha256").asText(), row.path("code").asText(),
          row.path("path").asText(), row.path("exceptionClass").asText(), row.path("exceptionMessage").asText(),
          row.path("reason").asText());
        if (source.isBlank() || !expectation.sha256().matches("[a-f0-9]{64}") || expectation.code().isBlank()
          || expectation.path().isBlank() || expectation.exceptionClass().isBlank()
          || expectation.exceptionMessage().isBlank() || expectation.reason().isBlank()
          || result.putIfAbsent(source, expectation) != null)
          throw new IOException("Invalid expected-rejection metadata for " + source);
      }
    }
    return result;
  }

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
      System.err.println("Usage: CorpusFixtureVerifier [--write] <path to cedar-test-artifacts>");
      System.exit(2);
    }
    boolean write = List.of(args).contains("--write");
    Path corpus = Path.of(args[args.length - 1]);
    Path artifacts = corpus.resolve("artifacts");
    if (!Files.isDirectory(artifacts)) {
      System.err.println("Not a corpus: " + artifacts.toAbsolutePath());
      System.exit(2);
    }

    var result = verify(corpus, write, expectedRejections());
    System.out.printf("corpus fixtures checked: %d   %s: %d   verified expected rejections: %d   problems: %d%n",
      result.checked(), write ? "rewritten" : "stale", result.stale().size(), result.expectedRejections().size(),
      result.problems().size());
    if (!result.expectedRejections().isEmpty()) {
      System.out.println("\nThese pinned historic definitions remain invalid; their old fixtures are retained:");
      result.expectedRejections().forEach(artifact -> System.out.println("  " + artifact));
    }
    if (!result.problems().isEmpty()) {
      System.out.println("\nUnexpected corpus acceptance or rejection:");
      result.problems().forEach(problem -> System.out.println("  " + problem));
    }
    if (!result.stale().isEmpty()) {
      System.out.println(write ? "\nThese fixtures now hold what this library writes:"
        : "\nThese committed fixtures are not what this library writes now:");
      result.stale().forEach(fixture -> System.out.println("  " + fixture));
      System.out.println("\nRegenerate current fixtures with --write and mirror them into the TypeScript corpus.\n"
        + "Expected-rejection fixtures are historical and are never rewritten by this command.");
    }
    if (!result.successful(write)) System.exit(1);
  }

  static VerificationResult verify(Path corpus, boolean write, Map<String, ExpectedRejection> expectations)
    throws IOException
  {
    Path artifacts = corpus.resolve("artifacts");
    List<String> stale = new ArrayList<>(), rejected = new ArrayList<>(), problems = new ArrayList<>();
    var missing = new HashSet<>(expectations.keySet());
    int checked = 0;

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

          String relative = artifacts.relativize(source).toString().replace('\\', '/');
          var expected = expectations.get(relative);
          missing.remove(relative);
          byte[] bytes = Files.readAllBytes(source);
          if (expected != null && !expected.sha256().equals(sha256(bytes))) {
            problems.add(relative + ": expected-invalid source bytes changed; review its rejection metadata");
            continue;
          }

          Artifact artifact;
          try {
            artifact = read(kind, new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
          } catch (Exception e) {
            if (expected != null && expected.exceptionClass().equals(e.getClass().getName())
              && expected.exceptionMessage().equals(e.getMessage())
              && hasExpectedFinding(kind, bytes, expected))
              rejected.add(relative + ": " + expected.code() + " at " + expected.path() + " — " + expected.reason());
            else problems.add(relative + ": " + e.getClass().getSimpleName() + ": " + e.getMessage()
              + (expected == null ? " (unlisted rejection)" : " (rejection differs from pinned expectation)"));
            continue;
          }
          if (expected != null) {
            problems.add(relative + ": expected-invalid definition was accepted");
            continue;
          }

          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.yaml"),
            YamlSerializer.getYAML(artifact, false, true), stale, write);
          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.compact.yaml"),
            YamlSerializer.getYAML(artifact, true, true), stale, write);
          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.json"),
            renderJson(kind, artifact), stale, write);
        }
      }
    }

    missing.stream().sorted().forEach(source -> problems.add(source + ": expected-invalid source is missing"));
    return new VerificationResult(checked, stale, rejected, problems);
  }

  private static boolean hasExpectedFinding(Kind kind, byte[] bytes, ExpectedRejection expected) throws IOException
  {
    var validator = new CedarValidator();
    var source = new ObjectMapper().readTree(bytes);
    ValidationReport report = switch (kind) {
      case FIELDS -> validator.validateTemplateField(source);
      case ELEMENTS -> validator.validateTemplateElement(source);
      case TEMPLATES -> validator.validateTemplate(source);
      case INSTANCES -> throw new IllegalArgumentException("Instance rejection needs its template; not supported here");
    };
    var blocking = report.getFindings().stream()
      .filter(finding -> ValidationPolicy.blocksSave(finding, ValidationPolicy.Context.TEMPLATE)).toList();
    return !report.canSave() && blocking.size() == 1 && blocking.get(0).code().equals(expected.code())
      && blocking.get(0).path().equals(expected.path());
  }

  static String sha256(byte[] bytes)
  {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is required by Java", e);
    }
  }

  /**
   * Returns 1 when the fixture was present and compared, 0 when there was nothing to compare.
   *
   * <p>Under {@code write} a fixture that differs is rewritten with what was rendered here, so a
   * regeneration cannot be produced by a different call than the one that judges it stale. Only an
   * existing fixture is rewritten: which artifacts the corpus records in which forms is the corpus's
   * business, not this tool's.
   */
  private static int compare(Path fixture, String rendered, List<String> stale, boolean write) throws IOException
  {
    if (!Files.isRegularFile(fixture))
      return 0;
    if (!Files.readString(fixture).trim().equals(rendered.trim())) {
      stale.add(fixture.getParent().getParent().getFileName() + "/" + fixture.getParent().getFileName() + "/"
        + fixture.getFileName());
      if (write)
        Files.writeString(fixture, rendered.trim() + "\n");
    }
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
