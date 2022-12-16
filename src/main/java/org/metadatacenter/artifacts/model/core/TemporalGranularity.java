package org.metadatacenter.artifacts.model.core;

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

  public String getText() {
    return this.text;
  }

  public static TemporalGranularity fromString(String text) {
    for (TemporalGranularity tg : TemporalGranularity.values()) {
      if (tg.text.equalsIgnoreCase(text)) {
        return tg;
      }
    }
    throw new IllegalArgumentException("No temporal granularity constant with text " + text + " found");
  }
}
