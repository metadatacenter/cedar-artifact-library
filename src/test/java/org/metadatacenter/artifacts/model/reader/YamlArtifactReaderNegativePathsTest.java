package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.VersionSpec;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    LinkedHashMap<String, Object> foo = new LinkedHashMap<>();
    foo.put("type", "text-field");  // non-empty: an empty {} would trip the empty-mapping reject first
    kids.put("foo", foo);
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

  @Test public void testReadElementRejectsWrongTopLevelType()
  {
    // The resource server picks the reader from the endpoint, so a template sent to the element
    // endpoint must be refused rather than read as an element.
    LinkedHashMap<String, Object> node = minimalTemplate();

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readElementSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("should be element"), ex.getMessage());
  }

  @Test public void testReadTemplateInstanceRejectsWrongTopLevelType()
  {
    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "element-instance");
    node.put("name", "X");
    node.put("isBasedOn", "https://repo.metadatacenter.org/templates/abc");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateInstanceArtifact(node));
    assertTrue(ex.getMessage().contains("should be instance"), ex.getMessage());
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
    // the compact reader still happily produces the artifact. Provenance fields map to empty
    // Optionals — except version and status, which default to 0.0.1 / draft (matching the
    // builder), so a reader-built and a builder-built artifact agree.
    YamlArtifactReader compactReader = new YamlArtifactReader(true);

    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "Bare");
    // No modelVersion, version, status, @id, or provenance.

    TemplateSchemaArtifact template = compactReader.readTemplateSchemaArtifact(node);

    assertEquals("Bare", template.name());
    assertTrue(template.jsonLdId().isEmpty());
    assertEquals(Version.DEFAULT, template.version().orElseThrow(),
        "version defaults to 0.0.1 when absent, matching the builder");
    assertEquals(Status.DRAFT, template.status().orElseThrow(),
        "status defaults to draft when absent, matching the builder");
    assertTrue(template.createdBy().isEmpty());
    assertTrue(template.modifiedBy().isEmpty());
    assertTrue(template.createdOn().isEmpty());
    assertTrue(template.lastUpdatedOn().isEmpty());
  }

  @Test public void anEmptyIdentifierIsRefusedRatherThanReadAsAbsent()
  {
    // What a document writes where an identifier has not been assigned. Reading it as though the key
    // were absent hides that from whoever wrote it; the value to write there is null.
    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "template");
    node.put("name", "Blank identity");
    node.put("id", "");
    node.put("modelVersion", MODEL_VERSION);

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> new YamlArtifactReader(true).readTemplateSchemaArtifact(node));
    assertTrue(thrown.getMessage().contains("empty string is not a URI"), thrown.getMessage());
  }

  @Test public void anEmptyIdentifierOnAnInstanceIsRefusedToo()
  {
    LinkedHashMap<String, Object> node = new LinkedHashMap<>();
    node.put("type", "instance");
    node.put("name", "Blank identity");
    node.put("id", "");
    node.put("isBasedOn", "https://repo.metadatacenter.org/templates/t1");

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> new YamlArtifactReader().readTemplateInstanceArtifact(node));
    assertTrue(thrown.getMessage().contains("empty string is not a URI"), thrown.getMessage());
  }

  @Test public void anEmptyVersionIdIsRefusedWithItsExistingMessage()
  {
    LinkedHashMap<String, Object> field = controlledTermFieldPinnedTo("");

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(field));
    assertEquals("Expecting non-empty string value", thrown.getParseErrorMessage());
  }

  @Test public void aBlankVersionIdIsRefusedAsAParseError()
  {
    // VersionSpec refuses a blank id too, but with no path; the reader says where the id is.
    LinkedHashMap<String, Object> field = controlledTermFieldPinnedTo("   ");

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(field));
    assertEquals("A version id must not be blank", thrown.getParseErrorMessage());
  }

  private static final String INSTANCE =
    "type: instance\nname: X\nisBasedOn: \"https://repo.metadatacenter.org/templates/abc\"\n";

  private ArtifactParseException refusalOf(String children)
  {
    return assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateInstanceArtifact(ArtifactSourceParser.yaml(INSTANCE + children)));
  }

  @Test public void aRepeatedChildMixingElementAndFieldEntriesIsRefused() throws Exception
  {
    // Reading the list by its first entry reads every later entry of the other kind as the wrong one.
    // A field read as an element loses its value, and an element read as a field loses its children.
    // An entry is an element when it carries `children:` or `type: element-instance`.
    String element = "    - children:\n        f:\n          value: a\n";
    String field = "    - value: b\n";
    String stub = "    - type: element-instance\n";
    Map<String, String> cases = new LinkedHashMap<>();
    cases.put("children:\n  R:\n" + element + field, "/R[1]");
    cases.put("children:\n  R:\n" + field + element, "/R[1]");
    cases.put("children:\n  R:\n" + field + stub, "/R[1]");
    cases.put("children:\n  R:\n" + element + stub + field, "/R[2]");
    cases.put("children:\n  E:\n    children:\n      R:\n        - value: b\n"
      + "        - children:\n            f:\n              value: a\n", "/E/R[1]");

    for (Map.Entry<String, String> mixed : cases.entrySet()) {
      ArtifactParseException thrown = refusalOf(mixed.getKey());
      assertEquals("A repeated child mixes element and field entries", thrown.getParseErrorMessage(),
        mixed.getKey());
      assertEquals("R", thrown.getFieldKey(), mixed.getKey());
      assertTrue(thrown.getPath().endsWith(mixed.getValue()), thrown.getMessage());
    }

    // A list of one kind still reads, the stub among elements included.
    TemplateInstanceArtifact elements = reader.readTemplateInstanceArtifact(
      ArtifactSourceParser.yaml(INSTANCE + "children:\n  R:\n" + element + stub));
    assertEquals(2, elements.multiInstanceElementInstances().get("R").size());
    TemplateInstanceArtifact fields = reader.readTemplateInstanceArtifact(
      ArtifactSourceParser.yaml(INSTANCE + "children:\n  R:\n" + field + field));
    assertEquals(2, fields.multiInstanceFieldInstances().get("R").size());
  }

  @Test public void aNonMappingEntryInARepeatedChildIsAParseError()
  {
    // Each entry is checked before any is cast, so a later one cannot throw ClassCastException.
    Map<String, String> cases = new LinkedHashMap<>();
    cases.put("children:\n  R:\n    - value: b\n    - plain\n", "/R[1]");
    cases.put("children:\n  R:\n    - children:\n        f:\n          value: a\n    - 5\n", "/R[1]");
    cases.put("children:\n  R:\n    - value: a\n    - value: b\n    - [c]\n", "/R[2]");
    cases.put("children:\n  R:\n    - plain\n    - value: b\n", "/R[0]");

    for (Map.Entry<String, String> entry : cases.entrySet()) {
      ArtifactParseException thrown = refusalOf(entry.getKey());
      assertEquals("Expected map entries in multi-instance list", thrown.getParseErrorMessage(), entry.getKey());
      assertEquals("R", thrown.getFieldKey(), entry.getKey());
      assertTrue(thrown.getPath().endsWith(entry.getValue()), thrown.getMessage());
    }
  }

  @Test public void anEmptyDatatypeIsRefusedAsAnInvalidDatatype()
  {
    // Read as the empty relative IRI, it would be written to JSON as "@type": "".
    for (String children : List.of("children:\n  F:\n    value: x\n    datatype: \"\"\n",
      "children:\n  R:\n    - value: x\n      datatype: \"\"\n")) {
      ArtifactParseException thrown = refusalOf(children);
      assertEquals("Invalid datatype \"\"", thrown.getParseErrorMessage(), children);
      assertEquals("datatype", thrown.getFieldKey(), children);
    }
  }

  @Test public void aDatatypeThatIsNotAnIriIsRefusedTheSameWay()
  {
    // The refusal names the key and the path, where java.net.URI's IllegalArgumentException named neither.
    ArtifactParseException thrown = refusalOf("children:\n  F:\n    value: x\n    datatype: \"a b\"\n");
    assertEquals("Invalid datatype \"a b\"", thrown.getParseErrorMessage());
    assertEquals("datatype", thrown.getFieldKey());
    assertTrue(thrown.getPath().endsWith("/F"), thrown.getMessage());
  }

  /** A controlled-term field's YAML, its one ontology constraint pinned to {@code versionId}. */
  private static LinkedHashMap<String, Object> controlledTermFieldPinnedTo(String versionId)
  {
    OntologyValueConstraint pinned = new OntologyValueConstraint(
      URI.create("https://data.bioontology.org/ontologies/DOID"), "DOID", "Human Disease Ontology",
      Optional.empty(), Optional.empty(), Optional.empty(),
      Optional.of(new VersionSpec("63ef56dff672", Optional.empty(), Optional.empty())));
    ControlledTermField field = ControlledTermField.builder().withName("Disease")
      .withOntologyValueConstraint(pinned).build();
    LinkedHashMap<String, Object> rendering = new YamlArtifactRenderer(false).renderFieldSchemaArtifact(field);
    assertTrue(setVersionId(rendering, versionId), "the rendering states the constraint's version");
    return rendering;
  }

  /** Replaces the id of the first version map under {@code node}; false when there is none. */
  @SuppressWarnings("unchecked")
  private static boolean setVersionId(Object node, String versionId)
  {
    if (node instanceof Map<?, ?> map) {
      if (map.get("version") instanceof Map<?, ?> version && version.containsKey("id")) {
        ((Map<String, Object>) version).put("id", versionId);
        return true;
      }
      for (Object child : map.values())
        if (setVersionId(child, versionId))
          return true;
    } else if (node instanceof List<?> list) {
      for (Object child : list)
        if (setVersionId(child, versionId))
          return true;
    }
    return false;
  }
}
