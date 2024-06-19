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

public sealed interface RadioFieldInstance extends FieldInstanceArtifact, LiteralFieldInstance
{
  static RadioFieldInstance create(List<URI> jsonLdTypes, Optional<String> jsonLdValue, Optional<String> language)
  {
    return new RadioFieldInstanceRecord(jsonLdTypes, Optional.empty(), jsonLdValue,
      Optional.empty(), Optional.empty(), Optional.empty(), language);
  }

  static RadioFieldInstanceBuilder builder()
  {
    return new RadioFieldInstanceBuilder();
  }

  static RadioFieldInstanceBuilder builder(RadioFieldInstance radioFieldInstance)
  {
    return new RadioFieldInstanceBuilder(radioFieldInstance);
  }

  final class RadioFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public RadioFieldInstanceBuilder() {}

    public RadioFieldInstanceBuilder(RadioFieldInstance radioFieldInstance) {
      super(radioFieldInstance);
    }

    public RadioFieldInstanceBuilder withValue(String value)
    {
      super.withJsonLdValue(value);
      return this;
    }

    public RadioFieldInstanceBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    public RadioFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdValue, language);
    }
  }

}

record RadioFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                               Optional<String> language)
  implements RadioFieldInstance
{
  public RadioFieldInstanceRecord
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