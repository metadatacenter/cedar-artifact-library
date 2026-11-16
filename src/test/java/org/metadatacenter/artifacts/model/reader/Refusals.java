package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;

/** Finds the reason a reader refused a document, so a refusal test can require the reason it is named for. */
public final class Refusals
{
  private Refusals() {}

  /** The {@link ArtifactParseException} the action throws, directly or as the cause of what it throws. */
  public static ArtifactParseException parseRefusal(Executable action)
  {
    Throwable thrown = assertThrows(Throwable.class, action);
    for (Throwable cause = thrown; cause != null; cause = cause.getCause())
      if (cause instanceof ArtifactParseException refusal)
        return refusal;
    throw new AssertionError("refused without an ArtifactParseException: " + thrown, thrown);
  }

  /** The messages of what the action throws and of each cause, joined, for a refusal that is not a parse error. */
  public static String messages(Executable action)
  {
    StringBuilder messages = new StringBuilder();
    for (Throwable cause = assertThrows(Throwable.class, action); cause != null; cause = cause.getCause())
      messages.append(messages.isEmpty() ? "" : " <- ").append(cause.getMessage());
    return messages.toString();
  }

  /** The refusal as {@code key at path}, for an assertion that names both. */
  public static String keyAndPath(Executable action)
  {
    ArtifactParseException refusal = parseRefusal(action);
    return refusal.getFieldKey() + " at " + refusal.getPath();
  }
}
