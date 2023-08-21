package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class ElementUITest {

  @Test
  public void testGetUIType() {
    ElementUI elementUI = ElementUI.builder()
      .build();
    assertEquals(UIType.ELEMENT_UI, elementUI.getUIType());
  }

  @Test
  public void testGetOrder() {
    List<String> order = List.of("field1", "field2", "field3");
    ElementUI elementUI = ElementUI.builder()
      .withOrder(order)
      .build();
    assertEquals(order, elementUI.order());
  }

  @Test
  public void testGetPropertyLabels() {
    List<String> order = List.of("field1", "field2", "field3");
    Map<String, String> propertyLabels = new HashMap<>();
    propertyLabels.put("field1", "Label 1");
    propertyLabels.put("field2", "Label 2");
    ElementUI elementUI = ElementUI.builder()
      .withOrder(order)
      .withPropertyLabels(propertyLabels)
      .build();
    assertEquals(propertyLabels, elementUI.propertyLabels());
  }

  @Test
  public void testGetPropertyDescriptions() {
    List<String> order = List.of("field1", "field2", "field3");
    Map<String, String> propertyDescriptions = new HashMap<>();
    propertyDescriptions.put("field1", "Description 1");
    propertyDescriptions.put("field2", "Description 2");
    ElementUI elementUI = ElementUI.builder()
      .withOrder(order)
      .withPropertyDescriptions(propertyDescriptions)
      .build();
    assertEquals(propertyDescriptions, elementUI.propertyDescriptions());
  }

  @Test
  public void testGetHeader() {
    String header = "Header";
    ElementUI elementUI = ElementUI.builder()
      .withHeader(header)
      .build();
    assertEquals(Optional.of(header), elementUI.header());
  }

  @Test
  public void testGetFooter() {
    String footer = "Footer";
    ElementUI elementUI = ElementUI.builder()
      .withFooter(footer)
      .build();
    assertEquals(Optional.of(footer), elementUI.footer());
  }
}