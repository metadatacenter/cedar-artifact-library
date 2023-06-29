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
    Optional<URI> sourceUri = Optional.of(URI.create("https://purl.org/datacite/v4.4/TitleType"));
    String source = "DATACITE-VOCAB";
    ValueType type = ValueType.ONTOLOGY_CLASS;
    ValueConstraintsActionType action = ValueConstraintsActionType.DELETE;
    Optional<Integer> to = Optional.of(123);

    ValueConstraintsAction valueConstraintsActionObj = new ValueConstraintsAction.Builder()
      .termUri(termUri)
      .sourceUri(URI.create("https://purl.org/datacite/v4.4/TitleType"))
      .source(source)
      .type(type)
      .action(action)
      .to(123)
      .build();

    assertEquals(termUri, valueConstraintsActionObj.getTermUri());
    assertEquals(sourceUri, valueConstraintsActionObj.getSourceUri());
    assertEquals(source, valueConstraintsActionObj.getSource());
    assertEquals(type, valueConstraintsActionObj.getType());
    assertEquals(action, valueConstraintsActionObj.getAction());
    assertEquals(to, valueConstraintsActionObj.getTo());
  }
}