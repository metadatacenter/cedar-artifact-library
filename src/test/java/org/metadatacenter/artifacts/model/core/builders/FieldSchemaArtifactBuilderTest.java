package org.metadatacenter.artifacts.model.core.builders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

    Assertions.assertEquals(FieldInputType.TEXTFIELD, textField.fieldUi().inputType());
    Assertions.assertEquals(name, textField.name());
    Assertions.assertEquals(description, textField.description());
    Assertions.assertEquals(minLength, textField.minLength().get());
    Assertions.assertEquals(maxLength, textField.maxLength().get());
    Assertions.assertEquals(regex, textField.regex().get());
    Assertions.assertEquals(valueRecommendation, textField.fieldUi().valueRecommendationEnabled());
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

    Assertions.assertEquals(FieldInputType.TEXTFIELD, clonedTextField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedTextField.name());
    Assertions.assertEquals(description, clonedTextField.description());
    Assertions.assertEquals(minLength, clonedTextField.minLength().get());
    Assertions.assertEquals(maxLength, clonedTextField.maxLength().get());
    Assertions.assertEquals(regex, clonedTextField.regex().get());
    Assertions.assertEquals(valueRecommendation, clonedTextField.fieldUi().valueRecommendationEnabled());
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

    Assertions.assertEquals(FieldInputType.NUMERIC, numericField.fieldUi().inputType());
    Assertions.assertEquals(name, numericField.name());
    Assertions.assertEquals(description, numericField.description());
    Assertions.assertEquals(numericType, numericField.valueConstraints().get().asNumericValueConstraints().numberType());
    Assertions.assertEquals(defaultValue,
        numericField.valueConstraints().get().asNumericValueConstraints().defaultValue().get().value());
    Assertions.assertEquals(minValue, numericField.valueConstraints().get().asNumericValueConstraints().minValue().get());
    Assertions.assertEquals(maxValue, numericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assertions.assertEquals(maxValue, numericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assertions.assertEquals(unitOfMeasure,
        numericField.valueConstraints().get().asNumericValueConstraints().unitOfMeasure().get());
    Assertions.assertEquals(decimalPlaces,
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

    Assertions.assertEquals(FieldInputType.NUMERIC, clonedNumericField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedNumericField.name());
    Assertions.assertEquals(description, clonedNumericField.description());
    Assertions.assertEquals(numericType,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().numberType());
    Assertions.assertEquals(defaultValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().defaultValue().get().value());
    Assertions.assertEquals(minValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().minValue().get());
    Assertions.assertEquals(maxValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assertions.assertEquals(maxValue,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().maxValue().get());
    Assertions.assertEquals(unitOfMeasure,
        clonedNumericField.valueConstraints().get().asNumericValueConstraints().unitOfMeasure().get());
    Assertions.assertEquals(decimalPlaces,
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

    Assertions.assertEquals(FieldInputType.TEMPORAL, temporalField.fieldUi().inputType());
    Assertions.assertEquals(name, temporalField.name());
    Assertions.assertEquals(description, temporalField.description());
    Assertions.assertEquals(temporalGranularity, temporalField.fieldUi().asTemporalFieldUi().temporalGranularity());
    Assertions.assertEquals(inputTimeFormat, temporalField.fieldUi().asTemporalFieldUi().inputTimeFormat().get());
    Assertions.assertEquals(timezoneEnabled, temporalField.fieldUi().asTemporalFieldUi().timezoneEnabled().get());
    Assertions.assertEquals(temporalType,
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

    Assertions.assertEquals(FieldInputType.TEMPORAL, clonedTemporalField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedTemporalField.name());
    Assertions.assertEquals(description, clonedTemporalField.description());
    Assertions.assertEquals(temporalGranularity, clonedTemporalField.fieldUi().asTemporalFieldUi().temporalGranularity());
    Assertions.assertEquals(inputTimeFormat, clonedTemporalField.fieldUi().asTemporalFieldUi().inputTimeFormat().get());
    Assertions.assertEquals(timezoneEnabled, clonedTemporalField.fieldUi().asTemporalFieldUi().timezoneEnabled().get());
    Assertions.assertEquals(temporalType,
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

    Assertions.assertEquals(FieldInputType.TEXTFIELD, controlledTermField.fieldUi().inputType());
    Assertions.assertEquals(name, controlledTermField.name());
    Assertions.assertEquals(description, controlledTermField.description());
    Assertions.assertEquals(ontologyUri,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).uri());
    Assertions.assertEquals(ontologyAcronym,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).acronym());
    Assertions.assertEquals(ontologyName,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).name());
    Assertions.assertEquals(branchUri,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).uri());
    Assertions.assertEquals(branchAcronym,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).acronym());
    Assertions.assertEquals(branchName,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).name());
    Assertions.assertEquals(branchSource,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).source());
    Assertions.assertEquals(branchMaxDepth,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).maxDepth());
    Assertions.assertEquals(classUri,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).uri());
    Assertions.assertEquals(classSource,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).source());
    Assertions.assertEquals(classLabel,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).label());
    Assertions.assertEquals(classPrefLabel,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).prefLabel());
    Assertions.assertEquals(classValueType,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).type());
    Assertions.assertEquals(valueSetUri,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).uri());
    Assertions.assertEquals(valueSetCollection,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).vsCollection());
    Assertions.assertEquals(valueSetName,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).name());
    Assertions.assertEquals(valueSetNumTerms,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).numTerms().get());
    Assertions.assertEquals(actionTermUri,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).termUri());
    Assertions.assertEquals(actionSourceUri,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).sourceUri().get());
    Assertions.assertEquals(actionSource,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).source());
    Assertions.assertEquals(actionValueType,
        controlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).type());
    Assertions.assertEquals(actionTo,
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

    Assertions.assertEquals(FieldInputType.TEXTFIELD, clonedControlledTermField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedControlledTermField.name());
    Assertions.assertEquals(description, clonedControlledTermField.description());
    Assertions.assertEquals(ontologyUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).uri());
    Assertions.assertEquals(ontologyAcronym,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).acronym());
    Assertions.assertEquals(ontologyName,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().ontologies().get(0).name());
    Assertions.assertEquals(branchUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).uri());
    Assertions.assertEquals(branchAcronym,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).acronym());
    Assertions.assertEquals(branchName,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).name());
    Assertions.assertEquals(branchSource,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).source());
    Assertions.assertEquals(branchMaxDepth,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().branches().get(0).maxDepth());
    Assertions.assertEquals(classUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).uri());
    Assertions.assertEquals(classSource,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).source());
    Assertions.assertEquals(classLabel,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).label());
    Assertions.assertEquals(classPrefLabel,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).prefLabel());
    Assertions.assertEquals(classValueType,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().classes().get(0).type());
    Assertions.assertEquals(valueSetUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).uri());
    Assertions.assertEquals(valueSetCollection,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).vsCollection());
    Assertions.assertEquals(valueSetName,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).name());
    Assertions.assertEquals(valueSetNumTerms,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().valueSets().get(0).numTerms().get());
    Assertions.assertEquals(actionTermUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).termUri());
    Assertions.assertEquals(actionSourceUri,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).sourceUri().get());
    Assertions.assertEquals(actionSource,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).source());
    Assertions.assertEquals(actionValueType,
        clonedControlledTermField.valueConstraints().get().asControlledTermValueConstraints().actions().get(0).type());
    Assertions.assertEquals(actionTo,
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

    Assertions.assertEquals(FieldInputType.RADIO, radioField.fieldUi().inputType());
    Assertions.assertEquals(name, radioField.name());
    Assertions.assertEquals(description, radioField.description());
    Assertions.assertEquals("Choice 1",
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assertions.assertEquals(false,
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assertions.assertEquals("Choice 2",
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assertions.assertEquals(false,
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assertions.assertEquals("Choice 3",
        radioField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assertions.assertEquals(true,
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

    Assertions.assertEquals(FieldInputType.RADIO, clonedRadioField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedRadioField.name());
    Assertions.assertEquals(description, clonedRadioField.description());
    Assertions.assertEquals("Choice 1",
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assertions.assertEquals(false,
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assertions.assertEquals("Choice 2",
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assertions.assertEquals(false,
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assertions.assertEquals("Choice 3",
        clonedRadioField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assertions.assertEquals(true,
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

    Assertions.assertEquals(FieldInputType.LIST, listField.fieldUi().inputType());
    Assertions.assertEquals(name, listField.name());
    Assertions.assertEquals(description, listField.description());
    Assertions.assertEquals("Choice 1",
        listField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assertions.assertEquals(false,
        listField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assertions.assertEquals("Choice 2",
        listField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assertions.assertEquals(false,
        listField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assertions.assertEquals("Choice 3",
        listField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assertions.assertEquals(true,
        listField.valueConstraints().get().asTextValueConstraints().literals().get(2).selectedByDefault());
    Assertions.assertEquals(true, listField.isMultiple());
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

    Assertions.assertEquals(FieldInputType.LIST, clonedListField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedListField.name());
    Assertions.assertEquals(description, clonedListField.description());
    Assertions.assertEquals("Choice 1",
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assertions.assertEquals(false,
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assertions.assertEquals("Choice 2",
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assertions.assertEquals(false,
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assertions.assertEquals("Choice 3",
        clonedListField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assertions.assertEquals(true,
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

    Assertions.assertEquals(FieldInputType.CHECKBOX, checkboxField.fieldUi().inputType());
    Assertions.assertEquals(name, checkboxField.name());
    Assertions.assertEquals(description, checkboxField.description());
    Assertions.assertEquals("Choice 1",
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assertions.assertEquals(false,
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assertions.assertEquals("Choice 2",
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assertions.assertEquals(false,
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assertions.assertEquals("Choice 3",
        checkboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assertions.assertEquals(true,
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

    Assertions.assertEquals(FieldInputType.CHECKBOX, clonedCheckboxField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedCheckboxField.name());
    Assertions.assertEquals(description, clonedCheckboxField.description());
    Assertions.assertEquals("Choice 1",
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).label());
    Assertions.assertEquals(false,
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(0).selectedByDefault());
    Assertions.assertEquals("Choice 2",
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).label());
    Assertions.assertEquals(false,
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(1).selectedByDefault());
    Assertions.assertEquals("Choice 3",
        clonedCheckboxField.valueConstraints().get().asTextValueConstraints().literals().get(2).label());
    Assertions.assertEquals(true,
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

    Assertions.assertEquals(FieldInputType.PHONE_NUMBER, phoneNumberField.fieldUi().inputType());
    Assertions.assertEquals(name, phoneNumberField.name());
    Assertions.assertEquals(description, phoneNumberField.description());
    Assertions.assertEquals(minLength, phoneNumberField.minLength().get());
    Assertions.assertEquals(maxLength, phoneNumberField.maxLength().get());
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

    Assertions.assertEquals(FieldInputType.PHONE_NUMBER, clonedPhoneNumberField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedPhoneNumberField.name());
    Assertions.assertEquals(description, clonedPhoneNumberField.description());
    Assertions.assertEquals(minLength, clonedPhoneNumberField.minLength().get());
    Assertions.assertEquals(maxLength, clonedPhoneNumberField.maxLength().get());
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

    Assertions.assertEquals(FieldInputType.EMAIL, emailField.fieldUi().inputType());
    Assertions.assertEquals(name, emailField.name());
    Assertions.assertEquals(description, emailField.description());
    Assertions.assertEquals(minLength, emailField.minLength().get());
    Assertions.assertEquals(maxLength, emailField.maxLength().get());
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

    Assertions.assertEquals(FieldInputType.EMAIL, clonedEmailField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedEmailField.name());
    Assertions.assertEquals(description, clonedEmailField.description());
    Assertions.assertEquals(minLength, clonedEmailField.minLength().get());
    Assertions.assertEquals(maxLength, clonedEmailField.maxLength().get());
  }

  @Test
  public void testCreateLinkFieldWithBuilder() {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://example.com/Study");

    LinkField linkField = LinkField.builder().
        withName(name).
        withDescription(description).
        withDefaultValue(defaultURI).
        build();

    Assertions.assertEquals(FieldInputType.LINK, linkField.fieldUi().inputType());
    Assertions.assertEquals(name, linkField.name());
    Assertions.assertEquals(description, linkField.description());
    Assertions.assertEquals(defaultURI,
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

    Assertions.assertEquals(FieldInputType.LINK, clonedLinkField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedLinkField.name());
    Assertions.assertEquals(description, clonedLinkField.description());
    Assertions.assertEquals(defaultURI,
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

    Assertions.assertEquals(FieldInputType.ROR, clonedRorField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedRorField.name());
    Assertions.assertEquals(description, clonedRorField.description());
    Assertions.assertEquals(defaultURI,
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

    Assertions.assertEquals(FieldInputType.ORCID, clonedOrcidField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedOrcidField.name());
    Assertions.assertEquals(description, clonedOrcidField.description());
    Assertions.assertEquals(defaultURI,
        clonedOrcidField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreatePfasFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";
    URI defaultURI = URI.create("https://api-ccte.epa.gov/chemical/detail/search/by-dtxsid/DTXSID7020182");

    PfasField initialPfasField = PfasField.builder().
        withName(name).
        withDescription(description).
        withDefaultValue(defaultURI).
        build();

    PfasField clonedPfasField = new PfasField.PfasFieldBuilder(initialPfasField).build();

    Assertions.assertEquals(FieldInputType.PFAS, clonedPfasField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedPfasField.name());
    Assertions.assertEquals(description, clonedPfasField.description());
    Assertions.assertEquals(defaultURI,
        clonedPfasField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
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

    Assertions.assertEquals(FieldInputType.TEXTAREA, textAreaField.fieldUi().inputType());
    Assertions.assertEquals(name, textAreaField.name());
    Assertions.assertEquals(description, textAreaField.description());
    Assertions.assertEquals(minLength, textAreaField.minLength().get());
    Assertions.assertEquals(maxLength, textAreaField.maxLength().get());
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

    Assertions.assertEquals(FieldInputType.TEXTAREA, clonedTextAreaField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedTextAreaField.name());
    Assertions.assertEquals(description, clonedTextAreaField.description());
    Assertions.assertEquals(minLength, clonedTextAreaField.minLength().get());
    Assertions.assertEquals(maxLength, clonedTextAreaField.maxLength().get());
  }

  @Test
  public void testCreateAttributeValueFieldWithBuilder() {
    String name = "Field name";
    String description = "Field description";

    AttributeValueField attributeValueField = AttributeValueField.builder().
        withName(name).
        withDescription(description).
        build();

    Assertions.assertEquals(FieldInputType.ATTRIBUTE_VALUE, attributeValueField.fieldUi().inputType());
    Assertions.assertEquals(name, attributeValueField.name());
    Assertions.assertEquals(description, attributeValueField.description());
  }

  @Test
  public void testCreateAttributeValueFieldWithCopyBuilder() {
    String name = "Field name";
    String description = "Field description";

    AttributeValueField initialAttributeValueField = AttributeValueField.builder().withName(name)
        .withDescription(description).build();

    AttributeValueField clonedAttributeValueField = new AttributeValueField.AttributeValueFieldBuilder(
        initialAttributeValueField).build();

    Assertions.assertEquals(FieldInputType.ATTRIBUTE_VALUE, clonedAttributeValueField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedAttributeValueField.name());
    Assertions.assertEquals(description, clonedAttributeValueField.description());
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

    Assertions.assertEquals(FieldInputType.PAGE_BREAK, pageBreakField.fieldUi().inputType());
    Assertions.assertEquals(name, pageBreakField.name());
    Assertions.assertEquals(description, pageBreakField.description());
    Assertions.assertEquals(content, pageBreakField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.PAGE_BREAK, clonedPageBreakField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedPageBreakField.name());
    Assertions.assertEquals(description, clonedPageBreakField.description());
    Assertions.assertEquals(preferredLabel, clonedPageBreakField.preferredLabel().get());
    Assertions.assertEquals(content, clonedPageBreakField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.SECTION_BREAK, sectionBreakField.fieldUi().inputType());
    Assertions.assertEquals(name, sectionBreakField.name());
    Assertions.assertEquals(description, sectionBreakField.description());
    Assertions.assertEquals(preferredLabel, sectionBreakField.preferredLabel().get());
    Assertions.assertEquals(content, sectionBreakField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.SECTION_BREAK, clonedSectionBreakField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedSectionBreakField.name());
    Assertions.assertEquals(description, clonedSectionBreakField.description());
    Assertions.assertEquals(preferredLabel, clonedSectionBreakField.preferredLabel().get());
    Assertions.assertEquals(content, clonedSectionBreakField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.IMAGE, imageField.fieldUi().inputType());
    Assertions.assertEquals(name, imageField.name());
    Assertions.assertEquals(description, imageField.description());
    Assertions.assertEquals(preferredLabel, imageField.preferredLabel().get());
    Assertions.assertEquals(content, imageField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.IMAGE, clonedImageField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedImageField.name());
    Assertions.assertEquals(description, clonedImageField.description());
    Assertions.assertEquals(preferredLabel, clonedImageField.preferredLabel().get());
    Assertions.assertEquals(content, clonedImageField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.YOUTUBE, youTubeField.fieldUi().inputType());
    Assertions.assertEquals(name, youTubeField.name());
    Assertions.assertEquals(description, youTubeField.description());
    Assertions.assertEquals(preferredLabel, youTubeField.preferredLabel().get());
    Assertions.assertEquals(content, youTubeField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.YOUTUBE, clonedYouTubeField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedYouTubeField.name());
    Assertions.assertEquals(description, clonedYouTubeField.description());
    Assertions.assertEquals(preferredLabel, clonedYouTubeField.preferredLabel().get());
    Assertions.assertEquals(content, clonedYouTubeField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.RICHTEXT, richTextField.fieldUi().inputType());
    Assertions.assertEquals(name, richTextField.name());
    Assertions.assertEquals(description, richTextField.description());
    Assertions.assertEquals(preferredLabel, richTextField.preferredLabel().get());
    Assertions.assertEquals(content, richTextField.fieldUi().asStaticFieldUi()._content().get());
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

    Assertions.assertEquals(FieldInputType.RICHTEXT, clonedRichTextField.fieldUi().inputType());
    Assertions.assertEquals(name, clonedRichTextField.name());
    Assertions.assertEquals(description, clonedRichTextField.description());
    Assertions.assertEquals(preferredLabel, clonedRichTextField.preferredLabel().get());
    Assertions.assertEquals(content, clonedRichTextField.fieldUi().asStaticFieldUi()._content().get());
  }

  @Test
  public void testCreateDoiField() {
    String name = "DOI";
    String description = "Document DOI";
    URI defaultURI = URI.create("https://doi.org/10.82658/8vc1-abcd");

    DoiField doiField = DoiField.builder().withName(name).withDescription(description)
        .withDefaultValue(defaultURI).build();

    Assertions.assertEquals(FieldInputType.DOI, doiField.fieldUi().inputType());
    Assertions.assertEquals(name, doiField.name());
    Assertions.assertEquals(description, doiField.description());
    Assertions.assertEquals(defaultURI,
        doiField.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreateDoiFieldWithCopyBuilder() {
    URI defaultURI = URI.create("https://doi.org/10.82658/8vc1-abcd");
    DoiField initial = DoiField.builder().withName("DOI").withDescription("desc")
        .withDefaultValue(defaultURI).build();

    DoiField cloned = new DoiField.DoiFieldBuilder(initial).build();

    Assertions.assertEquals(initial, cloned);
  }

  @Test
  public void testCreateNihGrantIdField() {
    String name = "Grant";
    String description = "NIH grant identifier";
    URI defaultURI = URI.create("https://reporter.nih.gov/grant/R01CA000000");

    NihGrantIdField field = NihGrantIdField.builder().withName(name).withDescription(description)
        .withDefaultValue(defaultURI).build();

    Assertions.assertEquals(FieldInputType.NIH_GRANT_ID, field.fieldUi().inputType());
    Assertions.assertEquals(name, field.name());
    Assertions.assertEquals(description, field.description());
    Assertions.assertEquals(defaultURI,
        field.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreateNihGrantIdFieldWithCopyBuilder() {
    URI defaultURI = URI.create("https://reporter.nih.gov/grant/R01CA000000");
    NihGrantIdField initial = NihGrantIdField.builder().withName("Grant").withDescription("desc")
        .withDefaultValue(defaultURI).build();

    NihGrantIdField cloned = new NihGrantIdField.NihGrantIdFieldBuilder(initial).build();

    Assertions.assertEquals(initial, cloned);
  }

  @Test
  public void testCreatePubMedField() {
    String name = "PMID";
    String description = "PubMed identifier";
    URI defaultURI = URI.create("https://pubmed.ncbi.nlm.nih.gov/12345");

    PubMedField field = PubMedField.builder().withName(name).withDescription(description)
        .withDefaultValue(defaultURI).build();

    Assertions.assertEquals(FieldInputType.PUBMED, field.fieldUi().inputType());
    Assertions.assertEquals(name, field.name());
    Assertions.assertEquals(description, field.description());
    Assertions.assertEquals(defaultURI,
        field.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreatePubMedFieldWithCopyBuilder() {
    URI defaultURI = URI.create("https://pubmed.ncbi.nlm.nih.gov/12345");
    PubMedField initial = PubMedField.builder().withName("PMID").withDescription("desc")
        .withDefaultValue(defaultURI).build();

    PubMedField cloned = new PubMedField.PubMedFieldBuilder(initial).build();

    Assertions.assertEquals(initial, cloned);
  }

  @Test
  public void testCreateRridField() {
    String name = "RRID";
    String description = "Research resource identifier";
    URI defaultURI = URI.create("https://scicrunch.org/resolver/RRID:AB_123");

    RridField field = RridField.builder().withName(name).withDescription(description)
        .withDefaultValue(defaultURI).build();

    Assertions.assertEquals(FieldInputType.RRID, field.fieldUi().inputType());
    Assertions.assertEquals(name, field.name());
    Assertions.assertEquals(description, field.description());
    Assertions.assertEquals(defaultURI,
        field.valueConstraints().get().asLinkValueConstraints().defaultValue().get().termUri());
  }

  @Test
  public void testCreateRridFieldWithCopyBuilder() {
    URI defaultURI = URI.create("https://scicrunch.org/resolver/RRID:AB_123");
    RridField initial = RridField.builder().withName("RRID").withDescription("desc")
        .withDefaultValue(defaultURI).build();

    RridField cloned = new RridField.RridFieldBuilder(initial).build();

    Assertions.assertEquals(initial, cloned);
  }

  @Test public void imageFieldBuilderSetsContentAndDimensions()
  {
    ImageField imageField = ImageField.builder()
      .withName("Logo")
      .withContent("https://example.org/logo.png")
      .withWidth(640)
      .withHeight(480)
      .build();

    Assertions.assertEquals("https://example.org/logo.png",
      imageField.fieldUi().asStaticFieldUi()._content().orElseThrow());
    Assertions.assertEquals(640, imageField.fieldUi().asStaticFieldUi().width().orElseThrow());
    Assertions.assertEquals(480, imageField.fieldUi().asStaticFieldUi().height().orElseThrow());
  }

  @Test public void imageFieldCopyBuilderKeepsContentAndDimensions()
  {
    ImageField original = ImageField.builder()
      .withName("Logo")
      .withContent("https://example.org/logo.png")
      .withWidth(640)
      .withHeight(480)
      .build();

    ImageField copied = ImageField.builder(original).build();
    Assertions.assertEquals(original.fieldUi().asStaticFieldUi(), copied.fieldUi().asStaticFieldUi());
  }

  @Test public void youTubeFieldCopyBuilderKeepsDimensions()
  {
    YouTubeField original = YouTubeField.builder()
      .withName("Intro video")
      .withContent("https://youtube.com/watch?v=xyz")
      .withWidth(1280)
      .withHeight(720)
      .build();

    YouTubeField copied = YouTubeField.builder(original).build();
    Assertions.assertEquals(original.fieldUi().asStaticFieldUi(), copied.fieldUi().asStaticFieldUi());
  }

  @Test public void staticDimensionsRenderAsUiSizeAndRoundTrip() throws Exception
  {
    ImageField original = ImageField.builder()
      .withName("Logo")
      .withContent("https://example.org/logo.png")
      .withWidth(640)
      .withHeight(480)
      .build();

    com.fasterxml.jackson.databind.node.ObjectNode rendered =
      new org.metadatacenter.artifacts.model.renderer.JsonArtifactRenderer().renderFieldSchemaArtifact(original);

    // Canonical wire form: _ui._size {width, height}; the flat keys are rejected by the
    // CEDAR meta-schema's additionalProperties: false.
    Assertions.assertEquals(640, rendered.path("_ui").path("_size").path("width").asInt());
    Assertions.assertEquals(480, rendered.path("_ui").path("_size").path("height").asInt());
    Assertions.assertFalse(rendered.path("_ui").has("width"));
    Assertions.assertFalse(rendered.path("_ui").has("height"));

    FieldSchemaArtifact roundTripped =
      new org.metadatacenter.artifacts.model.reader.JsonArtifactReader().readFieldSchemaArtifact(rendered);
    Assertions.assertEquals(original.fieldUi().asStaticFieldUi(), roundTripped.fieldUi().asStaticFieldUi());

    org.metadatacenter.model.validation.report.ValidationReport report =
      new org.metadatacenter.model.validation.CedarValidator().validateTemplateField(rendered);
    Assertions.assertEquals("true", report.getValidationStatus(),
      "a static field with dimensions must pass the canonical validator; got: " + report.getErrors());
  }

  @Test public void controlledTermCloneBuilderAccumulatesConstraints()
  {
    ControlledTermField original = ControlledTermField.builder()
      .withName("Disease")
      .withClassValueConstraint(URI.create("http://purl.obolibrary.org/obo/DOID_4"),
        "DOID", "disease", "disease", ValueType.ONTOLOGY_CLASS)
      .build();

    ControlledTermField extended = ControlledTermField.builder(original)
      .withOntologyValueConstraint(URI.create("https://data.bioontology.org/ontologies/LOINC"),
        "LOINC", "Logical Observation Identifiers")
      .build();

    Assertions.assertEquals(1, extended.valueConstraints().orElseThrow()
      .asControlledTermValueConstraints().classes().size());
    Assertions.assertEquals(1, extended.valueConstraints().orElseThrow()
      .asControlledTermValueConstraints().ontologies().size());
  }

}
