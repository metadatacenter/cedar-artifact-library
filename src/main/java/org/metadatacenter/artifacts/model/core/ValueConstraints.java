package org.metadatacenter.artifacts.model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTIONS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DECIMAL_PLACE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LITERALS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_STRING_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_STRING_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TEMPORAL_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_UNIT_OF_MEASURE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS;

public interface ValueConstraints
{
  boolean requiredValue();

  boolean multipleChoice();

  Optional<NumberType> numberType();

  Optional<String> unitOfMeasure();

  Optional<Number> minValue();

  Optional<Number> maxValue();

  Optional<Integer> decimalPlaces();

  Optional<Integer> minLength();

  Optional<Integer> maxLength();

  Optional<TemporalType> temporalType();

  List<OntologyValueConstraint> ontologies();

  List<ValueSetValueConstraint> valueSets();

  List<ClassValueConstraint> classes();

  List<BranchValueConstraint> branches();

  List<LiteralValueConstraint> literals();

  Optional<DefaultValue> defaultValue();

  List<ValueConstraintsAction> actions();

  boolean hasLiteralBasedConstraint();

  boolean hasValueBasedConstraints();

  boolean hasOntologyValueBasedConstraints();

  static ValueConstraints create(boolean requiredValue, boolean multipleChoice, Optional<NumberType> numberType,
    Optional<String> unitOfMeasure, Optional<Number> minValue, Optional<Number> maxValue,
    Optional<Integer> decimalPlaces, Optional<Integer> minLength, Optional<Integer> maxLength,
    Optional<TemporalType> temporalType, List<OntologyValueConstraint> ontologies,
    List<ValueSetValueConstraint> valueSets, List<ClassValueConstraint> classes, List<BranchValueConstraint> branches,
    List<LiteralValueConstraint> literals, Optional<DefaultValue> defaultValue, List<ValueConstraintsAction> actions)
  {
    return new ValueConstraintsRecord(requiredValue, multipleChoice, numberType, unitOfMeasure, minValue, maxValue,
      decimalPlaces, minLength, maxLength, temporalType, ontologies, valueSets, classes, branches, literals,
      defaultValue, actions);
  }

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private boolean requiredValue = false;
    private boolean multipleChoice = false;
    private Optional<NumberType> numberType = Optional.empty();
    private Optional<String> unitOfMeasure = Optional.empty();
    private Optional<Number> minValue = Optional.empty();
    private Optional<Number> maxValue = Optional.empty();
    private Optional<Integer> decimalPlaces = Optional.empty();
    private Optional<Integer> minLength = Optional.empty();
    private Optional<Integer> maxLength = Optional.empty();
    private Optional<TemporalType> temporalType = Optional.empty();
    private Optional<DefaultValue> defaultValue = Optional.empty();
    private List<OntologyValueConstraint> ontologies = new ArrayList<>();
    private List<ValueSetValueConstraint> valueSets = new ArrayList<>();
    private List<ClassValueConstraint> classes = new ArrayList<>();
    private List<BranchValueConstraint> branches = new ArrayList<>();
    private List<LiteralValueConstraint> literals = new ArrayList<>();
    private List<ValueConstraintsAction> actions = new ArrayList<>();

    private Builder() {
    }

    public Builder withRequiredValue(boolean requiredValue) {
      this.requiredValue = requiredValue;
      return this;
    }

    public Builder withMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public Builder withNumberType(NumberType numberType) {
      this.numberType = Optional.ofNullable(numberType);
      return this;
    }

    public Builder withUnitOfMeasure(String unitOfMeasure) {
      this.unitOfMeasure = Optional.ofNullable(unitOfMeasure);
      return this;
    }

    public Builder withMinValue(Number minValue) {
      this.minValue = Optional.ofNullable(minValue);
      return this;
    }

    public Builder withMaxValue(Number maxValue) {
      this.maxValue = Optional.ofNullable(maxValue);
      return this;
    }

    public Builder withDecimalPlaces(Integer decimalPlaces) {
      this.decimalPlaces = Optional.ofNullable(decimalPlaces);
      return this;
    }

    public Builder withMinLength(Integer minLength) {
      this.minLength = Optional.ofNullable(minLength);
      return this;
    }

    public Builder withMaxLength(Integer maxLength) {
      this.maxLength = Optional.ofNullable(maxLength);
      return this;
    }

