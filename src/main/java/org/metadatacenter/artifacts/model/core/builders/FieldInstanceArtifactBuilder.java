package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract sealed class FieldInstanceArtifactBuilder permits
  TextFieldInstanceBuilder, TextAreaFieldInstanceBuilder, TemporalFieldInstanceBuilder, NumericFieldInstanceBuilder,
  ControlledTermFieldInstanceBuilder, LinkFieldInstanceBuilder,
  EmailFieldInstanceBuilder, CheckboxFieldInstanceBuilder, ListFieldInstanceBuilder, PhoneNumberFieldInstanceBuilder,
  RadioFieldInstanceBuilder
{
  protected List<URI> jsonLdTypes = new ArrayList<>();
  protected Optional<URI> jsonLdId = Optional.empty();
  protected Optional<String> jsonLdValue = Optional.empty();
  protected Optional<String> label = Optional.empty();
  protected Optional<String> notation = Optional.empty();
  protected Optional<String> prefLabel = Optional.empty();
  protected Optional<String> language = Optional.empty();

  protected FieldInstanceArtifactBuilder()
  {
  }

  protected FieldInstanceArtifactBuilder(FieldInstanceArtifact fieldInstanceArtifact)
  {
    this.jsonLdTypes = new ArrayList<>(fieldInstanceArtifact.jsonLdTypes());
    this.jsonLdId = fieldInstanceArtifact.jsonLdId();
    this.jsonLdValue = fieldInstanceArtifact.jsonLdValue();
    this.label = fieldInstanceArtifact.label();
    this.notation = fieldInstanceArtifact.notation();
    this.prefLabel = fieldInstanceArtifact.prefLabel();
    this.language = fieldInstanceArtifact.language();
  }

  protected FieldInstanceArtifactBuilder withJsonLdType(URI jsonLdType)
  {
    if (jsonLdType != null)
      this.jsonLdTypes.add(jsonLdType);
    return this;
  }

  protected FieldInstanceArtifactBuilder withJsonLdId(URI jsonLdId)
  {
    // Null a valid API value; serializer should ensure that no @id: null is generated in JSON-LD
    this.jsonLdId = Optional.of(jsonLdId);
    return this;
  }

  protected FieldInstanceArtifactBuilder withJsonLdValue(String jsonLdValue)
  {
    this.jsonLdValue = Optional.of(jsonLdValue); // Null a valid value
    return this;
  }

  protected FieldInstanceArtifactBuilder withLabel(String label)
  {
    this.label = Optional.ofNullable(label);
    return this;
  }

  protected FieldInstanceArtifactBuilder withNotation(String notation)
  {
    this.notation = Optional.ofNullable(notation);
    return this;
  }

  protected FieldInstanceArtifactBuilder withPrefLabel(String prefLabel)
  {
    this.prefLabel = Optional.ofNullable(prefLabel);
    return this;
  }

  protected FieldInstanceArtifactBuilder withLanguage(String language)
  {
    this.language = Optional.ofNullable(language);
    return this;
  }

  protected FieldInstanceArtifact build()
  {
    return FieldInstanceArtifact.create(jsonLdTypes, jsonLdId, jsonLdValue, label, notation, prefLabel, language);
  }
}