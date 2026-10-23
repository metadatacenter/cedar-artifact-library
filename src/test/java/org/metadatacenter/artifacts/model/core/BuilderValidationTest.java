package org.metadatacenter.artifacts.model.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;

/**
 * Null/invalid-arg validation paths in the field-schema and constraint builders. Pins the
 * visible invariants so future refactors don't silently loosen them.
 */
public class BuilderValidationTest
{
  // ---- FieldSchemaArtifactBuilder null guards (via TextField builder) ----

  @Test public void testBuildRejectsNullName()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withName(null));
    assertTrue(ex.getMessage().contains("name"));
  }

  @Test public void testBuildRejectsNullJsonLdContext()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withJsonLdContext(null));
  }

  @Test public void testBuildRejectsNullJsonLdType()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withJsonLdType(null));
  }

  @Test public void testBuildRejectsNullDescription()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withDescription(null));
    assertTrue(ex.getMessage().contains("description"));
  }

  @Test public void testBuildRejectsNullIdentifier()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withIdentifier(null));
  }

  @Test public void testBuildRejectsNullPreferredLabel()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withPreferredLabel(null));
  }

  @Test public void testBuildRejectsNullAlternateLabels()
  {
    // Empty list is OK; null is not.
    TextField.builder().withName("OK").withAlternateLabels(Collections.emptyList()).build();
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withAlternateLabels(null));
  }

  @Test public void testBuildRejectsNullVersion()
  {
    assertThrows(IllegalArgumentException.class,
      () -> TextField.builder().withVersion(null));
  }

  // ---- A field must have a name, as a template and an element must ----

  static Stream<Arguments> everyFieldKind()
  {
    return Stream.of(
      kind("text", TextField::builder),
      kind("text area", TextAreaField::builder),
      kind("numeric", () -> NumericField.builder().withNumericType(XsdNumericDatatype.DECIMAL)),
      kind("temporal", () -> TemporalField.builder().withTemporalType(XsdTemporalDatatype.DATE)
        .withTemporalGranularity(TemporalGranularity.DAY)),
      kind("radio", RadioField::builder),
      kind("checkbox", CheckboxField::builder),
      kind("list", ListField::builder),
      kind("phone number", PhoneNumberField::builder),
      kind("email", EmailField::builder),
      kind("link", LinkField::builder),
      kind("controlled term", ControlledTermField::builder),
      kind("attribute value", AttributeValueField::builder),
      kind("ROR", RorField::builder),
      kind("ORCID", OrcidField::builder),
      kind("PFAS", PfasField::builder),
      kind("RRID", RridField::builder),
      kind("PubMed", PubMedField::builder),
      kind("NIH grant", NihGrantIdField::builder),
      kind("DOI", DoiField::builder),
      kind("page break", PageBreakField::builder),
      kind("section break", SectionBreakField::builder),
      kind("image", ImageField::builder),
      kind("YouTube", YouTubeField::builder),
      kind("rich text", RichTextField::builder));
  }

  private static Arguments kind(String kind, Supplier<FieldSchemaArtifactBuilder<?>> builder)
  {
    return Arguments.of(kind, builder);
  }

  @ParameterizedTest(name = "{0}") @MethodSource("everyFieldKind")
  public void testBuildRejectsFieldWithNoName(String kind, Supplier<FieldSchemaArtifactBuilder<?>> builder)
  {
    IllegalStateException ex = assertThrows(IllegalStateException.class, () -> builder.get().build());
    assertTrue(ex.getMessage().startsWith("field " + SCHEMA_ORG_NAME + " is null in "), ex.getMessage());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("everyFieldKind")
  public void testBuildRejectsFieldWithEmptyName(String kind, Supplier<FieldSchemaArtifactBuilder<?>> builder)
  {
    IllegalStateException ex = assertThrows(IllegalStateException.class,
      () -> builder.get().withName("").build());
    assertTrue(ex.getMessage().startsWith("field " + SCHEMA_ORG_NAME + " is empty in "), ex.getMessage());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("everyFieldKind")
  public void testBuildAcceptsFieldWithName(String kind, Supplier<FieldSchemaArtifactBuilder<?>> builder)
  {
    FieldSchemaArtifact field = builder.get().withName("Name").build();

    assertEquals("Name", field.name());
    // A copy keeps the name, and cannot be emptied of it.
    assertEquals("Name", FieldSchemaArtifactBuilder.builder(field).build().name());
    assertThrows(IllegalStateException.class, () -> FieldSchemaArtifactBuilder.builder(field).withName("").build());
  }

  // ---- A field builder refuses a name made only of whitespace ----

  static final List<String> WHITESPACE_ONLY_NAMES = List.of(" ", "   ", "\t", "\n", " \t\r\n ");

  static Stream<Arguments> everyFieldKindAndWhitespaceOnlyName()
  {
    return everyFieldKind().flatMap(kind -> WHITESPACE_ONLY_NAMES.stream()
      .map(name -> Arguments.of(kind.get()[0], kind.get()[1], name)));
  }

  @ParameterizedTest(name = "{0} [{index}]") @MethodSource("everyFieldKindAndWhitespaceOnlyName")
  public void testBuilderRejectsFieldWithWhitespaceOnlyName(String kind,
    Supplier<FieldSchemaArtifactBuilder<?>> builder, String name)
  {
    FieldSchemaArtifactBuilder<?> fieldBuilder = builder.get();

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> fieldBuilder.withName(name));
    assertEquals("whitespace-only name passed to builder", ex.getMessage());

    // The refusal leaves the builder unchanged, so the internal name follows the next name given.
    FieldSchemaArtifact field = fieldBuilder.withName("Name").build();
    assertEquals("Name", field.name());
    assertEquals("Name field schema", field.internalName());

    IllegalArgumentException copyEx = assertThrows(IllegalArgumentException.class,
      () -> FieldSchemaArtifactBuilder.builder(field).withName(name));
    assertEquals("whitespace-only name passed to builder", copyEx.getMessage());
  }

  @ParameterizedTest(name = "{0}") @MethodSource("everyFieldKind")
  public void testBuildAcceptsFieldWithNameThatHasWhitespaceAroundIt(String kind,
    Supplier<FieldSchemaArtifactBuilder<?>> builder)
  {
    assertEquals(" Name\t", builder.get().withName(" Name\t").build().name());
  }

  // ---- TextValueConstraints negative paths ----

  @Test public void testTextValueConstraintsBuilderRejectsEmptyDefaultValue()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextValueConstraints.builder().withDefaultValue(""));
    assertTrue(ex.getMessage().contains("empty"));
  }

  @Test public void testTextValueConstraintsBuilderRejectsNullRegex()
  {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
      () -> TextValueConstraints.builder().withRegex(null));
    assertTrue(ex.getMessage().contains("regex"));
  }
}
