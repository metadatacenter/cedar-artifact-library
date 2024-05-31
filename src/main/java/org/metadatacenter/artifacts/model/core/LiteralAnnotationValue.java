package org.metadatacenter.artifacts.model.core;

public record LiteralAnnotationValue(String value) implements AnnotationValue<String>
{
  public String getValue()
  {
    return value;
  }
}
