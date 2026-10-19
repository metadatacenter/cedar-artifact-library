package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.metadatacenter.model.validation.FieldValuePolicy;

/** Datatype wrappers use the same default policy as raw-schema validation. */
final class DefaultValueValidation {
  private DefaultValueValidation() {}
  static void requireValid(String inputType, String value) {
    var field = JsonNodeFactory.instance.objectNode();
    field.putObject("_ui").put("inputType", inputType);
    field.putObject("_valueConstraints");
    var findings = FieldValuePolicy.validate(value, field, "/_valueConstraints/defaultValue", true);
    if (!findings.isEmpty()) throw new IllegalStateException(findings.get(0).message());
  }
}
