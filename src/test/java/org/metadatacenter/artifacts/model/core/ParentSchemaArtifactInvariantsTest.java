package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * {@code prunedToOrder} drops children absent from the UI order (a silent cleanup the records
 * rely on) while leaving the caller's map untouched — it returns a fresh copy rather than
 * mutating its input.
 */
public class ParentSchemaArtifactInvariantsTest
{
  @Test public void prunedToOrderRemovesKeysNotInOrderWithoutMutatingInput()
  {
    LinkedHashMap<String, FieldSchemaArtifact> input = new LinkedHashMap<>();
    input.put("a", TextField.builder().withName("A").build());
    input.put("b", TextField.builder().withName("B").build());

    LinkedHashMap<String, FieldSchemaArtifact> pruned =
      ParentSchemaArtifactInvariants.prunedToOrder(input, List.of("a"));

    // "b" is not in the order, so it is dropped from the returned copy ...
    assertEquals(List.of("a"), List.copyOf(pruned.keySet()));
    // ... but the caller's map is left exactly as supplied.
    assertEquals(List.of("a", "b"), List.copyOf(input.keySet()));
    assertNotSame(input, pruned);
  }

  @Test public void prunedToOrderKeepsEveryDeclaredChild()
  {
    LinkedHashMap<String, FieldSchemaArtifact> input = new LinkedHashMap<>();
    input.put("a", TextField.builder().withName("A").build());

    // Extra order entries with no matching child are harmless.
    LinkedHashMap<String, FieldSchemaArtifact> pruned =
      ParentSchemaArtifactInvariants.prunedToOrder(input, List.of("a", "not-a-child"));

    assertEquals(List.of("a"), List.copyOf(pruned.keySet()));
  }
}
