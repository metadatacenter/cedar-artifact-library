package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_IRI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE_SYSTEM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VS_COLLECTION;

/**
 * A value-set value space. The additive, source-explicit fields (VERSIONING-ROADMAP "The Model" §6) are optional:
 * {@code iri} (canonical identity), {@code sourceSystem} (backend; absent ⇒ BioPortal), and
 * {@code version} (absent ⇒ latest). A legacy constraint omits all three.
 */
public record ValueSetValueConstraint(URI uri, String vsCollection, String name, Optional<Integer> numTerms,
                                      Optional<URI> iri, Optional<String> sourceSystem,
                                      Optional<VersionSpec> version) {

  public ValueSetValueConstraint {
    validateStringFieldNotNull(this, name, VALUE_CONSTRAINTS_NAME);
    validateStringFieldNotNull(this, vsCollection, VALUE_CONSTRAINTS_VS_COLLECTION);
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateOptionalFieldNotNull(this, numTerms, VALUE_CONSTRAINTS_NUM_TERMS);
    validateOptionalFieldNotNull(this, iri, VALUE_CONSTRAINTS_IRI);
    validateOptionalFieldNotNull(this, sourceSystem, VALUE_CONSTRAINTS_SOURCE_SYSTEM);
    validateOptionalFieldNotNull(this, version, VALUE_CONSTRAINTS_VERSION);
  }

  /** Backward-compatible constructor: the source-explicit fields default to absent. */
  public ValueSetValueConstraint(URI uri, String vsCollection, String name, Optional<Integer> numTerms) {
    this(uri, vsCollection, name, numTerms, Optional.empty(), Optional.empty(), Optional.empty());
  }
}
