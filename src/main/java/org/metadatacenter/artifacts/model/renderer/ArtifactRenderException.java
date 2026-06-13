package org.metadatacenter.artifacts.model.renderer;

/**
 * Thrown when an artifact cannot be rendered because the in-memory model violates a contract the
 * renderer requires — for example a child key with no corresponding child instance, or an
 * unhandled enum constant. Distinct from {@code ArtifactParseException}: it signals an invalid
 * model rather than invalid input, so callers can tell a programming error from a data error.
 */
public class ArtifactRenderException extends RuntimeException
{
  public ArtifactRenderException(String message)
  {
    super(message);
  }
}
