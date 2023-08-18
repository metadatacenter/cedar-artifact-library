package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;
import static org.metadatacenter.model.ModelNodeNames.UI_INPUT_TIME_FORMAT;
import static org.metadatacenter.model.ModelNodeNames.UI_TEMPORAL_GRANULARITY;
import static org.metadatacenter.model.ModelNodeNames.UI_TIMEZONE_ENABLED;

public record FieldUI(FieldInputType inputType, boolean valueRecommendationEnabled, boolean hidden,
                      Optional<Boolean> timeZoneEnabled, Optional<TemporalGranularity> temporalGranularity,
                      Optional<InputTimeFormat> inputTimeFormat, Optional<String> content) implements UI
{
  public FieldUI {
    if (inputType == null)
      throw new IllegalStateException("Field " + UI_FIELD_INPUT_TYPE + " must set in " + this);

    validateOptionalFieldNotNull(this, timeZoneEnabled, UI_TIMEZONE_ENABLED);
    validateOptionalFieldNotNull(this, temporalGranularity, UI_TEMPORAL_GRANULARITY);
    validateOptionalFieldNotNull(this, inputTimeFormat, UI_INPUT_TIME_FORMAT);

    if (inputType == FieldInputType.TEMPORAL) {
      if (!temporalGranularity.isPresent())
        throw new IllegalStateException(
          "Field " + UI_TEMPORAL_GRANULARITY + " must set for temporal fields in " + this);

      // TODO Disable for moment until verify with Matthew that he is adding this to temporal fields
      //      if (!inputTimeFormat.isPresent())
      //        throw new IllegalStateException(
      //          "Field " + UI_INPUT_TIME_FORMAT + " must be set for temporal fields in " + this);

    } else { // Non-temporal fields
      if (timeZoneEnabled.isPresent())
        throw new IllegalStateException(
          "Field " + UI_TIMEZONE_ENABLED + " cannot be set for fields of valueType " + inputType + " in " + this);

      if (temporalGranularity.isPresent())
        throw new IllegalStateException(
          "Field " + UI_TEMPORAL_GRANULARITY + " cannot be set for fields of valueType " + inputType + " in " + this);

      if (inputTimeFormat.isPresent())
        throw new IllegalStateException(
          "Field " + UI_INPUT_TIME_FORMAT + " cannot be set for fields of valueType " + inputType + " in " + this);
    }

    // TODO Disable for moment until verify with Matthew that he is adding this to temporal fields
    //    if (inputType != FieldInputType.TEXTFIELD) {
    //      if (valueRecommendationEnabled)
    //        throw new IllegalStateException(
    //          "Field " + UI_VALUE_RECOMMENDATION_ENABLED + " cannot be set for fields of valueType " + inputType + " in " + this);
    //    }

  }

  @Override public UIType getUIType() { return UIType.FIELD_UI; }

  public boolean isTextField() {return inputType == FieldInputType.TEXTFIELD;}

  public boolean isTextarea() {return inputType == FieldInputType.TEXTAREA;}

  public boolean isRadio() {return inputType == FieldInputType.RADIO;}

  public boolean isCheckbox() {return inputType == FieldInputType.CHECKBOX;}

  public boolean isTemporal() {return inputType == FieldInputType.TEMPORAL;}

  public boolean isEmail() {return inputType == FieldInputType.EMAIL;}

  public boolean isList() {return inputType == FieldInputType.LIST;}

  public boolean isNumeric() {return inputType == FieldInputType.NUMERIC;}

  public boolean isPhoneNumber() {return inputType == FieldInputType.PHONE_NUMBER;}

  public boolean isSectionBreak() {return inputType == FieldInputType.SECTION_BREAK;}

  public boolean isRichText() {return inputType == FieldInputType.RICHTEXT;}

  public boolean isImage() {return inputType == FieldInputType.IMAGE;}

  public boolean isLink() {return inputType == FieldInputType.LINK;}

  public boolean isYouTube() {return inputType == FieldInputType.YOUTUBE;}

  public boolean isAttributeValue() {return inputType == FieldInputType.ATTRIBUTE_VALUE;}

  public boolean isValueRecommendationEnabled()
  {
    return valueRecommendationEnabled;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public boolean isStatic() {return isRichText() || isImage() || isYouTube();}

  public Optional<Boolean> getTimeZoneEnabled()
  {
    return timeZoneEnabled;
  }

  public static Builder builder()
  {
    return new Builder();
  }

  public static class Builder
  {
    private FieldInputType inputType;
    private boolean valueRecommendationEnabled = false;
    private boolean hidden = false;
    private Optional<Boolean> timeZoneEnabled = Optional.empty();
    private Optional<TemporalGranularity> temporalGranularity = Optional.empty();
    private Optional<InputTimeFormat> inputTimeFormat = Optional.empty();
    private Optional<String> content = Optional.empty();

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

    public Builder withTimeZoneEnabled(boolean timeZoneEnabled)
    {
      this.timeZoneEnabled = Optional.of(timeZoneEnabled);
      return this;
    }

    public Builder withTemporalGranularity(TemporalGranularity temporalGranularity)
    {
      this.temporalGranularity = Optional.ofNullable(temporalGranularity);
      return this;
    }

    public Builder withInputTimeFormat(InputTimeFormat inputTimeFormat)
    {
      this.inputTimeFormat = Optional.ofNullable(inputTimeFormat);
      return this;
    }

    public Builder withContent(String content)
    {
      this.content = Optional.ofNullable(content);
      return this;
    }

    public FieldUI build()
    {
      return new FieldUI(inputType, valueRecommendationEnabled, hidden, timeZoneEnabled, temporalGranularity,
        inputTimeFormat, content);
    }
  }
}


