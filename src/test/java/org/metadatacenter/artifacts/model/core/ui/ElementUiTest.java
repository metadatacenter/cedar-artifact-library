package org.metadatacenter.artifacts.model.core.ui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ElementUiTest
{

  @Test
  public void testGetUIType() {
    ElementUi elementUi = ElementUi.builder()
      .build();
    assertEquals(UiType.ELEMENT_UI, elementUi.uiType());
  }

  @Test
  public void testGetOrder() {
    String fieldKey = "Field 1";
    ElementUi elementUi = ElementUi.builder()
      .withOrder(fieldKey)
      .build();
    assertFalse(elementUi.order().isEmpty());
    assertEquals(elementUi.order().get(0), fieldKey);
  }

  @Test
  public void testGetPropertyLabels() {
    String fieldKey1 = "Field 1";
    String fieldKey2 = "Field 2";
    String label1 = "Label 1";
    String label2 = "Label 2";

    ElementUi elementUi = ElementUi.builder()
      .withOrder(fieldKey1)
      .withOrder(fieldKey2)
      .withPropertyLabel(fieldKey1, label1)
      .withPropertyLabel(fieldKey2, label2)
      .build();

    assertEquals(elementUi.order().size(), 2);
    assertEquals(elementUi.order().get(0), fieldKey1);
    assertEquals(elementUi.order().get(1), fieldKey2);

    assertEquals(elementUi.propertyLabels().size(), 2);
    assertEquals(elementUi.propertyLabels().get(fieldKey1), label1);
    assertEquals(elementUi.propertyLabels().get(fieldKey2), label2);
  }

  @Test
  public void testGetPropertyDescriptions() {
    String fieldKey1 = "Field 1";
    String fieldKey2 = "Field 2";
    String propertyDescription1 = "Description 1";
    String propertyDescription2 = "Description 2";
    ElementUi elementUi = ElementUi.builder()
      .withOrder(fieldKey1)
      .withOrder(fieldKey2)
      .withPropertyDescription(fieldKey1, propertyDescription1)
      .withPropertyDescription(fieldKey2, propertyDescription2)
      .build();

    assertEquals(elementUi.order().size(), 2);
    assertEquals(elementUi.order().get(0), fieldKey1);
    assertEquals(elementUi.order().get(1), fieldKey2);

    assertEquals(elementUi.propertyDescriptions().size(), 2);
    assertEquals(elementUi.propertyDescriptions().get(fieldKey1), propertyDescription1);
    assertEquals(elementUi.propertyDescriptions().get(fieldKey2), propertyDescription2);
  }
}