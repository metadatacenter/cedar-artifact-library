package org.metadatacenter.artifacts.model.core;

public sealed interface UI permits TemplateUI, ElementUI, FieldUI
{
  UIType getUIType();

  default boolean isTemplateUI() { return getUIType() == UIType.TEMPLATE_UI; }

  default boolean isElementUI() { return getUIType() == UIType.ELEMENT_UI; }

  default boolean isFieldUI() { return getUIType() == UIType.FIELD_UI; }

  default TemplateUI asTemplateUI()
  {
    if (isTemplateUI())
      return (TemplateUI)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TemplateUI.class.getName());
  }

  default ElementUI asElementUI()
  {
    if (isElementUI())
      return (ElementUI)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + ElementUI.class.getName());
  }

  default FieldUI asFieldUI()
  {
    if (isFieldUI())
      return (FieldUI)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + FieldUI.class.getName());
  }
}