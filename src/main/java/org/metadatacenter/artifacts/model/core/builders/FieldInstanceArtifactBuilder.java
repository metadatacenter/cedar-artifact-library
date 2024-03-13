package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract sealed class FieldInstanceArtifactBuilder permits
  TextAreaFieldInstanceBuilder, TextFieldInstanceBuilder, TemporalFieldInstanceBuilder, NumericFieldInstanceBuilder,
  ControlledTermFieldInstanceBuilder, LinkFieldInstanceBuilder,
  EmailFieldInstanceBuilder, CheckboxFieldInstanceBuilder, ListFieldInstanceBuilder, PhoneNumberFieldInstanceBuilder,
  RadioFieldInstanceBuilder
{
  protected final Map<String, URI> jsonLdContext = Collections.emptyMap();
  protected final List<URI> jsonLdTypes = new ArrayList<>();
  protected Optional<URI> jsonLdId = Optional.empty();
  protected Optional<String> jsonLdValue = Optional.empty();
  protected Optional<String> label = Optional.empty();
  protected Optional<String> notation = Optional.empty();
  protected Optional<String> prefLabel = Optional.empty();
  protected Optional<String> language = Optional.empty();
  protected Optional<URI> createdBy = Optional.empty();
  protected Optional<URI> modifiedBy = Optional.empty();
  protected Optional<OffsetDateTime> createdOn = Optional.empty();
  protected Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();

  protected FieldInstanceArtifactBuilder()
  {
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

  protected FieldInstanceArtifactBuilder withCreatedBy(URI createdBy)
  {
    this.createdBy = Optional.ofNullable(createdBy);
    return this;
  }

  protected FieldInstanceArtifactBuilder withModifiedBy(URI modifiedBy)
  {
    if (modifiedBy != null)
      this.modifiedBy = Optional.ofNullable(modifiedBy);
    return this;
  }

  protected FieldInstanceArtifactBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    this.createdOn = Optional.ofNullable(createdOn);
    return this;
  }

  protected FieldInstanceArtifactBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    this.lastUpdatedOn = Optional.ofNullable(lastUpdatedOn);
    return this;
  }

  protected FieldInstanceArtifact build()
  {
    return FieldInstanceArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, jsonLdValue, label, notation, prefLabel,
      language, createdBy, modifiedBy, createdOn, lastUpdatedOn);
  }
}