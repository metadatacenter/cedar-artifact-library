package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;
import org.metadatacenter.model.ModelNodeNames;

public enum ValueType
{
  ONTOLOGY_CLASS(ModelNodeNames.VALUE_CONSTRAINTS_TYPE_ONTOLOGY_CLASS),
  VALUE_SET(ModelNodeNames.VALUE_CONSTRAINTS_TYPE_VALUE_SET);

  private final String text;

  ValueType(String text) {
    this.text = text;
  }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static ValueType fromString(String text) {
    for (ValueType f : ValueType.values()) {
      if (f.text.equalsIgnoreCase(text)) {
        return f;
      }
    }
    throw new IllegalArgumentException("No value type constant with text " + text + " found");
  }
}