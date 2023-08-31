package org.metadatacenter.artifacts.model.core;

import org.apache.poi.ss.formula.functions.T;

public enum InputTimeFormat
{
  TWELVE_HOUR("12h"),
  TWENTY_FOUR_HOUR("24h");

  private String text;

  InputTimeFormat(String text) {
    this.text = text;
  }

  public boolean isTwelveHour() { return this == TWELVE_HOUR; }

  public boolean isTwentyFourHour() { return this == TWENTY_FOUR_HOUR; }

  public String getText() {
    return this.text;
  }

  public static InputTimeFormat fromString(String text) {
    for (InputTimeFormat itf : InputTimeFormat.values()) {
      if (itf.text.equalsIgnoreCase(text)) {
        return itf;
      }
    }
    throw new IllegalArgumentException("No input time format constant with text " + text + " found");
  }

  @Override public String toString()
  {
    return text;
  }
}
