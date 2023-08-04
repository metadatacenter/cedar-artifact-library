package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TemplateUITest {

  @Test
  public void testGetUIType() {
    TemplateUI templateUI = TemplateUI.builder()
      .withOrder(Collections.singletonList("order1"))
      .withPages(Collections.singletonList("page1"))
      .build();
    assertEquals(UIType.TEMPLATE_UI, templateUI.getUIType());
  }

  @Test
  public void testGetOrder() {
    List<String> order = Arrays.asList("order1", "order2", "order3");
    TemplateUI templateUI = TemplateUI.builder()
      .withOrder(order)
      .build();
    assertEquals(order, templateUI.getOrder());
  }

  @Test
  public void testGetPages() {
    List<String> pages = Arrays.asList("page1", "page2", "page3");
    TemplateUI templateUI = TemplateUI.builder()
      .withPages(pages)
      .build();
    assertEquals(pages, templateUI.getPages());
  }

  @Test
  public void testGetPropertyLabels() {
    List<String> order = Arrays.asList("field1", "field2", "field3");
    Map<String, String> propertyLabels = new HashMap<>();
    propertyLabels.put("field1", "Label 1");
    propertyLabels.put("field2", "Label 2");
    TemplateUI templateUI = TemplateUI.builder()
      .withOrder(order)
      .withPropertyLabels(propertyLabels)
      .build();
    assertEquals(propertyLabels, templateUI.getPropertyLabels());
  }

  @Test
  public void testGetPropertyDescriptions() {
    List<String> order = Arrays.asList("field1", "field2", "field3");
    Map<String, String> propertyDescriptions = new HashMap<>();
    propertyDescriptions.put("field1", "Description 1");
    propertyDescriptions.put("field2", "Description 2");
    TemplateUI templateUI = TemplateUI.builder()
      .withOrder(order)
      .withPropertyDescriptions(propertyDescriptions)
      .build();
    assertEquals(propertyDescriptions, templateUI.getPropertyDescriptions());
  }

  @Test
  public void testGetHeader() {
    Optional<String> header = Optional.of("Header");
    TemplateUI templateUI = TemplateUI.builder()
      .withHeader(header.get())
      .build();
    assertEquals(header, templateUI.getHeader());
  }

  @Test
  public void testGetFooter() {
    Optional<String> footer = Optional.of("Footer");
    TemplateUI templateUI = TemplateUI.builder()
      .withFooter(footer.get())
      .build();
    assertEquals(footer, templateUI.getFooter());
  }
}