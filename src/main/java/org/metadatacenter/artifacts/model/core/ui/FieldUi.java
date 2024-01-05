package org.metadatacenter.artifacts.model.core.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

public sealed interface FieldUi extends Ui permits TemporalFieldUi, NumericFieldUi, StaticFieldUi, FieldUiRecord
{
  FieldInputType inputType();

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  boolean hidden();

  @JsonIgnore
  default boolean isStatic() { return inputType().isStatic(); }

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  boolean valueRecommendationEnabled();

  default TemporalFieldUi asTemporalFieldUi()
  {
    if (inputType().isTemporal())
      return (TemporalFieldUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TemporalFieldUi.class.getName());
  }

  default NumericFieldUi asNumericFieldUi()
  {
    if (inputType().isNumeric())
      return (NumericFieldUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + NumericFieldUi.class.getName());
  }

  default StaticFieldUi asStaticFieldUi()
  {
    if (inputType().isStatic())
      return (StaticFieldUi)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + StaticFieldUi.class.getName());
  }

  @JsonIgnore default boolean isTemporal() {return inputType() == FieldInputType.TEMPORAL;}

  @JsonIgnore default boolean isNumeric() {return inputType() == FieldInputType.NUMERIC;}

  @JsonIgnore default boolean isTextField() {return inputType() == FieldInputType.TEXTFIELD;}

  @JsonIgnore default boolean isTextarea() {return inputType() == FieldInputType.TEXTAREA;}

  @JsonIgnore default boolean isRadio() {return inputType() == FieldInputType.RADIO;}

  @JsonIgnore default boolean isCheckbox() {return inputType() == FieldInputType.CHECKBOX;}

  @JsonIgnore default boolean isEmail() {return inputType() == FieldInputType.EMAIL;}

  @JsonIgnore default boolean isList() {return inputType() == FieldInputType.LIST;}

  @JsonIgnore default boolean isPhoneNumber() {return inputType() == FieldInputType.PHONE_NUMBER;}

  @JsonIgnore default boolean isLink() {return inputType() == FieldInputType.LINK;}

  @JsonIgnore default boolean isAttributeValue() {return inputType() == FieldInputType.ATTRIBUTE_VALUE;}

  @JsonIgnore default boolean isRichText() {return inputType() == FieldInputType.RICHTEXT;}

  @JsonIgnore default boolean isSectionBreak() {return inputType() == FieldInputType.SECTION_BREAK;}

  @JsonIgnore default boolean isImage() {return inputType() == FieldInputType.IMAGE;}

  @JsonIgnore default boolean isYouTube() {return inputType() == FieldInputType.YOUTUBE;}

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
    if (inputType == null)
      throw new IllegalArgumentException("Field input type is null in " + FieldUi.class.getName());

    if (inputType.isStatic())
      throw new IllegalArgumentException("The " + StaticFieldUi.class.getName() + " class should be used for static field UIs");

    if (inputType.isTemporal())
      throw new IllegalArgumentException("The " + TemporalFieldUi.class.getName() + " class should be used for temporal field UIs");
  }
}

