package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ALT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ATTRIBUTE_VALUE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.BRANCH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHECKBOX_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHILDREN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CLASS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONFIGURATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DECIMAL_PLACES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DRAFT_STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.EMAIL_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INSTANCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IS_BASED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LAST_UPDATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LINK_FIELD;
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
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTI_SELECT_LIST_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NUMERIC_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ONTOLOGY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PHONE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PUBLISHED_STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RADIO_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RECOMMENDED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REGEX;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REQUIRED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SELECTED_BY_DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SINGLE_SELECT_LIST_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_IMAGE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_PAGE_BREAK;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_RICH_TEXT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_SECTION_BREAK;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_YOUTUBE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STRING;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPORAL_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_AREA_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.UNIT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET_COLLECTION;

public class YamlArtifactRenderer implements ArtifactRenderer<Map<String, Object>>
{
  private final boolean isCompact;

  public YamlArtifactRenderer(boolean isCompact)
  {
    this.isCompact = isCompact;
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
   *   - type: text-field
   *     name: Study Name
   *     description: Study name field
   *     datatype: xsd:string
   *     configuration:
   *       required: true
   *
   *   - type: text-field
   *     name: Study ID
   *     description: Study ID field
   *     datatype: xsd:string
   *     minLength: 2
   *     configuration:
   *       required: true
   *
   *   - type: element
   *     name: Address
   *     description: Address element
   *     configuration:
   *       isMultiple: true
   *       minItems: 0
   *       maxItems: 4
   *
   *     children:
   *       - type: text-field
   *         name: field: Address 1
   *         datatype: xsd:string
   *       - type: text-field
   *         name: field: ZIP
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

    if (templateSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, getChildSchemasRendering(templateSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  /**
   * Generate YAML rendering of an element schema artifact
   * <p>
   * e.g.,
   * <pre>
   *   - type: element
   *     name: Address
   *     description: Address element
   *     configuration:
   *       isMultiple: true
   *       minItems: 0
   *       maxItems: 4
   *
   *     children:
   *       - type: text-field
   *         name: field: Address 1
   *         datatype: xsd:string
   *       - type: text-field
   *         name: field: ZIP
   *         datatype: xsd:string
   *         minLength: 5
   *         maxLength: 5
   *
   * </pre>
   */
  public LinkedHashMap<String, Object> renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderSchemaArtifact(elementSchemaArtifact, ELEMENT);

    rendering.put(CONFIGURATION, renderElementConfiguration(elementSchemaArtifact));

    if (elementSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, getChildSchemasRendering(elementSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  /**
   * Generate YAML rendering of a field schema artifact
   * <p>
   * e.g.,
   * <pre>
   * type: controlled-term-field
   * name: Disease
   * values:
   *       - type: ontology
   *         ontologyName: Human Disease Ontology
   *         acronym: DOID
   *         iri: "https://data.bioontology.org/ontologies/DOID"
   *       - type: branch
   *         ontologyName: Disease
   *         acronym: DPCO
   *         iri: "http://purl.org/twc/dpo/ont/Disease"
   *       - type: class
   *         ontologyName: Translated Title
   *         acronym: DATACITE-VOCAB
   *         iri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   * </pre>
   */
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering
      = renderSchemaArtifact(fieldSchemaArtifact, generateFieldTypeName(fieldSchemaArtifact));

    if (fieldSchemaArtifact.skosPrefLabel().isPresent())
      rendering.put(LABEL, fieldSchemaArtifact.skosPrefLabel().get());

    if (!fieldSchemaArtifact.skosAlternateLabels().isEmpty()) {
      List<Object> skosAlternateLabelRendering = new ArrayList<>(fieldSchemaArtifact.skosAlternateLabels());
      rendering.put(ALT_LABEL, skosAlternateLabelRendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderCoreValueConstraints(valueConstraints, rendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderValueConstraintValues(valueConstraints, rendering);
    }

    rendering.put(CONFIGURATION, renderFieldConfiguration(fieldSchemaArtifact));

    return rendering;
  }

  public LinkedHashMap<String, Object> renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(INSTANCE, templateInstanceArtifact.name());

    if (templateInstanceArtifact.description().isPresent())
      rendering.put(DESCRIPTION, templateInstanceArtifact.description());

    if (!isCompact && templateInstanceArtifact.jsonLdId().isPresent())
      rendering.put(ID, templateInstanceArtifact.jsonLdId().get().toString());

    rendering.put(IS_BASED_ON, templateInstanceArtifact.isBasedOn().toString());

    if (!isCompact && templateInstanceArtifact.createdBy().isPresent())
      rendering.put(CREATED_BY, templateInstanceArtifact.createdBy().get().toString());

    if (!isCompact && templateInstanceArtifact.modifiedBy().isPresent())
      rendering.put(MODIFIED_BY, templateInstanceArtifact.modifiedBy().get().toString());

    if (!isCompact && templateInstanceArtifact.createdOn().isPresent())
      rendering.put(CREATED_ON, templateInstanceArtifact.createdOn().get().toString());

    if (templateInstanceArtifact.lastUpdatedOn().isPresent())
      rendering.put(LAST_UPDATED_ON, templateInstanceArtifact.lastUpdatedOn().get().toString());

    // TODO Need to generate YAML for children of template instance

    return rendering;
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
        ontologyValueConstraintRendering.put(IRI, ontologyValueConstraint.uri());
        valuesRendering.add(ontologyValueConstraintRendering);
      }

      for (ValueSetValueConstraint valueSetValueConstraint : controlledTermValueConstraints.valueSets()) {
        LinkedHashMap<String, Object> valueSetValueConstraintRendering = new LinkedHashMap<>();
        valueSetValueConstraintRendering.put(VALUE_SET, valueSetValueConstraint.name());
        valueSetValueConstraintRendering.put(VALUE_SET_COLLECTION, valueSetValueConstraint.vsCollection());
        valueSetValueConstraintRendering.put(IRI, valueSetValueConstraint.uri());
        valuesRendering.add(valueSetValueConstraintRendering);
      }

      for (ClassValueConstraint classValueConstraint : controlledTermValueConstraints.classes()) {
        LinkedHashMap<String, Object> classValueConstraintRendering = new LinkedHashMap<>();
        classValueConstraintRendering.put(CLASS, classValueConstraint.label());
        classValueConstraintRendering.put(IRI, classValueConstraint.uri());
        classValueConstraintRendering.put(LABEL, classValueConstraint.prefLabel());
        classValueConstraintRendering.put(TYPE, classValueConstraint.type());
        classValueConstraintRendering.put(SOURCE, classValueConstraint.source());
        valuesRendering.add(classValueConstraintRendering);
      }

      for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches()) {
        LinkedHashMap<String, Object> branchValueConstraintRendering = new LinkedHashMap<>();
        branchValueConstraintRendering.put(BRANCH, branchValueConstraint.name());
        branchValueConstraintRendering.put(IRI, branchValueConstraint.uri());
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

  // TODO Clean this up
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
      rendering.put(DATATYPE, IRI);
    else
      rendering.put(DATATYPE, STRING);

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

      if (textValueConstraints.regex().isPresent())
        rendering.put(REGEX, textValueConstraints.regex().get());

      if (textValueConstraints.minLength().isPresent())
        rendering.put(MIN_LENGTH, textValueConstraints.minLength().get());

      if (textValueConstraints.maxLength().isPresent())
        rendering.put(MAX_LENGTH, textValueConstraints.maxLength().get());
    }

    // TODO Generate YAML for _valueConstraints.actions
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

  private LinkedHashMap<String, Object> renderElementConfiguration(ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    if (elementSchemaArtifact.isMultiple())
      rendering.put(MULTIPLE, true);

    if (elementSchemaArtifact.minItems().isPresent())
      rendering.put(MIN_ITEMS, elementSchemaArtifact.minItems().get());

    if (elementSchemaArtifact.maxItems().isPresent())
      rendering.put(MAX_ITEMS, elementSchemaArtifact.maxItems().get());

    if (elementSchemaArtifact.propertyUri().isPresent())
      rendering.put(PROPERTY_IRI, elementSchemaArtifact.propertyUri().get().toString());

    // TODO Generate YAML for UI.propertyLabels, propertyDescriptions
    // TODO Generate YAML for childPropertyUris

    return rendering;
  }

  private LinkedHashMap<String, Object> renderFieldConfiguration(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      if (fieldSchemaArtifact.valueConstraints().get().requiredValue())
        rendering.put(REQUIRED, true);

      if (fieldSchemaArtifact.valueConstraints().get().recommendedValue())
        rendering.put(RECOMMENDED, true);
    }

    if (fieldSchemaArtifact.fieldUi().hidden())
      rendering.put(HIDDEN, fieldSchemaArtifact.propertyUri().get().toString());

    if (fieldSchemaArtifact.isMultiple())
      rendering.put(MULTIPLE, true);

    if (fieldSchemaArtifact.minItems().isPresent())
      rendering.put(MIN_ITEMS, fieldSchemaArtifact.minItems().get());

    if (fieldSchemaArtifact.maxItems().isPresent())
      rendering.put(MAX_ITEMS, fieldSchemaArtifact.maxItems().get());

    if (fieldSchemaArtifact.propertyUri().isPresent())
      rendering.put(PROPERTY_IRI, fieldSchemaArtifact.propertyUri().get().toString());

    // TODO valueRecommendation
    // TODO propertyLabels
    // TODO propertyDescriptions

    return rendering;
  }

  private LinkedHashMap<String, Object> renderSchemaArtifact(SchemaArtifact schemaArtifact, String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(TYPE, artifactTypeName);

    rendering.put(NAME, schemaArtifact.name());

    if (!schemaArtifact.description().isEmpty())
      rendering.put(DESCRIPTION, schemaArtifact.description());

    if (schemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, schemaArtifact.identifier().get());

    if (!isCompact && schemaArtifact.jsonLdId().isPresent())
      rendering.put(ID, schemaArtifact.jsonLdId().get());

    if (!isCompact && schemaArtifact.status().isPresent())
      rendering.put(STATUS, generateStatusName(schemaArtifact.status().get()));

    if (!isCompact)
      rendering.put(MODEL_VERSION, schemaArtifact.modelVersion().toString());

    if (!isCompact && schemaArtifact.previousVersion().isPresent())
      rendering.put(PREVIOUS_VERSION, schemaArtifact.previousVersion().get().toString());

    if (!isCompact && schemaArtifact.derivedFrom().isPresent())
      rendering.put(DERIVED_FROM, schemaArtifact.derivedFrom().get().toString());

    if (!isCompact) {
      if (schemaArtifact.createdBy().isPresent())
        rendering.put(CREATED_BY, schemaArtifact.createdBy().get().toString());
      if (schemaArtifact.createdOn().isPresent())
        rendering.put(CREATED_ON, schemaArtifact.createdOn().get().toString());
      if (schemaArtifact.lastUpdatedOn().isPresent())
        rendering.put(LAST_UPDATED_ON, schemaArtifact.lastUpdatedOn().get().toString());
      if (schemaArtifact.modifiedBy().isPresent())
        rendering.put(MODIFIED_BY, schemaArtifact.lastUpdatedOn().get().toString());
    }

    // TODO Generate YAML for annotations

    return rendering;
  }


  private String generateStatusName(Status status)
  {
    // TODO Use typesafe switch when available
    switch (status) {
    case DRAFT:
      return DRAFT_STATUS;
    case PUBLISHED:
      return PUBLISHED_STATUS;
    default:
      throw new RuntimeException("Unknown statua " + status);
    }
  }

  private String generateFieldTypeName(FieldSchemaArtifact fieldSchemaArtifact)
  {
    // TODO Use typesafe switch when available
    switch (fieldSchemaArtifact.fieldUi().inputType()) {
    case TEXTFIELD:
      return TEXT_FIELD;
    case TEXTAREA:
      return TEXT_AREA_FIELD;
    case PHONE_NUMBER:
      return PHONE_FIELD;
    case EMAIL:
      return EMAIL_FIELD;
    case RADIO:
      return RADIO_FIELD;
    case CHECKBOX:
      return CHECKBOX_FIELD;
    case LIST:
      if (fieldSchemaArtifact.valueConstraints().isPresent() && fieldSchemaArtifact.valueConstraints().get()
        .multipleChoice())
        return MULTI_SELECT_LIST_FIELD;
      else
        return SINGLE_SELECT_LIST_FIELD;
    case LINK:
      return LINK_FIELD;
    case NUMERIC:
      return NUMERIC_FIELD;
    case TEMPORAL:
      return TEMPORAL_FIELD;
    case ATTRIBUTE_VALUE:
      return ATTRIBUTE_VALUE_FIELD;
    case PAGE_BREAK:
      return STATIC_PAGE_BREAK;
    case SECTION_BREAK:
      return STATIC_SECTION_BREAK;
    case RICHTEXT:
      return STATIC_RICH_TEXT;
    case IMAGE:
      return STATIC_IMAGE;
    case YOUTUBE:
      return STATIC_YOUTUBE_FIELD;
    default:
      throw new RuntimeException("Unknown field input type " + fieldSchemaArtifact.fieldUi().inputType() + " for field "
        + fieldSchemaArtifact.name());
    }
  }
}

