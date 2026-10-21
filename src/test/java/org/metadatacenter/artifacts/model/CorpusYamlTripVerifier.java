package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.YamlTripComparison.Difference;
import org.metadatacenter.artifacts.model.YamlTripComparison.Family;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.ArtifactSourceParser;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.tools.YamlSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Checks that every artifact in the shared corpus writes the same JSON straight from its JSON source
 * as it does after a trip through this library's own YAML, apart from the families of difference the
 * YAML form is known to produce.
 *
 * <p>The schema artifacts' JSON is written from the artifact the JSON source yields, and again from the
 * artifact that artifact's YAML yields. An instance read back from YAML is completed against its
 * template before it is written, as the resource server completes a YAML write. An instance finds its
 * template by {@code schema:isBasedOn}; a CEE suite instance that names none takes the template in its
 * own case. {@code /concordance/corpus-yaml-trip.json} declares which cases show each family and which
 * cases cannot be compared. A case showing an undeclared family fails, and so does one that stops
 * showing a declared one or can now be compared despite its exclusion.
 *
 * <p>Not a unit test, for the reason {@link CorpusFixtureVerifier} gives. Run with:
 *
 * <pre>{@code
 *   mvn test-compile exec:java \
 *       -Dexec.classpathScope=test \
 *       -Dexec.mainClass=org.metadatacenter.artifacts.model.CorpusYamlTripVerifier \
 *       -Dexec.args=/path/to/cedar-test-artifacts
 * }</pre>
 */
public final class CorpusYamlTripVerifier
{
  record Declarations(Map<String, Set<Family>> families, Map<String, String> exclusions) {}

  record VerificationResult(int compared, int excluded, Map<String, Set<Family>> shown, List<String> problems)
  {
    boolean successful() { return problems.isEmpty(); }
  }

  private record TripCase(String id, Supplier<List<Difference>> trip) {}

  private static final JsonArtifactRenderer RENDERER = new JsonArtifactRenderer();
  private static final int SHOWN_DIFFERENCES = 8;
  private static final int EXAMPLES = 3;

  static Declarations declarations() throws IOException
  {
    try (var input = CorpusYamlTripVerifier.class.getResourceAsStream("/concordance/corpus-yaml-trip.json")) {
      if (input == null) throw new IOException("Missing YAML trip declarations");
      JsonNode root = new ObjectMapper().readTree(input);
      Map<String, Set<Family>> families = new TreeMap<>();
      root.path("families").fields().forEachRemaining(entry -> entry.getValue().forEach(id ->
        families.computeIfAbsent(id.asText(), any -> EnumSet.noneOf(Family.class)).add(Family.of(entry.getKey()))));
      Map<String, String> exclusions = new TreeMap<>();
      root.path("exclusions").fields().forEachRemaining(entry -> {
        if (entry.getValue().asText().isBlank()) throw new IllegalArgumentException("No reason given for excluding " + entry.getKey());
        exclusions.put(entry.getKey(), entry.getValue().asText());
      });
      if (families.values().stream().anyMatch(f -> f.contains(Family.SCHEMA_TEXT)))
        throw new IllegalArgumentException("schemaText is allowed everywhere and declared nowhere");
      // A definition this library refuses on purpose is pinned once, beside the fixtures it left behind.
      CorpusFixtureVerifier.expectedRejections().forEach((source, rejection) -> exclusions.put(caseId(source),
        "refused by the JSON reader, as corpus-expected-rejections.json pins: " + rejection.reason()));
      return new Declarations(families, exclusions);
    }
  }

  public static void main(String[] args) throws IOException
  {
    if (args.length != 1) {
      System.err.println("Usage: CorpusYamlTripVerifier <path to cedar-test-artifacts>");
      System.exit(2);
    }
    Path corpus = Path.of(args[0]);
    if (!Files.isDirectory(corpus.resolve("artifacts"))) {
      System.err.println("Not a corpus: " + corpus.resolve("artifacts").toAbsolutePath());
      System.exit(2);
    }

    var result = verify(corpus, declarations());
    Map<Family, Integer> counts = new TreeMap<>();
    result.shown().values().forEach(families -> families.forEach(family -> counts.merge(family, 1, Integer::sum)));
    System.out.printf("cases compared through YAML: %d   excluded: %d   showing a known family: %s   problems: %d%n",
      result.compared(), result.excluded(), counts, result.problems().size());
    if (!result.successful()) {
      System.out.println("\nThese cases differ from what corpus-yaml-trip.json declares:");
      result.problems().forEach(problem -> System.out.println("  " + problem));
      System.exit(1);
    }
  }

