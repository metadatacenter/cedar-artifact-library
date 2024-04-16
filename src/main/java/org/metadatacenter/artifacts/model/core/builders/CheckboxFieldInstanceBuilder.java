package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.CheckboxFieldInstance;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class CheckboxFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public CheckboxFieldInstanceBuilder() {}

  public CheckboxFieldInstanceBuilder(CheckboxFieldInstance checkboxFieldInstance) {
    super(checkboxFieldInstance);
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

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
