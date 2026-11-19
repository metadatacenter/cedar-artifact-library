package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer;
import org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * An integer has no decimal places, so a numeric field whose number type is an integer type, such as
 * {@code xsd:long}, and which states {@code decimalPlace} is refused by both readers, as the validation
 * library's {@code FieldValuePolicy} refuses it. The server reads every write through these readers, so a reader
 * that stopped refusing it would let the server store such a field.
 *
 * <p>Each document starts from a decimal field the readers accept and changes only its number type, so
 * the refusal can have no other cause.
 */
public class IntegerDecimalPlacesRefusalTest
{
  private static final String REFUSAL = "An integer datatype cannot have decimal places.";

  private static TemplateSchemaArtifact decimalTemplate()
  {
    return TemplateSchemaArtifact.builder().withName("Measures")
      .withFieldSchema(NumericField.builder().withName("Weight").withNumericType(XsdNumericDatatype.DECIMAL)
        .withDecimalPlaces(2).build())
      .build();
  }

  @Test public void theJsonReaderRefusesAnIntegerFieldThatStatesDecimalPlaces()
  {
    ObjectNode template = new JsonArtifactRenderer().renderTemplateSchemaArtifact(decimalTemplate());
    ObjectNode constraints = (ObjectNode) template.at("/properties/Weight/_valueConstraints");
    assertEquals(2, constraints.get("decimalPlace").asInt());
    new JsonArtifactReader().readTemplateSchemaArtifact(template.deepCopy());

    constraints.put("numberType", "xsd:long");
    Exception refusal = assertThrows(RuntimeException.class,
      () -> new JsonArtifactReader().readTemplateSchemaArtifact(template));
    assertEquals(REFUSAL, rootCause(refusal).getMessage());
  }

  @Test @SuppressWarnings("unchecked")
  public void theYamlReaderRefusesAnIntegerFieldThatStatesDecimalPlaces()
  {
    LinkedHashMap<String, Object> template = new YamlArtifactRenderer(false).renderTemplateSchemaArtifact(decimalTemplate());
    Map<String, Object> field = (Map<String, Object>) ((List<Object>) template.get("children")).get(0);
    assertEquals(2, field.get("decimalPlaces"));
    new YamlArtifactReader().readTemplateSchemaArtifact(new LinkedHashMap<>(template));

    field.put("datatype", "xsd:long");
    Exception refusal = assertThrows(RuntimeException.class,
      () -> new YamlArtifactReader().readTemplateSchemaArtifact(template));
    assertEquals(REFUSAL, rootCause(refusal).getMessage());
  }

  private static Throwable rootCause(Throwable thrown)
  {
    Throwable cause = thrown;
    while (cause.getCause() != null && cause.getCause() != cause)
      cause = cause.getCause();
    return cause;
  }
}
