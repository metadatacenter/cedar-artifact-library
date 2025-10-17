package org.metadatacenter.artifacts.model.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract sealed class FieldInstanceArtifactBuilder permits TextFieldInstance.TextFieldInstanceBuilder,
    TextAreaFieldInstance.TextAreaFieldInstanceBuilder, TemporalFieldInstance.TemporalFieldInstanceBuilder,
    NumericFieldInstance.NumericFieldInstanceBuilder, ControlledTermFieldInstance.ControlledTermFieldInstanceBuilder,
    LinkFieldInstance.LinkFieldInstanceBuilder, RorFieldInstance.RorFieldInstanceBuilder,
    OrcidFieldInstance.OrcidFieldInstanceBuilder, PfasFieldInstance.PfasFieldInstanceBuilder,
    RridFieldInstance.RridFieldInstanceBuilder, PubMedFieldInstance.PubMedFieldInstanceBuilder,
    NihGrantIdFieldInstance.NihGrantIdFieldInstanceBuilder,
    EmailFieldInstance.EmailFieldInstanceBuilder, CheckboxFieldInstance.CheckboxFieldInstanceBuilder,
    ListFieldInstance.ListFieldInstanceBuilder, PhoneNumberFieldInstance.PhoneNumberFieldInstanceBuilder,
    RadioFieldInstance.RadioFieldInstanceBuilder, DoiFieldInstance.DoiFieldInstanceBuilder
{
  protected List<URI> jsonLdTypes = new ArrayList<>();
  protected Optional<URI> jsonLdId = Optional.empty();
  protected Optional<String> jsonLdValue = Optional.empty();
  protected Optional<String> label = Optional.empty();
  protected Optional<String> notation = Optional.empty();
  protected Optional<String> preferredLabel = Optional.empty();
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
    this.preferredLabel = fieldInstanceArtifact.preferredLabel();
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
    this.jsonLdId = Optional.ofNullable(jsonLdId);
    return this;
  }

  protected FieldInstanceArtifactBuilder withJsonLdValue(String jsonLdValue)
  {
    this.jsonLdValue = Optional.ofNullable(jsonLdValue);
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

  protected FieldInstanceArtifactBuilder withPreferredLabel(String preferredLabel)
  {
    this.preferredLabel = Optional.ofNullable(preferredLabel);
    return this;
  }

  protected FieldInstanceArtifactBuilder withLanguage(String language)
  {
    this.language = Optional.ofNullable(language);
    return this;
  }
}