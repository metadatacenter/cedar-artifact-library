package org.metadatacenter.artifacts.model.core;

import java.net.URI;

public class ClassValueConstraint
{
  private final URI uri;
  private final String prefLabel;
  private final String type;
  private final String label;
  private final String source;

  public ClassValueConstraint(URI uri, String prefLabel, String type, String label, String source)
  {
    this.uri = uri;
    this.prefLabel = prefLabel;
    this.type = type;
    this.label = label;
    this.source = source;
  }

  public URI getUri()
  {
    return uri;
  }

  public String getPrefLabel()
  {
    return prefLabel;
  }

  public String getType()
  {
    return type;
  }

  public String getLabel()
  {
    return label;
  }

  public String getSource()
  {
    return source;
  }

  @Override public String toString()
  {
    return "ClassValueConstraint{" + "uri=" + uri + ", prefLabel='" + prefLabel + '\'' + ", valueType='" + type + '\''
      + ", label='" + label + '\'' + ", source='" + source + '\'' + '}';
  }
}
