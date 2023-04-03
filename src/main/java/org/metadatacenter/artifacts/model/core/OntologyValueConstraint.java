package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.Optional;

public class OntologyValueConstraint
{
  private final URI uri;
  private final String acronym;
  private final String name;
  private final Optional<Integer> numTerms;

  public OntologyValueConstraint(URI uri, String acronym, String name, Optional<Integer> numTerms)
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

  public Optional<Integer> getNumTerms()
  {
    return numTerms;
  }

  @Override public String toString()
  {
    return "OntologyValueConstraint{" + "uri=" + uri + ", acronym='" + acronym + '\'' + ", name='" + name + '\''
      + ", numTerms=" + numTerms + '}';
  }
}
