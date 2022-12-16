package org.metadatacenter.artifacts.model.core;

public enum InputTimeFormat
{
  TWELVE_HOUR("12h"),
  TWENTY_FOUR_HOUR("24h");

  private String text;

  InputTimeFormat(String text) {
    this.text = text;
  }

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
}
