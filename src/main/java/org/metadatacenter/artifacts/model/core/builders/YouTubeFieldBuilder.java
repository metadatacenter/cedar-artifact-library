package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.StaticFieldUi;

public final class YouTubeFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final StaticFieldUi.YouTubeFieldUiBuilder fieldUiBuilder = StaticFieldUi.youTubeFieldUiBuilder();

  public YouTubeFieldBuilder() {}

  public YouTubeFieldBuilder withContent(String content)
  {
    fieldUiBuilder.withContent(content);
    return this;
  }

  public YouTubeFieldBuilder withHidden(boolean hidden)
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