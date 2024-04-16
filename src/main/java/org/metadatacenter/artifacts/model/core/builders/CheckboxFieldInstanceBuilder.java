package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class CheckboxFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public CheckboxFieldInstanceBuilder() {}

  public CheckboxFieldInstanceBuilder(FieldInstanceArtifact fieldInstanceArtifact) {
    super(fieldInstanceArtifact);
  }

  public CheckboxFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public CheckboxFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }

  public CheckboxFieldInstanceBuilder withNotation(String notation)
  {
    super.withNotation(notation);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
