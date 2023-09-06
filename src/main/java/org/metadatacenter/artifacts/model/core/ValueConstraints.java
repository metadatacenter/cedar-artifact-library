package org.metadatacenter.artifacts.model.core;

import java.util.Optional;

public sealed interface ValueConstraints permits TextValueConstraints, NumericValueConstraints,
  ControlledTermValueConstraints, TemporalValueConstraints
{
  boolean requiredValue();

  boolean multipleChoice();

  Optional<? extends DefaultValue> defaultValue();
}
