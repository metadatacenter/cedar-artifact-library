package org.metadatacenter.artifacts.model.renderer;

import org.metadatacenter.artifacts.model.core.*;
import org.metadatacenter.artifacts.model.core.fields.*;
import org.metadatacenter.artifacts.model.core.fields.constraints.*;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;
import org.metadatacenter.artifacts.util.TerminologyServerClient;
import org.metadatacenter.artifacts.util.TerminologyValue;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.*;

public class YamlArtifactRenderer implements ArtifactRenderer<LinkedHashMap<String, Object>>
{
  private final boolean isCompact;
  private final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
  private final TerminologyServerClient terminologyServerClient;
  private final Version modelVersion = Version.fromString(ModelNodeNames.MODEL_VERSION);

  public YamlArtifactRenderer(boolean isCompact, TerminologyServerClient terminologyServerClient)
  {
    this.isCompact = isCompact;
    this.terminologyServerClient = terminologyServerClient;
  }

  public YamlArtifactRenderer(boolean isCompact)
  {
    this.isCompact = isCompact;
    this.terminologyServerClient = null;
  }

  /**
   * Generate YAML rendering of a template schema artifact
   * <p>
   * e.g.,
   * <pre>
   *   type: template
   *   name: Study
   *   description: Study template
   *   identifier: SFY343
   *   version: 1.0.0
   *   status: published
   *   children:
   *   - key: study-name
   *     type: text-field
   *     name: Study Name
   *     description: Study name field
   *     configuration:
   *       required: true
   *   - type: text-field
   *     name: Study ID
   *     description: Study ID field
   *     minLength: 2
   *     configuration:
   *       required: true
   *   - key: address
   *     type: element
   *     name: Address
   *     description: Address element
   *     configuration:
   *       isMultiple: true
   *       minItems: 0
   *       maxItems: 4
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
    LinkedHashMap<String, Object> rendering = renderTopLevelSchemaArtifactBase(templateSchemaArtifact, TEMPLATE);

    if (templateSchemaArtifact.instanceJsonLdType().isPresent())
      rendering.put(INSTANCE_TYPE, templateSchemaArtifact.instanceJsonLdType().get().toString());

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
  public LinkedHashMap<String, Object> renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderTopLevelSchemaArtifactBase(elementSchemaArtifact, ELEMENT);

    if (elementSchemaArtifact.instanceJsonLdType().isPresent())
      rendering.put(INSTANCE_TYPE, elementSchemaArtifact.instanceJsonLdType().get().toString());

    addArtifactProvenanceRendering(elementSchemaArtifact, rendering);

    if (elementSchemaArtifact.preferredLabel().isPresent())
      rendering.put(PREF_LABEL, elementSchemaArtifact.preferredLabel().get());

    if (!elementSchemaArtifact.alternateLabels().isEmpty()) {
      List<Object> alternateLabelRendering = new ArrayList<>(elementSchemaArtifact.alternateLabels());
      rendering.put(ALT_LABEL, alternateLabelRendering);
    }

    if (elementSchemaArtifact.annotations().isPresent())
      rendering.put(ANNOTATIONS, renderAnnotations(elementSchemaArtifact.annotations().get()));

    if (elementSchemaArtifact.hasChildren())
      rendering.put(CHILDREN, renderChildSchemas(elementSchemaArtifact, elementSchemaArtifact.getChildSchemas()));

    return rendering;
  }

  public LinkedHashMap<String, Object> renderElementSchemaArtifact(String elementKey,
    ElementSchemaArtifact elementSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderNestedSchemaArtifactBase(elementKey, elementSchemaArtifact,
      ELEMENT);

    if (elementSchemaArtifact.instanceJsonLdType().isPresent())
      rendering.put(INSTANCE_TYPE, elementSchemaArtifact.instanceJsonLdType().get().toString());

    addArtifactProvenanceRendering(elementSchemaArtifact, rendering);

    if (elementSchemaArtifact.preferredLabel().isPresent())
      rendering.put(PREF_LABEL, elementSchemaArtifact.preferredLabel().get());

    if (!elementSchemaArtifact.alternateLabels().isEmpty()) {
      List<Object> alternateLabelRendering = new ArrayList<>(elementSchemaArtifact.alternateLabels());
      rendering.put(ALT_LABEL, alternateLabelRendering);
    }

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
   *     sourceAcronym: DOID
   *     sourceName: Human Disease Ontology
   *   - type: class
   *     sourceAcronym: LOINC
   *     termIri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *     termType: class
   *     termLabel: Homo Sapiens
   *     label: Human
   *   - type: branch
   *     sourceAcronym: DPCO
   *     sourceName: Diabetes Pharmacology Ontology
   *     termBaseIri: "http://purl.org/twc/dpo/ont/Disease"
   *     termBaseLabel: Disease
   *     termMaxDepth: 0
   *   - type: valueSet
   *     sourceAcronym: HRAVS
   *     termBaseIri: "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"
   *     termBaseLabel: Area unit
   * </pre>
   */
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderTopLevelSchemaArtifactBase(fieldSchemaArtifact,
      renderFieldTypeName(fieldSchemaArtifact));

    addCoreFieldSchemaArtifactRendering(fieldSchemaArtifact, rendering);

    // A standalone field has no parent, which is where a nested child's `configuration:`
    // block is normally rendered. Emit the field-level UI flags and the field-own
    // configuration entries here so a top-level field round-trips losslessly through the
    // YAML reader/renderer pair.
    addStandaloneFieldUiRendering(fieldSchemaArtifact, rendering);

    LinkedHashMap<String, Object> configuration = renderStandaloneFieldConfiguration(fieldSchemaArtifact);
    if (!configuration.isEmpty())
      rendering.put(CONFIGURATION, configuration);

