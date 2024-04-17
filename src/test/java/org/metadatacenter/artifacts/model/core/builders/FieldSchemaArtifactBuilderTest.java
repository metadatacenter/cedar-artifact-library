package org.metadatacenter.artifacts.model.core.builders;

import org.checkerframework.checker.units.qual.N;
import org.junit.Assert;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.AttributeValueField;
import org.metadatacenter.artifacts.model.core.CheckboxField;
import org.metadatacenter.artifacts.model.core.ControlledTermField;
import org.metadatacenter.artifacts.model.core.EmailField;
import org.metadatacenter.artifacts.model.core.ImageField;
import org.metadatacenter.artifacts.model.core.LinkField;
import org.metadatacenter.artifacts.model.core.ListField;
import org.metadatacenter.artifacts.model.core.NumericField;
import org.metadatacenter.artifacts.model.core.PageBreakField;
import org.metadatacenter.artifacts.model.core.PhoneNumberField;
import org.metadatacenter.artifacts.model.core.RadioField;
import org.metadatacenter.artifacts.model.core.RichTextField;
import org.metadatacenter.artifacts.model.core.SectionBreakField;
import org.metadatacenter.artifacts.model.core.TemporalField;
import org.metadatacenter.artifacts.model.core.TextAreaField;
import org.metadatacenter.artifacts.model.core.TextField;
import org.metadatacenter.artifacts.model.core.YouTubeField;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import java.net.URI;

public class FieldSchemaArtifactBuilderTest
{

  @Test public void testCreateTextField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;
    String regex = "*";
    boolean valueRecommendation = false;

    TextField textField = TextField.builder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      withRegex(regex).withValueRecommendation(valueRecommendation).
      build();

