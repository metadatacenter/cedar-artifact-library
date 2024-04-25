package org.metadatacenter.artifacts.model.core;

public sealed interface AnnotationValue<T> permits LiteralAnnotationValue, IriAnnotationValue
{
  T getValue();
}
