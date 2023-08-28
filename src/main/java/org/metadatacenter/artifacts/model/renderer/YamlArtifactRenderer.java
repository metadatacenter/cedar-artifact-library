package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.ChildSchemaArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlArtifactRenderer implements ArtifactRenderer<Map<String, Object>>
{
  private static String TEMPLATE = "template";
  private static String ELEMENT = "element";
  private static String FIELD = "field";
  private static String INSTANCE = "instance";
  private static String ID = "id";
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
  private static String MIN_ITEMS = "minItems";
  private static String MAX_ITEMS = "maxItems";
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
  private static String HEADER = "header";
  private static String FOOTER = "footer";
  private static String CONTENT = "content";
  private static int CONTENT_PREVIEW_LENGTH = 40;

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

    // Static fields have no JSON Schema fields (properties, required, additionalProperties), or
    // value constraints.
    rendering.put(TYPE, fieldSchemaArtifact.fieldUi().inputType());

    if (fieldSchemaArtifact.isStatic()) {
      if (fieldSchemaArtifact.fieldUi()._content().isPresent()) {
        if (isExpanded)
          rendering.put(CONTENT, fieldSchemaArtifact.fieldUi()._content().get());
        else
          rendering.put(CONTENT, fieldSchemaArtifact.fieldUi()._content().get().substring(0, CONTENT_PREVIEW_LENGTH) + "...");
      }
    } else {

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
      rendering.put(IS_MULTIPLE, true);

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

