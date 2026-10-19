package org.metadatacenter.artifacts.model.core.fields.constraints;

import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateStringFieldNotBlank;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VERSION_DECLARED_VERSION;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VERSION_EFFECTIVE_DATE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VERSION_ID;

/**
 * The version triple that pins a value-constraint entry to a reproducible vocabulary state
 * (VERSIONING-ROADMAP "The Model" §4.1):
 * <ul>
 *   <li>{@code id} — the content-hash version id. <b>Identity</b>: resolution uses only this.</li>
 *   <li>{@code effectiveDate} — when the state entered circulation (display/ordering; may be
 *       absent).</li>
 *   <li>{@code declaredVersion} — the source's self-declared label (display only; may be absent or
 *       ambiguous).</li>
 * </ul>
 *
 * The absence of a {@code version} on an entry means <b>latest</b> — resolve the newest state at
 * serve time. A published template stamps the triple to freeze the entry against ontology drift.
 *
 * The {@code id} must contain a character other than whitespace. Readers refuse a pin with an empty
 * id, and a published template cannot be changed, so a template frozen with one could never be read
 * again.
 */
public record VersionSpec(String id, Optional<String> effectiveDate, Optional<String> declaredVersion) {

  public VersionSpec {
    validateStringFieldNotBlank(this, id, VALUE_CONSTRAINTS_VERSION_ID);
    validateOptionalFieldNotNull(this, effectiveDate, VALUE_CONSTRAINTS_VERSION_EFFECTIVE_DATE);
    validateOptionalFieldNotNull(this, declaredVersion, VALUE_CONSTRAINTS_VERSION_DECLARED_VERSION);
  }
}
