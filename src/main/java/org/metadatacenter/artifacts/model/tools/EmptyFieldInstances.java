package org.metadatacenter.artifacts.model.tools;

import org.metadatacenter.artifacts.model.core.CheckboxFieldInstance;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.ControlledTermFieldInstance;
import org.metadatacenter.artifacts.model.core.DoiFieldInstance;
import org.metadatacenter.artifacts.model.core.EmailFieldInstance;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.LinkFieldInstance;
import org.metadatacenter.artifacts.model.core.ListFieldInstance;
import org.metadatacenter.artifacts.model.core.NihGrantIdFieldInstance;
import org.metadatacenter.artifacts.model.core.NumericFieldInstance;
import org.metadatacenter.artifacts.model.core.OrcidFieldInstance;
import org.metadatacenter.artifacts.model.core.PfasFieldInstance;
import org.metadatacenter.artifacts.model.core.PhoneNumberFieldInstance;
import org.metadatacenter.artifacts.model.core.PubMedFieldInstance;
import org.metadatacenter.artifacts.model.core.RadioFieldInstance;
import org.metadatacenter.artifacts.model.core.RorFieldInstance;
import org.metadatacenter.artifacts.model.core.RridFieldInstance;
import org.metadatacenter.artifacts.model.core.TemporalFieldInstance;
import org.metadatacenter.artifacts.model.core.TextAreaFieldInstance;
import org.metadatacenter.artifacts.model.core.TextFieldInstance;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;

/**
 * Dispatch from a {@link FieldSchemaArtifact} to an empty (value-less)
 * {@link FieldInstanceArtifact} of the matching kind — one empty value per schema child when
 * building or completing an instance skeleton.
 *
 * <p>Static fields (page-break, section-break, rich-text, image, youtube) and attribute-value
 * fields have no regular instance representation and are excluded — the caller must check
 * {@link FieldInputType#isStatic} / {@link FieldInputType#isAttributeValue} and skip the schema
 * child before reaching this helper.
 */
public final class EmptyFieldInstances
{
  private EmptyFieldInstances() {}

  /**
   * @throws IllegalArgumentException if the schema's input type has no regular instance
   *   representation (static, attribute-value, or an unknown type).
   */
  public static FieldInstanceArtifact emptyFor(FieldSchemaArtifact field)
  {
    FieldInputType inputType = field.fieldUi().inputType();

    // The TEXTFIELD input type covers both text-field and controlled-term-field; pick
    // ControlledTermFieldInstance when the schema is a controlled-term field, else
    // TextFieldInstance.
    if (inputType == FieldInputType.TEXTFIELD) {
      if (field instanceof ControlledTermField)
        return ControlledTermFieldInstance.builder().build();
      return TextFieldInstance.builder().build();
    }

    return switch (inputType) {
      case TEXTAREA -> TextAreaFieldInstance.builder().build();
      case NUMERIC -> {
        // Numeric instances carry both @value and @type per CEDAR's typed-literal
        // contract; the rendered template's per-field sub-schema lists both as
        // required. Thread the field's declared XsdNumericDatatype through to
        // the instance so a freshly built skeleton validates on the first try.
        NumericFieldInstance.NumericFieldInstanceBuilder builder = NumericFieldInstance.builder();
        XsdNumericDatatype datatype = field.valueConstraints()
            .filter(NumericValueConstraints.class::isInstance)
            .map(NumericValueConstraints.class::cast)
            .map(NumericValueConstraints::numberType)
            .orElse(XsdNumericDatatype.DECIMAL);
        builder.withType(datatype);
        yield builder.build();
      }
      case TEMPORAL -> {
        // Same shape as NUMERIC. The rendered sub-schema requires @type, so seed it
        // from the field's declared XsdTemporalDatatype.
        TemporalFieldInstance.TemporalFieldInstanceBuilder builder = TemporalFieldInstance.builder();
        XsdTemporalDatatype datatype = field.valueConstraints()
            .filter(TemporalValueConstraints.class::isInstance)
            .map(TemporalValueConstraints.class::cast)
            .map(TemporalValueConstraints::temporalType)
            .orElse(XsdTemporalDatatype.DATETIME);
        builder.withType(datatype);
        yield builder.build();
      }
      case PHONE_NUMBER -> PhoneNumberFieldInstance.builder().build();
      case EMAIL -> EmailFieldInstance.builder().build();
      case RADIO -> RadioFieldInstance.builder().build();
      case CHECKBOX -> CheckboxFieldInstance.builder().build();
      case LIST -> ListFieldInstance.builder().build();
      case LINK -> LinkFieldInstance.builder().build();
      case ROR -> RorFieldInstance.builder().build();
      case ORCID -> OrcidFieldInstance.builder().build();
      case PFAS -> PfasFieldInstance.builder().build();
      case RRID -> RridFieldInstance.builder().build();
      case PUBMED -> PubMedFieldInstance.builder().build();
      case NIH_GRANT_ID -> NihGrantIdFieldInstance.builder().build();
      case DOI -> DoiFieldInstance.builder().build();
      case TEXTFIELD -> throw new IllegalStateException("handled above");
      case ATTRIBUTE_VALUE -> throw new IllegalArgumentException(
          "attribute-value fields have no regular instance; caller must skip them");
      case PAGE_BREAK, SECTION_BREAK, RICHTEXT, IMAGE, YOUTUBE -> throw new IllegalArgumentException(
          "static field " + inputType + " has no instance representation");
    };
  }
}
