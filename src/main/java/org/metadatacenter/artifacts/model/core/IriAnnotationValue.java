package org.metadatacenter.artifacts.model.core;

import java.net.URI;

public final class IriAnnotationValue implements AnnotationValue<URI>
{
  private final URI value;

  public IriAnnotationValue(URI value)
  {
    this.value = value;
  }

  public URI getValue()
  {
    return value;
  }
}
