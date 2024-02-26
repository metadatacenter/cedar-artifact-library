package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.fields.constraints.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SKOS_ALT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.BRANCH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHILDREN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CLASS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INSTANCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DECIMAL_PLACES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.GRANULARITY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IS_BASED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LAST_UPDATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LITERAL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_DEPTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_LENGTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_LENGTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODEL_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTIPLE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTIPLE_CHOICE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ONTOLOGY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REQUIRED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SELECTED_BY_DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SKOS_PREF_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TIME_FORMAT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TIME_ZONE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.UNIT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.URI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_RECOMMENDATION_ENABLED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET_COLLECTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.XSD_ANYURI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.XSD_STRING;

public class YamlArtifactRenderer implements ArtifactRenderer<Map<String, Object>>
{
  private final boolean isExpanded;

  public YamlArtifactRenderer(boolean isExpanded)
  {
    this.isExpanded = isExpanded;
  }

  /**
   * Generate YAML rendering of a template schema artifact
   * <p>
   * e.g.,
   * <pre>
   * template: Study
   * description: Study template
   * identifier: SFY343
   * version: 1.0.0
   * status: published
   *
   * children:
   *
   *   - field: Study Name
   *     description: Study name field
   *     inputType: textfield
   *     datatype: xsd:string
   *     required: true
   *
   *   - field: Study ID
   *     description: Study ID field
   *     inputType: textfield
   *     datatype: xsd:string
   *     required: true
   *     minLength: 2
   *
   *   - element: Address
   *     description: Address element
   *     isMultiple: true
   *     minItems: 0
   *     maxItems: 4
   *
   *     children:
   *       - field: Address 1
   *         inputType: textfield
   *         datatype: xsd:string
   *       - field: ZIP
   *         inputType: textfield
   *         datatype: xsd:string
   *         minLength: 5
   *         maxLength: 5
   * </pre>
   */
  public LinkedHashMap<String, Object> renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderSchemaArtifact(templateSchemaArtifact, TEMPLATE);

    // TODO Generate YAML for header/footer
    // TODO Generate YAML for UI.propertyLabels, UI.propertyDescriptions
    // TODO Generate YAML for childPropertyUris
    // TODO Generate YAML for annotations
    // TODO Generate YAML for provenance

