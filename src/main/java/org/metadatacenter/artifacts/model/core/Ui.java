package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

public sealed interface Ui permits TemplateUi, ElementUi, FieldUi
{
  @JsonIgnore UiType getUiType();

  @JsonIgnore default boolean isTemplateUi() { return getUiType() == UiType.TEMPLATE_UI; }

  @JsonIgnore default boolean isElementUi() { return getUiType() == UiType.ELEMENT_UI; }

  @JsonIgnore default boolean isFieldUi() { return getUiType() == UiType.FIELD_UI; }

  default TemplateUi asTemplateUi()
  {
    if (isTemplateUi())
      return (TemplateUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TemplateUi.class.getName());
  }

  default ElementUi asElementUi()
  {
    if (isElementUi())
      return (ElementUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + ElementUi.class.getName());
  }

  default FieldUi asFieldUi()
  {
    if (isFieldUi())
      return (FieldUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + FieldUi.class.getName());
  }
}