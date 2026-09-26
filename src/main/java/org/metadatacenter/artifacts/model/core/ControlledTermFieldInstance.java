package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateAtMostOneFieldInstanceType;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_LANGUAGE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;
import static org.metadatacenter.model.ModelNodeNames.RDFS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_NOTATION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;

public sealed interface ControlledTermFieldInstance extends FieldInstanceArtifact, IriFieldInstance
{
  static ControlledTermFieldInstance create(List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<String> label, Optional<String> notation, Optional<String> preferredLabel, Optional<String> language)
  {
    return new ControlledTermFieldInstanceRecord(jsonLdTypes, jsonLdId, Optional.empty(), label, notation, preferredLabel, language, jsonLdId.map(URI::toString));
  }

  /** Construct a controlled term without rewriting the lexical RDF identifier. */
  static ControlledTermFieldInstance createWithIri(List<URI> types, Optional<String> iri,
      Optional<String> label, Optional<String> notation, Optional<String> preferredLabel, Optional<String> language) {
    var field = FieldInstanceArtifact.createWithIri(types, iri, Optional.empty(), label, notation,
        preferredLabel, language, false);
    return new ControlledTermFieldInstanceRecord(types, field.jsonLdId(), Optional.empty(), label,
        notation, preferredLabel, language, iri);
  }

  static ControlledTermFieldInstanceBuilder builder()
  {
    return new ControlledTermFieldInstanceBuilder();
  }

  static ControlledTermFieldInstanceBuilder builder(ControlledTermFieldInstance controlledTermFieldInstance)
  {
    return new ControlledTermFieldInstanceBuilder(controlledTermFieldInstance);
  }

  final class ControlledTermFieldInstanceBuilder extends FieldInstanceArtifactBuilder
  {
    private Optional<String> iriValue = Optional.empty();

    public ControlledTermFieldInstanceBuilder() {}

    public ControlledTermFieldInstanceBuilder(ControlledTermFieldInstance controlledTermFieldInstance) {
      super(controlledTermFieldInstance);
      this.iriValue = controlledTermFieldInstance.jsonLdIdIri();
    }

    public ControlledTermFieldInstanceBuilder withValue(URI value)
    {
      super.withJsonLdId(value);
      this.iriValue = Optional.ofNullable(value).map(URI::toString);
      return this;
    }

    /** Supply the exact IRI, including Unicode that java.net.URI cannot store directly. */
    public ControlledTermFieldInstanceBuilder withIriValue(String value) {
      this.iriValue = Optional.ofNullable(value);
      return this;
    }

    public ControlledTermFieldInstanceBuilder withLabel(String label)
    {
      super.withLabel(label);
      return this;
    }

    public ControlledTermFieldInstanceBuilder withPreferredLabel(String preferredLabel)
    {
      this.preferredLabel = Optional.ofNullable(preferredLabel);
      return this;
    }

    public ControlledTermFieldInstanceBuilder withNotation(String notation)
    {
      super.withNotation(notation);
      return this;
    }

    public ControlledTermFieldInstance build()
    {
      return createWithIri(jsonLdTypes, iriValue, label, notation, preferredLabel, language);
    }
  }

}

record ControlledTermFieldInstanceRecord(List<URI> jsonLdTypes, Optional<URI> jsonLdId, Optional<String> jsonLdValue,
                               Optional<String> label, Optional<String> notation, Optional<String> preferredLabel,
                               Optional<String> language, Optional<String> jsonLdIdIri)
  implements ControlledTermFieldInstance
{
  public ControlledTermFieldInstanceRecord
  {
    validateListFieldNotNull(this, jsonLdTypes, JSON_LD_TYPE);
    validateAtMostOneFieldInstanceType(this, jsonLdTypes);
    validateOptionalFieldNotNull(this, jsonLdValue, JSON_LD_VALUE);
    validateOptionalFieldNotNull(this, jsonLdId, JSON_LD_ID);
    validateOptionalFieldNotNull(this, jsonLdIdIri, JSON_LD_ID);
    validateOptionalFieldNotNull(this, label, RDFS_LABEL);
    validateOptionalFieldNotNull(this, language, JSON_LD_LANGUAGE);
    validateOptionalFieldNotNull(this, notation, SKOS_NOTATION);
    validateOptionalFieldNotNull(this, preferredLabel, SKOS_PREFLABEL);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