    if (templateSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, getChildSchemasRendering(templateSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  /**
   * Generate YAML rendering of an element schema artifact
   * <p>
   * e.g.,
   * <pre>
   * element: Address
   * description: Address element
   * isMultiple: true
   * minItems: 0
   * maxItems: 4
   *
   * children:
   *   - field: Address 1
   *     inputType: textfield
   *     datatype: xsd:string
   *
   *   - field: ZIP
   *     inputType: textfield
   *     datatype: xsd:string
   *     minLength: 5
   *     maxLength: 5
   *
   * </pre>
   */
  public LinkedHashMap<String, Object> renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderChildSchemaArtifact(elementSchemaArtifact, ELEMENT);

    // TODO Generate YAML for UI.propertyLabels, propertyDescriptions
    // TODO Generate YAML for childPropertyUris

    if (elementSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, getChildSchemasRendering(elementSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  /**
   * Generate YAML rendering of a field schema artifact
   * <p>
   * e.g.,
   * <pre>
   * field: Disease
   * inputType: textfield
   * datatype: xsd:anyURI
   * values:
   *       - ontology: Human Disease Ontology
   *         acronym: DOID
   *         termUri: "https://data.bioontology.org/ontologies/DOID"
   *       - branch: Disease
   *         acronym: DPCO
   *         termUri: "http://purl.org/twc/dpo/ont/Disease"
   *       - class: Translated Title
   *         source: DATACITE-VOCAB
   *         termUri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   *         type: OntologyClass
   * </pre>
   */
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderChildSchemaArtifact(fieldSchemaArtifact, FIELD);

    if (fieldSchemaArtifact.skosPrefLabel().isPresent())
      rendering.put(SKOS_PREF_LABEL, fieldSchemaArtifact.skosPrefLabel().get());

    rendering.put(INPUT_TYPE, fieldSchemaArtifact.fieldUi().inputType());

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderCoreValueConstraints(valueConstraints, rendering);
    }

    if (!fieldSchemaArtifact.skosAlternateLabels().isEmpty()) {
      List<Object> skosAlternateLabelRendering = new ArrayList<>(fieldSchemaArtifact.skosAlternateLabels());
      rendering.put(SKOS_ALT_LABEL, skosAlternateLabelRendering);
    }

    renderFieldUi(fieldSchemaArtifact.fieldUi(), rendering);

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderValueConstraintValues(valueConstraints, rendering);
    }

    // TODO Generate YAML for _valueConstraints.actions

    return rendering;
  }

  private void renderFieldUi(FieldUi fieldUi, LinkedHashMap<String, Object> rendering)
  {
    if (fieldUi.hidden())
      rendering.put(HIDDEN, true);

    if (isExpanded && fieldUi.valueRecommendationEnabled())
      rendering.put(VALUE_RECOMMENDATION_ENABLED, true);

    if (fieldUi.isStatic())
      rendering.put(CONTENT, fieldUi.asStaticFieldUi()._content());
    else if (fieldUi.isTemporal()) {

      rendering.put(TIME_ZONE, fieldUi.asTemporalFieldUi().timezoneEnabled());

      rendering.put(GRANULARITY, fieldUi.asTemporalFieldUi().temporalGranularity());

      rendering.put(TIME_FORMAT, fieldUi.asTemporalFieldUi().inputTimeFormat());
    }
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints specification
   * <p>
   * e.g.,
   * <pre>
   * values:
   *       - ontology: Human Disease Ontology
   *         acronym: DOID
   *         termUri: "https://data.bioontology.org/ontologies/DOID"
   *       - branch: Disease
   *         acronym: DPCO
   *         termUri: "http://purl.org/twc/dpo/ont/Disease"
   *       - class: Translated Title
   *         source: DATACITE-VOCAB
   *         termUri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   *         type: OntologyClass
   * </pre>
   */
  private void renderValueConstraintValues(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> valuesRendering = new ArrayList<>();

    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      for (OntologyValueConstraint ontologyValueConstraint : controlledTermValueConstraints.ontologies()) {
        LinkedHashMap<String, Object> ontologyValueConstraintRendering = new LinkedHashMap<>();
        ontologyValueConstraintRendering.put(ONTOLOGY, ontologyValueConstraint.name());
        ontologyValueConstraintRendering.put(ACRONYM, ontologyValueConstraint.acronym());
        ontologyValueConstraintRendering.put(URI, ontologyValueConstraint.uri());
        valuesRendering.add(ontologyValueConstraintRendering);
      }

      for (ValueSetValueConstraint valueSetValueConstraint : controlledTermValueConstraints.valueSets()) {
        LinkedHashMap<String, Object> valueSetValueConstraintRendering = new LinkedHashMap<>();
        valueSetValueConstraintRendering.put(VALUE_SET, valueSetValueConstraint.name());
        valueSetValueConstraintRendering.put(VALUE_SET_COLLECTION, valueSetValueConstraint.vsCollection());
        valueSetValueConstraintRendering.put(URI, valueSetValueConstraint.uri());
        valuesRendering.add(valueSetValueConstraintRendering);
      }

      for (ClassValueConstraint classValueConstraint : controlledTermValueConstraints.classes()) {
        LinkedHashMap<String, Object> classValueConstraintRendering = new LinkedHashMap<>();
        classValueConstraintRendering.put(CLASS, classValueConstraint.label());
        classValueConstraintRendering.put(URI, classValueConstraint.uri());
        classValueConstraintRendering.put(SKOS_PREF_LABEL, classValueConstraint.prefLabel());
        classValueConstraintRendering.put(INPUT_TYPE, classValueConstraint.type());
        classValueConstraintRendering.put(SOURCE, classValueConstraint.source());
        valuesRendering.add(classValueConstraintRendering);
      }

      for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches()) {
        LinkedHashMap<String, Object> branchValueConstraintRendering = new LinkedHashMap<>();
        branchValueConstraintRendering.put(BRANCH, branchValueConstraint.name());
        branchValueConstraintRendering.put(URI, branchValueConstraint.uri());
        branchValueConstraintRendering.put(ACRONYM, branchValueConstraint.acronym());
        branchValueConstraintRendering.put(SOURCE, branchValueConstraint.source());
        branchValueConstraintRendering.put(MAX_DEPTH, branchValueConstraint.maxDepth());
        valuesRendering.add(branchValueConstraintRendering);
      }

    } else if (valueConstraints instanceof TextValueConstraints) {
      TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints;

      for (LiteralValueConstraint literalValueConstraint : textValueConstraints.literals()) {
        LinkedHashMap<String, Object> literalValueConstraintRendering = new LinkedHashMap<>();
        literalValueConstraintRendering.put(LITERAL, literalValueConstraint.label());
        if (literalValueConstraint.selectedByDefault())
          literalValueConstraintRendering.put(SELECTED_BY_DEFAULT, true);

        valuesRendering.add(literalValueConstraintRendering);
      }
    }

    if (!valuesRendering.isEmpty())
      rendering.put(VALUES, valuesRendering);
  }

