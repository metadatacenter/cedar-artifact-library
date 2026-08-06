package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives {@link ArtifactConvertor#main} over the four combinations of its input and output
 * format options, and asserts that a JSON artifact taken out to YAML and back through the
 * command line is unchanged.
 */
public class ArtifactConvertorTest
{
  private static final String TEMPLATE_FIXTURE = "templates-json/SimpleTemplateWithType.json";

  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Test public void testJsonToYaml(@TempDir Path tempDir) throws IOException
  {
    Path yamlFile = tempDir.resolve("template.yaml");

    ArtifactConvertor.main(new String[] { "-jif", "-yof", "-tsf", fixturePath(), "-f", yamlFile.toString() });

    assertTrue(Files.exists(yamlFile));
    assertTrue(Files.readString(yamlFile).contains("type: template"));
  }

  @Test public void testYamlToJson(@TempDir Path tempDir) throws IOException
  {
    Path yamlFile = tempDir.resolve("template.yaml");
    Path jsonFile = tempDir.resolve("template.json");

    ArtifactConvertor.main(new String[] { "-jif", "-yof", "-tsf", fixturePath(), "-f", yamlFile.toString() });
    ArtifactConvertor.main(new String[] { "-yif", "-jof", "-tsf", yamlFile.toString(), "-f", jsonFile.toString() });

    JsonNode regenerated = jsonMapper.readTree(jsonFile.toFile());

    assertEquals("https://schema.metadatacenter.org/core/Template", regenerated.get("@type").asText());
  }

  @Test public void testJsonToJson(@TempDir Path tempDir) throws IOException
  {
    Path jsonFile = tempDir.resolve("template.json");

    ArtifactConvertor.main(new String[] { "-jif", "-jof", "-tsf", fixturePath(), "-f", jsonFile.toString() });

    assertEquals(jsonMapper.readTree(Paths.get(fixturePath()).toFile()), jsonMapper.readTree(jsonFile.toFile()));
  }

  @Test public void testYamlToYaml(@TempDir Path tempDir) throws IOException
  {
    Path yamlFile = tempDir.resolve("template.yaml");
    Path regeneratedYamlFile = tempDir.resolve("regenerated.yaml");

    ArtifactConvertor.main(new String[] { "-jif", "-yof", "-tsf", fixturePath(), "-f", yamlFile.toString() });
    ArtifactConvertor.main(
      new String[] { "-yif", "-yof", "-tsf", yamlFile.toString(), "-f", regeneratedYamlFile.toString() });

    assertEquals(Files.readString(yamlFile), Files.readString(regeneratedYamlFile));
  }

  /**
   * The JSON fixtures under {@code templates-json/} are canonical library output, so a
   * JSON to YAML to JSON trip through the command line must reproduce the fixture exactly.
   */
  @Test public void testJsonSurvivesRoundTripThroughYaml(@TempDir Path tempDir) throws IOException
  {
    Path yamlFile = tempDir.resolve("template.yaml");
    Path jsonFile = tempDir.resolve("template.json");

    ArtifactConvertor.main(new String[] { "-jif", "-yof", "-tsf", fixturePath(), "-f", yamlFile.toString() });
    ArtifactConvertor.main(new String[] { "-yif", "-jof", "-tsf", yamlFile.toString(), "-f", jsonFile.toString() });

    assertEquals(jsonMapper.readTree(Paths.get(fixturePath()).toFile()), jsonMapper.readTree(jsonFile.toFile()));
  }

  /**
   * Compact YAML omits the model version, so reading it back requires the same {@code -cy}
   * that produced it.
   */
  @Test public void testCompactYamlRoundTrip(@TempDir Path tempDir) throws IOException
  {
    Path yamlFile = tempDir.resolve("template-compact.yaml");
    Path regeneratedYamlFile = tempDir.resolve("regenerated-compact.yaml");

    ArtifactConvertor.main(new String[] { "-jif", "-yof", "-cy", "-tsf", fixturePath(), "-f", yamlFile.toString() });
    ArtifactConvertor.main(
      new String[] { "-yif", "-yof", "-cy", "-tsf", yamlFile.toString(), "-f", regeneratedYamlFile.toString() });

    assertEquals(Files.readString(yamlFile), Files.readString(regeneratedYamlFile));
  }

  private String fixturePath()
  {
    URL fixtureUrl = getClass().getClassLoader().getResource(TEMPLATE_FIXTURE);
    assertNotNull(fixtureUrl, "Missing test fixture " + TEMPLATE_FIXTURE);

    try {
      return Paths.get(fixtureUrl.toURI()).toString();
    } catch (Exception e) {
      throw new RuntimeException("Could not resolve test fixture " + TEMPLATE_FIXTURE, e);
    }
  }
}
