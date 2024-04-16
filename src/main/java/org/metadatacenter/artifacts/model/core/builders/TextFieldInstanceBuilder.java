package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TextFieldInstance;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class TextFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public TextFieldInstanceBuilder() {}

  public TextFieldInstanceBuilder(TextFieldInstance textFieldInstance) {
    super(textFieldInstance);
  }

  public TextFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
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
