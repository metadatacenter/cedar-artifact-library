package org.metadatacenter.artifacts.model.core;

import java.util.LinkedHashMap;

public class Annotations
{
  private final LinkedHashMap<String, AnnotationValue> annotations;

  public Annotations(LinkedHashMap<String, AnnotationValue> annotations)
  {
    this.annotations = new LinkedHashMap<>(annotations);
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