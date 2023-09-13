package org.metadatacenter.artifacts.model.core;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTIONS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS;

public non-sealed interface ControlledTermValueConstraints extends ValueConstraints
{
  List<OntologyValueConstraint> ontologies();

  List<ValueSetValueConstraint> valueSets();

  List<ClassValueConstraint> classes();

  List<BranchValueConstraint> branches();

  List<ControlledTermValueConstraintsAction> actions();

  default boolean hasValues() {
    return !ontologies().isEmpty() || !valueSets().isEmpty() || !classes().isEmpty() || !branches().isEmpty();
  }

  @Override Optional<ControlledTermDefaultValue> defaultValue();

  static ValueConstraints create(List<OntologyValueConstraint> ontologies, List<ValueSetValueConstraint> valueSets, List<ClassValueConstraint> classes, List<BranchValueConstraint> branches,
    Optional<ControlledTermDefaultValue> defaultValue, List<ControlledTermValueConstraintsAction> actions,
    boolean requiredValue, boolean multipleChoice)
  {
    return new ControlledTermValueConstraintsRecord(ontologies, valueSets, classes, branches, actions,
      defaultValue, requiredValue, multipleChoice);
  }

  static Builder builder()
  {
    return new Builder();
  }

  class Builder
  {
    private List<OntologyValueConstraint> ontologies = new ArrayList<>();
    private List<ValueSetValueConstraint> valueSets = new ArrayList<>();
    private List<ClassValueConstraint> classes = new ArrayList<>();
    private List<BranchValueConstraint> branches = new ArrayList<>();
    private List<ControlledTermValueConstraintsAction> actions = new ArrayList<>();
    private Optional<ControlledTermDefaultValue> defaultValue = Optional.empty();
    private boolean requiredValue = false;
    private boolean multipleChoice = false;

    private Builder()
    {
    }

    public Builder withOntologyValueConstraint(OntologyValueConstraint constraint)
    {
      ontologies.add(constraint);
      return this;
    }

    public Builder withValueSetValueConstraint(ValueSetValueConstraint constraint)
    {
      valueSets.add(constraint);
      return this;
    }

    public Builder withClassValueConstraint(ClassValueConstraint constraint)
    {
      classes.add(constraint);
      return this;
    }

    public Builder withBranchValueConstraint(BranchValueConstraint constraint)
    {
      branches.add(constraint);
      return this;
    }

    public Builder withValueConstraintsAction(ControlledTermValueConstraintsAction action)
    {
      actions.add(action);
      return this;
    }

    public Builder withDefaultValue(URI uri, String label)
    {
      this.defaultValue = Optional.of(new ControlledTermDefaultValue(new ImmutablePair<>(uri, label)));
      return this;
    }

    public Builder withRequiredValue(boolean requiredValue)
    {
      this.requiredValue = requiredValue;
      return this;
    }

    public Builder withMultipleChoice(boolean multipleChoice)
    {
      this.multipleChoice = multipleChoice;
      return this;
    }

    public ValueConstraints build()
    {
      return new ControlledTermValueConstraintsRecord(ontologies, valueSets, classes, branches, actions,
        defaultValue, requiredValue, multipleChoice);
    }
  }
}

record ControlledTermValueConstraintsRecord( List<OntologyValueConstraint> ontologies,
                              List<ValueSetValueConstraint> valueSets, List<ClassValueConstraint> classes,
                              List<BranchValueConstraint> branches, List<ControlledTermValueConstraintsAction> actions,
                              Optional<ControlledTermDefaultValue> defaultValue,
                              boolean requiredValue, boolean multipleChoice) implements ControlledTermValueConstraints {

  public ControlledTermValueConstraintsRecord
  {
    validateListFieldNotNull(this, ontologies, VALUE_CONSTRAINTS_ONTOLOGIES);
    validateListFieldNotNull(this, valueSets, VALUE_CONSTRAINTS_VALUE_SETS);
    validateListFieldNotNull(this, branches, VALUE_CONSTRAINTS_BRANCHES);
    validateListFieldNotNull(this, actions, VALUE_CONSTRAINTS_ACTIONS);

    ontologies = List.copyOf(ontologies);
    valueSets = List.copyOf(valueSets);
    classes = List.copyOf(classes);
    branches = List.copyOf(branches);
    actions = List.copyOf(actions);
  }
}
