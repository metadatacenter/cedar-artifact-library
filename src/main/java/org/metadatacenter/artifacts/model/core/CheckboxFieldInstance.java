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

public sealed interface CheckboxFieldInstance extends FieldInstanceArtifact
{
  static CheckboxFieldInstance create(List<URI> jsonLdTypes, Optional<String> jsonLdValue, Optional<String> language)
  {
    return new CheckboxFieldInstanceRecord(jsonLdTypes, Optional.empty(), jsonLdValue,
      Optional.empty(), Optional.empty(), Optional.empty(), language);
  }

  static CheckboxFieldInstanceBuilder builder()
  {
    return new CheckboxFieldInstanceBuilder();
  }

  static CheckboxFieldInstanceBuilder builder(CheckboxFieldInstance checkboxFieldInstance)
  {
    return new CheckboxFieldInstanceBuilder(checkboxFieldInstance);
  }

  final class CheckboxFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public CheckboxFieldInstanceBuilder() {}

    public CheckboxFieldInstanceBuilder(CheckboxFieldInstance checkboxFieldInstance) {
      super(checkboxFieldInstance);
    }

    public CheckboxFieldInstanceBuilder withValue(String value)
    {
      super.withJsonLdValue(value);
      return this;
    }

    public CheckboxFieldInstanceBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    public CheckboxFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdValue, language);
    }
  }
}

record CheckboxFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
                               Optional<String> language)
  implements CheckboxFieldInstance
{
  public CheckboxFieldInstanceRecord
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