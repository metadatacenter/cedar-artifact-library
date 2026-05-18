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
  ControlledTermField, LinkField, RorField, OrcidField, PfasField, RridField, PubMedField, NihGrantIdField, DoiField
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
    else if (fieldUi.inputType() == FieldInputType.NIH_GRANT_ID)
      return NihGrantIdField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
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
