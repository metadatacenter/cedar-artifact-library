package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.StaticFieldUi;

public final class ImageFieldBuilder extends FieldSchemaArtifactBuilder
{
  private final StaticFieldUi.ImageFieldUiBuilder fieldUiBuilder = StaticFieldUi.imageFieldUiBuilder();

  public ImageFieldBuilder() {}

  public ImageFieldBuilder withContent(String content)
  {
    fieldUiBuilder.withContent(content);
    return this;
  }

  public ImageFieldBuilder withHidden(boolean hidden)
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