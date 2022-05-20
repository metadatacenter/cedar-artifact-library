package org.metadatacenter.model.core;

public class LiteralValueConstraint
{
  private final String label;
  private final boolean selectedByDefault;

  public LiteralValueConstraint(String label, boolean selectedByDefault)
  {
    this.label = label;
    this.selectedByDefault = selectedByDefault;
  }

  public String getLabel()
  {
    return label;
  }

  public boolean isSelectedByDefault()
  {
    return selectedByDefault;
  }

  @Override public String toString()
  {
    return "LiteralValueConstraint{" + "label='" + label + '\'' + ", selectedByDefault=" + selectedByDefault + '}';
  }
}
