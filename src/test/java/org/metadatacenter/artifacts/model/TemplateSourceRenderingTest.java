package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.SourceRenderingComparison.Difference;
import org.metadatacenter.artifacts.model.SourceRenderingComparison.Family;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds the template battery to its sources.
 *
 * <p>The battery's goldens under {@code templates-json} and {@code templates-yaml} are this library's own
 * output, so the battery compares the library with itself: a key the pipeline drops is absent from both
 * sides. For each golden whose source sits under {@code templates}, two things are checked here. First, the
 * source written as JSON differs from the source only in the normalizations the writer intends, and
 * {@code /concordance/template-source-rendering.json} declares which sources show each one. A source that
 * shows an undeclared normalization fails, as does one that stops showing a declared one. Second, each
 * golden is exactly what its generator writes from its source today, so a golden cannot drift from the
 * source it stands for.
 *
 * <p>The fourteen goldens without a source in this repository are held only by the battery.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateSourceRenderingTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Map<String, Set<Family>> declared = declarations();

  private static Map<String, Set<Family>> declarations()
  {
    try (InputStream input = TemplateSourceRenderingTest.class.getResourceAsStream("/concordance/template-source-rendering.json")) {
      Map<String, Set<Family>> families = new TreeMap<>();
      MAPPER.readTree(input).path("families").fields().forEachRemaining(entry -> entry.getValue().forEach(name ->
        families.computeIfAbsent(name.asText(), any -> EnumSet.noneOf(Family.class)).add(Family.of(entry.getKey()))));
      return families;
    } catch (IOException e) {
      throw new IllegalStateException("unreadable template-source-rendering.json", e);
    }
  }

  private static Path resource(String directory)
  {
    try {
      return Paths.get(TemplateSourceRenderingTest.class.getClassLoader().getResource(directory).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  /** The goldens whose source is in {@code templates}, by file stem. */
  public Stream<String> sourced() throws IOException
  {
    try (var goldens = Files.list(resource("templates-json"))) {
      return goldens.map(golden -> golden.getFileName().toString().replaceFirst("\\.json$", ""))
        .filter(stem -> Files.isRegularFile(resource("templates").resolve(stem + ".json"))).sorted().toList().stream();
    }
  }

  private static ObjectNode source(String stem) throws IOException
  {
    return (ObjectNode) MAPPER.readTree(resource("templates").resolve(stem + ".json").toFile());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sourced")
  public void sourceRendersWithOnlyItsDeclaredNormalizations(String stem) throws Exception
  {
    ObjectNode source = source(stem);
    JsonNode rendering = new JsonArtifactRenderer().renderTemplateSchemaArtifact(
      new JsonArtifactReader().readTemplateSchemaArtifact(source.deepCopy()));
    List<Difference> differences = SourceRenderingComparison.compare(source, MAPPER.readTree(rendering.toString()));

    List<Difference> unexplained = differences.stream().filter(d -> d.family().isEmpty()).toList();
    assertTrue(unexplained.isEmpty(), stem + " loses or changes what no normalization explains:\n  "
      + unexplained.stream().limit(10).map(Difference::toString).collect(Collectors.joining("\n  ")));

    Set<Family> shown = EnumSet.noneOf(Family.class);
    differences.stream().map(Difference::family).flatMap(Optional::stream).filter(f -> f != Family.SCHEMA_TEXT)
      .forEach(shown::add);
    assertEquals(keys(declared.getOrDefault(stem, Set.of())), keys(shown),
      stem + " shows other normalizations than template-source-rendering.json declares");
  }

  @ParameterizedTest(name = "{0}") @MethodSource("sourced")
  public void goldensAreWhatTheGeneratorsWriteFromTheSource(String stem) throws Exception
  {
    ObjectNode source = source(stem);
    assertEquals(GoldenJsonGenerator.golden(source.deepCopy()).trim(),
      Files.readString(resource("templates-json").resolve(stem + ".json")).trim(),
      "templates-json/" + stem + ".json is stale; regenerate it with GoldenJsonGenerator");
    assertEquals(GoldenYamlGenerator.golden(source.deepCopy()).trim(),
      Files.readString(resource("templates-yaml").resolve(stem + ".yaml")).trim(),
      "templates-yaml/" + stem + ".yaml is stale; regenerate it with GoldenYamlGenerator");
  }

  @Test public void declarationsNameOnlySourcedGoldensAndNeverTheSchemaText() throws Exception
  {
    Set<String> stems = sourced().collect(Collectors.toSet());
    declared.keySet().forEach(stem -> assertTrue(stems.contains(stem), "declared for no sourced golden: " + stem));
    declared.values().forEach(families -> assertTrue(!families.contains(Family.SCHEMA_TEXT),
      "schemaText is allowed everywhere and declared nowhere"));
  }

  private static List<String> keys(Set<Family> families)
  {
    return families.stream().map(Family::key).sorted().toList();
  }
}
