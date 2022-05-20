package org.metadatacenter.model.core;

import java.net.URI;

public class OntologyValueConstraint
{
  private final URI uri;
  private final String acronym;
  private final String name;
  private final int numTerms;

  public OntologyValueConstraint(URI uri, String acronym, String name, int numTerms)
  {
    this.uri = uri;
    this.acronym = acronym;
    this.name = name;
    this.numTerms = numTerms;
  }

  public URI getUri()
  {
    return uri;
  }

  public String getAcronym()
  {
    return acronym;
  }

  public String getName()
  {
    return name;
  }

  public int getNumTerms()
  {
    return numTerms;
  }

  @Override public String toString()
  {
    return "OntologyValueConstraint{" + "uri=" + uri + ", acronym='" + acronym + '\'' + ", name='" + name + '\''
      + ", numTerms=" + numTerms + '}';
  }
}
