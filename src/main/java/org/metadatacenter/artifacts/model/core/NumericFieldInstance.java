package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;

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

public sealed interface NumericFieldInstance extends FieldInstanceArtifact, LiteralFieldInstance
{
  static NumericFieldInstance create(List<URI> jsonLdTypes, Optional<String> jsonLdValue)
  {
    return new NumericFieldInstanceRecord(jsonLdTypes, Optional.empty(), jsonLdValue,
      Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  static NumericFieldInstanceBuilder builder()
  {
    return new NumericFieldInstanceBuilder();
  }

  static NumericFieldInstanceBuilder builder(NumericFieldInstance numericFieldInstance)
  {
    return new NumericFieldInstanceBuilder(numericFieldInstance);
  }

   final class NumericFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    public NumericFieldInstanceBuilder() {
    }

    public NumericFieldInstanceBuilder(NumericFieldInstance numericFieldInstance) {
      super(numericFieldInstance);
    }

    public NumericFieldInstanceBuilder withValue(Number value)
    {
      super.withJsonLdValue(value.toString());
      return this;
    }

    public NumericFieldInstanceBuilder withType(XsdNumericDatatype datatype)
    {
      super.withJsonLdType(datatype.toUri());
      return this;
    }

    public NumericFieldInstance build()
    {
      return create(jsonLdTypes, jsonLdValue);
    }
  }
}

record NumericFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                               Optional<String> language)
  implements NumericFieldInstance
{
  public NumericFieldInstanceRecord
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