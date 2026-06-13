package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The CEDAR JSON Schema for {@code _valueConstraints.defaultValue} permits a string or a
 * URI/label object but not a bare JSON number. {@link NumericDefaultValue} must therefore
 * serialize its value as a string. The round-trip side of the contract is covered by
 * {@code JsonArtifactRoundTripTest}; this test pins the wire shape directly.
 */
public class NumericDefaultValueTest
{
  private final ObjectMapper mapper = new ObjectMapper();

  @Test public void serializesIntegerAsString() throws Exception
  {
    String json = mapper.writeValueAsString(new NumericDefaultValue(42));
    assertEquals("\"42\"", json);
  }

  @Test public void serializesLongAsString() throws Exception
  {
    String json = mapper.writeValueAsString(new NumericDefaultValue(9_000_000_000L));
    assertEquals("\"9000000000\"", json);
  }

  @Test public void serializesDoubleAsString() throws Exception
  {
    String json = mapper.writeValueAsString(new NumericDefaultValue(1.5));
    assertEquals("\"1.5\"", json);
  }
}
