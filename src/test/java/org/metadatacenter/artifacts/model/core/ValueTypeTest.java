package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import static org.junit.Assert.*;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TYPE_ONTOLOGY_CLASS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TYPE_VALUE;

public class ValueTypeTest {

  @Test
  public void testGetValueTypeText() {
    assertEquals(VALUE_CONSTRAINTS_TYPE_ONTOLOGY_CLASS, ValueType.ONTOLOGY_CLASS.getText());
    assertEquals(VALUE_CONSTRAINTS_TYPE_VALUE, ValueType.VALUE.getText());
  }

  @Test
  public void testValueTypeFromString() {
    assertEquals(ValueType.ONTOLOGY_CLASS, ValueType.fromString(VALUE_CONSTRAINTS_TYPE_ONTOLOGY_CLASS));
    assertEquals(ValueType.VALUE, ValueType.fromString(VALUE_CONSTRAINTS_TYPE_VALUE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueTypeFromString() {
    ValueType.fromString("InvalidValueType");
  }
}