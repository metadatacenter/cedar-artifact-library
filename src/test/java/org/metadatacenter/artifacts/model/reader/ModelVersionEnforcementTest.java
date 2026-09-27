package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import static org.metadatacenter.model.ModelNodeNames.MODEL_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;

/**
 * What each reader does with the model version an artifact declares, recorded because the two
 * readers do different things with the same declaration.
 *
 * <p>{@code JsonArtifactShapeChecks.checkSchemaArtifactModelVersion} rejects a value it cannot parse
 * and accepts every value it can: the comparison against the current version is commented out, and
 * an artifact declaring no version at all is accepted too. {@code YamlArtifactReader} declares a
 * method of the same name that compares, so the same artifact is read as JSON and refused as YAML.
 * The JSON side is deliberate and cannot change on its own — enabling the comparison would refuse
 * every stored artifact written against an earlier model, and refuse those carrying no version at
 * all, so the deployment's artifacts have to be patched first.
 *
 * <p>The two readers now agree. A walk of every schema artifact a deployment serves found
 * {@code schema:schemaVersion} declared on all of them and holding the current value throughout,
 * so the comparison was restored and these tests assert the rejections they were written to expect.
 * They stay because the agreement is worth pinning: the day either reader stops refusing a stale or
 * absent version, one of these fails rather than the divergence going unnoticed again.
 *
 * <p>Nothing else in the suites can catch the divergence. Every other fixture supplies the version by
 * referencing {@code ModelNodeNames.MODEL_VERSION}, the same constant the disabled comparison would
 * compare against, so the comparison is satisfied whether it runs or not; {@code YamlArtifactRenderer}
 * writes that constant rather than the version its source artifact declared, so a cross-format round
 * trip turns a stale version into the current one before the strict reader sees it; and the in-memory
 * model has no field to carry a model version through in the first place.
 */
public class ModelVersionEnforcementTest
{
  /** Well-formed, and older than any model CEDAR has shipped. */
  private static final String STALE_VERSION = "0.1.0";

  /** The YAML spelling of the key. The JSON spelling is {@code schema:schemaVersion}. */
  private static final String YAML_MODEL_VERSION = "modelVersion";

  private JsonArtifactReader jsonReader;
  private YamlArtifactReader yamlReader;
  private ObjectMapper mapper;

  @BeforeEach public void setup()
  {
    jsonReader = new JsonArtifactReader();
    yamlReader = new YamlArtifactReader();
    mapper = new ObjectMapper();
  }

  // ---- JSON: what the restored comparison refuses ----

  @Test public void jsonRefusesAStaleModelVersion()
  {
    ObjectNode template = jsonTemplate();
    template.put(SCHEMA_ORG_SCHEMA_VERSION, STALE_VERSION);

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> jsonReader.readTemplateSchemaArtifact(template));

    assertTrue(thrown.getMessage().contains(STALE_VERSION), thrown.getMessage());
  }

  @Test public void jsonRefusesAnArtifactDeclaringNoModelVersionAtAll()
  {
    // Absence is not conformance: an artifact that never declared a version has not asserted that
    // it follows this model.
    ObjectNode template = jsonTemplate();
    template.remove(SCHEMA_ORG_SCHEMA_VERSION);

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> jsonReader.readTemplateSchemaArtifact(template));

    assertTrue(thrown.getMessage().toLowerCase().contains("model version"), thrown.getMessage());
  }

  @Test public void jsonRejectsAModelVersionItCannotParse()
  {
    // The one thing the JSON check does enforce, and the reason a test asking "is the model version
    // validated?" passes today. This assertion survives the comparison being restored.
    ObjectNode template = jsonTemplate();
    template.put(SCHEMA_ORG_SCHEMA_VERSION, "not-a-version");

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> jsonReader.readTemplateSchemaArtifact(template));
    assertTrue(thrown.getMessage().contains("not-a-version"), thrown.getMessage());
  }

  // ---- YAML: the same two declarations, refused ----
  //
  // YamlArtifactReaderNegativePathsTest covers the YAML reader's own contract, including its compact
  // mode. These two are here so the divergence is stated in one place instead of inferred from two
  // files, and they pair with the two JSON acceptances above.

  @Test public void yamlRefusesTheStaleVersionJsonAccepts()
  {
    LinkedHashMap<String, Object> template = yamlTemplate();
    template.put(YAML_MODEL_VERSION, STALE_VERSION);

    ArtifactParseException thrown = assertThrows(ArtifactParseException.class,
      () -> yamlReader.readTemplateSchemaArtifact(template));
    assertTrue(thrown.getMessage().toLowerCase().contains("model version"), thrown.getMessage());
  }

  @Test public void yamlRefusesTheAbsentVersionJsonAccepts()
  {
    LinkedHashMap<String, Object> template = yamlTemplate();
    template.remove(YAML_MODEL_VERSION);

    assertThrows(ArtifactParseException.class, () -> yamlReader.readTemplateSchemaArtifact(template));
  }

  // ---- Helpers ----

  /** A template the JSON reader accepts, declaring the current model version. */
  private ObjectNode jsonTemplate()
  {
    ObjectNode node = mapper.createObjectNode();

    node.put(SCHEMA_ORG_SCHEMA_VERSION, MODEL_VERSION);
    node.put(SCHEMA_ORG_NAME, "T");
    node.put(SCHEMA_ORG_DESCRIPTION, "d");
    node.put(JSON_SCHEMA_SCHEMA, JSON_SCHEMA_SCHEMA_IRI);
    node.put(JSON_SCHEMA_TYPE, JSON_SCHEMA_OBJECT);
    node.put(JSON_SCHEMA_TITLE, "title");
    node.put(JSON_SCHEMA_DESCRIPTION, "desc");
    node.put(JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    node.put(JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);
    node.put(UI, mapper.createObjectNode());
    node.put(JSON_LD_CONTEXT, contextMap(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS));
    node.put(JSON_LD_TYPE, TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI);

    return node;
  }

  /** The same template in YAML, declaring the same current model version. */
  private LinkedHashMap<String, Object> yamlTemplate()
  {
    LinkedHashMap<String, Object> node = new LinkedHashMap<>();

    node.put("type", "template");
    node.put("name", "T");
    node.put(YAML_MODEL_VERSION, MODEL_VERSION);

    return node;
  }

  private ObjectNode contextMap(Map<String, java.net.URI> contextMap)
  {
    ObjectNode node = mapper.createObjectNode();
    for (var entry : contextMap.entrySet())
      node.put(entry.getKey(), entry.getValue().toString());
    return node;
  }
}
