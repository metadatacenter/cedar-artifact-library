package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Example YAML rendering of a template schema artifact:
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
 *
 *   - field: Disease
 *     type: IRI
 *     values:
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
public class YamlArtifactRenderer implements ArtifactRenderer<Map<String, Object>>
{
  private static String TEMPLATE = "template";
  private static String ELEMENT = "element";
  private static String FIELD = "field";
  private static String INSTANCE = "instance";
  private static String ID = "description";
  private static String DESCRIPTION = "description";
  private static String IDENTIFIER = "identifier";
  private static String VERSION = "version";
  private static String CHILDREN = "children";
  private static String TYPE = "type";
  private static String TEXTFIELD_TYPE = "textfield";
  private static String IRI_TYPE = "IRI";
  private static String REQUIRED = "required";
  private static String STATUS = "status";
  private static String IS_MULTIPLE = "isMultiple";
  private static String MIN_LENGTH = "minLength";
  private static String MAX_LENGTH = "maxLength";
  private static String VALUES = "values";
  private static String URI = "uri";
  private static String SOURCE = "source";
  private static String ACRONYM = "acronym";
  private static String ONTOLOGY = "ontology";
  private static String BRANCH = "branch";
  private static String MODEL_VERSION = "modelVersion";
  private static String PREVIOUS_VERSION = "previousVersion";
  private static String IS_BASED_ON = "isBasedOn";
  private static String DERIVED_FROM = "derivedFrom";
  private static String CREATED_BY = "createdBy";
  private static String MODIFIED_BY = "modifiedBy";
  private static String CREATED_ON = "createdOn";
  private static String LAST_UPDATED_ON = "lastUpdatedOn";
  private static String PREF_LABEL = "prefLabel";
  private static String ALT_LABEL = "altLabel";

  private final boolean isExpanded;

  public YamlArtifactRenderer(boolean isExpanded)
  {
    this.isExpanded = isExpanded;
  }

  public Map<String, Object> renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    Map<String, Object> rendering = new HashMap<>();

    rendering.put(TEMPLATE, templateSchemaArtifact.name());
    rendering.put(DESCRIPTION, templateSchemaArtifact.description());

    // TODO UI

    if (templateSchemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, templateSchemaArtifact.identifier().get());

    if (templateSchemaArtifact.version().isPresent())
      rendering.put(VERSION, templateSchemaArtifact.version().get().toString());

    if (templateSchemaArtifact.status().isPresent())
      rendering.put(STATUS, templateSchemaArtifact.status().get().toString());

    if (isExpanded)
      rendering.put(MODEL_VERSION, templateSchemaArtifact.modelVersion().toString());

    if (isExpanded && templateSchemaArtifact.previousVersion().isPresent())
      rendering.put(PREVIOUS_VERSION, templateSchemaArtifact.previousVersion().get().toString());

    if (isExpanded && templateSchemaArtifact.derivedFrom().isPresent())
      rendering.put(DERIVED_FROM, templateSchemaArtifact.derivedFrom().get().toString());

    // TODO Children
    // for (ChildSchemaArtifact childSchemaArtifact : templateSchemaArtifact.getChildSchemas())

    return rendering;
  }

  public Map<String, Object> renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    Map<String, Object> rendering = new HashMap<>();

    rendering.put(TEMPLATE, elementSchemaArtifact.name());
    rendering.put(DESCRIPTION, elementSchemaArtifact.description());

    // TODO UI
    //rendering.put(TYPE, ui.fieldinputtype

    if (elementSchemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, elementSchemaArtifact.identifier().get());

    if (isExpanded && elementSchemaArtifact.version().isPresent())
      rendering.put(VERSION, elementSchemaArtifact.version().get().toString());

    if (isExpanded && elementSchemaArtifact.status().isPresent())
      rendering.put(STATUS, elementSchemaArtifact.status().get().toString());

    if (isExpanded)
      rendering.put(MODEL_VERSION, elementSchemaArtifact.modelVersion().toString());

    if (isExpanded && elementSchemaArtifact.previousVersion().isPresent())
      rendering.put(PREVIOUS_VERSION, elementSchemaArtifact.previousVersion().get().toString());

    if (isExpanded && elementSchemaArtifact.derivedFrom().isPresent())
      rendering.put(DERIVED_FROM, elementSchemaArtifact.derivedFrom().get().toString());

    // TODO Children
    // for (ChildSchemaArtifact childSchemaArtifact : templateSchemaArtifact.getChildSchemas())

    // TODO isMultiple, minItem, maxItems

    return rendering;
  }

  public Map<String, Object> renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    Map<String, Object> rendering = new HashMap<>();

    rendering.put(FIELD, fieldSchemaArtifact.name());
    rendering.put(DESCRIPTION, fieldSchemaArtifact.description());

    // TODO UI
    //rendering.put(TYPE, ui.fieldinputtype

    if (fieldSchemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, fieldSchemaArtifact.identifier().get());

    if (isExpanded && fieldSchemaArtifact.version().isPresent())
      rendering.put(VERSION, fieldSchemaArtifact.version().get().toString());

    if (isExpanded && fieldSchemaArtifact.status().isPresent())
      rendering.put(STATUS, fieldSchemaArtifact.status().get().toString());

    if (isExpanded)
      rendering.put(MODEL_VERSION, fieldSchemaArtifact.modelVersion().toString());

    if (isExpanded && fieldSchemaArtifact.previousVersion().isPresent())
      rendering.put(PREVIOUS_VERSION, fieldSchemaArtifact.previousVersion().get().toString());

    if (isExpanded && fieldSchemaArtifact.derivedFrom().isPresent())
      rendering.put(DERIVED_FROM, fieldSchemaArtifact.derivedFrom().get().toString());

    // Static fields have no JSON Schema fields (properties, required, additionalProperties), or
    // value constraints.
    if (!fieldSchemaArtifact.isStatic()) {

      if (fieldSchemaArtifact.hasIRIValue()) {
      } else {
        // Non-IRI fields may have en empty object as a value so there are no required fields
      }

      if (fieldSchemaArtifact.skosPrefLabel().isPresent())
        rendering.put(PREF_LABEL, fieldSchemaArtifact.skosPrefLabel().get().toString());

      if (!fieldSchemaArtifact.skosAlternateLabels().isEmpty()) {
        // for (String skosAlternateLabel : fieldSchemaArtifact.skosAlternateLabels())
        // TODO AltLabel
      }
      // TODO ValueConstraint
    }

    // TODO isMultiple, minItem, maxItems
    return rendering;
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
}

