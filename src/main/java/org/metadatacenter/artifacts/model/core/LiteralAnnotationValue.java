package org.metadatacenter.artifacts.model.core;

public final class LiteralAnnotationValue implements AnnotationValue<String>
{
  private final String value;

  public LiteralAnnotationValue(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
}
