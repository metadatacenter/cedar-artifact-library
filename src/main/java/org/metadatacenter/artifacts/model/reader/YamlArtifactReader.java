package org.metadatacenter.artifacts.model.reader;

import org.metadatacenter.artifacts.model.core.Annotations;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.LinkDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.NumericDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TemporalDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.TextDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.XsdDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype;
import org.metadatacenter.artifacts.model.core.fields.XsdTemporalDatatype;
import org.metadatacenter.artifacts.model.core.fields.constraints.BranchValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ClassValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ControlledTermValueConstraintsAction;
import org.metadatacenter.artifacts.model.core.fields.constraints.LinkValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.LiteralValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.NumericValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.OntologyValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.TemporalValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.TextValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraintsActionType;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueSetValueConstraint;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueType;
import org.metadatacenter.artifacts.model.core.ui.ElementUi;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.NumericFieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.ui.TemplateUi;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ACTION_TO;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ALT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ANNOTATIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ATTRIBUTE_VALUE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.BRANCH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DOI_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.EMAIL_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LINK_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NIH_GRANT_ID_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ORCID_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PFAS_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PHONE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PUBMED_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ROR_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RRID_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_IMAGE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_PAGE_BREAK;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_RICH_TEXT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_SECTION_BREAK;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATIC_YOUTUBE_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHECKBOX_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CLASS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DATATYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DECIMAL_PLACES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DELETE_ACTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FIELD_TYPES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LITERAL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_DEPTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_LENGTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_LENGTH;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NOTATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NUM_TERMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ONTOLOGY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ONTOLOGY_NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RECOMMENDED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REGEX;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.REQUIRED;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SELECTED_BY_DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_ACRONYM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SOURCE_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TERM_TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.UNIT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_SET_NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTINUE_PREVIOUS_LINE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONTROLLED_TERM_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CREATED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DERIVED_FROM;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DRAFT_STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FIELD_TYPES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.FOOTER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.GRANULARITY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEADER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HEIGHT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.HIDDEN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ID;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IDENTIFIER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ELEMENT_INSTANCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INSTANCE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INSTANCE_TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.IS_BASED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CHILDREN;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.CONFIGURATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.DEFAULT_VALUE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_FORMAT;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.INPUT_TIME_ZONE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.KEY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.LANGUAGE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MAX_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MIN_ITEMS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODEL_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_BY;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MODIFIED_ON;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTIPLE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.MULTI_SELECT_LIST_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NAME;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.NUMERIC_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.ORDER;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.OVERRIDE_DESCRIPTION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.OVERRIDE_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREF_LABEL;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PREVIOUS_VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_IRI;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PROPERTY_LABELS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.PUBLISHED_STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.RADIO_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.SINGLE_SELECT_LIST_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.STATUS;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPLATE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEMPORAL_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_AREA_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TEXT_FIELD;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.TYPE;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUES;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VALUE_RECOMMENDATION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.VERSION;
import static org.metadatacenter.artifacts.model.yaml.YamlConstants.WIDTH;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.XSD_IRI;
import static org.metadatacenter.model.ModelNodeNames.PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeValues.TEMPORAL_GRANULARITIES;
import static org.metadatacenter.model.ModelNodeValues.TIME_FORMATS;

public class YamlArtifactReader implements ArtifactReader<LinkedHashMap<String, Object>>
{
  private final Version modelVersion = Version.fromString(ModelNodeNames.MODEL_VERSION);
  private final boolean isCompact;

  public YamlArtifactReader()
  {
    this(false);
  }

  /**
   * @param isCompact when {@code true}, accept compact-form YAML as authored against the
   *   matching {@link org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer}
   *   compact output. Currently this means: tolerate an absent {@code modelVersion} key
   *   and default it to {@link ModelNodeNames#MODEL_VERSION}. A present-but-wrong value
   *   is still rejected.
   */
  public YamlArtifactReader(boolean isCompact)
  {
    this.isCompact = isCompact;
  }

  /**
   * Read a YAML specification of a template schema artifact
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
   *         name: ZIP
   *         minLength: 5
   *         maxLength: 5
   * </pre>
   */
  @Override public TemplateSchemaArtifact readTemplateSchemaArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    rejectNullAndEmptyValues(sourceNode, path);
    String artifactType = readRequiredString(sourceNode, path, TYPE, false);

    if (!artifactType.equals(TEMPLATE))
      throw new ArtifactParseException("invalid artifact type " + artifactType + "; should be " + TEMPLATE, TYPE, path);

