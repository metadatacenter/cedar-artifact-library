package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;

import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;

public enum XsdNumericDatatype
{
  DECIMAL("xsd:decimal"),
  INTEGER("xsd:int"),
  LONG("xsd:long"),
  BYTE("xsd:byte"),
  SHORT("xsd:short"),
  INT("xsd:int"),
  FLOAT("xsd:float"),
  DOUBLE("xsd:double");

  private final String text;

  XsdNumericDatatype(String text) {
    this.text = text;
  }

  public URI toUri() { return URI.create(XSD_IRI + this.text.substring(this.text.indexOf(":") + 1)); }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static XsdNumericDatatype fromString(String text) {
    for (XsdNumericDatatype n : XsdNumericDatatype.values()) {
      if (n.text.equalsIgnoreCase(text)) {
        return n;
      }
    }
    throw new IllegalArgumentException("No numeric datatype constant " + text + " found");
  }

  @Override public String toString()
  {
    return text;
  }
}