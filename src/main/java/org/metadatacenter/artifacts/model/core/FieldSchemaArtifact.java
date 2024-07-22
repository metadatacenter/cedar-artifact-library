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

public sealed interface FieldSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact permits TextField,
  TextAreaField, TemporalField, RadioField, PhoneNumberField, NumericField, ListField, LinkField, EmailField,
  ControlledTermField, CheckboxField, AttributeValueField, PageBreakField, SectionBreakField, ImageField, YouTubeField,
  RichTextField
{
  FieldUi fieldUi();

  Optional<ValueConstraints> valueConstraints();

  Optional<String> preferredLabel();

  List<String> alternateLabels();

  default boolean hidden() { return fieldUi().hidden(); }

  default boolean requiredValue()
  {
    return valueConstraints().isPresent() && valueConstraints().get().requiredValue();
  }

  @JsonIgnore default boolean isStatic() {return fieldUi().isStatic();}

  @JsonIgnore default boolean isAttributeValue() { return fieldUi().isAttributeValue(); }

  default boolean hasIRIValue()
  {
    return (fieldUi().isTextField() && (valueConstraints().isPresent() && valueConstraints().get()
      .isControlledTermValueConstraint())) || fieldUi().isLink();
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
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TextAreaField.class.getName());
  }

  default TemporalField asTemporalField()
  {
    if (this instanceof TemporalField)
      return (TemporalField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TemporalField.class.getName());
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
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + PhoneNumberField.class.getName());
  }

  default NumericField asNumericField()
  {
    if (this instanceof NumericField)
      return (NumericField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + NumericField.class.getName());
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
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + ControlledTermField.class.getName());
  }

  default CheckboxField asCheckboxField()
  {
    if (this instanceof CheckboxField)
      return (CheckboxField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + CheckboxField.class.getName());
  }

  default AttributeValueField asAttributeValueField()
  {
    if (this instanceof AttributeValueField)
      return (AttributeValueField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + AttributeValueField.class.getName());
  }
  default PageBreakField asPageBreakField()
  {
    if (this instanceof PageBreakField)
      return (PageBreakField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + PageBreakField.class.getName());
  }

  default SectionBreakField asSectionBreakField()
  {
    if (this instanceof SectionBreakField)
      return (SectionBreakField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + SectionBreakField.class.getName());
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
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + YouTubeField.class.getName());
  }

  default RichTextField asRichTextField()
  {
    if (this instanceof RichTextField)
      return (RichTextField)this;
    else
      throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + RichTextField.class.getName());
  }

  static FieldSchemaArtifact create(String jsonSchemaTitle,
    String jsonSchemaDescription, LinkedHashMap<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier, Version modelVersion, Optional<Version> version,
    Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom, boolean isMultiple,
    Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri, Optional<URI> createdBy,
    Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> preferredLabel, List<String> alternateLabels, Optional<String> language, FieldUi fieldUi,
    Optional<ValueConstraints> valueConstraints, Optional<Annotations> annotations)
  {
    // TODO Use typesafe switch when available
    if (fieldUi.inputType() == FieldInputType.TEXTFIELD) {
      if (valueConstraints.isPresent() && valueConstraints.get().isControlledTermValueConstraint())
        return ControlledTermField.create(jsonSchemaTitle, jsonSchemaDescription,
          jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
      else
        return TextField.create(jsonSchemaTitle, jsonSchemaDescription,
          jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    } else if (fieldUi.inputType() == FieldInputType.TEXTAREA)
      return TextAreaField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.PHONE_NUMBER)
      return PhoneNumberField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.EMAIL)
      return EmailField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language,fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.RADIO)
      return RadioField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.CHECKBOX)
      return CheckboxField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.LIST)
      return ListField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.LINK)
      return LinkField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.NUMERIC)
      return NumericField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asNumericFieldUi(), valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.TEMPORAL)
      return TemporalField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asTemporalFieldUi(), valueConstraints,
        annotations);
    else if (fieldUi.inputType() == FieldInputType.ATTRIBUTE_VALUE)
      return AttributeValueField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.PAGE_BREAK)
      return PageBreakField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.SECTION_BREAK)
      return SectionBreakField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, language, fieldUi, annotations);
    else if (fieldUi.inputType() == FieldInputType.RICHTEXT)
      return RichTextField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, language, preferredLabel, fieldUi.asStaticFieldUi(), annotations);
    else if (fieldUi.inputType() == FieldInputType.IMAGE)
      return ImageField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, language, preferredLabel, fieldUi, annotations);
    else if (fieldUi.inputType() == FieldInputType.YOUTUBE)
      return YouTubeField.create(jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, language, fieldUi, annotations);
    else
      throw new IllegalArgumentException("unknown input type " + fieldUi.inputType() + " for field " + name);
  }
}
