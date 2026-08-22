package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateIntegerFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACRONYM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_IRI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE_SYSTEM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VERSION;

/**
 * A branch value space (a subtree of an ontology). {@code source} is the legacy free-text display
 * string (e.g. {@code "Human Disease Ontology (DOID)"}), <b>not</b> a backend. The additive,
 * source-explicit fields (VERSIONING-ROADMAP "The Model" §6) are optional: {@code iri} (canonical identity;
 * derivable from the target uri), {@code sourceSystem} (backend; absent ⇒ BioPortal), and
 * {@code version} (absent ⇒ latest). A legacy constraint omits all three.
 */
public record BranchValueConstraint(URI uri, String source, String acronym, String name, Integer maxDepth,
                                    Optional<URI> iri, Optional<String> sourceSystem,
                                    Optional<VersionSpec> version) {

  public BranchValueConstraint
  {
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateStringFieldNotNull(this, source, VALUE_CONSTRAINTS_SOURCE);
    validateStringFieldNotNull(this, acronym, VALUE_CONSTRAINTS_ACRONYM);
    validateStringFieldNotNull(this, name, VALUE_CONSTRAINTS_NAME);
    validateIntegerFieldNotNull(this, maxDepth, VALUE_CONSTRAINTS_NAME);
    validateOptionalFieldNotNull(this, iri, VALUE_CONSTRAINTS_IRI);
    validateOptionalFieldNotNull(this, sourceSystem, VALUE_CONSTRAINTS_SOURCE_SYSTEM);
    validateOptionalFieldNotNull(this, version, VALUE_CONSTRAINTS_VERSION);
  }

  /** Backward-compatible constructor: the source-explicit fields default to absent. */
  public BranchValueConstraint(URI uri, String source, String acronym, String name, Integer maxDepth) {
    this(uri, source, acronym, name, maxDepth, Optional.empty(), Optional.empty(), Optional.empty());
  }
}
