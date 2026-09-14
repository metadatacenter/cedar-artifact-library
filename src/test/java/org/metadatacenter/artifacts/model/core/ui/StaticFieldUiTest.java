package org.metadatacenter.artifacts.model.core.ui;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StaticFieldUiTest
{
  @Test
  public void testCreateWithSectionBreakFieldUiBuilder() {

    String content = "My content";
    boolean hidden = true;

    StaticFieldUi staticFieldUi = StaticFieldUi.sectionBreakFieldUiBuilder()
      .withContent(content)
      .withHidden(hidden)
      .build();

    assertTrue(staticFieldUi.inputType().isStatic());
    assertEquals(FieldInputType.SECTION_BREAK, staticFieldUi.inputType());
    assertEquals(content, staticFieldUi._content().get());
    assertEquals(hidden, staticFieldUi.hidden());
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
    assertEquals(content, staticFieldUi._content().get());
    assertEquals(hidden, staticFieldUi.hidden());
    // A static field has no line placement to set, and reports none.
    assertFalse(staticFieldUi.continuePreviousLine());
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
    assertEquals(content, staticFieldUi._content().get());
    assertEquals(hidden, staticFieldUi.hidden());
    // A static field has no line placement to set, and reports none.
    assertFalse(staticFieldUi.continuePreviousLine());
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
    assertEquals(content, staticFieldUi._content().get());
    assertEquals(hidden, staticFieldUi.hidden());
    // A static field has no line placement to set, and reports none.
    assertFalse(staticFieldUi.continuePreviousLine());
  }

}
