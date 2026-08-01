package org.metadatacenter.artifacts.model.tools;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** Exhaustive dispatch tests for schema fields materialized into empty instance slots. */
class EmptyFieldInstancesTest {

  @ParameterizedTest(name = "{0} -> {1}")
  @MethodSource("regularFieldMappings")
  void everyRegularFieldGetsTheMatchingValueLessInstance(FieldSchemaArtifact field,
                                                           Class<?> expectedType) {
    FieldInstanceArtifact instance = EmptyFieldInstances.emptyFor(field);

    assertInstanceOf(expectedType, instance);
    assertTrue(instance.jsonLdValue().isEmpty());
    assertTrue(instance.jsonLdId().isEmpty());
  }

  @ParameterizedTest
  @EnumSource(XsdNumericDatatype.class)
  void numericDatatypeIsCarriedIntoTheEmptyTypedLiteral(XsdNumericDatatype datatype) {
    NumericField field = NumericField.builder().withName("number").withNumericType(datatype).build();

    FieldInstanceArtifact instance = EmptyFieldInstances.emptyFor(field);

    assertEquals(java.util.List.of(datatype.toUri()), instance.jsonLdTypes());
    assertTrue(instance.jsonLdValue().isEmpty());
  }

  @ParameterizedTest
  @EnumSource(XsdTemporalDatatype.class)
  void temporalDatatypeIsCarriedIntoTheEmptyTypedLiteral(XsdTemporalDatatype datatype) {
    TemporalField field = temporalField(datatype);

    FieldInstanceArtifact instance = EmptyFieldInstances.emptyFor(field);

    assertEquals(java.util.List.of(datatype.toUri()), instance.jsonLdTypes());
    assertTrue(instance.jsonLdValue().isEmpty());
  }

  @ParameterizedTest(name = "rejects {0}")
  @MethodSource("nonInstanceFields")
  void staticAndAttributeValueFieldsCannotBecomeRegularInstances(FieldSchemaArtifact field) {
    assertThrows(IllegalArgumentException.class, () -> EmptyFieldInstances.emptyFor(field));
  }

  private static Stream<Arguments> regularFieldMappings() {
    return Stream.of(
        Arguments.of(TextField.builder().withName("text").build(), TextFieldInstance.class),
        Arguments.of(ControlledTermField.builder().withName("term").build(), ControlledTermFieldInstance.class),
        Arguments.of(TextAreaField.builder().withName("area").build(), TextAreaFieldInstance.class),
        Arguments.of(NumericField.builder().withName("number")
            .withNumericType(XsdNumericDatatype.DECIMAL).build(), NumericFieldInstance.class),
        Arguments.of(temporalField(XsdTemporalDatatype.DATETIME), TemporalFieldInstance.class),
        Arguments.of(PhoneNumberField.builder().withName("phone").build(), PhoneNumberFieldInstance.class),
        Arguments.of(EmailField.builder().withName("email").build(), EmailFieldInstance.class),
        Arguments.of(RadioField.builder().withName("radio").build(), RadioFieldInstance.class),
        Arguments.of(CheckboxField.builder().withName("checkbox").build(), CheckboxFieldInstance.class),
        Arguments.of(ListField.builder().withName("list").build(), ListFieldInstance.class),
        Arguments.of(LinkField.builder().withName("link").build(), LinkFieldInstance.class),
        Arguments.of(RorField.builder().withName("ror").build(), RorFieldInstance.class),
        Arguments.of(OrcidField.builder().withName("orcid").build(), OrcidFieldInstance.class),
        Arguments.of(PfasField.builder().withName("pfas").build(), PfasFieldInstance.class),
        Arguments.of(RridField.builder().withName("rrid").build(), RridFieldInstance.class),
        Arguments.of(PubMedField.builder().withName("pubmed").build(), PubMedFieldInstance.class),
        Arguments.of(NihGrantIdField.builder().withName("grant").build(), NihGrantIdFieldInstance.class),
        Arguments.of(DoiField.builder().withName("doi").build(), DoiFieldInstance.class));
  }

  private static Stream<FieldSchemaArtifact> nonInstanceFields() {
    return Stream.of(
        AttributeValueField.builder().withName("attribute").build(),
        PageBreakField.builder().withName("page").build(),
        SectionBreakField.builder().withName("section").build(),
        RichTextField.builder().withName("rich text").build(),
        ImageField.builder().withName("image").build(),
        YouTubeField.builder().withName("video").build());
  }

  private static TemporalField temporalField(XsdTemporalDatatype datatype) {
    return TemporalField.builder().withName("when").withTemporalType(datatype)
        .withTemporalGranularity(TemporalGranularity.SECOND)
        .withInputTimeFormat(InputTimeFormat.TWENTY_FOUR_HOUR)
        .withTimeZoneEnabled(false).build();
  }
}
