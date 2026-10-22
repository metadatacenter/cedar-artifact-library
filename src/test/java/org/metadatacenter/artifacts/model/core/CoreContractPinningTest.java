package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;

public class CoreContractPinningTest
{
  @Test public void testXsdIntReadsAsInt()
  {
    // One constant carries "xsd:int", so reading it back gives the constant a field was built with.
    assertEquals(XsdNumericDatatype.INT, XsdNumericDatatype.fromString("xsd:int"));
    assertEquals(XsdDatatype.INT, XsdDatatype.fromString("xsd:int"));
    assertEquals(XsdDatatype.INT, XsdDatatype.fromString(XSD_IRI + "int"));
  }

  @Test public void testXsdIntegerIsRefused()
  {
    // xsd:integer is a different XSD datatype from xsd:int, and the meta-schema admits only xsd:int
    // as a numberType.
    assertThrows(IllegalArgumentException.class, () -> XsdNumericDatatype.fromString("xsd:integer"));
    assertThrows(IllegalArgumentException.class, () -> XsdDatatype.fromString("xsd:integer"));
    assertThrows(IllegalArgumentException.class, () -> XsdDatatype.fromString(XSD_IRI + "integer"));
    assertFalse(XsdDatatype.isKnownXsdDatatype("xsd:integer"));
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
