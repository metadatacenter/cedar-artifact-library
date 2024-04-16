package org.metadatacenter.artifacts.model.core.builders;

import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA_IRI;

public abstract sealed class FieldSchemaArtifactBuilder permits TextFieldBuilder, TextAreaFieldBuilder, TemporalFieldBuilder,
  RadioFieldBuilder, PhoneNumberFieldBuilder, NumericFieldBuilder, ListFieldBuilder, LinkFieldBuilder,
  EmailFieldBuilder, ControlledTermFieldBuilder, CheckboxFieldBuilder,
  AttributeValueFieldBuilder,
  PageBreakFieldBuilder, SectionBreakFieldBuilder, ImageFieldBuilder, YouTubeFieldBuilder, RichTextFieldBuilder
{
  private Map<String, URI> jsonLdContext;
  private List<URI> jsonLdTypes = new ArrayList<>();
  private Optional<URI> jsonLdId = Optional.empty();
  private Optional<URI> createdBy = Optional.empty();
  private Optional<URI> modifiedBy = Optional.empty();
  private Optional<OffsetDateTime> createdOn = Optional.empty();
  private Optional<OffsetDateTime> lastUpdatedOn = Optional.empty();
  private final URI jsonSchemaSchemaUri = URI.create(JSON_SCHEMA_SCHEMA_IRI);
  private String jsonSchemaType;
  private String jsonSchemaTitle = "";
  private String jsonSchemaDescription = "";
  private String name;
  private String description = "";
  private Optional<String> identifier = Optional.empty();
  private Optional<String> skosPrefLabel = Optional.empty();
  private List<String> skosAlternateLabels = Collections.emptyList();
  private Version modelVersion = new Version(1, 6, 0); // TODO Put 1.6.0 in ModelNodeNames
  private Optional<Version> version = Optional.of(new Version(0, 0, 1)); // TODO Put 0.0.1. in ModelNodeNames
  private Optional<Status> status = Optional.of(Status.DRAFT);
  private Optional<URI> previousVersion = Optional.empty();
  private Optional<URI> derivedFrom = Optional.empty();
  private boolean isMultiple = false;
  private Optional<Integer> minItems = Optional.empty();
  private Optional<Integer> maxItems = Optional.empty();
  private Optional<URI> propertyUri = Optional.empty();
  private FieldUi fieldUi;
  private Optional<ValueConstraints> valueConstraints = Optional.empty();

  protected FieldSchemaArtifactBuilder(String jsonSchemaType, URI artifactTypeIri)
  {
    this.jsonSchemaType = jsonSchemaType;
    this.jsonLdTypes.add(artifactTypeIri);
  }

  protected FieldSchemaArtifactBuilder(FieldSchemaArtifact fieldSchemaArtifact)
  {
    this.jsonLdContext = new HashMap<>(fieldSchemaArtifact.jsonLdContext());
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
    this.skosAlternateLabels = fieldSchemaArtifact.skosAlternateLabels();
    this.modelVersion = fieldSchemaArtifact.modelVersion();
    this.version = fieldSchemaArtifact.version();
    this.status = fieldSchemaArtifact.status();
    this.previousVersion = fieldSchemaArtifact.previousVersion();
    this.derivedFrom = fieldSchemaArtifact.derivedFrom();
    this.isMultiple = fieldSchemaArtifact.isMultiple();
    this.minItems = fieldSchemaArtifact.minItems();
    this.maxItems = fieldSchemaArtifact.maxItems();
    this.propertyUri = fieldSchemaArtifact.propertyUri();
    this.fieldUi = fieldSchemaArtifact.fieldUi();
    this.valueConstraints = fieldSchemaArtifact.valueConstraints();
  }

  protected FieldSchemaArtifactBuilder withJsonLdContext(Map<String, URI> jsonLdContext)
  {
    if (jsonLdContext == null)
      throw new IllegalArgumentException("null JSON-LD @context passed to builder");

    this.jsonLdContext = Map.copyOf(jsonLdContext);
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
    if (jsonLdId== null)
      throw new IllegalArgumentException("null JSON-LD @id passed to builder");

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

  protected FieldSchemaArtifactBuilder withPreferredLabel(String skosPrefLabel)
  {
    if (skosPrefLabel == null)
      throw new IllegalArgumentException("null SKOS preferred label passed to builder");

    this.skosPrefLabel = Optional.ofNullable(skosPrefLabel);
    return this;
  }

  protected FieldSchemaArtifactBuilder withAlternateLabels(List<String> skosAlternateLabels)
  {
    if (skosAlternateLabels == null)
      throw new IllegalArgumentException("null SKOS alternate labels passed to builder");

    this.skosAlternateLabels = skosAlternateLabels;
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

  public FieldSchemaArtifact build()
  {
    return FieldSchemaArtifact.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      isMultiple, minItems, maxItems, propertyUri,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      skosPrefLabel, skosAlternateLabels,
      fieldUi, valueConstraints);
  }
}