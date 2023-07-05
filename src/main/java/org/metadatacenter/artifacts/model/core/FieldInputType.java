package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;
import org.metadatacenter.model.ModelNodeNames;

public enum FieldInputType
{
  TEXTFIELD(ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD),
  TEXTAREA(ModelNodeNames.FIELD_INPUT_TYPE_TEXTAREA),
  RADIO(ModelNodeNames.FIELD_INPUT_TYPE_RADIO),
  CHECKBOX(ModelNodeNames.FIELD_INPUT_TYPE_CHECKBOX),
  TEMPORAL(ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL),
  EMAIL(ModelNodeNames.FIELD_INPUT_TYPE_EMAIL),
  LIST(ModelNodeNames.FIELD_INPUT_TYPE_LIST),
  NUMERIC(ModelNodeNames.FIELD_INPUT_TYPE_NUMERIC),
  PHONE_NUMBER(ModelNodeNames.FIELD_INPUT_TYPE_PHONE_NUMBER),
  SECTION_BREAK(ModelNodeNames.FIELD_INPUT_TYPE_SECTION_BREAK),
  RICHTEXT(ModelNodeNames.FIELD_INPUT_TYPE_RICH_TEXT),
  IMAGE(ModelNodeNames.FIELD_INPUT_TYPE_IMAGE),
  LINK(ModelNodeNames.FIELD_INPUT_TYPE_LINK),
  YOUTUBE(ModelNodeNames.FIELD_INPUT_TYPE_YOUTUBE),
  ATTRIBUTE_VALUE(ModelNodeNames.FIELD_INPUT_TYPE_ATTRIBUTE_VALUE);

  private final String text;

  FieldInputType(String text) {
    this.text = text;
  }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static FieldInputType fromString(String text) {
    for (FieldInputType f : FieldInputType.values()) {
      if (f.text.equalsIgnoreCase(text)) {
        return f;
      }
    }
    throw new IllegalArgumentException("No field input type constant with text " + text + " found");
  }
}