    public Builder withTemporalType(TemporalType temporalType) {
      this.temporalType = Optional.ofNullable(temporalType);
      return this;
    }

    public Builder withOntologyValueConstraint(OntologyValueConstraint constraint) {
      ontologies.add(constraint);
      return this;
    }

    public Builder withValueSetValueConstraint(ValueSetValueConstraint constraint) {
      valueSets.add(constraint);
      return this;
    }

    public Builder withClassValueConstraint(ClassValueConstraint constraint) {
      classes.add(constraint);
      return this;
    }

    public Builder withBranchValueConstraint(BranchValueConstraint constraint) {
      branches.add(constraint);
      return this;
    }

    public Builder withLiteralValueConstraint(LiteralValueConstraint constraint) {
      literals.add(constraint);
      return this;
    }

    public Builder withDefaultValue(DefaultValue defaultValue) {
      this.defaultValue = Optional.ofNullable(defaultValue);
      return this;
    }

    public Builder withValueConstraintsAction(ValueConstraintsAction action) {
      actions.add(action);
      return this;
    }

    public ValueConstraints build()
    {
      return new ValueConstraintsRecord(requiredValue, multipleChoice, numberType, unitOfMeasure, minValue, maxValue,
        decimalPlaces, minLength, maxLength, temporalType, ontologies, valueSets, classes, branches, literals,
        defaultValue, actions);
    }
  }
}

record ValueConstraintsRecord(boolean requiredValue, boolean multipleChoice, Optional<NumberType> numberType,
  Optional<String> unitOfMeasure, Optional<Number> minValue, Optional<Number> maxValue,
  Optional<Integer> decimalPlaces, Optional<Integer> minLength, Optional<Integer> maxLength,
  Optional<TemporalType> temporalType, List<OntologyValueConstraint> ontologies,
  List<ValueSetValueConstraint> valueSets, List<ClassValueConstraint> classes,
  List<BranchValueConstraint> branches, List<LiteralValueConstraint> literals,
  Optional<DefaultValue> defaultValue, List<ValueConstraintsAction> actions) implements ValueConstraints {

  public ValueConstraintsRecord
  {
    validateOptionalFieldNotNull(this, numberType, VALUE_CONSTRAINTS_NUMBER_TYPE);
    validateOptionalFieldNotNull(this, unitOfMeasure, VALUE_CONSTRAINTS_UNIT_OF_MEASURE);
    validateOptionalFieldNotNull(this, minValue, VALUE_CONSTRAINTS_MIN_NUMBER_VALUE);
    validateOptionalFieldNotNull(this, maxValue, VALUE_CONSTRAINTS_MAX_NUMBER_VALUE);
    validateOptionalFieldNotNull(this, decimalPlaces, VALUE_CONSTRAINTS_DECIMAL_PLACE);
    validateOptionalFieldNotNull(this, minLength, VALUE_CONSTRAINTS_MIN_STRING_LENGTH);
    validateOptionalFieldNotNull(this, maxLength, VALUE_CONSTRAINTS_MAX_STRING_LENGTH);
    validateOptionalFieldNotNull(this, temporalType, VALUE_CONSTRAINTS_TEMPORAL_TYPE);
    validateListFieldNotNull(this, ontologies, VALUE_CONSTRAINTS_ONTOLOGIES);
    validateListFieldNotNull(this, valueSets, VALUE_CONSTRAINTS_VALUE_SETS);
    validateListFieldNotNull(this, branches, VALUE_CONSTRAINTS_BRANCHES);
    validateListFieldNotNull(this, literals, VALUE_CONSTRAINTS_LITERALS);
    validateListFieldNotNull(this, actions, VALUE_CONSTRAINTS_ACTIONS);

    ontologies = List.copyOf(ontologies);
    valueSets = List.copyOf(valueSets);
    classes = List.copyOf(classes);
    branches = List.copyOf(branches);
    literals = List.copyOf(literals);
    actions = List.copyOf(actions);
  }

  @Override public boolean hasLiteralBasedConstraint() {
    return !literals.isEmpty();
  }

  @Override public boolean hasValueBasedConstraints()
  {
    return !ontologies.isEmpty() || !valueSets.isEmpty() || !classes.isEmpty() || !branches.isEmpty() || !literals.isEmpty();
  }

  @Override public boolean hasOntologyValueBasedConstraints()
  {
    return !ontologies.isEmpty() || !valueSets.isEmpty() || !classes.isEmpty() || !branches.isEmpty();
  }
}
