package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;
import org.metadatacenter.model.ModelNodeNames;

import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_ATTRIBUTE_VALUE;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_CHECKBOX;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_EMAIL;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_IMAGE;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_LINK;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_LIST;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_NUMERIC;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_PHONE_NUMBER;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_RADIO;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_RICH_TEXT;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_SECTION_BREAK;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_TEXTAREA;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_YOUTUBE;

public enum FieldInputType
{
  TEXTFIELD(FIELD_INPUT_TYPE_TEXTFIELD),
  TEXTAREA(FIELD_INPUT_TYPE_TEXTAREA),
  PHONE_NUMBER(FIELD_INPUT_TYPE_PHONE_NUMBER),
  EMAIL(FIELD_INPUT_TYPE_EMAIL),
  RADIO(FIELD_INPUT_TYPE_RADIO),
  CHECKBOX(FIELD_INPUT_TYPE_CHECKBOX),
  LIST(FIELD_INPUT_TYPE_LIST),
  LINK(FIELD_INPUT_TYPE_LINK),

  NUMERIC(FIELD_INPUT_TYPE_NUMERIC),

  TEMPORAL(FIELD_INPUT_TYPE_TEMPORAL),

  ATTRIBUTE_VALUE(FIELD_INPUT_TYPE_ATTRIBUTE_VALUE),

  SECTION_BREAK(FIELD_INPUT_TYPE_SECTION_BREAK),
  RICHTEXT(FIELD_INPUT_TYPE_RICH_TEXT),
  IMAGE(FIELD_INPUT_TYPE_IMAGE),
  YOUTUBE(FIELD_INPUT_TYPE_YOUTUBE);


  private final String text;

  FieldInputType(String text) {
    this.text = text;
  }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public boolean isTemporal() { return this == TEMPORAL; }

  public boolean isNumeric() { return this == NUMERIC; }

  public boolean isStatic() { return this == SECTION_BREAK || this == RICHTEXT || this == IMAGE || this == YOUTUBE; }

  public static FieldInputType fromString(String text) {
    for (FieldInputType f : FieldInputType.values()) {
      if (f.text.equalsIgnoreCase(text)) {
        return f;
      }
    }
    throw new IllegalArgumentException("No field input type constant with text " + text + " found");
  }

  @Override public String toString()
  {
    return text;
  }
}
