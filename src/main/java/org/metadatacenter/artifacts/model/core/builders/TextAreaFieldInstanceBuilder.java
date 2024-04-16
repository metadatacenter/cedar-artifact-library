package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TextAreaField;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class TextAreaFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public TextAreaFieldInstanceBuilder() {}

  public TextAreaFieldInstanceBuilder(FieldInstanceArtifact fieldInstanceArtifact) {
    super(fieldInstanceArtifact);
  }

  public TextAreaFieldInstanceBuilder withValue(String jsonLdValue)
  {
    super.withJsonLdValue(jsonLdValue);
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
