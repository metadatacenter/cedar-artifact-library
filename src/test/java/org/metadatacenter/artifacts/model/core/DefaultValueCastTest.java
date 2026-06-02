package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The {@code as*DefaultValue} narrowing accessors on {@link org.metadatacenter.artifacts.model.core.fields.DefaultValue}
 * throw {@link ClassCastException} on a type mismatch. {@code asControlledTermDefaultValue} used to
 * be the lone exception, returning {@code null}; this pins it to the consistent contract.
 */
public class DefaultValueCastTest
{
  @Test public void asControlledTermThrowsForNonControlledTermDefault()
  {
    TextDefaultValue text = new TextDefaultValue("x");
    assertThrows(ClassCastException.class, text::asControlledTermDefaultValue);
  }

  @Test public void asControlledTermReturnsSelfForControlledTermDefault()
  {
    ControlledTermDefaultValue controlledTerm = new ControlledTermDefaultValue(URI.create("https://example.com/1"),
      "label");
    assertSame(controlledTerm, controlledTerm.asControlledTermDefaultValue());
  }
}
