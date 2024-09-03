package org.metadatacenter.artifacts.model.core.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.model.ModelNodeNames;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;

public non-sealed interface StaticFieldUi extends FieldUi
{
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Optional<String> _content();

  boolean hidden();

  default boolean recommendedValue() { return false; }

  default boolean valueRecommendationEnabled() { return false; }

  default boolean continuePreviousLine() { return false; }

  Optional<Integer> width();

  Optional<Integer> height();

  static StaticFieldUi create(FieldInputType fieldInputType, Optional<String> content, boolean hidden,
    boolean continuePreviousLine, Optional<Integer> width, Optional<Integer> height)
  {
    return new StaticFieldUiRecord(fieldInputType, content, hidden, continuePreviousLine, width, height);
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

  static SectionBreakFieldUiBuilder sectionBreakFieldUiBuilder(StaticFieldUi staticFieldUi)
  {
    return new SectionBreakFieldUiBuilder(staticFieldUi);
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

  final class PageBreakFieldUiBuilder implements FieldUiBuilder
  {
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;
    private boolean valueRecommendationEnabled = false;

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

    public PageBreakFieldUiBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      this.hidden = hidden;
      return this;
    }

    public PageBreakFieldUiBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.PAGE_BREAK, content, hidden, false, Optional.empty(),
        Optional.empty());
    }
  }

  final class SectionBreakFieldUiBuilder implements FieldUiBuilder
  {
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;
    private boolean valueRecommendationEnabled = false;

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

    public SectionBreakFieldUiBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
      return this;
    }

    public SectionBreakFieldUiBuilder withContinuePreviousLine(boolean continuePreviousLine)
    {
      this.continuePreviousLine = continuePreviousLine;
      return this;
    }


    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.SECTION_BREAK, content, hidden, false, Optional.empty(),
        Optional.empty());
    }
  }

  final class RichTextFieldUiBuilder implements FieldUiBuilder
  {
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;
    private boolean valueRecommendationEnabled = false;

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

    public RichTextFieldUiBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.RICHTEXT, content, hidden, continuePreviousLine, Optional.empty(),
        Optional.empty());
    }
  }

  final class ImageFieldUiBuilder implements FieldUiBuilder
  {
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean continuePreviousLine = false;
    private boolean valueRecommendationEnabled = false;

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

    public ImageFieldUiBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.IMAGE, content, hidden, continuePreviousLine, Optional.empty(),
        Optional.empty());
    }
  }

  final class YouTubeFieldUiBuilder implements FieldUiBuilder
  {
    private Optional<String> content = Optional.empty();
    private boolean hidden = false;
    private boolean valueRecommendationEnabled = false;
    private boolean continuePreviousLine = false;
    private Optional<Integer> width = Optional.empty();
    private Optional<Integer> height = Optional.empty();

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

    public YouTubeFieldUiBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled)
    {
      this.valueRecommendationEnabled = valueRecommendationEnabled;
      return this;
    }

    public YouTubeFieldUiBuilder withWidth(Integer width)
    {
      this.width = Optional.ofNullable(width);
      return this;
    }

    public YouTubeFieldUiBuilder withHeight(Integer height)
    {
      this.height = Optional.ofNullable(height);
      return this;
    }

    public StaticFieldUi build()
    {
      return new StaticFieldUiRecord(FieldInputType.YOUTUBE, content, hidden, continuePreviousLine, width, height);
    }
  }

}

record StaticFieldUiRecord(FieldInputType inputType, Optional<String> _content, boolean hidden,
                           boolean continuePreviousLine, Optional<Integer> width, Optional<Integer> height) implements StaticFieldUi
{
  public StaticFieldUiRecord
  {
    validateOptionalFieldNotNull(this, _content, ModelNodeNames.UI_CONTENT);

    if (inputType == null)
      throw new IllegalStateException("Field " + UI_FIELD_INPUT_TYPE + " must set for static  fields in " + this);
  }
}