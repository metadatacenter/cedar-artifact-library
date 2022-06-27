package org.metadatacenter.model.core;

public enum TemporalType
{
  DATE("xsd:date"),
  DATETIME("xsd:time"),
  TIME("xsd:time");

  private String text;

  TemporalType(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  public static TemporalType fromString(String text) {
    for (TemporalType t : TemporalType.values()) {
      if (t.text.equalsIgnoreCase(text)) {
        return t;
      }
    }
    throw new IllegalArgumentException("No temporal type constant with text " + text + " found");
  }
}