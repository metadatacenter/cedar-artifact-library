package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ParentSchemaArtifact;
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
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION_TO;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ALT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ATTRIBUTE_VALUE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.BRANCH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHECKBOX_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHILDREN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CLASS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONFIGURATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTROLLED_TERM_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DECIMAL_PLACES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT_VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DELETE_ACTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DRAFT_STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.EMAIL_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FOOTER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.GRANULARITY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEADER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_FORMAT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_ZONE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INSTANCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IS_BASED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.KEY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LANGUAGE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_ON;
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
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MOVE_ACTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTIPLE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTI_SELECT_LIST_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NUMERIC_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NUM_TERMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ONTOLOGY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ONTOLOGY_NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.OVERRIDE_DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.OVERRIDE_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PHONE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREF_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PUBLISHED_STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RADIO_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RECOMMENDED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REGEX;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REQUIRED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SELECTED_BY_DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SINGLE_SELECT_LIST_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_IMAGE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_PAGE_BREAK;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_RICH_TEXT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_SECTION_BREAK;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_YOUTUBE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPORAL_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_AREA_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.UNIT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_RECOMMENDATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET_NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;

public class YamlArtifactRenderer implements ArtifactRenderer<LinkedHashMap<String, Object>>
{
  private final boolean isCompact;
  private final DateTimeFormatter datetimeFormatter;

