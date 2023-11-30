package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumericValueConstraintsTest
{
  @Test public void numericValueConstraintsBuildTest()
  {
    boolean requiredValue = false;
    boolean multipleChoice= false;
    XsdNumericDatatype numberType = XsdNumericDatatype.DECIMAL;
    String unitOfMeasure = "mm";
    Number minValue = 0;
    Number maxValue = 100;
    Integer decimalPlaces = 1;

    NumericValueConstraints numericValueConstraints = NumericValueConstraints.builder()
      .withRequiredValue(requiredValue)
      .withMultipleChoice(multipleChoice)
      .withNumberType(numberType)
      .withUnitOfMeasure(unitOfMeasure)
      .withMinValue(minValue)
      .withMaxValue(maxValue)
      .withDecimalPlaces(decimalPlaces)
      .build();

    assertEquals(requiredValue, numericValueConstraints.requiredValue());
    assertEquals(multipleChoice, numericValueConstraints.multipleChoice());
    assertEquals(numberType, numericValueConstraints.numberType());
    assertEquals(unitOfMeasure, numericValueConstraints.unitOfMeasure().get());
    assertEquals(minValue, numericValueConstraints.minValue().get());
    assertEquals(maxValue, numericValueConstraints.maxValue().get());
    assertEquals(decimalPlaces, numericValueConstraints.decimalPlace().get());
  }
}