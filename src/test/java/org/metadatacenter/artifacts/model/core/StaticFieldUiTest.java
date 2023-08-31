package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class StaticFieldUiTest
{
  @Test
  public void testCreate() {

    FieldInputType fieldInputType = FieldInputType.IMAGE;
    String content = "My content";
    boolean hidden = false;

    StaticFieldUi staticFieldUi = StaticFieldUi.builder()
      .withFieldInputType(fieldInputType)
      .withContent(content)
      .withHidden(hidden)
      .build();

    assertTrue(staticFieldUi.inputType().isStatic());
    assertEquals(fieldInputType, staticFieldUi.inputType());
    assertEquals(content, staticFieldUi._content());
    assertEquals(hidden, staticFieldUi.hidden());
  }

}