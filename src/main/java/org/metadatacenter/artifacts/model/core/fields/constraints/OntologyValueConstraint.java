package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.net.URI;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACRONYM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_IRI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE_SYSTEM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VERSION;

/**
 * An ontology value space. {@code uri}/{@code acronym}/{@code name}/{@code numTerms} are the legacy,
 * BioPortal-shaped fields. The source-explicit fields are additive and optional (VERSIONING-DESIGN
 * §6): {@code iri} is the canonical cross-source identity, {@code sourceSystem} names the backend
 * (absent ⇒ BioPortal), and {@code version} pins a state (absent ⇒ latest). A legacy constraint
 * omits all three and is read/rendered exactly as before.
 */
public record OntologyValueConstraint(URI uri, String acronym, String name, Optional<Integer> numTerms,
                                      Optional<URI> iri, Optional<String> sourceSystem,
                                      Optional<VersionSpec> version) {

  public OntologyValueConstraint {
    validateUriFieldNotNull(this, uri, VALUE_CONSTRAINTS_URI);
    validateStringFieldNotNull(this, acronym, VALUE_CONSTRAINTS_ACRONYM);
    validateStringFieldNotNull(this, name, VALUE_CONSTRAINTS_NAME);
    validateOptionalFieldNotNull(this, numTerms, VALUE_CONSTRAINTS_NUM_TERMS);
    validateOptionalFieldNotNull(this, iri, VALUE_CONSTRAINTS_IRI);
    validateOptionalFieldNotNull(this, sourceSystem, VALUE_CONSTRAINTS_SOURCE_SYSTEM);
    validateOptionalFieldNotNull(this, version, VALUE_CONSTRAINTS_VERSION);
  }

  /** Backward-compatible constructor: the source-explicit fields default to absent (BioPortal source,
   *  latest version, acronym-derived iri). */
  public OntologyValueConstraint(URI uri, String acronym, String name, Optional<Integer> numTerms) {
    this(uri, acronym, name, numTerms, Optional.empty(), Optional.empty(), Optional.empty());
  }
}
