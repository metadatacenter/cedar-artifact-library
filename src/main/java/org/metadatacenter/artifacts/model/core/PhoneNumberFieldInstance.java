package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_LANGUAGE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;
import static org.metadatacenter.model.ModelNodeNames.RDFS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_NOTATION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;

public sealed interface PhoneNumberFieldInstance extends FieldInstanceArtifact
{
  static PhoneNumberFieldInstance create(List<URI> jsonLdTypes, Optional<String> jsonLdValue)
  {
    return new PhoneNumberFieldInstanceRecord(jsonLdTypes, Optional.empty(), jsonLdValue,
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }
}

record PhoneNumberFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
                               Optional<String> language)
  implements PhoneNumberFieldInstance
{
  public PhoneNumberFieldInstanceRecord
  {
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdValue, JSON_LD_VALUE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, label, RDFS_LABEL);
    validateOptionalFieldNotNull(this, language, JSON_LD_LANGUAGE);
    validateOptionalFieldNotNull(this, notation, SKOS_NOTATION);
    validateOptionalFieldNotNull(this, prefLabel, SKOS_PREFLABEL);
  }
}