package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class TemplateUiTest
{
  @Test
  public void testGetUiType() {
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder("field 1")
      .withPage("page 1")
      .build();
    assertEquals(UiType.TEMPLATE_UI, templateUi.uiType());
  }

  @Test
  public void testGetOrder() {
    String fieldName = "Field 1";
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(fieldName)
      .build();
    assertFalse(templateUi.order().isEmpty());
    assertEquals(templateUi.order().get(0), fieldName);
  }

  @Test
  public void testGetPages() {
    String pageName = "Page 1";
    TemplateUi templateUi = TemplateUi.builder()
      .withPage(pageName)
      .build();
    assertFalse(templateUi.pages().isEmpty());
    assertEquals(templateUi.pages().get(0), pageName);
  }

  @Test
  public void testGetPropertyLabels() {
    String fieldName1 = "Field 1";
    String fieldName2 = "Field 2";
    String propertyLabel1 = "Label 1";
    String propertyLabel2 = "Label 2";

    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(fieldName1)
      .withOrder(fieldName2)
      .withPropertyLabel(fieldName1, propertyLabel1)
      .withPropertyLabel(fieldName2, propertyLabel2)
      .build();

    assertEquals(templateUi.order().get(0), fieldName1);
    assertEquals(templateUi.order().get(1), fieldName2);
    assertEquals(templateUi.propertyLabels().get(fieldName1), propertyLabel1);
    assertEquals(templateUi.propertyLabels().get(fieldName2), propertyLabel2);
  }

  @Test
  public void testGetPropertyDescriptions() {
    String fieldName1 = "Field 1";
    String fieldName2 = "Field 2";
    String propertyDescription1 = "Description 1";
    String propertyDescription2 = "Description 1";

    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(fieldName1)
      .withOrder(fieldName2)
      .withPropertyDescription(fieldName1, propertyDescription1)
      .withPropertyDescription(fieldName2, propertyDescription2)
      .build();

    assertEquals(templateUi.order().get(0), fieldName1);
    assertEquals(templateUi.order().get(1), fieldName2);
    assertEquals(templateUi.propertyDescriptions().get(fieldName1), propertyDescription1);
    assertEquals(templateUi.propertyDescriptions().get(fieldName1), propertyDescription2);
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