package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ListFieldInstance;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class ListFieldInstanceBuilder extends FieldInstanceArtifactBuilder
{
  public ListFieldInstanceBuilder() {}

  public ListFieldInstanceBuilder(ListFieldInstance listFieldInstance) {
    super(listFieldInstance);
  }

  public ListFieldInstanceBuilder withValue(String value)
  {
    super.withJsonLdValue(value);
    return this;
  }

  public ListFieldInstanceBuilder withLanguage(String language)
  {
    super.withLanguage(language);
    return this;
  }

  public FieldInstanceArtifact build()
  {
    return super.build();
  }
}
