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

public sealed interface NihGrantIdFieldInstance extends FieldInstanceArtifact, IriFieldInstance
{
  static NihGrantIdFieldInstance create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                  Optional<String> label, Optional<String> notation, Optional<String> preferredLabel, Optional<String> language)
  {
    return new NihGrantIdFieldInstanceRecord(jsonLdTypes, jsonLdId, Optional.empty(), label, notation, preferredLabel, language);
  }

  static NihGrantIdFieldInstanceBuilder builder()
  {
    return new NihGrantIdFieldInstanceBuilder();
  }

  static NihGrantIdFieldInstanceBuilder builder(NihGrantIdFieldInstance NihGrantIdFieldInstance)
  {
    return new NihGrantIdFieldInstanceBuilder(NihGrantIdFieldInstance);
  }

  final class NihGrantIdFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public NihGrantIdFieldInstanceBuilder() {}

    public NihGrantIdFieldInstanceBuilder(NihGrantIdFieldInstance NihGrantIdFieldInstance) {
      super(NihGrantIdFieldInstance);
    }

    public NihGrantIdFieldInstanceBuilder withValue(URI value)
    {
      super.withJsonLdId(value);
      return this;
    }

    public NihGrantIdFieldInstanceBuilder withLabel(String label)
    {
      super.withLabel(label);
      return this;
    }

    public NihGrantIdFieldInstanceBuilder withPreferredLabel(String preferredLabel)
    {
      this.preferredLabel = Optional.ofNullable(preferredLabel);
      return this;
    }

    public NihGrantIdFieldInstanceBuilder withNotation(String notation)
    {
      super.withNotation(notation);
      return this;
    }

    public NihGrantIdFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdId, label, notation, preferredLabel, language);
    }
  }

}

record NihGrantIdFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                               Optional<String> language)
    implements NihGrantIdFieldInstance
{
  public NihGrantIdFieldInstanceRecord
  {
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateOptionalFieldNotNull(this, jsonLdValue, JSON_LD_VALUE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, label, RDFS_LABEL);
    validateOptionalFieldNotNull(this, language, JSON_LD_LANGUAGE);
    validateOptionalFieldNotNull(this, notation, SKOS_NOTATION);
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
  }
}