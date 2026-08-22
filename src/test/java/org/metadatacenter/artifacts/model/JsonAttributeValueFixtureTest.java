package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonAttributeValueFixtureTest
{
  private static final String TEMPLATE_FIXTURE = "attribute-values/two-fields-template.json";
  private static final String INSTANCE_FIXTURE = "attribute-values/two-fields-instance.json";

  private JsonArtifactReader reader;
  private JsonArtifactRenderer renderer;
  private ObjectMapper mapper;

  @BeforeEach public void setup()
  {
    reader = new JsonArtifactReader();
    renderer = new JsonArtifactRenderer();
    mapper = new ObjectMapper();
  }

  @Test public void testTemplateDeclaresTwoAttributeValueFields()
  {
    TemplateSchemaArtifact template = reader.readTemplateSchemaArtifact(readFixture(TEMPLATE_FIXTURE));

    assertAttributeValueTemplate(template);
    assertAttributeValueTemplate(reader.readTemplateSchemaArtifact(renderer.renderTemplateSchemaArtifact(template)));
  }

  private void assertAttributeValueTemplate(TemplateSchemaArtifact template)
  {
    assertEquals(List.of("My AV Field 1", "My AV Field 2"), template.getUi().order());
    assertEquals(2, template.fieldSchemas().size());
    assertTrue(template.getFieldSchemaArtifact("My AV Field 1").isAttributeValue());
    assertTrue(template.getFieldSchemaArtifact("My AV Field 1").isMultiple());
    assertTrue(template.getFieldSchemaArtifact("My AV Field 2").isAttributeValue());
    assertTrue(template.getFieldSchemaArtifact("My AV Field 2").isMultiple());
  }

  @Test public void testInstanceKeepsEachAttributeInItsDeclaredGroup()
  {
    ObjectNode templateJson = readFixture(TEMPLATE_FIXTURE);
    ObjectNode instanceJson = readFixture(INSTANCE_FIXTURE);
    TemplateInstanceArtifact instance = reader.readTemplateInstanceArtifact(instanceJson);

    assertEquals(URI.create(templateJson.get("@id").asText()), instance.isBasedOn());
    assertAttributeValueGroups(instance, context(instanceJson));
  }

  @Test public void testInstanceRoundTripPreservesJsonLdShapeAndContext()
  {
    ObjectNode source = readFixture(INSTANCE_FIXTURE);
    ObjectNode rendered = renderer.renderTemplateInstanceArtifact(reader.readTemplateInstanceArtifact(source));

    for (Map.Entry<String, LinkedHashMap<String, String>> group : expectedGroups().entrySet()) {
      List<String> renderedNames = new ArrayList<>();
      rendered.withArray(group.getKey()).forEach(node -> renderedNames.add(node.asText()));
      assertEquals(List.copyOf(group.getValue().keySet()), renderedNames);
      for (Map.Entry<String, String> attribute : group.getValue().entrySet()) {
        assertEquals(attribute.getValue(), rendered.get(attribute.getKey()).get("@value").asText());
        assertEquals(context(source).get(attribute.getKey()), context(rendered).get(attribute.getKey()));
      }
    }

    String renderedText = rendered.toString();
    assertFalse(renderedText.contains("dataContainer"));
    assertFalse(renderedText.contains("_values"));
    assertFalse(renderedText.contains("_iris"));
    assertAttributeValueGroups(reader.readTemplateInstanceArtifact(rendered), context(rendered));
  }

  private void assertAttributeValueGroups(TemplateInstanceArtifact instance, ObjectNode context)
  {
    Map<String, Map<String, FieldInstanceArtifact>> actualGroups = instance.attributeValueFieldInstanceGroups();
    assertEquals(List.copyOf(expectedGroups().keySet()), List.copyOf(actualGroups.keySet()));

    for (Map.Entry<String, LinkedHashMap<String, String>> group : expectedGroups().entrySet()) {
      Map<String, FieldInstanceArtifact> actualAttributes = actualGroups.get(group.getKey());
      assertNotNull(actualAttributes);
      assertEquals(List.copyOf(group.getValue().keySet()), List.copyOf(actualAttributes.keySet()));

      for (Map.Entry<String, String> attribute : group.getValue().entrySet()) {
        assertEquals(attribute.getValue(), actualAttributes.get(attribute.getKey()).jsonLdValue().orElseThrow());
        assertEquals(URI.create(context.get(attribute.getKey()).asText()),
          instance.jsonLdContext().get(attribute.getKey()));
        assertFalse(instance.singleInstanceFieldInstances().containsKey(attribute.getKey()));
        assertFalse(instance.multiInstanceFieldInstances().containsKey(attribute.getKey()));
      }
    }
  }

  private LinkedHashMap<String, LinkedHashMap<String, String>> expectedGroups()
  {
    LinkedHashMap<String, LinkedHashMap<String, String>> groups = new LinkedHashMap<>();
    LinkedHashMap<String, String> first = new LinkedHashMap<>();
    first.put("A11", "V11");
    first.put("A12", "V12");
    groups.put("My AV Field 1", first);
    LinkedHashMap<String, String> second = new LinkedHashMap<>();
    second.put("A21", "V21");
    second.put("A22", "V22");
    groups.put("My AV Field 2", second);
    return groups;
  }

  private ObjectNode readFixture(String name)
  {
    try (InputStream input = JsonAttributeValueFixtureTest.class.getClassLoader().getResourceAsStream(name)) {
      assertNotNull(input, "Missing fixture " + name);
      JsonNode node = mapper.readTree(input);
      assertTrue(node.isObject(), "Fixture root must be a JSON object: " + name);
      return (ObjectNode)node;
    } catch (IOException e) {
      throw new RuntimeException("Error reading fixture " + name, e);
    }
  }

  private static ObjectNode context(ObjectNode node)
  {
    return (ObjectNode)node.get("@context");
  }
}
