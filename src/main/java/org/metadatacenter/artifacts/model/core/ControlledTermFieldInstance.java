package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.builders.ControlledTermFieldInstanceBuilder;

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

public sealed interface ControlledTermFieldInstance extends FieldInstanceArtifact
{
  static ControlledTermFieldInstance create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<String> label, Optional<String> notation, Optional<String> prefLabel, Optional<String> language)
  {
    return new ControlledTermFieldInstanceRecord(jsonLdTypes, jsonLdId, Optional.empty(), label, notation, prefLabel, language);
  }

  static ControlledTermFieldInstanceBuilder builder()
  {
    return new ControlledTermFieldInstanceBuilder();
  }

  static ControlledTermFieldInstanceBuilder builder(ControlledTermFieldInstance controlledTermFieldInstance)
  {
    return new ControlledTermFieldInstanceBuilder(controlledTermFieldInstance);
  }
}

record ControlledTermFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
                               Optional<String> language)
  implements ControlledTermFieldInstance
{
  public ControlledTermFieldInstanceRecord
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