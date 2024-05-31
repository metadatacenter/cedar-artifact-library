package org.metadatacenter.artifacts.model.core;

import java.util.LinkedHashMap;

public record Annotations(LinkedHashMap<String, AnnotationValue> annotations)
{
  public LinkedHashMap<String, AnnotationValue> annotations()
  {
    return new LinkedHashMap<>(annotations);
  }

  public static Builder builder() { return new Builder(); }

  static final class Builder
  {
    private LinkedHashMap<String, AnnotationValue> annotations = new LinkedHashMap<>();

    private Builder() {}

    public Builder withAnnotation(String annotationName, AnnotationValue annotationValue)
    {
      annotations.put(annotationName, annotationValue);

      return this;
    }

    public Annotations build()
    {
      return new Annotations(this.annotations);
    }
  }
}