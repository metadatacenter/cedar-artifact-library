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

  static FieldSchemaArtifact create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle,
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
        return ControlledTermField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
          jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
      else
        return TextField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
          jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
          previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
          lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    } else if (fieldUi.inputType() == FieldInputType.TEXTAREA)
      return TextAreaField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.PHONE_NUMBER)
      return PhoneNumberField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.EMAIL)
      return EmailField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language,fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.RADIO)
      return RadioField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.CHECKBOX)
      return CheckboxField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.LIST)
      return ListField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.LINK)
      return LinkField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.NUMERIC)
      return NumericField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.TEMPORAL)
      return TemporalField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.ATTRIBUTE_VALUE)
      return AttributeValueField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.PAGE_BREAK)
      return PageBreakField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
    else if (fieldUi.inputType() == FieldInputType.SECTION_BREAK)
      return SectionBreakField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, language, fieldUi, annotations);
    else if (fieldUi.inputType() == FieldInputType.RICHTEXT)
      return RichTextField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, language, preferredLabel, fieldUi, annotations);
    else if (fieldUi.inputType() == FieldInputType.IMAGE)
      return ImageField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, language, preferredLabel, fieldUi, annotations);
    else if (fieldUi.inputType() == FieldInputType.YOUTUBE)
      return YouTubeField.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
        jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, modelVersion, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, language, fieldUi, annotations);
    else
      throw new RuntimeException("unknown input type " + fieldUi.inputType() + " for field " + name);
  }
}
