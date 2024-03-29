package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;

import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;

public enum XsdTemporalDatatype
{
  DATE("xsd:date"),
  DATETIME("xsd:dateTime"),
  TIME("xsd:time");

  private final String text;

  XsdTemporalDatatype(String text) {
    this.text = text;
  }

  public URI toUri() { return URI.create(XSD_IRI + this.text.substring(this.text.indexOf(":") + 1)); }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static XsdTemporalDatatype fromString(String text) {
    for (XsdTemporalDatatype t : XsdTemporalDatatype.values()) {
      if (t.text.equalsIgnoreCase(text)) {
        return t;
      }
    }
    throw new IllegalArgumentException("No temporal datatype constant " + text + " found");
  }

  @Override public String toString()
  {
    return text;
  }
}