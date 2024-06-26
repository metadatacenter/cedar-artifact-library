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

public sealed interface LinkFieldInstance extends FieldInstanceArtifact, IriFieldInstance
{
  static LinkFieldInstance create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<String> label, Optional<String> notation, Optional<String> preferredLabel, Optional<String> language)
  {
    return new LinkFieldInstanceRecord(jsonLdTypes, jsonLdId, Optional.empty(), label, notation, preferredLabel, language);
  }

  static LinkFieldInstanceBuilder builder()
  {
    return new LinkFieldInstanceBuilder();
  }

  static LinkFieldInstanceBuilder builder(LinkFieldInstance linkFieldInstance)
  {
    return new LinkFieldInstanceBuilder(linkFieldInstance);
  }

  final class LinkFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public LinkFieldInstanceBuilder() {}

    public LinkFieldInstanceBuilder(LinkFieldInstance linkFieldInstance) {
      super(linkFieldInstance);
    }

    public LinkFieldInstanceBuilder withValue(URI value)
    {
      super.withJsonLdId(value);
      return this;
    }

    public LinkFieldInstanceBuilder withLabel(String label)
    {
      super.withLabel(label);
      return this;
    }

    public LinkFieldInstanceBuilder withPreferredLabel(String preferredLabel)
    {
      this.preferredLabel = Optional.ofNullable(preferredLabel);
      return this;
    }

    public LinkFieldInstanceBuilder withNotation(String notation)
    {
      super.withNotation(notation);
      return this;
    }

    public LinkFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdId, label, notation, preferredLabel, language);
    }
  }

}

record LinkFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                               Optional<String> language)
  implements LinkFieldInstance
{
  public LinkFieldInstanceRecord
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