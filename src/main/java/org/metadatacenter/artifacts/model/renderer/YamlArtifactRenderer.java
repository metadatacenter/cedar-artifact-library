package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.AnnotationValue;
import org.metadatacenter.artifacts.model.core.Annotations;
import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.IriAnnotationValue;
import org.metadatacenter.artifacts.model.core.LiteralAnnotationValue;
import org.metadatacenter.artifacts.model.core.ParentSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;
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
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;
import org.metadatacenter.artifacts.util.TerminologyServerClient;

import java.net.URI;
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
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ANNOTATIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ATTRIBUTE_VALUE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.BRANCH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHECKBOX_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHILDREN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CLASS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONFIGURATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTINUE_PREVIOUS_LINE;
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
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEIGHT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_FORMAT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_ZONE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INSTANCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IS_BASED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.KEY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LANGUAGE;
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
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_ON;
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
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STRING;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPORAL_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_AREA_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.UNIT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_RECOMMENDATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET_NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.WIDTH;

public class YamlArtifactRenderer implements ArtifactRenderer<LinkedHashMap<String, Object>>
{
  private final boolean isCompact;
  private final DateTimeFormatter datetimeFormatter;
  private final TerminologyServerClient terminologyServerClient;
  private final Version modelVersion = Version.fromString("1.6.0");

  public YamlArtifactRenderer(boolean isCompact, TerminologyServerClient terminologyServerClient)
  {
    this.isCompact = isCompact;
    this.datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    this.terminologyServerClient = terminologyServerClient;
  }

