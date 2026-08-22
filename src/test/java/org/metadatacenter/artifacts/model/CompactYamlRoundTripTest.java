package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The compact YAML form is an identity-free structural description of an artifact. It leaves out
 * repository-assigned identifiers at every schema-artifact depth, together with the model version,
 * version, status and provenance. A reader can still recover that structural description, which this
 * test asserts over the same template battery the full-form round trip uses.
 *
 * <p>The round trip is
 *
 * <pre>{@code
 *   fullYamlMap
 *     -> YamlArtifactReader                    -> TemplateSchemaArtifact
 *     -> YamlArtifactRenderer(compact)         -> compactYamlMap
 *     -> YamlArtifactReader(isCompact = true)  -> TemplateSchemaArtifact
 *     -> YamlArtifactRenderer(compact)         -> regeneratedCompactYamlMap
 * }</pre>
 *
 * and the two compact maps must be structurally equal. Reading compact input needs the compact
 * reader: the ordinary one rejects it over the absent model version, which is what the CLI's
 * {@code -cy} flag selects on the way in as well as on the way out.
 *
 * <p>Sister test: {@link TemplateBatteryYamlRoundTripTest}, which closes the same loop through the
 * full form and the JSON pair.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompactYamlRoundTripTest
{
  private static final String FIXTURES_RESOURCE = "templates-yaml";

  /** What the compact form drops, and what must therefore not appear in it. */
  private static final List<String> SYSTEM_RECORDED_KEYS =
    List.of("modelVersion", "version", "status", "createdOn", "createdBy", "modifiedOn", "modifiedBy");

  private YamlArtifactReader fullReader;
  private YamlArtifactReader compactReader;
  private YamlArtifactRenderer compactRenderer;

  @BeforeEach public void setUp()
  {
    fullReader = new YamlArtifactReader();
    compactReader = new YamlArtifactReader(true);
    compactRenderer = new YamlArtifactRenderer(true);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("templates")
  public void compactFormReadsBackAsTheSameArtifact(String displayName, Path yamlFile) throws Exception
  {
    TemplateSchemaArtifact fromFull = fullReader.readTemplateSchemaArtifact(parse(yamlFile));

    LinkedHashMap<String, Object> compact = compactRenderer.renderTemplateSchemaArtifact(fromFull);
    TemplateSchemaArtifact fromCompact = compactReader.readTemplateSchemaArtifact(compact);
    LinkedHashMap<String, Object> regenerated = compactRenderer.renderTemplateSchemaArtifact(fromCompact);

    assertEquals(compact, regenerated,
      "the compact form did not read back as the artifact it was written from: " + displayName);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("templates")
  public void compactFormDropsTheDocumentIdentifierAndWhatTheSystemRecords(String displayName, Path yamlFile)
    throws Exception
  {
    TemplateSchemaArtifact artifact = fullReader.readTemplateSchemaArtifact(parse(yamlFile));

    LinkedHashMap<String, Object> compact = compactRenderer.renderTemplateSchemaArtifact(artifact);

    // The compact form describes an artifact being authored rather than one already stored, so it does
    // not name the artifact it describes: a repository assigns that identifier on save, along with the
    // provenance. Keeping it would let a conversion look like an edit of the stored artifact while
    // giving that artifact's children freshly derived property IRIs.
    assertFalse(compact.containsKey("id"),
      "the compact form names the artifact it describes: " + displayName);

    for (String key : SYSTEM_RECORDED_KEYS) {
      assertFalse(compact.containsKey(key),
        "the compact form carries " + key + ", which the system records: " + displayName);
    }
  }

  @ParameterizedTest(name = "{0}") @MethodSource("templates")
  public void compactFormDropsTheIdentifiersOfChildren(String displayName, Path yamlFile) throws Exception
  {
    TemplateSchemaArtifact artifact = fullReader.readTemplateSchemaArtifact(parse(yamlFile));

    LinkedHashMap<String, Object> compact = compactRenderer.renderTemplateSchemaArtifact(artifact);

    assertTrue(childIdentifiers(compact).stream().allMatch(identifier -> identifier == null),
      "the compact form carries a child's repository identifier: " + displayName);
  }

  @SuppressWarnings("unchecked")
  private static List<Object> childIdentifiers(LinkedHashMap<String, Object> rendering)
  {
    List<Object> identifiers = new ArrayList<>();
    Object children = rendering.get("children");
    if (children instanceof List<?> childList) {
      for (Object child : childList) {
        if (child instanceof LinkedHashMap<?, ?> childMap) {
          identifiers.add(childMap.get("id"));
          identifiers.addAll(childIdentifiers((LinkedHashMap<String, Object>) childMap));
        }
      }
    }
    return identifiers;
  }

  @Test public void theOrdinaryReaderRejectsCompactInput() throws Exception
  {
    Path first = templates().findFirst().map(arguments -> (Path) arguments.get()[1]).orElseThrow();
    LinkedHashMap<String, Object> compact =
      compactRenderer.renderTemplateSchemaArtifact(fullReader.readTemplateSchemaArtifact(parse(first)));

    Exception thrown = org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
      () -> fullReader.readTemplateSchemaArtifact(compact),
      "the ordinary reader accepted compact input, so the compact reader is not what distinguishes them");
    assertTrue(thrown.getMessage().contains("modelVersion"),
      "expected the absent model version to be what the ordinary reader rejects, got: " + thrown.getMessage());
  }

  @Test public void theCompactReaderRefusesADocumentThatNamesItsArtifact() throws Exception
  {
    Path first = templates().findFirst().map(arguments -> (Path) arguments.get()[1]).orElseThrow();
    LinkedHashMap<String, Object> compact =
      compactRenderer.renderTemplateSchemaArtifact(fullReader.readTemplateSchemaArtifact(parse(first)));
    compact.put("id", "https://repo.metadatacenter.org/templates/written-by-hand");

    // Ignoring it would leave an author believing the document still refers to that stored template,
    // while a conversion gave its children freshly derived property IRIs.
    Exception thrown = org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
      () -> compactReader.readTemplateSchemaArtifact(compact),
      "the compact reader accepted a document naming the artifact it describes");
    assertTrue(thrown.getMessage().contains("id"),
      "expected the identifier to be what the compact reader rejects, got: " + thrown.getMessage());
  }

  @SuppressWarnings("unchecked") private LinkedHashMap<String, Object> parse(Path yamlFile) throws Exception
  {
    LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) new Yaml().load(Files.readString(yamlFile));
    assertNotNull(map, "fixture parsed to null: " + yamlFile);
    return map;
  }

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
        args.add(Arguments.of(stem.substring(0, stem.length() - ".yaml".length()), yaml));
      }
    }
    args.sort((a, b) -> ((String) a.get()[0]).compareTo((String) b.get()[0]));
    return args.stream();
  }
}