  private void renderCoreValueConstraints(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    // TODO Use typesafe switch when available
    if (valueConstraints instanceof NumericValueConstraints) {
      NumericValueConstraints numericValueConstraints = (NumericValueConstraints)valueConstraints;
      rendering.put(DATATYPE, numericValueConstraints.numberType());
    } else if (valueConstraints instanceof TemporalValueConstraints) {
      TemporalValueConstraints temporalValueConstraints = (TemporalValueConstraints)valueConstraints;
      rendering.put(DATATYPE, temporalValueConstraints.temporalType());
    } else if (valueConstraints instanceof ControlledTermValueConstraints)
      rendering.put(DATATYPE, XSD_ANYURI);
    else
      rendering.put(DATATYPE, XSD_STRING);

    if (valueConstraints.requiredValue())
      rendering.put(REQUIRED, true);

    if (valueConstraints.multipleChoice())
      rendering.put(MULTIPLE_CHOICE, true);

    if (valueConstraints.defaultValue().isPresent()) {
      DefaultValue defaultValue = valueConstraints.defaultValue().get();
      if (defaultValue.isTextDefaultValue()) {
        TextDefaultValue textDefaultValue = defaultValue.asTextDefaultValue();
        if (!textDefaultValue.value().isEmpty())
          rendering.put(DEFAULT, textDefaultValue.value());
      } else if (defaultValue.isNumericDefaultValue()) {
        NumericDefaultValue numericDefaultValue = defaultValue.asNumericDefaultValue();
        rendering.put(DEFAULT, numericDefaultValue.value());
      } else if (defaultValue.isControlledTermDefaultValue()) {
        ControlledTermDefaultValue controlledTermDefaultValue = defaultValue.asControlledTermDefaultValue();
        rendering.put(DEFAULT, controlledTermDefaultValue.value().getLeft());
      }
    }

    // TODO Use typesafe switch when available
    if (valueConstraints instanceof NumericValueConstraints) {
      NumericValueConstraints numericValueConstraints = (NumericValueConstraints)valueConstraints;

      if (numericValueConstraints.minValue().isPresent())
        rendering.put(MIN_VALUE, numericValueConstraints.minValue().get());

      if (numericValueConstraints.maxValue().isPresent())
        rendering.put(MAX_VALUE, numericValueConstraints.maxValue().get());

      if (numericValueConstraints.decimalPlace().isPresent())
        rendering.put(DECIMAL_PLACES, numericValueConstraints.decimalPlace().get());

      if (numericValueConstraints.unitOfMeasure().isPresent())
        rendering.put(UNIT, numericValueConstraints.unitOfMeasure().get());
    }

    if (valueConstraints instanceof TextValueConstraints) {
      TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints;

      if (textValueConstraints.minLength().isPresent())
        rendering.put(MIN_LENGTH, textValueConstraints.minLength().get());

      if (textValueConstraints.maxLength().isPresent())
        rendering.put(MAX_LENGTH, textValueConstraints.maxLength().get());
    }
  }

