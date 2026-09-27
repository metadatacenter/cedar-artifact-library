package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InstanceDerivedFromTest {
  private static final URI SOURCE = URI.create("https://example.org/instances/original");
  private final JsonArtifactRenderer json = new JsonArtifactRenderer();
  private final YamlArtifactRenderer yaml = new YamlArtifactRenderer(false);

  private TemplateInstanceArtifact instance(boolean derived) {
    return TemplateInstanceArtifact.builder().withName("Copy")
      .withIsBasedOn(URI.create("https://example.org/templates/example"))
      .withDerivedFrom(derived ? SOURCE : null).build();
  }

  @Test void preservesProvenanceThroughJsonYamlAndBack() {
    var source = json.renderTemplateInstanceArtifact(instance(true));
    assertEquals(SOURCE.toString(), source.path("pav:derivedFrom").asText());
    var fromJson = new JsonArtifactReader().readTemplateInstanceArtifact(source);
    assertEquals(Optional.of(SOURCE), fromJson.derivedFrom());
    var document = yaml.renderTemplateInstanceArtifact(fromJson);
    assertEquals(SOURCE.toString(), document.get("derivedFrom"));
    var fromYaml = new YamlArtifactReader().readTemplateInstanceArtifact(document);
    assertEquals(Optional.of(SOURCE), fromYaml.derivedFrom());
    assertEquals(source, json.renderTemplateInstanceArtifact(fromYaml));
    assertEquals(document, yaml.renderTemplateInstanceArtifact(fromYaml));
    assertEquals(Optional.of(SOURCE), TemplateInstanceArtifact.builder(fromYaml).build().derivedFrom());
  }

  @Test void absentProvenanceStaysAbsent() {
    var document = yaml.renderTemplateInstanceArtifact(instance(false));
    assertFalse(document.containsKey("derivedFrom"));
    var restored = new YamlArtifactReader().readTemplateInstanceArtifact(document);
    assertTrue(restored.derivedFrom().isEmpty());
    assertFalse(json.renderTemplateInstanceArtifact(restored).has("pav:derivedFrom"));
  }

  @Test void compactYamlIntentionallyOmitsRepositoryProvenance() {
    var document = new YamlArtifactRenderer(true).renderTemplateInstanceArtifact(instance(true));
    assertFalse(document.containsKey("derivedFrom"));
    assertEquals("https://example.org/templates/example", document.get("isBasedOn"));
  }
}
