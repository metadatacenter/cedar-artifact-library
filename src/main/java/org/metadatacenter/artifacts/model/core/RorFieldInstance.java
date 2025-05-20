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

public sealed interface RorFieldInstance extends FieldInstanceArtifact, IriFieldInstance
{
  static RorFieldInstance create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                 Optional<String> label, Optional<String> notation, Optional<String> preferredLabel, Optional<String> language)
  {
    return new RorFieldInstanceRecord(jsonLdTypes, jsonLdId, Optional.empty(), label, notation, preferredLabel, language);
  }

  static RorFieldInstanceBuilder builder()
  {
    return new RorFieldInstanceBuilder();
  }

  static RorFieldInstanceBuilder builder(RorFieldInstance RoRFieldInstance)
  {
    return new RorFieldInstanceBuilder(RoRFieldInstance);
  }

  final class RorFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public RorFieldInstanceBuilder() {}

    public RorFieldInstanceBuilder(RorFieldInstance RoRFieldInstance) {
      super(RoRFieldInstance);
    }

    public RorFieldInstanceBuilder withValue(URI value)
    {
      super.withJsonLdId(value);
      return this;
    }

    public RorFieldInstanceBuilder withLabel(String label)
    {
      super.withLabel(label);
      return this;
    }

    public RorFieldInstanceBuilder withPreferredLabel(String preferredLabel)
    {
      this.preferredLabel = Optional.ofNullable(preferredLabel);
      return this;
    }

    public RorFieldInstanceBuilder withNotation(String notation)
    {
      super.withNotation(notation);
      return this;
    }

    public RorFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdId, label, notation, preferredLabel, language);
    }
  }

}

record RorFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                              Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                              Optional<String> language)
    implements RorFieldInstance
{
  public RorFieldInstanceRecord
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