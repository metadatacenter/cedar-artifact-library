package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

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
    List<String> order = List.of("field1", "field2", "field3");
    ElementUi elementUi = ElementUi.builder()
      .withOrder(order)
      .build();
    assertEquals(order, elementUi.order());
  }

  @Test
  public void testGetPropertyLabels() {
    List<String> order = List.of("field1", "field2", "field3");
    Map<String, String> propertyLabels = new HashMap<>();
    propertyLabels.put("field1", "Label 1");
    propertyLabels.put("field2", "Label 2");
    ElementUi elementUi = ElementUi.builder()
      .withOrder(order)
      .withPropertyLabels(propertyLabels)
      .build();
    assertEquals(propertyLabels, elementUi.propertyLabels());
  }

  @Test
  public void testGetPropertyDescriptions() {
    List<String> order = List.of("field1", "field2", "field3");
    Map<String, String> propertyDescriptions = new HashMap<>();
    propertyDescriptions.put("field1", "Description 1");
    propertyDescriptions.put("field2", "Description 2");
    ElementUi elementUi = ElementUi.builder()
      .withOrder(order)
      .withPropertyDescriptions(propertyDescriptions)
      .build();
    assertEquals(propertyDescriptions, elementUi.propertyDescriptions());
  }

  @Test
  public void testGetHeader() {
    String header = "Header";
    ElementUi elementUi = ElementUi.builder()
      .withHeader(header)
      .build();
    assertEquals(Optional.of(header), elementUi.header());
  }

  @Test
  public void testGetFooter() {
    String footer = "Footer";
    ElementUi elementUi = ElementUi.builder()
      .withFooter(footer)
      .build();
    assertEquals(Optional.of(footer), elementUi.footer());
  }
}