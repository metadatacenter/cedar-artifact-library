package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Set;

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
  ORCID(FIELD_INPUT_TYPE_EXT_ORCID),
  PFAS(FIELD_INPUT_TYPE_EXT_PFAS),
  RRID(FIELD_INPUT_TYPE_EXT_RRID),
  PUBMED(FIELD_INPUT_TYPE_EXT_PUBMED),
  NIH_GRANT_ID(FIELD_INPUT_TYPE_EXT_NIH_GRANT_ID),
  DOI(FIELD_INPUT_TYPE_EXT_DOI);

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


  public boolean isAttributeValue() { return this == ATTRIBUTE_VALUE; }

  public boolean isStatic() { return this == PAGE_BREAK || this == SECTION_BREAK || this == RICHTEXT || this == IMAGE || this == YOUTUBE; }

  /**
   * Whether a field of this type records a requirement.
   *
   * <p>CEDAR keeps a requirement in the field's own {@code _valueConstraints}, and two kinds of
   * field have none. A static field shows something rather than collecting it. An attribute-value
   * field describes fields whose names a form-filler supplies, so the template has no property to
   * constrain and carries {@code additionalProperties} in place of one — which is why
   * {@link org.metadatacenter.artifacts.model.core.ParentSchemaArtifact} excludes both kinds from
   * the JSON Schema {@code required} array as well.
   *
   * <p>Asked by the YAML reader. The YAML form keeps a requirement on the child, in its {@code
   * configuration:} block, where JSON keeps it inside the field; that block exists for every kind of
   * child, so a YAML document can declare a requirement JSON has nowhere to carry. One written
   * before this rule existed says the field is required, and honouring it would put in the model
   * something no writer emits — and a form rendered from the stored JSON would not enforce it.
   *
   * <p>{@code AttributeValueField.Builder} has always refused a requirement, by making {@code
   * withRequiredValue} and {@code withRecommendedValue} no-ops, so a caller cannot set one either.
   * Between the two the renderer needs no guard of its own: a model that reached it carrying a
   * requirement on this type would have to have been constructed through {@code create} directly.
   */
  public boolean recordsRequirement() { return !(isStatic() || isAttributeValue()); }

  public boolean isIri() { return IRI_TYPES.contains(this); }

  private static final Set<FieldInputType> IRI_TYPES = Set.of(LINK, ROR, ORCID, PFAS, RRID, PUBMED, DOI, NIH_GRANT_ID);

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
