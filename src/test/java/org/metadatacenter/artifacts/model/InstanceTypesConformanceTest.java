package org.metadatacenter.artifacts.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.reader.*;
import org.metadatacenter.artifacts.model.renderer.*;
import org.metadatacenter.model.validation.CedarValidator;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

/** Live model, wire-format, schema-validator and shared TypeScript conformance checks. */
class InstanceTypesConformanceTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonArtifactRenderer JSON = new JsonArtifactRenderer();
  private static final JsonArtifactReader READER = new JsonArtifactReader();
  private static final Path FIXTURE = Path.of("src/test/resources/concordance/instance-types.json");
  private static final List<URI> TYPES = List.of(URI.create("urn:types:one"), URI.create("urn:types:two"));

  @Test void jsonYamlAndCompactYamlPreserveEveryTypeAndConformToTheSchema() throws Exception {
    var cases = MAPPER.createArrayNode();
    for (int count : List.of(0, 1, 2, 64)) {
      var types = IntStream.range(0, count).mapToObj(i -> URI.create("urn:types:" + i)).toList();
      var element = ElementSchemaArtifact.builder().withName("Element").withDescription("Element description").withInstanceJsonLdTypes(types).build();
      var template = TemplateSchemaArtifact.builder().withName("Template").withDescription("Template description").withInstanceJsonLdTypes(types)
        .withElementSchema("Element", element).build();
      var elementJson = JSON.renderElementSchemaArtifact(element);
      assertEquals(elementJson, JSON.renderElementSchemaArtifact(READER.readElementSchemaArtifact(elementJson)));
      for (boolean compact : List.of(false, true)) {
        var elementYaml = new YamlArtifactRenderer(compact).renderElementSchemaArtifact(element);
        var restoredElement = new YamlArtifactReader(compact).readElementSchemaArtifact(elementYaml);
        assertEquals(types, restoredElement.instanceJsonLdTypes());
        assertEquals(elementYaml, new YamlArtifactRenderer(compact).renderElementSchemaArtifact(restoredElement));
      }
      var json = JSON.renderTemplateSchemaArtifact(template);
      var expected = MAPPER.valueToTree(types.stream().map(URI::toString).toList());
      if (count > 0) {
        assertEquals(expected, json.at("/properties/@type/oneOf/0/enum"));
        assertEquals(expected, json.at("/properties/@type/oneOf/1/items/enum"));
        assertEquals(expected, json.at("/properties/Element/properties/@type/oneOf/0/enum"));
      } else assertTrue(json.at("/properties/@type/oneOf/0/enum").isMissingNode());
      var report = new CedarValidator().validateTemplate(json);
      assertEquals("true", report.getValidationStatus(), report.getErrors().toString());
      var read = READER.readTemplateSchemaArtifact(json);
      assertEquals(types, read.instanceJsonLdTypes());
      assertEquals(types, read.elementSchemas().get("Element").instanceJsonLdTypes());
      assertEquals(json, JSON.renderTemplateSchemaArtifact(read));
      assertEquals(types, TemplateSchemaArtifact.builder(template).build().instanceJsonLdTypes());
      assertEquals(types, ElementSchemaArtifact.builder(element).build().instanceJsonLdTypes());
      var entry = MAPPER.createObjectNode().put("count", count);
      entry.set("json", json);
      for (boolean compact : List.of(false, true)) {
        var writer = new YamlArtifactRenderer(compact);
        var yaml = writer.renderTemplateSchemaArtifact(template);
        var restored = new YamlArtifactReader(compact).readTemplateSchemaArtifact(yaml);
        assertEquals(types, restored.instanceJsonLdTypes());
        assertEquals(types, restored.elementSchemas().get("Element").instanceJsonLdTypes());
        assertEquals(yaml, writer.renderTemplateSchemaArtifact(restored));
        if (!compact) assertEquals(json, JSON.renderTemplateSchemaArtifact(restored));
        if (count == 0) assertFalse(yaml.containsKey("instanceType"));
        else if (count == 1) assertEquals(types.get(0).toString(), yaml.get("instanceType"));
        else assertEquals(types.stream().map(URI::toString).toList(), yaml.get("instanceType"));
        entry.set(compact ? "compactYaml" : "yaml", MAPPER.valueToTree(yaml));
      }
      cases.add(entry);
    }
    if (Boolean.getBoolean("updateInstanceTypesConformance")) {
      Files.createDirectories(FIXTURE.getParent());
      MAPPER.writerWithDefaultPrettyPrinter().writeValue(FIXTURE.toFile(), cases);
    }
    assertEquals(MAPPER.readTree(FIXTURE.toFile()), cases, "Update and review the shared conformance fixture when the contract changes");
  }

  @Test void selectedClassesAreAllowedAlternativesRatherThanAllRequired() {
    var template = TemplateSchemaArtifact.builder().withName("T").withInstanceJsonLdTypes(TYPES).build();
    var schema = org.metadatacenter.model.validation.internal.FgeCompatFormats.FACTORY
      .getSchema(JSON.renderTemplateSchemaArtifact(template).at("/properties/@type"));
    assertTrue(schema.validate(MAPPER.valueToTree("urn:types:one")).isEmpty());
    assertTrue(schema.validate(MAPPER.valueToTree("urn:types:two")).isEmpty());
    assertTrue(schema.validate(MAPPER.valueToTree(List.of("urn:types:one", "urn:types:two"))).isEmpty());
    assertFalse(schema.validate(MAPPER.valueToTree("urn:other")).isEmpty());
    assertFalse(schema.validate(MAPPER.valueToTree(List.of("urn:types:one", "urn:other"))).isEmpty());
    assertFalse(schema.validate(MAPPER.valueToTree(List.of("urn:types:one", "urn:types:one"))).isEmpty());
  }

  @Test void singletonCompatibilityAndDefensiveCopies() {
    var source = new ArrayList<>(TYPES);
    var builder = TemplateSchemaArtifact.builder().withName("T").withInstanceJsonLdTypes(source);
    source.clear();
    var template = builder.build();
    assertEquals(TYPES, template.instanceJsonLdTypes());
    assertEquals(Optional.of(TYPES.get(0)), template.instanceJsonLdType());
    assertThrows(UnsupportedOperationException.class, () -> template.instanceJsonLdTypes().clear());
    assertEquals(List.of(TYPES.get(1)), builder.withInstanceJsonLdType(TYPES.get(1)).build().instanceJsonLdTypes());
    assertTrue(builder.withInstanceJsonLdType(null).build().instanceJsonLdTypes().isEmpty());
    assertEquals(TYPES, template.instanceJsonLdTypes());
  }

  @Test void rejectsMalformedAndDuplicateTypesInsteadOfDroppingThem() {
    assertThrows(IllegalStateException.class, () -> TemplateSchemaArtifact.builder().withName("T")
      .withInstanceJsonLdTypes(List.of(TYPES.get(0), TYPES.get(0))).build());
    assertThrows(IllegalArgumentException.class, () -> ElementSchemaArtifact.builder().withName("E")
      .withInstanceJsonLdTypes(List.of(URI.create("relative"))).build());
    var template = TemplateSchemaArtifact.builder().withName("T").withInstanceJsonLdTypes(TYPES).build();
    for (Object invalid : List.of(List.of(42), List.of("relative"), List.of("urn:a", "urn:a"), List.of())) {
      var json = JSON.renderTemplateSchemaArtifact(template);
      ((ObjectNode)json.at("/properties/@type/oneOf/0")).set("enum", MAPPER.valueToTree(invalid));
      assertThrows(ArtifactParseException.class, () -> READER.readTemplateSchemaArtifact(json));
      var yaml = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(template);
      yaml.put("instanceType", invalid);
      assertThrows(ArtifactParseException.class, () -> new YamlArtifactReader().readTemplateSchemaArtifact(yaml));
    }
  }
}
