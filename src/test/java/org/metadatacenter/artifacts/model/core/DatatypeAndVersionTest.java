package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Group H — enum / datatype round-trips.
 *
 * <p>The {@link XsdNumericDatatype#fromString} round-trip notably collides on
 * {@code "xsd:int"} (mapped by both {@code INT} and {@code INTEGER}), so the test
 * asserts the observed first-wins behavior rather than a per-constant identity.
 */
public class DatatypeAndVersionTest
{
  @Test public void testValueTypeFromStringRejectsUnknown()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> ValueType.fromString("definitely-not-a-value-type"));
    assertTrue(ex.getMessage().contains("definitely-not-a-value-type"));
  }

  @Test public void testXsdNumericDatatypeRoundTrip()
  {
    // Every numeric constant must round-trip through its text encoding back to *some* enum
    // member with the same text. INT and INTEGER share "xsd:int" so we relax the round-trip
    // to a text equality check rather than identity.
    for (XsdNumericDatatype dt : XsdNumericDatatype.values()) {
      XsdNumericDatatype reparsed = XsdNumericDatatype.fromString(dt.getText());
      assertNotNull(reparsed, "fromString returned null for " + dt);
      assertEquals(dt.getText(), reparsed.getText(),
        "Text encoding mismatch for " + dt + " (got " + reparsed + ")");
    }
    assertThrows(IllegalArgumentException.class, () -> XsdNumericDatatype.fromString("xsd:garbage"));
  }

  @Test public void testXsdTemporalDatatypeRoundTrip()
  {
    for (XsdTemporalDatatype dt : XsdTemporalDatatype.values()) {
      assertEquals(dt, XsdTemporalDatatype.fromString(dt.getText()),
        "Round-trip failed for " + dt);
    }
    assertThrows(IllegalArgumentException.class, () -> XsdTemporalDatatype.fromString("xsd:garbage"));
  }

  @Test public void testTemporalGranularityFlagsAreMutuallyExclusive()
  {
    assertTrue(TemporalGranularity.YEAR.isYear());
    assertFalse(TemporalGranularity.YEAR.isMonth());
    assertTrue(TemporalGranularity.DAY.isDay());
    assertFalse(TemporalGranularity.DAY.isHour());
    // Each constant claims exactly one of the seven granularity slots.
    for (TemporalGranularity g : TemporalGranularity.values()) {
      int hits = (g.isYear() ? 1 : 0) + (g.isMonth() ? 1 : 0) + (g.isDay() ? 1 : 0)
        + (g.isHour() ? 1 : 0) + (g.isMinute() ? 1 : 0) + (g.isSecond() ? 1 : 0)
        + (g.isDecimalSecond() ? 1 : 0);
      assertEquals(1, hits, g + " claims " + hits + " granularity slots; expected exactly 1");
    }
  }

  @Test public void testVersionFromStringRejectsMalformed()
  {
    // Sanity for the JsonNodeReaders.readVersion / readModelVersion guard added in PR #43.
    assertThrows(IllegalArgumentException.class, () -> Version.fromString("not-a-version"));
    assertThrows(IllegalArgumentException.class, () -> Version.fromString("1.2"));
    assertThrows(IllegalArgumentException.class, () -> Version.fromString("v1.0.0"));
    assertFalse(Version.isValidVersion(""));
    assertFalse(Version.isValidVersion("1"));
    assertTrue(Version.isValidVersion("0.0.1"));
    assertEquals(new Version(1, 6, 0), Version.fromString("1.6.0"));
  }
}
