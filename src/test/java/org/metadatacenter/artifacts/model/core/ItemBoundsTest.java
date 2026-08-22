package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.UNBOUNDED_MAX_ITEMS;

/**
 * A maxItems of zero means unlimited, not an empty list — the convention CEDAR uses across the
 * system, presented to authors by the Template Designer as "unlimited". It is accepted on both
 * fields and elements, and never conflicts with minItems, since there is no upper bound to
 * conflict with.
 */
public class ItemBoundsTest
{
  @Test public void testUnboundedMaxItemsAcceptedOnField()
  {
    TextField field = TextField.builder().withName("F").withMinItems(1).withMaxItems(UNBOUNDED_MAX_ITEMS).build();

    assertEquals(UNBOUNDED_MAX_ITEMS, field.maxItems().get());
  }

  @Test public void testUnboundedMaxItemsAcceptedOnElement()
  {
    ElementSchemaArtifact element =
      ElementSchemaArtifact.builder().withName("E").withMinItems(1).withMaxItems(UNBOUNDED_MAX_ITEMS).build();

    assertEquals(UNBOUNDED_MAX_ITEMS, element.maxItems().get());
  }

  @Test public void testUnboundedMaxItemsDoesNotConflictWithAnyMinItems()
  {
    // Without the unbounded rule this would trip the minItems <= maxItems check.
    TextField field = TextField.builder().withName("F").withMinItems(5).withMaxItems(UNBOUNDED_MAX_ITEMS).build();

    assertEquals(5, field.minItems().get());
  }

  @Test public void testNegativeMaxItemsRejected()
  {
    IllegalStateException e = assertThrows(IllegalStateException.class,
      () -> TextField.builder().withName("F").withMaxItems(-1).build());

    assertTrue(e.getMessage().contains("maxItems must be zero or greater"), e.getMessage());
  }

  @Test public void testNegativeMinItemsRejected()
  {
    IllegalStateException e = assertThrows(IllegalStateException.class,
      () -> TextField.builder().withName("F").withMinItems(-1).build());

    assertTrue(e.getMessage().contains("minItems must be zero or greater"), e.getMessage());
  }

  @Test public void testBoundedMaxItemsStillCheckedAgainstMinItems()
  {
    IllegalStateException e = assertThrows(IllegalStateException.class,
      () -> TextField.builder().withName("F").withMinItems(3).withMaxItems(2).build());

    assertTrue(e.getMessage().contains("minItems must be less than or equal to maxItems"), e.getMessage());
  }
}
