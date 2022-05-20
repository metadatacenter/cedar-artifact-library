package org.metadatacenter.model.core;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ValueConstraints
{
  private final boolean requiredValue;
  private final boolean multipleChoice;
  private final Optional<String> numberType;
  private final Optional<String> unitOfMeasure;
  private final Optional<String> minValue;
  private final Optional<String> maxValue;
  private final Optional<Integer> decimalPlaces;
  private final Optional<Integer> minLength;
  private final Optional<Integer> maxLength;
  private final Optional<String> temporalType;
  private final List<OntologyValueConstraint> ontologies;
  private final List<ValueSetValueConstraint> valueSets;
  private final List<ClassValueConstraint> classes;
  private final List<BranchValueConstraint> branches;
  private final List<LiteralValueConstraint> literals;
  private final Optional<String> defaultValue;

  public ValueConstraints(boolean requiredValue, boolean multipleChoice, Optional<String> numberType,
    Optional<String> unitOfMeasure, Optional<String> minValue, Optional<String> maxValue,
    Optional<Integer> decimalPlaces, Optional<Integer> minLength, Optional<Integer> maxLength,
    Optional<String> temporalType, List<OntologyValueConstraint> ontologies, List<ValueSetValueConstraint> valueSets,
    List<ClassValueConstraint> classes, List<BranchValueConstraint> branches, List<LiteralValueConstraint> literals,
    Optional<String> defaultValue)
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

  public Optional<String> getNumberType()
  {
    return numberType;
  }

  public Optional<String> getUnitOfMeasure()
  {
    return unitOfMeasure;
  }

  public Optional<String> getMinValue()
  {
    return minValue;
  }

  public Optional<String> getMaxValue()
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

  public Optional<String> getTemporalType()
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

  public Optional<String> getDefaultValue()
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
