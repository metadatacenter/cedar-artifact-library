package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.Version;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;

public class JsonNodeReadersTest
{
  private final ObjectMapper mapper = new ObjectMapper();

  @Test public void readVersionReturnsParsedVersionWhenPresent()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put(PAV_VERSION, "2.1.3");

    Optional<Version> result = JsonNodeReaders.readVersion(node, "/", PAV_VERSION);

    assertTrue(result.isPresent());
    assertEquals(Version.fromString("2.1.3"), result.get());
  }

  @Test public void readVersionReturnsEmptyWhenAbsent()
  {
    ObjectNode node = mapper.createObjectNode();

    Optional<Version> result = JsonNodeReaders.readVersion(node, "/", PAV_VERSION);

    assertTrue(result.isEmpty());
  }

  @Test public void readVersionThrowsOnInvalidVersionString()
  {
    // Regression test: previously this silently rewrote invalid version strings to "0.0.1",
    // masking malformed input.
    ObjectNode node = mapper.createObjectNode();
    node.put(PAV_VERSION, "not-a-version");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> JsonNodeReaders.readVersion(node, "/", PAV_VERSION));
    assertTrue(ex.getMessage().contains("not-a-version"),
      "Exception message should mention the invalid version. Got: " + ex.getMessage());
  }

  @Test public void readModelVersionReturnsParsedVersionWhenPresent()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put(SCHEMA_ORG_SCHEMA_VERSION, "1.6.0");

    Optional<Version> result = JsonNodeReaders.readModelVersion(node, "/");

    assertTrue(result.isPresent());
    assertEquals(Version.fromString("1.6.0"), result.get());
  }

  @Test public void readModelVersionReturnsEmptyWhenAbsent()
  {
    // Regression test: previously this silently defaulted to "1.6.0", masking documents that
    // never declared a model version.
    ObjectNode node = mapper.createObjectNode();

    Optional<Version> result = JsonNodeReaders.readModelVersion(node, "/");

    assertTrue(result.isEmpty());
  }

  @Test public void readModelVersionThrowsOnInvalidVersionString()
  {
    ObjectNode node = mapper.createObjectNode();
    node.put(SCHEMA_ORG_SCHEMA_VERSION, "garbage");

    ArtifactParseException ex = assertThrows(ArtifactParseException.class,
      () -> JsonNodeReaders.readModelVersion(node, "/"));
    assertTrue(ex.getMessage().contains("garbage"),
      "Exception message should mention the invalid version. Got: " + ex.getMessage());
  }
}
