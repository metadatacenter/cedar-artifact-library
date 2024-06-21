package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;

import java.io.IOException;

public class CustomPrettyPrinter extends DefaultPrettyPrinter {

  public CustomPrettyPrinter() {
    super();
    this._objectFieldValueSeparatorWithSpaces = ": ";
    this._arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
    this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
  }

  @Override
  public DefaultPrettyPrinter createInstance() {
    return new CustomPrettyPrinter();
  }

  @Override
  public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
    if (!this._objectIndenter.isInline()) {
      --this._nesting;
    }

    if (nrOfEntries > 0) {
      this._objectIndenter.writeIndentation(g, this._nesting);
    } else {
      // do nothing
    }

    g.writeRaw('}');
  }

  @Override
  public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
    if (!this._arrayIndenter.isInline()) {
      --this._nesting;
    }

    if (nrOfValues > 0) {
      this._arrayIndenter.writeIndentation(g, this._nesting);
    } else {
      // do nothing
    }

    g.writeRaw(']');
  }
}