  static VerificationResult verify(Path corpus, Declarations declared) throws IOException
  {
    Path artifacts = corpus.resolve("artifacts");
    Map<String, String> templates = templateSources(artifacts);
    List<TripCase> cases = new ArrayList<>();
    caseFiles(artifacts.resolve("templates"), "template").forEach((id, file) -> cases.add(schemaCase(id, file, Kind.TEMPLATE)));
    caseFiles(artifacts.resolve("elements"), "element").forEach((id, file) -> cases.add(schemaCase(id, file, Kind.ELEMENT)));
    caseFiles(artifacts.resolve("fields"), "field").forEach((id, file) -> cases.add(schemaCase(id, file, Kind.FIELD)));
    caseFiles(artifacts.resolve("instances"), "instance").forEach((id, file) -> cases.add(instanceCase(id, file, null, templates)));
    caseFiles(artifacts.resolve("cee-suite"), "template").forEach((id, file) -> cases.add(schemaCase("cee-" + id, file, Kind.TEMPLATE)));
    caseFiles(artifacts.resolve("cee-suite"), "instance").forEach((id, file) -> cases.add(instanceCase("cee-" + id, file,
      file.resolveSibling(file.getFileName().toString().replace("instance-", "template-")), templates)));

    List<String> problems = new ArrayList<>();
    Set<String> ids = new HashSet<>();
    cases.forEach(c -> ids.add(c.id()));
    declared.families().keySet().stream().filter(id -> !ids.contains(id) || declared.exclusions().containsKey(id))
      .forEach(id -> problems.add(id + ": declared families for a case that is not compared"));
    declared.exclusions().keySet().stream().filter(id -> !ids.contains(id))
      .forEach(id -> problems.add(id + ": excluded, but the corpus holds no such case"));

    int compared = 0, excluded = 0;
    Map<String, Set<Family>> shown = new TreeMap<>();
    for (TripCase c : cases) {
      List<Difference> differences;
      try {
        differences = c.trip().get();
      } catch (RuntimeException e) {
        if (declared.exclusions().containsKey(c.id())) excluded++;
        else problems.add(c.id() + ": cannot be compared (" + e.getMessage() + "); fix it or declare its exclusion");
        continue;
      }
      if (declared.exclusions().containsKey(c.id())) {
        problems.add(c.id() + ": can be compared now; remove its exclusion and declare the families it shows");
        continue;
      }
      compared++;
      List<Difference> unexplained = differences.stream().filter(d -> d.family().isEmpty()).toList();
      unexplained.stream().limit(SHOWN_DIFFERENCES).forEach(d -> problems.add(c.id() + ": unexplained difference at " + d));
      if (unexplained.size() > SHOWN_DIFFERENCES)
        problems.add(c.id() + ": " + (unexplained.size() - SHOWN_DIFFERENCES) + " more unexplained differences");
      Set<Family> families = EnumSet.noneOf(Family.class);
      differences.stream().map(Difference::family).flatMap(Optional::stream).filter(f -> f != Family.SCHEMA_TEXT).forEach(families::add);
      Set<Family> expected = declared.families().getOrDefault(c.id(), Set.of());
      if (!families.equals(expected)) {
        problems.add(c.id() + ": shows " + keys(families) + " where corpus-yaml-trip.json declares " + keys(expected));
        for (Family undeclared : families) {
          if (expected.contains(undeclared)) continue;
          differences.stream().filter(d -> d.family().equals(Optional.of(undeclared))).limit(EXAMPLES)
            .forEach(d -> problems.add(c.id() + ":   " + undeclared.key() + " at " + d));
        }
      }
      if (!families.isEmpty()) shown.put(c.id(), families);
    }
    return new VerificationResult(compared, excluded, shown, problems);
  }

  private enum Kind { TEMPLATE, ELEMENT, FIELD }

  private static TripCase schemaCase(String id, Path file, Kind kind)
  {
    return new TripCase(id, () -> {
      ObjectNode source = json(file);
      var jsonReader = new JsonArtifactReader();
      var yamlReader = new YamlArtifactReader(false);
      return switch (kind) {
        case TEMPLATE -> {
          TemplateSchemaArtifact artifact = jsonReader.readTemplateSchemaArtifact(source);
          TemplateSchemaArtifact back = yamlReader.readTemplateSchemaArtifact(yaml(YamlSerializer.getYAML(artifact, false, true)));
          yield YamlTripComparison.compareSchemas(RENDERER.renderTemplateSchemaArtifact(artifact), RENDERER.renderTemplateSchemaArtifact(back));
        }
        case ELEMENT -> {
          ElementSchemaArtifact artifact = jsonReader.readElementSchemaArtifact(source);
          ElementSchemaArtifact back = yamlReader.readElementSchemaArtifact(yaml(YamlSerializer.getYAML(artifact, false, true)));
          yield YamlTripComparison.compareSchemas(RENDERER.renderElementSchemaArtifact(artifact), RENDERER.renderElementSchemaArtifact(back));
        }
        case FIELD -> {
          FieldSchemaArtifact artifact = jsonReader.readFieldSchemaArtifact(source);
          FieldSchemaArtifact back = yamlReader.readFieldSchemaArtifact(yaml(YamlSerializer.getYAML(artifact, false, true)));
          yield YamlTripComparison.compareSchemas(RENDERER.renderFieldSchemaArtifact(artifact), RENDERER.renderFieldSchemaArtifact(back));
        }
      };
    });
  }

