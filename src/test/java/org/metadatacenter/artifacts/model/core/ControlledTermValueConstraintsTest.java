package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControlledTermValueConstraintsTest
{
  @Test public void testBuilderWithOntologyAndClass()
  {
    OntologyValueConstraint ontology = new OntologyValueConstraint(
      URI.create("https://data.bioontology.org/ontologies/DOID"),
      "DOID", "Human Disease Ontology", Optional.of(15000));
    ClassValueConstraint clazz = new ClassValueConstraint(
      URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3"),
      "LOINC", "Homo Sapiens", "Homo Sapiens", ValueType.ONTOLOGY_CLASS);

    ControlledTermValueConstraints constraints = ControlledTermValueConstraints.builder()
      .withOntologyValueConstraint(ontology)
      .withClassValueConstraint(clazz)
      .withRequiredValue(true)
      .build();

    assertEquals(1, constraints.ontologies().size());
    assertEquals(ontology, constraints.ontologies().get(0));
    assertEquals(1, constraints.classes().size());
    assertEquals(clazz, constraints.classes().get(0));
    assertEquals(true, constraints.requiredValue());
    assertTrue(constraints.hasExplicitConstraints());
  }

  @Test public void testBuilderWithBranchAndValueSet()
  {
    BranchValueConstraint branch = new BranchValueConstraint(
      URI.create("http://purl.org/twc/dpo/ont/Disease"), "DPCO", "Diabetes Pharmacology Ontology",
      "Disease", 3);
    ValueSetValueConstraint valueSet = new ValueSetValueConstraint(
      URI.create("https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"), "HRAVS", "Area unit",
      Optional.of(40));

    ControlledTermValueConstraints constraints = ControlledTermValueConstraints.builder()
      .withBranchValueConstraint(branch)
      .withValueSetValueConstraint(valueSet)
      .build();

    assertEquals(1, constraints.branches().size());
    assertEquals(branch, constraints.branches().get(0));
    assertEquals(1, constraints.valueSets().size());
    assertEquals(valueSet, constraints.valueSets().get(0));
    assertTrue(constraints.hasExplicitConstraints());
  }

  @Test public void testBuilderWithActionAndDefaultValue()
  {
    URI termUri = URI.create("https://purl.org/datacite/v4.4/TranslatedTitle");
    ControlledTermValueConstraintsAction action = new ControlledTermValueConstraintsAction.Builder()
      .withTermUri(termUri).withSource("DATACITE-VOCAB").withType(ValueType.ONTOLOGY_CLASS)
      .withAction(ValueConstraintsActionType.MOVE).build();

    URI defaultUri = URI.create("https://example.com/disease/1");
    String defaultLabel = "Diabetes";

    ControlledTermValueConstraints constraints = ControlledTermValueConstraints.builder()
      .withValueConstraintsAction(action)
      .withDefaultValue(defaultUri, defaultLabel)
      .build();

    assertEquals(1, constraints.actions().size());
    assertEquals(action, constraints.actions().get(0));
    assertEquals(defaultUri, constraints.defaultValue().get().termUri());
    assertEquals(defaultLabel, constraints.defaultValue().get().label());
  }
}
