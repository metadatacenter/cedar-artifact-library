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

public sealed interface OrcidFieldInstance extends FieldInstanceArtifact, IriFieldInstance
{
  static OrcidFieldInstance create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                 Optional<String> label, Optional<String> notation, Optional<String> preferredLabel, Optional<String> language)
  {
    return new OrcidFieldInstanceRecord(jsonLdTypes, jsonLdId, Optional.empty(), label, notation, preferredLabel, language);
  }

  static OrcidFieldInstanceBuilder builder()
  {
    return new OrcidFieldInstanceBuilder();
  }

  static OrcidFieldInstanceBuilder builder(OrcidFieldInstance OrcidFieldInstance)
  {
    return new OrcidFieldInstanceBuilder(OrcidFieldInstance);
  }

  final class OrcidFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public OrcidFieldInstanceBuilder() {}

    public OrcidFieldInstanceBuilder(OrcidFieldInstance OrcidFieldInstance) {
      super(OrcidFieldInstance);
    }

    public OrcidFieldInstanceBuilder withValue(URI value)
    {
      super.withJsonLdId(value);
      return this;
    }

    public OrcidFieldInstanceBuilder withLabel(String label)
    {
      super.withLabel(label);
      return this;
    }

    public OrcidFieldInstanceBuilder withPreferredLabel(String preferredLabel)
    {
      this.preferredLabel = Optional.ofNullable(preferredLabel);
      return this;
    }

    public OrcidFieldInstanceBuilder withNotation(String notation)
    {
      super.withNotation(notation);
      return this;
    }

    public OrcidFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdId, label, notation, preferredLabel, language);
    }
  }

}

record OrcidFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                              Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                              Optional<String> language)
    implements OrcidFieldInstance
{
  public OrcidFieldInstanceRecord
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