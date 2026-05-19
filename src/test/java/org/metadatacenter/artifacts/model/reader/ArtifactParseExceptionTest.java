package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArtifactParseExceptionTest
{
  @Test public void testPreservesFieldKey()
  {
    ArtifactParseException ex = new ArtifactParseException("bad value", "schema:name", "/template/foo");
    assertEquals("schema:name", ex.getFieldKey());
  }

  @Test public void testPreservesPath()
  {
    ArtifactParseException ex = new ArtifactParseException("bad value", "schema:name", "/template/foo");
    assertEquals("/template/foo", ex.getPath());
  }

  @Test public void testMessageContainsBothFieldKeyAndPath()
  {
    // Consumers display ex.getMessage() to humans; both the offending field name and the path
    // to it must appear so the message is actionable.
    ArtifactParseException ex = new ArtifactParseException("bad value", "schema:name", "/template/foo");
    assertTrue(ex.getMessage().contains("schema:name"),
      "exception message missing field key. Got: " + ex.getMessage());
    assertTrue(ex.getMessage().contains("/template/foo"),
      "exception message missing path. Got: " + ex.getMessage());
    assertTrue(ex.getMessage().contains("bad value"),
      "exception message missing root cause. Got: " + ex.getMessage());
    // And the raw parse-error message is recoverable separately.
    assertEquals("bad value", ex.getParseErrorMessage());
  }
}
