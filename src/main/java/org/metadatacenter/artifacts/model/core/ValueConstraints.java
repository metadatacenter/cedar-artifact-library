package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Optional;

public sealed interface ValueConstraints permits TextValueConstraints, NumericValueConstraints,
  ControlledTermValueConstraints, TemporalValueConstraints
{
  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  boolean requiredValue();

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  boolean multipleChoice();

  Optional<? extends DefaultValue> defaultValue();

  default boolean isTextValueConstraint() { return this instanceof TextValueConstraints; }

  default boolean isNumericValueConstraint() { return this instanceof NumericValueConstraints; }

  default boolean isControlledTermValueConstraint() { return this instanceof ControlledTermValueConstraints; }

  default boolean isTemporalValueConstraint() { return this instanceof TemporalValueConstraints; }

  default TextValueConstraints asTextValueConstraints()
  {
    if (this instanceof TextValueConstraints) // TODO Use typesafe switch when available
      return (TextValueConstraints)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TextValueConstraints.class.getName());
  }

  default NumericValueConstraints asNumericValueConstraints()
  {
    if (this instanceof NumericValueConstraints) // TODO Use typesafe switch when available
      return (NumericValueConstraints)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + NumericValueConstraints.class.getName());
  }

  default ControlledTermValueConstraints asControlledTermValueConstraints()
  {
    if (this instanceof ControlledTermValueConstraints) // TODO Use typesafe switch when available
      return (ControlledTermValueConstraints)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + ControlledTermValueConstraints.class.getName());
  }

  default TemporalValueConstraints asTemporalValueConstraints()
  {
    if (this instanceof TemporalValueConstraints) // TODO Use typesafe switch when available
      return (TemporalValueConstraints)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TemporalValueConstraints.class.getName());
  }

}