    Assert.assertEquals(FieldInputType.TEXTFIELD, textField.fieldUi().inputType());
    Assert.assertEquals(name, textField.name());
    Assert.assertEquals(description, textField.description());
    Assert.assertEquals(minLength, textField.minLength().get());
    Assert.assertEquals(maxLength, textField.maxLength().get());
    Assert.assertEquals(regex, textField.regex().get());
    Assert.assertEquals(valueRecommendation, textField.fieldUi().valueRecommendationEnabled());
  }

  @Test public void testCreateNumericField()
  {
    String name = "Field name";
    String description = "Field description";
    XsdNumericDatatype numericType = XsdNumericDatatype.DOUBLE;
    Number defaultValue = 2.2;
    Number minValue = 0.0;
    Number maxValue = 100.0;
    String unitOfMeasure = "%";
    Integer decimalPlaces = 2;

    NumericField numericField = NumericField.builder().
      withName(name).
      withDescription(description).
      withNumericType(numericType).
      withDefaultValue(defaultValue).
      withMinValue(minValue).
      withMaxValue(maxValue).
      withUnitOfMeasure(unitOfMeasure).
      withDecimalPlaces(decimalPlaces).
      build();

    Assert.assertEquals(FieldInputType.NUMERIC, numericField.fieldUi().inputType());
    Assert.assertEquals(name, numericField.name());
    Assert.assertEquals(description, numericField.description());
    Assert.assertEquals(numericType, numericField.valueConstraints().get().asNumericValueConstraints().numberType());
    Assert.assertEquals(defaultValue, numericField.valueConstraints().get().asNumericValueConstraints().defaultValue().get().value());
    Assert.assertEquals(minValue, numericField.valueConstraints().get().asNumericValueConstraints().minValue().get());
    Assert.assertEquals(maxValue, numericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(maxValue, numericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(unitOfMeasure, numericField.valueConstraints().get().asNumericValueConstraints().unitOfMeasure().get());
    Assert.assertEquals(decimalPlaces, numericField.valueConstraints().get().asNumericValueConstraints().decimalPlace().get());
  }

  @Test public void testCreateTemporalField()
  {
    String name = "Field name";
    String description = "Field description";
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.TIME;
    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timezoneEnabled = false;

    TemporalField temporalField = TemporalField.builder().
      withName(name).
      withDescription(description).
      withTemporalType(temporalType).
      withTemporalGranularity(temporalGranularity).
      withInputTimeFormat(inputTimeFormat).
      withTimeZoneEnabled(timezoneEnabled).
      build();

    Assert.assertEquals(FieldInputType.TEMPORAL, temporalField.fieldUi().inputType());
    Assert.assertEquals(name, temporalField.name());
    Assert.assertEquals(description, temporalField.description());
    Assert.assertEquals(temporalGranularity, temporalField.fieldUi().asTemporalFieldUi().temporalGranularity());
    Assert.assertEquals(inputTimeFormat, temporalField.fieldUi().asTemporalFieldUi().inputTimeFormat());
    Assert.assertEquals(timezoneEnabled, temporalField.fieldUi().asTemporalFieldUi().timezoneEnabled());
    Assert.assertEquals(temporalType, temporalField.valueConstraints().get().asTemporalValueConstraints().temporalType());
  }

  @Test public void testCreateControlledTermField()
  {
    String name = "Field name";
    String description = "Field description";
    URI ontologyUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String ontologyAcronym = "DOID";
    String ontologyName = "Human Disease Ontology";
    URI branchUri = URI.create("http://purl.bioontology.org/ontology/SNOMEDCT/64572001");
    String branchAcronym = "SNOMEDCT";
    String branchName = "Disease";
    String branchSource = "SNOMEDCT";
    Integer branchMaxDepth = 3;
    URI classUri = URI.create("http://purl.bioontology.org/ontology/LNC/LA19711-3");
    String classSource = "LOINC";
    String classLabel= "Human";
    String classPrefLabel = "Homo Spiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;
    URI valueSetUri = URI.create("https://cadsr.nci.nih.gov/metadata/CADSR-VS/77d61de250089d223d7153a4283e738043a15707");
    String valueSetCollection = "CADSR-VS";
    String valueSetName = "Stable Disease";
    Integer valueSetNumTerms = 1;
    URI actionTermUri = URI.create("http://purl.obolibrary.org/obo/NCBITaxon_51291");
    URI actionSourceUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String actionSource = "DOID";
    ValueType actionValueType = ValueType.ONTOLOGY_CLASS;
    Integer actionTo = 0;

    ControlledTermField controlledTermField = ControlledTermField.builder().
      withName(name).
      withDescription(description).
      withOntologyValueConstraint(ontologyUri, ontologyAcronym, ontologyName).
      withBranchValueConstraint(branchUri, branchSource, branchAcronym, branchName, branchMaxDepth).
      withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).
      withValueSetValueConstraint(valueSetUri, valueSetCollection, valueSetName, valueSetNumTerms).
      withValueConstraintsAction(actionTermUri, actionSource, actionValueType, ValueConstraintsActionType.DELETE,
      actionSourceUri, actionTo).
      build();

    Assert.assertEquals(FieldInputType.TEXTFIELD, controlledTermField.fieldUi().inputType());
    Assert.assertEquals(name, controlledTermField.name());
    Assert.assertEquals(description, controlledTermField.description());
    Assert.assertEquals(ontologyUri,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).uri());
    Assert.assertEquals(ontologyAcronym,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).acronym());
    Assert.assertEquals(ontologyName,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).name());
    Assert.assertEquals(branchUri,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).uri());
    Assert.assertEquals(branchAcronym,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).acronym());
    Assert.assertEquals(branchName,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).name());
    Assert.assertEquals(branchSource,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).source());
    Assert.assertEquals(branchMaxDepth,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).maxDepth());
    Assert.assertEquals(classUri,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).uri());
    Assert.assertEquals(classSource,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).source());
    Assert.assertEquals(classLabel,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).label());
    Assert.assertEquals(classPrefLabel,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).prefLabel());
    Assert.assertEquals(classValueType,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).type());
    Assert.assertEquals(valueSetUri,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).uri());
    Assert.assertEquals(valueSetCollection,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).vsCollection());
    Assert.assertEquals(valueSetName,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).name());
    Assert.assertEquals(valueSetNumTerms,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).numTerms().get());
    Assert.assertEquals(actionTermUri,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).termUri());
    Assert.assertEquals(actionSourceUri,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).sourceUri().get());
    Assert.assertEquals(actionSource,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).source());
    Assert.assertEquals(actionValueType,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).type());
    Assert.assertEquals(actionTo,
      controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).to().get());
  }

  @Test public void testCreateRadioField()
  {
    String name = "Field name";
    String description = "Field description";

    RadioField radioField = RadioField.builder().
      withName(name).
      withDescription(description).
      withOption("Choice 1").
      withOption("Choice 2").
      withOption("Choice 3", true).
      build();

    Assert.assertEquals(FieldInputType.RADIO, radioField.fieldUi().inputType());
    Assert.assertEquals(name, radioField.name());
    Assert.assertEquals(description, radioField.description());
    Assert.assertEquals("Choice 1", radioField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false, radioField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2", radioField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false, radioField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3", radioField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true, radioField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test public void testCreateListField()
  {
    String name = "Field name";
    String description = "Field description";

    ListField listField = ListField.builder().
      withName(name).
      withDescription(description).
      withOption("Choice 1").
      withOption("Choice 2").
      withOption("Choice 3", true).
      build();

    Assert.assertEquals(FieldInputType.LIST, listField.fieldUi().inputType());
    Assert.assertEquals(name, listField.name());
    Assert.assertEquals(description, listField.description());
    Assert.assertEquals("Choice 1", listField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false, listField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2", listField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false, listField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3", listField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true, listField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test public void testCreateCheckboxField()
  {
    String name = "Field name";
    String description = "Field description";

    CheckboxField checkboxField = CheckboxField.builder().
      withName(name).
      withDescription(description).
      withOption("Choice 1", false).
      withOption("Choice 2", false).
      withOption("Choice 3", true).
      build();

    Assert.assertEquals(FieldInputType.CHECKBOX, checkboxField.fieldUi().inputType());
    Assert.assertEquals(name, checkboxField.name());
    Assert.assertEquals(description, checkboxField.description());
    Assert.assertEquals("Choice 1", checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false, checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2", checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false, checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3", checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true, checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test public void testCreatePhoneNumberField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    PhoneNumberField phoneNumberField = PhoneNumberField.builder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      build();

    Assert.assertEquals(FieldInputType.PHONE_NUMBER, phoneNumberField.fieldUi().inputType());
    Assert.assertEquals(name, phoneNumberField.name());
    Assert.assertEquals(description, phoneNumberField.description());
    Assert.assertEquals(minLength, phoneNumberField.minLength().get());
    Assert.assertEquals(maxLength, phoneNumberField.maxLength().get());
  }

  @Test public void testCreateEmailField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    EmailField emailField = EmailField.builder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      build();

    Assert.assertEquals(FieldInputType.EMAIL, emailField.fieldUi().inputType());
    Assert.assertEquals(name, emailField.name());
    Assert.assertEquals(description, emailField.description());
    Assert.assertEquals(minLength, emailField.minLength().get());
    Assert.assertEquals(maxLength, emailField.maxLength().get());
  }

  @Test public void testCreateLinkField()
  {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://example.com/Study");
    String defaultLabel = "Study";

    LinkField linkField = LinkField.builder().
      withName(name).
      withDescription(description).
      withDefaultValue(defaultURI).
      build();

    Assert.assertEquals(FieldInputType.LINK, linkField.fieldUi().inputType());
    Assert.assertEquals(name, linkField.name());
    Assert.assertEquals(description, linkField.description());
    Assert.assertEquals(defaultURI, linkField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test public void testCreateTextAreaField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    TextAreaField textAreaField = TextAreaField.builder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      build();

    Assert.assertEquals(FieldInputType.TEXTAREA, textAreaField.fieldUi().inputType());
    Assert.assertEquals(name, textAreaField.name());
    Assert.assertEquals(description, textAreaField.description());
    Assert.assertEquals(minLength, textAreaField.minLength().get());
    Assert.assertEquals(maxLength, textAreaField.maxLength().get());
  }

  @Test public void testCreateAttributeValueField()
  {
    String name = "Field name";
    String description = "Field description";

    AttributeValueField attributeValueField = AttributeValueField.builder().
      withName(name).
      withDescription(description).
      build();

    Assert.assertEquals(FieldInputType.ATTRIBUTE_VALUE, attributeValueField.fieldUi().inputType());
    Assert.assertEquals(name, attributeValueField.name());
    Assert.assertEquals(description, attributeValueField.description());
  }

  @Test public void testCreatePageBreakField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    PageBreakField pageBreakField = PageBreakField.builder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.PAGE_BREAK, pageBreakField.fieldUi().inputType());
    Assert.assertEquals(name, pageBreakField.name());
    Assert.assertEquals(description, pageBreakField.description());
    Assert.assertEquals(content, pageBreakField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test public void testCreateSectionBreakField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    SectionBreakField sectionBreakField = SectionBreakField.builder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.SECTION_BREAK, sectionBreakField.fieldUi().inputType());
    Assert.assertEquals(name, sectionBreakField.name());
    Assert.assertEquals(description, sectionBreakField.description());
    Assert.assertEquals(content, sectionBreakField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test public void testCreateImageField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    ImageField imageField = ImageField.builder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.IMAGE, imageField.fieldUi().inputType());
    Assert.assertEquals(name, imageField.name());
    Assert.assertEquals(description, imageField.description());
    Assert.assertEquals(content, imageField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test public void testCreateYouTubeField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    YouTubeField youTubeField = YouTubeField.builder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.YOUTUBE, youTubeField.fieldUi().inputType());
    Assert.assertEquals(name, youTubeField.name());
    Assert.assertEquals(description, youTubeField.description());
    Assert.assertEquals(content, youTubeField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test public void testCreateRichTextField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    RichTextField richTextField = RichTextField.builder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.RICHTEXT, richTextField.fieldUi().inputType());
    Assert.assertEquals(name, richTextField.name());
    Assert.assertEquals(description, richTextField.description());
    Assert.assertEquals(content, richTextField.fieldUi().asStaticFieldUi()._content().get());
  }

}
