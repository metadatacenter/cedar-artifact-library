package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

import java.net.URI;

import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;

public enum XsdDatatype
{
  STRING("xsd:string"), ANY_URI("xsd:anyUri"), DATE("xsd:date"), DATETIME("xsd:dateTime"), TIME("xsd:time"), DECIMAL(
  "xsd:decimal"), INTEGER("xsd:int"), LONG("xsd:long"), BYTE("xsd:byte"), SHORT("xsd:short"), INT("xsd:int"), FLOAT(
  "xsd:float"), DOUBLE("xsd:double");

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

  public static XsdDatatype fromUri(URI uri)
  {
    String uriString = uri.toString();
    String base = uri.toString().substring(0, uriString.indexOf("#") + 1);
    String fragment = uri.toString().substring(uriString.indexOf("#") + 1);

    if (base == null || !base.equals(XSD_IRI))
      throw new IllegalArgumentException("Base of URI " + uriString + " is not " + XSD_IRI);

    return fromString("xsd:" + fragment);
  }

  public static boolean isKnownXsdDatatype(String datatype)
  {
    for (XsdDatatype d : XsdDatatype.values()) {
      if (d.toUri().toString().equalsIgnoreCase(datatype) || d.text.equals(datatype)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isKnownXsdDatatypeUri(URI uri)
  {
    return isKnownXsdDatatype(uri.toString());
  }

  @Override public String toString()
  {
    return text;
  }
}