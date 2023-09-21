package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TemplateUiTest
{

  @Test
  public void testGetUiType() {
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(Collections.singletonList("order1"))
      .withPages(Collections.singletonList("page1"))
      .build();
    assertEquals(UiType.TEMPLATE_UI, templateUi.uiType());
  }

  @Test
  public void testGetOrder() {
    List<String> order = List.of("order1", "order2", "order3");
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(order)
      .build();
    assertEquals(order, templateUi.order());
  }

  @Test
  public void testGetPages() {
    List<String> pages = List.of("page1", "page2", "page3");
    TemplateUi templateUi = TemplateUi.builder()
      .withPages(pages)
      .build();
    assertEquals(pages, templateUi.pages());
  }

  @Test
  public void testGetPropertyLabels() {
    List<String> order = List.of("field1", "field2", "field3");
    Map<String, String> propertyLabels = new HashMap<>();
    propertyLabels.put("field1", "Label 1");
    propertyLabels.put("field2", "Label 2");
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(order)
      .withPropertyLabels(propertyLabels)
      .build();
    assertEquals(propertyLabels, templateUi.propertyLabels());
  }

  @Test
  public void testGetPropertyDescriptions() {
    List<String> order = List.of("field1", "field2", "field3");
    Map<String, String> propertyDescriptions = new HashMap<>();
    propertyDescriptions.put("field1", "Description 1");
    propertyDescriptions.put("field2", "Description 2");
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(order)
      .withPropertyDescriptions(propertyDescriptions)
      .build();
    assertEquals(propertyDescriptions, templateUi.propertyDescriptions());
  }

  @Test
  public void testGetHeader() {
    Optional<String> header = Optional.of("Header");
    TemplateUi templateUi = TemplateUi.builder()
      .withHeader(header.get())
      .build();
    assertEquals(header, templateUi.header());
  }

  @Test
  public void testGetFooter() {
    Optional<String> footer = Optional.of("Footer");
    TemplateUi templateUi = TemplateUi.builder()
      .withFooter(footer.get())
      .build();
    assertEquals(footer, templateUi.footer());
  }
}