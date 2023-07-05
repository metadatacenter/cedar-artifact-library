package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TemporalType
{
  DATE("xsd:date"),
  DATETIME("xsd:dateTime"),
  TIME("xsd:time");

  private String text;

  TemporalType(String text) {
    this.text = text;
  }

  @JsonValue
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