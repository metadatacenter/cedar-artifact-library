package org.metadatacenter.artifacts.model.renderer;

import org.apache.regexp.RE;
import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInputType;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldUi;
import org.metadatacenter.artifacts.model.core.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ValueConstraints;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class YamlArtifactRenderer implements ArtifactRenderer<Map<String, Object>>
{
  public static String TEMPLATE = "template";
  public static String ELEMENT = "element";
  public static String FIELD = "field";
  public static String INSTANCE = "instance";
  public static String ID = "id";
  public static String DESCRIPTION = "description";
  public static String IDENTIFIER = "identifier";
  public static String VERSION = "version";
  public static String STATUS = "status";
  public static String MODEL_VERSION = "modelVersion";
  public static String PREVIOUS_VERSION = "previousVersion";
  public static String IS_BASED_ON = "isBasedOn";
  public static String DERIVED_FROM = "derivedFrom";
  public static String CREATED_BY = "createdBy";
  public static String MODIFIED_BY = "modifiedBy";
  public static String CREATED_ON = "createdOn";
  public static String CHILDREN = "children";
  public static String LAST_UPDATED_ON = "lastUpdatedOn";
  public static String PREF_LABEL = "prefLabel";
  public static String ALT_LABEL = "altLabel";
  public static String TYPE = "type";
  public static String HIDDEN = "hidden";
  public static String VALUE_RECOMMENDATION_ENABLED = "valueRecommendationEnabled";
  public static String MULTIPLE_CHOICE = "multipleChoice";
  public static String NUMBER_TYPE = "numberType";
  public static String TEMPORAL_TYPE = "temporalType";
  public static String TIME_ZONE = "timeZone";
  public static String GRANULARITY = "granularity";
  public static String TIME_FORMAT = "timeFormat";
  public static String REQUIRED = "required";
  public static String UNIT = "default";
  public static String DEFAULT = "default";
  public static String MULTIPLE = "multiple";
  public static String MIN_ITEMS = "minItems";
  public static String MAX_ITEMS = "maxItems";
  public static String MIN_VALUE = "minValue";
  public static String MAX_VALUE = "maxValue";
  public static String DECIMAL_PLACES = "decimalPlaces";
  public static String MIN_LENGTH = "minLength";
  public static String MAX_LENGTH = "maxLength";
  public static String URI = "uri";
  public static String SOURCE = "source";
  public static String ACRONYM = "acronym";
  public static String ONTOLOGY = "ontology";
  public static String BRANCH = "branch";
  public static String HEADER = "header";
  public static String FOOTER = "footer";
  public static String CONTENT = "content";
  public static int CONTENT_PREVIEW_LENGTH = 40;

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
   *     type: textfield
   *     required: true
   *
   *   - field: Study ID
   *     description: Study ID field
   *     type: textfield
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
   *         type: textfield
   *       - field: ZIP
   *         type: textfield
   *         minLength: 5
   *         maxLength: 5
   * </pre>
   */
  public LinkedHashMap<String, Object> renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderSchemaArtifact(templateSchemaArtifact, TEMPLATE);

    // TODO UI: propertyLabels, propertyDescriptions
    // TODO childPropertyUris

    if (templateSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, getChildSchemasRendering(templateSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  /**
   * Generate YAML rendering of an element schema artifact
   *
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
   *     type: textfield
   *
   *  - field: ZIP
   *     type: textfield
   *     minLength: 5
   *     maxLength: 5
   *
   * </pre>
   */
  public LinkedHashMap<String, Object> renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderChildSchemaArtifact(elementSchemaArtifact, ELEMENT);

    // TODO UI: propertyLabels, propertyDescriptions
    // TODO childPropertyUris

    if (elementSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, getChildSchemasRendering(elementSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  /**
   * Generate YAML rendering of a field schema artifact
   *
   * e.g.,
   * <pre>
   * field: Disease
   * type: IRI
   * values:
   *       - ontology: Human Disease Ontology
   *         acronym: DOID
   *         uri: "https://data.bioontology.org/ontologies/DOID"
   *       - branch: Disease
   *         acronym: DPCO
   *         uri: "http://purl.org/twc/dpo/ont/Disease"
   *       - class: Translated Title
   *         source: DATACITE-VOCAB
   *         uri: "http://purl.org/datacite/v4.4/TranslatedTitle"
   *         type: OntologyClass
   * </pre>
   */
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderChildSchemaArtifact(fieldSchemaArtifact, FIELD);

    renderFieldUi(fieldSchemaArtifact.fieldUi(), rendering);

    if (fieldSchemaArtifact.skosPrefLabel().isPresent())
      rendering.put(PREF_LABEL, fieldSchemaArtifact.skosPrefLabel().get().toString());

    if (!fieldSchemaArtifact.skosAlternateLabels().isEmpty()) {
      List<Object> skosAlternateLabelRendering = new ArrayList<>();
      for (String skosAlternateLabel : fieldSchemaArtifact.skosAlternateLabels())
        skosAlternateLabelRendering.add(skosAlternateLabel);
      rendering.put(ALT_LABEL, skosAlternateLabelRendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderValueConstraints(valueConstraints, rendering);
    }
    return rendering;
  }

  private void renderFieldUi(FieldUi fieldUi, LinkedHashMap<String, Object> rendering)
  {
    rendering.put(TYPE, fieldUi.inputType());

    if (fieldUi.hidden())
      rendering.put(HIDDEN, true);

    if (isExpanded && fieldUi.valueRecommendationEnabled())
      rendering.put(VALUE_RECOMMENDATION_ENABLED, true);

    if (fieldUi._content().isPresent()) {
      if (isExpanded)
        rendering.put(CONTENT, fieldUi._content().get());
      else
        rendering.put(CONTENT, fieldUi._content().get().substring(0, CONTENT_PREVIEW_LENGTH) + "...");
    }

    if (fieldUi.timeZoneEnabled().isPresent())
      rendering.put(TIME_ZONE, fieldUi.timeZoneEnabled().get());

    if (fieldUi.temporalGranularity().isPresent())
      rendering.put(GRANULARITY, fieldUi.temporalGranularity().get());

    if (fieldUi.inputTimeFormat().isPresent())
      rendering.put(TIME_FORMAT, fieldUi.inputTimeFormat().get());

  }

  private void renderValueConstraints(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    renderCoreValueConstraints(valueConstraints, rendering);

    // TODO
    // ontologies, valueSets, classes, branches
    if (valueConstraints.hasOntologyValueBasedConstraints()) {
      for (OntologyValueConstraint ontologyValueConstraint : valueConstraints.ontologies()) {

      }
    } else {
      // literals
      if (valueConstraints.hasLiteralBasedConstraint()) {
        List<Object> literalsRendering = new ArrayList<>();
        for (LiteralValueConstraint literalValueConstraint : valueConstraints.literals()) {

        }
      }

    }
  }

  private void renderCoreValueConstraints(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    if (valueConstraints.requiredValue())
      rendering.put(REQUIRED, true);

    if (valueConstraints.multipleChoice())
      rendering.put(MULTIPLE_CHOICE, true);

    if (valueConstraints.numberType().isPresent())
      rendering.put(NUMBER_TYPE, valueConstraints.numberType().get());

    if (valueConstraints.minValue().isPresent())
      rendering.put(MIN_VALUE, valueConstraints.minValue().get());

    if (valueConstraints.maxValue().isPresent())
      rendering.put(MAX_VALUE, valueConstraints.maxValue().get());

    if (valueConstraints.defaultValue().isPresent())
      rendering.put(DECIMAL_PLACES, valueConstraints.decimalPlaces().get());

    if (valueConstraints.temporalType().isPresent())
      rendering.put(TEMPORAL_TYPE, valueConstraints.temporalType().get());

    if (valueConstraints.defaultValue().isPresent())
      rendering.put(DEFAULT, valueConstraints.defaultValue().get());

    if (valueConstraints.unitOfMeasure().isPresent())
      rendering.put(UNIT, valueConstraints.unitOfMeasure().get());

    if (valueConstraints.minLength().isPresent())
      rendering.put(MIN_LENGTH, valueConstraints.minLength().get());

    if (valueConstraints.maxLength().isPresent())
      rendering.put(MAX_LENGTH, valueConstraints.maxLength().get());
  }

  public LinkedHashMap<String, Object> renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(INSTANCE, templateInstanceArtifact.name());
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
    rendering.put(DESCRIPTION, schemaArtifact.description());

    if (isExpanded && schemaArtifact.jsonLdId().isPresent())
      rendering.put(ID, schemaArtifact.jsonLdId().get());

    if (schemaArtifact.identifier().isPresent())
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
