package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.StaticFieldUi;

public final class RichTextFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final StaticFieldUi.RichTextFieldUiBuilder fieldUiBuilder = StaticFieldUi.richTextFieldUiBuilder();

  public RichTextFieldBuilder() {}

  public RichTextFieldBuilder withContent(String content)
  {
    fieldUiBuilder.withContent(content);
    return this;
  }

  public RichTextFieldBuilder withHidden(boolean hidden)
  {
    fieldUiBuilder.withHidden(hidden);
    return this;
  }

  @Override public FieldSchemaArtifact build()
  {
    withFieldUi(fieldUiBuilder.build());
    return super.build();
  }
}