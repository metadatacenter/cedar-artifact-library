package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class TextAreaFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public TextAreaFieldInstanceBuilder() {}

  public TextAreaFieldInstanceBuilder withValue(String jsonLdValue)
  {
    super.withJsonLdValue(jsonLdValue);
    return this;
  }

  public TextAreaFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public TextAreaFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
