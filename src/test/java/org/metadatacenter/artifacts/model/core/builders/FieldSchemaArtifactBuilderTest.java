package org.metadatacenter.artifacts.model.core.builders;

import org.junit.Assert;
import org.junit.Test;
import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;

import java.net.URI;

public class FieldSchemaArtifactBuilderTest {

  @Test
  public void testCreateTextFieldWithBuilder() {
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
        withRegex(regex).withValueRecommendationEnabled(valueRecommendation).
        build();

    Assert.assertEquals(FieldInputType.TEXTFIELD, textField.fieldUi().inputType());
    Assert.assertEquals(name, textField.name());
    Assert.assertEquals(description, textField.description());
    Assert.assertEquals(minLength, textField.minLength().get());
    Assert.assertEquals(maxLength, textField.maxLength().get());
    Assert.assertEquals(regex, textField.regex().get());
    Assert.assertEquals(valueRecommendation, textField.fieldUi().valueRecommendationEnabled());
  }

  @Test
  public void testCreateTextFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;
    String regex = "*";
    boolean valueRecommendation = false;

    TextField initialTextField = TextField.builder().
        withName(name).
        withDescription(description).
        withMinLength(minLength).
        withMaxLength(maxLength).
        withRegex(regex).withValueRecommendationEnabled(valueRecommendation).
        build();

    TextField clonedTextField = new TextField.TextFieldBuilder(initialTextField).build();

