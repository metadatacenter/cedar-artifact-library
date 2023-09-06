package org.metadatacenter.artifacts.model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LITERALS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_STRING_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_STRING_LENGTH;

public non-sealed interface TextValueConstraints extends ValueConstraints
{
  Optional<Integer> minLength();

  Optional<Integer> maxLength();

  Optional<TextDefaultValue> defaultValue();

  List<LiteralValueConstraint> literals();

  static TextValueConstraints create(Optional<Integer> minLength, Optional<Integer> maxLength,
    Optional<TextDefaultValue> defaultValue, List<LiteralValueConstraint> literals, boolean requiredValue, boolean multipleChoice)
  {
    return new TextValueConstraintsRecord(minLength, maxLength, defaultValue, literals, requiredValue, multipleChoice);
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private Optional<Integer> minLength = Optional.empty();
    private Optional<Integer> maxLength = Optional.empty();
    private Optional<TextDefaultValue> defaultValue = Optional.empty();
    private List<LiteralValueConstraint> literals = new ArrayList<>();
    private boolean requiredValue = false;
    private boolean multipleChoice = false;

    private Builder() {
    }

    public Builder withMinLength(Integer minLength) {
      this.minLength = Optional.ofNullable(minLength);
      return this;
    }

    public Builder withMaxLength(Integer maxLength) {
      this.maxLength = Optional.ofNullable(maxLength);
      return this;
    }

    public Builder withDefaultValue(TextDefaultValue defaultValue) {
      this.defaultValue = Optional.ofNullable(defaultValue);
      return this;
    }

    public Builder withLiterals(List<LiteralValueConstraint> literals) {
      this.literals = List.copyOf(literals);
      return this;
    }

    public Builder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public Builder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public ValueConstraints build()
    {
      return new TextValueConstraintsRecord(minLength, maxLength, defaultValue, literals, requiredValue, multipleChoice);
    }
  }
}

record TextValueConstraintsRecord(Optional<Integer> minLength, Optional<Integer> maxLength,
                                  Optional<TextDefaultValue> defaultValue, List<LiteralValueConstraint> literals,
                                  boolean requiredValue, boolean multipleChoice) implements TextValueConstraints {

  public TextValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, minLength, VALUE_CONSTRAINTS_MIN_STRING_LENGTH);
    validateOptionalFieldNotNull(this, maxLength, VALUE_CONSTRAINTS_MAX_STRING_LENGTH);
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
    validateListFieldNotNull(this, literals, VALUE_CONSTRAINTS_LITERALS);
  }
}
