package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import java.math.BigDecimal;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class DefaultConsistencyTest {
  @Test void numericDefaultsRespectTypeAndExactDeclaredBounds() {
    assertThrows(IllegalStateException.class, () -> NumericValueConstraints.builder()
        .withNumberType(XsdNumericDatatype.INT).withMinValue(0).withMaxValue(10).withDefaultValue(11).build());
    assertThrows(IllegalStateException.class, () -> NumericValueConstraints.builder()
        .withNumberType(XsdNumericDatatype.INT).withDefaultValue(new BigDecimal("1.1")).build());
    assertThrows(IllegalStateException.class, () -> NumericValueConstraints.builder()
        .withNumberType(XsdNumericDatatype.LONG).withMaxValue(new BigInteger("9007199254740992"))
        .withDefaultValue(new BigInteger("9007199254740993")).build());
    assertDoesNotThrow(() -> NumericValueConstraints.builder().withNumberType(XsdNumericDatatype.LONG)
        .withDefaultValue(new BigInteger("9223372036854775807")).build());
    assertThrows(IllegalStateException.class, () -> NumericValueConstraints.builder()
        .withNumberType(XsdNumericDatatype.DECIMAL).withMinValue(11).withMaxValue(10).build());
  }
  @Test void textDefaultsRespectCodePointLengthChoicesAndBrowserRegex() {
    assertDoesNotThrow(() -> TextValueConstraints.builder().withMinLength(1).withMaxLength(1).withDefaultValue("😀").build());
    assertThrows(IllegalStateException.class, () -> TextValueConstraints.builder().withMaxLength(1).withDefaultValue("e\u0301").build());
    assertThrows(IllegalStateException.class, () -> TextValueConstraints.builder().withChoice("one").withDefaultValue("two").build());
    assertThrows(IllegalStateException.class, () -> TextValueConstraints.builder().withRegex("[A-Z]+").withDefaultValue("lowercase").build());
  }
  @Test void temporalDefaultsCheckCalendarWithoutLosingPartialValueCompatibility() {
    assertThrows(IllegalStateException.class, () -> TemporalValueConstraints.builder()
        .withTemporalType(XsdTemporalDatatype.DATE).withDefaultValue("2023-02-29").build());
    assertDoesNotThrow(() -> TemporalValueConstraints.builder().withTemporalType(XsdTemporalDatatype.DATE)
        .withDefaultValue("2024-02").build());
    assertDoesNotThrow(() -> TemporalValueConstraints.builder().withTemporalType(XsdTemporalDatatype.DATETIME)
        .withDefaultValue("2024-02-29T12").build());
  }
  @Test void temporalFieldDefaultsRespectTheirUiGranularityAndTimezone() {
    assertThrows(IllegalStateException.class, () -> TemporalField.builder().withName("Date")
        .withTemporalType(XsdTemporalDatatype.DATE).withTemporalGranularity(TemporalGranularity.MONTH)
        .withDefaultValue("2024-02-29").build());
    assertDoesNotThrow(() -> TemporalField.builder().withName("Date")
        .withTemporalType(XsdTemporalDatatype.DATE).withTemporalGranularity(TemporalGranularity.MONTH)
        .withDefaultValue("2024-02").build());
    assertThrows(IllegalStateException.class, () -> TemporalField.builder().withName("Time")
        .withTemporalType(XsdTemporalDatatype.TIME).withTemporalGranularity(TemporalGranularity.MINUTE)
        .withTimeZoneEnabled(false).withDefaultValue("12:30Z").build());
    assertDoesNotThrow(() -> TemporalField.builder().withName("Time")
        .withTemporalType(XsdTemporalDatatype.TIME).withTemporalGranularity(TemporalGranularity.MINUTE)
        .withTimeZoneEnabled(true).withDefaultValue("12:30Z").build());
    assertDoesNotThrow(() -> TemporalField.builder().withName("Date")
        .withTemporalType(XsdTemporalDatatype.DATE).withTemporalGranularity(TemporalGranularity.DAY).build());
  }
}
