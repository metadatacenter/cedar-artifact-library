package org.metadatacenter.artifacts.model.reader;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Shared offset-datetime parsing used by both the JSON and YAML readers, so a colon-less zone
 * offset is accepted identically on both paths.
 */
public class OffsetDateTimesTest
{
  @Test public void normalizesColonlessOffset()
  {
    assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05+01:00"),
      OffsetDateTimes.parse("2026-01-02T03:04:05+0100"));
  }

  @Test public void acceptsStandardOffsetAndZulu()
  {
    assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05+01:00"),
      OffsetDateTimes.parse("2026-01-02T03:04:05+01:00"));
    assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), OffsetDateTimes.parse("2026-01-02T03:04:05Z"));
  }

  @Test public void rejectsGarbage()
  {
    assertThrows(DateTimeParseException.class, () -> OffsetDateTimes.parse("not-a-datetime"));
  }
}
