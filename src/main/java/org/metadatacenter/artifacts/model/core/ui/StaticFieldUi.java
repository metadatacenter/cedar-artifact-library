package org.metadatacenter.artifacts.model.core.ui;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.model.ModelNodeNames;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;

public non-sealed interface StaticFieldUi extends FieldUi
{
  String _content();

  boolean hidden();

  default boolean recommendedValue() { return false; }

  default boolean valueRecommendationEnabled() { return false; }

  default boolean continuePreviousLine() { return false; }

  static StaticFieldUi create(FieldInputType fieldInputType, String content, boolean hidden, boolean continuePreviousLine)
  {
    return new StaticFieldUiRecord(fieldInputType, content, hidden, continuePreviousLine);
  }

  static SectionBreakFieldUiBuilder sectionBreakFieldUiBuilder()
  {
    return new SectionBreakFieldUiBuilder();
  }

  static RichTextFieldUiBuilder richTextFieldUiBuilder() { return new RichTextFieldUiBuilder(); }

  static ImageFieldUiBuilder imageFieldUiBuilder()
  {
    return new ImageFieldUiBuilder();
  }

  static YouTubeFieldUiBuilder youTubeFieldUiBuilder()
  {
    return new YouTubeFieldUiBuilder();
  }

  class SectionBreakFieldUiBuilder
  {
    private String content;
    private boolean hidden = false;

    private SectionBreakFieldUiBuilder() {}

    public SectionBreakFieldUiBuilder withContent(String content)
    {
      this.content = content;
      return this;
    }

    public SectionBreakFieldUiBuilder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.SECTION_BREAK, content, hidden, false);
    }
  }

  class RichTextFieldUiBuilder
  {
    private String content;
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private RichTextFieldUiBuilder() {}

    public RichTextFieldUiBuilder withContent(String content)
    {
      this.content = content;
      return this;
    }

    public RichTextFieldUiBuilder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public RichTextFieldUiBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      this.continuePreviousLine = continuePreviousLine;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.RICHTEXT, content, hidden, continuePreviousLine);
    }
  }

  class ImageFieldUiBuilder
  {
    private String content;
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private ImageFieldUiBuilder()
    {
    }

    public ImageFieldUiBuilder withContent(String content)
    {
      this.content = content;
      return this;
    }

    public ImageFieldUiBuilder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public ImageFieldUiBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      this.continuePreviousLine = continuePreviousLine;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.IMAGE, content, hidden, continuePreviousLine);
    }
  }

  class YouTubeFieldUiBuilder
  {
    private String content;
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private YouTubeFieldUiBuilder()
    {
    }

    public YouTubeFieldUiBuilder withContent(String content)
    {
      this.content = content;
      return this;
    }

    public YouTubeFieldUiBuilder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public YouTubeFieldUiBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      this.continuePreviousLine = continuePreviousLine;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.YOUTUBE, content, hidden, continuePreviousLine);
    }
  }

}

record StaticFieldUiRecord(FieldInputType inputType, String _content, boolean hidden, boolean continuePreviousLine) implements StaticFieldUi
{
  public StaticFieldUiRecord
  {

    validateStringFieldNotNull(this, _content, ModelNodeNames.UI_CONTENT);

    if (inputType == null)
      throw new IllegalStateException("Field " + UI_FIELD_INPUT_TYPE + " must set for static  fields in " + this);
  }
}