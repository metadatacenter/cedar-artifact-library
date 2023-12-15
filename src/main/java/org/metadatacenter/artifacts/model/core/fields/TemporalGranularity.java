package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TemporalGranularity
{
  YEAR("year"),
  MONTH("month"),
  DAY("day"),
  HOUR("hour"),
  MINUTE("minute"),
  SECOND("second"),
  DECIMAL_SECOND("decimalSecond");

  private String text;

  TemporalGranularity(String text) {
    this.text = text;
  }

  public boolean isYear() { return this == YEAR; }

  public boolean isMonth() { return this == MONTH; }

  public boolean isDay() { return this == DAY; }

  public boolean isHour() { return this == HOUR; }

  public boolean isMinute() { return this == MINUTE; }

  public boolean isSecond() { return this == SECOND; }

  public boolean isDecimalSecond() { return this == DECIMAL_SECOND; }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static TemporalGranularity fromString(String text) {
    for (TemporalGranularity tg : TemporalGranularity.values()) {
      if (tg.text.equalsIgnoreCase(text)) {
        return tg;
      }
    }
    throw new IllegalArgumentException("No temporal granularity constant with name " + text + " found");
  }

  @Override public String toString()
  {
    return text;
  }
}
