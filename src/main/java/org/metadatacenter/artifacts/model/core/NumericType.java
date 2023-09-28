package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;

import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;

public enum NumericType
{
  DECIMAL("xsd:decimal"),
  INTEGER("xsd:integer"),
  LONG("xsd:long"),
  BYTE("xsd:byte"),
  SHORT("xsd:short"),
  INT("xsd:int"),
  FLOAT("xsd:float"),
  DOUBLE("xsd:double");

  private String text;

  NumericType(String text) {
    this.text = text;
  }

  public URI toURI() { return URI.create(XSD_IRI + this.text.substring(this.text.indexOf(":"))); }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static NumericType fromString(String text) {
    for (NumericType n : NumericType.values()) {
      if (n.text.equalsIgnoreCase(text)) {
        return n;
      }
    }
    throw new IllegalArgumentException("No numeric type constant with text " + text + " found");
  }

  @Override public String toString()
  {
    return text;
  }
}