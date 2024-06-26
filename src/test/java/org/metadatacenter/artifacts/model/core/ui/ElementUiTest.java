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
    String fieldName = "Field 1";
    ElementUi elementUi = ElementUi.builder()
      .withOrder(fieldName)
      .build();
    assertFalse(elementUi.order().isEmpty());
    assertEquals(elementUi.order().get(0), fieldName);
  }

  @Test
  public void testGetPropertyLabels() {
    String fieldName1 = "Field 1";
    String fieldName2 = "Field 2";
    String label1 = "Label 1";
    String label2 = "Label 2";

    ElementUi elementUi = ElementUi.builder()
      .withOrder(fieldName1)
      .withOrder(fieldName2)
      .withPropertyLabel(fieldName1, label1)
      .withPropertyLabel(fieldName2, label2)
      .build();

    assertEquals(elementUi.order().size(), 2);
    assertEquals(elementUi.order().get(0), fieldName1);
    assertEquals(elementUi.order().get(1), fieldName2);

    assertEquals(elementUi.propertyLabels().size(), 2);
    assertEquals(elementUi.propertyLabels().get(fieldName1), label1);
    assertEquals(elementUi.propertyLabels().get(fieldName2), label2);
  }

  @Test
  public void testGetPropertyDescriptions() {
    String fieldName1 = "Field 1";
    String fieldName2 = "Field 2";
    String propertyDescription1 = "Description 1";
    String propertyDescription2 = "Description 2";
    ElementUi elementUi = ElementUi.builder()
      .withOrder(fieldName1)
      .withOrder(fieldName2)
      .withPropertyDescription(fieldName1, propertyDescription1)
      .withPropertyDescription(fieldName2, propertyDescription2)
      .build();

    assertEquals(elementUi.order().size(), 2);
    assertEquals(elementUi.order().get(0), fieldName1);
    assertEquals(elementUi.order().get(1), fieldName2);

    assertEquals(elementUi.propertyDescriptions().size(), 2);
    assertEquals(elementUi.propertyDescriptions().get(fieldName1), propertyDescription1);
    assertEquals(elementUi.propertyDescriptions().get(fieldName2), propertyDescription2);
  }
}