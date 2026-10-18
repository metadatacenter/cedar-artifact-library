package org.metadatacenter.artifacts.model;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.reader.JsonArtifactReader;
import org.metadatacenter.artifacts.model.reader.YamlArtifactReader;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;
import org.metadatacenter.model.validation.NumericPolicy;
import static org.junit.jupiter.api.Assertions.*;

class ExactNumericPreservationTest {
  @Test void defaultsAndBoundsSurviveBothModelPathsWithoutRounding() {
    for (String text : new String[] {"9007199254740993", "9223372036854775808",
        "0.10000000000000001", "1.0000000000000001", "1e9999"}) {
      Number exact = NumericPolicy.number(text);
      var original = NumericField.builder().withName("Exact")
          .withNumericType(XsdNumericDatatype.DECIMAL).withDefaultValue(exact)
          .withMinValue(exact).withMaxValue(exact).build();
      var json = new JsonArtifactRenderer().renderFieldSchemaArtifact(original);
      var yaml = new YamlArtifactRenderer(false).renderFieldSchemaArtifact(original);
      var fromJson = new JsonArtifactReader().readFieldSchemaArtifact(json);
      var fromYaml = new YamlArtifactReader().readFieldSchemaArtifact(yaml);
      for (var result : new org.metadatacenter.artifacts.model.core.FieldSchemaArtifact[] {fromJson, fromYaml}) {
        var constraints = result.valueConstraints().orElseThrow().asNumericValueConstraints();
        assertEquals(0, NumericPolicy.compare(text, constraints.defaultValue().orElseThrow().value().toString()));
        assertEquals(0, NumericPolicy.compare(text, constraints.minValue().orElseThrow().toString()));
        assertEquals(0, NumericPolicy.compare(text, constraints.maxValue().orElseThrow().toString()));
      }
    }
  }

  @Test void rejectsNonFiniteDefaults() {
    assertThrows(NumberFormatException.class, () -> NumericField.builder().withName("Bad")
        .withNumericType(XsdNumericDatatype.DECIMAL).withDefaultValue(Double.NaN).build());
  }
}
