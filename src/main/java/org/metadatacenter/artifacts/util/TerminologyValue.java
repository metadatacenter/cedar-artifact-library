package org.metadatacenter.artifacts.util;

import java.net.URI;

/**
 * A single value retrieved from the terminology server.
 * <p>
 * The URI identifies the value; the preferred label does not. Two classes drawn from different
 * ontologies, or from different branches of one ontology, can carry the same preferred label.
 */
public record TerminologyValue(URI uri, String prefLabel)
{
  public TerminologyValue
  {
    if (uri == null)
      throw new IllegalArgumentException("terminology value URI must not be null");

    if (prefLabel == null)
      throw new IllegalArgumentException("terminology value preferred label must not be null");
  }
}
