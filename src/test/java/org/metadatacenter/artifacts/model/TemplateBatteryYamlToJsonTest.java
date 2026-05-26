package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.model.validation.CedarValidator;
import org.metadatacenter.model.validation.ModelValidator;
import org.metadatacenter.model.validation.report.ErrorItem;
import org.metadatacenter.model.validation.report.ValidationReport;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Real-world battery: parses every {@code *.yaml} fixture under
 * {@code src/test/resources/templates-yaml/} via {@link YamlArtifactReader}, renders
 * the result back to JSON via {@link JsonArtifactRenderer}, and confirms the
 * rendered JSON Schema is accepted by {@link CedarValidator}.
 *
 * <p>The fixtures are canonical YAML derived from real-world CEDAR templates by
 * round-tripping the paired authoritative JSON Schemas through this library's own
 * reader and renderer (see {@link GoldenYamlGenerator}). The current fixture set
 * is sourced from the HuBMAP template library (44 templates spanning 10X Multiome,
 * ATAC-seq, RNA-seq, Visium, Xenium, MERFISH, CODEX, CyTOF, LC-MS, MIBI, ...) and
 * exercises:
 *
 * <ul>
 *   <li>All commonly-used field types: text, numeric, temporal, controlled-term,
 *       radio, single-select-list, multi-select-list, email, link, attribute-value.</li>
 *   <li>Controlled-term constraints (the central CEDAR integration story).</li>
 *   <li>Nested elements (template &rarr; element &rarr; field).</li>
 *   <li>Required-field configurations, default values, {@code prefLabel} carriers,
 *       multi-line description blocks.</li>
 * </ul>
 *
 * <p>Templates from other sources can be added to the same fixtures directory; the
 * battery picks them up automatically. Failures are reported per template name, so
 * a regression in one template is identifiable without re-running the rest.
 *
 * @see GoldenYamlGenerator
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TemplateBatteryYamlToJsonTest
{
  private static final String FIXTURES_RESOURCE = "templates-yaml";

  private YamlArtifactReader yamlReader;
  private JsonArtifactRenderer jsonRenderer;
  private ModelValidator cedarValidator;
  private ObjectMapper mapper;

  private final List<String> passes = new ArrayList<>();
  private final List<String> failures = new ArrayList<>();

  @BeforeEach public void setUp()
  {
    yamlReader = new YamlArtifactReader();
    jsonRenderer = new JsonArtifactRenderer();
    cedarValidator = new CedarValidator();
    mapper = new ObjectMapper();
  }

  @BeforeAll public void announceBattery()
  {
    System.out.println("[TemplateBatteryYamlToJsonTest] running against "
      + "src/test/resources/" + FIXTURES_RESOURCE + "/");
  }

  @AfterAll public void printSummary()
  {
    int total = passes.size() + failures.size();
    System.out.println("[TemplateBatteryYamlToJsonTest] summary: "
      + passes.size() + "/" + total + " templates compiled and validated");
    if (!failures.isEmpty()) {
      System.out.println("[TemplateBatteryYamlToJsonTest] failures:");
      for (String f : failures) System.out.println("  - " + f);
    }
  }

  @ParameterizedTest(name = "{0}") @MethodSource("templates")
  public void roundTripTemplateThroughYamlReaderAndJsonRenderer(String displayName, Path yamlFile)
    throws Exception
  {
    String yamlText = Files.readString(yamlFile);

    LinkedHashMap<String, Object> yamlMap;
    try {
      @SuppressWarnings("unchecked") LinkedHashMap<String, Object> parsed =
        (LinkedHashMap<String, Object>) new Yaml().load(yamlText);
      yamlMap = parsed;
    } catch (Exception e) {
      failures.add(displayName + " — YAML parse failed: " + e.getMessage());
      throw e;
    }

    TemplateSchemaArtifact template;
    try {
      template = yamlReader.readTemplateSchemaArtifact(yamlMap);
    } catch (Exception e) {
      failures.add(displayName + " — YamlArtifactReader rejected: " + e.getMessage());
      throw e;
    }

    ObjectNode rendered = jsonRenderer.renderTemplateSchemaArtifact(template);

    ValidationReport report = cedarValidator.validateTemplate(rendered);
    if (!"true".equals(report.getValidationStatus())) {
      StringBuilder msg = new StringBuilder(
        "CedarValidator rejected the rendered JSON Schema for " + yamlFile.getFileName() + ":\n");
      for (ErrorItem err : report.getErrors()) msg.append("  - ").append(err).append('\n');
      failures.add(displayName + " — CedarValidator rejected rendered output");
      fail(msg.toString());
    }

    assertEquals(template.name(), rendered.path("schema:name").asText(),
      "rendered schema:name must match the parsed model");

    passes.add(displayName);
  }

  // ------------------------------------------------------------------
  // argument source: every *.yaml in the fixtures resource folder
  // ------------------------------------------------------------------

  public Stream<Arguments> templates() throws Exception
  {
    URL resource = getClass().getClassLoader().getResource(FIXTURES_RESOURCE);
    assertNotNull(resource,
      "fixtures folder not on classpath: src/test/resources/" + FIXTURES_RESOURCE);
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
}
