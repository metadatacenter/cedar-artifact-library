package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Group D — YAML reader negative paths. The reader has well-defined throws at boundaries
 * (children must be a list, child entries must be maps, types must be known) that no test
 * exercised. Hand-authored YAML is common so these messages have to surface useful errors.
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
    // YamlArtifactReader requires `modelVersion: 1.6.0` to pass its top-level shape check.
    node.put("modelVersion", "1.6.0");
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
    node.put("modelVersion", "1.6.0");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("template"));
  }

  @Test public void testReadVersionRejectsInvalidString()
  {
    // Mirrors the JSON readVersion contract (PR #43) on the YAML side. A field with an
    // invalid `version:` must surface a parse error, not silently coerce.
    LinkedHashMap<String, Object> field = new LinkedHashMap<>();
    field.put("type", "text-field");
    field.put("name", "X");
    field.put("modelVersion", "1.6.0");
    field.put("version", "garbage");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(field));
    assertTrue(ex.getMessage().contains("garbage"));
  }
}
