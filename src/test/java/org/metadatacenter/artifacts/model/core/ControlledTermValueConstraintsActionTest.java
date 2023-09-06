package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.*;

public class ControlledTermValueConstraintsActionTest
{

  @Test
  public void testValueConstraintsActionBuilder() {
    URI termUri = URI.create("https://purl.org/datacite/v4.4/TranslatedTitle");
    URI sourceUri = URI.create("https://purl.org/datacite/v4.4/TitleType");
    String source = "DATACITE-VOCAB";
    ValueType valueType = ValueType.ONTOLOGY_CLASS;
    ValueConstraintsActionType actionType = ValueConstraintsActionType.DELETE;
    Optional<Integer> to = Optional.of(123);

    ControlledTermValueConstraintsAction controlledTermValueConstraintsAction = new ControlledTermValueConstraintsAction.Builder()
      .withTermUri(termUri)
      .withSourceUri(sourceUri)
      .withSource(source)
      .withValueType(valueType)
      .withActionType(actionType)
      .withTo(123)
      .build();

    assertEquals(termUri, controlledTermValueConstraintsAction.termUri());
    assertEquals(sourceUri, controlledTermValueConstraintsAction.sourceUri().get());
    assertEquals(source, controlledTermValueConstraintsAction.source());
    assertEquals(valueType, controlledTermValueConstraintsAction.valueType());
    assertEquals(actionType, controlledTermValueConstraintsAction.actionType());
    assertEquals(to, controlledTermValueConstraintsAction.to());
  }
}