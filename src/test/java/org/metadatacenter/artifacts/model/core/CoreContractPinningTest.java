package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Groups H + L — small contracts worth pinning.
 *
 * <p>**H1.** {@code XsdNumericDatatype.fromString("xsd:int")} has a documented ambiguity:
 * both {@code INTEGER("xsd:int")} and {@code INT("xsd:int")} match. The current
 * enum-declaration-order behavior returns {@code INTEGER}. If someone reorders the constants,
 * callers downstream silently switch to {@code INT} (or fail equality checks). This test
 * pins the current behavior.
 *
 * <p>**L1.** {@code FieldInstanceArtifact.create} must accept an empty {@code jsonLdTypes}
 * list — this is the normal shape for plain literal field instances. Pin it so a future
 * refactor doesn't add a "required" guard.
 *
 * <p>**L2.** {@code Status.fromString} rejects unknown strings (mirrors the version contract
 * locked in by PR #43).
 */
public class CoreContractPinningTest
{
  @Test public void testXsdNumericDatatypeIntStringIsDeterministicallyInteger()
  {
    // INTEGER is declared before INT in the enum (lines 12, 16 of XsdNumericDatatype.java),
    // so the fromString iteration matches INTEGER first. This test pins that ordering — if
    // the enum members are ever reordered, this test fails loudly rather than letting the
    // ambiguity silently flip downstream callers (e.g., JsonValueConstraintsReader L330).
    XsdNumericDatatype result = XsdNumericDatatype.fromString("xsd:int");
    assertEquals(XsdNumericDatatype.INTEGER, result,
      "Expected INTEGER (first declared); got " + result + ". "
        + "If you reordered the enum, search for `XsdNumericDatatype.fromString(\"xsd:int\")` "
        + "usages and verify they still expect INTEGER.");
  }

  @Test public void testFieldInstanceCreateAcceptsEmptyJsonLdTypes()
  {
    // Normal usage: a literal field instance has no JSON-LD @type. The create factory must
    // accept an empty list, not throw.
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
