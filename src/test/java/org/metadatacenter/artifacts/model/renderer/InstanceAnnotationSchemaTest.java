package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.model.validation.CedarValidator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InstanceAnnotationSchemaTest {
  private final ObjectMapper mapper = new ObjectMapper();
  private final JsonArtifactRenderer renderer = new JsonArtifactRenderer();

  @Test void everyTemplateAllowsOptionalAnnotationsWithoutTreatingThemAsChildren() throws Exception {
    var template = TemplateSchemaArtifact.builder().withName("Annotations").build();
    ObjectNode json = renderer.renderTemplateSchemaArtifact(template);
    assertEquals("true", new CedarValidator().validateTemplate(json).getValidationStatus());
    assertFalse(json.withArray("required").toString().contains("_annotations"));
    assertEquals("@nest", json.at("/properties/@context/properties/_annotations/enum/0").asText());
    var read = new JsonArtifactReader().readTemplateSchemaArtifact(json);
    assertTrue(read.getChildSchemas().isEmpty());
    assertEquals(json, renderer.renderTemplateSchemaArtifact(read));
    var legacy = json.deepCopy();
    ((ObjectNode) legacy.get("properties")).remove("_annotations");
    ((ObjectNode) legacy.at("/properties/@context/properties")).remove("_annotations");
    assertEquals(json, renderer.renderTemplateSchemaArtifact(new JsonArtifactReader().readTemplateSchemaArtifact(legacy)));
  }

  @Test void annotationSchemaAcceptsValueObjectsAndRejectsMalformedEntries() throws Exception {
    var json = renderer.renderTemplateSchemaArtifact(TemplateSchemaArtifact.builder().withName("Annotations").build());
    var schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4).getSchema(json.at("/properties/_annotations"));
    for (String entry : List.of("{\"@id\":\"https://doi.org/10.1234/example\"}", "{\"@value\":\"note\"}",
        "{\"@value\":42}", "{\"@value\":true}", "{\"@value\":null}")) {
      assertTrue(schema.validate(mapper.readTree("{\"note\":" + entry + "}")).isEmpty(), entry);
    }
    for (String entry : List.of("null", "42", "\"misplaced\"", "[]", "{}", "{\"other\":\"x\"}",
        "{\"@id\":\"urn:doi\",\"@value\":\"conflict\"}", "{\"@value\":[]}")) {
      assertFalse(schema.validate(mapper.readTree("{\"note\":" + entry + "}")).isEmpty(), entry);
    }
    assertTrue(schema.validate(mapper.readTree("{}")).isEmpty());
    assertFalse(schema.validate(mapper.readTree("{\"\":{\"@value\":\"empty name\"}}")).isEmpty());
  }
}
