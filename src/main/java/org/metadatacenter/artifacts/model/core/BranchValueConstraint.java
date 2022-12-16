package org.metadatacenter.artifacts.model.core;

import java.net.URI;

public class BranchValueConstraint
{
  private final String source;
  private final String acronym;
  private final URI uri;
  private final String name;
  private final int maxDepth;

  public BranchValueConstraint(String source, String acronym, URI uri, String name, int maxDepth)
  {
    this.source = source;
    this.acronym = acronym;
    this.uri = uri;
    this.name = name;
    this.maxDepth = maxDepth;
  }

  public String getSource()
  {
    return source;
  }

  public String getAcronym()
  {
    return acronym;
  }

  public URI getUri()
  {
    return uri;
  }

  public String getName()
  {
    return name;
  }

  public int getMaxDepth()
  {
    return maxDepth;
  }

  @Override public String toString()
  {
    return "BranchValueConstraint{" + "source='" + source + '\'' + ", acronym='" + acronym + '\'' + ", uri=" + uri
      + ", name='" + name + '\'' + ", maxDepth=" + maxDepth + '}';
  }
}

