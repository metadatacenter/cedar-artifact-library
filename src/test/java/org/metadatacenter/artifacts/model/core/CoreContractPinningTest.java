package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreContractPinningTest
{
  @Test public void testXsdNumericDatatypeIntStringIsDeterministicallyInteger()
  {
    // Both XsdNumericDatatype.INTEGER and XsdNumericDatatype.INT carry the text "xsd:int".
    // fromString iterates in enum-declaration order; INTEGER is declared first, so it wins.
    // Reordering the constants would silently change which one downstream callers receive.
    XsdNumericDatatype result = XsdNumericDatatype.fromString("xsd:int");
    assertEquals(XsdNumericDatatype.INTEGER, result,
      "Expected INTEGER (first declared); got " + result + ". "
        + "If you reordered the enum, search for `XsdNumericDatatype.fromString(\"xsd:int\")` "
        + "usages and verify they still expect INTEGER.");
  }

  @Test public void testFieldInstanceCreateAcceptsEmptyJsonLdTypes()
  {
    // A literal field instance has no JSON-LD @type; the create factory must accept that.
    FieldInstanceArtifact instance = FieldInstanceArtifact.create(
      Collections.emptyList(), Optional.empty(),
      Optional.of("hello"), Optional.empty(), Optional.empty(),
      Optional.empty(), Optional.empty());

    assertNotNull(instance);
    assertTrue(instance.jsonLdTypes().isEmpty());
    assertEquals("hello", instance.jsonLdValue().get());
  }

  @Test public void testStatusFromStringRejectsUnknown()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> Status.fromString("limbo"));
    assertTrue(ex.getMessage().contains("limbo"));
  }
}
