package org.metadatacenter.artifacts.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ValueConstraints
{
  private final boolean requiredValue;
  private final boolean multipleChoice;
  private final Optional<NumberType> numberType;
  private final Optional<String> unitOfMeasure;
  private final Optional<Number> minValue;
  private final Optional<Number> maxValue;
  private final Optional<Integer> decimalPlaces;
  private final Optional<Integer> minLength;
  private final Optional<Integer> maxLength;
  private final Optional<TemporalType> temporalType;
  private final List<OntologyValueConstraint> ontologies;
  private final List<ValueSetValueConstraint> valueSets;
  private final List<ClassValueConstraint> classes;
  private final List<BranchValueConstraint> branches;
  private final List<LiteralValueConstraint> literals;
  private final Optional<DefaultValue> defaultValue;

  public ValueConstraints(boolean requiredValue, boolean multipleChoice, Optional<NumberType> numberType,
    Optional<String> unitOfMeasure, Optional<Number> minValue, Optional<Number> maxValue,
    Optional<Integer> decimalPlaces, Optional<Integer> minLength, Optional<Integer> maxLength,
    Optional<TemporalType> temporalType, List<OntologyValueConstraint> ontologies, List<ValueSetValueConstraint> valueSets,
    List<ClassValueConstraint> classes, List<BranchValueConstraint> branches, List<LiteralValueConstraint> literals,
    Optional<DefaultValue> defaultValue)
  {
    this.requiredValue = requiredValue;
    this.multipleChoice = multipleChoice;
    this.numberType = numberType;
    this.unitOfMeasure = unitOfMeasure;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.decimalPlaces = decimalPlaces;
    this.minLength = minLength;
    this.maxLength = maxLength;
    this.temporalType = temporalType;
    this.ontologies = Collections.unmodifiableList(ontologies);
    this.valueSets = Collections.unmodifiableList(valueSets);
    this.classes = Collections.unmodifiableList(classes);
    this.branches = Collections.unmodifiableList(branches);
    this.literals = Collections.unmodifiableList(literals);
    this.defaultValue = defaultValue;
  }

  public boolean isRequiredValue()
  {
    return requiredValue;
  }

  public boolean isMultipleChoice()
  {
    return multipleChoice;
  }

  public Optional<NumberType> getNumberType()
  {
    return numberType;
  }

  public Optional<String> getUnitOfMeasure()
  {
    return unitOfMeasure;
  }

  public Optional<Number> getMinValue()
  {
    return minValue;
  }

  public Optional<Number> getMaxValue()
  {
    return maxValue;
  }

  public Optional<Integer> getDecimalPlaces()
  {
    return decimalPlaces;
  }

  public Optional<Integer> getMinLength()
  {
    return minLength;
  }

  public Optional<Integer> getMaxLength()
  {
    return maxLength;
  }

  public Optional<TemporalType> getTemporalType()
  {
    return temporalType;
  }

  public List<OntologyValueConstraint> getOntologies()
  {
    return ontologies;
  }

  public List<ValueSetValueConstraint> getValueSets()
  {
    return valueSets;
  }

  public List<ClassValueConstraint> getClasses()
  {
    return classes;
  }

  public List<BranchValueConstraint> getBranches()
  {
    return branches;
  }

  public List<LiteralValueConstraint> getLiterals()
  {
    return literals;
  }

  public boolean hasValueBasedConstraints()
  {
    return !ontologies.isEmpty() || !valueSets.isEmpty() || !classes.isEmpty() || !branches.isEmpty() || !literals.isEmpty();
  }

  public boolean hasOntologyValueBasedConstraints()
  {
    return !ontologies.isEmpty() || !valueSets.isEmpty() || !classes.isEmpty() || !branches.isEmpty();
  }

  public Optional<DefaultValue> getDefaultValue()
  {
    return defaultValue;
  }

  @Override public String toString()
  {
    return "ValueConstraints{" + "requiredValue=" + requiredValue + ", multipleChoice=" + multipleChoice
      + ", numberType=" + numberType + ", unitOfMeasure=" + unitOfMeasure + ", minValue=" + minValue + ", maxValue="
      + maxValue + ", decimalPlaces=" + decimalPlaces + ", minLength=" + minLength + ", maxLength=" + maxLength
      + ", temporalType=" + temporalType + ", ontologies=" + ontologies + ", valueSets=" + valueSets + ", classes="
      + classes + ", branches=" + branches + ", literals=" + literals + ", defaultValue=" + defaultValue + '}';
  }
}
