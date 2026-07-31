package org.metadatacenter.artifacts.util;

import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.VersionSpec;

import java.util.List;
import java.util.Optional;

/**
 * Freeze-on-publish for controlled-term value constraints (VERSIONING-DESIGN §7). Given a resolver
 * that maps an ontology to its current version triple, returns a copy of the constraints with every
 * <b>unpinned</b> ontology or branch entry stamped with that triple, so a published template resolves
 * to a fixed vocabulary state forever instead of drifting with "latest".
 *
 * The transformation is pure — the resolver is injected — so freezing is testable without a live
 * terminology server. Freezing is deliberately <b>not</b> a terminology-server operation: the server
 * exposes only "resolve current → triple"; a resolver adapts that call, and this walk stamps the
 * result. Entries that already carry a version are left untouched (idempotent), and an entry the
 * resolver cannot resolve (not served locally, unknown acronym) is left unpinned rather than guessed.
 *
 * Ontology and branch entries carry an {@code acronym} and are frozen. Class and value-set entries do
 * not name their ontology directly and are left unchanged for now (a documented refinement).
 */
public final class ControlledTermVersionFreezer {

  /** Resolves an ontology acronym to its current version triple, or empty when it cannot be resolved.
   *  Backed in production by a call to the terminology server's resolve-current endpoint. */
  @FunctionalInterface
  public interface VersionResolver {
    Optional<VersionSpec> currentVersion(String acronym);
  }

  /**
   * A copy of {@code constraints} with each unpinned ontology/branch entry frozen to its current
   * version. Already-pinned entries and unresolvable entries are returned unchanged; class and
   * value-set entries pass through untouched.
   */
  public static ControlledTermValueConstraints freeze(ControlledTermValueConstraints constraints,
                                                      VersionResolver resolver) {
    List<OntologyValueConstraint> ontologies = constraints.ontologies().stream()
        .map(o -> freezeOntology(o, resolver)).toList();
    List<BranchValueConstraint> branches = constraints.branches().stream()
        .map(b -> freezeBranch(b, resolver)).toList();

    return (ControlledTermValueConstraints) ControlledTermValueConstraints.create(
        ontologies, constraints.valueSets(), constraints.classes(), branches,
        constraints.defaultValue(), constraints.actions(), constraints.requiredValue(),
        constraints.recommendedValue(), constraints.multipleChoice());
  }

  private static OntologyValueConstraint freezeOntology(OntologyValueConstraint o, VersionResolver resolver) {
    if (o.version().isPresent()) {
      return o; // already pinned — idempotent
    }
    return resolver.currentVersion(o.acronym())
        .map(v -> new OntologyValueConstraint(o.uri(), o.acronym(), o.name(), o.numTerms(),
            o.iri(), o.sourceSystem(), Optional.of(v)))
        .orElse(o); // cannot resolve — leave unpinned
  }

  private static BranchValueConstraint freezeBranch(BranchValueConstraint b, VersionResolver resolver) {
    if (b.version().isPresent()) {
      return b;
    }
    return resolver.currentVersion(b.acronym())
        .map(v -> new BranchValueConstraint(b.uri(), b.source(), b.acronym(), b.name(), b.maxDepth(),
            b.iri(), b.sourceSystem(), Optional.of(v)))
        .orElse(b);
  }

  private ControlledTermVersionFreezer() {}
}
