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

public sealed interface PfasFieldInstance extends FieldInstanceArtifact, IriFieldInstance
{
  static PfasFieldInstance create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                   Optional<String> label, Optional<String> notation, Optional<String> preferredLabel, Optional<String> language)
  {
    return new PfasFieldInstanceRecord(jsonLdTypes, jsonLdId, Optional.empty(), label, notation, preferredLabel, language);
  }

  static PfasFieldInstanceBuilder builder()
  {
    return new PfasFieldInstanceBuilder();
  }

  static PfasFieldInstanceBuilder builder(PfasFieldInstance PfasFieldInstance)
  {
    return new PfasFieldInstanceBuilder(PfasFieldInstance);
  }

  final class PfasFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public PfasFieldInstanceBuilder() {}

    public PfasFieldInstanceBuilder(PfasFieldInstance PfasFieldInstance) {
      super(PfasFieldInstance);
    }

    public PfasFieldInstanceBuilder withValue(URI value)
    {
      super.withJsonLdId(value);
      return this;
    }

    public PfasFieldInstanceBuilder withLabel(String label)
    {
      super.withLabel(label);
      return this;
    }

    public PfasFieldInstanceBuilder withPreferredLabel(String preferredLabel)
    {
      this.preferredLabel = Optional.ofNullable(preferredLabel);
      return this;
    }

    public PfasFieldInstanceBuilder withNotation(String notation)
    {
      super.withNotation(notation);
      return this;
    }

    public PfasFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdId, label, notation, preferredLabel, language);
    }
  }

}

record PfasFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                                Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                                Optional<String> language)
    implements PfasFieldInstance
{
  public PfasFieldInstanceRecord
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