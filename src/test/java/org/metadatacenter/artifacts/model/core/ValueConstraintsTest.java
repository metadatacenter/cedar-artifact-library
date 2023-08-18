package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueConstraintsTest
{
  @Test public void valueConstraintsBuildTest()
  {
    boolean requiredValue = false;
    boolean multipleChoice= false;
    NumberType numberType = NumberType.DECIMAL;
    String unitOfMeasure = "mm";
    Number minValue = 0;
    Number maxValue = 100;
    Integer decimalPlaces = 1;

    ValueConstraints valueConstraints = ValueConstraints.builder()
      .withRequiredValue(requiredValue)
      .withMultipleChoice(multipleChoice)
      .withNumberType(numberType)
      .withUnitOfMeasure(unitOfMeasure)
      .withMinValue(minValue)
      .withMaxValue(maxValue)
      .withDecimalPlaces(decimalPlaces)
      .build();

    assertEquals(requiredValue, valueConstraints.requiredValue());
    assertEquals(multipleChoice, valueConstraints.multipleChoice());
    assertEquals(numberType, valueConstraints.numberType().get());
    assertEquals(unitOfMeasure, valueConstraints.unitOfMeasure().get());
    assertEquals(minValue, valueConstraints.minValue().get());
    assertEquals(maxValue, valueConstraints.maxValue().get());
    assertEquals(decimalPlaces, valueConstraints.decimalPlaces().get());
  }
}