package org.metadatacenter.artifacts.util;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.VersionSpec;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControlledTermVersionFreezerTest {

  private static final VersionSpec DOID_V =
      new VersionSpec("63ef56dff672", Optional.of("2026-07-01"), Optional.of("2026-06-30"));

  /** A resolver that knows exactly one acronym. */
  private static ControlledTermVersionFreezer.VersionResolver resolverFor(String acronym, VersionSpec v) {
    return acr -> acronym.equals(acr) ? Optional.of(v) : Optional.empty();
  }

  @Test public void freezesUnpinnedOntologyEntryPreservingIdentityFields() {
    OntologyValueConstraint doid = new OntologyValueConstraint(
        URI.create("https://data.bioontology.org/ontologies/DOID"), "DOID", "Human Disease Ontology",
        Optional.of(19578), Optional.of(URI.create("http://purl.obolibrary.org/obo/doid")),
        Optional.of("BioPortal"), Optional.empty());
    ControlledTermValueConstraints vc =
        ControlledTermValueConstraints.builder().withOntologyValueConstraint(doid).build();

    ControlledTermValueConstraints frozen = ControlledTermVersionFreezer.freeze(vc, resolverFor("DOID", DOID_V));

    OntologyValueConstraint out = frozen.ontologies().get(0);
    assertEquals(Optional.of(DOID_V), out.version());       // frozen to the current triple
    assertEquals(doid.iri(), out.iri());                    // identity fields preserved
    assertEquals(doid.sourceSystem(), out.sourceSystem());
    assertEquals("DOID", out.acronym());
  }

  @Test public void leavesAlreadyPinnedEntryUnchanged() {
    VersionSpec existingPin = new VersionSpec("oldpin", Optional.empty(), Optional.empty());
    OntologyValueConstraint doid = new OntologyValueConstraint(
        URI.create("https://data.bioontology.org/ontologies/DOID"), "DOID", "n", Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.of(existingPin));
    ControlledTermValueConstraints vc =
        ControlledTermValueConstraints.builder().withOntologyValueConstraint(doid).build();

    ControlledTermValueConstraints frozen = ControlledTermVersionFreezer.freeze(vc, resolverFor("DOID", DOID_V));

    assertEquals(Optional.of(existingPin), frozen.ontologies().get(0).version()); // not re-frozen (idempotent)
  }

  @Test public void leavesUnresolvableEntryUnpinned() {
    OntologyValueConstraint mystery = new OntologyValueConstraint(
        URI.create("https://data.bioontology.org/ontologies/NOPE"), "NOPE", "n", Optional.empty());
    ControlledTermValueConstraints vc =
        ControlledTermValueConstraints.builder().withOntologyValueConstraint(mystery).build();

    // Resolver knows only DOID, so NOPE cannot be resolved and is left unpinned rather than guessed.
    ControlledTermValueConstraints frozen = ControlledTermVersionFreezer.freeze(vc, resolverFor("DOID", DOID_V));

    assertTrue(frozen.ontologies().get(0).version().isEmpty());
  }

  @Test public void freezesUnpinnedBranchEntry() {
    BranchValueConstraint branch = new BranchValueConstraint(
        URI.create("http://purl.obolibrary.org/obo/DOID_4"), "Human Disease Ontology (DOID)", "DOID", "disease", 0);
    ControlledTermValueConstraints vc =
        ControlledTermValueConstraints.builder().withBranchValueConstraint(branch).build();

    ControlledTermValueConstraints frozen = ControlledTermVersionFreezer.freeze(vc, resolverFor("DOID", DOID_V));

    assertEquals(Optional.of(DOID_V), frozen.branches().get(0).version());
    assertEquals("disease", frozen.branches().get(0).name()); // other fields preserved
  }
}
