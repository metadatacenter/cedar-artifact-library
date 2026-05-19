package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;

/**
 * Negative-path tests for JsonArtifactReader: malformed input must fail fast with a useful
 * exception rather than producing a silently-wrong artifact.
 */
public class JsonArtifactReaderNegativePathsTest
{
  private JsonArtifactReader reader;
  private ObjectMapper mapper;

  @BeforeEach public void setup()
  {
    reader = new JsonArtifactReader();
    mapper = new ObjectMapper();
  }

  // ---- @type checks ----

  @Test public void testReadTemplateThrowsOnMissingJsonLdType()
  {
    ObjectNode node = baseTemplate("T", "d");
    node.remove(JSON_LD_TYPE);

    assertThrows(ArtifactParseException.class, () -> reader.readTemplateSchemaArtifact(node));
  }

  @Test public void testReadTemplateThrowsOnWrongJsonLdType()
  {
    // A template document tagged with the element IRI must be rejected, not silently coerced.
    ObjectNode node = baseTemplate("T", "d");
    node.put(JSON_LD_TYPE, ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI);

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("template"));
  }

  @Test public void testReadFieldThrowsOnMissingSchemaName()
  {
    ObjectNode node = baseField("Test", "d");
    node.remove(SCHEMA_ORG_NAME);

    assertThrows(ArtifactParseException.class, () -> reader.readFieldSchemaArtifact(node));
  }

  @Test public void testReadFieldThrowsOnMalformedVersion()
  {
    ObjectNode node = baseField("Test", "d");
    node.put(PAV_VERSION, "not-a-version");
    node.with(UI).put("inputType", "textfield");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("not-a-version"));
  }

  @Test public void testReadTemplateInstanceThrowsOnMissingIsBasedOn()
  {
    // Without `isBasedOn` there's no template to validate the instance against.
    ObjectNode node = mapper.createObjectNode();
    node.put(JSON_LD_TYPE, mapper.createArrayNode());
    node.put(SCHEMA_ORG_NAME, "Inst");
    node.put(SCHEMA_ORG_DESCRIPTION, "d");

    assertThrows(Exception.class, () -> reader.readTemplateInstanceArtifact(node));
  }

  @Test public void testReadElementThrowsOnMissingProperties()
  {
    // Element schemas need a `properties` block for nested children.
    ObjectNode node = baseElement("E", "d");
    node.remove(JSON_SCHEMA_PROPERTIES);

    assertThrows(ArtifactParseException.class, () -> reader.readElementSchemaArtifact(node));
  }

  // ---- JsonNodeReaders helper rejections ----

  @Test public void testReadUriRejectsNonStringNode()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("uri", 42);

    assertThrows(Exception.class, () -> JsonNodeReaders.readUri(node, "/", "uri"));
  }

  @Test public void testReadIntegerRejectsStringFormattedNumber()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("n", "42");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> JsonNodeReaders.readInteger(node, "/", "n"));
    assertTrue(ex.getMessage().contains("integer"));
  }

  @Test public void testReadBooleanRejectsStringTrue()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("flag", "true");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> JsonNodeReaders.readBoolean(node, "/", "flag", false));
    assertTrue(ex.getMessage().contains("boolean"));
  }

  @Test public void testReadOffsetDateTimeRejectsMalformedString()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put("ts", "not-a-date");

    assertThrows(Exception.class, () -> JsonNodeReaders.readOffsetDateTime(node, "/", "ts"));
  }

  // ---- JSON shape robustness ----

  @Test public void testReadTemplateThrowsOnArrayProperties()
  {
    // JSON Schema `properties` must be an object; an array (or other non-object) is malformed.
    ObjectNode node = baseTemplate("T", "d");
    node.set(JSON_SCHEMA_PROPERTIES, mapper.createArrayNode());

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readTemplateSchemaArtifact(node));
    assertTrue(ex.getMessage().toLowerCase().contains("properties"));
  }

  @Test public void testReadFieldThrowsOnUnknownInputType()
  {
    ObjectNode node = baseField("Test", "d");
    node.with(UI).put("inputType", "not-a-real-input-type");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> reader.readFieldSchemaArtifact(node));
    assertTrue(ex.getMessage().contains("not-a-real-input-type"));
  }

  @Test public void testReadFieldThrowsOnMissingUi()
  {
    ObjectNode node = baseField("Test", "d");
    node.remove(UI);

    assertThrows(Exception.class, () -> reader.readFieldSchemaArtifact(node));
  }

  @Test public void testReadTemplateThrowsOnWrongJsonSchemaType()
  {
    // Top-level JSON Schema `type` must be `object`.
    ObjectNode node = baseTemplate("T", "d");
    node.put(JSON_SCHEMA_TYPE, "array");

    assertThrows(ArtifactParseException.class, () -> reader.readTemplateSchemaArtifact(node));
  }

  // ---- Helpers ----

  private ObjectNode baseTemplate(String name, String description)
  {
    ObjectNode node = baseSchema(name, description);
    node.put(JSON_LD_CONTEXT, createContextMap(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    node.put(JSON_LD_TYPE, TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);
    return node;
  }

  private ObjectNode baseElement(String name, String description)
  {
    ObjectNode node = baseSchema(name, description);
    node.put(JSON_LD_CONTEXT, createContextMap(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    node.put(JSON_LD_TYPE, ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI);
    return node;
  }

  private ObjectNode baseField(String name, String description)
  {
    ObjectNode node = baseSchema(name, description);
    node.put(JSON_LD_CONTEXT, createContextMap(FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    node.put(JSON_LD_TYPE, FIELD_SCHEMA_ARTIFACT_TYPE_IRI);
    return node;
  }

  private ObjectNode baseSchema(String name, String description)
  {
    ObjectNode node = mapper.createObjectNode();
    node.put(SCHEMA_ORG_SCHEMA_VERSION, "1.6.0");
    node.put(SCHEMA_ORG_NAME, name);
    node.put(SCHEMA_ORG_DESCRIPTION, description);
    node.put(JSON_SCHEMA_SCHEMA, JSON_SCHEMA_SCHEMA_IRI);
    node.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);
    node.put(JSON_SCHEMA_TITLE, "title");
    node.put(JSON_SCHEMA_DESCRIPTION, "desc");
    node.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    node.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);
    node.put(UI, mapper.createObjectNode());
    return node;
  }

  private ObjectNode createContextMap(Map<String, java.net.URI> contextMap)
  {
    ObjectNode node = mapper.createObjectNode();
    for (var e : contextMap.entrySet())
      node.put(e.getKey(), e.getValue().toString());
    return node;
  }
}
