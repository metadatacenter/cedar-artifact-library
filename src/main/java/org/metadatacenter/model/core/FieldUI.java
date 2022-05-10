package org.metadatacenter.model.core;

public class FieldUI
{
  private final String inputType;
  private final boolean valueRecommendationEnabled;
  private final boolean hidden;

  public FieldUI(String inputType, boolean valueRecommendationEnabled, boolean hidden)
  {
    this.inputType = inputType;
    this.valueRecommendationEnabled = valueRecommendationEnabled;
    this.hidden = hidden;
  }

  public String getInputType()
  {
    return inputType;
  }

  public boolean isValueRecommendationEnabled()
  {
    return valueRecommendationEnabled;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  @Override public String toString()
  {
    return "FieldUI{" + "inputType='" + inputType + '\'' + ", valueRecommendationEnabled=" + valueRecommendationEnabled
      + ", hidden=" + hidden + '}';
  }
}
