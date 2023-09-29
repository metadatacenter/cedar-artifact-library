package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;

import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;

public enum TemporalType
{
  DATE("xsd:date"),
  DATETIME("xsd:dateTime"),
  TIME("xsd:time");

  private String text;

  TemporalType(String text) {
    this.text = text;
  }

  public URI toURI() { return URI.create(XSD_IRI + this.text.substring(this.text.indexOf(":") + 1)); }

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

  @Override public String toString()
  {
    return text;
  }
}