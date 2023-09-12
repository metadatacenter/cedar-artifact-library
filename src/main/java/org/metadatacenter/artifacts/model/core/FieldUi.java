package org.metadatacenter.artifacts.model.core;

public sealed interface FieldUi extends Ui permits TemporalFieldUi, NumericFieldUi, StaticFieldUi, FieldUiRecord
{
  FieldInputType inputType();

  boolean hidden();

  default boolean isStatic() { return false; }

  boolean valueRecommendationEnabled();

  default TemporalFieldUi asTemporalFieldUi()
  {
    if (inputType().isTemporal())
      return (TemporalFieldUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TemporalFieldUi.class.getName());
  }

  default StaticFieldUi asStaticFieldUi()
  {
    if (inputType().isStatic())
      return (StaticFieldUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + StaticFieldUi.class.getName());
  }

  default boolean isTemporal() { return inputType() == FieldInputType.TEMPORAL; }

  default boolean isNumeric() { return inputType() == FieldInputType.NUMERIC; }

  default boolean isTextField() { return inputType() == FieldInputType.TEXTFIELD; }

  default boolean isTextarea() { return inputType() == FieldInputType.TEXTAREA; }

  default boolean isRadio() { return inputType() == FieldInputType.RADIO; }

  default boolean isCheckbox() { return inputType() == FieldInputType.CHECKBOX; }

  default boolean isEmail() { return inputType() == FieldInputType.EMAIL; }

  default boolean isList() { return inputType() == FieldInputType.LIST; }

  default boolean isPhoneNumber() { return inputType() == FieldInputType.PHONE_NUMBER; }

  default boolean isLink() { return inputType() == FieldInputType.LINK; }

  default boolean isAttributeValue() { return inputType() == FieldInputType.ATTRIBUTE_VALUE; }

  default boolean isRichText() { return inputType() == FieldInputType.RICHTEXT; }

  default boolean isSectionBreak() { return inputType() == FieldInputType.SECTION_BREAK; }

  default boolean isImage() { return inputType() == FieldInputType.IMAGE; }

  default boolean isYouTube() { return inputType() == FieldInputType.YOUTUBE; }

  static FieldUi create(FieldInputType fieldInputType, boolean hidden, boolean valueRecommendationEnabled)
  {
    return new FieldUiRecord(fieldInputType, hidden, valueRecommendationEnabled);
  }

  static Builder builder()
  {
    return new Builder();
  }

  class Builder
  {
    private FieldInputType inputType;
    private boolean hidden = false;
    private boolean valueRecommendationEnabled = false;

    private Builder()
    {
    }

    public Builder withInputType(FieldInputType inputType)
    {
      this.inputType = inputType;
      return this;
    }

    public Builder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
      return this;
    }

    public Builder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }


    public FieldUi build()
    {
      return new FieldUiRecord(inputType, hidden, valueRecommendationEnabled);
    }
  }
}

record FieldUiRecord(FieldInputType inputType, boolean hidden, boolean valueRecommendationEnabled) implements FieldUi
{
  public FieldUiRecord {
    if (inputType.isStatic())
      throw new IllegalArgumentException("The " + StaticFieldUi.class.getName() + " class should be used for static field UIs");

    if (inputType.isTemporal())
      throw new IllegalArgumentException("The " + TemporalFieldUi.class.getName() + " class should be used for temporal field UIs");
  }
}


