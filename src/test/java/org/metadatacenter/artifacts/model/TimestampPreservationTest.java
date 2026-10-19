package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TimestampPreservationTest {
  @Test void jsonAndYamlKeepNanosecondsLocalDateAndOffset() {
    for (String value : new String[] {"2026-01-02T03:04:05.123456789+01:02:03",
        "0001-01-02T03:04:05.000000001-04:00", "2026-01-02T03:04:05.12Z"}) {
      var timestamp = OffsetDateTime.parse(value);
      var original = TemplateSchemaArtifact.builder().withName("Time")
          .withCreatedOn(timestamp).withLastUpdatedOn(timestamp).build();
      var json = new JsonArtifactRenderer().renderTemplateSchemaArtifact(original);
      var yaml = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(original);
      var fromJson = new JsonArtifactReader().readTemplateSchemaArtifact(json);
      var fromYaml = new YamlArtifactReader().readTemplateSchemaArtifact(yaml);
      assertEquals(original.createdOn(), fromJson.createdOn(), value);
      assertEquals(original.lastUpdatedOn(), fromJson.lastUpdatedOn(), value);
      assertEquals(original.createdOn(), fromYaml.createdOn(), value);
      assertEquals(original.lastUpdatedOn(), fromYaml.lastUpdatedOn(), value);
    }
  }
}
