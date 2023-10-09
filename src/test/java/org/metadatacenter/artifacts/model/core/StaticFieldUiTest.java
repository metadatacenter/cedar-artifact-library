package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class StaticFieldUiTest
{
  @Test
  public void testCreateWithSectionBreakFieldUiBuilder() {

    String content = "My content";

    StaticFieldUi staticFieldUi = StaticFieldUi.sectionBreakFieldUiBuilder()
      .withContent(content)
      .build();

    assertTrue(staticFieldUi.inputType().isStatic());
    assertEquals(FieldInputType.SECTION_BREAK, staticFieldUi.inputType());
    assertEquals(content, staticFieldUi._content());
  }

  @Test
  public void testCreateWithRichTextFieldUiBuilder() {

    String content = "My content";
    boolean hidden = true;

    StaticFieldUi staticFieldUi = StaticFieldUi.richTextFieldUiBuilder()
      .withContent(content)
      .withHidden(hidden)
      .build();

    assertTrue(staticFieldUi.inputType().isStatic());
    assertEquals(FieldInputType.RICHTEXT, staticFieldUi.inputType());
    assertEquals(content, staticFieldUi._content());
  }

  @Test
  public void testCreateWithImageFieldUiBuilder() {

    String content = "My content";
    boolean hidden = true;

    StaticFieldUi staticFieldUi = StaticFieldUi.imageFieldUiBuilder()
      .withContent(content)
      .withHidden(hidden)
      .build();

    assertTrue(staticFieldUi.inputType().isStatic());
    assertEquals(FieldInputType.IMAGE, staticFieldUi.inputType());
    assertEquals(content, staticFieldUi._content());
  }

  @Test
  public void testCreateWithYouTubeFieldUiBuilder() {

    String content = "My content";
    boolean hidden = true;

    StaticFieldUi staticFieldUi = StaticFieldUi.youTubeFieldUiBuilder()
      .withContent(content)
      .withHidden(hidden)
      .build();

    assertTrue(staticFieldUi.inputType().isStatic());
    assertEquals(FieldInputType.YOUTUBE, staticFieldUi.inputType());
    assertEquals(content, staticFieldUi._content());
  }

}