  public YamlArtifactRenderer(boolean isCompact)
  {
    this.isCompact = isCompact;
    this.datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
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
   *   - key: study-name
   *     type: text-field
   *     name: Study Name
   *     description: Study name field
   *     configuration:
   *       required: true
   *
   *   - type: text-field
   *     name: Study ID
   *     description: Study ID field
   *     minLength: 2
   *     configuration:
   *       required: true
   *
   *   - key: address
   *     type: element
   *     name: Address
   *     description: Address element
   *     configuration:
   *       isMultiple: true
   *       minItems: 0
   *       maxItems: 4
   *
   *     children:
   *       - key: address-1
   *         type: text-field
   *         name: field: Address 1
   *       - key: zip
   *         type: text-field
   *         name: field: ZIP
   *         minLength: 5
   *         maxLength: 5
   * </pre>
   */
  public LinkedHashMap<String, Object> renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderSchemaArtifactBase(templateSchemaArtifact, TEMPLATE);

    if (templateSchemaArtifact.templateUi().header().isPresent())
      rendering.put(HEADER, templateSchemaArtifact.templateUi().header().get());

    if (templateSchemaArtifact.templateUi().footer().isPresent())
      rendering.put(FOOTER, templateSchemaArtifact.templateUi().footer().get());

    addArtifactProvenanceRendering(templateSchemaArtifact, rendering);

    if (templateSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, renderChildSchemas(templateSchemaArtifact, templateSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  /**
   * Generate YAML rendering of an element schema artifact
   * <p>
   * e.g.,
   * <pre>
   *   - key: address
   *     type: element
   *     name: Address
   *     description: Address element
   *     configuration:
   *       isMultiple: true
   *       minItems: 0
   *       maxItems: 4
   *
   *     children:
   *       - key: address-1
   *         type: text-field
   *         name: field: Address 1
   *       - key: zip
   *         type: text-field
   *         name: field: ZIP
   *         minLength: 5
   *         maxLength: 5
   *
   * </pre>
   */
  public LinkedHashMap<String, Object> renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderSchemaArtifactBase(elementSchemaArtifact, ELEMENT);

    addArtifactProvenanceRendering(elementSchemaArtifact, rendering);

    if (elementSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, renderChildSchemas(elementSchemaArtifact, elementSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  public LinkedHashMap<String, Object> renderElementSchemaArtifact(String elementName, ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderChildSchemaArtifactBase(elementName, elementSchemaArtifact, ELEMENT);

    addArtifactProvenanceRendering(elementSchemaArtifact, rendering);

    if (elementSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, renderChildSchemas(elementSchemaArtifact, elementSchemaArtifact.getChildSchemas()));

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
   *   - branch: Disease
   *     acronym: DPCO
   *     termUri: "http://purl.org/twc/dpo/ont/Disease"
   *   - type: ontology
   *     source: DOID
   *     name: Human Disease Ontology
   *     acronym: DOID
   *     iri: "https://data.bioontology.org/ontologies/DOID"
   *   - class: Translated Title
   *     source: DATACITE-VOCAB
   *     termUri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   *     type: OntologyClass
   * </pre>
   */
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering
      = renderSchemaArtifactBase(fieldSchemaArtifact, renderFieldTypeName(fieldSchemaArtifact));

    if (fieldSchemaArtifact.skosPrefLabel().isPresent())
      rendering.put(PREF_LABEL, fieldSchemaArtifact.skosPrefLabel().get());

    if (!fieldSchemaArtifact.skosAlternateLabels().isEmpty()) {
      List<Object> skosAlternateLabelRendering = new ArrayList<>(fieldSchemaArtifact.skosAlternateLabels());
      rendering.put(ALT_LABEL, skosAlternateLabelRendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderCoreValueConstraints(valueConstraints, fieldSchemaArtifact.fieldUi(), rendering);
      renderValueConstraintValues(valueConstraints, rendering);
      renderValueConstraintActions(valueConstraints, rendering);
    }

    if (fieldSchemaArtifact.fieldUi().isTemporal()) {
      TemporalFieldUi templateUi = fieldSchemaArtifact.fieldUi().asTemporalFieldUi();
      rendering.put(GRANULARITY, templateUi.temporalGranularity());
      if (!templateUi.temporalGranularity().isYear() && !templateUi.temporalGranularity().isMonth() &&
        !templateUi.temporalGranularity().isDay()) {
        rendering.put(INPUT_TIME_FORMAT, templateUi.inputTimeFormat());
        rendering.put(INPUT_TIME_ZONE, templateUi.timezoneEnabled());
      }
    }

    if (fieldSchemaArtifact.fieldUi().valueRecommendationEnabled())
      rendering.put(VALUE_RECOMMENDATION, true);

    addArtifactProvenanceRendering(fieldSchemaArtifact, rendering);

    return rendering;
  }

  // TOOD Cut and paste from above. Merge
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(String fieldName, FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering
      = renderChildSchemaArtifactBase(fieldName, fieldSchemaArtifact, renderFieldTypeName(fieldSchemaArtifact));

    if (fieldSchemaArtifact.skosPrefLabel().isPresent())
      rendering.put(PREF_LABEL, fieldSchemaArtifact.skosPrefLabel().get());

    if (!fieldSchemaArtifact.skosAlternateLabels().isEmpty()) {
      List<Object> skosAlternateLabelRendering = new ArrayList<>(fieldSchemaArtifact.skosAlternateLabels());
      rendering.put(ALT_LABEL, skosAlternateLabelRendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderCoreValueConstraints(valueConstraints, fieldSchemaArtifact.fieldUi(), rendering);
      renderValueConstraintValues(valueConstraints, rendering);
      renderValueConstraintActions(valueConstraints, rendering);
    }

    if (fieldSchemaArtifact.fieldUi().isTemporal()) {
      TemporalFieldUi templateUi = fieldSchemaArtifact.fieldUi().asTemporalFieldUi();
      rendering.put(GRANULARITY, templateUi.temporalGranularity());
      if (!templateUi.temporalGranularity().isYear() && !templateUi.temporalGranularity().isMonth() &&
        !templateUi.temporalGranularity().isDay()) {
        rendering.put(INPUT_TIME_FORMAT, templateUi.inputTimeFormat());
        rendering.put(INPUT_TIME_ZONE, templateUi.timezoneEnabled());
      }
    }

    if (fieldSchemaArtifact.fieldUi().valueRecommendationEnabled())
      rendering.put(VALUE_RECOMMENDATION, true);

    if (fieldSchemaArtifact.isStatic()) {
      if (fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content().isPresent()) {
        String content = fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content().get();

        if (!content.isEmpty())
          rendering.put(CONTENT, content);
      }
    }

    addArtifactProvenanceRendering(fieldSchemaArtifact, rendering);

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
      rendering.put(CREATED_ON, renderOffsetDateTime(templateInstanceArtifact.createdOn().get()));

    if (templateInstanceArtifact.lastUpdatedOn().isPresent())
      rendering.put(MODIFIED_ON, renderOffsetDateTime(templateInstanceArtifact.lastUpdatedOn().get()));

    // TODO Need to generate YAML for children of template instance

    return rendering;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints values specification
   * <p>
   * e.g.,
   * <pre>
   * values:
   *   - type: ontology
   *     name: Human Disease Ontology
   *     acronym: DOID
   *     iri: "https://data.bioontology.org/ontologies/DOID"
   *   - branch: Disease
   *     acronym: DPCO
   *     termUri: "http://purl.org/twc/dpo/ont/Disease"
   *   - class: Translated Title
   *     source: DATACITE-VOCAB
   *     termUri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   *     type: OntologyClass
   * </pre>
   */
  private void renderValueConstraintValues(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> valuesRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      for (OntologyValueConstraint ontologyValueConstraint : controlledTermValueConstraints.ontologies()) {
        LinkedHashMap<String, Object> ontologyValueConstraintRendering = new LinkedHashMap<>();
        ontologyValueConstraintRendering.put(TYPE, ONTOLOGY);
        ontologyValueConstraintRendering.put(ACRONYM, ontologyValueConstraint.acronym());
        ontologyValueConstraintRendering.put(ONTOLOGY_NAME, ontologyValueConstraint.name());
        ontologyValueConstraintRendering.put(IRI, ontologyValueConstraint.uri());
        if (ontologyValueConstraint.numTerms().isPresent())
          ontologyValueConstraintRendering.put(NUM_TERMS, ontologyValueConstraint.numTerms().get());

        valuesRendering.add(ontologyValueConstraintRendering);
      }

      for (ValueSetValueConstraint valueSetValueConstraint : controlledTermValueConstraints.valueSets()) {
        LinkedHashMap<String, Object> valueSetValueConstraintRendering = new LinkedHashMap<>();
        valueSetValueConstraintRendering.put(TYPE, VALUE_SET);
        valueSetValueConstraintRendering.put(ACRONYM, valueSetValueConstraint.vsCollection());
        valueSetValueConstraintRendering.put(VALUE_SET_NAME, valueSetValueConstraint.name());
        valueSetValueConstraintRendering.put(IRI, valueSetValueConstraint.uri());
        valuesRendering.add(valueSetValueConstraintRendering);
      }

      for (ClassValueConstraint classValueConstraint : controlledTermValueConstraints.classes()) {
        LinkedHashMap<String, Object> classValueConstraintRendering = new LinkedHashMap<>();
        classValueConstraintRendering.put(TYPE, CLASS);
        classValueConstraintRendering.put(PREF_LABEL, classValueConstraint.label());
        classValueConstraintRendering.put(ACRONYM, classValueConstraint.source());
        classValueConstraintRendering.put(TERM_TYPE, classValueConstraint.type());
        classValueConstraintRendering.put(TERM_LABEL, classValueConstraint.prefLabel());
        classValueConstraintRendering.put(IRI, classValueConstraint.uri());
        valuesRendering.add(classValueConstraintRendering);
      }

      for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches()) {
        LinkedHashMap<String, Object> branchValueConstraintRendering = new LinkedHashMap<>();
        branchValueConstraintRendering.put(TYPE, BRANCH);
        branchValueConstraintRendering.put(ONTOLOGY_NAME, branchValueConstraint.name());
        branchValueConstraintRendering.put(ACRONYM, branchValueConstraint.acronym());
        branchValueConstraintRendering.put(IRI, branchValueConstraint.uri());
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

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints action specification
   * <p>
   * e.g.,
   * <pre>
   * actions:
   *   - action: delete
   *     termIri: http://www.semanticweb.org/navyarenjith/ontologies/2021/3/untitled-ontology-5#Baton
   *     sourceIri: https://data.bioontology.org/ontologies/HOME
   *     sourceAcronym: HOME
   *   - action: move
   *     to: 0
   *     termIri: http://www.semanticweb.org/navyarenjith/ontologies/2021/3/untitled-ontology-5#Denial_Of_Counseling_Preventive_Service
   *     sourceIri: https://data.bioontology.org/ontologies/HOME
   *     sourceAcronym: HOME
   * </pre>
   */
  private void renderValueConstraintActions(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> actionsRendering = new ArrayList<>();

    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      if (!controlledTermValueConstraints.actions().isEmpty()) {

        for (ControlledTermValueConstraintsAction action : controlledTermValueConstraints.actions()) {

          LinkedHashMap<String, Object> actionRendering = new LinkedHashMap<>();
          actionRendering.put(ACTION, renderActionName(action.action()));
          actionRendering.put(TERM_IRI, action.termUri());
          actionRendering.put(SOURCE_ACRONYM, action.source());
          if (action.sourceUri().isPresent())
            actionRendering.put(SOURCE_IRI, action.sourceUri().get());
          if (action.to().isPresent())
            actionRendering.put(ACTION_TO, action.to().get());

          actionsRendering.add(actionRendering);
        }
      }
    }

    if (!actionsRendering.isEmpty())
      rendering.put(ACTIONS, actionsRendering);
  }

  // TODO Clean this up
  private void renderCoreValueConstraints(ValueConstraints valueConstraints, FieldUi fieldUi, LinkedHashMap<String, Object> rendering)
  {
    // TODO Use typesafe switch when available
    if (valueConstraints instanceof NumericValueConstraints) {
      NumericValueConstraints numericValueConstraints = (NumericValueConstraints)valueConstraints;
      rendering.put(DATATYPE, numericValueConstraints.numberType());
    } else if (valueConstraints instanceof TemporalValueConstraints) {
      TemporalValueConstraints temporalValueConstraints = (TemporalValueConstraints)valueConstraints;
      rendering.put(DATATYPE, temporalValueConstraints.temporalType());
      rendering.put(GRANULARITY, fieldUi.asTemporalFieldUi().temporalGranularity());
    } else if (valueConstraints instanceof ControlledTermValueConstraints)
      rendering.put(DATATYPE, IRI);

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
        LinkedHashMap<String, Object> defaultRendering = new LinkedHashMap<>();
        defaultRendering.put(DEFAULT_VALUE, controlledTermDefaultValue.value().getLeft());
        defaultRendering.put(DEFAULT_LABEL, controlledTermDefaultValue.value().getRight());

        rendering.put(DEFAULT, defaultRendering);
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

      if (textValueConstraints.regex().isPresent())
        rendering.put(REGEX, textValueConstraints.regex().get());
    }

    // TODO Generate YAML for _valueConstraints.actions
  }

  private List<LinkedHashMap<String, Object>> renderChildSchemas(ParentSchemaArtifact parentSchemaArtifact,
    LinkedHashMap<String, ChildSchemaArtifact> childSchemaArtifacts) {
    List<LinkedHashMap<String, Object>> childSchemasRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    for (Map.Entry<String, ChildSchemaArtifact> childSchemaArtifactEntry : childSchemaArtifacts.entrySet()) {
      String childName = childSchemaArtifactEntry.getKey();
      ChildSchemaArtifact childSchemaArtifact = childSchemaArtifactEntry.getValue();
      if (childSchemaArtifact instanceof FieldSchemaArtifact) {
        FieldSchemaArtifact fieldSchemaArtifact = (FieldSchemaArtifact)childSchemaArtifact;
        LinkedHashMap<String, Object> fieldSchemaRendering = renderFieldSchemaArtifact(childName, fieldSchemaArtifact);
        LinkedHashMap<String, Object> fieldConfigurationRendering =
          renderFieldConfiguration(parentSchemaArtifact, childName, fieldSchemaArtifact);
        if (!fieldConfigurationRendering.isEmpty())
          fieldSchemaRendering.put(CONFIGURATION, fieldConfigurationRendering);
        childSchemasRendering.add(fieldSchemaRendering);
      } else if (childSchemaArtifact instanceof ElementSchemaArtifact) {
        ElementSchemaArtifact elementSchemaArtifact = (ElementSchemaArtifact)childSchemaArtifact;
        LinkedHashMap<String, Object> elementSchemaRendering = renderElementSchemaArtifact(childName, elementSchemaArtifact);
        LinkedHashMap<String, Object> elementConfigurationRendering =
          renderElementConfiguration(parentSchemaArtifact, childName, elementSchemaArtifact);
        if (!elementConfigurationRendering.isEmpty())
          elementSchemaRendering.put(CONFIGURATION, elementConfigurationRendering);

        childSchemasRendering.add(elementSchemaRendering);
      }
    }

    return childSchemasRendering;
  }

  private LinkedHashMap<String, Object> renderElementConfiguration(ParentSchemaArtifact parentSchemaArtifact,
    String elementName, ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    if (elementSchemaArtifact.propertyUri().isPresent())
      rendering.put(PROPERTY_IRI, elementSchemaArtifact.propertyUri().get().toString());

    if (elementSchemaArtifact.isMultiple())
      rendering.put(MULTIPLE, true);

    if (elementSchemaArtifact.minItems().isPresent())
      rendering.put(MIN_ITEMS, elementSchemaArtifact.minItems().get());

    if (elementSchemaArtifact.maxItems().isPresent())
      rendering.put(MAX_ITEMS, elementSchemaArtifact.maxItems().get());

    if (parentSchemaArtifact.getUi().propertyLabels().containsKey(elementName)) {
      String overrideLabel = parentSchemaArtifact.getUi().propertyLabels().get(elementName);
      if (!overrideLabel.equals(elementSchemaArtifact.name()))
        rendering.put(OVERRIDE_LABEL, overrideLabel);
    }

    if (parentSchemaArtifact.getUi().propertyDescriptions().containsKey(elementName)) {
      String overrideDescription = parentSchemaArtifact.getUi().propertyDescriptions().get(elementName);
      if (!overrideDescription.equals(elementSchemaArtifact.description()))
        rendering.put(OVERRIDE_DESCRIPTION, overrideDescription);
    }

    return rendering;
  }

  private LinkedHashMap<String, Object> renderFieldConfiguration(ParentSchemaArtifact parentSchemaArtifact,
    String fieldName, FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      if (fieldSchemaArtifact.valueConstraints().get().requiredValue())
        rendering.put(REQUIRED, true);

      if (fieldSchemaArtifact.valueConstraints().get().recommendedValue())
        rendering.put(RECOMMENDED, true);
    }

    if (fieldSchemaArtifact.fieldUi().hidden())
      rendering.put(HIDDEN, true);

    if (fieldSchemaArtifact.propertyUri().isPresent())
      rendering.put(PROPERTY_IRI, fieldSchemaArtifact.propertyUri().get().toString());

    if (fieldSchemaArtifact.isMultiple() &&
      !fieldSchemaArtifact.fieldUi().isCheckbox() && !fieldSchemaArtifact.isAttributeValue())
      rendering.put(MULTIPLE, true);
    else if (fieldSchemaArtifact.fieldUi().isList())
      rendering.put(MULTIPLE, false);

    if (fieldSchemaArtifact.minItems().isPresent()
      && !fieldSchemaArtifact.fieldUi().isCheckbox() && !fieldSchemaArtifact.isAttributeValue())
      rendering.put(MIN_ITEMS, fieldSchemaArtifact.minItems().get());

    if (fieldSchemaArtifact.maxItems().isPresent())
      rendering.put(MAX_ITEMS, fieldSchemaArtifact.maxItems().get());

    if (parentSchemaArtifact.getUi().propertyLabels().containsKey(fieldName)) {
      String overrideLabel = parentSchemaArtifact.getUi().propertyLabels().get(fieldName);
      if (!overrideLabel.equals(fieldSchemaArtifact.name()))
        rendering.put(OVERRIDE_LABEL, overrideLabel);
    }

    if (parentSchemaArtifact.getUi().propertyDescriptions().containsKey(fieldName)) {
      String overrideDescription = parentSchemaArtifact.getUi().propertyDescriptions().get(fieldName);
      if (!overrideDescription.equals(fieldSchemaArtifact.description()))
        rendering.put(OVERRIDE_DESCRIPTION, overrideDescription);
    }

    return rendering;
  }

  private LinkedHashMap<String, Object> renderSchemaArtifactBase(SchemaArtifact schemaArtifact, String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(TYPE, artifactTypeName);

    if (schemaArtifact.language().isPresent())
      rendering.put(LANGUAGE, schemaArtifact.language().get().toString());

    rendering.put(NAME, schemaArtifact.name());

    if (!schemaArtifact.description().isEmpty())
      rendering.put(DESCRIPTION, schemaArtifact.description());

    if (schemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, schemaArtifact.identifier().get());

    if (!isCompact && schemaArtifact.jsonLdId().isPresent())
      rendering.put(ID, schemaArtifact.jsonLdId().get());

    if (!isCompact && schemaArtifact.status().isPresent())
      rendering.put(STATUS, renderStatusName(schemaArtifact.status().get()));

    if (!isCompact && schemaArtifact.version().isPresent())
      rendering.put(VERSION, schemaArtifact.version().get().toString());

    if (!isCompact)
      rendering.put(MODEL_VERSION, schemaArtifact.modelVersion().toString());

    if (!isCompact && schemaArtifact.previousVersion().isPresent())
      rendering.put(PREVIOUS_VERSION, schemaArtifact.previousVersion().get().toString());

    if (!isCompact && schemaArtifact.derivedFrom().isPresent())
      rendering.put(DERIVED_FROM, schemaArtifact.derivedFrom().get().toString());

    // TODO Generate YAML for annotations

    return rendering;
  }

  private LinkedHashMap<String, Object> renderChildSchemaArtifactBase(String childName,
    SchemaArtifact schemaArtifact, String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(KEY, childName);

    rendering.put(TYPE, artifactTypeName);

    rendering.put(NAME, schemaArtifact.name());

    if (!schemaArtifact.description().isEmpty())
      rendering.put(DESCRIPTION, schemaArtifact.description());

    if (schemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, schemaArtifact.identifier().get());

    if (!isCompact && schemaArtifact.jsonLdId().isPresent())
      rendering.put(ID, schemaArtifact.jsonLdId().get());

    if (!isCompact && schemaArtifact.status().isPresent())
      rendering.put(STATUS, renderStatusName(schemaArtifact.status().get()));

    if (!isCompact && schemaArtifact.version().isPresent())
      rendering.put(VERSION, schemaArtifact.version().get().toString());

    if (!isCompact)
      rendering.put(MODEL_VERSION, schemaArtifact.modelVersion().toString());

    if (!isCompact && schemaArtifact.previousVersion().isPresent())
      rendering.put(PREVIOUS_VERSION, schemaArtifact.previousVersion().get().toString());

    if (!isCompact && schemaArtifact.derivedFrom().isPresent())
      rendering.put(DERIVED_FROM, schemaArtifact.derivedFrom().get().toString());

    if (schemaArtifact.language().isPresent())
      rendering.put(LANGUAGE, schemaArtifact.language().get().toString());

    // TODO Generate YAML for annotations

    return rendering;
  }


  private void addArtifactProvenanceRendering(SchemaArtifact schemaArtifact, LinkedHashMap<String, Object> rendering)
  {
    if (!isCompact) {
      if (schemaArtifact.createdOn().isPresent())
        rendering.put(CREATED_ON, renderOffsetDateTime(schemaArtifact.createdOn().get()));
      if (schemaArtifact.createdBy().isPresent())
        rendering.put(CREATED_BY, schemaArtifact.createdBy().get().toString());
      if (schemaArtifact.lastUpdatedOn().isPresent())
        rendering.put(MODIFIED_ON, renderOffsetDateTime(schemaArtifact.lastUpdatedOn().get()));
      if (schemaArtifact.modifiedBy().isPresent())
        rendering.put(MODIFIED_BY, schemaArtifact.modifiedBy().get().toString());
    }
  }

  private String renderStatusName(Status status)
  {
    // TODO Use typesafe switch when available
    switch (status) {
    case DRAFT:
      return DRAFT_STATUS;
    case PUBLISHED:
      return PUBLISHED_STATUS;
    default:
      throw new RuntimeException("Unknown status " + status);
    }
  }

  private String renderActionName(ValueConstraintsActionType actionType)
  {
    // TODO Use typesafe switch when available
    switch (actionType) {
    case MOVE:
      return MOVE_ACTION;
    case DELETE:
      return DELETE_ACTION;
    default:
      throw new RuntimeException("Unknown action type " + actionType);
    }
  }

  private String renderFieldTypeName(FieldSchemaArtifact fieldSchemaArtifact)
  {
    // TODO Use typesafe switch when available
    switch (fieldSchemaArtifact.fieldUi().inputType()) {
    case TEXTFIELD:
      if (fieldSchemaArtifact.valueConstraints().isPresent() && fieldSchemaArtifact.valueConstraints().get().isControlledTermValueConstraint())
        return CONTROLLED_TERM_FIELD;
      else
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

  private String renderOffsetDateTime(OffsetDateTime offsetDateTime)
  {
    return offsetDateTime.format(datetimeFormatter);
  }

}

