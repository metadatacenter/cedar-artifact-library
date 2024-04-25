package org.metadatacenter.artifacts.model.tools;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;

public class CustomPrettyPrinter extends DefaultPrettyPrinter {
  @Override
  public DefaultPrettyPrinter createInstance() {
    return new DefaultPrettyPrinter(this);
  }

  @Override
  public DefaultPrettyPrinter withSeparators(Separators separators) {
    _separators = separators;
    _objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
    return this;
  }
}
