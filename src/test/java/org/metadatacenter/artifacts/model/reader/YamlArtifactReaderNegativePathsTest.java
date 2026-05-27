package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

  @Test public void testCompactReaderUsesPresentCorrectModelVersion()
  {
    // Defaulting is only for absence; a present-and-correct modelVersion goes through the
    // same parsing path as the strict reader. This pins the spec so a future "be lenient
    // about wrong values too" regression would fail loudly.
    YamlArtifactReader compactReader = new YamlArtifactReader(true);

    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "T");
    node.put("modelVersion", MODEL_VERSION);

    // No throw; the artifact builds.
    TemplateSchemaArtifact template = compactReader.readTemplateSchemaArtifact(node);
    assertEquals("T", template.name());
  }

  @Test public void testCompactReaderReadsPresentNonCompactFields()
  {
    // The compact flag is about tolerance for *absence* of compact-form-omitted fields,
    // not about ignoring them when present. When standard-form YAML is fed to the
    // compact reader, every populated field still flows through to the artifact.
    YamlArtifactReader compactReader = new YamlArtifactReader(true);

    URI jsonLdId = URI.create("https://repo.metadatacenter.org/templates/77");
    URI createdBy = URI.create("https://repo.metadatacenter.org/users/1");
    URI modifiedBy = URI.create("https://repo.metadatacenter.org/users/2");
    OffsetDateTime createdOn = OffsetDateTime.parse("2026-01-01T00:00:00Z");
    OffsetDateTime lastUpdatedOn = OffsetDateTime.parse("2026-02-01T00:00:00Z");

    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "Full");
    node.put("id", jsonLdId.toString());
    node.put("modelVersion", MODEL_VERSION);
    node.put("version", "1.2.3");
    node.put("status", "draft");
    node.put("createdBy", createdBy.toString());
    node.put("modifiedBy", modifiedBy.toString());
    node.put("createdOn", createdOn.toString());
    node.put("modifiedOn", lastUpdatedOn.toString());

    TemplateSchemaArtifact template = compactReader.readTemplateSchemaArtifact(node);

    assertEquals(jsonLdId, template.jsonLdId().get());
    assertEquals(Version.fromString("1.2.3"), template.version().get());
    assertEquals(Status.DRAFT, template.status().get());
    assertEquals(createdBy, template.createdBy().get());
    assertEquals(modifiedBy, template.modifiedBy().get());
    assertEquals(createdOn, template.createdOn().get());
    assertEquals(lastUpdatedOn, template.lastUpdatedOn().get());
  }

  @Test public void testCompactReaderAcceptsAllNonCompactFieldsAbsent()
  {
    // Companion to the previous test: when *every* compact-form-omitted field is missing,
    // the compact reader still happily produces the artifact, with the omitted fields
    // mapping to empty Optionals on the builder.
    YamlArtifactReader compactReader = new YamlArtifactReader(true);

    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "Bare");
    // No modelVersion, version, status, @id, or provenance.

    TemplateSchemaArtifact template = compactReader.readTemplateSchemaArtifact(node);

    assertEquals("Bare", template.name());
    assertTrue(template.jsonLdId().isEmpty());
    assertTrue(template.version().isEmpty());
    assertTrue(template.status().isEmpty());
    assertTrue(template.createdBy().isEmpty());
    assertTrue(template.modifiedBy().isEmpty());
    assertTrue(template.createdOn().isEmpty());
    assertTrue(template.lastUpdatedOn().isEmpty());
  }
}