    return rendering;
  }

  /**
   * The field-own {@code configuration:} entries for a standalone field — the subset of
   * {@link #renderFieldConfiguration} that does not depend on a parent (no property-IRI
   * mapping, no parent label/description overrides). The standalone reader consumes exactly
   * these keys from a top-level {@code configuration:} block.
   */
  private LinkedHashMap<String, Object> renderStandaloneFieldConfiguration(FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      if (fieldSchemaArtifact.valueConstraints().get().requiredValue())
        rendering.put(REQUIRED, true);
      if (fieldSchemaArtifact.valueConstraints().get().recommendedValue())
        rendering.put(RECOMMENDED, true);
    }

    if (fieldSchemaArtifact.isMultiple() && !fieldSchemaArtifact.fieldUi().isCheckbox()
      && !fieldSchemaArtifact.isAttributeValue() && !isMultiSelectListField(fieldSchemaArtifact))
      rendering.put(MULTIPLE, true);

    if (fieldSchemaArtifact.minItems().isPresent() && !fieldSchemaArtifact.fieldUi().isCheckbox()
      && !fieldSchemaArtifact.isAttributeValue() && !isMultiSelectListField(fieldSchemaArtifact))
      rendering.put(MIN_ITEMS, fieldSchemaArtifact.minItems().get());

    if (fieldSchemaArtifact.maxItems().isPresent() && !fieldSchemaArtifact.fieldUi().isCheckbox()
      && !fieldSchemaArtifact.isAttributeValue() && !isMultiSelectListField(fieldSchemaArtifact))
      rendering.put(MAX_ITEMS, fieldSchemaArtifact.maxItems().get());

    return rendering;
  }

  /**
   * Generate YAML rendering of a nested field schema artifact. Nested fields with have a key field
   * <p>
   * e.g.,
   * <pre>
   * key: disease
   * type: controlled-term-field
   * name: Disease
   * values:
   *   - type: ontology
   *     sourceAcronym: DOID
   *     sourceName: Human Disease Ontology
   *   - type: class
   *     sourceAcronym: LOINC
   *     termIri: http://purl.bioontology.org/ontology/LNC/LA19711-3
   *     termType: class
   *     termLabel: Homo Sapiens
   *     label: Human
   *   - type: branch
   *     sourceAcronym: DPCO
   *     sourceName: Diabetes Pharmacology Ontology
   *     termBaseIri: http://purl.org/twc/dpo/ont/Disease
   *     termBaseLabel: Disease
   *     termMaxDepth: 0
   *   - type: valueSet
   *     sourceAcronym: HRAVS
   *     termBaseIri: https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161
   *     termBaseLabel: Area unit
   * </pre>
   */
  public LinkedHashMap<String, Object> renderFieldSchemaArtifact(String fieldKey,
    FieldSchemaArtifact fieldSchemaArtifact)
  {
    LinkedHashMap<String, Object> rendering = renderNestedSchemaArtifactBase(fieldKey, fieldSchemaArtifact,
      renderFieldTypeName(fieldSchemaArtifact));

    addCoreFieldSchemaArtifactRendering(fieldSchemaArtifact, rendering);

    return rendering;
  }

  /**
   * Generate YAML rendering of a template instance artifact.
   * <p>
   * e.g.,
   * <pre>
   * type: instance
   * name: SDY232
   * description: "Study SDY232 instance"
   * id: https://repo.metadatacenter.org/template-instances/19f5261e-9259-45ec-b961-b3d17c92f27f
   * isBasedOn: https://repo.metadatacenter.org/templates/ec3f500f-ddca-4ec1-9196-29932f9304fd
   * createdOn: 2020-07-20T14:09:01-07:00
   * createdBy: https://metadatacenter.org/users/2fa8910d-96e7-4e2f-ae60-4dfa8ec9877d
   * modifiedOn: 2020-07-20T14:09:01-07:00
   * modifiedBy: https://metadatacenter.org/users/2fa8910d-96e7-4e2f-ae60-4dfa8ec9877d
   * children:
   *   "Study Id":
   *     iri: https://example.com/p1
   *     value: text value
   *   "Participants":
   *     value: "2323"
   *     datatype: "xsd:int"
   *   "Disease":
   *     id: https://example.com/d2
   *     label: label
   *   "Protocol URL":
   *     value: https://example.com/p2
   *     datatype: iri
   *   "Study Protocol":
   *     iri: https://example.com/p2
   *     children:
   *       "Protocol Name":
   *         value: Protocol 232
   *       "Protocol ID":
   *         value: P232
   *       "pages":
   *         datatype: "xsd:number"
   *         values:
   *           - "1"
   *           - "23"
   *           - "88"
   *   "Contributors":
   *     iri: https://example.com/p3
   *     children:
   *       - "Contributor Name":
   *           value: "Dr Bob"
   *         "Institution":
   *           value: "Stanford"
   *       - "Contributor Name":
   *           value: "Dr Joe"
   *         "Institution":
   *           value: "Stanford"
   *   "Extra User-Supplied Attributes":
   *     - name: "Study ZIP"
   *       value: "94402"
   *     - name: "Study Duration"
   *       value: "2"
   *   annotations:
   *     - name: https://datacite.com/doi
   *       type: iri
   *       value: https://doi.org/10.82658/8vc1-abcd
   *     - name: Preferred Ontology
   *       value: DOID
   * </pre>
   */
  public LinkedHashMap<String, Object> renderTemplateInstanceArtifact(TemplateInstanceArtifact templateInstanceArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(TYPE, INSTANCE);

    if (templateInstanceArtifact.name().isEmpty())
      throw new ArtifactRenderException("template instance must have a name");
    else
      rendering.put(NAME, templateInstanceArtifact.name().get());

    if (templateInstanceArtifact.description().isPresent() && !templateInstanceArtifact.description().get().isEmpty())
      rendering.put(DESCRIPTION, templateInstanceArtifact.description().get());

    // The id is emitted in both compact and full forms so the instance round-trips.
    if (templateInstanceArtifact.jsonLdId().isPresent())
      rendering.put(ID, templateInstanceArtifact.jsonLdId().get().toString());

    rendering.put(IS_BASED_ON, templateInstanceArtifact.isBasedOn().toString());

    if (!isCompact && templateInstanceArtifact.createdOn().isPresent())
      rendering.put(CREATED_ON, renderOffsetDateTime(templateInstanceArtifact.createdOn().get()));

    if (!isCompact && templateInstanceArtifact.createdBy().isPresent())
      rendering.put(CREATED_BY, templateInstanceArtifact.createdBy().get().toString());

    if (!isCompact && templateInstanceArtifact.lastUpdatedOn().isPresent())
      rendering.put(MODIFIED_ON, renderOffsetDateTime(templateInstanceArtifact.lastUpdatedOn().get()));

    if (!isCompact && templateInstanceArtifact.modifiedBy().isPresent())
      rendering.put(MODIFIED_BY, templateInstanceArtifact.modifiedBy().get().toString());

    LinkedHashMap<String, Object> childInstanceArtifactsRendering = renderChildInstanceArtifacts(
      templateInstanceArtifact);
    if (!childInstanceArtifactsRendering.isEmpty())
      rendering.put(CHILDREN, childInstanceArtifactsRendering);

    for (Map.Entry<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroup : templateInstanceArtifact.attributeValueFieldInstanceGroups()
      .entrySet()) {
      String attributeValueFieldInstanceGroupKey = attributeValueFieldInstanceGroup.getKey();
      Map<String, FieldInstanceArtifact> attributeValueFieldInstanceGroupFields = attributeValueFieldInstanceGroup.getValue();

      if (!attributeValueFieldInstanceGroupFields.isEmpty()) {
        rendering.put(attributeValueFieldInstanceGroupKey,
          renderAttributeValueFieldInstanceGroupFields(attributeValueFieldInstanceGroupFields));
      }
    }

    if (templateInstanceArtifact.annotations().isPresent())
      rendering.put(ANNOTATIONS, renderAnnotations(templateInstanceArtifact.annotations().get()));

    return rendering;
  }

  /**
   * Render a standalone element instance ({@code type: element-instance}). Unlike the
   * nested form — which omits an all-empty element entirely, since its presence is
   * reconstructable from the template — the standalone document always carries its
   * {@code type} discriminator plus any name / description / id, so an empty skeleton
   * still round-trips as a document.
   */
  public LinkedHashMap<String, Object> renderElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(TYPE, ELEMENT_INSTANCE);

    if (elementInstanceArtifact.name().isPresent())
      rendering.put(NAME, elementInstanceArtifact.name().get());

    if (elementInstanceArtifact.description().isPresent() && !elementInstanceArtifact.description().get().isEmpty())
      rendering.put(DESCRIPTION, elementInstanceArtifact.description().get());

    // The id is emitted in both compact and full forms so the instance round-trips.
    if (elementInstanceArtifact.jsonLdId().isPresent())
      rendering.put(ID, elementInstanceArtifact.jsonLdId().get().toString());

    if (!isCompact && elementInstanceArtifact.createdOn().isPresent())
      rendering.put(CREATED_ON, renderOffsetDateTime(elementInstanceArtifact.createdOn().get()));

    if (!isCompact && elementInstanceArtifact.createdBy().isPresent())
      rendering.put(CREATED_BY, elementInstanceArtifact.createdBy().get().toString());

    if (!isCompact && elementInstanceArtifact.lastUpdatedOn().isPresent())
      rendering.put(MODIFIED_ON, renderOffsetDateTime(elementInstanceArtifact.lastUpdatedOn().get()));

    if (!isCompact && elementInstanceArtifact.modifiedBy().isPresent())
      rendering.put(MODIFIED_BY, elementInstanceArtifact.modifiedBy().get().toString());

    LinkedHashMap<String, Object> childInstanceArtifactsRendering = renderChildInstanceArtifacts(
      elementInstanceArtifact);
    if (!childInstanceArtifactsRendering.isEmpty())
      rendering.put(CHILDREN, childInstanceArtifactsRendering);

    for (Map.Entry<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroup : elementInstanceArtifact.attributeValueFieldInstanceGroups()
      .entrySet()) {
      Map<String, FieldInstanceArtifact> fields = attributeValueFieldInstanceGroup.getValue();
      if (!fields.isEmpty())
        rendering.put(attributeValueFieldInstanceGroup.getKey(),
          renderAttributeValueFieldInstanceGroupFields(fields));
    }

    return rendering;
  }

  private LinkedHashMap<String, Object> renderNestedElementInstanceArtifact(ElementInstanceArtifact elementInstanceArtifact)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    LinkedHashMap<String, Object> childInstanceArtifactsRendering = renderChildInstanceArtifacts(
      elementInstanceArtifact);

    LinkedHashMap<String, Object> attributeValueGroups = new LinkedHashMap<>();
    for (Map.Entry<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroup : elementInstanceArtifact.attributeValueFieldInstanceGroups()
      .entrySet()) {
      Map<String, FieldInstanceArtifact> fields = attributeValueFieldInstanceGroup.getValue();
      if (!fields.isEmpty())
        attributeValueGroups.put(attributeValueFieldInstanceGroup.getKey(),
          renderAttributeValueFieldInstanceGroupFields(fields));
    }

    // An element with no set descendant field and no attribute-value groups is an unset slot:
    // omit it entirely (not even a bare `id:`), exactly as an unset field is omitted. Emitting
    // only an `id:` would both reintroduce noise and be ambiguous with a field on read (a field
    // is a map with no `children:` key). The element's presence — and a fresh @id — are
    // reconstructable from the template at the JSON boundary.
    if (childInstanceArtifactsRendering.isEmpty() && attributeValueGroups.isEmpty())
      return rendering;

    // The id is emitted in both compact and full forms so the instance round-trips.
    if (elementInstanceArtifact.jsonLdId().isPresent())
      rendering.put(ID, elementInstanceArtifact.jsonLdId().get().toString());

    if (!childInstanceArtifactsRendering.isEmpty())
      rendering.put(CHILDREN, childInstanceArtifactsRendering);

    rendering.putAll(attributeValueGroups);

    return rendering;
  }

  private LinkedHashMap<String, Object> renderChildInstanceArtifacts(ParentInstanceArtifact parentInstanceArtifact)
  {
    LinkedHashMap<String, Object> childInstanceArtifactsRendering = new LinkedHashMap<>();

    for (String childKey : parentInstanceArtifact.childKeys()) {
      if (parentInstanceArtifact.singleInstanceFieldInstances().containsKey(childKey)) {
        FieldInstanceArtifact fieldInstanceArtifact = parentInstanceArtifact.singleInstanceFieldInstances()
          .get(childKey);
        LinkedHashMap<String, Object> fieldInstanceArtifactRendering = renderFieldInstanceArtifact(
          fieldInstanceArtifact);
        // An unset single field renders empty and is omitted from YAML entirely (no `{}`); its
        // presence is reconstructable from the template at the JSON boundary. Only fields that
        // carry a value/id/label are emitted.
        if (!fieldInstanceArtifactRendering.isEmpty())
          childInstanceArtifactsRendering.put(childKey, fieldInstanceArtifactRendering);
      } else if (parentInstanceArtifact.singleInstanceElementInstances().containsKey(childKey)) {
        if (parentInstanceArtifact.singleInstanceElementInstances().containsKey(childKey)) {
          ElementInstanceArtifact elementInstanceArtifact = parentInstanceArtifact.singleInstanceElementInstances()
            .get(childKey);
          LinkedHashMap<String, Object> elementInstanceArtifactRendering = renderNestedElementInstanceArtifact(
            elementInstanceArtifact);

          if (!elementInstanceArtifactRendering.isEmpty())
            childInstanceArtifactsRendering.put(childKey, elementInstanceArtifactRendering);
        }
      } else if (parentInstanceArtifact.multiInstanceFieldInstances().containsKey(childKey)) {
        List<LinkedHashMap<String, Object>> fieldInstanceArtifactsRendering = renderFieldInstanceArtifacts(
          parentInstanceArtifact.multiInstanceFieldInstances().get(childKey));

        // An empty multi-instance field is an unset slot: omit it entirely (no `[]`), in both
        // modes, exactly as an unset single field is omitted. Its presence is reconstructable
        // from the template at the JSON boundary.
        if (!fieldInstanceArtifactsRendering.isEmpty()) {
          childInstanceArtifactsRendering.put(childKey, fieldInstanceArtifactsRendering);
        }
      } else if (parentInstanceArtifact.multiInstanceElementInstances().containsKey(childKey)) {
        List<LinkedHashMap<String, Object>> elementInstanceArtifactsRendering = renderElementInstanceArtifacts(
          parentInstanceArtifact.multiInstanceElementInstances().get(childKey));

        // An empty multi-element array stays elided: an empty YAML list is field/element
        // ambiguous on read, so we only round-trip empty multi-instance *field* arrays (above),
        // which the reader can default to a field. Empty multi-element slots are reconstructable
        // from the schema.
        if (!elementInstanceArtifactsRendering.isEmpty()) {
          childInstanceArtifactsRendering.put(childKey, elementInstanceArtifactsRendering);
        }
      }
    }

    return childInstanceArtifactsRendering;
  }

  private List<LinkedHashMap<String, Object>> renderElementInstanceArtifacts(
    List<ElementInstanceArtifact> elementInstanceArtifacts)
  {
    List<LinkedHashMap<String, Object>> elementInstanceArtifactsRendering = new ArrayList<>();

    for (ElementInstanceArtifact elementInstanceArtifact : elementInstanceArtifacts) {

      LinkedHashMap<String, Object> elementInstanceArtifactRendering = renderNestedElementInstanceArtifact(
        elementInstanceArtifact);
      // An all-empty entry cannot simply be omitted here: unlike a single-instance element —
      // whose presence is reconstructable from the template — the entry count of a
      // multi-instance list is information (an appended-but-not-yet-filled element instance).
      // Nor can it render as a bare `id:` map, which is read as a field. Emit a typed stub
      // instead; the reader classifies on the discriminator.
      if (elementInstanceArtifactRendering.isEmpty()) {
        elementInstanceArtifactRendering.put(TYPE, ELEMENT_INSTANCE);
        if (elementInstanceArtifact.jsonLdId().isPresent())
          elementInstanceArtifactRendering.put(ID, elementInstanceArtifact.jsonLdId().get().toString());
      }
      elementInstanceArtifactsRendering.add(elementInstanceArtifactRendering);
    }

    return elementInstanceArtifactsRendering;
  }

  private List<LinkedHashMap<String, Object>> renderFieldInstanceArtifacts(
    List<FieldInstanceArtifact> fieldInstanceArtifacts)
  {
    List<LinkedHashMap<String, Object>> fieldInstanceArtifactsRendering = new ArrayList<>();

    for (FieldInstanceArtifact fieldInstanceArtifact : fieldInstanceArtifacts) {

      LinkedHashMap<String, Object> fieldInstanceArtifactRendering = renderFieldInstanceArtifact(fieldInstanceArtifact);
      if (!fieldInstanceArtifactRendering.isEmpty())
        fieldInstanceArtifactsRendering.add(fieldInstanceArtifactRendering);
    }

    return fieldInstanceArtifactsRendering;
  }

  private LinkedHashMap<String, Object> renderFieldInstanceArtifact(FieldInstanceArtifact fieldInstanceArtifact)
  {
    LinkedHashMap<String, Object> fieldInstanceArtifactRendering = new LinkedHashMap<>();

    // An unset field instance — no @value, no @id, no label/notation/prefLabel — is omitted
    // from YAML entirely: it renders to an empty map here and the parent renderer elides the
    // child. No `{}`, no `value: null`, and not even a bare datatype seed (the datatype is
    // recoverable from the schema). The field's presence in the instance is reconstructable
    // from the template at the JSON boundary, so the YAML carries only fields that hold a
    // value. This applies to both compact and expanded output.
    boolean hasValue = fieldInstanceArtifact.jsonLdValue() != null
      && fieldInstanceArtifact.jsonLdValue().isPresent();
    boolean hasId = fieldInstanceArtifact.jsonLdId().isPresent();
    boolean hasLabel = fieldInstanceArtifact.label().isPresent()
      || fieldInstanceArtifact.preferredLabel().isPresent()
      || fieldInstanceArtifact.notation().isPresent();
    if (!hasValue && !hasId && !hasLabel)
      return fieldInstanceArtifactRendering;

    if (!fieldInstanceArtifact.jsonLdTypes().isEmpty())
      fieldInstanceArtifactRendering.put(DATATYPE,
        renderPossiblyXsdPrefixedUri(fieldInstanceArtifact.jsonLdTypes().get(0)));

    if (fieldInstanceArtifact.jsonLdId().isPresent())
      fieldInstanceArtifactRendering.put(ID, fieldInstanceArtifact.jsonLdId().get().toString());

    // The value is emitted only when present (an unset slot returned early above, so this is
    // never a `value: null`).
    if (hasValue) {
      String raw = fieldInstanceArtifact.jsonLdValue().get();
      // Instance @value is string-valued in the model and in JSON. Keep it a
      // string in YAML as well so choosing a serialization format cannot change
      // the value's scalar type. Numeric schema defaults remain numeric below.
      fieldInstanceArtifactRendering.put(VALUE, raw);
    }

    if (fieldInstanceArtifact.label().isPresent())
      fieldInstanceArtifactRendering.put(LABEL, fieldInstanceArtifact.label().get());

    if (fieldInstanceArtifact.notation().isPresent())
      fieldInstanceArtifactRendering.put(NOTATION, fieldInstanceArtifact.notation().get());

    if (fieldInstanceArtifact.preferredLabel().isPresent())
      fieldInstanceArtifactRendering.put(PREF_LABEL, fieldInstanceArtifact.preferredLabel().get());

    if (fieldInstanceArtifact.language().isPresent())
      fieldInstanceArtifactRendering.put(LANGUAGE, fieldInstanceArtifact.language().get());

    return fieldInstanceArtifactRendering;
  }

  /**
   * Generate YAML rendering for core fields in a field schema artifact.
   * <p>
   * e.g.,
   * <pre>
   * prefLabel: Core Diseases
   * altLabels: [ "Patient Diseases", "Diseases" ]
   * values:
   *   - type: ontology
   *     sourceAcronym: DOID
   *     sourceName: Human Disease Ontology
   *   - type: class
   *     sourceAcronym: LOINC
   *     termIri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *     termType: class
   *     termLabel: Homo Sapiens
   *     label: Human
   *   - type: branch
   *     sourceAcronym: DPCO
   *     sourceName: Diabetes Pharmacology Ontology
   *     termBaseIri: "http://purl.org/twc/dpo/ont/Disease"
   *     termBaseLabel: Disease
   *     termMaxDepth: 0
   *   - type: valueSet
   *     sourceAcronym: HRAVS
   *     termBaseIri: "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"
   *     termBaseLabel: Area unit
   * </pre>
   */
  private void addCoreFieldSchemaArtifactRendering(FieldSchemaArtifact fieldSchemaArtifact,
    LinkedHashMap<String, Object> rendering)
  {
    if (fieldSchemaArtifact.preferredLabel().isPresent())
      rendering.put(PREF_LABEL, fieldSchemaArtifact.preferredLabel().get());

    if (!fieldSchemaArtifact.alternateLabels().isEmpty()) {
      List<Object> alternateLabelRendering = new ArrayList<>(fieldSchemaArtifact.alternateLabels());
      rendering.put(ALT_LABEL, alternateLabelRendering);
    }

    if (fieldSchemaArtifact.valueConstraints().isPresent()) {
      ValueConstraints valueConstraints = fieldSchemaArtifact.valueConstraints().get();
      renderCoreValueConstraints(valueConstraints, fieldSchemaArtifact.fieldUi(), rendering);

      if (terminologyServerClient == null)
        renderValueConstraintsValues(valueConstraints, rendering);
      else
        renderValueConstraintsValuesInlined(valueConstraints, rendering);

      renderValueConstraintsActions(valueConstraints, rendering);
    }

    if (fieldSchemaArtifact.fieldUi().isTemporal()) {
      TemporalFieldUi templateUi = fieldSchemaArtifact.fieldUi().asTemporalFieldUi();
      rendering.put(GRANULARITY, templateUi.temporalGranularity().toString());
      if (!templateUi.temporalGranularity().isYear() && !templateUi.temporalGranularity().isMonth()
        && !templateUi.temporalGranularity().isDay() && templateUi.inputTimeFormat().isPresent()) {
        rendering.put(INPUT_TIME_FORMAT, templateUi.inputTimeFormat().get().toString());
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
  }

  /**
   * Emit the field-level UI flags that a nested child carries in its {@code configuration:}
   * block but that a top-level (standalone) field would otherwise drop on a YAML round trip:
   * {@code hidden}, {@code continuePreviousLine}, {@code valueRecommendation}, and the
   * static-field {@code width} / {@code height}. The reader accepts all of these at the field
   * level (see {@code YamlArtifactReader.readFieldUi}). Parent-relative settings (required,
   * recommended, multiple, min/maxItems, override labels, propertyIri) are intentionally
   * excluded — they are only meaningful for a field embedded in a parent.
   */
  private void addStandaloneFieldUiRendering(FieldSchemaArtifact fieldSchemaArtifact,
    LinkedHashMap<String, Object> rendering)
  {
    if (fieldSchemaArtifact.fieldUi().hidden())
      rendering.put(HIDDEN, true);

    if (fieldSchemaArtifact.fieldUi().continuePreviousLine())
      rendering.put(CONTINUE_PREVIOUS_LINE, true);

    if (fieldSchemaArtifact.fieldUi().valueRecommendationEnabled())
      rendering.put(VALUE_RECOMMENDATION, true);

    if (fieldSchemaArtifact.fieldUi().isStatic()) {
      StaticFieldUi staticFieldUi = fieldSchemaArtifact.fieldUi().asStaticFieldUi();
      if (staticFieldUi.width().isPresent())
        rendering.put(WIDTH, staticFieldUi.width().get());

      if (staticFieldUi.height().isPresent())
        rendering.put(HEIGHT, staticFieldUi.height().get());
    }
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints values specification
   * <p>
   * e.g.,
   * <pre>
   * values:
   *   - type: ontology
   *     sourceAcronym: DOID
   *     sourceName: Human Disease Ontology
   *   - type: class
   *     sourceAcronym: LOINC
   *     termIri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *     termType: class
   *     termLabel: Homo Sapiens
   *     label: Human
   *   - type: branch
   *     sourceAcronym: DPCO
   *     sourceName: Diabetes Pharmacology Ontology
   *     termBaseIri: "http://purl.org/twc/dpo/ont/Disease"
   *     termBaseLabel: Disease
   *     termMaxDepth: 0
   *   - type: valueSet
   *     sourceAcronym: HRAVS
   *     termBaseIri: "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"
   *     termBaseLabel: Area unit
   * </pre>
   */
  private void renderValueConstraintsValues(ValueConstraints valueConstraints, LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> valuesRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      for (OntologyValueConstraint ontologyValueConstraint : controlledTermValueConstraints.ontologies()) {
        LinkedHashMap<String, Object> ontologyValueConstraintRendering = renderOntologyValueConstraint(
          ontologyValueConstraint);
        valuesRendering.add(ontologyValueConstraintRendering);
      }

      for (ClassValueConstraint classValueConstraint : controlledTermValueConstraints.classes()) {
        LinkedHashMap<String, Object> classValueConstraintRendering = renderClassValueConstraint(classValueConstraint);
        valuesRendering.add(classValueConstraintRendering);
      }

      for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches()) {
        LinkedHashMap<String, Object> branchValueConstraintRendering = renderBranchValueConstraint(
          branchValueConstraint);
        valuesRendering.add(branchValueConstraintRendering);
      }

      for (ValueSetValueConstraint valueSetValueConstraint : controlledTermValueConstraints.valueSets()) {
        LinkedHashMap<String, Object> valueSetValueConstraintRendering = renderValueSetValueConstraint(
          valueSetValueConstraint);
        valuesRendering.add(valueSetValueConstraintRendering);
      }

    } else if (valueConstraints instanceof TextValueConstraints) {
      TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints;

      for (LiteralValueConstraint literalValueConstraint : textValueConstraints.literals()) {
        LinkedHashMap<String, Object> literalValueConstraintRendering = renderLiteralValueConstraint(
          literalValueConstraint);
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
   *     sourceAcronym: LOINC
   *     termIri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *     termType: class
   *     termLabel: Homo Sapiens
   *     label: Human
   *   - type: class
   *     sourceAcronym: HRAVS
   *     termIri: http://purl.obolibrary.org/obo/UO_0000082
   *     termType: value
   *     termLabel: square millimeter
   *     label: mm^2
   *   - type: class
   *     sourceAcronym: HRAVS
   *     termIri: http://purl.obolibrary.org/obo/UO_0010001
   *     termType: value
   *     termLabel: square micrometer
   *     label: um^2
   * </pre>
   */
  private void renderValueConstraintsValuesInlined(ValueConstraints valueConstraints,
    LinkedHashMap<String, Object> rendering)
  {
    List<LinkedHashMap<String, Object>> valuesRendering = new ArrayList<>();

    // TODO Use typesafe switch when available
    if (valueConstraints instanceof ControlledTermValueConstraints) {
      ControlledTermValueConstraints controlledTermValueConstraints = (ControlledTermValueConstraints)valueConstraints;

      for (OntologyValueConstraint ontologyValueConstraint : controlledTermValueConstraints.ontologies()) {

        List<ClassValueConstraint> classValueConstraints = ontologyValueConstraint2ClassValueConstraints(
          ontologyValueConstraint);

        for (ClassValueConstraint classValueConstraint : classValueConstraints) {
          LinkedHashMap<String, Object> classValueConstraintRendering = renderClassValueConstraint(
            classValueConstraint);
          valuesRendering.add(classValueConstraintRendering);
        }
      }

      for (ClassValueConstraint classValueConstraint : controlledTermValueConstraints.classes()) {
        LinkedHashMap<String, Object> classValueConstraintRendering = renderClassValueConstraint(classValueConstraint);
        valuesRendering.add(classValueConstraintRendering);
      }

      for (BranchValueConstraint branchValueConstraint : controlledTermValueConstraints.branches()) {
        List<ClassValueConstraint> classValueConstraints = branchValueConstraint2ClassValueConstraints(
          branchValueConstraint);

        for (ClassValueConstraint classValueConstraint : classValueConstraints) {
          LinkedHashMap<String, Object> classValueConstraintRendering = renderClassValueConstraint(
            classValueConstraint);
          valuesRendering.add(classValueConstraintRendering);
        }
      }

      for (ValueSetValueConstraint valueSetValueConstraint : controlledTermValueConstraints.valueSets()) {
        List<ClassValueConstraint> classValueConstraints = valueSetValueConstraint2ClassValueConstraints(
          valueSetValueConstraint);

        for (ClassValueConstraint classValueConstraint : classValueConstraints) {
          LinkedHashMap<String, Object> classValueConstraintRendering = renderClassValueConstraint(
            classValueConstraint);
          valuesRendering.add(classValueConstraintRendering);
        }
      }

    } else if (valueConstraints instanceof TextValueConstraints) {
      TextValueConstraints textValueConstraints = (TextValueConstraints)valueConstraints;

      for (LiteralValueConstraint literalValueConstraint : textValueConstraints.literals()) {
        LinkedHashMap<String, Object> literalValueConstraintRendering = renderLiteralValueConstraint(
          literalValueConstraint);
        valuesRendering.add(literalValueConstraintRendering);
      }
    }

    if (!valuesRendering.isEmpty())
      rendering.put(VALUES, valuesRendering);
  }

  private List<ClassValueConstraint> ontologyValueConstraint2ClassValueConstraints(
    OntologyValueConstraint ontologyValueConstraint)
  {
    if (this.terminologyServerClient == null)
      throw new RuntimeException("no terminology server configured");

    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    ControlledTermValueConstraints controlledTermValueConstraints = ControlledTermValueConstraints.builder()
      .withOntologyValueConstraint(ontologyValueConstraint).build();

    List<TerminologyValue> values = terminologyServerClient.getValuesFromTerminologyServer(
      controlledTermValueConstraints);

    for (TerminologyValue value : values) {
      ClassValueConstraint classValueConstraint = new ClassValueConstraint(value.uri(),
        ontologyValueConstraint.acronym(), value.prefLabel(), value.prefLabel(), ValueType.ONTOLOGY_CLASS);
      classValueConstraints.add(classValueConstraint);
    }

    return classValueConstraints;
  }

  private List<ClassValueConstraint> branchValueConstraint2ClassValueConstraints(
    BranchValueConstraint branchValueConstraint)
  {
    if (this.terminologyServerClient == null)
      throw new RuntimeException("no terminology server configured");

    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    ControlledTermValueConstraints controlledTermValueConstraints = ControlledTermValueConstraints.builder()
      .withBranchValueConstraint(branchValueConstraint).build();

    List<TerminologyValue> values = terminologyServerClient.getValuesFromTerminologyServer(
      controlledTermValueConstraints);

    for (TerminologyValue value : values) {
      ClassValueConstraint classValueConstraint = new ClassValueConstraint(value.uri(),
        branchValueConstraint.acronym(), value.prefLabel(), value.prefLabel(), ValueType.ONTOLOGY_CLASS);
      classValueConstraints.add(classValueConstraint);
    }

    return classValueConstraints;
  }

  private List<ClassValueConstraint> valueSetValueConstraint2ClassValueConstraints(
    ValueSetValueConstraint valueSetValueConstraint)
  {
    if (this.terminologyServerClient == null)
      throw new RuntimeException("no terminology server configured");

    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    ControlledTermValueConstraints controlledTermValueConstraints = ControlledTermValueConstraints.builder()
      .withValueSetValueConstraint(valueSetValueConstraint).build();

    List<TerminologyValue> values = terminologyServerClient.getValuesFromTerminologyServer(
      controlledTermValueConstraints);

    for (TerminologyValue value : values) {
      ClassValueConstraint classValueConstraint = new ClassValueConstraint(value.uri(),
        valueSetValueConstraint.vsCollection(), value.prefLabel(), value.prefLabel(), ValueType.VALUE);
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
          actionRendering.put(TERM_IRI, action.termUri().toString());
          if (action.sourceUri().isPresent())
            actionRendering.put(SOURCE_IRI, action.sourceUri().get().toString());
          actionRendering.put(SOURCE_ACRONYM, action.source());

          // TODO Use typesafe switch when available
          if (action.type() == ValueType.ONTOLOGY_CLASS)
            actionRendering.put(TYPE, CLASS);
          else
            actionRendering.put(TYPE, VALUE);

          actionsRendering.add(actionRendering);
        }
      }
    }

    if (!actionsRendering.isEmpty())
      rendering.put(ACTIONS, actionsRendering);
  }

  // TODO Clean this up
  private void renderCoreValueConstraints(ValueConstraints valueConstraints, FieldUi fieldUi,
    LinkedHashMap<String, Object> rendering)
  {
    // TODO Use typesafe switch when available
    if (valueConstraints instanceof NumericValueConstraints) {
      NumericValueConstraints numericValueConstraints = (NumericValueConstraints)valueConstraints;
      rendering.put(DATATYPE, numericValueConstraints.numberType().toString());
    } else if (valueConstraints instanceof TemporalValueConstraints) {
      TemporalValueConstraints temporalValueConstraints = (TemporalValueConstraints)valueConstraints;
      rendering.put(DATATYPE, temporalValueConstraints.temporalType().toString());
      rendering.put(GRANULARITY, fieldUi.asTemporalFieldUi().temporalGranularity().toString());
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
        // Compact YAML aesthetic: render plain numeric defaults unquoted (e.g.
        // `default: 42`) and reserve quotes for values whose YAML auto-typing would
        // change them on a subsequent read (leading-zero forms like '010', exponential
        // notation like '1e3', etc.). The library's reader accepts either form and
        // normalises to canonical string at the model boundary, so the round trip is
        // safe in both directions; this only affects display.
        rendering.put(DEFAULT, renderNumericLiteralForYaml(numericDefaultValue.value().toString()));
      } else if (defaultValue.isControlledTermDefaultValue()) {
        ControlledTermDefaultValue controlledTermDefaultValue = defaultValue.asControlledTermDefaultValue();
        LinkedHashMap<String, Object> defaultRendering = new LinkedHashMap<>();
        defaultRendering.put(DEFAULT_VALUE, controlledTermDefaultValue.value().getLeft().toString());
        defaultRendering.put(DEFAULT_LABEL, controlledTermDefaultValue.value().getRight());

        rendering.put(DEFAULT, defaultRendering);
      } else if (defaultValue.isLinkDefaultValue()) {
        // Link / ext-* identifier default: a bare URI under `default:` (the reader reads it
        // back via readLinkDefaultValue).
        rendering.put(DEFAULT, defaultValue.asLinkDefaultValue().value().toString());
      } else if (defaultValue.isTemporalDefaultValue()) {
        rendering.put(DEFAULT, defaultValue.asTemporalDefaultValue().value());
      } else if (defaultValue.isBooleanDefaultValue()) {
        // A boolean default is a bare true/false. An explicit null default is not emitted: the
        // YAML exchange form forbids null values, so it cannot carry one (the JSON form does).
        Boolean booleanDefault = defaultValue.asBooleanDefaultValue().value();
        if (booleanDefault != null)
          rendering.put(DEFAULT, booleanDefault);
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

    if (valueConstraints instanceof BooleanValueConstraints) {
      BooleanValueConstraints booleanValueConstraints = (BooleanValueConstraints)valueConstraints;

      if (booleanValueConstraints.nullEnabled().isPresent())
        rendering.put(NULL_ENABLED, booleanValueConstraints.nullEnabled().get());

      if (!booleanValueConstraints.labels().isEmpty())
        rendering.put(LABELS, new LinkedHashMap<>(booleanValueConstraints.labels()));
    }
  }

  private List<LinkedHashMap<String, Object>> renderChildSchemas(ParentSchemaArtifact parentSchemaArtifact,
    LinkedHashMap<String, ChildSchemaArtifact> childSchemaArtifacts)
  {
    List<LinkedHashMap<String, Object>> childSchemasRendering = new ArrayList<>();

    for (Map.Entry<String, ChildSchemaArtifact> childSchemaArtifactEntry : childSchemaArtifacts.entrySet()) {
      String childKey = childSchemaArtifactEntry.getKey();
      ChildSchemaArtifact childSchemaArtifact = childSchemaArtifactEntry.getValue();

      // TODO Use typesafe switch when available
      if (childSchemaArtifact instanceof FieldSchemaArtifact) {
        FieldSchemaArtifact fieldSchemaArtifact = (FieldSchemaArtifact)childSchemaArtifact;
        LinkedHashMap<String, Object> fieldSchemaRendering = renderFieldSchemaArtifact(childKey, fieldSchemaArtifact);

        LinkedHashMap<String, Object> fieldConfigurationRendering = renderFieldConfiguration(parentSchemaArtifact,
          childKey, fieldSchemaArtifact);
        if (!fieldConfigurationRendering.isEmpty())
          fieldSchemaRendering.put(CONFIGURATION, fieldConfigurationRendering);

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

    // Attribute-value and static fields don't get a propertyUri mapping in the JSON
    // @context (see ParentSchemaArtifact.getChildPropertyUris which skips them), so a
    // propertyUri carried in their YAML configuration would be lost on the JSON round
    // trip. Emit only for field kinds where the JSON serialization actually preserves it.
    if (!isCompact && fieldSchemaArtifact.propertyUri().isPresent()
      && !fieldSchemaArtifact.isAttributeValue() && !fieldSchemaArtifact.isStatic())
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

  private LinkedHashMap<String, Object> renderTopLevelSchemaArtifactBase(SchemaArtifact schemaArtifact,
    String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    addSchemaArtifactBaseRendering(schemaArtifact, artifactTypeName, rendering);

    return rendering;
  }

  private LinkedHashMap<String, Object> renderNestedSchemaArtifactBase(String childKey,
    ChildSchemaArtifact childSchemaArtifact, String artifactTypeName)
  {
    LinkedHashMap<String, Object> rendering = new LinkedHashMap<>();

    rendering.put(KEY, childKey);

    addSchemaArtifactBaseRendering(childSchemaArtifact, artifactTypeName, rendering);

    return rendering;
  }

  private void addSchemaArtifactBaseRendering(SchemaArtifact schemaArtifact, String artifactTypeName,
    LinkedHashMap<String, Object> rendering)
  {
    rendering.put(TYPE, artifactTypeName);

    if (schemaArtifact.language().isPresent())
      rendering.put(LANGUAGE, schemaArtifact.language().get().toString());

    rendering.put(NAME, schemaArtifact.name());

    if (!schemaArtifact.description().isEmpty())
      rendering.put(DESCRIPTION, schemaArtifact.description());

    if (schemaArtifact.identifier().isPresent())
      rendering.put(IDENTIFIER, schemaArtifact.identifier().get());

    // The id is emitted whenever the artifact has one — top-level artifacts and nested
    // children alike, in both compact and full forms. It identifies the artifact itself, so
    // unlike the provenance/version fields below the compact form keeps it. A nested child is
    // not required to have an id (the reader never demands one), but when it does the renderer
    // preserves it so a JSON template carrying child ids survives a YAML round trip.
    if (schemaArtifact.jsonLdId().isPresent())
      rendering.put(ID, schemaArtifact.jsonLdId().get().toString());

    if (!isCompact && schemaArtifact.status().isPresent())
      rendering.put(STATUS, renderStatus(schemaArtifact.status().get()));

    if (!isCompact && schemaArtifact.version().isPresent())
      rendering.put(VERSION, schemaArtifact.version().get().toString());

    if (!isCompact)
      rendering.put(MODEL_VERSION, modelVersion.toString());
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

  private String renderStatus(Status status)
  {
    // TODO Use typesafe switch when available
    switch (status) {
    case DRAFT:
      return DRAFT_STATUS;
    case PUBLISHED:
      return PUBLISHED_STATUS;
    default:
      throw new ArtifactRenderException("Unknown status " + status);
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
      throw new ArtifactRenderException("Unknown action type " + actionType);
    }
  }

  private String renderFieldTypeName(FieldSchemaArtifact fieldSchemaArtifact) {
    // TODO Use typesafe switch when available
    switch (fieldSchemaArtifact.fieldUi().inputType()) {
      case TEXTFIELD:
        if (fieldSchemaArtifact.valueConstraints().isPresent() && fieldSchemaArtifact.valueConstraints().get()
            .isControlledTermValueConstraint())
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
      case ROR:
        return ROR_FIELD;
      case ORCID:
        return ORCID_FIELD;
      case PFAS:
        return PFAS_FIELD;
      case RRID:
        return RRID_FIELD;
      case PUBMED:
        return PUBMED_FIELD;
      case NIH_GRANT_ID:
        return NIH_GRANT_ID_FIELD;
      case DOI:
        return DOI_FIELD;
      case NUMERIC:
        return NUMERIC_FIELD;
      case TEMPORAL:
        return TEMPORAL_FIELD;
      case BOOLEAN:
        return BOOLEAN_FIELD;
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
        throw new ArtifactRenderException(
            "Unknown field input type " + fieldSchemaArtifact.fieldUi().inputType() + " for field "
                + fieldSchemaArtifact.name());
    }
  }

  private LinkedHashMap<String, Object> renderAttributeValueFieldInstanceGroupFields(
    Map<String, FieldInstanceArtifact> attributeValueFieldInstanceGroupFields)
  {
    LinkedHashMap<String, Object> attributeValueFieldInstanceGroupFieldsRendering = new LinkedHashMap<>();

    for (Map.Entry<String, FieldInstanceArtifact> attributeValueFieldInstanceGroupField : attributeValueFieldInstanceGroupFields.entrySet()) {
      String attributeValueFieldInstanceFieldKey = attributeValueFieldInstanceGroupField.getKey();
      FieldInstanceArtifact fieldInstanceArtifact = attributeValueFieldInstanceGroupField.getValue();

      LinkedHashMap<String, Object> fieldRendering = renderFieldInstanceArtifact(fieldInstanceArtifact);
      // The same omission rule applies inside an attribute-value group: an attribute whose
      // value is unknown must not become `{}`, because the strict YAML reader rejects empty
      // placeholders everywhere in the document.
      if (!fieldRendering.isEmpty())
        attributeValueFieldInstanceGroupFieldsRendering.put(attributeValueFieldInstanceFieldKey, fieldRendering);
    }

    return attributeValueFieldInstanceGroupFieldsRendering;
  }

  private LinkedHashMap<String, Object> renderAnnotations(Annotations annotations)
  {
    LinkedHashMap<String, Object> annotationsRendering = new LinkedHashMap<>();

    for (Map.Entry<String, AnnotationValue> annotationValueEntry : annotations.annotations().entrySet()) {
      String annotationName = annotationValueEntry.getKey();
      AnnotationValue annotationValue = annotationValueEntry.getValue();
      LinkedHashMap<String, Object> annotationRendering = new LinkedHashMap<>();

      // TODO Use typesafe switch when available
      if (annotationValue instanceof LiteralAnnotationValue) {
        LiteralAnnotationValue literalAnnotationValue = (LiteralAnnotationValue)annotationValue;
        annotationRendering.put(VALUE, literalAnnotationValue.getValue());
      } else if (annotationValue instanceof IriAnnotationValue) {
        IriAnnotationValue iriAnnotationValue = (IriAnnotationValue)annotationValue;
        annotationRendering.put(ID, iriAnnotationValue.getValue().toString());
      }
      annotationsRendering.put(annotationName, annotationRendering);
    }

    return annotationsRendering;
  }

  private String renderOffsetDateTime(OffsetDateTime offsetDateTime)
  {
    return offsetDateTime.format(datetimeFormatter);
  }

  /**
   * Decide how to emit a numeric schema default in YAML output.
   *
   * <p>Returns the parsed {@link Number} (Long or Double) when the canonical string
   * representation is round-trip-safe under SnakeYAML's auto-typing — i.e. a bare
   * integer or a plain decimal with no leading zeros, no exponential notation, no
   * trailing-dot weirdness. The YAML serializer then emits a bare number, which is
   * the readable form a human would write.
   *
   * <p>Returns the input {@link String} unchanged for everything else. The library's
   * YAML serializer has {@code ALWAYS_QUOTE_NUMBERS_AS_STRINGS} enabled, so a
   * String that looks numeric stays quoted in the output — preserving its
   * string-ness across a subsequent read (which is what the pathological forms
   * need: a Java String "010" must not round-trip as octal Integer 8).
   *
   * <p>Instance values do not use this conversion: their JSON-LD {@code @value}
   * is string-valued and YAML preserves that same scalar type.
   */
  private static Object renderNumericLiteralForYaml(String canonical)
  {
    if (canonical == null) return null;
    // Plain integer: optional sign, no leading zeros except just "0".
    if (canonical.matches("-?(0|[1-9]\\d*)")) {
      try { return Long.parseLong(canonical); }
      catch (NumberFormatException e) { /* fall through — out of long range */ }
    }
    // Plain decimal: digits, dot, digits; no leading zeros on the integer part.
    if (canonical.matches("-?(0|[1-9]\\d*)\\.\\d+")) {
      try { return Double.parseDouble(canonical); }
      catch (NumberFormatException e) { /* fall through */ }
    }
    // Anything else stays String-typed and the YAML serializer will quote it.
    return canonical;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints class values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: class
   *   sourceAcronym: LOINC
   *   termIri: "http://purl.bioontology.org/ontology/LNC/LA19711-3"
   *   termType: class
   *   termLabel: Homo Sapiens
   *   label: Human
   * </pre>
   */
  /** The pinned version triple {@code {id, effectiveDate?, declaredVersion?}}; absent version omits the
   *  whole key (⇒ latest), mirroring the JSON renderer. */
  private static LinkedHashMap<String, Object> renderVersionSpec(VersionSpec v)
  {
    LinkedHashMap<String, Object> m = new LinkedHashMap<>();
    m.put(VERSION_ID, v.id());
    v.effectiveDate().ifPresent(d -> m.put(VERSION_EFFECTIVE_DATE, d));
    v.declaredVersion().ifPresent(d -> m.put(VERSION_DECLARED_VERSION, d));
    return m;
  }

  private LinkedHashMap<String, Object> renderClassValueConstraint(ClassValueConstraint c)
  {
    LinkedHashMap<String, Object> m = new LinkedHashMap<>();
    m.put(TYPE, CLASS);
    c.sourceSystem().ifPresent(s -> m.put(SOURCE_SYSTEM, s));
    m.put(SOURCE_ACRONYM, c.source());
    c.iri().ifPresent(i -> m.put(SOURCE_IRI, i.toString()));
    m.put(TERM_IRI, c.uri().toString());
    m.put(TERM_TYPE, c.type() == ValueType.ONTOLOGY_CLASS ? "class" : "value");
    m.put(TERM_LABEL, c.prefLabel());
    // The author-facing display label is not rendered: the compact YAML has no display-label key; it
    // defaults to the preferred label on read (VERSIONING-ROADMAP "Revisit").
    c.version().ifPresent(v -> m.put(VERSION, renderVersionSpec(v)));
    return m;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints ontology values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: ontology
   *   sourceAcronym: DOID
   *   sourceName: Human Disease Ontology
   * </pre>
   */
  private LinkedHashMap<String, Object> renderOntologyValueConstraint(OntologyValueConstraint o)
  {
    LinkedHashMap<String, Object> m = new LinkedHashMap<>();
    m.put(TYPE, ONTOLOGY);
    o.sourceSystem().ifPresent(s -> m.put(SOURCE_SYSTEM, s));
    m.put(SOURCE_ACRONYM, o.acronym());
    m.put(SOURCE_NAME, o.name());
    o.iri().ifPresent(i -> m.put(SOURCE_IRI, i.toString()));
    // The ontology's backend URL is not rendered: it is derivable from the acronym and reconstructed on
    // read (VERSIONING-ROADMAP "Revisit: sourceUri is derivable").
    o.numTerms().ifPresent(n -> m.put(TERM_COUNT, n));
    o.version().ifPresent(v -> m.put(VERSION, renderVersionSpec(v)));
    return m;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints branch values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: branch
   *   sourceAcronym: DPCO
   *   sourceName: Diabetes Pharmacology Ontology
   *   termBaseIri: "http://purl.org/twc/dpo/ont/Disease"
   *   termBaseLabel: Disease
   *   termMaxDepth: 0
   * </pre>
   */
  private LinkedHashMap<String, Object> renderBranchValueConstraint(BranchValueConstraint b)
  {
    LinkedHashMap<String, Object> m = new LinkedHashMap<>();
    m.put(TYPE, BRANCH);
    b.sourceSystem().ifPresent(s -> m.put(SOURCE_SYSTEM, s));
    m.put(SOURCE_ACRONYM, b.acronym());
    m.put(SOURCE_NAME, b.source());
    b.iri().ifPresent(i -> m.put(SOURCE_IRI, i.toString()));
    m.put(TERM_BASE_IRI, b.uri().toString());
    m.put(TERM_BASE_LABEL, b.name());
    m.put(TERM_MAX_DEPTH, b.maxDepth());
    b.version().ifPresent(v -> m.put(VERSION, renderVersionSpec(v)));
    return m;
  }

  /**
   * Generate YAML rendering of a field schema artifact _valueConstraints value set values specification
   * <p>
   * e.g.,
   * <pre>
   *   type: valueSet
   *   sourceAcronym: HRAVS
   *   termBaseIri: "https://purl.humanatlas.io/vocab/hravs#HRAVS_1000161"
   *   termBaseLabel: Area unit
   * </pre>
   */
  private LinkedHashMap<String, Object> renderValueSetValueConstraint(ValueSetValueConstraint vs)
  {
    LinkedHashMap<String, Object> m = new LinkedHashMap<>();
    m.put(TYPE, VALUE_SET);
    vs.sourceSystem().ifPresent(s -> m.put(SOURCE_SYSTEM, s));
    m.put(SOURCE_ACRONYM, vs.vsCollection());
    vs.iri().ifPresent(i -> m.put(SOURCE_IRI, i.toString()));
    m.put(TERM_BASE_IRI, vs.uri().toString());
    m.put(TERM_BASE_LABEL, vs.name());
    vs.numTerms().ifPresent(n -> m.put(TERM_COUNT, n));
    vs.version().ifPresent(v -> m.put(VERSION, renderVersionSpec(v)));
    return m;
  }

  private static LinkedHashMap<String, Object> renderLiteralValueConstraint(
    LiteralValueConstraint literalValueConstraint)
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

  private String renderPossiblyXsdPrefixedUri(URI uri)
  {
    if (XsdDatatype.isKnownXsdDatatypeUri(uri))
      return XsdDatatype.fromUri(uri).getText(); // We render the prefixed form of XSD datatypes
    else
      return uri.toString();
  }
}
