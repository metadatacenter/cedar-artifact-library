package org.metadatacenter.artifacts.model.core.fields.constraints;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.model.validation.FieldValuePolicy;
import org.metadatacenter.model.validation.NumericPolicy;
import org.metadatacenter.model.validation.ValidationFinding;
import java.util.List;
import java.util.Optional;

/** Artifact readers and builders use the same local consistency rules as the model validator. */
final class ConstraintConsistency {
  private ConstraintConsistency() {}
  static ObjectNode field(String inputType) {
    ObjectNode field = JsonNodeFactory.instance.objectNode();
    field.putObject("_ui").put("inputType", inputType).put("timezoneEnabled", true);
    field.putObject("_valueConstraints");
    return field;
  }
  static void number(ObjectNode constraints, String key, Optional<Number> value) {
    value.ifPresent(number -> {
      try { constraints.put(key, NumericPolicy.parse(number.toString())); }
      catch (NumberFormatException invalid) { throw new IllegalStateException(key + " must be a finite decimal."); }
    });
  }
  static void validate(ObjectNode field, Optional<String> value) {
    requireValid(FieldValuePolicy.validateConfiguration(field, ""));
    value.ifPresent(text -> requireValid(FieldValuePolicy.validate(text, field, "/_valueConstraints/defaultValue", true)));
  }
  private static void requireValid(List<ValidationFinding> findings) {
    if (!findings.isEmpty()) throw new IllegalStateException(findings.get(0).message());
  }
}
