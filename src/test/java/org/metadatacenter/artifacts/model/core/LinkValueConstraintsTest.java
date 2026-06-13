package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.LinkValueConstraints;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkValueConstraintsTest
{
  @Test public void testBuilderWithAllSetters()
  {
    URI defaultUri = URI.create("https://example.com/link");

    LinkValueConstraints constraints = LinkValueConstraints.builder()
      .withDefaultValue(defaultUri)
      .withRequiredValue(true)
      .withRecommendedValue(false)
      .withMultipleChoice(true)
      .build();

    assertEquals(defaultUri, constraints.defaultValue().get().termUri());
    assertEquals(true, constraints.requiredValue());
    assertEquals(false, constraints.recommendedValue());
    assertEquals(true, constraints.multipleChoice());
  }
}
