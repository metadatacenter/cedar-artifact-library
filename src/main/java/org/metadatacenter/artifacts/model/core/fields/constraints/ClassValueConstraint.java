package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_IRI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE_SYSTEM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VERSION;

/**
 * A single-class value space. {@code source} is the legacy free-text display string, <b>not</b> a
 * backend. The additive, source-explicit fields (VERSIONING-DESIGN §6) are optional: {@code iri}
 * (canonical identity; derivable from the target uri), {@code sourceSystem} (backend; absent ⇒
 * BioPortal), and {@code version} (absent ⇒ latest). A legacy constraint omits all three.
 */
public record ClassValueConstraint(URI uri, String source, String label, String prefLabel, ValueType type,
                                   Optional<URI> iri, Optional<String> sourceSystem,
                                   Optional<VersionSpec> version) {

  public ClassValueConstraint {
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateStringFieldNotNull(this, prefLabel, VALUE_CONSTRAINTS_PREFLABEL);
    validateStringFieldNotNull(this, label, VALUE_CONSTRAINTS_LABEL);
    validateStringFieldNotNull(this, source, VALUE_CONSTRAINTS_SOURCE);
    validateOptionalFieldNotNull(this, iri, VALUE_CONSTRAINTS_IRI);
    validateOptionalFieldNotNull(this, sourceSystem, VALUE_CONSTRAINTS_SOURCE_SYSTEM);
    validateOptionalFieldNotNull(this, version, VALUE_CONSTRAINTS_VERSION);
  }

  /** Backward-compatible constructor: the source-explicit fields default to absent. */
  public ClassValueConstraint(URI uri, String source, String label, String prefLabel, ValueType type) {
    this(uri, source, label, prefLabel, type, Optional.empty(), Optional.empty(), Optional.empty());
  }
}
