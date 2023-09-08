package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.StaticFieldUi;

public final class SectionBreakFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final StaticFieldUi.SectionBreakFieldUiBuilder fieldUiBuilder = StaticFieldUi.sectionBreakFieldUiBuilder();

  public SectionBreakFieldBuilder() {}

  public SectionBreakFieldBuilder withContent(String content)
  {
    fieldUiBuilder.withContent(content);
    return this;
  }

  public SectionBreakFieldBuilder withHidden(boolean hidden)
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