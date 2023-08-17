package org.metadatacenter.artifacts.model.core;

import org.junit.Test;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.*;

public class ValueConstraintsActionTest
{

  @Test
  public void testValueConstraintsActionBuilder() {
    URI termUri = URI.create("https://purl.org/datacite/v4.4/TranslatedTitle");
    URI sourceUri = URI.create("https://purl.org/datacite/v4.4/TitleType");
    String source = "DATACITE-VOCAB";
    ValueType valueType = ValueType.ONTOLOGY_CLASS;
    ValueConstraintsActionType action = ValueConstraintsActionType.DELETE;
    Optional<Integer> to = Optional.of(123);

    ValueConstraintsAction valueConstraintsAction = new ValueConstraintsAction.Builder()
      .withTermUri(termUri)
      .withSourceUri(sourceUri)
      .withSource(source)
      .withValueType(valueType)
      .withAction(action)
      .withTo(123)
      .build();

    assertEquals(termUri, valueConstraintsAction.termUri());
    assertEquals(sourceUri, valueConstraintsAction.sourceUri().get());
    assertEquals(source, valueConstraintsAction.source());
    assertEquals(valueType, valueConstraintsAction.type());
    assertEquals(action, valueConstraintsAction.action());
    assertEquals(to, valueConstraintsAction.to());
  }
}