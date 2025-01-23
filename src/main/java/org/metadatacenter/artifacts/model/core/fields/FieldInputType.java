package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.metadatacenter.model.ModelNodeNames.*;

public enum FieldInputType
{
  TEXTFIELD(FIELD_INPUT_TYPE_TEXTFIELD),
  TEXTAREA(FIELD_INPUT_TYPE_TEXTAREA),
  PHONE_NUMBER(FIELD_INPUT_TYPE_PHONE_NUMBER),
  EMAIL(FIELD_INPUT_TYPE_EMAIL),
  RADIO(FIELD_INPUT_TYPE_RADIO),
  CHECKBOX(FIELD_INPUT_TYPE_CHECKBOX),
  LIST(FIELD_INPUT_TYPE_LIST),
  NUMERIC(FIELD_INPUT_TYPE_NUMERIC),
  TEMPORAL(FIELD_INPUT_TYPE_TEMPORAL),
  ATTRIBUTE_VALUE(FIELD_INPUT_TYPE_ATTRIBUTE_VALUE),
  PAGE_BREAK(FIELD_INPUT_TYPE_PAGE_BREAK),
  SECTION_BREAK(FIELD_INPUT_TYPE_SECTION_BREAK),
  RICHTEXT(FIELD_INPUT_TYPE_RICH_TEXT),
  IMAGE(FIELD_INPUT_TYPE_IMAGE),
  YOUTUBE(FIELD_INPUT_TYPE_YOUTUBE),
  LINK(FIELD_INPUT_TYPE_LINK),
  ROR(FIELD_INPUT_TYPE_EXT_ROR),
  ORCID(FIELD_INPUT_TYPE_EXT_ORCID);

  private final String text;

  FieldInputType(String text) {
    this.text = text;
  }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public boolean isTemporal() { return this == TEMPORAL; }

  public boolean isTextField() { return this == TEXTFIELD; }

  public boolean isTextArea() { return this == TEXTAREA; }

  public boolean isNumeric() { return this == NUMERIC; }

  public boolean isPhoneNumber() { return this == PHONE_NUMBER; }

  public boolean isEmail() { return this == EMAIL; }

  public boolean isRadio() { return this == RADIO; }

  public boolean isCheckbox() { return this == CHECKBOX; }

  public boolean isList() { return this == LIST; }

  public boolean isLink() { return this == LINK; }

  public boolean isRor() { return this == ROR; }

  public boolean isOrcid() { return this == ORCID; }

  public boolean isAttributeValue() { return this == ATTRIBUTE_VALUE; }

  public boolean isStatic() { return this == PAGE_BREAK || this == SECTION_BREAK || this == RICHTEXT || this == IMAGE || this == YOUTUBE; }

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