    checkSchemaArtifactModelVersion(sourceNode, path);
    return readTemplateSchemaArtifact(sourceNode, path);
  }

  /**
   * Read a YAML specification of an element schema artifact
   * <p></p>
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
   *         configuration:
   *           minLength: 5
   *           maxLength: 5
   * </pre>
   */
  @Override public ElementSchemaArtifact readElementSchemaArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    rejectNullAndEmptyValues(sourceNode, path);
    String artifactType = readRequiredString(sourceNode, path, TYPE, false);

    if (!artifactType.equals(ELEMENT))
      throw new ArtifactParseException("invalid artifact type " + artifactType + "; should be " + ELEMENT, TYPE, path);

    checkSchemaArtifactModelVersion(sourceNode, path);
    return readElementSchemaArtifact(sourceNode, path);
  }

  /**
   * Read a YAML specification of a field schema artifact
   * <p>
   * e.g.,
   * <pre>
   * type: controlled-term-field
   * name: Disease
   * values:
   *   - branch: Disease
   *     acronym: DPCO
   *     termUri: http://purl.org/twc/dpo/ont/Disease
   *   - type: ontology
   *     source: DOID
   *     name: Human Disease Ontology
   *     acronym: DOID
   *     iri: https://data.bioontology.org/ontologies/DOID
   *   - class: Translated Title
   *     source: DATACITE-VOCAB
   *     termUri: http://purl.org/datacite/v4.4/TranslatedTitle
   *     type: OntologyClass
   * </pre>
   */
  @Override public FieldSchemaArtifact readFieldSchemaArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    rejectNullAndEmptyValues(sourceNode, path);

    checkSchemaArtifactModelVersion(sourceNode, path);
    return readFieldSchemaArtifact(sourceNode, path);
  }

  @Override public TemplateInstanceArtifact readTemplateInstanceArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    rejectNullAndEmptyValues(sourceNode, path);
    String artifactType = readRequiredString(sourceNode, path, TYPE, false);

    if (!artifactType.equals(INSTANCE))
      throw new ArtifactParseException("invalid artifact type " + artifactType + "; should be " + INSTANCE, TYPE, path);

    TemplateInstanceArtifact.Builder builder = TemplateInstanceArtifact.builder();
    builder.withName(readRequiredString(sourceNode, path, NAME, false));
    readString(sourceNode, path, DESCRIPTION).ifPresent(builder::withDescription);
    readUri(sourceNode, path, ID).ifPresent(builder::withJsonLdId);
    builder.withIsBasedOn(readRequiredUri(sourceNode, path, IS_BASED_ON));
    readUri(sourceNode, path, CREATED_BY).ifPresent(builder::withCreatedBy);
    readUri(sourceNode, path, MODIFIED_BY).ifPresent(builder::withModifiedBy);
    readOffsetDatetime(sourceNode, path, CREATED_ON).ifPresent(builder::withCreatedOn);
    readOffsetDatetime(sourceNode, path, MODIFIED_ON).ifPresent(builder::withLastUpdatedOn);

    readChildInstanceArtifacts(sourceNode, path).accept(new InstanceChildVisitor() {
      @Override public void visitSingleField(String key, FieldInstanceArtifact f) {
        builder.withSingleInstanceFieldInstance(key, f);
      }

      @Override public void visitSingleElement(String key, ElementInstanceArtifact e) {
        builder.withSingleInstanceElementInstance(key, e);
      }

      @Override public void visitMultiField(String key, List<FieldInstanceArtifact> fs) {
        builder.withMultiInstanceFieldInstances(key, fs);
      }

      @Override public void visitMultiElement(String key, List<ElementInstanceArtifact> es) {
        builder.withMultiInstanceElementInstances(key, es);
      }
    });

    readAttributeValueFieldInstanceGroups(sourceNode, path, TEMPLATE_INSTANCE_RESERVED_KEYS,
      builder::withAttributeValueFieldGroup);

    readAnnotations(sourceNode, path).ifPresent(builder::withAnnotations);

    return builder.build();
  }

  @Override public ElementInstanceArtifact readElementInstanceArtifact(LinkedHashMap<String, Object> sourceNode)
  {
    String path = "/";
    rejectNullAndEmptyValues(sourceNode, path);
    String artifactType = readRequiredString(sourceNode, path, TYPE, false);

    if (!artifactType.equals(ELEMENT_INSTANCE))
      throw new ArtifactParseException(
        "invalid artifact type " + artifactType + "; should be " + ELEMENT_INSTANCE, TYPE, path);

    ElementInstanceArtifact.Builder builder = ElementInstanceArtifact.builder();
    readString(sourceNode, path, NAME).ifPresent(builder::withName);
    readString(sourceNode, path, DESCRIPTION).ifPresent(builder::withDescription);
    readUri(sourceNode, path, ID).ifPresent(builder::withJsonLdId);
    readUri(sourceNode, path, CREATED_BY).ifPresent(builder::withCreatedBy);
    readUri(sourceNode, path, MODIFIED_BY).ifPresent(builder::withModifiedBy);
    readOffsetDatetime(sourceNode, path, CREATED_ON).ifPresent(builder::withCreatedOn);
    readOffsetDatetime(sourceNode, path, MODIFIED_ON).ifPresent(builder::withLastUpdatedOn);

    readChildInstanceArtifacts(sourceNode, path).accept(new InstanceChildVisitor() {
      @Override public void visitSingleField(String key, FieldInstanceArtifact f) {
        builder.withSingleInstanceFieldInstance(key, f);
      }

      @Override public void visitSingleElement(String key, ElementInstanceArtifact e) {
        builder.withSingleInstanceElementInstance(key, e);
      }

      @Override public void visitMultiField(String key, List<FieldInstanceArtifact> fs) {
        builder.withMultiInstanceFieldInstances(key, fs);
      }

      @Override public void visitMultiElement(String key, List<ElementInstanceArtifact> es) {
        builder.withMultiInstanceElementInstances(key, es);
      }
    });

    readAttributeValueFieldInstanceGroups(sourceNode, path, STANDALONE_ELEMENT_INSTANCE_RESERVED_KEYS,
      builder::withAttributeValueFieldGroup);

    return builder.build();
  }

  /**
   * Enforce the YAML contract that none of {@code null}, an empty mapping ({@code {}}), or an
   * empty list ({@code []}) is a valid value. A key whose value is the null literal
   * ({@code key: null}, {@code key:}, {@code key: ~}), an empty mapping ({@code key: {}}), or an
   * empty list ({@code key: []}), and any such list element, is rejected anywhere in the
   * document. The serialization rule is "if something is unknown, omit it"; an explicit null or
   * empty placeholder is always an error on read, mirroring the renderer which never emits one.
   * Run once at each public entry point, so the per-field read helpers never have to tolerate any
   * of them.
   */
  private static void rejectNullAndEmptyValues(Object node, String path)
  {
    if (node instanceof Map<?, ?> map) {
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        String key = String.valueOf(entry.getKey());
        rejectNullOrEmpty(entry.getValue(), key, path);
        rejectNullAndEmptyValues(entry.getValue(), path + (path.endsWith("/") ? "" : "/") + key);
      }
    } else if (node instanceof List<?> list) {
      for (int i = 0; i < list.size(); i++) {
        rejectNullOrEmpty(list.get(i), "[" + i + "]", path);
        rejectNullAndEmptyValues(list.get(i), path + "[" + i + "]");
      }
    }
  }

  private static void rejectNullOrEmpty(Object value, String key, String path)
  {
    if (value == null)
      throw new ArtifactParseException(
        "null is not a valid value; omit the key if the value is unknown", key, path);
    if (value instanceof Map<?, ?> m && m.isEmpty())
      throw new ArtifactParseException(
        "an empty mapping ({}) is not a valid value; omit the key if the value is unknown", key, path);
    if (value instanceof List<?> l && l.isEmpty())
      throw new ArtifactParseException(
        "an empty list ([]) is not a valid value; omit the key if the value is unknown", key, path);
  }

  /**
   * Walk the {@code children:} map of a template/element instance. Distinguishes single
   * vs. multi-instance by whether the value is a Map or a List; distinguishes field vs.
   * element instances by presence of a {@code children:} key (element) or absence (field).
   * Calls the visitor for each bucket.
   */
  @SuppressWarnings("unchecked")
  private ChildInstancesAccumulator readChildInstanceArtifacts(LinkedHashMap<String, Object> sourceNode, String path)
  {
    ChildInstancesAccumulator acc = new ChildInstancesAccumulator();
    LinkedHashMap<String, Object> childrenNode = readChildNode(sourceNode, path, CHILDREN);
    if (childrenNode == null)
      return acc;

    for (Map.Entry<String, Object> entry : childrenNode.entrySet()) {
      String childKey = entry.getKey();
      Object rawValue = entry.getValue();
      String childPath = path + "/" + childKey;

      if (rawValue instanceof List<?>) {
        // Multi-instance: every element should be a map; if it carries `children:` it's an
        // element instance, otherwise it's a field instance. We assume the list is homogeneous.
        List<?> list = (List<?>) rawValue;
        if (list.isEmpty()) {
          // An empty multi-instance array is the expanded-mode skeleton slot for a
          // multi-instance field (the renderer elides empty multi-element arrays, so an empty
          // list here is always a field). Preserve it as an empty multi-field so the slot
          // survives the round trip (e.g. so set_field_value can append to it).
          acc.multiField(childKey, new ArrayList<>());
          continue;
        }
        Object first = list.get(0);
        if (!(first instanceof LinkedHashMap<?, ?>))
          throw new ArtifactParseException("Expected map entries in multi-instance list", childKey, path);
        boolean isElement = isElementInstanceMap((LinkedHashMap<String, Object>) first);
        if (isElement) {
          List<ElementInstanceArtifact> elements = new ArrayList<>();
          for (Object e : list)
            elements.add(readElementInstanceArtifact((LinkedHashMap<String, Object>) e, childPath));
          acc.multiElement(childKey, elements);
        } else {
          List<FieldInstanceArtifact> fields = new ArrayList<>();
          for (Object f : list)
            fields.add(readFieldInstanceArtifact((LinkedHashMap<String, Object>) f, childPath));
          acc.multiField(childKey, fields);
        }
      } else if (rawValue instanceof LinkedHashMap<?, ?>) {
        LinkedHashMap<String, Object> mapValue = (LinkedHashMap<String, Object>) rawValue;
        if (isElementInstanceMap(mapValue)) {
          acc.singleElement(childKey, readElementInstanceArtifact(mapValue, childPath));
        } else {
          acc.singleField(childKey, readFieldInstanceArtifact(mapValue, childPath));
        }
      } else if (rawValue == null) {
        // null is never a valid value (rejectNullValues already rejects it at the entry point;
        // this is the defensive local statement of the same rule). A value-less field slot is
        // an empty mapping ({}), not null.
        throw new ArtifactParseException(
          "null is not a valid child instance; an empty field slot is an empty mapping ({})",
          childKey, path);
      } else {
        throw new ArtifactParseException(
          "Expected map or list value for child instance, got " + rawValue.getClass(), childKey, path);
      }
    }
    return acc;
  }

  /**
   * A child-instance map is an element instance when it carries a {@code children:} block, or
   * when it is an all-empty sub-record stub carrying the {@code type: element-instance}
   * discriminator (the renderer emits the stub for empty entries in a multi-instance list,
   * whose count must survive the round trip).
   */
  private static boolean isElementInstanceMap(LinkedHashMap<String, Object> map)
  {
    return map.containsKey(CHILDREN) || ELEMENT_INSTANCE.equals(map.get(TYPE));
  }

  private ElementInstanceArtifact readElementInstanceArtifact(LinkedHashMap<String, Object> sourceNode, String path)
  {
    ElementInstanceArtifact.Builder builder = ElementInstanceArtifact.builder();
    readUri(sourceNode, path, ID).ifPresent(builder::withJsonLdId);

    readChildInstanceArtifacts(sourceNode, path).accept(new InstanceChildVisitor() {
      @Override public void visitSingleField(String key, FieldInstanceArtifact f) {
        builder.withSingleInstanceFieldInstance(key, f);
      }

      @Override public void visitSingleElement(String key, ElementInstanceArtifact e) {
        builder.withSingleInstanceElementInstance(key, e);
      }

      @Override public void visitMultiField(String key, List<FieldInstanceArtifact> fs) {
        builder.withMultiInstanceFieldInstances(key, fs);
      }

      @Override public void visitMultiElement(String key, List<ElementInstanceArtifact> es) {
        builder.withMultiInstanceElementInstances(key, es);
      }
    });

    readAttributeValueFieldInstanceGroups(sourceNode, path, ELEMENT_INSTANCE_RESERVED_KEYS,
      builder::withAttributeValueFieldGroup);

    return builder.build();
  }

  /**
   * Top-level YAML keys reserved by the template-instance renderer. Any other top-level key is
   * treated as an attribute-value field instance group whose value is a map of attribute names
   * to field instances.
   */
  private static final Set<String> TEMPLATE_INSTANCE_RESERVED_KEYS = Set.of(
    TYPE, NAME, DESCRIPTION, ID, IS_BASED_ON,
    CREATED_BY, MODIFIED_BY, CREATED_ON, MODIFIED_ON,
    CHILDREN, ANNOTATIONS);

  /**
   * Top-level YAML keys reserved by the element-instance renderer. Element instances are nested
   * (they appear as values in a parent's {@code children:} map) and only ever carry {@code id}
   * and {@code children} as reserved keys.
   */
  private static final Set<String> ELEMENT_INSTANCE_RESERVED_KEYS = Set.of(TYPE, ID, CHILDREN);

  /**
   * Top-level YAML keys reserved by the standalone element-instance form ({@code type:
   * element-instance}) — the nested set plus the document-level keys the standalone
   * renderer emits.
   */
  private static final Set<String> STANDALONE_ELEMENT_INSTANCE_RESERVED_KEYS = Set.of(
    TYPE, NAME, DESCRIPTION, ID, CREATED_BY, MODIFIED_BY, CREATED_ON, MODIFIED_ON, CHILDREN);

  /**
   * Walk top-level keys of an instance YAML map looking for attribute-value field groups. Each
   * non-reserved key whose value is a map is treated as a group; each entry in that map is
   * read as a {@link FieldInstanceArtifact} (so the inner shape is the same one written by
   * {@code renderAttributeValueFieldInstanceGroupFields}).
   */
  @SuppressWarnings("unchecked")
  private void readAttributeValueFieldInstanceGroups(LinkedHashMap<String, Object> sourceNode, String path,
    Set<String> reservedKeys, java.util.function.BiConsumer<String, LinkedHashMap<String, FieldInstanceArtifact>> sink)
  {
    for (Map.Entry<String, Object> entry : sourceNode.entrySet()) {
      String key = entry.getKey();
      if (reservedKeys.contains(key))
        continue;
      Object rawValue = entry.getValue();
      if (!(rawValue instanceof LinkedHashMap<?, ?>))
        continue;
      LinkedHashMap<String, Object> groupNode = (LinkedHashMap<String, Object>) rawValue;
      LinkedHashMap<String, FieldInstanceArtifact> groupFields = new LinkedHashMap<>();
      for (Map.Entry<String, Object> fieldEntry : groupNode.entrySet()) {
        String fieldName = fieldEntry.getKey();
        Object fieldRaw = fieldEntry.getValue();
        if (!(fieldRaw instanceof LinkedHashMap<?, ?>))
          throw new ArtifactParseException("Expected map value for attribute-value field " + fieldName, key, path);
        groupFields.put(fieldName,
          readFieldInstanceArtifact((LinkedHashMap<String, Object>) fieldRaw, path + "/" + key + "/" + fieldName));
      }
      sink.accept(key, groupFields);
    }
  }

  /**
   * Read a single field-instance YAML map. The renderer writes:
   * {@code datatype:} (optional, an XSD-prefixed URI), {@code id:} (optional URI),
   * {@code value:} (optional, may be explicit {@code null}), plus optional
   * {@code label:}, {@code notation:}, {@code prefLabel:}, {@code language:}.
   */
  private FieldInstanceArtifact readFieldInstanceArtifact(LinkedHashMap<String, Object> sourceNode, String path)
  {
    List<URI> jsonLdTypes = new ArrayList<>();
    Optional<String> datatype = readEnumOrString(sourceNode, path, DATATYPE);
    if (datatype.isPresent()) {
      // The renderer writes the instance @type as an xsd:-prefixed name (e.g. "xsd:int").
      // The in-memory model and the JSON renderer expect the fully-qualified XSD URI, so
      // expand the prefix here — otherwise rendering the instance to JSON throws when it
      // tries to re-prefix a value that is already prefixed.
      String dt = datatype.get();
      jsonLdTypes.add(dt.startsWith("xsd:")
        ? URI.create(XSD_IRI + dt.substring("xsd:".length()))
        : URI.create(dt));
    }
    Optional<URI> jsonLdId = readUri(sourceNode, path, ID);
    Optional<String> jsonLdValue = readScalarAsString(sourceNode, path, VALUE);
    Optional<String> label = readString(sourceNode, path, LABEL, true);
    Optional<String> notation = readString(sourceNode, path, NOTATION, true);
    Optional<String> preferredLabel = readString(sourceNode, path, PREF_LABEL, true);
    Optional<String> language = readString(sourceNode, path, LANGUAGE);

    return FieldInstanceArtifact.create(jsonLdTypes, jsonLdId, jsonLdValue, label, notation, preferredLabel, language);
  }

  /**
   * Read a field-instance {@code value:} as a string. Unlike {@link #readString}, this coerces a
   * scalar number or boolean to its string form: the renderer emits numeric instance values as
   * bare YAML numbers (e.g. {@code value: 33}) for readability, but the model stores @value as a
   * string. An absent key or an explicit {@code null} reads as empty.
   */
  private Optional<String> readScalarAsString(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    if (!sourceNode.containsKey(fieldKey))
      return Optional.empty();
    Object raw = sourceNode.get(fieldKey);
    if (raw == null)
      return Optional.empty();
    if (raw instanceof String s)
      return Optional.of(s);
    if (raw instanceof Number || raw instanceof Boolean)
      return Optional.of(raw.toString());
    throw new ArtifactParseException("Expecting a scalar value (string, number, or boolean), got "
      + raw.getClass(), fieldKey, path);
  }

  /**
   * Small visitor used to bucket child instances back into the builder without duplicating the
   * four-way dispatch (single field / single element / multi field / multi element) per caller.
   */
  private interface InstanceChildVisitor
  {
    void visitSingleField(String key, FieldInstanceArtifact f);
    void visitSingleElement(String key, ElementInstanceArtifact e);
    void visitMultiField(String key, List<FieldInstanceArtifact> fs);
    void visitMultiElement(String key, List<ElementInstanceArtifact> es);
  }

  private static final class ChildInstancesAccumulator
  {
    private final List<Runnable> dispatches = new ArrayList<>();

    void singleField(String key, FieldInstanceArtifact f) {
      dispatches.add(() -> currentVisitor.visitSingleField(key, f));
    }

    void singleElement(String key, ElementInstanceArtifact e) {
      dispatches.add(() -> currentVisitor.visitSingleElement(key, e));
    }

    void multiField(String key, List<FieldInstanceArtifact> fs) {
      dispatches.add(() -> currentVisitor.visitMultiField(key, fs));
    }

    void multiElement(String key, List<ElementInstanceArtifact> es) {
      dispatches.add(() -> currentVisitor.visitMultiElement(key, es));
    }

    private InstanceChildVisitor currentVisitor;

    void accept(InstanceChildVisitor visitor) {
      currentVisitor = visitor;
      for (Runnable r : dispatches) r.run();
      currentVisitor = null;
    }
  }

  private TemplateSchemaArtifact readTemplateSchemaArtifact(LinkedHashMap<String, Object> sourceNode, String path)
  {
    String templateName = readRequiredString(sourceNode, path, NAME, false);
    String internalName = templateName + " template schema";
    String internalDescription = templateName + " template schema generated by the CEDAR Artifact Library";
    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readUri(sourceNode, path, ID);
    Optional<URI> instanceJsonLdType = readUri(sourceNode, path, INSTANCE_TYPE);
    String description = readString(sourceNode, path, DESCRIPTION, "");
    Optional<String> identifier = readString(sourceNode, path, IDENTIFIER, true);
    // Default version/status on the top-level artifact only; preserve absence on nested children.
    ArtifactDefaults.Policy defaultingPolicy =
      isRootPath(path) ? ArtifactDefaults.Policy.APPLY : ArtifactDefaults.Policy.PRESERVE;
    Optional<Version> version = ArtifactDefaults.version(readVersion(sourceNode, path, VERSION), defaultingPolicy);
    Optional<Status> status = ArtifactDefaults.status(readStatus(sourceNode, path, STATUS), defaultingPolicy);
    Optional<URI> previousVersion = readUri(sourceNode, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, DERIVED_FROM);
    Optional<URI> createdBy = readUri(sourceNode, path, CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDatetime(sourceNode, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDatetime(sourceNode, path, MODIFIED_ON);
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    List<String> childOrder = new ArrayList<>();
    LinkedHashMap<String, String> childLabels = new LinkedHashMap<>();
    LinkedHashMap<String, String> childDescriptions = new LinkedHashMap<>();
    readChildSchemas(sourceNode, path, fieldSchemas, elementSchemas, childOrder, childLabels, childDescriptions);
    Optional<String> language = readString(sourceNode, path, LANGUAGE);
    TemplateUi templateUi = readTemplateUi(sourceNode, path, childOrder, childLabels, childDescriptions);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path);

    return TemplateSchemaArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, instanceJsonLdType, templateName,
      description, identifier, version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn,
      lastUpdatedOn, fieldSchemas, elementSchemas, language, templateUi, annotations, internalName,
      internalDescription);
  }

  private ElementSchemaArtifact readElementSchemaArtifact(LinkedHashMap<String, Object> sourceNode, String path)
  {
    String elementName = readRequiredString(sourceNode, path, NAME, false);
    String internalName = elementName + " element schema";
    String internalDescription = elementName + " element schema generated by the CEDAR Artifact Library";
    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(PARENT_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readUri(sourceNode, path, ID);
    Optional<URI> instanceJsonLdType = readUri(sourceNode, path, INSTANCE_TYPE);
    String description = readString(sourceNode, path, DESCRIPTION, "");
    Optional<String> identifier = readString(sourceNode, path, IDENTIFIER, true);
    // Default version/status on the top-level artifact only; preserve absence on nested children.
    ArtifactDefaults.Policy defaultingPolicy =
      isRootPath(path) ? ArtifactDefaults.Policy.APPLY : ArtifactDefaults.Policy.PRESERVE;
    Optional<Version> version = ArtifactDefaults.version(readVersion(sourceNode, path, VERSION), defaultingPolicy);
    Optional<Status> status = ArtifactDefaults.status(readStatus(sourceNode, path, STATUS), defaultingPolicy);
    Optional<URI> previousVersion = readUri(sourceNode, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, DERIVED_FROM);
    Optional<URI> createdBy = readUri(sourceNode, path, CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDatetime(sourceNode, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDatetime(sourceNode, path, MODIFIED_ON);
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    List<String> childOrder = new ArrayList<>();
    LinkedHashMap<String, String> childLabels = new LinkedHashMap<>();
    LinkedHashMap<String, String> childDescriptions = new LinkedHashMap<>();
    readChildSchemas(sourceNode, path, fieldSchemas, elementSchemas, childOrder, childLabels, childDescriptions);
    Optional<String> language = readString(sourceNode, path, LANGUAGE);
    ElementUi elementUi = readElementUi(sourceNode, path, childOrder, childLabels, childDescriptions);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path);

    // isMultiple / minItems / maxItems / propertyUri are written by the renderer under the
    // `configuration:` sub-block for nested elements. Accept either location for
    // backwards-compat with hand-authored YAML and the existing reader tests.
    LinkedHashMap<String, Object> configNode = readChildNode(sourceNode, path, CONFIGURATION);
    LinkedHashMap<String, Object> configSource = configNode != null ? configNode : sourceNode;
    boolean isMultiple = readBoolean(configSource, path, MULTIPLE, false);
    Optional<Integer> minItems = readInteger(configSource, path, MIN_ITEMS);
    Optional<Integer> maxItems = readInteger(configSource, path, MAX_ITEMS);
    Optional<URI> propertyUri = readUri(configSource, path, PROPERTY_IRI);

    return ElementSchemaArtifact.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId,
      instanceJsonLdType, elementName, description, identifier, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn, fieldSchemas, elementSchemas, isMultiple, minItems, maxItems,
      propertyUri, language, elementUi, annotations);
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(LinkedHashMap<String, Object> sourceNode, String path)
  {
    String fieldName = readRequiredString(sourceNode, path, NAME, false);
    String internalName = fieldName + " field schema";
    String internalDescription = fieldName + " field schema generated by the CEDAR Artifact Library";
    // Static fields (page-break, section-break, richtext, image, youtube) have a
    // distinct JSON-LD @type and a different @context prefix-mappings table than
    // ordinary fields. Determine the input type up front so the @type and
    // @context match what the JSON reader / renderer expect for static content.
    FieldInputType fieldInputType = readFieldInputType(sourceNode, path, TYPE);
    boolean isStaticField = fieldInputType.isStatic();
    LinkedHashMap<String, URI> jsonLdContext = new LinkedHashMap<>(
      isStaticField ? STATIC_FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS
                    : FIELD_SCHEMA_ARTIFACT_CONTEXT_PREFIX_MAPPINGS);
    List<URI> jsonLdTypes = List.of(URI.create(
      isStaticField ? STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI : FIELD_SCHEMA_ARTIFACT_TYPE_IRI));
    Optional<URI> jsonLdId = readUri(sourceNode, path, ID);
    String description = readString(sourceNode, path, DESCRIPTION, "");
    Optional<String> identifier = readString(sourceNode, path, IDENTIFIER, true);
    // Default version/status on the top-level artifact only; preserve absence on nested children.
    ArtifactDefaults.Policy defaultingPolicy =
      isRootPath(path) ? ArtifactDefaults.Policy.APPLY : ArtifactDefaults.Policy.PRESERVE;
    Optional<Version> version = ArtifactDefaults.version(readVersion(sourceNode, path, VERSION), defaultingPolicy);
    Optional<Status> status = ArtifactDefaults.status(readStatus(sourceNode, path, STATUS), defaultingPolicy);
    Optional<URI> previousVersion = readUri(sourceNode, path, PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, DERIVED_FROM);
    Optional<URI> createdBy = readUri(sourceNode, path, CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDatetime(sourceNode, path, CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDatetime(sourceNode, path, MODIFIED_ON);
    LinkedHashMap<String, Object> configNode = readChildNode(sourceNode, path, CONFIGURATION);
    FieldUi fieldUi = readFieldUi(sourceNode, path, fieldInputType, configNode);
    Optional<ValueConstraints> valueConstraints = readValueConstraints(sourceNode, path, fieldInputType, configNode);
    Optional<String> preferredLabel = readString(sourceNode, path, PREF_LABEL);
    List<String> alternateLabels = readStringArray(sourceNode, path, ALT_LABEL);
    Optional<String> language = readString(sourceNode, path, LANGUAGE);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path);

    // isMultiple / minItems / maxItems / propertyUri live under the `configuration:` sub-block
    // for nested fields; top-level fields rendered by the renderer don't write configuration.
    LinkedHashMap<String, Object> configSource = configNode != null ? configNode : new LinkedHashMap<>();
    boolean isMultiple = readBoolean(configSource, path, MULTIPLE, false);
    Optional<Integer> minItems = readInteger(configSource, path, MIN_ITEMS);
    Optional<Integer> maxItems = readInteger(configSource, path, MAX_ITEMS);
    Optional<URI> propertyUri = readUri(configSource, path, PROPERTY_IRI);

    return FieldSchemaArtifact.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId,
      fieldName, description, identifier, version, status, previousVersion, derivedFrom, isMultiple, minItems, maxItems,
      propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn, preferredLabel, alternateLabels, language, fieldUi,
      valueConstraints, annotations);
  }

  private TemplateUi readTemplateUi(LinkedHashMap<String, Object> sourceNode, String path,
    List<String> derivedOrder, LinkedHashMap<String, String> derivedLabels,
    LinkedHashMap<String, String> derivedDescriptions)
  {
    // The renderer derives order/labels/descriptions from the children list and never writes them
    // as separate top-level keys; we accept the explicit keys for backwards-compat with hand-
    // authored YAML and fall back to the derived ones.
    List<String> explicitOrder = readStringArray(sourceNode, path, ORDER);
    List<String> order = explicitOrder.isEmpty() ? derivedOrder : explicitOrder;
    LinkedHashMap<String, String> propertyLabels = readString2StringMap(sourceNode, path, PROPERTY_LABELS);
    if (propertyLabels.isEmpty()) propertyLabels = derivedLabels;
    LinkedHashMap<String, String> propertyDescriptions = readString2StringMap(sourceNode, path, PROPERTY_DESCRIPTIONS);
    if (propertyDescriptions.isEmpty()) propertyDescriptions = derivedDescriptions;
    Optional<String> header = readString(sourceNode, path, HEADER);
    Optional<String> footer = readString(sourceNode, path, FOOTER);

    return TemplateUi.create(order, propertyLabels, propertyDescriptions, header, footer);
  }

  private ElementUi readElementUi(LinkedHashMap<String, Object> sourceNode, String path,
    List<String> derivedOrder, LinkedHashMap<String, String> derivedLabels,
    LinkedHashMap<String, String> derivedDescriptions)
  {
    List<String> explicitOrder = readStringArray(sourceNode, path, ORDER);
    List<String> order = explicitOrder.isEmpty() ? derivedOrder : explicitOrder;
    LinkedHashMap<String, String> propertyLabels = readString2StringMap(sourceNode, path, PROPERTY_LABELS);
    if (propertyLabels.isEmpty()) propertyLabels = derivedLabels;
    LinkedHashMap<String, String> propertyDescriptions = readString2StringMap(sourceNode, path, PROPERTY_DESCRIPTIONS);
    if (propertyDescriptions.isEmpty()) propertyDescriptions = derivedDescriptions;

    return ElementUi.create(order, propertyLabels, propertyDescriptions);
  }

  /**
   * Read the optional {@code annotations:} sub-map. Each entry is keyed by annotation name and
   * its value is a one-key map: {@code {value: <literal>}} for a literal annotation, or
   * {@code {id: <iri>}} for an IRI annotation. Mirrors the renderer at
   * {@link org.metadatacenter.artifacts.model.renderer.YamlArtifactRenderer#renderAnnotations}.
   */
  @SuppressWarnings("unchecked")
  private Optional<Annotations> readAnnotations(LinkedHashMap<String, Object> sourceNode, String path)
  {
    LinkedHashMap<String, Object> annotationsNode = readChildNode(sourceNode, path, ANNOTATIONS);
    if (annotationsNode == null)
      return Optional.empty();

    Annotations.Builder builder = Annotations.builder();
    for (Map.Entry<String, Object> entry : annotationsNode.entrySet()) {
      String annotationName = entry.getKey();
      Object rawValue = entry.getValue();
      String annotationPath = path + "/annotations/" + annotationName;
      if (!(rawValue instanceof LinkedHashMap<?, ?>))
        throw new ArtifactParseException("Expected map value for annotation " + annotationName,
          ANNOTATIONS, path);
      LinkedHashMap<String, Object> valueNode = (LinkedHashMap<String, Object>) rawValue;
      if (valueNode.containsKey(VALUE))
        builder.withLiteralAnnotation(annotationName, readRequiredString(valueNode, annotationPath, VALUE, true));
      else if (valueNode.containsKey(ID))
        builder.withIriAnnotation(annotationName, readRequiredUri(valueNode, annotationPath, ID));
      else
        throw new ArtifactParseException("Annotation " + annotationName + " must have either a `value:` or `id:` key",
          ANNOTATIONS, path);
    }
    return Optional.of(builder.build());
  }

  /**
   * Reads a boolean that may live either under a field's {@code configuration:} sub-block
   * (where the renderer puts it) or at the field level (where older or hand-authored YAML
   * sometimes does). Returns {@code false} if absent from both locations.
   */
  private boolean readBooleanFromConfigOrField(LinkedHashMap<String, Object> configNode,
    LinkedHashMap<String, Object> sourceNode, String path, String key)
  {
    if (configNode != null && configNode.containsKey(key))
      return readBoolean(configNode, path, key, false);
    return readBoolean(sourceNode, path, key, false);
  }

  private FieldUi readFieldUi(LinkedHashMap<String, Object> sourceNode, String path, FieldInputType fieldInputType,
    LinkedHashMap<String, Object> configNode)
  {
    // The renderer emits hidden / valueRecommendation / continuePreviousLine under the
    // `configuration:` sub-block for nested fields. Older YAML or hand-authored YAML may
    // place them at the field level. Accept either, preferring configuration when present.
    boolean valueRecommendationEnabled = readBooleanFromConfigOrField(configNode, sourceNode, path,
      VALUE_RECOMMENDATION);
    boolean hidden = readBooleanFromConfigOrField(configNode, sourceNode, path, HIDDEN);
    boolean continuePreviousLine = readBooleanFromConfigOrField(configNode, sourceNode, path, CONTINUE_PREVIOUS_LINE);
    Optional<Integer> width = readInteger(sourceNode, path, WIDTH);
    Optional<Integer> height = readInteger(sourceNode, path, HEIGHT);

    if (fieldInputType.isTemporal()) {
      TemporalGranularity temporalGranularity = readTemporalGranularity(sourceNode, path, GRANULARITY);
      Optional<InputTimeFormat> inputTimeFormat = readInputTimeFormat(sourceNode, path, INPUT_TIME_FORMAT);
      Optional<Boolean> timeZoneEnabled = readBoolean(sourceNode, path, INPUT_TIME_ZONE);

      return TemporalFieldUi.create(temporalGranularity, inputTimeFormat, timeZoneEnabled, hidden,
        continuePreviousLine);
    } else if (fieldInputType.isNumeric()) {
      return NumericFieldUi.create(hidden, continuePreviousLine);
    } else if (fieldInputType.isStatic()) {
      Optional<String> content = readString(sourceNode, path, CONTENT, true);
      return StaticFieldUi.create(fieldInputType, content, hidden, continuePreviousLine, width, height);
    } else {
      return FieldUi.create(fieldInputType, hidden, continuePreviousLine, valueRecommendationEnabled);
    }
  }

  /**
   * Read value constraints for a field. Unlike the JSON-Schema serialization (which nests
   * constraints under a {@code _valueConstraints} object), the YAML renderer inlines them at the
   * field level: {@code minLength}, {@code maxLength}, {@code regex}, {@code default},
   * {@code datatype}, {@code minValue}, {@code maxValue}, {@code decimalPlaces}, {@code unit},
   * plus a {@code values:} list for controlled-term / literal constraints and an {@code actions:}
   * list. {@code required}, {@code recommended}, and {@code multipleChoice} come from the
   * {@code configuration:} sub-block (when this field is a child of a template or element).
   */
  private Optional<ValueConstraints> readValueConstraints(LinkedHashMap<String, Object> sourceNode, String path,
    FieldInputType fieldInputType, LinkedHashMap<String, Object> configNode)
  {
    boolean requiredValue = configNode != null && readBoolean(configNode, path, REQUIRED, false);
    boolean recommendedValue = configNode != null && readBoolean(configNode, path, RECOMMENDED, false);
    // Checkbox is inherently multi-select. Lists can be either single or multi; the YAML
    // serialization encodes that via the multi-select-list-field vs single-select-list-field
    // discriminator. Both map to FieldInputType.LIST upstream, so we peek at the original
    // type string here to recover the multi-select bit.
    boolean isMultiSelectList =
      MULTI_SELECT_LIST_FIELD.equals(readString(sourceNode, path, TYPE).orElse(null));
    boolean multipleChoice = fieldInputType == FieldInputType.CHECKBOX || isMultiSelectList;

    Optional<String> unitOfMeasure = readString(sourceNode, path, UNIT);
    Optional<Number> minValue = readNumber(sourceNode, path, MIN_VALUE);
    Optional<Number> maxValue = readNumber(sourceNode, path, MAX_VALUE);
    Optional<Integer> decimalPlaces = readInteger(sourceNode, path, DECIMAL_PLACES);
    Optional<Integer> minLength = readInteger(sourceNode, path, MIN_LENGTH);
    Optional<Integer> maxLength = readInteger(sourceNode, path, MAX_LENGTH);
    Optional<String> regex = readString(sourceNode, path, REGEX);

    List<OntologyValueConstraint> ontologies = readOntologyValueConstraints(sourceNode, path);
    List<ValueSetValueConstraint> valueSets = readValueSetValueConstraints(sourceNode, path);
    List<ClassValueConstraint> classes = readClassValueConstraints(sourceNode, path);
    List<BranchValueConstraint> branches = readBranchValueConstraints(sourceNode, path);
    List<LiteralValueConstraint> literals = readLiteralValueConstraints(sourceNode, path);
    List<ControlledTermValueConstraintsAction> actions = readValueConstraintsActions(sourceNode, path);

    if (fieldInputType == FieldInputType.NUMERIC) {
      Optional<XsdNumericDatatype> numberType = readNumberType(sourceNode, path, DATATYPE);
      Optional<NumericDefaultValue> numericDefaultValue = readNumericDefaultValue(sourceNode, path);
      return Optional.of(NumericValueConstraints.create(numberType.orElse(XsdNumericDatatype.DECIMAL), minValue,
        maxValue, decimalPlaces, unitOfMeasure, numericDefaultValue, requiredValue, recommendedValue, multipleChoice));
    } else if (fieldInputType == FieldInputType.TEMPORAL) {
      Optional<XsdTemporalDatatype> temporalType = readTemporalType(sourceNode, path, DATATYPE);
      Optional<TemporalDefaultValue> temporalDefaultValue = readTemporalDefaultValue(sourceNode, path);
      return Optional.of(TemporalValueConstraints.create(temporalType.orElse(XsdTemporalDatatype.DATETIME),
        temporalDefaultValue, requiredValue, recommendedValue, multipleChoice));
    } else if (fieldInputType.isIri()) {
      // LINK and the ext-* identifier fields (ROR, ORCID, PFAS, RRID, PubMed, NIH grant id,
      // DOI) are all IRI-valued and carry LinkValueConstraints. Keying on isIri() rather than
      // == LINK ensures the ext-* fields round-trip: FieldSchemaArtifact.create dispatches them
      // to RorField/OrcidField/etc., whose builders require LinkValueConstraints (a plain
      // TextValueConstraints here throws a ClassCastException).
      Optional<LinkDefaultValue> linkDefaultValue = readLinkDefaultValue(sourceNode, path);
      return Optional.of(
        LinkValueConstraints.create(linkDefaultValue, requiredValue, recommendedValue, multipleChoice));
    } else if (fieldInputType == FieldInputType.TEXTFIELD && (
      !ontologies.isEmpty() || !valueSets.isEmpty() || !classes.isEmpty() || !branches.isEmpty())) {
      Optional<ControlledTermDefaultValue> controlledTermDefaultValue = readControlledTermDefaultValue(sourceNode, path);
      return Optional.of(
        ControlledTermValueConstraints.create(ontologies, valueSets, classes, branches, controlledTermDefaultValue,
          actions, requiredValue, recommendedValue, multipleChoice));
    } else {
      Optional<TextDefaultValue> textDefaultValue = readTextDefaultValue(sourceNode, path);
      return Optional.of(TextValueConstraints.create(minLength, maxLength, textDefaultValue, literals, requiredValue,
        recommendedValue, multipleChoice, regex));
    }
  }

  private String readRequiredString(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey,
    boolean allowEmpty)
  {
    if (!sourceNode.containsKey(fieldKey))
      throw new ArtifactParseException("No field present", fieldKey, path);

    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null)
      throw new ArtifactParseException("No value", fieldKey, path);

    if (!(rawValue instanceof String))
      throw new ArtifactParseException("Expecting string value, got " + rawValue.getClass(), fieldKey, path);

    String value = (String)rawValue;

    if (value.isEmpty() && !allowEmpty)
      throw new ArtifactParseException("Expecting non-empty string value", fieldKey, path);
    if (value.isEmpty() && !allowEmpty)
      throw new ArtifactParseException("Expecting non-empty string value", fieldKey, path);

    return value;
  }

  private String readString(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey, String defaultValue)
  {
    Optional<String> optionalString = readString(sourceNode, path, fieldKey, true);

    return optionalString.orElse(defaultValue);
  }

  private Optional<String> readString(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    return readString(sourceNode, path, fieldKey, true);
  }

  private Optional<String> readString(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey,
    boolean allowEmpty)
  {
    if (!sourceNode.containsKey(fieldKey))
      return Optional.empty();

    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null) {
      return Optional.empty();
    }

    if (!(rawValue instanceof String))
      throw new ArtifactParseException("Expecting string value, got " + rawValue.getClass(), fieldKey, path);

    String value = (String)rawValue;

    if (value.isEmpty() && !allowEmpty) {
      return Optional.empty();
    }

    return Optional.of(value);
  }

  private Optional<Integer> readInteger(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null) {
      return Optional.empty();
    }

    if (!(rawValue instanceof Integer))
      throw new ArtifactParseException("Value must be an integer", fieldKey, path);

    return Optional.of((Integer)rawValue);
  }

  private Optional<Number> readNumber(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null) {
      return Optional.empty();
    }

    if (!(rawValue instanceof Number))
      throw new ArtifactParseException("Value must be a number", fieldKey, path);

    return Optional.of((Number)rawValue);
  }

  private boolean readBoolean(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey,
    boolean defaultValue)
  {
    if (!sourceNode.containsKey(fieldKey))
      return defaultValue;

    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null) {
      return defaultValue;
    }

    if (!(rawValue instanceof Boolean))
      throw new ArtifactParseException("Expecting Boolean value, got " + rawValue.getClass(), fieldKey, path);

    return (Boolean)rawValue;
  }

  private Optional<Boolean> readBoolean(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    if (!sourceNode.containsKey(fieldKey))
      return Optional.empty();

    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null)
      return Optional.empty();

    if (!(rawValue instanceof Boolean))
      throw new ArtifactParseException("Expecting Boolean value, got " + rawValue.getClass(), fieldKey, path);

    return Optional.of((Boolean)rawValue);
  }

  private List<String> readStringArray(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    Object rawValue = sourceNode.get(fieldKey);
    List<String> stringValues = new ArrayList<>();

    if (rawValue != null) {
      if (rawValue instanceof List<?>) {
        List<?> rawListValues = (List<?>)rawValue;
        Iterator<?> rawListValueIterator = rawListValues.iterator();
        int arrayIndex = 0;
        while (rawListValueIterator.hasNext()) {
          Object rawListValue = rawListValueIterator.next();
          if (rawListValue instanceof String) {
            String stringValue = (String)rawListValue;
            if (!stringValue.isEmpty()) {
              stringValues.add(stringValue);
            } else
              throw new ArtifactParseException("Value in array at index " + arrayIndex + " must be a string", fieldKey,
                path);
            arrayIndex++;
          }
        }
      }
    }
    return stringValues;
  }

  private TemporalGranularity readTemporalGranularity(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldKey)
  {
    String granularityString = readEnumOrString(sourceNode, path, fieldKey)
      .orElseThrow(() -> new ArtifactParseException("No field present", fieldKey, path));

    if (!TEMPORAL_GRANULARITIES.contains(granularityString))
      throw new ArtifactParseException("Invalid granularity " + granularityString + " in field " + fieldKey, TYPE,
        path);

    return TemporalGranularity.fromString(granularityString);
  }

  private Optional<InputTimeFormat> readInputTimeFormat(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldKey)
  {
    Optional<String> inputTimeFormatString = readEnumOrString(sourceNode, path, fieldKey);

    if (inputTimeFormatString.isEmpty()) {
      return Optional.empty();
    }

    if (!TIME_FORMATS.contains(inputTimeFormatString.get()))
      throw new ArtifactParseException(
        "Invalid input time format " + inputTimeFormatString.get() + " in field " + fieldKey, TYPE, path);

    return Optional.of(InputTimeFormat.fromString(inputTimeFormatString.get()));
  }

  private FieldInputType readFieldInputType(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    String inputTypeString = readRequiredString(sourceNode, path, fieldKey, false);

    if (!FIELD_TYPES.contains(inputTypeString))
      throw new ArtifactParseException("Invalid field input type " + inputTypeString + " in field " + fieldKey, TYPE,
        path);

//    public static final Set<String> FIELD_TYPES = Set.of(TEXT_FIELD, CONTROLLED_TERM_FIELD, TEXT_AREA_FIELD,
//      NUMERIC_FIELD, TEMPORAL_FIELD, RADIO_FIELD, CHECKBOX_FIELD, SINGLE_SELECT_LIST_FIELD, MULTI_SELECT_LIST_FIELD,
//      PHONE_FIELD, EMAIL_FIELD, LINK_FIELD, ATTRIBUTE_VALUE_FIELD, STATIC_PAGE_BREAK, STATIC_SECTION_BREAK, STATIC_IMAGE,
//      STATIC_RICH_TEXT, STATIC_YOUTUBE_FIELD);

    if (inputTypeString.equals(TEXT_FIELD)) return FieldInputType.TEXTFIELD;
    if (inputTypeString.equals(CONTROLLED_TERM_FIELD)) return FieldInputType.TEXTFIELD;
    if (inputTypeString.equals(TEXT_AREA_FIELD)) return FieldInputType.TEXTAREA;
    if (inputTypeString.equals(NUMERIC_FIELD)) return FieldInputType.NUMERIC;
    if (inputTypeString.equals(TEMPORAL_FIELD)) return FieldInputType.TEMPORAL;
    if (inputTypeString.equals(RADIO_FIELD)) return FieldInputType.RADIO;
    if (inputTypeString.equals(CHECKBOX_FIELD)) return FieldInputType.CHECKBOX;
    if (inputTypeString.equals(SINGLE_SELECT_LIST_FIELD)) return FieldInputType.LIST;
    if (inputTypeString.equals(MULTI_SELECT_LIST_FIELD)) return FieldInputType.LIST;
    if (inputTypeString.equals(PHONE_FIELD)) return FieldInputType.PHONE_NUMBER;
    if (inputTypeString.equals(EMAIL_FIELD)) return FieldInputType.EMAIL;
    if (inputTypeString.equals(LINK_FIELD)) return FieldInputType.LINK;
    if (inputTypeString.equals(ROR_FIELD)) return FieldInputType.ROR;
    if (inputTypeString.equals(ORCID_FIELD)) return FieldInputType.ORCID;
    if (inputTypeString.equals(PFAS_FIELD)) return FieldInputType.PFAS;
    if (inputTypeString.equals(RRID_FIELD)) return FieldInputType.RRID;
    if (inputTypeString.equals(PUBMED_FIELD)) return FieldInputType.PUBMED;
    if (inputTypeString.equals(NIH_GRANT_ID_FIELD)) return FieldInputType.NIH_GRANT_ID;
    if (inputTypeString.equals(DOI_FIELD)) return FieldInputType.DOI;
    if (inputTypeString.equals(ATTRIBUTE_VALUE_FIELD)) return FieldInputType.ATTRIBUTE_VALUE;
    if (inputTypeString.equals(STATIC_PAGE_BREAK)) return FieldInputType.PAGE_BREAK;
    if (inputTypeString.equals(STATIC_SECTION_BREAK)) return FieldInputType.SECTION_BREAK;
    if (inputTypeString.equals(STATIC_RICH_TEXT)) return FieldInputType.RICHTEXT;
    if (inputTypeString.equals(STATIC_IMAGE)) return FieldInputType.IMAGE;
    if (inputTypeString.equals(STATIC_YOUTUBE_FIELD)) return FieldInputType.YOUTUBE;

    return FieldInputType.fromString(inputTypeString);
  }

  /** True for the top-level artifact's path; recursive child reads use a deeper path. */
  private static boolean isRootPath(String path)
  {
    return path.isEmpty() || "/".equals(path);
  }

  private Optional<Version> readVersion(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    Optional<String> versionString = readString(sourceNode, path, fieldKey, false);

    if (versionString.isEmpty()) {
      return Optional.empty();
    }

    if (Version.isValidVersion(versionString.get()))
      return Optional.of(Version.fromString(versionString.get()));
    else
      throw new ArtifactParseException("Invalid version " + versionString.get() + " in field " + fieldKey, fieldKey,
        path);
  }

  private Version readRequiredVersion(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    String versionString = readRequiredString(sourceNode, path, fieldKey, false);

    if (Version.isValidVersion(versionString))
      return Version.fromString(versionString);
    else
      throw new ArtifactParseException("Invalid version " + versionString, fieldKey, path);
  }

  /**
   * Read a value that may be either a {@link String} or an enum/object that resolves to a string
   * via {@link Object#toString()}. The renderer leaves some values (e.g. datatype enums) as enum
   * instances rather than strings in the in-memory map; YAML serialization later turns them into
   * strings. For an in-memory round-trip we need to accept both forms.
   */
  private Optional<String> readEnumOrString(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    if (!sourceNode.containsKey(fieldKey)) return Optional.empty();
    Object raw = sourceNode.get(fieldKey);
    if (raw == null) return Optional.empty();
    // Unwrap Optional<...> — the renderer occasionally puts an Optional in the map
    // (e.g. for inputTimeFormat) rather than its inner value.
    if (raw instanceof Optional<?>) {
      Optional<?> opt = (Optional<?>) raw;
      if (opt.isEmpty()) return Optional.empty();
      raw = opt.get();
    }
    if (raw instanceof String) return Optional.of((String) raw);
    if (raw instanceof Enum<?>) return Optional.of(raw.toString());
    throw new ArtifactParseException("Expecting string or enum value, got " + raw.getClass(), fieldKey, path);
  }

  private Optional<XsdNumericDatatype> readNumberType(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldKey)
  {
    return readEnumOrString(sourceNode, path, fieldKey).map(XsdNumericDatatype::fromString);
  }

  private Optional<XsdTemporalDatatype> readTemporalType(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldKey)
  {
    return readEnumOrString(sourceNode, path, fieldKey).map(XsdTemporalDatatype::fromString);
  }

  private Status readRequiredStatus(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    String statusString = readRequiredString(sourceNode, path, fieldKey, false);

    if (Status.isValidStatus(statusString))
      return Status.fromString(statusString);
    else
      throw new ArtifactParseException("Invalid status " + statusString, fieldKey, path);
  }

  private Optional<Status> readStatus(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    Optional<String> statusString = readString(sourceNode, path, fieldKey, false);

    if (statusString.isEmpty())
      return Optional.empty();

    if (statusString.get().equals(DRAFT_STATUS))
      return Optional.of(Status.DRAFT);
    else if (statusString.get().equals(PUBLISHED_STATUS))
      return Optional.of(Status.PUBLISHED);
    else
      throw new ArtifactParseException("Invalid status " + statusString.get(), fieldKey, path);
  }

  private Optional<XsdDatatype> readXsdDatatype(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    Optional<String> xsdDatatypeString = readString(sourceNode, path, fieldKey, false);

    if (xsdDatatypeString.isEmpty())
      return Optional.empty();

    if (XsdDatatype.isKnownXsdDatatype(xsdDatatypeString.get()))
      return Optional.of(XsdDatatype.fromString(xsdDatatypeString.get()));
    else
      throw new ArtifactParseException("Invalid status " + xsdDatatypeString.get(), fieldKey, path);
  }

  private URI readRequiredUri(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    if (!sourceNode.containsKey(fieldKey))
      throw new ArtifactParseException("Expecting URI field", fieldKey, path);

    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null)
      throw new ArtifactParseException("Expecting URI field", fieldKey, path);

    if (rawValue instanceof URI)
      return (URI)rawValue;
    else if (rawValue instanceof String) {
      try {
        return new URI((String)rawValue);
      } catch (URISyntaxException e) {
        throw new ArtifactParseException("Invalid URI " + rawValue, fieldKey, path);
      }
    } else
      throw new ArtifactParseException("Expecting URI or string value, got " + rawValue.getClass(), fieldKey, path);
  }

  private Optional<URI> readUri(LinkedHashMap<String, Object> sourceNode, String path, String fieldKey)
  {
    if (!sourceNode.containsKey(fieldKey))
      return Optional.empty();

    Object rawValue = sourceNode.get(fieldKey);

    if (rawValue == null)
      return Optional.empty();

    if (rawValue instanceof URI)
      return Optional.of((URI)rawValue);
    else if (rawValue instanceof String) {
      try {
        return Optional.of(new URI((String)rawValue));
      } catch (URISyntaxException e) {
        throw new ArtifactParseException("Invalid URI " + rawValue, fieldKey, path);
      }
    } else
      throw new ArtifactParseException("Expecting URI or string value, got " + rawValue.getClass(), fieldKey, path);
  }

  private Optional<OffsetDateTime> readOffsetDatetime(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldKey)
  {
    Optional<String> dateTimeValue = readString(sourceNode, path, fieldKey, false);

    try {
      if (dateTimeValue.isPresent()) {
        return Optional.of(OffsetDateTimes.parse(dateTimeValue.get()));
      } else {
        return Optional.empty();
      }
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException("Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(),
        fieldKey, path);
    }
  }

  /**
   * Read each entry of the {@code values:} list at {@code sourceNode}, dispatching to the supplied
   * mapper for entries whose {@code type} discriminator matches {@code typeDiscriminator}. Entries
   * that don't match (e.g. {@code literal:}-style entries when reading ontology constraints) are
   * silently skipped.
   */
  @SuppressWarnings("unchecked")
  private <T> List<T> readValuesEntriesOfType(LinkedHashMap<String, Object> sourceNode, String path,
    String typeDiscriminator, java.util.function.Function<LinkedHashMap<String, Object>, T> mapper)
  {
    Object raw = sourceNode.get(VALUES);
    if (!(raw instanceof List<?>))
      return Collections.emptyList();
    List<T> result = new ArrayList<>();
    for (Object rawEntry : (List<?>) raw) {
      if (!(rawEntry instanceof LinkedHashMap<?, ?>)) continue;
      LinkedHashMap<String, Object> entry = (LinkedHashMap<String, Object>) rawEntry;
      Object t = entry.get(TYPE);
      if (typeDiscriminator.equals(t))
        result.add(mapper.apply(entry));
    }
    return result;
  }

  private List<OntologyValueConstraint> readOntologyValueConstraints(LinkedHashMap<String, Object> sourceNode,
    String path)
  {
    return readValuesEntriesOfType(sourceNode, path, ONTOLOGY, entry -> {
      String acronym = readRequiredString(entry, path, ACRONYM, false);
      String name = readRequiredString(entry, path, ONTOLOGY_NAME, false);
      URI iri = readRequiredUri(entry, path, IRI);
      Optional<Integer> numTerms = readInteger(entry, path, NUM_TERMS);
      return new OntologyValueConstraint(iri, acronym, name, numTerms);
    });
  }

  private List<ClassValueConstraint> readClassValueConstraints(LinkedHashMap<String, Object> sourceNode, String path)
  {
    return readValuesEntriesOfType(sourceNode, path, CLASS, entry -> {
      String label = readRequiredString(entry, path, LABEL, false);
      String acronym = readRequiredString(entry, path, ACRONYM, false);
      String termType = readRequiredString(entry, path, TERM_TYPE, false);
      String termLabel = readRequiredString(entry, path, TERM_LABEL, false);
      URI iri = readRequiredUri(entry, path, IRI);
      ValueType valueType = termType.equalsIgnoreCase(CLASS)
        ? ValueType.ONTOLOGY_CLASS
        : ValueType.VALUE;
      return new ClassValueConstraint(iri, acronym, label, termLabel, valueType);
    });
  }

  private List<ValueSetValueConstraint> readValueSetValueConstraints(LinkedHashMap<String, Object> sourceNode,
    String path)
  {
    return readValuesEntriesOfType(sourceNode, path, VALUE_SET, entry -> {
      String acronym = readRequiredString(entry, path, ACRONYM, false);
      String name = readRequiredString(entry, path, VALUE_SET_NAME, false);
      URI iri = readRequiredUri(entry, path, IRI);
      Optional<Integer> numTerms = readInteger(entry, path, NUM_TERMS);
      return new ValueSetValueConstraint(iri, acronym, name, numTerms);
    });
  }

  private List<BranchValueConstraint> readBranchValueConstraints(LinkedHashMap<String, Object> sourceNode, String path)
  {
    return readValuesEntriesOfType(sourceNode, path, BRANCH, entry -> {
      String ontologyName = readRequiredString(entry, path, ONTOLOGY_NAME, false);
      String acronym = readRequiredString(entry, path, ACRONYM, false);
      String termLabel = readRequiredString(entry, path, TERM_LABEL, false);
      URI iri = readRequiredUri(entry, path, IRI);
      Optional<Integer> maxDepth = readInteger(entry, path, MAX_DEPTH);
      return new BranchValueConstraint(iri, ontologyName, acronym, termLabel, maxDepth.orElse(0));
    });
  }

  /**
   * Literal entries don't have a {@code type:} discriminator; they're distinguished by carrying
   * a {@code literal:} key.
   */
  @SuppressWarnings("unchecked")
  private List<LiteralValueConstraint> readLiteralValueConstraints(LinkedHashMap<String, Object> sourceNode,
    String path)
  {
    Object raw = sourceNode.get(VALUES);
    if (!(raw instanceof List<?>))
      return Collections.emptyList();
    List<LiteralValueConstraint> result = new ArrayList<>();
    for (Object rawEntry : (List<?>) raw) {
      if (!(rawEntry instanceof LinkedHashMap<?, ?>)) continue;
      LinkedHashMap<String, Object> entry = (LinkedHashMap<String, Object>) rawEntry;
      if (entry.containsKey(LITERAL)) {
        String label = readRequiredString(entry, path, LITERAL, false);
        boolean selected = readBoolean(entry, path, SELECTED_BY_DEFAULT, false);
        result.add(new LiteralValueConstraint(label, selected));
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private List<ControlledTermValueConstraintsAction> readValueConstraintsActions(
    LinkedHashMap<String, Object> sourceNode, String path)
  {
    Object raw = sourceNode.get(ACTIONS);
    if (!(raw instanceof List<?>))
      return Collections.emptyList();
    List<ControlledTermValueConstraintsAction> result = new ArrayList<>();
    for (Object rawEntry : (List<?>) raw) {
      if (!(rawEntry instanceof LinkedHashMap<?, ?>)) continue;
      LinkedHashMap<String, Object> entry = (LinkedHashMap<String, Object>) rawEntry;

      String actionName = readRequiredString(entry, path, ACTION, false);
      ValueConstraintsActionType actionType = actionName.equalsIgnoreCase(DELETE_ACTION)
        ? ValueConstraintsActionType.DELETE
        : ValueConstraintsActionType.MOVE;
      Optional<Integer> to = readInteger(entry, path, ACTION_TO);
      URI termIri = readRequiredUri(entry, path, TERM_IRI);
      Optional<URI> sourceIri = readUri(entry, path, SOURCE_IRI);
      String sourceAcronym = readRequiredString(entry, path, SOURCE_ACRONYM, false);
      String typeName = readRequiredString(entry, path, TYPE, false);
      ValueType valueType = typeName.equalsIgnoreCase(CLASS) ? ValueType.ONTOLOGY_CLASS : ValueType.VALUE;

      result.add(new ControlledTermValueConstraintsAction(termIri, sourceAcronym, valueType, actionType, sourceIri, to));
    }
    return result;
  }

  private Optional<TextDefaultValue> readTextDefaultValue(LinkedHashMap<String, Object> sourceNode, String path)
  {
    Optional<String> raw = readString(sourceNode, path, DEFAULT);
    return raw.map(TextDefaultValue::new);
  }

  private Optional<NumericDefaultValue> readNumericDefaultValue(LinkedHashMap<String, Object> sourceNode, String path)
  {
    // The canonical YAML form for a numeric default is a quoted string — see the
    // renderer comment for why. Accept either a string (canonical) or a number
    // (legacy YAML files where the value was written bare). Parse the string into
    // a Long if it has no fractional part, Double otherwise; the model compares
    // NumericDefaultValue by canonical string form so the choice of Number subtype
    // doesn't affect equality.
    Object raw = sourceNode.get(DEFAULT);
    if (raw == null) return Optional.empty();
    if (raw instanceof Number) return Optional.of(new NumericDefaultValue((Number) raw));
    if (raw instanceof String) {
      String text = ((String) raw).trim();
      if (text.isEmpty()) return Optional.empty();
      // A ternary returning Double-or-Long would trigger binary numeric promotion
      // (unbox to primitives, promote to double, re-box) — silently widening every
      // integer to Double. The if/else keeps the Number subtype distinct.
      Number parsed;
      try {
        if (text.contains(".") || text.contains("e") || text.contains("E"))
          parsed = Double.valueOf(text);
        else
          parsed = Long.valueOf(text);
      } catch (NumberFormatException e) {
        throw new ArtifactParseException(
          "default for numeric field is not a valid number: '" + text + "'", DEFAULT, path);
      }
      return Optional.of(new NumericDefaultValue(parsed));
    }
    throw new ArtifactParseException(
      "default for numeric field must be a number or numeric string, got " + raw.getClass().getSimpleName(),
      DEFAULT, path);
  }

  private Optional<LinkDefaultValue> readLinkDefaultValue(LinkedHashMap<String, Object> sourceNode, String path)
  {
    return readUri(sourceNode, path, DEFAULT).map(LinkDefaultValue::new);
  }

  private Optional<TemporalDefaultValue> readTemporalDefaultValue(LinkedHashMap<String, Object> sourceNode, String path)
  {
    Optional<String> raw = readString(sourceNode, path, DEFAULT);
    return raw.map(TemporalDefaultValue::new);
  }

  /**
   * The controlled-term default is a map with {@code value:} (an IRI) and {@code label:} keys.
   */
  private Optional<ControlledTermDefaultValue> readControlledTermDefaultValue(
    LinkedHashMap<String, Object> sourceNode, String path)
  {
    LinkedHashMap<String, Object> defaultNode = readChildNode(sourceNode, path, DEFAULT);
    if (defaultNode == null)
      return Optional.empty();
    URI uri = readRequiredUri(defaultNode, path, DEFAULT_VALUE);
    String label = readRequiredString(defaultNode, path, DEFAULT_LABEL, false);
    return Optional.of(new ControlledTermDefaultValue(uri, label));
  }

  private LinkedHashMap<String, String> readString2StringMap(LinkedHashMap<String, Object> sourceNode, String path,
    String fieldKey)
  {
    LinkedHashMap<String, Object> raw = readChildNode(sourceNode, path, fieldKey);
    if (raw == null)
      return new LinkedHashMap<>();

    LinkedHashMap<String, String> result = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : raw.entrySet()) {
      if (entry.getValue() instanceof String)
        result.put(entry.getKey(), (String) entry.getValue());
      else
        throw new ArtifactParseException("Expected string value at key " + entry.getKey(), fieldKey, path);
    }
    return result;
  }

  /**
   * Returns the child map at {@code parentNode[fieldKey]} or {@code null} if the field is absent
   * or {@code null}-valued.
   */
  @SuppressWarnings("unchecked")
  private LinkedHashMap<String, Object> readChildNode(LinkedHashMap<String, Object> parentNode, String path,
    String fieldKey)
  {
    Object raw = parentNode.get(fieldKey);
    if (raw == null)
      return null;
    if (!(raw instanceof LinkedHashMap<?, ?>))
      throw new ArtifactParseException("Expected map value, got " + raw.getClass(), fieldKey, path);
    return (LinkedHashMap<String, Object>) raw;
  }

  /**
   * Walk a YAML list under {@code fieldKey}, dispatching each entry to the appropriate reader
   * (field vs element) by its {@code type} discriminator. Populates the supplied maps with the
   * parsed children, builds the {@code order} list in document order, and builds
   * {@code propertyLabels} / {@code propertyDescriptions} from each child's name/description
   * (mirroring what the builder does when assembling templates/elements in code).
   */
  @SuppressWarnings("unchecked")
  private void readChildSchemas(LinkedHashMap<String, Object> sourceNode, String path,
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas,
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas,
    List<String> order,
    LinkedHashMap<String, String> propertyLabels,
    LinkedHashMap<String, String> propertyDescriptions)
  {
    Object raw = sourceNode.get(CHILDREN);
    if (raw == null)
      return;
    if (!(raw instanceof List<?>))
      throw new ArtifactParseException("Expected list value for children", CHILDREN, path);

    for (Object rawChild : (List<?>) raw) {
      if (!(rawChild instanceof LinkedHashMap<?, ?>))
        throw new ArtifactParseException("Expected map value in children list", CHILDREN, path);
      LinkedHashMap<String, Object> childNode = (LinkedHashMap<String, Object>) rawChild;

      String childKey = readRequiredString(childNode, path, KEY, false);
      String childType = readRequiredString(childNode, path, TYPE, false);
      String childPath = path + "/" + childKey;

      // Read override-label / override-description from the child's configuration block if present;
      // otherwise default to the child's own name / description.
      LinkedHashMap<String, Object> childConfig = readChildNode(childNode, childPath, CONFIGURATION);
      String overrideLabel = childConfig != null
        ? readString(childConfig, childPath, OVERRIDE_LABEL).orElse(null)
        : null;
      String overrideDescription = childConfig != null
        ? readString(childConfig, childPath, OVERRIDE_DESCRIPTION).orElse(null)
        : null;

      if (childType.equals(ELEMENT)) {
        ElementSchemaArtifact element = readElementSchemaArtifact(childNode, childPath);
        elementSchemas.put(childKey, element);
        order.add(childKey);
        propertyLabels.put(childKey, overrideLabel != null ? overrideLabel : element.name());
        propertyDescriptions.put(childKey, overrideDescription != null ? overrideDescription : element.description());
      } else if (FIELD_TYPES.contains(childType)) {
        FieldSchemaArtifact field = readFieldSchemaArtifact(childNode, childPath);
        fieldSchemas.put(childKey, field);
        order.add(childKey);
        propertyLabels.put(childKey, overrideLabel != null ? overrideLabel : field.name());
        propertyDescriptions.put(childKey, overrideDescription != null ? overrideDescription : field.description());
      } else {
        throw new ArtifactParseException("Unknown child type " + childType, TYPE, childPath);
      }
    }
  }

  private void checkSchemaArtifactModelVersion(LinkedHashMap<String, Object> sourceNode, String path)
  {
    if (isCompact && !sourceNode.containsKey(MODEL_VERSION))
      return;

    Version artifactModelVersion = readRequiredVersion(sourceNode, path, MODEL_VERSION);

    if (!artifactModelVersion.equals(modelVersion)) {
      throw new ArtifactParseException("Expecting model version " + modelVersion + ", got " + artifactModelVersion,
        MODEL_VERSION, path);
    }
  }

}
