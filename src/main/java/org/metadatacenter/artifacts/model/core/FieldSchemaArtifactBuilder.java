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

public abstract sealed class FieldSchemaArtifactBuilder<SELF extends FieldSchemaArtifactBuilder<SELF>>
  permits TextField.TextFieldBuilder,
    TextAreaField.TextAreaFieldBuilder, TemporalField.TemporalFieldBuilder,
    RadioField.RadioFieldBuilder, PhoneNumberField.PhoneNumberFieldBuilder,
    NumericField.NumericFieldBuilder, ListField.ListFieldBuilder,
    EmailField.EmailFieldBuilder,
    CheckboxField.CheckboxFieldBuilder, AttributeValueField.AttributeValueFieldBuilder,
    PageBreakField.PageBreakFieldBuilder, SectionBreakField.SectionBreakFieldBuilder, ImageField.ImageFieldBuilder,
    YouTubeField.YouTubeFieldBuilder, RichTextField.RichTextFieldBuilder,
    ControlledTermField.ControlledTermFieldBuilder,
    LinkField.LinkFieldBuilder, RorField.RorFieldBuilder, OrcidField.OrcidFieldBuilder,
    PfasField.PfasFieldBuilder, RridField.RridFieldBuilder, PubMedField.PubMedFieldBuilder,
    NihGrantIdField.NihGrantIdFieldBuilder, DoiField.DoiFieldBuilder {

  @SuppressWarnings("unchecked")
  protected SELF self() { return (SELF) this; }

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

  static public FieldSchemaArtifactBuilder builder(FieldSchemaArtifact fieldSchemaArtifact) {
    // TODO Use sealed pattern switch when adopted (Java 21+)
    if (fieldSchemaArtifact instanceof TextField tf) {
      return new TextField.TextFieldBuilder(tf);
    } else if (fieldSchemaArtifact instanceof TextAreaField tf) {
      return new TextAreaField.TextAreaFieldBuilder(tf);
    } else if (fieldSchemaArtifact instanceof TemporalField tf) {
      return new TemporalField.TemporalFieldBuilder(tf);
    } else if (fieldSchemaArtifact instanceof RadioField rf) {
      return new RadioField.RadioFieldBuilder(rf);
    } else if (fieldSchemaArtifact instanceof PhoneNumberField pf) {
      return new PhoneNumberField.PhoneNumberFieldBuilder(pf);
    } else if (fieldSchemaArtifact instanceof NumericField nf) {
      return new NumericField.NumericFieldBuilder(nf);
    } else if (fieldSchemaArtifact instanceof ListField lf) {
      return new ListField.ListFieldBuilder(lf);
    } else if (fieldSchemaArtifact instanceof LinkField lf) {
      return new LinkField.LinkFieldBuilder(lf);
    } else if (fieldSchemaArtifact instanceof EmailField ef) {
      return new EmailField.EmailFieldBuilder(ef);
    } else if (fieldSchemaArtifact instanceof ControlledTermField ctf) {
      return new ControlledTermField.ControlledTermFieldBuilder(ctf);
    } else if (fieldSchemaArtifact instanceof CheckboxField cf) {
      return new CheckboxField.CheckboxFieldBuilder(cf);
    } else if (fieldSchemaArtifact instanceof AttributeValueField avf) {
      return new AttributeValueField.AttributeValueFieldBuilder(avf);
    } else if (fieldSchemaArtifact instanceof PageBreakField pf) {
      return new PageBreakField.PageBreakFieldBuilder(pf);
    } else if (fieldSchemaArtifact instanceof SectionBreakField sf) {
      return new SectionBreakField.SectionBreakFieldBuilder(sf);
    } else if (fieldSchemaArtifact instanceof ImageField imgf) {
      return new ImageField.ImageFieldBuilder(imgf);
    } else if (fieldSchemaArtifact instanceof YouTubeField yf) {
      return new YouTubeField.YouTubeFieldBuilder(yf);
    } else if (fieldSchemaArtifact instanceof RichTextField rtf) {
      return new RichTextField.RichTextFieldBuilder(rtf);
    } else if (fieldSchemaArtifact instanceof RorField rf) {
      return new RorField.RorFieldBuilder(rf);
    } else if (fieldSchemaArtifact instanceof OrcidField of) {
      return new OrcidField.OrcidFieldBuilder(of);
    } else if (fieldSchemaArtifact instanceof PfasField pf) {
      return new PfasField.PfasFieldBuilder(pf);
    } else if (fieldSchemaArtifact instanceof RridField rf) {
      return new RridField.RridFieldBuilder(rf);
    } else if (fieldSchemaArtifact instanceof PubMedField pmf) {
      return new PubMedField.PubMedFieldBuilder(pmf);
    } else if (fieldSchemaArtifact instanceof NihGrantIdField ngf) {
      return new NihGrantIdField.NihGrantIdFieldBuilder(ngf);
    } else if (fieldSchemaArtifact instanceof DoiField df) {
      return new DoiField.DoiFieldBuilder(df);
    } else {
      throw new IllegalArgumentException("class " + fieldSchemaArtifact.getClass().getName() + " has no known builder");
    }
  }

  public abstract FieldSchemaArtifact build();

  protected FieldSchemaArtifactBuilder(String jsonSchemaType, URI artifactTypeIri) {
    this.jsonLdTypes.add(artifactTypeIri);
  }

  protected FieldSchemaArtifactBuilder(FieldSchemaArtifact fieldSchemaArtifact) {
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

  public abstract SELF withRequiredValue(boolean required);

  public abstract SELF withRecommendedValue(boolean recommendedValue);

  public abstract SELF withContinuePreviousLine(boolean continuePreviousLine);

  public abstract SELF withHidden(boolean hidden);

  public abstract SELF withValueRecommendationEnabled(boolean valueRecommendationEnabled);

  public SELF withJsonLdContext(LinkedHashMap<String, URI> jsonLdContext) {
    if (jsonLdContext == null) {
      throw new IllegalArgumentException("null JSON-LD @context passed to builder");
    }

    this.jsonLdContext = new LinkedHashMap<>(jsonLdContext);
    return self();
  }

  public SELF withJsonLdType(URI jsonLdType) {
    if (jsonLdType == null) {
      throw new IllegalArgumentException("null JSON-LD @type passed to builder");
    }

    this.jsonLdTypes.add(jsonLdType);
    return self();
  }

  public SELF withJsonLdId(URI jsonLdId) {
    this.jsonLdId = Optional.ofNullable(jsonLdId);
    return self();
  }

  public SELF withName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("null name passed to builder");
    }

    this.name = name;

    if (this.internalName.isEmpty()) {
      this.internalName = name + " field schema";
    }

    if (this.internalDescription.isEmpty()) {
      this.internalDescription = name + " field schema generated by the CEDAR Artifact Library";
    }

    return self();
  }

  public SELF withDescription(String description) {
    if (description == null) {
      throw new IllegalArgumentException("null description passed to builder");
    }

    this.description = description;
    return self();
  }

  public SELF withIdentifier(String identifier) {
    if (identifier == null) {
      throw new IllegalArgumentException("null identifier passed to builder");
    }

    this.identifier = Optional.ofNullable(identifier);
    return self();
  }

  public SELF withPreferredLabel(String preferredLabel) {
    if (preferredLabel == null) {
      throw new IllegalArgumentException("null preferred label passed to builder");
    }

    this.preferredLabel = Optional.ofNullable(preferredLabel);
    return self();
  }

  public SELF withAlternateLabels(List<String> alternateLabels) {
    if (alternateLabels == null) {
      throw new IllegalArgumentException("null alternate labels passed to builder");
    }

    this.alternateLabels = alternateLabels;
    return self();
  }

  public SELF withVersion(Version version) {
    if (version == null) {
      throw new IllegalArgumentException("null artifact version passed to builder");
    }

    this.version = Optional.ofNullable(version);
    return self();
  }

  public SELF withStatus(Status status) {
    this.status = Optional.ofNullable(status);
    return self();
  }

  public SELF withCreatedBy(URI createdBy) {
    this.createdBy = Optional.ofNullable(createdBy);
    return self();
  }

  public SELF withModifiedBy(URI modifiedBy) {
    this.modifiedBy = Optional.ofNullable(modifiedBy);
    return self();
  }

  public SELF withCreatedOn(OffsetDateTime createdOn) {
    this.createdOn = Optional.ofNullable(createdOn);
    return self();
  }

  public SELF withLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
    this.lastUpdatedOn = Optional.ofNullable(lastUpdatedOn);
    return self();
  }

  public SELF withPreviousVersion(URI previousVersion) {
    this.previousVersion = Optional.ofNullable(previousVersion);
    return self();
  }

  public SELF withDerivedFrom(URI derivedFrom) {
    this.derivedFrom = Optional.ofNullable(derivedFrom);
    return self();
  }

  public SELF withIsMultiple(boolean isMultiple) {
    this.isMultiple = isMultiple;
    return self();
  }

  public SELF withMinItems(Integer minItems) {
    this.minItems = Optional.ofNullable(minItems);
    return self();
  }

  public SELF withMaxItems(Integer maxItems) {
    this.maxItems = Optional.ofNullable(maxItems);
    return self();
  }

  public SELF withPropertyUri(URI propertyUri) {
    this.propertyUri = Optional.ofNullable(propertyUri);
    return self();
  }

  public SELF withLanguage(String language) {
    this.language = Optional.ofNullable(language);
    return self();
  }

  public SELF withInternalName(String internalName) {
    if (internalName == null) {
      throw new IllegalArgumentException("null JSON Schema title passed to builder");
    }

    this.internalName = internalName;
    return self();
  }

  public SELF withInternalDescription(String internalDescription) {
    if (internalDescription == null) {
      throw new IllegalArgumentException("null JSON Schema description passed to builder");
    }

    this.internalDescription = internalDescription;
    return self();
  }

  public SELF withAnnotations(Annotations annotations) {
    if (annotations == null) {
      throw new IllegalArgumentException("null annotations passed to builder");
    }

    this.annotations = Optional.ofNullable(annotations);
    return self();
  }

  public SELF withFieldUi(FieldUi fieldUi) {
    if (fieldUi == null) {
      throw new IllegalArgumentException("null field UI passed to builder");
    }

    this.fieldUi = fieldUi;

    return self();
  }

  public SELF withValueConstraints(ValueConstraints valueConstraints) {
    if (valueConstraints == null) {
      throw new IllegalArgumentException("null value constraints passed to builder");
    }

    this.valueConstraints = Optional.ofNullable(valueConstraints);
    return self();
  }
}
