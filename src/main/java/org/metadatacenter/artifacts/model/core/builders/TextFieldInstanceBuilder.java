package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class TextFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public TextFieldInstanceBuilder() {}

  public TextFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }
  
  public TextFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public TextFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
