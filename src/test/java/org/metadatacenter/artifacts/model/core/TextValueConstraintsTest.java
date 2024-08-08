package org.metadatacenter.artifacts.model.core;

import org.junit.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;

import static org.junit.Assert.assertEquals;

public class TextValueConstraintsTest
{
  @Test public void textValueConstraintsBuildTest()
  {
    Integer minLength = 1;
    Integer maxLength = 10;

    TextValueConstraints textValueConstraints = TextValueConstraints.builder()
      .withMinLength(minLength)
      .withMaxLength(maxLength)
      .build();

    assertEquals(minLength, textValueConstraints.minLength().get());
    assertEquals(maxLength, textValueConstraints.maxLength().get());
  }
}