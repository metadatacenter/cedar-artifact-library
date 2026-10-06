package org.metadatacenter.artifacts.model.reader;

import org.metadatacenter.model.validation.IriReference;

import java.net.URI;
import java.net.URISyntaxException;

/** The IRI parse the readers share for every position except a field's value. */
final class IriSyntax
{
  private IriSyntax() {}

  /**
   * The value as a {@link URI}, refused when RFC 3987 does not allow one of its characters or
   * java.net.URI cannot hold it as spelled. java.net.URI alone accepts a private-use character
   * outside a query, an unpaired surrogate and a noncharacter, all of which the validator refuses, so
   * a reader used to open what nothing should have stored. A field's value goes through
   * {@link IriReference} itself, because the model keeps that value's spelling separately.
   */
  static URI uri(String value) throws URISyntaxException
  {
    IriReference.toUri(value);
    return new URI(value);
  }
}
