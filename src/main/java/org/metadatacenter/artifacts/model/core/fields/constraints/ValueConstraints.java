package org.metadatacenter.artifacts.model.core.fields.constraints;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;

import java.util.Optional;

public sealed interface ValueConstraints permits TextValueConstraints, NumericValueConstraints,
  ControlledTermValueConstraints, TemporalValueConstraints, LinkValueConstraints
{
  boolean requiredValue();

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  boolean recommendedValue();

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  boolean multipleChoice();

  Optional<? extends DefaultValue> defaultValue();

  @JsonIgnore
  default boolean isTextValueConstraint() { return this instanceof TextValueConstraints; }

  @JsonIgnore
  default boolean isNumericValueConstraint() { return this instanceof NumericValueConstraints; }

  @JsonIgnore
  default boolean isControlledTermValueConstraint() { return this instanceof ControlledTermValueConstraints; }

  @JsonIgnore
  default boolean isTemporalValueConstraint() { return this instanceof TemporalValueConstraints; }

  @JsonIgnore
  default boolean isLinkValueConstraint() { return this instanceof LinkValueConstraints; }

  default TextValueConstraints asTextValueConstraints()
  {
    if (this instanceof TextValueConstraints) // TODO Use typesafe switch when available
      return (TextValueConstraints)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TextValueConstraints.class.getName());
  }

  default LinkValueConstraints asLinkValueConstraints()
  {
    if (this instanceof LinkValueConstraints) // TODO Use typesafe switch when available
      return (LinkValueConstraints)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + LinkValueConstraints.class.getName());
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
