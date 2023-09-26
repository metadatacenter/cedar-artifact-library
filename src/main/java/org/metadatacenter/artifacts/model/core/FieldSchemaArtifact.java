package org.metadatacenter.artifacts.model.core;

import org.metadatacenter.artifacts.model.core.builders.CheckboxFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.ControlledTermFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.EmailFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.ImageFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.LinkFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.ListFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.NumericFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.PhoneNumberFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.RadioFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.RichTextFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.TemporalFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.TextAreaFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.AttributeValueFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.SectionBreakFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.TextFieldBuilder;
import org.metadatacenter.artifacts.model.core.builders.YouTubeFieldBuilder;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateListFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateMapContainsAll;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateOptionalFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUiFieldNotNull;
import static org.metadatacenter.artifacts.model.core.ValidationHelper.validateUriListContainsOneOf;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;

public non-sealed interface FieldSchemaArtifact extends SchemaArtifact, ChildSchemaArtifact
{
  static FieldSchemaArtifact create(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
    Optional<URI> createdBy, Optional<URI> modifiedBy, Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
    URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
    String name, String description, Optional<String> identifier,
    Version modelVersion, Optional<Version> version, Optional<Status> status, Optional<URI> previousVersion, Optional<URI> derivedFrom,
    boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri,
    Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
    FieldUi fieldUi, Optional<ValueConstraints> valueConstraints)
  {
    return new FieldSchemaArtifactRecord(jsonLdContext, jsonLdTypes, jsonLdId, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription, name, description,
      identifier, modelVersion, version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems,
      skosPrefLabel, skosAlternateLabels, propertyUri, fieldUi, valueConstraints);
  }

  FieldUi fieldUi();

  Optional<ValueConstraints> valueConstraints();

  Optional<String> skosPrefLabel();

  List<String> skosAlternateLabels();

  default boolean hidden() { return fieldUi().hidden(); }

  default boolean requiredValue() {
    return valueConstraints().isPresent() &&  valueConstraints().get().requiredValue();
  }

  default boolean isStatic() { return fieldUi().isStatic(); }

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

  static SectionBreakFieldBuilder sectionBreakFieldBuilder() { return new SectionBreakFieldBuilder(); }

  static AttributeValueFieldBuilder attributeValueFieldBuilder() { return new AttributeValueFieldBuilder(); }

  static ImageFieldBuilder imageFieldBuilder() { return new ImageFieldBuilder(); }

  static RichTextFieldBuilder richTextFieldBuilder() { return new RichTextFieldBuilder(); }

  static YouTubeFieldBuilder youTubeFieldBuilder() { return new YouTubeFieldBuilder(); }
}

record FieldSchemaArtifactRecord(Map<String, URI> jsonLdContext, List<URI> jsonLdTypes, Optional<URI> jsonLdId,
                                 Optional<URI> createdBy, Optional<URI> modifiedBy,
                                 Optional<OffsetDateTime> createdOn, Optional<OffsetDateTime> lastUpdatedOn,
                                 URI jsonSchemaSchemaUri, String jsonSchemaType, String jsonSchemaTitle, String jsonSchemaDescription,
                                 String name, String description, Optional<String> identifier,
                                 Version modelVersion, Optional<Version> version, Optional<Status> status,
                                 Optional<URI> previousVersion, Optional<URI> derivedFrom,
                                 boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems,
                                 Optional<String> skosPrefLabel, List<String> skosAlternateLabels,
                                 Optional<URI> propertyUri,
                                 FieldUi fieldUi, Optional<ValueConstraints> valueConstraints)
  implements FieldSchemaArtifact
{
  public FieldSchemaArtifactRecord
  {
    validateMapContainsAll(this, jsonLdContext, JSON_LD_CONTEXT, SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    validateUriListContainsOneOf(this, jsonLdTypes, ModelNodeNames.JSON_LD_TYPE, Set.of(URI.create(FIELD_SCHEMA_ARTIFACT_TYPE_IRI), URI.create(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI)));
    validateOptionalFieldNotNull(this, skosPrefLabel, SKOS_PREFLABEL);
    validateListFieldNotNull(this, skosAlternateLabels, SKOS_ALTLABEL);
    validateOptionalFieldNotNull(this, minItems, JSON_SCHEMA_MIN_ITEMS);
    validateOptionalFieldNotNull(this, maxItems, JSON_SCHEMA_MAX_ITEMS);
    validateOptionalFieldNotNull(this, propertyUri, "propertyUri"); // TODO Add to ModelNodeNames
    validateUiFieldNotNull(this, fieldUi, UI);
    validateOptionalFieldNotNull(this, valueConstraints, VALUE_CONSTRAINTS);

    if (minItems.isPresent() && minItems.get() < 0)
      throw new IllegalStateException("minItems must be zero or greater in element schema artifact " + name);

    if (maxItems.isPresent() && maxItems.get() < 1)
      throw new IllegalStateException("maxItems must be one or greater in element schema artifact " + name);

    if (minItems.isPresent() && maxItems.isPresent() && (minItems.get() > maxItems.get()))
      throw new IllegalStateException("minItems must be lass than maxItems in element schema artifact " + name);

    jsonLdContext = Map.copyOf(jsonLdContext);
    jsonLdTypes = List.copyOf(jsonLdTypes);
  }
}
