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

public sealed interface TextAreaFieldInstance extends FieldInstanceArtifact, LiteralFieldInstance
{
  static TextAreaFieldInstance create(List<URI> jsonLdTypes,Optional<String> jsonLdValue, Optional<String> language)
  {
    return new TextAreaFieldInstanceRecord(jsonLdTypes, Optional.empty(), jsonLdValue,
      Optional.empty(), Optional.empty(), Optional.empty(), language);
  }

  static TextAreaFieldInstanceBuilder builder()
  {
    return new TextAreaFieldInstanceBuilder();
  }

  static TextAreaFieldInstanceBuilder builder(TextAreaFieldInstance textAreaFieldInstance)
  {
    return new TextAreaFieldInstanceBuilder(textAreaFieldInstance);
  }

  public final class TextAreaFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public TextAreaFieldInstanceBuilder() {}

    public TextAreaFieldInstanceBuilder(TextAreaFieldInstance textAreaFieldInstance) {
      super(textAreaFieldInstance);
    }

    public TextAreaFieldInstanceBuilder withValue(String jsonLdValue)
    {
      super.withJsonLdValue(jsonLdValue);
      return this;
    }

    public TextAreaFieldInstanceBuilder withLanguage(String language)
    {
      super.withLanguage(language);
      return this;
    }

    public TextAreaFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdValue, language);
    }
  }

}

record TextAreaFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                               Optional<String> language)
  implements TextAreaFieldInstance
{
  public TextAreaFieldInstanceRecord
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