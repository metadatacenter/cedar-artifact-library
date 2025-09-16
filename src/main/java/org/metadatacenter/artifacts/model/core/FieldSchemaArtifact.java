package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public sealed interface FieldSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact
  permits TextField, TextAreaField, TemporalField, RadioField, PhoneNumberField, NumericField, ListField, EmailField,
  CheckboxField, AttributeValueField, PageBreakField, SectionBreakField, ImageField, YouTubeField, RichTextField,
  ControlledTermField, LinkField, RorField, OrcidField, PfasField, RridField, PubMedField, DoiField
{
  FieldUi fieldUi();

  Optional<ValueConstraints> valueConstraints();

  Optional<String> preferredLabel();

  List<String> alternateLabels();

  default boolean hidden() {return fieldUi().hidden();}

  default boolean requiredValue()
  {
    return valueConstraints().isPresent() && valueConstraints().get().requiredValue();
  }

  @JsonIgnore default boolean isStatic() {return fieldUi().isStatic();}

  @JsonIgnore default boolean isAttributeValue() {return fieldUi().isAttributeValue();}

  default boolean hasIRIValue()
  {
    return (fieldUi().isTextField() && (valueConstraints().isPresent() && valueConstraints().get()
      .isControlledTermValueConstraint())) || fieldUi().isIri();
  }

  default Optional<Integer> minLength()
  {
    if (valueConstraints().isPresent() && valueConstraints().get().isTextValueConstraint()) {
      TextValueConstraints textValueConstraints = valueConstraints().get().asTextValueConstraints();
      return textValueConstraints.minLength();
    } else
      return Optional.empty();
  }

  default Optional<Integer> maxLength()
  {
    if (valueConstraints().isPresent() && valueConstraints().get().isTextValueConstraint()) {
      TextValueConstraints textValueConstraints = valueConstraints().get().asTextValueConstraints();
      return textValueConstraints.maxLength();
    } else
      return Optional.empty();
  }

  default Optional<String> regex()
  {
    if (valueConstraints().isPresent() && valueConstraints().get().isTextValueConstraint()) {
      TextValueConstraints textValueConstraints = valueConstraints().get().asTextValueConstraints();
      return textValueConstraints.regex();
    } else
      return Optional.empty();
  }

  @Override default void accept(SchemaArtifactVisitor visitor, String path)
  {
    visitor.visitFieldSchemaArtifact(this, path);
  }

  default TextField asTextField()
  {
    if (this instanceof TextField)
      return (TextField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TextField.class.getName());
  }

  default TextAreaField asTextAreaField()
  {
    if (this instanceof TextAreaField)
      return (TextAreaField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + TextAreaField.class.getName());
  }

  default TemporalField asTemporalField()
  {
    if (this instanceof TemporalField)
      return (TemporalField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + TemporalField.class.getName());
  }

  default RadioField asRadioField()
  {
    if (this instanceof RadioField)
      return (RadioField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + RadioField.class.getName());
  }

  default PhoneNumberField asPhoneNumberField()
  {
    if (this instanceof PhoneNumberField)
      return (PhoneNumberField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + PhoneNumberField.class.getName());
  }

  default NumericField asNumericField()
  {
    if (this instanceof NumericField)
      return (NumericField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + NumericField.class.getName());
  }

  default ListField asListField()
  {
    if (this instanceof ListField)
      return (ListField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + ListField.class.getName());
  }

  default LinkField asLinkField()
  {
    if (this instanceof LinkField)
      return (LinkField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + LinkField.class.getName());
  }

  default EmailField asEmailField()
  {
    if (this instanceof EmailField)
      return (EmailField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + EmailField.class.getName());
  }

  default ControlledTermField asControlledTermField()
  {
    if (this instanceof ControlledTermField)
      return (ControlledTermField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + ControlledTermField.class.getName());
  }

  default CheckboxField asCheckboxField()
  {
    if (this instanceof CheckboxField)
      return (CheckboxField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + CheckboxField.class.getName());
  }

  default AttributeValueField asAttributeValueField()
  {
    if (this instanceof AttributeValueField)
      return (AttributeValueField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + AttributeValueField.class.getName());
  }

  default PageBreakField asPageBreakField()
  {
    if (this instanceof PageBreakField)
      return (PageBreakField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + PageBreakField.class.getName());
  }

  default SectionBreakField asSectionBreakField()
  {
    if (this instanceof SectionBreakField)
      return (SectionBreakField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + SectionBreakField.class.getName());
  }

  default ImageField asImageField()
  {
    if (this instanceof ImageField)
      return (ImageField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + ImageField.class.getName());
  }

  default YouTubeField asYouTubeField()
  {
    if (this instanceof YouTubeField)
      return (YouTubeField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + YouTubeField.class.getName());
  }

  default RichTextField asRichTextField()
  {
    if (this instanceof RichTextField)
      return (RichTextField)this;
    else
      throw new ClassCastException(
        "Cannot convert " + this.getClass().getName() + " to " + RichTextField.class.getName());
  }

  default RorField asRorField()
  {
    if (this instanceof RorField)
      return (RorField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + RorField.class.getName());
  }

  default OrcidField asOrcidField()
  {
    if (this instanceof OrcidField)
      return (OrcidField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + OrcidField.class.getName());
  }

  default PfasField asPfasField()
  {
    if (this instanceof PfasField)
      return (PfasField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + PfasField.class.getName());
  }

  default RridField asRridField()
  {
    if (this instanceof RridField)
      return (RridField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + RridField.class.getName());
  }

  default PubMedField asPubMedField()
  {
    if (this instanceof PubMedField)
      return (PubMedField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + PubMedField.class.getName());
  }

  default DoiField asDoiField()
  {
    if (this instanceof DoiField)
      return (DoiField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + DoiField.class.getName());
  }

  static FieldSchemaArtifact create(String internalName, String internalDescription,
    LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId, String name,
    String description, Optional<String> identifier, Optional<Version> version, Optional<Status> status,
    Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple, Optional<Integer> minItems,
    Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy, Optional<URI> modifiedBy,
    Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn, Optional<String> preferredLabel,
    List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations)
  {
    // TODO Use typesafe switch when available
    if (fieldUi.inputType() == FieldInputType.TEXTFIELD) {
      if (valueConstraints.isPresent() && valueConstraints.get().isControlledTermValueConstraint())
        return ControlledTermField.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId, name,
          description, identifier, version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems,
          propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language,
          fieldUi, valueConstraints, annotations);
      else
        return TextField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
          internalName, internalDescription);
    } else if (fieldUi.inputType() == FieldInputType.TEXTAREA)
      return TextAreaField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.PHONE_NUMBER)
      return PhoneNumberField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.EMAIL)
      return EmailField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.RADIO)
      return RadioField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.CHECKBOX)
      return CheckboxField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.LIST)
      return ListField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.LINK)
      return LinkField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.ROR)
      return RorField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.ORCID)
      return OrcidField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
    else if (fieldUi.inputType() == FieldInputType.PFAS)
      return PfasField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
          internalDescription);
    else if (fieldUi.inputType() == FieldInputType.RRID)
      return RridField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
          internalDescription);
    else if (fieldUi.inputType() == FieldInputType.PUBMED)
      return PubMedField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
          internalDescription);
    else if (fieldUi.inputType() == FieldInputType.DOI)
      return DoiField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
          internalDescription);
    else if (fieldUi.inputType() == FieldInputType.NUMERIC)
      return NumericField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asNumericFieldUi(), valueConstraints,
        annotations, internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.TEMPORAL)
      return TemporalField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asTemporalFieldUi(), valueConstraints,
        annotations, internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.ATTRIBUTE_VALUE)
      return AttributeValueField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.PAGE_BREAK)
      return PageBreakField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels,
        language, fieldUi, valueConstraints, annotations, internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.SECTION_BREAK)
      return SectionBreakField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, language,
        fieldUi, annotations, internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.RICHTEXT)
      return RichTextField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, language, preferredLabel,
        fieldUi.asStaticFieldUi(), annotations, internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.IMAGE)
      return ImageField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, language, preferredLabel,
        fieldUi, annotations, internalName, internalDescription);
    else if (fieldUi.inputType() == FieldInputType.YOUTUBE)
      return YouTubeField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, language, fieldUi, annotations,
        internalName, internalDescription);
    else
      throw new IllegalArgumentException("unknown input type " + fieldUi.inputType() + " for field " + name);
  }
}
