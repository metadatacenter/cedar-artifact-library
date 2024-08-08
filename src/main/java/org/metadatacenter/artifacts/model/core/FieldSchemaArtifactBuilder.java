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
  protected String internalName = "";
  protected String internalDescription = "";
  protected String name;
  protected String description = "";
  protected Optional<String> identifier = Optional.empty();
  protected Optional<String> preferredLabel = Optional.empty();
  protected List<String> alternateLabels = Collections.emptyList();
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

  static public FieldSchemaArtifactBuilder builder(FieldSchemaArtifact fieldSchemaArtifact)
  {
    // TODO Use typesafe switch when available
    if (fieldSchemaArtifact instanceof TextField)
      return new TextField.TextFieldBuilder(fieldSchemaArtifact.asTextField());
    else if (fieldSchemaArtifact instanceof TextAreaField)
      return new TextAreaField.TextAreaFieldBuilder(fieldSchemaArtifact.asTextAreaField());
    else if (fieldSchemaArtifact instanceof TemporalField)
      return new TemporalField.TemporalFieldBuilder(fieldSchemaArtifact.asTemporalField());
    else if (fieldSchemaArtifact instanceof RadioField)
      return new RadioField.RadioFieldBuilder(fieldSchemaArtifact.asRadioField());
    else if (fieldSchemaArtifact instanceof PhoneNumberField)
      return new PhoneNumberField.PhoneNumberFieldBuilder(fieldSchemaArtifact.asPhoneNumberField());
    else if (fieldSchemaArtifact instanceof NumericField)
      return new NumericField.NumericFieldBuilder(fieldSchemaArtifact.asNumericField());
    else if (fieldSchemaArtifact instanceof ListField)
      return new ListField.ListFieldBuilder(fieldSchemaArtifact.asListField());
    else if (fieldSchemaArtifact instanceof LinkField)
      return new LinkField.LinkFieldBuilder(fieldSchemaArtifact.asLinkField());
    else if (fieldSchemaArtifact instanceof EmailField)
      return new EmailField.EmailFieldBuilder(fieldSchemaArtifact.asEmailField());
    else if (fieldSchemaArtifact instanceof ControlledTermField)
      return new ControlledTermField.ControlledTermFieldBuilder(fieldSchemaArtifact.asControlledTermField());
    else if (fieldSchemaArtifact instanceof CheckboxField)
      return new CheckboxField.CheckboxFieldBuilder(fieldSchemaArtifact.asCheckboxField());
    else if (fieldSchemaArtifact instanceof AttributeValueField)
      return new AttributeValueField.AttributeValueFieldBuilder(fieldSchemaArtifact.asAttributeValueField());
    else if (fieldSchemaArtifact instanceof PageBreakField)
      return new PageBreakField.PageBreakFieldBuilder(fieldSchemaArtifact.asPageBreakField());
    else if (fieldSchemaArtifact instanceof SectionBreakField)
      return new SectionBreakField.SectionBreakFieldBuilder(fieldSchemaArtifact.asSectionBreakField());
    else if (fieldSchemaArtifact instanceof ImageField)
      return new ImageField.ImageFieldBuilder(fieldSchemaArtifact.asImageField());
    else if (fieldSchemaArtifact instanceof YouTubeField)
      return new YouTubeField.YouTubeFieldBuilder(fieldSchemaArtifact.asYouTubeField());
    else if (fieldSchemaArtifact instanceof RichTextField)
      return new RichTextField.RichTextFieldBuilder(fieldSchemaArtifact.asRichTextField());
    else
      throw new IllegalArgumentException("class " + fieldSchemaArtifact.getClass().getName() + " has no known builder");
  }


  public abstract FieldSchemaArtifact build();

  protected FieldSchemaArtifactBuilder(String jsonSchemaType, URI artifactTypeIri)
  {
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
    this.internalName = fieldSchemaArtifact.internalName();
    this.internalDescription = fieldSchemaArtifact.internalDescription();
    this.name = fieldSchemaArtifact.name();
    this.description = fieldSchemaArtifact.description();
    this.identifier = fieldSchemaArtifact.identifier();
    this.preferredLabel = fieldSchemaArtifact.preferredLabel();
    this.alternateLabels = fieldSchemaArtifact.alternateLabels();
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

  public abstract FieldSchemaArtifactBuilder withRequiredValue(boolean required);

  public abstract FieldSchemaArtifactBuilder withRecommendedValue(boolean recommendedValue);

  public abstract FieldSchemaArtifactBuilder withContinuePreviousLine(boolean continuePreviousLine);

  public abstract FieldSchemaArtifactBuilder withHidden(boolean hidden);

  public abstract FieldSchemaArtifactBuilder withValueRecommendationEnabled(boolean valueRecommendationEnabled);

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

    if (this.internalName.isEmpty())
      this.internalName = name + " field schema";

    if (this.internalDescription.isEmpty())
      this.internalDescription = name + " field schema generated by the CEDAR Artifact Library";

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

  protected FieldSchemaArtifactBuilder withInternalName(String internalName)
  {
    if (internalName == null)
      throw new IllegalArgumentException("null JSON Schema title passed to builder");

    this.internalName = internalName;
    return this;
  }

  protected FieldSchemaArtifactBuilder withInternalDescription(String internalDescription)
  {
    if (internalDescription == null)
      throw new IllegalArgumentException("null JSON Schema description passed to builder");

    this.internalDescription = internalDescription;
    return this;
  }

  protected FieldSchemaArtifactBuilder withAnnotations(Annotations annotations)
  {
    if (annotations == null)
      throw new IllegalArgumentException("null annotations passed to builder");

    this.annotations = Optional.ofNullable(annotations);
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
}