  public YamlArtifactRenderer(boolean isCompact)
  {
    this.isCompact = isCompact;
    this.datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    this.terminologyServerClient = null;
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

    if (templateSchemaArtifact.annotations().isPresent())
      rendering.put(ANNOTATIONS, renderAnnotations(templateSchemaArtifact.annotations().get()));

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

  public LinkedHashMap<String, Object> renderElementSchemaArtifact(String elementKey, ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderChildSchemaArtifactBase(elementKey, elementSchemaArtifact, ELEMENT);

    addArtifactProvenanceRendering(elementSchemaArtifact, rendering);

    if (elementSchemaArtifact.annotations().isPresent())
      rendering.put(ANNOTATIONS, renderAnnotations(elementSchemaArtifact.annotations().get()));

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
   *   - type: ontology
   *     acronym: DOID
   *     ontologyName: Human Disease Ontology
   *     iri: "https://data.bioontology.org/ontologies/DOID"
   *   - type: class
   *     label: Human
   *     acronym: LOINC
   *     termType: OntologyClass
   *     termLabel: Homo Sapiens
   *     iri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *   - type: branch
   *     ontologyName: Diabetes Pharmacology Ontology
   *     acronym: DPCO
   *     termLabel: Disease
   *     iri: "http://purl.org/twc/dpo/ont/Disease"
   *   - type: valueSet
   *     acronym: HRAVS
   *     valueSetName: Area unit
   *     iri: "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"
   * </pre>
   */
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering
      = renderSchemaArtifactBase(fieldSchemaArtifact, renderFieldTypeName(fieldSchemaArtifact));

    if (fieldSchemaArtifact.preferredLabel().isPresent())
      rendering.put(PREF_LABEL, fieldSchemaArtifact.preferredLabel().get());

    if (!fieldSchemaArtifact.alternateLabels().isEmpty()) {
      List<Object> alternateLabelRendering = new ArrayList<>(fieldSchemaArtifact.alternateLabels());
      rendering.put(ALT_LABEL, alternateLabelRendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderCoreValueConstraints(valueConstraints, fieldSchemaArtifact.fieldUi(), rendering);
      renderValueConstraintsValues(valueConstraints, rendering);
      renderValueConstraintsActions(valueConstraints, rendering);
    }

    if (fieldSchemaArtifact.fieldUi().isTemporal()) {
      TemporalFieldUi templateUi = fieldSchemaArtifact.fieldUi().asTemporalFieldUi();
      rendering.put(GRANULARITY, templateUi.temporalGranularity());
      if (!templateUi.temporalGranularity().isYear() && !templateUi.temporalGranularity().isMonth() &&
        !templateUi.temporalGranularity().isDay()) {
        rendering.put(INPUT_TIME_FORMAT, templateUi.inputTimeFormat());
      }
      if (templateUi.timezoneEnabled().isPresent() && templateUi.timezoneEnabled().get())
        rendering.put(INPUT_TIME_ZONE, true);
    }

    if (fieldSchemaArtifact.isStatic()) {
      if (fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content().isPresent()) {
        String content = fieldSchemaArtifact.fieldUi().asStaticFieldUi()._content().get();

        if (!content.isEmpty())
          rendering.put(CONTENT, content);
      }
    }

    addArtifactProvenanceRendering(fieldSchemaArtifact, rendering);

    if (fieldSchemaArtifact.annotations().isPresent())
      rendering.put(ANNOTATIONS, renderAnnotations(fieldSchemaArtifact.annotations().get()));

    return rendering;
  }

  // TOOD Cut and paste from above. Merge
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(String fieldKey, FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering
      = renderChildSchemaArtifactBase(fieldKey, fieldSchemaArtifact, renderFieldTypeName(fieldSchemaArtifact));

    if (fieldSchemaArtifact.preferredLabel().isPresent())
      rendering.put(PREF_LABEL, fieldSchemaArtifact.preferredLabel().get());

    if (!fieldSchemaArtifact.alternateLabels().isEmpty()) {
      List<Object> alternateLabelRendering = new ArrayList<>(fieldSchemaArtifact.alternateLabels());
      rendering.put(ALT_LABEL, alternateLabelRendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderCoreValueConstraints(valueConstraints, fieldSchemaArtifact.fieldUi(), rendering);
      renderValueConstraintsValues(valueConstraints, rendering);
      renderValueConstraintsActions(valueConstraints, rendering);
    }

    if (fieldSchemaArtifact.fieldUi().isTemporal()) {
      TemporalFieldUi templateUi = fieldSchemaArtifact.fieldUi().asTemporalFieldUi();
      rendering.put(GRANULARITY, templateUi.temporalGranularity());
      if (!templateUi.temporalGranularity().isYear() && !templateUi.temporalGranularity().isMonth() &&
        !templateUi.temporalGranularity().isDay()) {
        rendering.put(INPUT_TIME_FORMAT, templateUi.inputTimeFormat());
      }
      if (templateUi.timezoneEnabled().isPresent() && templateUi.timezoneEnabled().get())
        rendering.put(INPUT_TIME_ZONE, true);
    }

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

    if (templateInstanceArtifact.annotations().isPresent())
      rendering.put(ANNOTATIONS, renderAnnotations(templateInstanceArtifact.annotations().get()));

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
   *     acronym: DOID
   *     ontologyName: Human Disease Ontology
   *     iri: "https://data.bioontology.org/ontologies/DOID"
   *   - type: class
   *     label: Human
   *     acronym: LOINC
   *     termType: OntologyClass
   *     termLabel: Homo Sapiens
   *     iri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *   - type: branch
   *     ontologyName: Diabetes Pharmacology Ontology
   *     acronym: DPCO
   *     termLabel: Disease
   *     iri: "http://purl.org/twc/dpo/ont/Disease"
   *   - type: valueSet
   *     acronym: HRAVS
   *     valueSetName: Area unit
   *     iri: "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"
   * </pre>
   */
  private void renderValueConstraintsValues(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> valuesRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      for (OntologyValueConstraint ontologyValueConstraint : controlledTermValueConstraints.ontologies()) {
        LinkedHashMap<String, Object> ontologyValueConstraintRendering = renderOntologyValueConstraint(ontologyValueConstraint);
        valuesRendering.add(ontologyValueConstraintRendering);
      }

      for (ClassValueConstraint classValueConstraint : controlledTermValueConstraints.classes()) {
        LinkedHashMap<String, Object> classValueConstraintRendering =  renderClassValueConstraint(classValueConstraint);
        valuesRendering.add(classValueConstraintRendering);
      }

      for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches()) {
        LinkedHashMap<String, Object> branchValueConstraintRendering = renderBranchValueConstraint(branchValueConstraint);
        valuesRendering.add(branchValueConstraintRendering);
      }

      for (ValueSetValueConstraint valueSetValueConstraint : controlledTermValueConstraints.valueSets()) {
        LinkedHashMap<String, Object> valueSetValueConstraintRendering = renderValueSetValueConstraint(valueSetValueConstraint);
        valuesRendering.add(valueSetValueConstraintRendering);
      }

    } else if (valueConstraints instanceof TextValueConstraints) {
      TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints;

      for (LiteralValueConstraint literalValueConstraint : textValueConstraints.literals()) {
        LinkedHashMap<String, Object> literalValueConstraintRendering = renderLiteralValueConstraint(literalValueConstraint);
        valuesRendering.add(literalValueConstraintRendering);
      }
    }

    if (!valuesRendering.isEmpty())
      rendering.put(VALUES, valuesRendering);
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints values specification with all the
   * values inlined as class specifications. Essentially, all terms for ontologies, branches and values sets
   * are retrieved for each of those specification types and each term is listed as a class value specification.
   * <p>
   * Useful for cases when a complete self-contained specification is needed.
   * <p>
   * e.g.,
   * <pre>
   * values:
   *   - type: class
   *     label: Human
   *     acronym: LOINC
   *     termType: OntologyClass
   *     termLabel: Homo Sapiens
   *     iri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *   - type: class
   *     label: mm^2
   *     acronym: HRAVS
   *     termType: ValueSet
   *     termLabel: square millimeter
   *     iri: http://purl.obolibrary.org/obo/UO_0000082
   *   - type: class
   *     label: um^2
   *     acronym: HRAVS
   *     termType: ValueSet
   *     termLabel: square micrometer
   *     iri: http://purl.obolibrary.org/obo/UO_0010001
   * </pre>
   */
  private void renderValueConstraintValuesWithValuesInlined(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> valuesRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      for (OntologyValueConstraint ontologyValueConstraint : controlledTermValueConstraints.ontologies()) {

        List<ClassValueConstraint> classValueConstraints = ontologyValueConstraint2ClassValueConstraints(ontologyValueConstraint);

        for (ClassValueConstraint classValueConstraint : classValueConstraints) {
          LinkedHashMap<String, Object> classValueConstraintRendering =  renderClassValueConstraint(classValueConstraint);
          valuesRendering.add(classValueConstraintRendering);
        }
      }

      for (ClassValueConstraint classValueConstraint : controlledTermValueConstraints.classes()) {
        LinkedHashMap<String, Object> classValueConstraintRendering =  renderClassValueConstraint(classValueConstraint);
        valuesRendering.add(classValueConstraintRendering);
      }

      for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches()) {
        List<ClassValueConstraint> classValueConstraints = branchValueConstraint2ClassValueConstraints(branchValueConstraint);

        for (ClassValueConstraint classValueConstraint : classValueConstraints) {
          LinkedHashMap<String, Object> classValueConstraintRendering =  renderClassValueConstraint(classValueConstraint);
          valuesRendering.add(classValueConstraintRendering);
        }
      }

      for (ValueSetValueConstraint valueSetValueConstraint : controlledTermValueConstraints.valueSets()) {
        List<ClassValueConstraint> classValueConstraints = valueSetValueConstraint2ClassValueConstraints(valueSetValueConstraint);

        for (ClassValueConstraint classValueConstraint : classValueConstraints) {
          LinkedHashMap<String, Object> classValueConstraintRendering =  renderClassValueConstraint(classValueConstraint);
          valuesRendering.add(classValueConstraintRendering);
        }
      }

    } else if (valueConstraints instanceof TextValueConstraints) {
      TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints;

      for (LiteralValueConstraint literalValueConstraint : textValueConstraints.literals()) {
        LinkedHashMap<String, Object> literalValueConstraintRendering = renderLiteralValueConstraint(literalValueConstraint);
        valuesRendering.add(literalValueConstraintRendering);
      }
    }

    if (!valuesRendering.isEmpty())
      rendering.put(VALUES, valuesRendering);
  }

  private List<ClassValueConstraint> ontologyValueConstraint2ClassValueConstraints(OntologyValueConstraint ontologyValueConstraint)
  {
    if (this.terminologyServerClient == null)
      throw new RuntimeException("no terminology server configured");

    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    ControlledTermValueConstraints controlledTermValueConstraints
      = ControlledTermValueConstraints.builder().withOntologyValueConstraint(ontologyValueConstraint).build();

    Map<String, String> preferredLabel2Uri = terminologyServerClient.getValuesFromTerminologyServer(controlledTermValueConstraints);

    for (Map.Entry<String, String> preferredLabel2UriEntry : preferredLabel2Uri.entrySet()) {
      String preferredLabel = preferredLabel2UriEntry.getKey();
      URI uri = URI.create(preferredLabel2UriEntry.getValue());
      ClassValueConstraint classValueConstraint = new ClassValueConstraint(uri, ontologyValueConstraint.acronym(),
        preferredLabel, preferredLabel, ValueType.ONTOLOGY_CLASS);
      classValueConstraints.add(classValueConstraint);
    }

    return classValueConstraints;
  }

  private List<ClassValueConstraint> branchValueConstraint2ClassValueConstraints(BranchValueConstraint branchValueConstraint)
  {
    if (this.terminologyServerClient == null)
      throw new RuntimeException("no terminology server configured");

    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    ControlledTermValueConstraints controlledTermValueConstraints
      = ControlledTermValueConstraints.builder().withBranchValueConstraint(branchValueConstraint).build();

    Map<String, String> preferredLabel2Uri = terminologyServerClient.getValuesFromTerminologyServer(controlledTermValueConstraints);

    for (Map.Entry<String, String> preferredLabel2UriEntry : preferredLabel2Uri.entrySet()) {
      String preferredLabel = preferredLabel2UriEntry.getKey();
      URI uri = URI.create(preferredLabel2UriEntry.getValue());
      ClassValueConstraint classValueConstraint = new ClassValueConstraint(uri, branchValueConstraint.acronym(),
        preferredLabel, preferredLabel, ValueType.ONTOLOGY_CLASS);
      classValueConstraints.add(classValueConstraint);
    }

    return classValueConstraints;
  }

  private List<ClassValueConstraint> valueSetValueConstraint2ClassValueConstraints(ValueSetValueConstraint valueSetValueConstraint)
  {
    if (this.terminologyServerClient == null)
      throw new RuntimeException("no terminology server configured");

    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    ControlledTermValueConstraints controlledTermValueConstraints
      = ControlledTermValueConstraints.builder().withValueSetValueConstraint(valueSetValueConstraint).build();

    Map<String, String> preferredLabel2Uri = terminologyServerClient.getValuesFromTerminologyServer(controlledTermValueConstraints);

    for (Map.Entry<String, String> preferredLabel2UriEntry : preferredLabel2Uri.entrySet()) {
      String preferredLabel = preferredLabel2UriEntry.getKey();
      URI uri = URI.create(preferredLabel2UriEntry.getValue());
      ClassValueConstraint classValueConstraint = new ClassValueConstraint(uri, valueSetValueConstraint.vsCollection(),
        preferredLabel, preferredLabel, ValueType.VALUE);
      classValueConstraints.add(classValueConstraint);
    }

    return classValueConstraints;
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
  private void renderValueConstraintsActions(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> actionsRendering = new ArrayList<>();

    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      if (!controlledTermValueConstraints.actions().isEmpty()) {

        for (ControlledTermValueConstraintsAction action : controlledTermValueConstraints.actions()) {

          LinkedHashMap<String, Object> actionRendering = new LinkedHashMap<>();
          actionRendering.put(ACTION, renderActionName(action.action()));
          if (action.to().isPresent())
            actionRendering.put(ACTION_TO, action.to().get());
          actionRendering.put(TERM_IRI, action.termUri());
          if (action.sourceUri().isPresent())
            actionRendering.put(SOURCE_IRI, action.sourceUri().get());
          actionRendering.put(SOURCE_ACRONYM, action.source());

          // TODO Use typesafe switch when available
          if (action.type() == ValueType.ONTOLOGY_CLASS)
            actionRendering.put(TYPE, "class");
          else
            actionRendering.put(TYPE, "value");

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
  }

  private List<LinkedHashMap<String, Object>> renderChildSchemas(ParentSchemaArtifact parentSchemaArtifact,
    LinkedHashMap<String, ChildSchemaArtifact> childSchemaArtifacts) {
    List<LinkedHashMap<String, Object>> childSchemasRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    for (Map.Entry<String, ChildSchemaArtifact> childSchemaArtifactEntry : childSchemaArtifacts.entrySet()) {
      String childKey = childSchemaArtifactEntry.getKey();
      ChildSchemaArtifact childSchemaArtifact = childSchemaArtifactEntry.getValue();
      if (childSchemaArtifact instanceof FieldSchemaArtifact) {
        FieldSchemaArtifact fieldSchemaArtifact = (FieldSchemaArtifact)childSchemaArtifact;
        LinkedHashMap<String, Object> fieldSchemaRendering = renderFieldSchemaArtifact(childKey, fieldSchemaArtifact);

        if (!(fieldSchemaArtifact.isAttributeValue())) {
          LinkedHashMap<String, Object> fieldConfigurationRendering = renderFieldConfiguration(parentSchemaArtifact,
            childKey, fieldSchemaArtifact);
          if (!fieldConfigurationRendering.isEmpty())
            fieldSchemaRendering.put(CONFIGURATION, fieldConfigurationRendering);
        }

        childSchemasRendering.add(fieldSchemaRendering);
      } else if (childSchemaArtifact instanceof ElementSchemaArtifact) {
        ElementSchemaArtifact elementSchemaArtifact = (ElementSchemaArtifact)childSchemaArtifact;
        LinkedHashMap<String, Object> elementSchemaRendering = renderElementSchemaArtifact(childKey,
          elementSchemaArtifact);
        LinkedHashMap<String, Object> elementConfigurationRendering = renderElementConfiguration(parentSchemaArtifact,
          childKey, elementSchemaArtifact);
        if (!elementConfigurationRendering.isEmpty())
          elementSchemaRendering.put(CONFIGURATION, elementConfigurationRendering);

        childSchemasRendering.add(elementSchemaRendering);
      }
    }

    return childSchemasRendering;
  }

  private LinkedHashMap<String, Object> renderElementConfiguration(ParentSchemaArtifact parentSchemaArtifact,
    String elementKey, ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    if (!isCompact && elementSchemaArtifact.propertyUri().isPresent())
      rendering.put(PROPERTY_IRI, elementSchemaArtifact.propertyUri().get().toString());

    if (parentSchemaArtifact.getUi().propertyLabels().containsKey(elementKey)) {
      String overrideLabel = parentSchemaArtifact.getUi().propertyLabels().get(elementKey);
      if (!overrideLabel.equals(elementSchemaArtifact.name()))
        rendering.put(OVERRIDE_LABEL, overrideLabel);
    }

    if (parentSchemaArtifact.getUi().propertyDescriptions().containsKey(elementKey)) {
      String overrideDescription = parentSchemaArtifact.getUi().propertyDescriptions().get(elementKey);
      if (!overrideDescription.equals(elementSchemaArtifact.description()))
        rendering.put(OVERRIDE_DESCRIPTION, overrideDescription);
    }

    if (elementSchemaArtifact.isMultiple())
      rendering.put(MULTIPLE, true);

    if (elementSchemaArtifact.minItems().isPresent())
      rendering.put(MIN_ITEMS, elementSchemaArtifact.minItems().get());

    if (elementSchemaArtifact.maxItems().isPresent())
      rendering.put(MAX_ITEMS, elementSchemaArtifact.maxItems().get());

    return rendering;
  }

  private LinkedHashMap<String, Object> renderFieldConfiguration(ParentSchemaArtifact parentSchemaArtifact,
    String fieldKey, FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      if (fieldSchemaArtifact.valueConstraints().get().requiredValue())
        rendering.put(REQUIRED, true);
    }

    if (fieldSchemaArtifact.fieldUi().hidden())
      rendering.put(HIDDEN, true);

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      if (fieldSchemaArtifact.valueConstraints().get().recommendedValue())
        rendering.put(RECOMMENDED, true);
    }

    if (!isCompact && fieldSchemaArtifact.propertyUri().isPresent())
      rendering.put(PROPERTY_IRI, fieldSchemaArtifact.propertyUri().get().toString());

    if (parentSchemaArtifact.getUi().propertyLabels().containsKey(fieldKey)) {
      String overrideLabel = parentSchemaArtifact.getUi().propertyLabels().get(fieldKey);
      if (!overrideLabel.equals(fieldSchemaArtifact.name()))
        rendering.put(OVERRIDE_LABEL, overrideLabel);
    }

    if (parentSchemaArtifact.getUi().propertyDescriptions().containsKey(fieldKey)) {
      String overrideDescription = parentSchemaArtifact.getUi().propertyDescriptions().get(fieldKey);
      if (!overrideDescription.equals(fieldSchemaArtifact.description()))
        rendering.put(OVERRIDE_DESCRIPTION, overrideDescription);
    }

    if (fieldSchemaArtifact.fieldUi().continuePreviousLine())
      rendering.put(CONTINUE_PREVIOUS_LINE, true);

    if (fieldSchemaArtifact.fieldUi().valueRecommendationEnabled())
      rendering.put(VALUE_RECOMMENDATION, true);

    if (fieldSchemaArtifact.isMultiple() && !fieldSchemaArtifact.fieldUi().isCheckbox()
      && !fieldSchemaArtifact.isAttributeValue() && !isMultiSelectListField(fieldSchemaArtifact))
      rendering.put(MULTIPLE, true);

    if (fieldSchemaArtifact.minItems().isPresent() && !fieldSchemaArtifact.fieldUi().isCheckbox()
      && !fieldSchemaArtifact.isAttributeValue() && !isMultiSelectListField(fieldSchemaArtifact))
      rendering.put(MIN_ITEMS, fieldSchemaArtifact.minItems().get());

    if (fieldSchemaArtifact.maxItems().isPresent())
      rendering.put(MAX_ITEMS, fieldSchemaArtifact.maxItems().get());

    if (fieldSchemaArtifact.fieldUi().isStatic()) {
      StaticFieldUi staticFieldUi = fieldSchemaArtifact.fieldUi().asStaticFieldUi();
      if (staticFieldUi.width().isPresent())
        rendering.put(WIDTH, staticFieldUi.width().get());

      if (staticFieldUi.height().isPresent())
        rendering.put(HEIGHT, staticFieldUi.height().get());
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
      rendering.put(MODEL_VERSION, modelVersion.toString());

    return rendering;
  }

  // TODO Lot of repetition of above method. This has key: [childKey] in YAML
  private LinkedHashMap<String, Object> renderChildSchemaArtifactBase(String childKey,
    SchemaArtifact schemaArtifact, String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(KEY, childKey);

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
      rendering.put(MODEL_VERSION, modelVersion.toString());

    // TODO Generate YAML for annotations

    return rendering;
  }


  private void addArtifactProvenanceRendering(SchemaArtifact schemaArtifact, LinkedHashMap<String, Object> rendering)
  {
    if (!isCompact) {
      if (schemaArtifact.previousVersion().isPresent())
        rendering.put(PREVIOUS_VERSION, schemaArtifact.previousVersion().get().toString());
      if (schemaArtifact.derivedFrom().isPresent())
        rendering.put(DERIVED_FROM, schemaArtifact.derivedFrom().get().toString());
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

  private List<LinkedHashMap<String, Object>> renderAnnotations(Annotations annotations) {
    List<LinkedHashMap<String, Object>> annotationsRendering = new ArrayList<>();

    for (Map.Entry<String, AnnotationValue> annotationValueEntry : annotations.annotations().entrySet()) {
      String annotationName = annotationValueEntry.getKey();
      AnnotationValue annotationValue = annotationValueEntry.getValue();
      LinkedHashMap<String, Object> annotationRendering = new LinkedHashMap<>();

      annotationRendering.put(NAME, annotationName);

      // TODO Use typesafe switch when available
      if (annotationValue instanceof LiteralAnnotationValue) {
        LiteralAnnotationValue literalAnnotationValue = (LiteralAnnotationValue)annotationValue;
        annotationRendering.put(TYPE, STRING);
        annotationRendering.put(VALUE, literalAnnotationValue.getValue());
      } else if (annotationValue instanceof IriAnnotationValue) {
        IriAnnotationValue iriAnnotationValue = (IriAnnotationValue)annotationValue;
        annotationRendering.put(TYPE, IRI);
        annotationRendering.put(VALUE, iriAnnotationValue.getValue().toString());
      }
      annotationsRendering.add(annotationRendering);
    }

    return annotationsRendering;
  }

 private String renderOffsetDateTime(OffsetDateTime offsetDateTime)
  {
    return offsetDateTime.format(datetimeFormatter);
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints class values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: class
   *   label: Human
   *   acronym: LOINC
   *   termType: class
   *   termLabel: Homo Sapiens
   *   iri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   * </pre>
   */
  private LinkedHashMap<String, Object>  renderClassValueConstraint(ClassValueConstraint classValueConstraint)
  {
    LinkedHashMap<String, Object> classValueConstraintRendering =  new LinkedHashMap<>();

    classValueConstraintRendering.put(TYPE, CLASS);
    classValueConstraintRendering.put(LABEL, classValueConstraint.label());
    classValueConstraintRendering.put(ACRONYM, classValueConstraint.source());

    // TODO Use typesafe switch when available
    if (classValueConstraint.type() == ValueType.ONTOLOGY_CLASS)
      classValueConstraintRendering.put(TERM_TYPE, "class");
    else
      classValueConstraintRendering.put(TERM_TYPE, "value");

    classValueConstraintRendering.put(TERM_LABEL, classValueConstraint.prefLabel());
    classValueConstraintRendering.put(IRI, classValueConstraint.uri().toString());

    return classValueConstraintRendering;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints ontology values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: ontology
   *   acronym: DOID
   *   ontologyName: Human Disease Ontology
   *   iri: "https://data.bioontology.org/ontologies/DOID"
   * </pre>
   */
  private LinkedHashMap<String, Object> renderOntologyValueConstraint(OntologyValueConstraint ontologyValueConstraint)
  {
    LinkedHashMap<String, Object> ontologyValueConstraintRendering = new LinkedHashMap<>();

    ontologyValueConstraintRendering.put(TYPE, ONTOLOGY);
    ontologyValueConstraintRendering.put(ACRONYM, ontologyValueConstraint.acronym());
    ontologyValueConstraintRendering.put(ONTOLOGY_NAME, ontologyValueConstraint.name());
    ontologyValueConstraintRendering.put(IRI, ontologyValueConstraint.uri().toString());
    if (ontologyValueConstraint.numTerms().isPresent())
      ontologyValueConstraintRendering.put(NUM_TERMS, ontologyValueConstraint.numTerms().get());

    return ontologyValueConstraintRendering;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints branch values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: branch
   *   ontologyName: Diabetes Pharmacology Ontology
   *   acronym: DPCO
   *   termLabel: Disease
   *   iri: "http://purl.org/twc/dpo/ont/Disease"
   * </pre>
   */
  private LinkedHashMap<String, Object>  renderBranchValueConstraint(BranchValueConstraint branchValueConstraint)
  {
    LinkedHashMap<String, Object> branchValueConstraintRendering = new LinkedHashMap<>();

    branchValueConstraintRendering.put(TYPE, BRANCH);
    branchValueConstraintRendering.put(ONTOLOGY_NAME, branchValueConstraint.source());
    branchValueConstraintRendering.put(ACRONYM, branchValueConstraint.acronym());
    branchValueConstraintRendering.put(TERM_LABEL, branchValueConstraint.name());
    branchValueConstraintRendering.put(IRI, branchValueConstraint.uri().toString());
    branchValueConstraintRendering.put(MAX_DEPTH, branchValueConstraint.maxDepth());

    return branchValueConstraintRendering;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints value set values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: valueSet
   *   acronym: HRAVS
   *   valueSetName: Area unit
   *   iri: "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"
   * </pre>
   */
  private LinkedHashMap<String, Object>  renderValueSetValueConstraint(ValueSetValueConstraint valueSetValueConstraint)
  {
    LinkedHashMap<String, Object> valueSetValueConstraintRendering = new LinkedHashMap<>();

    valueSetValueConstraintRendering.put(TYPE, VALUE_SET);
    valueSetValueConstraintRendering.put(ACRONYM, valueSetValueConstraint.vsCollection());
    valueSetValueConstraintRendering.put(VALUE_SET_NAME, valueSetValueConstraint.name());
    valueSetValueConstraintRendering.put(IRI, valueSetValueConstraint.uri().toString());
    if (valueSetValueConstraint.numTerms().isPresent())
      valueSetValueConstraintRendering.put(NUM_TERMS, valueSetValueConstraint.numTerms().get());

    return valueSetValueConstraintRendering;
  }

  private static LinkedHashMap<String, Object> renderLiteralValueConstraint(LiteralValueConstraint literalValueConstraint)
  {
    LinkedHashMap<String, Object> literalValueConstraintRendering = new LinkedHashMap<>();

    literalValueConstraintRendering.put(LITERAL, literalValueConstraint.label());
    if (literalValueConstraint.selectedByDefault())
      literalValueConstraintRendering.put(SELECTED_BY_DEFAULT, true);

    return literalValueConstraintRendering;
  }

  private boolean isMultiSelectListField(FieldSchemaArtifact fieldSchemaArtifact)
  {
    return fieldSchemaArtifact.fieldUi().isList() && fieldSchemaArtifact.valueConstraints().isPresent()
      && fieldSchemaArtifact.valueConstraints().get().multipleChoice();
  }

}

