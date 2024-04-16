package org.metadatacenter.artifacts.model.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.model.ModelNodeNames;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;

public non-sealed interface StaticFieldUi extends FieldUi
{
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  Optional<String> _content();

  boolean hidden();

  default boolean recommendedValue() { return false; }

  default boolean valueRecommendationEnabled() { return false; }

  default boolean continuePreviousLine() { return false; }

  static StaticFieldUi create(FieldInputType fieldInputType, Optional<String> content, boolean hidden, boolean continuePreviousLine)
  {
    return new StaticFieldUiRecord(fieldInputType, content, hidden, continuePreviousLine);
  }

  static PageBreakFieldUiBuilder pageBreakFieldUiBuilder()
  {
    return new PageBreakFieldUiBuilder();
  }

  static PageBreakFieldUiBuilder pageBreakFieldUiBuilder(StaticFieldUi staticFieldUi)
  {
    return new PageBreakFieldUiBuilder(staticFieldUi);
  }

  static SectionBreakFieldUiBuilder sectionBreakFieldUiBuilder()
  {
    return new SectionBreakFieldUiBuilder();
  }

  static RichTextFieldUiBuilder richTextFieldUiBuilder() { return new RichTextFieldUiBuilder(); }

  static RichTextFieldUiBuilder richTextFieldUiBuilder(StaticFieldUi staticFieldUi)
  {
    return new RichTextFieldUiBuilder(staticFieldUi);
  }

  static ImageFieldUiBuilder imageFieldUiBuilder()
  {
    return new ImageFieldUiBuilder();
  }

  static ImageFieldUiBuilder imageFieldUiBuilder(StaticFieldUi staticFieldUi)
  {
    return new ImageFieldUiBuilder(staticFieldUi);
  }

  static YouTubeFieldUiBuilder youTubeFieldUiBuilder()
  {
    return new YouTubeFieldUiBuilder();
  }

  static YouTubeFieldUiBuilder youTubeFieldUiBuilder(StaticFieldUi staticFieldUi)
  {
    return new YouTubeFieldUiBuilder(staticFieldUi);
  }

  class PageBreakFieldUiBuilder
  {
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;

    private PageBreakFieldUiBuilder() {}

    private PageBreakFieldUiBuilder(StaticFieldUi staticFieldUi)
    {
      this.content = staticFieldUi._content();
      this.hidden = staticFieldUi.hidden();
    }

    public PageBreakFieldUiBuilder withContent(String content)
    {
      this.content = Optional.ofNullable(content);
      return this;
    }

    public PageBreakFieldUiBuilder withHidden(boolean hidden)
    {
      this.hidden = hidden;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.PAGE_BREAK, content, hidden, false);
    }
  }

  class SectionBreakFieldUiBuilder
  {
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;

    private SectionBreakFieldUiBuilder() {}

    private SectionBreakFieldUiBuilder(StaticFieldUi staticFieldUi)
    {
      this.content = staticFieldUi._content();
      this.hidden = staticFieldUi.hidden();
    }

    public SectionBreakFieldUiBuilder withContent(String content)
    {
      this.content = Optional.ofNullable(content);
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
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private RichTextFieldUiBuilder() {}

    private RichTextFieldUiBuilder(StaticFieldUi staticFieldUi)
    {
      this.content = staticFieldUi._content();
      this.hidden = staticFieldUi.hidden();
      this.continuePreviousLine = staticFieldUi.continuePreviousLine();
    }

    public RichTextFieldUiBuilder withContent(String content)
    {
      this.content = Optional.ofNullable(content);
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
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private ImageFieldUiBuilder()
    {
    }

    private ImageFieldUiBuilder(StaticFieldUi staticFieldUi)
    {
      this.content = staticFieldUi._content();
      this.hidden = staticFieldUi.hidden();
      this.continuePreviousLine = staticFieldUi.continuePreviousLine();
    }

    public ImageFieldUiBuilder withContent(String content)
    {
      this.content = Optional.ofNullable(content);
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
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;

    private YouTubeFieldUiBuilder()
    {
    }

    private YouTubeFieldUiBuilder(StaticFieldUi staticFieldUi)
    {
      this.content = staticFieldUi._content();
      this.hidden = staticFieldUi.hidden();
      this.continuePreviousLine = staticFieldUi.continuePreviousLine();
    }

    public YouTubeFieldUiBuilder withContent(String content)
    {
      this.content = Optional.ofNullable(content);
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

record StaticFieldUiRecord(FieldInputType inputType, Optional<String> _content, boolean hidden, boolean continuePreviousLine) implements StaticFieldUi
{
  public StaticFieldUiRecord
  {

    validateOptionalFieldNotNull(this, _content, ModelNodeNames.UI_CONTENT);

    if (inputType == null)
      throw new IllegalStateException("Field " + UI_FIELD_INPUT_TYPE + " must set for static  fields in " + this);
  }
}