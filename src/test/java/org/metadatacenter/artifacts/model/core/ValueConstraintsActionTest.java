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
    ValueType type = ValueType.ONTOLOGY_CLASS;
    ValueConstraintsActionType action = ValueConstraintsActionType.DELETE;
    Optional<Integer> to = Optional.of(123);

    ValueConstraintsAction valueConstraintsAction = new ValueConstraintsAction.Builder()
      .termUri(termUri)
      .sourceUri(sourceUri)
      .source(source)
      .type(type)
      .action(action)
      .to(123)
      .build();

    assertEquals(termUri, valueConstraintsAction.termUri());
    assertEquals(sourceUri, valueConstraintsAction.sourceUri().get());
    assertEquals(source, valueConstraintsAction.source());
    assertEquals(type, valueConstraintsAction.type());
    assertEquals(action, valueConstraintsAction.action());
    assertEquals(to, valueConstraintsAction.to());
  }
}