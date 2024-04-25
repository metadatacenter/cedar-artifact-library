package org.metadatacenter.artifacts.model.core;

import java.net.URI;

public record IriAnnotationValue(URI value) implements AnnotationValue<URI>
{
  public URI getValue()
  {
    return value;
  }
}
