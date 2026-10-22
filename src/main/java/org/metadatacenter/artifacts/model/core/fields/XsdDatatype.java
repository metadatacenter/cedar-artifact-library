package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;

import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;

public enum XsdDatatype
{
  STRING("xsd:string"), ANY_URI("xsd:anyURI"), DATE("xsd:date"), DATETIME("xsd:dateTime"), TIME("xsd:time"), DECIMAL(
  "xsd:decimal"), LONG("xsd:long"), BYTE("xsd:byte"), SHORT("xsd:short"), INT("xsd:int"), FLOAT("xsd:float"), DOUBLE(
  "xsd:double");

  private final String text;

  XsdDatatype(String text)
  {
    this.text = text;
  }

  public URI toUri() { return URI.create(XSD_IRI + this.text.substring(this.text.indexOf(":") + 1)); }

  @JsonValue public String getText()
  {
    return this.text;
  }

  public static XsdDatatype fromString(String datatype)
  {
    for (XsdDatatype d : XsdDatatype.values()) {
      if (d.text.equals(datatype) || d.toUri().toString().equals(datatype)) {
        return d;
      }
    }
    throw new IllegalArgumentException("No XSD datatype constant " + datatype + " found");
  }

  // XSD names are case-sensitive, and this test must agree with fromString, which every caller
  // invokes on a datatype it reports as known.
  public static boolean isKnownXsdDatatype(String datatype)
  {
    for (XsdDatatype d : XsdDatatype.values()) {
      if (d.toUri().toString().equals(datatype) || d.text.equals(datatype)) {
        return true;
      }
    }
    return false;
  }

  @Override public String toString()
  {
    return text;
  }
}
