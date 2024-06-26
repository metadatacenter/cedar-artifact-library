package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;

public abstract sealed class FieldSchemaArtifactBuilder permits TextField.TextFieldBuilder,
  TextAreaField.TextAreaFieldBuilder, TemporalField.TemporalFieldBuilder,
  RadioField.RadioFieldBuilder, PhoneNumberField.PhoneNumberFieldBuilder,
  NumericField.NumericFieldBuilder, ListField.ListFieldBuilder,
  LinkField.LinkFieldBuilder, EmailField.EmailFieldBuilder, ControlledTermField.ControlledTermFieldBuilder,
  CheckboxField.CheckboxFieldBuilder, AttributeValueField.AttributeValueFieldBuilder,
  PageBreakField.PageBreakFieldBuilder, SectionBreakField.SectionBreakFieldBuilder, ImageField.ImageFieldBuilder,
  YouTubeField.YouTubeFieldBuilder, RichTextField.RichTextFieldBuilder
{
  protected LinkedHashMap<String, URI> jsonLdContext;
  protected List<URI> jsonLdTypes = new ArrayList<>();
  protected Optional<URI> jsonLdId = Optional.empty();
  protected Optional<URI> createdBy = Optional.empty();
  protected Optional<URI> modifiedBy = Optional.empty();
  protected Optional<OffsetDateTime> createdOn = Optional.empty();
  protected Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
  protected final URI jsonSchemaSchemaUri = URI.create(JSON_SCHEMA_SCHEMA_IRI);
  protected String jsonSchemaType;
  protected String jsonSchemaTitle = "";
  protected String jsonSchemaDescription = "";
  protected String name;
  protected String description = "";
  protected Optional<String> identifier = Optional.empty();
  protected Optional<String> preferredLabel = Optional.empty();
  protected List<String> alternateLabels = Collections.emptyList();
  protected Version modelVersion = new Version(1, 6, 0); // TODO Put 1.6.0 in ModelNodeNames
  protected Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO Put 0.0.1. in ModelNodeNames
  protected Optional<Status> status = Optional.of(Status.DRAFT);
  protected Optional<URI> previousVersion = Optional.empty();
  protected Optional<URI> derivedFrom = Optional.empty();
  protected boolean isMultiple = false;
  protected Optional<Integer> minItems = Optional.empty();
  protected Optional<Integer> maxItems = Optional.empty();
  protected Optional<URI> propertyUri = Optional.empty();
  protected Optional<String> language = Optional.empty();
  protected FieldUi fieldUi;
  protected Optional<ValueConstraints> valueConstraints = Optional.empty();
  protected Optional<Annotations> annotations = Optional.empty();

  protected FieldSchemaArtifactBuilder(String jsonSchemaType, URI artifactTypeIri)
  {
    this.jsonSchemaType = jsonSchemaType;
    this.jsonLdTypes.add(artifactTypeIri);
  }

  protected FieldSchemaArtifactBuilder(FieldSchemaArtifact fieldSchemaArtifact)
  {
    this.jsonLdContext = new LinkedHashMap<>(fieldSchemaArtifact.jsonLdContext());
    this.jsonLdTypes = new ArrayList<>(fieldSchemaArtifact.jsonLdTypes());
    this.jsonLdId = fieldSchemaArtifact.jsonLdId();
    this.createdBy = fieldSchemaArtifact.createdBy();
    this.modifiedBy = fieldSchemaArtifact.modifiedBy();
    this.createdOn = fieldSchemaArtifact.createdOn();
    this.lastUpdatedOn = fieldSchemaArtifact.lastUpdatedOn();
    this.jsonSchemaType = fieldSchemaArtifact.jsonSchemaType();
    this.jsonSchemaTitle = fieldSchemaArtifact.jsonSchemaTitle();
    this.jsonSchemaDescription = fieldSchemaArtifact.jsonSchemaDescription();
    this.name = fieldSchemaArtifact.name();
    this.description = fieldSchemaArtifact.description();
    this.identifier = fieldSchemaArtifact.identifier();
    this.alternateLabels = fieldSchemaArtifact.alternateLabels();
    this.modelVersion = fieldSchemaArtifact.modelVersion();
    this.version = fieldSchemaArtifact.version();
    this.status = fieldSchemaArtifact.status();
    this.previousVersion = fieldSchemaArtifact.previousVersion();
    this.derivedFrom = fieldSchemaArtifact.derivedFrom();
    this.isMultiple = fieldSchemaArtifact.isMultiple();
    this.minItems = fieldSchemaArtifact.minItems();
    this.maxItems = fieldSchemaArtifact.maxItems();
    this.propertyUri = fieldSchemaArtifact.propertyUri();
    this.language = fieldSchemaArtifact.language();
    this.fieldUi = fieldSchemaArtifact.fieldUi();
    this.valueConstraints = fieldSchemaArtifact.valueConstraints();
    this.annotations = fieldSchemaArtifact.annotations();
  }

  protected FieldSchemaArtifactBuilder withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext)
  {
    if (jsonLdContext == null)
      throw new IllegalArgumentException("null JSON-LD @context passed to builder");

    this.jsonLdContext = new LinkedHashMap<>(jsonLdContext);
    return this;
  }

  protected FieldSchemaArtifactBuilder withJsonLdType(URI jsonLdType)
  {
    if (jsonLdType == null)
      throw new IllegalArgumentException("null JSON-LD @type passed to builder");

    this.jsonLdTypes.add(jsonLdType);
    return this;
  }

  protected FieldSchemaArtifactBuilder withJsonLdId(URI jsonLdId)
  {
    this.jsonLdId = Optional.ofNullable(jsonLdId);
    return this;
  }

  protected FieldSchemaArtifactBuilder withName(String name)
  {
    if (name == null)
      throw new IllegalArgumentException("null name passed to builder");

    this.name = name;

    if (this.jsonSchemaTitle.isEmpty())
      this.jsonSchemaTitle = name + " field schema";

    if (this.jsonSchemaDescription.isEmpty())
      this.jsonSchemaDescription = name + " field schema generated by the CEDAR Artifact Library";

    return this;
  }

  protected FieldSchemaArtifactBuilder withDescription(String description)
  {
    if (description == null)
      throw new IllegalArgumentException("null description passed to builder");

    this.description = description;
    return this;
  }

  protected FieldSchemaArtifactBuilder withIdentifier(String identifier)
  {
    if (identifier == null)
      throw new IllegalArgumentException("null identifier passed to builder");

    this.identifier = Optional.ofNullable(identifier);
    return this;
  }

  protected FieldSchemaArtifactBuilder withPreferredLabel(String preferredLabel)
  {
    if (preferredLabel == null)
      throw new IllegalArgumentException("null preferred label passed to builder");

    this.preferredLabel = Optional.ofNullable(preferredLabel);
    return this;
  }

  protected FieldSchemaArtifactBuilder withAlternateLabels(List<String> alternateLabels)
  {
    if (alternateLabels == null)
      throw new IllegalArgumentException("null alternate labels passed to builder");

    this.alternateLabels = alternateLabels;
    return this;
  }

  protected FieldSchemaArtifactBuilder withModelVersion(Version modelVersion)
  {
    if (modelVersion == null)
      throw new IllegalArgumentException("null model version passed to builder");

    this.modelVersion = modelVersion;
    return this;
  }

  protected FieldSchemaArtifactBuilder withVersion(Version version)
  {
    if (version == null)
      throw new IllegalArgumentException("null artifact version passed to builder");

    this.version = Optional.ofNullable(version);
    return this;
  }

  protected FieldSchemaArtifactBuilder withStatus(Status status)
  {
    this.status = Optional.ofNullable(status);
    return this;
  }

  protected FieldSchemaArtifactBuilder withCreatedBy(URI createdBy)
  {
    this.createdBy = Optional.ofNullable(createdBy);
    return this;
  }

  protected FieldSchemaArtifactBuilder withModifiedBy(URI modifiedBy)
  {
    this.modifiedBy = Optional.ofNullable(modifiedBy);
    return this;
  }

  protected FieldSchemaArtifactBuilder withCreatedOn(OffsetDateTime createdOn)
  {
    this.createdOn = Optional.ofNullable(createdOn);
    return this;
  }

  protected FieldSchemaArtifactBuilder withLastUpdatedOn(OffsetDateTime lastUpdatedOn)
  {
    this.lastUpdatedOn = Optional.ofNullable(lastUpdatedOn);
    return this;
  }

  protected FieldSchemaArtifactBuilder withPreviousVersion(URI previousVersion)
  {
    this.previousVersion = Optional.ofNullable(previousVersion);
    return this;
  }

  protected FieldSchemaArtifactBuilder withDerivedFrom(URI derivedFrom)
  {
    this.derivedFrom = Optional.ofNullable(derivedFrom);
    return this;
  }

  protected FieldSchemaArtifactBuilder withIsMultiple(boolean isMultiple)
  {
    this.isMultiple = isMultiple;
    return this;
  }

  protected FieldSchemaArtifactBuilder withMinItems(Integer minItems)
  {
    this.minItems = Optional.ofNullable(minItems);
    return this;
  }

  protected FieldSchemaArtifactBuilder withMaxItems(Integer maxItems)
  {
    this.maxItems = Optional.ofNullable(maxItems);
    return this;
  }

  protected FieldSchemaArtifactBuilder withPropertyUri(URI propertyUri)
  {
    this.propertyUri = Optional.ofNullable(propertyUri);
    return this;
  }

  protected FieldSchemaArtifactBuilder withLanguage(String language)
  {
    this.language = Optional.ofNullable(language);
    return this;
  }

  protected FieldSchemaArtifactBuilder withJsonSchemaTitle(String jsonSchemaTitle)
  {
    if (jsonSchemaTitle == null)
      throw new IllegalArgumentException("null JSON Schema title passed to builder");

    this.jsonSchemaTitle = jsonSchemaTitle;
    return this;
  }

  protected FieldSchemaArtifactBuilder withJsonSchemaDescription(String jsonSchemaDescription)
  {
    if (jsonSchemaDescription == null)
      throw new IllegalArgumentException("null JSON Schema description passed to builder");

    this.jsonSchemaDescription = jsonSchemaDescription;
    return this;
  }

  protected FieldSchemaArtifactBuilder withFieldUi(FieldUi fieldUi)
  {
    if (fieldUi == null)
      throw new IllegalArgumentException("null field UI passed to builder");

    this.fieldUi = fieldUi;
    return this;
  }

  protected FieldSchemaArtifactBuilder withValueConstraints(ValueConstraints valueConstraints)
  {
    if (valueConstraints == null)
      throw new IllegalArgumentException("null value constraints passed to builder");

    this.valueConstraints = Optional.ofNullable(valueConstraints);
    return this;
  }

  protected FieldSchemaArtifactBuilder withAnnotations(Annotations annotations)
  {
    if (annotations == null)
      throw new IllegalArgumentException("null annotations passed to builder");

    this.annotations = Optional.ofNullable(annotations);
    return this;
  }

}