package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.model.ModelNodeNames;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;

public non-sealed interface StaticFieldUi extends FieldUi
{
  FieldInputType inputType();

  String _content();

  boolean hidden();

  default boolean isStatic() {return true;}

  default boolean valueRecommendationEnabled() {return false;}

  static StaticFieldUi create(FieldInputType fieldInputType, String content, boolean hidden)
  {
    return new StaticFieldUiRecord(fieldInputType, content, hidden);
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

    private SectionBreakFieldUiBuilder()
    {
    }

    public SectionBreakFieldUiBuilder withContent(String content)
    {
      this.content = content;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.SECTION_BREAK, content, false);
    }
  }

  class RichTextFieldUiBuilder
  {
    private String content;
    private boolean hidden = false;

    private RichTextFieldUiBuilder()
    {
    }

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

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.RICHTEXT, content, hidden);
    }
  }

  class ImageFieldUiBuilder
  {
    private String content;
    private boolean hidden = false;

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

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.IMAGE, content, hidden);
    }
  }

  class YouTubeFieldUiBuilder
  {
    private String content;
    private boolean hidden = false;

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

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.YOUTUBE, content, hidden);
    }
  }

}

record StaticFieldUiRecord(FieldInputType inputType, String _content, boolean hidden) implements StaticFieldUi
{
  public StaticFieldUiRecord
  {

    validateStringFieldNotNull(this, _content, ModelNodeNames.UI_CONTENT);

    if (inputType == null)
      throw new IllegalStateException("Field " + UI_FIELD_INPUT_TYPE + " must set for static  fields in " + this);
  }
}