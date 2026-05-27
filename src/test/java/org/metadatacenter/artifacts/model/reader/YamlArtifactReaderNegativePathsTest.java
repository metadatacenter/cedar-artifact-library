package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.MODEL_VERSION;

/**
 * Negative-path tests for YamlArtifactReader: hand-authored YAML is common, so malformed
 * documents must surface useful error messages rather than producing wrong artifacts.
 */
public class YamlArtifactReaderNegativePathsTest
{
  private YamlArtifactReader reader;

  @BeforeEach public void setup()
  {
    reader = new YamlArtifactReader();
  }

  private LinkedHashMap<String, Object> minimalTemplate()
  {
    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "T");
    // YamlArtifactReader requires modelVersion to pass its top-level shape check.
    node.put("modelVersion", MODEL_VERSION);
    return node;
  }

  @Test public void testReadChildSchemasRejectsMapInsteadOfList()
  {
    // `children:` must be a LIST of child maps; a top-level Map under `children:` is illegal.
    LinkedHashMap<String, Object> node = minimalTemplate();
    LinkedHashMap<String, Object> kids = new LinkedHashMap<>();
    kids.put("foo", new LinkedHashMap<>());
    node.put("children", kids);

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().toLowerCase().contains("list"));
  }

  @Test public void testReadChildSchemasRejectsNonMapEntryInList()
  {
    LinkedHashMap<String, Object> node = minimalTemplate();
    node.put("children", new ArrayList<>(Arrays.asList("a string, not a map")));

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().toLowerCase().contains("map"));
  }

  @Test public void testReadChildSchemasRejectsUnknownChildType()
  {
    LinkedHashMap<String, Object> child = new LinkedHashMap<>();
    child.put("key", "x");
    child.put("type", "not-a-real-type");
    child.put("name", "X");

    LinkedHashMap<String, Object> node = minimalTemplate();
    node.put("children", new ArrayList<>(Arrays.asList(child)));

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("not-a-real-type"),
      "Expected exception to mention the unknown type. Got: " + ex.getMessage());
  }

  @Test public void testReadTemplateRejectsWrongTopLevelType()
  {
    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "element");
    node.put("name", "X");
    node.put("modelVersion", MODEL_VERSION);

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("template"));
  }

  @Test public void testReadVersionRejectsInvalidString()
  {
    // An invalid `version:` must surface a parse error, not silently coerce.
    LinkedHashMap<String, Object> field = new LinkedHashMap<>();
    field.put("type", "text-field");
    field.put("name", "X");
    field.put("modelVersion", MODEL_VERSION);
    field.put("version", "garbage");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(field));
    assertTrue(ex.getMessage().contains("garbage"));
  }

  @Test public void testCompactReaderDefaultsAbsentModelVersion()
  {
    // The compact-mode reader fills in modelVersion when the input omits it — that's the
    // round-trip story for YamlArtifactRenderer's compact output, which drops modelVersion.
    YamlArtifactReader compactReader = new YamlArtifactReader(true);

    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "T");
    // intentionally no modelVersion

    // Should not throw.
    compactReader.readTemplateSchemaArtifact(node);
  }

  @Test public void testCompactReaderStillRejectsWrongModelVersion()
  {
    // Defaulting is only for absent keys; a present-but-wrong modelVersion is still a
    // hard error, otherwise the LLM could silently feed stale-version YAML and have it
    // accepted against the wrong schema.
    YamlArtifactReader compactReader = new YamlArtifactReader(true);

    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "T");
    node.put("modelVersion", "0.0.1");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> compactReader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().toLowerCase().contains("model version"),
      "expected message to mention model version; got: " + ex.getMessage());
  }

  @Test public void testStrictReaderStillRequiresModelVersion()
  {
    // Default reader (isCompact=false) must keep the strict contract: missing modelVersion
    // throws. This is the behavior the artifact-library has shipped with; the compact flag
    // is opt-in.
    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "T");

    assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
  }
}
