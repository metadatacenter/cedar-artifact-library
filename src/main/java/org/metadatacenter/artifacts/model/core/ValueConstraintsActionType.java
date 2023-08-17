package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;
import org.metadatacenter.model.ModelNodeNames;

import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTION_DELETE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTION_MOVE;

public enum ValueConstraintsActionType
{
  MOVE(VALUE_CONSTRAINTS_ACTION_MOVE),
  DELETE(VALUE_CONSTRAINTS_ACTION_DELETE);

  private final String text;

  ValueConstraintsActionType(String text) {
    this.text = text;
  }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static ValueConstraintsActionType fromString(String text) {
    for (ValueConstraintsActionType f : ValueConstraintsActionType.values()) {
      if (f.text.equals(text)) {
        return f;
      }
    }
    throw new IllegalArgumentException("No value constraints actionType valueType constant with text " + text + " found");
  }

  @Override public String toString()
  {
    return text;
  }
}
