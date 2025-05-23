package org.metadatacenter.artifacts.model.yaml;

import java.util.Set;

public class YamlConstants
{
  public static final String TEMPLATE = "template";
  public static final String ELEMENT = "element";
  public static final String TEXT_FIELD = "text-field";
  public static final String CONTROLLED_TERM_FIELD = "controlled-term-field";
  public static final String TEXT_AREA_FIELD = "text-area-field";
  public static final String NUMERIC_FIELD = "numeric-field";
  public static final String TEMPORAL_FIELD = "temporal-field";
  public static final String RADIO_FIELD = "radio-field";
  public static final String CHECKBOX_FIELD = "checkbox-field";
  public static final String SINGLE_SELECT_LIST_FIELD = "single-select-list-field";
  public static final String MULTI_SELECT_LIST_FIELD = "multi-select-list-field";
  public static final String PHONE_FIELD = "phone-number-field";
  public static final String EMAIL_FIELD = "email-field";
  public static final String LINK_FIELD = "link-field";
  public static final String ROR_FIELD = "ror-field";
  public static final String ORCID_FIELD = "orcid-field";
  public static final String ATTRIBUTE_VALUE_FIELD = "attribute-value-field";
  public static final String STATIC_PAGE_BREAK = "static-page-break";
  public static final String STATIC_SECTION_BREAK = "static-section-break";
  public static final String STATIC_IMAGE = "static-image";
  public static final String STATIC_RICH_TEXT = "static-rich-text";
  public static final String STATIC_YOUTUBE_FIELD = "static-youtube-video";

  public static final Set<String> FIELD_TYPES = Set.of(TEXT_FIELD, CONTROLLED_TERM_FIELD, TEXT_AREA_FIELD,
    NUMERIC_FIELD, TEMPORAL_FIELD, RADIO_FIELD, CHECKBOX_FIELD, SINGLE_SELECT_LIST_FIELD, MULTI_SELECT_LIST_FIELD,
    PHONE_FIELD, EMAIL_FIELD, ATTRIBUTE_VALUE_FIELD, STATIC_PAGE_BREAK, STATIC_SECTION_BREAK, STATIC_IMAGE,
    STATIC_RICH_TEXT, STATIC_YOUTUBE_FIELD, LINK_FIELD, ROR_FIELD, ORCID_FIELD);

  public static final String KEY = "key";
  public static final String NAME = "name";
  public static final String TYPE = "type";
  public static final String CHILDREN = "children";
  public static final String INSTANCE = "instance";
  public static final String DESCRIPTION = "description";
  public static final String IDENTIFIER = "identifier";

  public static final String ID = "id";
  public static final String VERSION = "version";
  public static final String STATUS = "status";
  public static final String DRAFT_STATUS = "draft";
  public static final String PUBLISHED_STATUS = "published";
  public static final String MODEL_VERSION = "modelVersion";
  public static final String PREVIOUS_VERSION = "previousVersion";
  public static final String IS_BASED_ON = "isBasedOn";
  public static final String DERIVED_FROM = "derivedFrom";
  public static final String CREATED_BY = "createdBy";
  public static final String CREATED_ON = "createdOn";
  public static final String MODIFIED_BY = "modifiedBy";
  public static final String MODIFIED_ON = "modifiedOn";

  public static final String LABEL = "label";
  public static final String PREF_LABEL = "prefLabel";
  public static final String NOTATION = "notation";
  public static final String ALT_LABEL = "altLabels";
  public static final String LANGUAGE = "language";
  public static final String CONTINUE_PREVIOUS_LINE = "continuePreviousLine";
  public static final String DATATYPE = "datatype";
  public static final String IRI = "iri";
  public static final String GRANULARITY = "granularity";
  public static final String INPUT_TIME_FORMAT = "inputTimeFormat";
  public static final String INPUT_TIME_ZONE = "inputTimeZone";
  public static final String UNIT = "unit";
  public static final String DEFAULT = "default";
  public static final String DEFAULT_VALUE = "value";
  public static final String DEFAULT_LABEL = "label";
  public static final String MIN_VALUE = "minValue";
  public static final String MAX_VALUE = "maxValue";
  public static final String REGEX = "regex";
  public static final String DECIMAL_PLACES = "decimalPlaces";
  public static final String MIN_LENGTH = "minLength";
  public static final String MAX_LENGTH = "maxLength";
  public static final String URI = "termUri";
  public static final String VALUES = "values";
  public static final String ACTIONS = "actions";
  public static final String ACRONYM = "acronym";
  public static final String ONTOLOGY_NAME = "ontologyName";
  public static final String VALUE_SET_NAME = "valueSetName";
  public static final String TERM_TYPE = "termType";
  public static final String TERM_LABEL = "termLabel";
  public static final String ONTOLOGY = "ontology";
  public static final String CLASS = "class";
  public static final String BRANCH = "branch";
  public static final String VALUE_SET = "valueSet";
  public static final String LITERAL = "label";
  public static final String ACTION = "action";
  public static final String DELETE_ACTION = "delete";
  public static final String MOVE_ACTION = "move";
  public static final String ACTION_TO = "to";
  public static final String TERM_IRI = "termIri";
  public static final String SOURCE_ACRONYM = "sourceAcronym";
  public static final String SOURCE_IRI = "sourceIri";
  public static final String SELECTED_BY_DEFAULT = "selected";
  public static final String MAX_DEPTH = "maxDepth";
  public static final String NUM_TERMS = "numTerms";
  public static final String CONTENT = "content";
  public static final String WIDTH = "width";
  public static final String HEIGHT = "height";
  public static final String HEADER = "header";
  public static final String FOOTER = "footer";
  public static final String ORDER = "order";
  public static final String ANNOTATIONS = "annotations";
  public static final String VALUE = "value";

  public static final String CONFIGURATION = "configuration";
  public static final String MULTIPLE = "multiple";
  public static final String PROPERTY_LABELS = "propertyLabels";
  public static final String PROPERTY_DESCRIPTIONS = "propertyDescriptions";
  public static final String MIN_ITEMS = "minItems";
  public static final String MAX_ITEMS = "maxItems";
  public static final String PROPERTY_IRI = "propertyIri";
  public static final String REQUIRED = "required";
  public static final String RECOMMENDED = "recommended";
  public static final String HIDDEN = "hidden";
  public static final String VALUE_RECOMMENDATION = "valueRecommendation";
  public static final String OVERRIDE_LABEL = "overrideLabel";
  public static final String OVERRIDE_DESCRIPTION = "overrideDescription";
}
