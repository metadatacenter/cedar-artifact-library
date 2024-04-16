package org.metadatacenter.artifacts.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.metadatacenter.artifacts.model.core.builders.AttributeValueFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.CheckboxFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.ControlledTermFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.EmailFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.ImageFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.LinkFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.ListFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.NumericFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.PageBreakFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.PhoneNumberFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.RadioFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.RichTextFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.SectionBreakFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.TemporalFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.TextAreaFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.TextFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.YouTubeFieldBuilder;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface FieldSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact permits TextField,
  TextAreaField, TemporalField, RadioField, PhoneNumberField, NumericField, ListField, LinkField, EmailField,
  ControlledTermField, CheckboxField, AttributeValueField, PageBreakField, SectionBreakField, ImageField, YouTubeField,
  RichTextField, FieldSchemaArtifactRecord
{
  static FieldSchemaArtifact create(URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    Optional<String> skosPrefLabel, List<String> skosAlternateLabels, FieldUi fieldUi, Optional<ValueConstraints> valueConstraints)
  {
    return new FieldSchemaArtifactRecord(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      isMultiple, minItems, maxItems, propertyUri,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      skosPrefLabel, skosAlternateLabels,
      fieldUi, valueConstraints);
  }

  FieldUi fieldUi();

  Optional<ValueConstraints> valueConstraints();

  Optional<String> skosPrefLabel();

  List<String> skosAlternateLabels();

  default boolean hidden() { return fieldUi().hidden(); }

  default boolean requiredValue() {
    return valueConstraints().isPresent() &&  valueConstraints().get().requiredValue();
  }

  @JsonIgnore
  default boolean isStatic() { return fieldUi().isStatic(); }

  @JsonIgnore
  default boolean isAttributeValue() { return fieldUi().isAttributeValue(); }

  default boolean hasIRIValue()
  {
    return (fieldUi().isTextField() &&
      (valueConstraints().isPresent() && valueConstraints().get().isControlledTermValueConstraint()))
      || fieldUi().isLink();
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

  static TextFieldBuilder textFieldBuilder() { return new TextFieldBuilder(); }

  static TextAreaFieldBuilder textAreaFieldBuilder() { return new TextAreaFieldBuilder(); }

  static ControlledTermFieldBuilder controlledTermFieldBuilder() { return new ControlledTermFieldBuilder(); }

  static NumericFieldBuilder numericFieldBuilder() { return new NumericFieldBuilder(); }

  static TemporalFieldBuilder temporalFieldBuilder() { return new TemporalFieldBuilder(); }

  static CheckboxFieldBuilder checkboxFieldBuilder() { return new CheckboxFieldBuilder(); }

  static EmailFieldBuilder emailFieldBuilder() { return new EmailFieldBuilder(); }

  static LinkFieldBuilder linkFieldBuilder() { return new LinkFieldBuilder(); }

  static ListFieldBuilder listFieldBuilder() { return new ListFieldBuilder(); }

  static PhoneNumberFieldBuilder phoneNumberFieldBuilder() { return new PhoneNumberFieldBuilder(); }

  static RadioFieldBuilder radioFieldBuilder() { return new RadioFieldBuilder(); }

  static PageBreakFieldBuilder pageBreakFieldBuilder() { return new PageBreakFieldBuilder(); }

  static SectionBreakFieldBuilder sectionBreakFieldBuilder() { return new SectionBreakFieldBuilder(); }

  static AttributeValueFieldBuilder attributeValueFieldBuilder() { return new AttributeValueFieldBuilder(); }

  static ImageFieldBuilder imageFieldBuilder() { return new ImageFieldBuilder(); }

  static RichTextFieldBuilder richTextFieldBuilder() { return new RichTextFieldBuilder(); }

  static YouTubeFieldBuilder youTubeFieldBuilder() { return new YouTubeFieldBuilder(); }

  @Override default void accept(SchemaArtifactVisitor visitor, String path) {
    visitor.visitFieldSchemaArtifact(this, path);
  }
}
