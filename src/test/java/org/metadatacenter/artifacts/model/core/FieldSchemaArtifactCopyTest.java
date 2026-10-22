package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Cross-cutting copy-builder behavior across all field types, plus annotations and
 * value-constraints preservation and nested-children copy on template / element schemas.
 *
 * <p>The parametrized {@code testCopyPreservesPreferredLabel} guards against positional-
 * argument slot mix-ups in the {@code FieldSchemaArtifact.create} switch expression — the
 * kind of bug where an outer variable silently fills the wrong slot for a particular field
 * arm and the data round-trips to the wrong field.
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
    // FieldSchemaArtifact.create must preserve preferredLabel across every field arm of the
    // switch expression.
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

  /**
   * One field of every type the sealed hierarchy permits, each carrying the metadata every field
   * shares, so that a copy which drops any of it is not equal to its source.
   */
  private static List<FieldSchemaArtifact> everyFieldTypeWithSharedMetadata()
  {
    return List.of(
      withSharedMetadata(TextField.builder().withName("t")),
      withSharedMetadata(TextAreaField.builder().withName("ta")),
      withSharedMetadata(TemporalField.builder().withName("date").withTemporalType(XsdTemporalDatatype.DATE)
        .withTemporalGranularity(TemporalGranularity.DAY)),
      withSharedMetadata(RadioField.builder().withName("radio").withOption("yes")),
      withSharedMetadata(PhoneNumberField.builder().withName("phone")),
      withSharedMetadata(NumericField.builder().withName("n").withNumericType(XsdNumericDatatype.DECIMAL)),
      withSharedMetadata(ListField.builder().withName("list").withOption("one")),
      withSharedMetadata(EmailField.builder().withName("email")),
      withSharedMetadata(CheckboxField.builder().withName("cbx").withOption("a")),
      withSharedMetadata(AttributeValueField.builder().withName("av")),
      withSharedMetadata(PageBreakField.builder().withName("pb")),
      withSharedMetadata(SectionBreakField.builder().withName("sb").withContent("Section")),
      withSharedMetadata(ImageField.builder().withName("img").withContent("https://example.org/i.png")),
      withSharedMetadata(YouTubeField.builder().withName("yt").withContent("dQw4w9WgXcQ")),
      withSharedMetadata(RichTextField.builder().withName("rt").withContent("<p>Text</p>")),
      withSharedMetadata(ControlledTermField.builder().withName("ct")),
      withSharedMetadata(LinkField.builder().withName("link")),
      withSharedMetadata(RorField.builder().withName("ror")),
      withSharedMetadata(OrcidField.builder().withName("orcid")),
      withSharedMetadata(PfasField.builder().withName("pfas")),
      withSharedMetadata(RridField.builder().withName("rrid")),
      withSharedMetadata(PubMedField.builder().withName("pmid")),
      withSharedMetadata(NihGrantIdField.builder().withName("nih")),
      withSharedMetadata(DoiField.builder().withName("doi")));
  }

  private static <B extends FieldSchemaArtifactBuilder<B>> FieldSchemaArtifact withSharedMetadata(B builder)
  {
    return builder.withJsonLdId(URI.create("https://repo.metadatacenter.org/template-fields/1"))
      .withDescription("A description").withIdentifier("ID-1").withPreferredLabel("Preferred")
      .withAlternateLabels(List.of("Alternate")).withPropertyUri(URI.create("https://example.org/property"))
      .withAnnotations(Annotations.builder().withLiteralAnnotation("source", "manual").build()).build();
  }

  @Test public void testGenericCopyBuilderRebuildsEveryFieldTypeUnchanged()
  {
    // FieldSchemaArtifactBuilder.builder(field) is how a caller holding a field of unknown type gets a
    // builder for it; an arm missing from its dispatch, or one reached by the wrong instanceof, turns
    // the field into another type or refuses it. The fixture list must name every permitted type, so a
    // new type cannot be added without passing through the dispatch here.
    List<FieldSchemaArtifact> fields = everyFieldTypeWithSharedMetadata();
    Set<Class<?>> permitted = Set.of(FieldSchemaArtifact.class.getPermittedSubclasses());
    assertEquals(permitted, permitted.stream().filter(type -> fields.stream().anyMatch(type::isInstance))
      .collect(Collectors.toSet()));

    for (FieldSchemaArtifact original : fields) {
      FieldSchemaArtifact copy = FieldSchemaArtifactBuilder.builder(original).build();

      assertEquals(original.getClass(), copy.getClass());
      assertEquals(original, copy, "copy of " + original.getClass().getSimpleName() + " differs from its source");
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
