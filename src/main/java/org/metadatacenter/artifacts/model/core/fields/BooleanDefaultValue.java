package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The default value of a boolean field.
 * <p>
 * A boolean field distinguishes three states for its default: no default (represented by an empty
 * {@link java.util.Optional}), a concrete {@code true}/{@code false} default, and an explicit null
 * default (allowed when the field enables a null choice). The explicit-null default is represented
 * by a present instance whose {@link #value()} is {@code null}, so it stays distinct from the
 * no-default case.
 */
public record BooleanDefaultValue(@JsonValue Boolean value) implements DefaultValue<Boolean>
{
  @Override public DefaultValueType getValueType()
  {
    return DefaultValueType.BOOLEAN;
  }
}
