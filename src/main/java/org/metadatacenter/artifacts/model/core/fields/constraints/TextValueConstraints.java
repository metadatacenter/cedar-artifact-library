package org.metadatacenter.artifacts.model.core.fields.constraints;

import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;

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

  Optional<String> regex();

  static TextValueConstraints create(Optional<Integer> minLength, Optional<Integer> maxLength,
    Optional<TextDefaultValue> defaultValue, List<LiteralValueConstraint> literals,
    boolean requiredValue, boolean multipleChoice, Optional<String> regex)
  {
    return new TextValueConstraintsRecord(minLength, maxLength, defaultValue, literals, requiredValue, multipleChoice, regex);
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private Optional<Integer> minLength = Optional.empty();
    private Optional<Integer> maxLength = Optional.empty();
    private Optional<TextDefaultValue> defaultValue = Optional.empty();
    private final List<LiteralValueConstraint> literals = new ArrayList<>();
    private boolean requiredValue = false;
    private boolean multipleChoice = false;
    private Optional<String> regex = Optional.empty();

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

    public Builder withDefaultValue(String defaultValue) {
      this.defaultValue = Optional.ofNullable(new TextDefaultValue(defaultValue));
      return this;
    }

    public Builder withChoice(String choice, boolean selectedByDefault) {
      this.literals.add(new LiteralValueConstraint(choice, selectedByDefault));
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

    public Builder withRegex(String regex) {
      this.regex = Optional.ofNullable(regex);
      return this;
    }

    public ValueConstraints build()
    {
      return new TextValueConstraintsRecord(minLength, maxLength, defaultValue, literals, requiredValue, multipleChoice, regex);
    }
  }
}

record TextValueConstraintsRecord(Optional<Integer> minLength, Optional<Integer> maxLength,
                                  Optional<TextDefaultValue> defaultValue, List<LiteralValueConstraint> literals,
                                  boolean requiredValue, boolean multipleChoice, Optional<String> regex) implements TextValueConstraints {

  public TextValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, minLength, VALUE_CONSTRAINTS_MIN_STRING_LENGTH);
    validateOptionalFieldNotNull(this, maxLength, VALUE_CONSTRAINTS_MAX_STRING_LENGTH);
    validateOptionalFieldNotNull(this, defaultValue, VALUE_CONSTRAINTS_DEFAULT_VALUE);
    validateListFieldNotNull(this, literals, VALUE_CONSTRAINTS_LITERALS);
    validateOptionalFieldNotNull(this, regex, "regex"); // TODO Add 'regex' to ModelNodeNames
  }
}
