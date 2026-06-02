package org.metadatacenter.artifacts.model.reader;

import java.time.OffsetDateTime;

/**
 * Offset-datetime parsing shared by the JSON and YAML readers, so both accept exactly the same set
 * of values.
 */
final class OffsetDateTimes
{
  private OffsetDateTimes() {}

  /**
   * Parse an ISO-8601 offset datetime, tolerating a colon-less zone offset (e.g. {@code +0100}) by
   * normalizing it to {@code +01:00} first.
   *
   * @throws java.time.format.DateTimeParseException if the value cannot be parsed
   */
  static OffsetDateTime parse(String value)
  {
    return OffsetDateTime.parse(value.replaceAll("([+-]\\d{2})(\\d{2})$", "$1:$2"));
  }
}
