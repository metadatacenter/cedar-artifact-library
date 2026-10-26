package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.MODEL_VERSION;
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
    node.put(SCHEMA_ORG_SCHEMA_VERSION, MODEL_VERSION);

    Optional<Version> result = JsonNodeReaders.readModelVersion(node, "/");

    assertTrue(result.isPresent());
    assertEquals(Version.fromString(MODEL_VERSION), result.get());
  }

  @Test public void readModelVersionReturnsEmptyWhenAbsent()
  {
    // Regression test: previously this silently defaulted to the model version, masking
    // documents that never declared one.
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

  @Test public void readUriArrayReadsEachItemAsReadUriReadsAString()
  {
    // A value's @type can be a string or a one-element array. Both must name the same IRI, so a known XSD
    // name is expanded in the array too, and any other IRI is kept as it is written.
    List<String> spellings = new ArrayList<>();
    for (XsdDatatype datatype : XsdDatatype.values()) {
      spellings.add(datatype.getText());
      spellings.add(datatype.toUri().toString());
    }
    spellings.addAll(List.of("xsd:integer", "xsd:DateTime", "http://www.w3.org/2001/XMLSchema#integer",
      "https://example.org/datatypes/count"));

    for (String spelling : spellings) {
      ObjectNode asString = mapper.createObjectNode().put(JSON_LD_TYPE, spelling);
      ObjectNode asArray = mapper.createObjectNode();
      asArray.putArray(JSON_LD_TYPE).add(spelling);

      assertEquals(List.of(JsonNodeReaders.readUri(asString, "/", JSON_LD_TYPE).get()),
        JsonNodeReaders.readUriArray(asArray, "/", JSON_LD_TYPE), spelling);
    }

    ObjectNode prefixed = mapper.createObjectNode();
    prefixed.putArray(JSON_LD_TYPE).add("xsd:int");
    assertEquals(List.of(URI.create("http://www.w3.org/2001/XMLSchema#int")),
      JsonNodeReaders.readUriArray(prefixed, "/", JSON_LD_TYPE));
  }
}
