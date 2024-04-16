package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.RadioFieldInstance;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class RadioFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public RadioFieldInstanceBuilder() {}

  public RadioFieldInstanceBuilder(RadioFieldInstance radioFieldInstance) {
    super(radioFieldInstance);
  }

  public RadioFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public RadioFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
