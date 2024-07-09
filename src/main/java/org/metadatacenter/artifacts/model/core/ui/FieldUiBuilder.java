package org.metadatacenter.artifacts.model.core.ui;

import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

public sealed interface FieldUiBuilder permits FieldUi.Builder, TemporalFieldUi.TemporalFieldUiBuilder,
  NumericFieldUi.NumericFieldUiBuilder, StaticFieldUi.PageBreakFieldUiBuilder,
  StaticFieldUi.SectionBreakFieldUiBuilder, StaticFieldUi.RichTextFieldUiBuilder,
  StaticFieldUi.ImageFieldUiBuilder, StaticFieldUi.YouTubeFieldUiBuilder

{
  static FieldUiBuilder builder(FieldUi fieldUi)
  {
    // TODO Use typesafe switch when available
    if (fieldUi instanceof TemporalFieldUi)
      return TemporalFieldUi.builder(fieldUi.asTemporalFieldUi());
    else if (fieldUi instanceof NumericFieldUi)
      return NumericFieldUi.builder(fieldUi.asNumericFieldUi());
    else if (fieldUi instanceof StaticFieldUi) {
      StaticFieldUi staticFieldUi = fieldUi.asStaticFieldUi();
      if (staticFieldUi.inputType() == FieldInputType.PAGE_BREAK)
        return StaticFieldUi.pageBreakFieldUiBuilder(staticFieldUi);
      else if (staticFieldUi.inputType() == FieldInputType.SECTION_BREAK)
        return StaticFieldUi.sectionBreakFieldUiBuilder(staticFieldUi);
      else if (staticFieldUi.inputType() == FieldInputType.RICHTEXT)
        return StaticFieldUi.richTextFieldUiBuilder(staticFieldUi);
      else if (staticFieldUi.inputType() == FieldInputType.IMAGE)
        return StaticFieldUi.imageFieldUiBuilder(staticFieldUi);
      else if (staticFieldUi.inputType() == FieldInputType.YOUTUBE)
        return StaticFieldUi.youTubeFieldUiBuilder(staticFieldUi);
      throw new IllegalArgumentException("class " + fieldUi.getClass().getName() + " has no known static builder");
    } else
      return FieldUi.builder(fieldUi);
  }
}
