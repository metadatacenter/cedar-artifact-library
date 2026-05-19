package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;


import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Group G — cross-cutting copy-builder behavior. The YouTube positional-argument bug
 * (PR #37: outer `language` clobbered `preferredLabel` in the YOUTUBE arm of
 * `FieldSchemaArtifact.create`) was a copy/round-trip regression. These tests guard
 * against the same kind of slot mix-up across every concrete field type, plus the
 * annotations + value-constraints round-trip, and the nested-children paths on
 * template / element schemas.
 */
public class FieldSchemaArtifactCopyTest
{
  private static FieldSchemaArtifact[] everyFieldVariant()
  {
    return new FieldSchemaArtifact[] {
      TextField.builder().withName("t").withPreferredLabel("LABEL").build(),
      TextAreaField.builder().withName("ta").withPreferredLabel("LABEL").build(),
      NumericField.builder().withName("n").withNumericType(XsdNumericDatatype.INTEGER)
        .withPreferredLabel("LABEL").build(),
      TemporalField.builder().withName("date").withTemporalType(XsdTemporalDatatype.DATE)
        .withTemporalGranularity(TemporalGranularity.DAY).withPreferredLabel("LABEL").build(),
      ControlledTermField.builder().withName("ct").withPreferredLabel("LABEL").build(),
      LinkField.builder().withName("link").withPreferredLabel("LABEL").build(),
      RadioField.builder().withName("radio").withPreferredLabel("LABEL").build(),
      CheckboxField.builder().withName("cbx").withPreferredLabel("LABEL").build(),
      ListField.builder().withName("list").withPreferredLabel("LABEL").build(),
      EmailField.builder().withName("email").withPreferredLabel("LABEL").build(),
      PhoneNumberField.builder().withName("phone").withPreferredLabel("LABEL").build(),
      RorField.builder().withName("ror").withPreferredLabel("LABEL").build(),
      OrcidField.builder().withName("orcid").withPreferredLabel("LABEL").build(),
      PfasField.builder().withName("pfas").withPreferredLabel("LABEL").build(),
      RridField.builder().withName("rrid").withPreferredLabel("LABEL").build(),
      DoiField.builder().withName("doi").withPreferredLabel("LABEL").build(),
      PubMedField.builder().withName("pmid").withPreferredLabel("LABEL").build(),
      NihGrantIdField.builder().withName("nih").withPreferredLabel("LABEL").build()
    };
  }

  @Test public void testCopyPreservesPreferredLabel()
  {
    // Regression for PR #37: copy via FieldSchemaArtifact.create must preserve preferredLabel
    // across every field arm of the switch expression.
    for (FieldSchemaArtifact original : everyFieldVariant()) {
      FieldSchemaArtifact copy = FieldSchemaArtifact.create(
        original.internalName(), original.internalDescription(),
        original.jsonLdContext(), original.jsonLdTypes(), original.jsonLdId(),
        original.name(), original.description(), original.identifier(),
        original.version(), original.status(), original.previousVersion(),
        original.derivedFrom(), original.isMultiple(), original.minItems(),
        original.maxItems(), original.propertyUri(), original.createdBy(),
        original.modifiedBy(), original.createdOn(), original.lastUpdatedOn(),
        original.preferredLabel(), original.alternateLabels(), original.language(),
        original.fieldUi(), original.valueConstraints(), original.annotations());

      assertEquals(original.preferredLabel(), copy.preferredLabel(),
        "preferredLabel lost when re-creating " + original.getClass().getSimpleName());
    }
  }

  @Test public void testCopyPreservesAnnotations()
  {
    Annotations annotations = Annotations.builder().withLiteralAnnotation("k", "v").build();
    TextField original = TextField.builder().withName("X").withAnnotations(annotations).build();

    TextField copy = new TextField.TextFieldBuilder(original).build();

    assertEquals(annotations, copy.annotations().get());
  }

  @Test public void testCopyPreservesValueConstraints()
  {
    TextField original = TextField.builder().withName("X").withMinLength(2).withMaxLength(8).build();

    TextField copy = new TextField.TextFieldBuilder(original).build();

    TextValueConstraints vc = (TextValueConstraints) copy.valueConstraints().get();
    assertEquals(2, vc.minLength().get());
    assertEquals(8, vc.maxLength().get());
  }

  @Test public void testTemplateCopyPreservesNestedChildren()
  {
    TextField inner = TextField.builder().withName("inner").build();
    TemplateSchemaArtifact original = TemplateSchemaArtifact.builder()
      .withName("Outer").withFieldSchema(inner).build();

    TemplateSchemaArtifact copy = TemplateSchemaArtifact.builder(original).build();

    assertEquals(original.fieldSchemas().keySet(), copy.fieldSchemas().keySet());
    assertEquals(original.fieldSchemas().get("inner").name(), copy.fieldSchemas().get("inner").name());
  }

  @Test public void testElementCopyPreservesNestedField()
  {
    TextField inner = TextField.builder().withName("inner").build();
    ElementSchemaArtifact original = ElementSchemaArtifact.builder()
      .withName("Outer").withFieldSchema(inner).build();

    ElementSchemaArtifact copy = ElementSchemaArtifact.builder(original).build();

    assertEquals(original.fieldSchemas().keySet(), copy.fieldSchemas().keySet());
  }
}
