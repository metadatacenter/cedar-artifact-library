package org.metadatacenter.model.core;

import java.net.URI;

public class ValueSetValueConstraint
{
  private final String name;
  private final String valueSetCollection;
  private final URI uri;
  private final int numberOfTerms;

  public ValueSetValueConstraint(String name, String valueSetCollection, URI uri, int numberOfTerms)
  {
    this.name = name;
    this.valueSetCollection = valueSetCollection;
    this.uri = uri;
    this.numberOfTerms = numberOfTerms;
  }

  public String getName()
  {
    return name;
  }

  public String getValueSetCollection()
  {
    return valueSetCollection;
  }

  public URI getUri()
  {
    return uri;
  }

  public int getNumberOfTerms()
  {
    return numberOfTerms;
  }

  @Override public String toString()
  {
    return "ValueSetValueConstraint{" + "name='" + name + '\'' + ", valueSetCollection=" + valueSetCollection
      + ", uri=" + uri + ", numberOfTerms=" + numberOfTerms + '}';
  }
}
