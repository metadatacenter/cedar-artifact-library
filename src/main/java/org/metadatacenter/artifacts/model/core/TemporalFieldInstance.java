package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;

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

public sealed interface TemporalFieldInstance extends FieldInstanceArtifact
{
  static TemporalFieldInstance create(List<URI> jsonLdTypes, Optional<String> jsonLdValue)
  {
    return new TemporalFieldInstanceRecord(jsonLdTypes, Optional.empty(), jsonLdValue,
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  static TemporalFieldInstanceBuilder builder()
  {
    return new TemporalFieldInstanceBuilder();
  }

  static TemporalFieldInstanceBuilder builder(TemporalFieldInstance temporalFieldInstance)
  {
    return new TemporalFieldInstanceBuilder(temporalFieldInstance);
  }

  final class TemporalFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public TemporalFieldInstanceBuilder() {}

    public TemporalFieldInstanceBuilder(TemporalFieldInstance temporalFieldInstance) {
      super(temporalFieldInstance);
    }

    public TemporalFieldInstanceBuilder withValue(String value)
    {
      super.withJsonLdValue(value);
      return this;
    }

    public TemporalFieldInstanceBuilder withType(XsdTemporalDatatype datatype)
    {
      super.withJsonLdType(datatype.toUri());
      return this;
    }

    public TemporalFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdValue);
    }
  }
}

record TemporalFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> prefLabel,
                               Optional<String> language)
  implements TemporalFieldInstance
{
  public TemporalFieldInstanceRecord
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