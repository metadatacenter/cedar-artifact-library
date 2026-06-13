package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    return switch (fieldUi.inputType()) {
      case TEXTFIELD -> (valueConstraints.isPresent() && valueConstraints.get().isControlledTermValueConstraint())
        ? ControlledTermField.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId, name,
            description, identifier, version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems,
            propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language,
            fieldUi, valueConstraints, annotations)
        : TextField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
            previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
            lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
            internalName, internalDescription);
      case TEXTAREA -> TextAreaField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier,
        version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints,
        annotations, internalName, internalDescription);
      case PHONE_NUMBER -> PhoneNumberField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier,
        version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints,
        annotations, internalName, internalDescription);
      case EMAIL -> EmailField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case RADIO -> RadioField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
      case CHECKBOX -> CheckboxField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier,
        version, status, previousVersion, derivedFrom, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case LIST -> ListField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case LINK -> LinkField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case ROR -> RorField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
      case ORCID -> OrcidField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case PFAS -> PfasField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case RRID -> RridField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case PUBMED -> PubMedField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations,
        internalName, internalDescription);
      case NIH_GRANT_ID -> NihGrantIdField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier,
        version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints,
        annotations, internalName, internalDescription);
      case DOI -> DoiField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version, status,
        previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn,
        lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations, internalName,
        internalDescription);
      case NUMERIC -> NumericField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy, modifiedBy,
        createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asNumericFieldUi(),
        valueConstraints, annotations, internalName, internalDescription);
      case TEMPORAL -> TemporalField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier,
        version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi.asTemporalFieldUi(),
        valueConstraints, annotations, internalName, internalDescription);
      case ATTRIBUTE_VALUE -> AttributeValueField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description,
        identifier, version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems, propertyUri,
        createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi,
        valueConstraints, annotations, internalName, internalDescription);
      case PAGE_BREAK -> PageBreakField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier,
        version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel,
        alternateLabels, language, fieldUi, valueConstraints, annotations, internalName, internalDescription);
      case SECTION_BREAK -> SectionBreakField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description,
        identifier, version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, language, fieldUi, annotations, internalName, internalDescription);
      case RICHTEXT -> RichTextField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier,
        version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, language,
        preferredLabel, fieldUi.asStaticFieldUi(), annotations, internalName, internalDescription);
      case IMAGE -> ImageField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, language, preferredLabel,
        fieldUi, annotations, internalName, internalDescription);
      case YOUTUBE -> YouTubeField.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, identifier, version,
        status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, fieldUi,
        annotations, internalName, internalDescription);
    };
  }
}
