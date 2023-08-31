package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;

public non-sealed interface StaticFieldUi extends FieldUi
{
  FieldInputType inputType();

  String _content();

  boolean hidden();

  default boolean isStatic() { return true; }

  default boolean valueRecommendationEnabled() { return false; }

  static StaticFieldUi create(FieldInputType fieldInputType, String content, boolean hidden)
  {
    return new StaticFieldUiRecord(fieldInputType, content, hidden);
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder
  {
    private FieldInputType fieldInputType;
    private String content;
    private boolean hidden = false;

    private Builder()
    {
    }

    public Builder withFieldInputType(FieldInputType fieldInputType)
    {
      this.fieldInputType = fieldInputType;
      return this;
    }

    public Builder withContent(String content)
    {
      this.content = content;
      return this;
    }


    public Builder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(fieldInputType, content, hidden);
    }
  }
}

record StaticFieldUiRecord(FieldInputType inputType, String _content, boolean hidden) implements StaticFieldUi
{
  public StaticFieldUiRecord {

    validateStringFieldNotNull(this, _content, ModelNodeNames.UI_CONTENT);

    if (inputType == null)
      throw new IllegalStateException("Field " + UI_FIELD_INPUT_TYPE + " must set for static  fields in " + this);
  }
}