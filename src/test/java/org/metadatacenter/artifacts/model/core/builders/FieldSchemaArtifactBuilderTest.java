package org.metadatacenter.artifacts.model.core.builders;

import org.junit.Assert;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.NumericType;
import org.metadatacenter.artifacts.model.core.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.TemporalType;
import org.metadatacenter.artifacts.model.core.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.ValueType;

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
    boolean valueRecommendationEnabled = false;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.textFieldBuilder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      withRegex(regex).
      withValueRecommendationEnabled(valueRecommendationEnabled).
      build();

    Assert.assertEquals(FieldInputType.TEXTFIELD, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(minLength, fieldSchemaArtifact.minLength().get());
    Assert.assertEquals(maxLength, fieldSchemaArtifact.maxLength().get());
    Assert.assertEquals(regex, fieldSchemaArtifact.regex().get());
    Assert.assertEquals(valueRecommendationEnabled, fieldSchemaArtifact.fieldUi().valueRecommendationEnabled());
  }

  @Test public void testCreateNumericField()
  {
    String name = "Field name";
    String description = "Field description";
    NumericType numericType = NumericType.DOUBLE;
    Number defaultValue = 22.3;
    Number minValue = 0.0;
    Number maxValue = 100.0;
    String unitOfMeasure = "%";
    Integer decimalPlaces = 2;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.numericFieldBuilder().
      withName(name).
      withDescription(description).
      withNumericType(numericType).
      withDefaultValue(defaultValue).
      withMinValue(minValue).
      withMaxValue(maxValue).
      withUnitOfMeasure(unitOfMeasure).
      withDecimalPlaces(decimalPlaces).
      build();

    Assert.assertEquals(FieldInputType.NUMERIC, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(numericType, fieldSchemaArtifact.valueConstraints().get().asNumericValueConstraints().numberType());
    Assert.assertEquals(defaultValue, fieldSchemaArtifact.valueConstraints().get().asNumericValueConstraints().defaultValue().get().value());
    Assert.assertEquals(minValue, fieldSchemaArtifact.valueConstraints().get().asNumericValueConstraints().minValue().get());
    Assert.assertEquals(maxValue, fieldSchemaArtifact.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(maxValue, fieldSchemaArtifact.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(unitOfMeasure, fieldSchemaArtifact.valueConstraints().get().asNumericValueConstraints().unitOfMeasure().get());
    Assert.assertEquals(decimalPlaces, fieldSchemaArtifact.valueConstraints().get().asNumericValueConstraints().decimalPlace().get());
  }

  @Test public void testCreateTemporalField()
  {
    String name = "Field name";
    String description = "Field description";
    TemporalType temporalType = TemporalType.TIME;
    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timezoneEnabled = false;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.temporalFieldBuilder().
      withName(name).
      withDescription(description).
      withTemporalType(temporalType).
      withTemporalGranularity(temporalGranularity).
      withInputTimeFormat(inputTimeFormat).
      withTimeZoneEnabled(timezoneEnabled).
      build();

    Assert.assertEquals(FieldInputType.TEMPORAL, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(temporalGranularity, fieldSchemaArtifact.fieldUi().asTemporalFieldUi().temporalGranularity());
    Assert.assertEquals(inputTimeFormat, fieldSchemaArtifact.fieldUi().asTemporalFieldUi().inputTimeFormat());
    Assert.assertEquals(timezoneEnabled, fieldSchemaArtifact.fieldUi().asTemporalFieldUi().timezoneEnabled());
    Assert.assertEquals(temporalType, fieldSchemaArtifact.valueConstraints().get().asTemporalValueConstraints().temporalType());
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

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.controlledTermFieldBuilder().
      withName(name).
      withDescription(description).
      withOntologyValueConstraint(ontologyUri, ontologyAcronym, ontologyName).
      withBranchValueConstraint(branchUri, branchSource, branchAcronym, branchName, branchMaxDepth).
      withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).
      withValueSetValueConstraint(valueSetUri, valueSetCollection, valueSetName, valueSetNumTerms).
      withValueConstraintsAction(actionTermUri, actionSource, actionValueType, ValueConstraintsActionType.DELETE,
      actionSourceUri, actionTo).
      build();

    Assert.assertEquals(FieldInputType.TEXTFIELD, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(ontologyUri,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).uri());
    Assert.assertEquals(ontologyAcronym,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).acronym());
    Assert.assertEquals(ontologyName,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).name());
    Assert.assertEquals(branchUri,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).uri());
    Assert.assertEquals(branchAcronym,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).acronym());
    Assert.assertEquals(branchName,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).name());
    Assert.assertEquals(branchSource,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).source());
    Assert.assertEquals(branchMaxDepth,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).maxDepth());
    Assert.assertEquals(classUri,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).uri());
    Assert.assertEquals(classSource,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).source());
    Assert.assertEquals(classLabel,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).label());
    Assert.assertEquals(classPrefLabel,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).prefLabel());
    Assert.assertEquals(classValueType,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).type());
    Assert.assertEquals(valueSetUri,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).uri());
    Assert.assertEquals(valueSetCollection,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).vsCollection());
    Assert.assertEquals(valueSetName,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).name());
    Assert.assertEquals(valueSetNumTerms,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).numTerms().get());
    Assert.assertEquals(actionTermUri,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).termUri());
    Assert.assertEquals(actionSourceUri,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).sourceUri().get());
    Assert.assertEquals(actionSource,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).source());
    Assert.assertEquals(actionValueType,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).type());
    Assert.assertEquals(actionTo,
      fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).to().get());
  }

  @Test public void testCreateRadioField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.radioFieldBuilder().
      withName(name).
      withDescription(description).
      withOption("Choice 1").
      withOption("Choice 2").
      withOption("Choice 3", true).
      build();

    Assert.assertEquals(FieldInputType.RADIO, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals("Choice 1", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test public void testCreateListField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.listFieldBuilder().
      withName(name).
      withDescription(description).
      withOption("Choice 1").
      withOption("Choice 2").
      withOption("Choice 3", true).
      build();

    Assert.assertEquals(FieldInputType.LIST, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals("Choice 1", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test public void testCreateCheckboxField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.checkboxFieldBuilder().
      withName(name).
      withDescription(description).
      withOption("Choice 1", false).
      withOption("Choice 2", false).
      withOption("Choice 3", true).
      build();

    Assert.assertEquals(FieldInputType.CHECKBOX, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals("Choice 1", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3", fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true, fieldSchemaArtifact.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test public void testCreatePhoneNumberField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.phoneNumberFieldBuilder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      build();

    Assert.assertEquals(FieldInputType.PHONE_NUMBER, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(minLength, fieldSchemaArtifact.minLength().get());
    Assert.assertEquals(maxLength, fieldSchemaArtifact.maxLength().get());
  }

  @Test public void testCreateEmailField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.emailFieldBuilder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      build();

    Assert.assertEquals(FieldInputType.EMAIL, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(minLength, fieldSchemaArtifact.minLength().get());
    Assert.assertEquals(maxLength, fieldSchemaArtifact.maxLength().get());
  }

  @Test public void testCreateLinkField()
  {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://example.com/Study");
    String defaultLabel = "Study";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.linkFieldBuilder().
      withName(name).
      withDescription(description).
      withDefaultValue(defaultURI, defaultLabel).
      build();

    Assert.assertEquals(FieldInputType.LINK, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(defaultURI, fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().defaultValue().get().value().getLeft());
    Assert.assertEquals(defaultLabel, fieldSchemaArtifact.valueConstraints().get().asControlledTermValueConstraints().defaultValue().get().value().getRight());
  }

  @Test public void testCreateTextAreaField()
  {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.textAreaFieldBuilder().
      withName(name).
      withDescription(description).
      withMinLength(minLength).
      withMaxLength(maxLength).
      build();

    Assert.assertEquals(FieldInputType.TEXTAREA, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(minLength, fieldSchemaArtifact.minLength().get());
    Assert.assertEquals(maxLength, fieldSchemaArtifact.maxLength().get());
  }

  @Test public void testCreateAttributeValueField()
  {
    String name = "Field name";
    String description = "Field description";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.attributeValueFieldBuilder().
      withName(name).
      withDescription(description).
      build();

    Assert.assertEquals(FieldInputType.ATTRIBUTE_VALUE, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
  }

  @Test public void testCreateSectionBreakField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.sectionBreakFieldBuilder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.SECTION_BREAK, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(content, fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content());
  }

  @Test public void testCreateImageField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.imageFieldBuilder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.IMAGE, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(content, fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content());
  }

  @Test public void testCreateYouTubeField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.youTubeFieldBuilder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.YOUTUBE, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(content, fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content());
  }

  @Test public void testCreateRichTextField()
  {
    String name = "Field name";
    String description = "Field description";
    String content = "Content";

    FieldSchemaArtifact fieldSchemaArtifact = FieldSchemaArtifact.richTextFieldBuilder().
      withName(name).
      withDescription(description).
      withContent(content).
      build();

    Assert.assertEquals(FieldInputType.RICHTEXT, fieldSchemaArtifact.fieldUi().inputType());
    Assert.assertEquals(name, fieldSchemaArtifact.name());
    Assert.assertEquals(description, fieldSchemaArtifact.description());
    Assert.assertEquals(content, fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content());
  }

}
