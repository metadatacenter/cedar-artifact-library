package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status
{
  DRAFT("bibo:draft"),
  PUBLISHED("bibo:published");

  private String text;

  Status(String text) {
    this.text = text;
  }

  @JsonValue
  public String getText() {
    return this.text;
  }

  public static Status fromString(String text) {
    for (Status s : Status.values()) {
      if (s.text.equalsIgnoreCase(text)) {
        return s;
      }
    }
    throw new IllegalArgumentException("No status constant with text " + text + " found");
  }
}