package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Refusals on the readers' write path that no other test reaches. The server reads every write through these
 * readers, so a refusal that stopped firing would let it store the shape. Each document starts from one the
 * reader accepts and changes one thing, and each test requires the reason the reader gives.
 */
public class WritePathRefusalsTest
{
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static TemplateSchemaArtifact template()
  {
    return TemplateSchemaArtifact.builder().withName("Study")
      .withJsonLdId(URI.create("https://repo.metadatacenter.org/templates/write-path"))
      .withFieldSchema(TextField.builder().withName("Title").build())
      .withFieldSchema(TextField.builder().withName("Tags").withIsMultiple(true).build())
      .withFieldSchema(TemporalField.builder().withName("When").withTemporalType(XsdTemporalDatatype.DATETIME)
        .withTemporalGranularity(TemporalGranularity.MINUTE).build())
      .withFieldSchema(ControlledTermField.builder().withName("Term").build())
      .withFieldSchema(NumericField.builder().withName("Count").withNumericType(XsdNumericDatatype.INT).build())
      .withElementSchema(ElementSchemaArtifact.builder().withName("Address")
        .withFieldSchema(TextField.builder().withName("Street").build()).build())
      .build();
  }

  /** The template's JSON with one change, which must be refused for {@code keyAndPath}. */
  private static void assertJsonRefused(String keyAndPath, Consumer<ObjectNode> change)
  {
    ObjectNode json = new JsonArtifactRenderer().renderTemplateSchemaArtifact(template());
    new JsonArtifactReader().readTemplateSchemaArtifact(json.deepCopy());
    change.accept(json);
    assertEquals(keyAndPath, Refusals.keyAndPath(() -> new JsonArtifactReader().readTemplateSchemaArtifact(json)));
  }

  @Test public void theJsonReaderRefusesATemplateNestedInATemplate()
  {
    assertJsonRefused("Address at /properties/Address", json ->
      ((ObjectNode) json.at("/properties/Address")).put("@type", "https://schema.metadatacenter.org/core/Template"));
  }

  // The child's @type is read, and refused, before the reader looks for the kind of child it names.
  @Test public void theJsonReaderRefusesAChildOfUnknownType()
  {
    assertJsonRefused("@type at /properties/Address", json ->
      ((ObjectNode) json.at("/properties/Address")).put("@type", "https://example.org/Unknown"));
  }

  @Test public void theJsonReaderRefusesAChildThatIsNoObject()
  {
    assertJsonRefused("Title at /properties/Title", json -> ((ObjectNode) json.get("properties")).put("Title", "text"));
  }

  @Test public void theJsonReaderRefusesARepeatedChildWithoutItems()
  {
    assertJsonRefused("items at /properties/Tags", json -> ((ObjectNode) json.at("/properties/Tags")).remove("items"));
  }

  @Test public void theJsonReaderRefusesAChildThatIsNeitherArrayNorObject()
  {
    assertJsonRefused("items at /properties/Title", json -> ((ObjectNode) json.at("/properties/Title")).put("type", "string"));
  }

  @Test public void theJsonReaderRefusesAnAnnotationWithNeitherIdNorValue()
  {
    assertJsonRefused("note at /_annotations", json -> json.putObject("_annotations").putObject("note").put("text", "x"));
  }

