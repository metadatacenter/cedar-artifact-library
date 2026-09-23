package org.metadatacenter.artifacts.model.core.fields.constraints;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;

/**
 * One option a field offers, and whether it is selected when nothing else is.
 * <p>
 * {@code selectedByDefault} is {@code null} where the source said nothing, which is not the same
 * as saying {@code false} even though the two mean the same thing to a reader of the artifact.
 * The literals array is {@code uniqueItems} in the CEDAR meta-schema, so a list holding both
 * {@code {"label": "X"}} and {@code {"label": "X", "selectedByDefault": false}} is two distinct
 * entries to a validator; rendering the second as the first makes them one repeated value and
 * takes the whole field down with it. Keeping what the source stated is what stops a valid
 * template becoming an invalid one.
 */
public record LiteralValueConstraint(String label,
                                     @JsonInclude(JsonInclude.Include.NON_NULL) Boolean selectedByDefault)
{
  public LiteralValueConstraint
  {
    validateStringFieldNotNull(this, label, VALUE_CONSTRAINTS_LABEL);
  }

  /** An option whose selection the source did not state. */
  public LiteralValueConstraint(String label)
  {
    this(label, null);
  }

  /**
   * Whether this option is the selected one, saying nothing being the same as saying no.
   * <p>
   * Not serialized: a bean-named accessor over a record component is the one Jackson would take,
   * and this one answers a primitive, which would write the unstated case as an explicit false.
   */
  @JsonIgnore
  public boolean isSelectedByDefault()
  {
    return Boolean.TRUE.equals(selectedByDefault);
  }

  /** Whether the source said anything about it, which is what a round trip has to preserve. */
  @JsonIgnore
  public boolean statesSelectedByDefault()
  {
    return selectedByDefault != null;
  }
}
