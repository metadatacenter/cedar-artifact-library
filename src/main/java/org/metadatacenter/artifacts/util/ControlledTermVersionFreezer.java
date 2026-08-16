package org.metadatacenter.artifacts.util;

import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.VersionSpec;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Freeze-on-publish for controlled-term value constraints (VERSIONING-ROADMAP "The Model" §7). Given a resolver
 * that maps a value space to its current version triple, returns a copy of the constraints with every
 * <b>unpinned</b> entry — ontology, branch, class, and value set — stamped with that triple, so a
 * published template resolves to a fixed vocabulary state forever instead of drifting with "latest".
 *
 * The transformation is pure — the resolver is injected — so freezing is testable without a live
 * terminology server. Freezing is deliberately <b>not</b> a terminology-server operation: the server
 * exposes only "resolve current → triple"; a resolver adapts that call, and this walk stamps the
 * result. Entries that already carry a version are left untouched (idempotent), and an entry the
 * resolver cannot resolve is left unpinned rather than guessed.
 *
 * The four entry kinds identify their value space differently, so the resolver takes the identifier
 * natural to each: an <b>acronym</b> for ontology and branch entries, the <b>class IRI</b> for class
 * entries (its ontology is derivable from the IRI's namespace), and the <b>value-set collection</b>
 * for value-set entries. Mapping those to an ontology/collection version is the resolver's job — the
 * terminology-server knowledge lives there, not here.
 */
public final class ControlledTermVersionFreezer {

  /**
   * Resolves a value space to its current version triple, or empty when it cannot be resolved (not
   * served locally, unknown). Backed in production by the terminology server's resolve-current
   * endpoint; each method corresponds to how one kind of entry names its value space.
   */
  public interface VersionResolver {
    /** By ontology acronym — ontology and branch entries. */
    Optional<VersionSpec> currentVersionByAcronym(String acronym);

    /** By the class IRI — class entries; the resolver maps the IRI to its ontology. */
    Optional<VersionSpec> currentVersionByClassUri(URI classUri);

    /** By value-set collection — value-set entries. */
    Optional<VersionSpec> currentVersionByValueSetCollection(String vsCollection);
  }

  /**
   * A copy of {@code constraints} with each unpinned entry frozen to its current version. Already-pinned
   * entries and entries the resolver cannot resolve are returned unchanged.
   */
  public static ControlledTermValueConstraints freeze(ControlledTermValueConstraints constraints,
                                                      VersionResolver resolver) {
    List<OntologyValueConstraint> ontologies = constraints.ontologies().stream()
        .map(o -> freezeOntology(o, resolver)).toList();
    List<BranchValueConstraint> branches = constraints.branches().stream()
        .map(b -> freezeBranch(b, resolver)).toList();
    List<ClassValueConstraint> classes = constraints.classes().stream()
        .map(c -> freezeClass(c, resolver)).toList();
    List<ValueSetValueConstraint> valueSets = constraints.valueSets().stream()
        .map(v -> freezeValueSet(v, resolver)).toList();

    return (ControlledTermValueConstraints) ControlledTermValueConstraints.create(
        ontologies, valueSets, classes, branches,
        constraints.defaultValue(), constraints.actions(), constraints.requiredValue(),
        constraints.recommendedValue(), constraints.multipleChoice());
  }

  private static OntologyValueConstraint freezeOntology(OntologyValueConstraint o, VersionResolver resolver) {
    if (o.version().isPresent()) {
      return o; // already pinned — idempotent
    }
    return resolver.currentVersionByAcronym(o.acronym())
        .map(v -> new OntologyValueConstraint(o.uri(), o.acronym(), o.name(), o.numTerms(),
            o.iri(), o.sourceSystem(), Optional.of(v)))
        .orElse(o); // cannot resolve — leave unpinned
  }

  private static BranchValueConstraint freezeBranch(BranchValueConstraint b, VersionResolver resolver) {
    if (b.version().isPresent()) {
      return b;
    }
    return resolver.currentVersionByAcronym(b.acronym())
        .map(v -> new BranchValueConstraint(b.uri(), b.source(), b.acronym(), b.name(), b.maxDepth(),
            b.iri(), b.sourceSystem(), Optional.of(v)))
        .orElse(b);
  }

  private static ClassValueConstraint freezeClass(ClassValueConstraint c, VersionResolver resolver) {
    if (c.version().isPresent()) {
      return c;
    }
    return resolver.currentVersionByClassUri(c.uri())
        .map(v -> new ClassValueConstraint(c.uri(), c.source(), c.label(), c.prefLabel(), c.type(),
            c.iri(), c.sourceSystem(), Optional.of(v)))
        .orElse(c);
  }

  private static ValueSetValueConstraint freezeValueSet(ValueSetValueConstraint vs, VersionResolver resolver) {
    if (vs.version().isPresent()) {
      return vs;
    }
    return resolver.currentVersionByValueSetCollection(vs.vsCollection())
        .map(v -> new ValueSetValueConstraint(vs.uri(), vs.vsCollection(), vs.name(), vs.numTerms(),
            vs.iri(), vs.sourceSystem(), Optional.of(v)))
        .orElse(vs);
  }

  private ControlledTermVersionFreezer() {}
}
