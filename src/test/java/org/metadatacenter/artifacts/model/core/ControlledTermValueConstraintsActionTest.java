package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

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
      .withType(valueType)
      .withAction(actionType)
      .withTo(123)
      .build();

    assertEquals(termUri, controlledTermValueConstraintsAction.termUri());
    assertEquals(sourceUri, controlledTermValueConstraintsAction.sourceUri().get());
    assertEquals(source, controlledTermValueConstraintsAction.source());
    assertEquals(valueType, controlledTermValueConstraintsAction.type());
    assertEquals(actionType, controlledTermValueConstraintsAction.action());
    assertEquals(to, controlledTermValueConstraintsAction.to());
  }
}