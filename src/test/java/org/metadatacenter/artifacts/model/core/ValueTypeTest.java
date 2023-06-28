package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import org.metadatacenter.model.ModelNodeNames;

import static org.junit.Assert.*;

public class ValueTypeTest {

  @Test
  public void testGetValueTypeText() {
    assertEquals(ModelNodeNames.VALUE_CONSTRAINTS_TYPE_ONTOLOGY_CLASS, ValueType.ONTOLOGY_CLASS.getText());
    assertEquals(ModelNodeNames.VALUE_CONSTRAINTS_TYPE_VALUE_SET, ValueType.VALUE_SET.getText());
  }

  @Test
  public void testValueTypeFromString() {
    assertEquals(ValueType.ONTOLOGY_CLASS, ValueType.fromString(ModelNodeNames.VALUE_CONSTRAINTS_TYPE_ONTOLOGY_CLASS));
    assertEquals(ValueType.VALUE_SET, ValueType.fromString(ModelNodeNames.VALUE_CONSTRAINTS_TYPE_VALUE_SET));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueTypeFromString() {
    ValueType.fromString("InvalidValueType");
  }
}