    Assert.assertEquals(FieldInputType.TEXTFIELD, clonedTextField.fieldUi().inputType());
    Assert.assertEquals(name, clonedTextField.name());
    Assert.assertEquals(description, clonedTextField.description());
    Assert.assertEquals(minLength, clonedTextField.minLength().get());
    Assert.assertEquals(maxLength, clonedTextField.maxLength().get());
    Assert.assertEquals(regex, clonedTextField.regex().get());
    Assert.assertEquals(valueRecommendation, clonedTextField.fieldUi().valueRecommendationEnabled());
  }

  @Test
  public void testCreateNumericFieldWithBuilder() {
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
    Assert.assertEquals(defaultValue,
        numericField.valueConstraints().get().asNumericValueConstraints().defaultValue().get().value());
    Assert.assertEquals(minValue, numericField.valueConstraints().get().asNumericValueConstraints().minValue().get());
    Assert.assertEquals(maxValue, numericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(maxValue, numericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(unitOfMeasure,
        numericField.valueConstraints().get().asNumericValueConstraints().unitOfMeasure().get());
    Assert.assertEquals(decimalPlaces,
        numericField.valueConstraints().get().asNumericValueConstraints().decimalPlace().get());
  }

  @Test
  public void testCreateNumericFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    XsdNumericDatatype numericType = XsdNumericDatatype.DOUBLE;
    Number defaultValue = 2.2;
    Number minValue = 0.0;
    Number maxValue = 100.0;
    String unitOfMeasure = "%";
    Integer decimalPlaces = 2;

    NumericField initialNumericField = NumericField.builder().
        withName(name).
        withDescription(description).
        withNumericType(numericType).
        withDefaultValue(defaultValue).
        withMinValue(minValue).
        withMaxValue(maxValue).
        withUnitOfMeasure(unitOfMeasure).
        withDecimalPlaces(decimalPlaces).
        build();

    NumericField clonedNumericField = new NumericField.NumericFieldBuilder(initialNumericField).build();

    Assert.assertEquals(FieldInputType.NUMERIC, clonedNumericField.fieldUi().inputType());
    Assert.assertEquals(name, clonedNumericField.name());
    Assert.assertEquals(description, clonedNumericField.description());
    Assert.assertEquals(numericType,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().numberType());
    Assert.assertEquals(defaultValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().defaultValue().get().value());
    Assert.assertEquals(minValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().minValue().get());
    Assert.assertEquals(maxValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(maxValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assert.assertEquals(unitOfMeasure,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().unitOfMeasure().get());
    Assert.assertEquals(decimalPlaces,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().decimalPlace().get());
  }

  @Test
  public void testCreateTemporalFieldWithBuilder() {
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
    Assert.assertEquals(inputTimeFormat, temporalField.fieldUi().asTemporalFieldUi().inputTimeFormat().get());
    Assert.assertEquals(timezoneEnabled, temporalField.fieldUi().asTemporalFieldUi().timezoneEnabled().get());
    Assert.assertEquals(temporalType,
        temporalField.valueConstraints().get().asTemporalValueConstraints().temporalType());
  }

  @Test
  public void testCreateTemporalFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    XsdTemporalDatatype temporalType = XsdTemporalDatatype.TIME;
    TemporalGranularity temporalGranularity = TemporalGranularity.SECOND;
    InputTimeFormat inputTimeFormat = InputTimeFormat.TWENTY_FOUR_HOUR;
    boolean timezoneEnabled = false;

    TemporalField initialTemporalField = TemporalField.builder().
        withName(name).
        withDescription(description).
        withTemporalType(temporalType).
        withTemporalGranularity(temporalGranularity).
        withInputTimeFormat(inputTimeFormat).
        withTimeZoneEnabled(timezoneEnabled).
        build();

    TemporalField clonedTemporalField = new TemporalField.TemporalFieldBuilder(initialTemporalField).build();

    Assert.assertEquals(FieldInputType.TEMPORAL, clonedTemporalField.fieldUi().inputType());
    Assert.assertEquals(name, clonedTemporalField.name());
    Assert.assertEquals(description, clonedTemporalField.description());
    Assert.assertEquals(temporalGranularity, clonedTemporalField.fieldUi().asTemporalFieldUi().temporalGranularity());
    Assert.assertEquals(inputTimeFormat, clonedTemporalField.fieldUi().asTemporalFieldUi().inputTimeFormat().get());
    Assert.assertEquals(timezoneEnabled, clonedTemporalField.fieldUi().asTemporalFieldUi().timezoneEnabled().get());
    Assert.assertEquals(temporalType,
        clonedTemporalField.valueConstraints().get().asTemporalValueConstraints().temporalType());
  }

  @Test
  public void testCreateControlledTermFieldWithBuilder() {
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
    String classLabel = "Human";
    String classPrefLabel = "Homo Spiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;
    URI valueSetUri = URI.create("https://cadsr.nci.nih" +
        ".gov/metadata/CADSR-VS/77d61de250089d223d7153a4283e738043a15707");
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

  @Test
  public void testCreateControlledTermFieldWithClonedBuilder() {
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
    String classLabel = "Human";
    String classPrefLabel = "Homo Spiens";
    ValueType classValueType = ValueType.ONTOLOGY_CLASS;
    URI valueSetUri = URI.create("https://cadsr.nci.nih" +
        ".gov/metadata/CADSR-VS/77d61de250089d223d7153a4283e738043a15707");
    String valueSetCollection = "CADSR-VS";
    String valueSetName = "Stable Disease";
    Integer valueSetNumTerms = 1;
    URI actionTermUri = URI.create("http://purl.obolibrary.org/obo/NCBITaxon_51291");
    URI actionSourceUri = URI.create("https://data.bioontology.org/ontologies/DOID");
    String actionSource = "DOID";
    ValueType actionValueType = ValueType.ONTOLOGY_CLASS;
    Integer actionTo = 0;

    ControlledTermField initialControlledTermField = ControlledTermField.builder().
        withName(name).
        withDescription(description).
        withOntologyValueConstraint(ontologyUri, ontologyAcronym, ontologyName).
        withBranchValueConstraint(branchUri, branchSource, branchAcronym, branchName, branchMaxDepth).
        withClassValueConstraint(classUri, classSource, classLabel, classPrefLabel, classValueType).
        withValueSetValueConstraint(valueSetUri, valueSetCollection, valueSetName, valueSetNumTerms).
        withValueConstraintsAction(actionTermUri, actionSource, actionValueType, ValueConstraintsActionType.DELETE,
            actionSourceUri, actionTo).
        build();

    ControlledTermField clonedControlledTermField =
        new ControlledTermField.ControlledTermFieldBuilder(initialControlledTermField).build();

    Assert.assertEquals(FieldInputType.TEXTFIELD, clonedControlledTermField.fieldUi().inputType());
    Assert.assertEquals(name, clonedControlledTermField.name());
    Assert.assertEquals(description, clonedControlledTermField.description());
    Assert.assertEquals(ontologyUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).uri());
    Assert.assertEquals(ontologyAcronym,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).acronym());
    Assert.assertEquals(ontologyName,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).name());
    Assert.assertEquals(branchUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).uri());
    Assert.assertEquals(branchAcronym,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).acronym());
    Assert.assertEquals(branchName,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).name());
    Assert.assertEquals(branchSource,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).source());
    Assert.assertEquals(branchMaxDepth,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).maxDepth());
    Assert.assertEquals(classUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).uri());
    Assert.assertEquals(classSource,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).source());
    Assert.assertEquals(classLabel,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).label());
    Assert.assertEquals(classPrefLabel,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).prefLabel());
    Assert.assertEquals(classValueType,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).type());
    Assert.assertEquals(valueSetUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).uri());
    Assert.assertEquals(valueSetCollection,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).vsCollection());
    Assert.assertEquals(valueSetName,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).name());
    Assert.assertEquals(valueSetNumTerms,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).numTerms().get());
    Assert.assertEquals(actionTermUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).termUri());
    Assert.assertEquals(actionSourceUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).sourceUri().get());
    Assert.assertEquals(actionSource,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).source());
    Assert.assertEquals(actionValueType,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).type());
    Assert.assertEquals(actionTo,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).to().get());
  }

  @Test
  public void testCreateRadioFieldWithBuilder() {
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
    Assert.assertEquals("Choice 1",
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false,
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2",
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false,
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3",
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true,
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test
  public void testCreateRadioFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";

    RadioField initialRadioField = RadioField.builder().
        withName(name).
        withDescription(description).
        withOption("Choice 1").
        withOption("Choice 2").
        withOption("Choice 3", true).
        build();

    RadioField clonedRadioField = new RadioField.RadioFieldBuilder(initialRadioField).build();

    Assert.assertEquals(FieldInputType.RADIO, clonedRadioField.fieldUi().inputType());
    Assert.assertEquals(name, clonedRadioField.name());
    Assert.assertEquals(description, clonedRadioField.description());
    Assert.assertEquals("Choice 1",
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false,
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2",
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false,
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3",
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true,
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test
  public void testCreateListFieldWithBuilder() {
    String name = "Field name";
    String description = "Field description";

    ListField listField = ListField.builder().
        withName(name).
        withDescription(description).
        withOption("Choice 1").
        withOption("Choice 2").
        withOption("Choice 3", true).
        withIsMultiple(true).
        build();

    Assert.assertEquals(FieldInputType.LIST, listField.fieldUi().inputType());
    Assert.assertEquals(name, listField.name());
    Assert.assertEquals(description, listField.description());
    Assert.assertEquals("Choice 1",
        listField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false,
        listField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2",
        listField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false,
        listField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3",
        listField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true,
        listField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
    Assert.assertEquals(true, listField.isMultiple());
  }

  @Test
  public void testCreateListFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";

    ListField initialListField = ListField.builder().
        withName(name).
        withDescription(description).
        withOption("Choice 1").
        withOption("Choice 2").
        withOption("Choice 3", true).
        build();

    ListField clonedListField = new ListField.ListFieldBuilder(initialListField).build();

    Assert.assertEquals(FieldInputType.LIST, clonedListField.fieldUi().inputType());
    Assert.assertEquals(name, clonedListField.name());
    Assert.assertEquals(description, clonedListField.description());
    Assert.assertEquals("Choice 1",
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false,
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2",
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false,
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3",
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true,
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test
  public void testCreateCheckboxFieldWithBuilder() {
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
    Assert.assertEquals("Choice 1",
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false,
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2",
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false,
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3",
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true,
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test
  public void testCreateCheckboxFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";

    CheckboxField initialCheckboxField = CheckboxField.builder().
        withName(name).
        withDescription(description).
        withOption("Choice 1", false).
        withOption("Choice 2", false).
        withOption("Choice 3", true).
        build();

    CheckboxField clonedCheckboxField = new CheckboxField.CheckboxFieldBuilder(initialCheckboxField).build();

    Assert.assertEquals(FieldInputType.CHECKBOX, clonedCheckboxField.fieldUi().inputType());
    Assert.assertEquals(name, clonedCheckboxField.name());
    Assert.assertEquals(description, clonedCheckboxField.description());
    Assert.assertEquals("Choice 1",
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assert.assertEquals(false,
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assert.assertEquals("Choice 2",
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assert.assertEquals(false,
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assert.assertEquals("Choice 3",
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assert.assertEquals(true,
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
  }

  @Test
  public void testCreatePhoneNumberFieldWithBuilder() {
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

  @Test
  public void testCreatePhoneNumberFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    PhoneNumberField initialPhoneNumberField = PhoneNumberField.builder().
        withName(name).
        withDescription(description).
        withMinLength(minLength).
        withMaxLength(maxLength).
        build();

    PhoneNumberField clonedPhoneNumberField =
        new PhoneNumberField.PhoneNumberFieldBuilder(initialPhoneNumberField).build();

    Assert.assertEquals(FieldInputType.PHONE_NUMBER, clonedPhoneNumberField.fieldUi().inputType());
    Assert.assertEquals(name, clonedPhoneNumberField.name());
    Assert.assertEquals(description, clonedPhoneNumberField.description());
    Assert.assertEquals(minLength, clonedPhoneNumberField.minLength().get());
    Assert.assertEquals(maxLength, clonedPhoneNumberField.maxLength().get());
  }

  @Test
  public void testCreateEmailFieldWithBuilder() {
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

  @Test
  public void testCreateEmailFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    EmailField initialEmailField = EmailField.builder().
        withName(name).
        withDescription(description).
        withMinLength(minLength).
        withMaxLength(maxLength).
        build();

    EmailField clonedEmailField = new EmailField.EmailFieldBuilder(initialEmailField).build();

    Assert.assertEquals(FieldInputType.EMAIL, clonedEmailField.fieldUi().inputType());
    Assert.assertEquals(name, clonedEmailField.name());
    Assert.assertEquals(description, clonedEmailField.description());
    Assert.assertEquals(minLength, clonedEmailField.minLength().get());
    Assert.assertEquals(maxLength, clonedEmailField.maxLength().get());
  }

  @Test
  public void testCreateLinkFieldWithBuilder() {
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
    Assert.assertEquals(defaultURI,
        linkField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreateLinkFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://example.com/Study");

    LinkField initialLinkField = LinkField.builder().
        withName(name).
        withDescription(description).
        withDefaultValue(defaultURI).
        build();

    LinkField clonedLinkField = new LinkField.LinkFieldBuilder(initialLinkField).build();

    Assert.assertEquals(FieldInputType.LINK, clonedLinkField.fieldUi().inputType());
    Assert.assertEquals(name, clonedLinkField.name());
    Assert.assertEquals(description, clonedLinkField.description());
    Assert.assertEquals(defaultURI,
        clonedLinkField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreateRorFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://ror.org/3434");
    String defaultLabel = "Study";

    RorField initialRorField = RorField.builder().
        withName(name).
        withDescription(description).
        withDefaultValue(defaultURI).
        build();

    RorField clonedRorField = new RorField.RorFieldBuilder(initialRorField).build();

    Assert.assertEquals(FieldInputType.ROR, clonedRorField.fieldUi().inputType());
    Assert.assertEquals(name, clonedRorField.name());
    Assert.assertEquals(description, clonedRorField.description());
    Assert.assertEquals(defaultURI,
        clonedRorField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreateOrcidFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://orcid.org/0000-0002-1825-0097");

    OrcidField initialOrcidField = OrcidField.builder().
        withName(name).
        withDescription(description).
        withDefaultValue(defaultURI).
        build();

    OrcidField clonedOrcidField = new OrcidField.OrcidFieldBuilder(initialOrcidField).build();

    Assert.assertEquals(FieldInputType.ORCID, clonedOrcidField.fieldUi().inputType());
    Assert.assertEquals(name, clonedOrcidField.name());
    Assert.assertEquals(description, clonedOrcidField.description());
    Assert.assertEquals(defaultURI,
        clonedOrcidField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreateTextAreaFieldWithBuilder() {
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

  @Test
  public void testCreateTextAreaFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    Integer minLength = 0;
    Integer maxLength = 10;

    TextAreaField initialTextAreaField = TextAreaField.builder().withName(name).withDescription(description)
        .withMinLength(minLength).withMaxLength(maxLength).build();

    TextAreaField clonedTextAreaField = new TextAreaField.TextAreaFieldBuilder(initialTextAreaField).build();

    Assert.assertEquals(FieldInputType.TEXTAREA, clonedTextAreaField.fieldUi().inputType());
    Assert.assertEquals(name, clonedTextAreaField.name());
    Assert.assertEquals(description, clonedTextAreaField.description());
    Assert.assertEquals(minLength, clonedTextAreaField.minLength().get());
    Assert.assertEquals(maxLength, clonedTextAreaField.maxLength().get());
  }

  @Test
  public void testCreateAttributeValueFieldWithBuilder() {
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

  @Test
  public void testCreateAttributeValueFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";

    AttributeValueField initialAttributeValueField = AttributeValueField.builder().withName(name)
        .withDescription(description).build();

    AttributeValueField clonedAttributeValueField = new AttributeValueField.AttributeValueFieldBuilder(
        initialAttributeValueField).build();

    Assert.assertEquals(FieldInputType.ATTRIBUTE_VALUE, clonedAttributeValueField.fieldUi().inputType());
    Assert.assertEquals(name, clonedAttributeValueField.name());
    Assert.assertEquals(description, clonedAttributeValueField.description());
  }

  @Test
  public void testCreatePageBreakFieldWithBuilder() {
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

  @Test
  public void testCreatePageBreakFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    PageBreakField initialPageBreakField = PageBreakField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).build();

    PageBreakField clonedPageBreakField = new PageBreakField.PageBreakFieldBuilder(initialPageBreakField).build();

    Assert.assertEquals(FieldInputType.PAGE_BREAK, clonedPageBreakField.fieldUi().inputType());
    Assert.assertEquals(name, clonedPageBreakField.name());
    Assert.assertEquals(description, clonedPageBreakField.description());
    Assert.assertEquals(preferredLabel, clonedPageBreakField.preferredLabel().get());
    Assert.assertEquals(content, clonedPageBreakField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateSectionBreakFieldWithBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    SectionBreakField sectionBreakField = SectionBreakField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    Assert.assertEquals(FieldInputType.SECTION_BREAK, sectionBreakField.fieldUi().inputType());
    Assert.assertEquals(name, sectionBreakField.name());
    Assert.assertEquals(description, sectionBreakField.description());
    Assert.assertEquals(preferredLabel, sectionBreakField.preferredLabel().get());
    Assert.assertEquals(content, sectionBreakField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateSectionBreakFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    SectionBreakField initialSectionBreakField = SectionBreakField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    SectionBreakField clonedSectionBreakField = new SectionBreakField.SectionBreakFieldBuilder(
        initialSectionBreakField).build();

    Assert.assertEquals(FieldInputType.SECTION_BREAK, clonedSectionBreakField.fieldUi().inputType());
    Assert.assertEquals(name, clonedSectionBreakField.name());
    Assert.assertEquals(description, clonedSectionBreakField.description());
    Assert.assertEquals(preferredLabel, clonedSectionBreakField.preferredLabel().get());
    Assert.assertEquals(content, clonedSectionBreakField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateImageFieldWithBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    ImageField imageField = ImageField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    Assert.assertEquals(FieldInputType.IMAGE, imageField.fieldUi().inputType());
    Assert.assertEquals(name, imageField.name());
    Assert.assertEquals(description, imageField.description());
    Assert.assertEquals(preferredLabel, imageField.preferredLabel().get());
    Assert.assertEquals(content, imageField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateImageFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    ImageField initialImageField = ImageField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    ImageField clonedImageField = new ImageField.ImageFieldBuilder(initialImageField).build();

    Assert.assertEquals(FieldInputType.IMAGE, clonedImageField.fieldUi().inputType());
    Assert.assertEquals(name, clonedImageField.name());
    Assert.assertEquals(description, clonedImageField.description());
    Assert.assertEquals(preferredLabel, clonedImageField.preferredLabel().get());
    Assert.assertEquals(content, clonedImageField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateYouTubeFieldWithBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    YouTubeField youTubeField = YouTubeField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    Assert.assertEquals(FieldInputType.YOUTUBE, youTubeField.fieldUi().inputType());
    Assert.assertEquals(name, youTubeField.name());
    Assert.assertEquals(description, youTubeField.description());
    Assert.assertEquals(preferredLabel, youTubeField.preferredLabel().get());
    Assert.assertEquals(content, youTubeField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateYouTubeFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    YouTubeField initialYouTubeField = YouTubeField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    YouTubeField clonedYouTubeField = new YouTubeField.YouTubeFieldBuilder(initialYouTubeField).build();

    Assert.assertEquals(FieldInputType.YOUTUBE, clonedYouTubeField.fieldUi().inputType());
    Assert.assertEquals(name, clonedYouTubeField.name());
    Assert.assertEquals(description, clonedYouTubeField.description());
    Assert.assertEquals(preferredLabel, clonedYouTubeField.preferredLabel().get());
    Assert.assertEquals(content, clonedYouTubeField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateRichTextFieldWithBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    RichTextField richTextField = RichTextField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    Assert.assertEquals(FieldInputType.RICHTEXT, richTextField.fieldUi().inputType());
    Assert.assertEquals(name, richTextField.name());
    Assert.assertEquals(description, richTextField.description());
    Assert.assertEquals(preferredLabel, richTextField.preferredLabel().get());
    Assert.assertEquals(content, richTextField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateRichTextFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    String preferredLabel = "Field label";
    String content = "Content";

    RichTextField initialRichTextField = RichTextField.builder().
        withName(name).
        withDescription(description).
        withPreferredLabel(preferredLabel).
        withContent(content).
        build();

    RichTextField clonedRichTextField = new RichTextField.RichTextFieldBuilder(initialRichTextField).build();

    Assert.assertEquals(FieldInputType.RICHTEXT, clonedRichTextField.fieldUi().inputType());
    Assert.assertEquals(name, clonedRichTextField.name());
    Assert.assertEquals(description, clonedRichTextField.description());
    Assert.assertEquals(preferredLabel, clonedRichTextField.preferredLabel().get());
    Assert.assertEquals(content, clonedRichTextField.fieldUi().asStaticFieldUi()._content().get());
  }

}
