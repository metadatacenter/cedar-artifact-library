package org.metadatacenter.artifacts.model.core.ui;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TemplateUiTest
{
  @Test
  public void testGetUiType() {
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder("field 1")
      .build();
    assertEquals(UiType.TEMPLATE_UI, templateUi.uiType());
  }

  @Test
  public void testGetOrder() {
    String fieldKey = "Field 1";
    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(fieldKey)
      .build();
    assertFalse(templateUi.order().isEmpty());
    assertEquals(templateUi.order().get(0), fieldKey);
  }

  @Test
  public void testGetPropertyLabels() {
    String fieldKey1 = "Field 1";
    String fieldKey2 = "Field 2";
    String propertyLabel1 = "Label 1";
    String propertyLabel2 = "Label 2";

    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(fieldKey1)
      .withOrder(fieldKey2)
      .withPropertyLabel(fieldKey1, propertyLabel1)
      .withPropertyLabel(fieldKey2, propertyLabel2)
      .build();

    assertEquals(templateUi.order().get(0), fieldKey1);
    assertEquals(templateUi.order().get(1), fieldKey2);
    assertEquals(templateUi.propertyLabels().get(fieldKey1), propertyLabel1);
    assertEquals(templateUi.propertyLabels().get(fieldKey2), propertyLabel2);
  }

  @Test
  public void testGetPropertyDescriptions() {
    String fieldKey1 = "Field 1";
    String fieldKey2 = "Field 2";
    String propertyDescription1 = "Description 1";
    String propertyDescription2 = "Description 1";

    TemplateUi templateUi = TemplateUi.builder()
      .withOrder(fieldKey1)
      .withOrder(fieldKey2)
      .withPropertyDescription(fieldKey1, propertyDescription1)
      .withPropertyDescription(fieldKey2, propertyDescription2)
      .build();

    assertEquals(templateUi.order().get(0), fieldKey1);
    assertEquals(templateUi.order().get(1), fieldKey2);
    assertEquals(templateUi.propertyDescriptions().get(fieldKey1), propertyDescription1);
    assertEquals(templateUi.propertyDescriptions().get(fieldKey1), propertyDescription2);
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