  @Test public void theJsonReaderRefusesAnUnknownGranularityAndTimeFormat()
  {
    assertJsonRefused("temporalGranularity at /properties/When/_ui",
      json -> ((ObjectNode) json.at("/properties/When/_ui")).put("temporalGranularity", "fortnight"));
    assertJsonRefused("inputTimeFormat at /properties/When/_ui",
      json -> ((ObjectNode) json.at("/properties/When/_ui")).put("inputTimeFormat", "13h"));
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> yamlChild(Map<String, Object> template, String key)
  {
    return ((List<Map<String, Object>>) template.get("children")).stream().filter(child -> key.equals(child.get("key")))
      .findFirst().orElseThrow();
  }

  /** The template's full YAML with one change, which must be refused for {@code keyAndPath}. */
  private static void assertYamlRefused(String keyAndPath, Consumer<LinkedHashMap<String, Object>> change)
  {
    LinkedHashMap<String, Object> yaml = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(template());
    new YamlArtifactReader().readTemplateSchemaArtifact(MAPPER.convertValue(yaml, LinkedHashMap.class));
    LinkedHashMap<String, Object> changed = MAPPER.convertValue(yaml, LinkedHashMap.class);
    change.accept(changed);
    assertEquals(keyAndPath, Refusals.keyAndPath(() -> new YamlArtifactReader().readTemplateSchemaArtifact(changed)));
  }

  @Test public void theYamlReaderRefusesAControlledTermFieldThatIsNoIri()
  {
    assertYamlRefused("datatype at /Term", yaml -> yamlChild(yaml, "Term").put("datatype", "xsd:string"));
  }

  @Test public void theYamlReaderRefusesAnUnknownStatus()
  {
    assertYamlRefused("status at /", yaml -> yaml.put("status", "retired"));
  }

  @Test public void theYamlReaderRefusesAMalformedAnnotation()
  {
    assertYamlRefused("annotations at /", yaml -> yaml.put("annotations", new LinkedHashMap<>(Map.of("note", "text"))));
    assertYamlRefused("annotations at /",
      yaml -> yaml.put("annotations", new LinkedHashMap<>(Map.of("note", new LinkedHashMap<>(Map.of("text", "x"))))));
  }

  @Test public void theYamlReaderRefusesANumericDefaultThatIsNoNumber()
  {
    assertYamlRefused("default at /Count", yaml -> yamlChild(yaml, "Count").put("default", true));
    assertYamlRefused("default at /Count", yaml -> yamlChild(yaml, "Count").put("default", "many"));
  }

  /** An instance of the template in YAML with one change, which must be refused with {@code message}. */
  private static void assertYamlInstanceRefused(String keyAndPath, Consumer<LinkedHashMap<String, Object>> change)
  {
    TemplateInstanceArtifact instance = TemplateInstanceArtifact.builder().withName("Study Instance")
      .withIsBasedOn(URI.create("https://repo.metadatacenter.org/templates/write-path"))
      .withSingleInstanceFieldInstance("Title", FieldInstanceArtifact.create(Collections.emptyList(), Optional.empty(),
        Optional.of("A study"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()))
      .build();
    LinkedHashMap<String, Object> yaml = MAPPER.convertValue(
      new YamlArtifactRenderer(false).renderTemplateInstanceArtifact(instance), LinkedHashMap.class);
    new YamlArtifactReader().readTemplateInstanceArtifact(MAPPER.convertValue(yaml, LinkedHashMap.class));
    change.accept(yaml);
    assertEquals(keyAndPath, Refusals.keyAndPath(() -> new YamlArtifactReader().readTemplateInstanceArtifact(yaml)));
  }

  @Test @SuppressWarnings("unchecked") public void theYamlReaderRefusesAChildInstanceThatIsNeitherMappingNorList()
  {
    assertYamlInstanceRefused("Title at /", yaml -> ((Map<String, Object>) yaml.get("children")).put("Title", 7));
  }

  @Test public void theYamlReaderRefusesAnAttributeThatIsNoMapping()
  {
    assertYamlInstanceRefused("Pairs at /", yaml -> yaml.put("Pairs", new LinkedHashMap<>(Map.of("colour", "blue"))));
  }

  @Test public void eachRefusalNamesWhatItRefuses()
  {
    var refusal = Refusals.parseRefusal(() -> {
      LinkedHashMap<String, Object> yaml = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(template());
      LinkedHashMap<String, Object> changed = MAPPER.convertValue(yaml, LinkedHashMap.class);
      yamlChild(changed, "Term").put("datatype", "xsd:string");
      new YamlArtifactReader().readTemplateSchemaArtifact(changed);
    });
    assertTrue(refusal.getMessage().startsWith("A controlled-term field's datatype is iri, not xsd:string"), refusal.getMessage());
  }
}