  public LinkedHashMap<String, Object> renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(INSTANCE, templateInstanceArtifact.name());
    if (templateInstanceArtifact.description().isPresent())
      rendering.put(DESCRIPTION, templateInstanceArtifact.description());

    if (isExpanded && templateInstanceArtifact.jsonLdId().isPresent())
      rendering.put(ID, templateInstanceArtifact.jsonLdId().get().toString());

    rendering.put(IS_BASED_ON, templateInstanceArtifact.isBasedOn().toString());

    if (isExpanded && templateInstanceArtifact.createdBy().isPresent())
      rendering.put(CREATED_BY, templateInstanceArtifact.createdBy().get().toString());

    if (isExpanded && templateInstanceArtifact.modifiedBy().isPresent())
      rendering.put(MODIFIED_BY, templateInstanceArtifact.modifiedBy().get().toString());

    if (isExpanded && templateInstanceArtifact.createdOn().isPresent())
      rendering.put(CREATED_ON, templateInstanceArtifact.createdOn().get().toString());

    if (templateInstanceArtifact.lastUpdatedOn().isPresent())
      rendering.put(LAST_UPDATED_ON, templateInstanceArtifact.lastUpdatedOn().get().toString());

    return rendering;
  }

  private List<LinkedHashMap<String, Object>> getChildSchemasRendering(List<ChildSchemaArtifact> childSchemaArtifacts) {
    List<LinkedHashMap<String, Object>> childSchemasRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    for (ChildSchemaArtifact childSchemaArtifact : childSchemaArtifacts) {
      if (childSchemaArtifact instanceof FieldSchemaArtifact) {
        FieldSchemaArtifact fieldSchemaArtifact = (FieldSchemaArtifact)childSchemaArtifact;
        LinkedHashMap<String, Object> fieldSchemaRendering = renderFieldSchemaArtifact(fieldSchemaArtifact);
        childSchemasRendering.add(fieldSchemaRendering);
      } else if (childSchemaArtifact instanceof ElementSchemaArtifact) {
        ElementSchemaArtifact elementSchemaArtifact = (ElementSchemaArtifact)childSchemaArtifact;
        LinkedHashMap<String, Object> elementSchemaRendering = renderElementSchemaArtifact(elementSchemaArtifact);
        childSchemasRendering.add(elementSchemaRendering);
      }
    }

    return childSchemasRendering;
  }

  private LinkedHashMap<String, Object> renderChildSchemaArtifact(ChildSchemaArtifact childSchemaArtifact, String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = renderSchemaArtifact(childSchemaArtifact, artifactTypeName);

    if (childSchemaArtifact.isMultiple())
      rendering.put(MULTIPLE, true);

    if (childSchemaArtifact.minItems().isPresent())
      rendering.put(MIN_ITEMS, childSchemaArtifact.minItems().get());

    if (childSchemaArtifact.maxItems().isPresent())
      rendering.put(MAX_ITEMS, childSchemaArtifact.maxItems().get());

    return rendering;
  }

  private LinkedHashMap<String, Object> renderSchemaArtifact(SchemaArtifact schemaArtifact, String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(artifactTypeName, schemaArtifact.name());

    if (!schemaArtifact.description().isEmpty())
      rendering.put(DESCRIPTION, schemaArtifact.description());

    if (isExpanded && schemaArtifact.jsonLdId().isPresent())
      rendering.put(ID, schemaArtifact.jsonLdId().get());

    if (isExpanded && schemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, schemaArtifact.identifier().get());

    if (isExpanded && schemaArtifact.version().isPresent())
      rendering.put(VERSION, schemaArtifact.version().get().toString());

    if (isExpanded && schemaArtifact.status().isPresent())
      rendering.put(STATUS, schemaArtifact.status().get().toString());

    if (isExpanded)
      rendering.put(MODEL_VERSION, schemaArtifact.modelVersion().toString());

    if (isExpanded && schemaArtifact.previousVersion().isPresent())
      rendering.put(PREVIOUS_VERSION, schemaArtifact.previousVersion().get().toString());

    if (isExpanded && schemaArtifact.derivedFrom().isPresent())
      rendering.put(DERIVED_FROM, schemaArtifact.derivedFrom().get().toString());

    return rendering;
  }
}

