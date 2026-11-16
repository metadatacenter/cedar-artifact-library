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
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
 * <p>So this walks the corpus, the CEE suite included, renders each artifact the three ways the fixtures record — full YAML,
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
 * bytes and reader failure must match, and the validator must block saving them with the pinned finding
 * among its blocking findings. They must remain rejected;
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
                            List<String> problems, Map<String, Set<SourceRenderingComparison.Family>> shown,
                            Map<String, List<String>> unexplained) {
    boolean successful(boolean write) { return problems.isEmpty() && (write || stale.isEmpty()); }
  }

  /** What corpus-source-rendering.json declares: the cases showing each family, and the differences recorded by case. */
  record SourceRenderingDeclarations(Map<String, Set<SourceRenderingComparison.Family>> families,
                                     Map<String, List<String>> recorded) {}

  static SourceRenderingDeclarations sourceRenderingDeclarations() throws IOException
  {
    try (var input = CorpusFixtureVerifier.class.getResourceAsStream("/concordance/corpus-source-rendering.json")) {
      if (input == null) throw new IOException("Missing corpus source rendering declarations");
      var root = new ObjectMapper().readTree(input);
      Map<String, Set<SourceRenderingComparison.Family>> families = new TreeMap<>();
      root.path("families").fields().forEachRemaining(entry -> entry.getValue().forEach(id -> families
        .computeIfAbsent(id.asText(), any -> EnumSet.noneOf(SourceRenderingComparison.Family.class))
        .add(SourceRenderingComparison.Family.of(entry.getKey()))));
      if (families.values().stream().anyMatch(f -> f.contains(SourceRenderingComparison.Family.SCHEMA_TEXT)))
        throw new IOException("schemaText is allowed everywhere and declared nowhere");
      Map<String, List<String>> recorded = new TreeMap<>();
      root.path("recorded").fields().forEachRemaining(entry -> {
        if (entry.getValue().path("reason").asText().isBlank())
          throw new IllegalArgumentException("No reason given for the differences recorded for " + entry.getKey());
        List<String> paths = new ArrayList<>();
        entry.getValue().path("paths").forEach(path -> paths.add(path.asText()));
        recorded.put(entry.getKey(), paths);
      });
      return new SourceRenderingDeclarations(families, recorded);
    }
  }

  /**
   * Each schema artifact's source, compared with the JSON this library writes from it, may differ only in the
   * normalizations corpus-source-rendering.json declares for that case and the differences it records for it.
   * A case showing an undeclared family or an unrecorded difference fails, and so does one that stops showing
   * a declared family or a recorded difference.
   */
  static List<String> sourceRenderingProblems(VerificationResult result, SourceRenderingDeclarations declared)
  {
    List<String> problems = new ArrayList<>();
    Set<String> cases = new TreeSet<>(result.shown().keySet());
    cases.addAll(result.unexplained().keySet());
    cases.addAll(declared.families().keySet());
    cases.addAll(declared.recorded().keySet());
    for (String id : cases) {
      Set<SourceRenderingComparison.Family> shown = result.shown().getOrDefault(id, Set.of());
      Set<SourceRenderingComparison.Family> expected = declared.families().getOrDefault(id, Set.of());
      if (!shown.equals(expected))
        problems.add(id + ": shows " + shown.stream().map(SourceRenderingComparison.Family::key).sorted().toList()
          + " where corpus-source-rendering.json declares "
          + expected.stream().map(SourceRenderingComparison.Family::key).sorted().toList());
      List<String> unexplained = result.unexplained().getOrDefault(id, List.of());
      List<String> recorded = declared.recorded().getOrDefault(id, List.of());
      unexplained.stream().filter(d -> recorded.stream().noneMatch(path -> d.startsWith(path + ":"))).limit(8)
        .forEach(d -> problems.add(id + ": its source and its JSON rendering differ at " + d));
      recorded.stream().filter(path -> unexplained.stream().noneMatch(d -> d.startsWith(path + ":")))
        .forEach(path -> problems.add(id + ": recorded difference at " + path + " is no longer shown"));
    }
    return problems;
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

  /** The sources, by path below {@code artifacts}, that are not JSON on purpose, with the reason. */
  static Map<String, String> expectedUnparseable() throws IOException
  {
    var result = new LinkedHashMap<String, String>();
    try (var input = CorpusFixtureVerifier.class.getResourceAsStream("/concordance/corpus-expected-rejections.json")) {
      if (input == null) throw new IOException("Missing expected-rejection metadata");
      for (var row : new ObjectMapper().readTree(input).path("unparseable")) {
        String source = row.path("source").asText(), reason = row.path("reason").asText();
        if (source.isBlank() || reason.isBlank() || result.putIfAbsent(source, reason) != null)
          throw new IOException("Invalid unparseable-source metadata for " + source);
      }
    }
    return result;
  }

  /**
   * How many cases each collection holds, as {@code /concordance/corpus-inventory.json} records it. A case
   * removed from the corpus removes it from this check and from the TypeScript comparisons, so a count that
   * differs from the record fails until the record is changed with the corpus.
   */
  static List<String> inventoryProblems(Path artifacts) throws IOException
  {
    List<String> problems = new ArrayList<>();
    try (var input = CorpusFixtureVerifier.class.getResourceAsStream("/concordance/corpus-inventory.json")) {
      if (input == null) throw new IOException("Missing corpus inventory");
      var counts = new ObjectMapper().readTree(input).path("caseCounts");
      var names = new ArrayList<String>();
      counts.fieldNames().forEachRemaining(names::add);
      for (String collection : names) {
        Path directory = artifacts.resolve(collection);
        long found = 0;
        if (Files.isDirectory(directory))
          try (var cases = Files.list(directory)) { found = cases.filter(Files::isDirectory).count(); }
        if (found != counts.get(collection).asLong())
          problems.add(collection + ": " + found + " cases, where corpus-inventory.json records "
            + counts.get(collection).asLong() + "; change the record with the corpus");
      }
      for (String collection : List.of("fields", "elements", "templates", "instances", "cee-suite"))
        if (!names.contains(collection)) problems.add(collection + ": no count in corpus-inventory.json");
    }
    return problems;
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

    var result = verify(corpus, write, expectedRejections(), expectedUnparseable());
    result.problems().addAll(inventoryProblems(artifacts));
    result.problems().addAll(sourceRenderingProblems(result, sourceRenderingDeclarations()));
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
    return verify(corpus, write, expectations, Map.of());
  }

  /**
   * {@code unparseable} holds the sources, by path below {@code artifacts}, that are not JSON on purpose and
   * so record no Java rendering. Every other readable case must record all three.
   */
  static VerificationResult verify(Path corpus, boolean write, Map<String, ExpectedRejection> expectations,
    Map<String, String> unparseable) throws IOException
  {
    Path artifacts = corpus.resolve("artifacts");
    List<String> stale = new ArrayList<>(), rejected = new ArrayList<>(), problems = new ArrayList<>();
    Map<String, Set<SourceRenderingComparison.Family>> shown = new TreeMap<>();
    Map<String, List<String>> unexplained = new TreeMap<>();
    var missing = new HashSet<>(expectations.keySet());
    var unlistedUnparseable = new HashSet<>(unparseable.keySet());
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
          if (expected != null) {
            checkExpectedRejection(kind, relative, bytes, expected, rejected, problems);
            continue;
          }

          Artifact artifact;
          try {
            artifact = read(kind, new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
          } catch (Exception e) {
            problems.add(relative + ": " + e.getClass().getSimpleName() + ": " + e.getMessage() + " (unlisted rejection)");
            continue;
          }

          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.yaml"),
            YamlSerializer.getYAML(artifact, false, true), stale, problems, write);
          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.compact.yaml"),
            YamlSerializer.getYAML(artifact, true, true), stale, problems, write);
          checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.json"),
            renderJson(kind, artifact), stale, problems, write);
          if (kind != Kind.INSTANCES)
            compareSource(name, bytes, renderJson(kind, artifact), shown, unexplained);
        }
      }
    }

    // Each CEE suite case holds a template and often an instance, and its Java renderings are checked as
    // the numbered collections' are. A template this library refuses is pinned like a numbered one, and a
    // template that is malformed on purpose is listed as unparseable; every other case records all three.
    Path suite = artifacts.resolve("cee-suite");
    if (Files.isDirectory(suite)) {
      try (var cases = Files.list(suite)) {
        for (Path caseDirectory : cases.filter(Files::isDirectory).sorted().toList()) {
          String number = caseDirectory.getFileName().toString();
          for (Kind kind : List.of(Kind.TEMPLATES, Kind.INSTANCES)) {
            String name = kind.stem + "-" + number;
            Path source = caseDirectory.resolve(name + ".json");
            String relative = artifacts.relativize(source).toString().replace('\\', '/');
            var expected = expectations.get(relative);
            if (expected != null) {
              missing.remove(relative);
              if (Files.isRegularFile(source))
                checkExpectedRejection(kind, relative, Files.readAllBytes(source), expected, rejected, problems);
              continue;
            }
            if (!Files.isRegularFile(source))
              continue;
            if (unparseable.containsKey(relative)) {
              unlistedUnparseable.remove(relative);
              try {
                parse(Files.readString(source));
                problems.add(relative + ": listed as unparseable, but it is JSON now");
              } catch (IllegalArgumentException notJson) {
                rejected.add(relative + ": not JSON — " + unparseable.get(relative));
              }
              continue;
            }
            Artifact artifact;
            try {
              ObjectNode json = parse(Files.readString(source));
              if (kind == Kind.INSTANCES && !json.has("schema:isBasedOn"))
                json = ceeInstanceAsStored(json, parse(Files.readString(caseDirectory.resolve("template-" + number + ".json"))),
                  "cee-" + name);
              artifact = read(kind, json.toString());
            } catch (Exception e) {
              problems.add(relative + ": " + e.getClass().getSimpleName() + ": " + e.getMessage() + " (neither pinned nor listed as unparseable)");
              continue;
            }
            checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.yaml"),
              YamlSerializer.getYAML(artifact, false, true), stale, problems, write);
            checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.compact.yaml"),
              YamlSerializer.getYAML(artifact, true, true), stale, problems, write);
            checked += compare(caseDirectory.resolve(name + "-generated-java-artifact-lib.json"),
              renderJson(kind, artifact), stale, problems, write);
            if (kind == Kind.TEMPLATES)
              compareSource("cee-" + name, Files.readAllBytes(source), renderJson(kind, artifact), shown, unexplained);
          }
        }
      }
    }

    missing.stream().sorted().forEach(source -> problems.add(source + ": expected-invalid source is missing"));
    unlistedUnparseable.stream().sorted().forEach(source -> problems.add(source + ": unparseable source is missing"));
    return new VerificationResult(checked, stale, rejected, problems, shown, unexplained);
  }

  /**
   * A CEE suite instance as a host stores it. Most of the suite's instances are CEE's output before a host
   * stores it: data alone, with no name and no template named. This library's reader requires both, so
   * such an instance is given the template in its own case and its case identifier as its name. The
   * TypeScript library's corpus generators give it the same.
   */
  static ObjectNode ceeInstanceAsStored(ObjectNode instance, ObjectNode template, String caseId)
  {
    if (instance.has("schema:isBasedOn"))
      return instance;
    ObjectNode stored = instance.deepCopy();
    stored.set("schema:isBasedOn", template.get("@id"));
    stored.put("schema:name", caseId);
    return stored;
  }

  private static void compareSource(String id, byte[] source, String rendering,
    Map<String, Set<SourceRenderingComparison.Family>> shown, Map<String, List<String>> unexplained) throws IOException
  {
    var mapper = new ObjectMapper();
    for (var difference : SourceRenderingComparison.compare(mapper.readTree(source), mapper.readTree(rendering))) {
      if (difference.family().isEmpty())
        unexplained.computeIfAbsent(id, any -> new ArrayList<>()).add(difference.toString());
      else if (difference.family().get() != SourceRenderingComparison.Family.SCHEMA_TEXT)
        shown.computeIfAbsent(id, any -> EnumSet.noneOf(SourceRenderingComparison.Family.class)).add(difference.family().get());
    }
  }

  /** A pinned definition must keep its bytes, and the reader must refuse it with the pinned exception. */
  private static void checkExpectedRejection(Kind kind, String relative, byte[] bytes, ExpectedRejection expected,
    List<String> rejected, List<String> problems) throws IOException
  {
    if (!expected.sha256().equals(sha256(bytes))) {
      problems.add(relative + ": expected-invalid source bytes changed; review its rejection metadata");
      return;
    }
    try {
      read(kind, new String(bytes, java.nio.charset.StandardCharsets.UTF_8));
    } catch (Exception e) {
      if (expected.exceptionClass().equals(e.getClass().getName()) && expected.exceptionMessage().equals(e.getMessage())
        && hasExpectedFinding(kind, bytes, expected))
        rejected.add(relative + ": " + expected.code() + " at " + expected.path() + " — " + expected.reason());
      else problems.add(relative + ": " + e.getClass().getSimpleName() + ": " + e.getMessage()
        + " (rejection differs from pinned expectation)");
      return;
    }
    problems.add(relative + ": expected-invalid definition was accepted");
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
    return !report.canSave() && blocking.stream()
      .anyMatch(finding -> finding.code().equals(expected.code()) && finding.path().equals(expected.path()));
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
   * Returns 1 when the fixture was present and compared, 0 when it is missing, which is a problem: every
   * readable case records all three Java renderings, and the TypeScript library compares its own output
   * with each of them, so a missing one would leave both comparisons short without notice.
   *
   * <p>Under {@code write} a fixture that differs is rewritten with what was rendered here, so a
   * regeneration cannot be produced by a different call than the one that judges it stale. Only an
   * existing fixture is rewritten: which cases the corpus records is the corpus's business, not this tool's.
   */
  private static int compare(Path fixture, String rendered, List<String> stale, List<String> problems, boolean write)
    throws IOException
  {
    if (!Files.isRegularFile(fixture)) {
      problems.add(fixture.getParent().getParent().getFileName() + "/" + fixture.getParent().getFileName() + "/"
        + fixture.getFileName() + ": missing; every readable case records all three Java renderings");
      return 0;
    }
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