  // Most CEE suite instances are CEE's output before a host stores it: data alone, with no name and no
  // template named, and CEE pairs each with the template in its own case. This reader requires both, so
  // such an instance is given them, as a host gives them before storing it.
  private static TripCase instanceCase(String id, Path file, Path ownTemplate, Map<String, String> templates)
  {
    return new TripCase(id, () -> {
      ObjectNode source = json(file);
      String templateSource = source.has("schema:isBasedOn") ? templates.get(source.get("schema:isBasedOn").asText())
        : ownTemplate != null && Files.isRegularFile(ownTemplate) ? read(ownTemplate) : null;
      if (templateSource == null) throw new IllegalStateException("its template is in no corpus case");
      ObjectNode templateNode = json(templateSource);
      if (!source.has("schema:isBasedOn")) {
        source.set("schema:isBasedOn", templateNode.get("@id"));
        source.put("schema:name", id);
      }
      var jsonReader = new JsonArtifactReader();
      TemplateSchemaArtifact template = jsonReader.readTemplateSchemaArtifact(templateNode);
      TemplateInstanceArtifact instance = jsonReader.readTemplateInstanceArtifact(source);
      TemplateInstanceArtifact sparse = new YamlArtifactReader(false)
        .readTemplateInstanceArtifact(yaml(YamlSerializer.getYAML(instance, false, true)));
      return YamlTripComparison.compareInstances(RENDERER.renderTemplateInstanceArtifact(instance),
        RENDERER.renderTemplateInstanceArtifact(template, sparse));
    });
  }

  // An instance names its template by identifier, and that template may sit in any case of the corpus.
  private static Map<String, String> templateSources(Path artifacts) throws IOException
  {
    Map<String, String> sources = new HashMap<>();
    for (String directory : List.of("templates", "cee-suite", "instances")) {
      for (Path file : caseFiles(artifacts.resolve(directory), "template").values()) {
        String text = read(file);
        try {
          JsonNode id = new ObjectMapper().readTree(text).get("@id");
          if (id != null && id.isTextual()) sources.putIfAbsent(id.asText(), text);
        } catch (IOException malformed) {
          // A malformed source names no template. The case that holds it reports the failure.
        }
      }
    }
    return sources;
  }

  /** The case files of one kind in a corpus directory, as {@code <stem>-<number>} to path, in order. */
  private static Map<String, Path> caseFiles(Path directory, String stem) throws IOException
  {
    Map<String, Path> files = new LinkedHashMap<>();
    if (!Files.isDirectory(directory)) return files;
    try (var cases = Files.list(directory)) {
      for (Path caseDirectory : cases.filter(Files::isDirectory).sorted().toList()) {
        String id = stem + "-" + caseDirectory.getFileName();
        Path file = caseDirectory.resolve(id + ".json");
        if (Files.isRegularFile(file)) files.put(id, file);
      }
    }
    return files;
  }

  private static String read(Path file)
  {
    try {
      return Files.readString(file);
    } catch (IOException e) {
      throw new IllegalStateException("unreadable: " + file, e);
    }
  }

  private static ObjectNode json(Path file) { return json(read(file)); }

  private static ObjectNode json(String text)
  {
    try {
      return ArtifactSourceParser.json(text);
    } catch (IOException e) {
      throw new IllegalArgumentException("not JSON: " + e.getMessage(), e);
    }
  }

  private static LinkedHashMap<String, Object> yaml(String text)
  {
    try {
      return ArtifactSourceParser.yaml(text);
    } catch (IOException e) {
      throw new IllegalArgumentException("its own YAML does not parse: " + e.getMessage(), e);
    }
  }

  /** {@code fields/001/field-001.json} is the case {@code field-001}; a CEE suite case is prefixed {@code cee-}. */
  static String caseId(String source)
  {
    String name = Path.of(source).getFileName().toString().replaceFirst("\\.json$", "");
    return source.startsWith("cee-suite/") ? "cee-" + name : name;
  }

  private static List<String> keys(Set<Family> families)
  {
    return families.stream().map(Family::key).sorted().toList();
  }
}
