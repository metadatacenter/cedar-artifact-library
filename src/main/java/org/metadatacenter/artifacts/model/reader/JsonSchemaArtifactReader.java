package org.metadatacenter.artifacts.model.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.AnnotationValue;
import org.metadatacenter.artifacts.model.core.Annotations;
import org.metadatacenter.artifacts.model.core.ElementInstanceArtifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldInstanceArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.IriAnnotationValue;
import org.metadatacenter.artifacts.model.core.LiteralAnnotationValue;
import org.metadatacenter.artifacts.model.core.Status;
import org.metadatacenter.artifacts.model.core.TemplateInstanceArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.artifacts.model.core.Version;
import org.metadatacenter.artifacts.model.core.fields.ControlledTermDefaultValue;
import org.metadatacenter.artifacts.model.core.fields.DefaultValue;
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.LinkDefaultValue;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.metadatacenter.model.ModelNodeNames.ANNOTATIONS;
import static org.metadatacenter.model.ModelNodeNames.ARTIFACT_CONTEXT_ENTRIES;
import static org.metadatacenter.model.ModelNodeNames.BIBO_STATUS;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_INSTANCE_ARTIFACT_KEYWORDS;
import static org.metadatacenter.model.ModelNodeNames.ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INSTANCE_ARTIFACT_KEYWORDS;
import static org.metadatacenter.model.ModelNodeNames.FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.INPUT_TYPES;
import static org.metadatacenter.model.ModelNodeNames.INSTANCE_ARTIFACT_KEYWORDS;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_CONTEXT;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_LANGUAGE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_TYPE;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_VALUE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ARRAY;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ENUM;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MAX_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_MIN_ITEMS;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_OBJECT;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_ONE_OF;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_PROPERTIES;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_SCHEMA;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TITLE;
import static org.metadatacenter.model.ModelNodeNames.JSON_SCHEMA_TYPE;
import static org.metadatacenter.model.ModelNodeNames.OSLC_MODIFIED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_BY;
import static org.metadatacenter.model.ModelNodeNames.PAV_CREATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_DERIVED_FROM;
import static org.metadatacenter.model.ModelNodeNames.PAV_LAST_UPDATED_ON;
import static org.metadatacenter.model.ModelNodeNames.PAV_PREVIOUS_VERSION;
import static org.metadatacenter.model.ModelNodeNames.PAV_VERSION;
import static org.metadatacenter.model.ModelNodeNames.RDFS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ARTIFACT_TYPE_IRIS;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_IS_BASED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_IDENTIFIER;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_NOTATION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.UI_CONTENT;
import static org.metadatacenter.model.ModelNodeNames.UI_CONTINUE_PREVIOUS_LINE;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;
import static org.metadatacenter.model.ModelNodeNames.UI_FOOTER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEADER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEIGHT;
import static org.metadatacenter.model.ModelNodeNames.UI_HIDDEN;
import static org.metadatacenter.model.ModelNodeNames.UI_INPUT_TIME_FORMAT;
import static org.metadatacenter.model.ModelNodeNames.UI_ORDER;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_LABELS;
import static org.metadatacenter.model.ModelNodeNames.UI_RECOMMENDED_VALUE;
import static org.metadatacenter.model.ModelNodeNames.UI_TEMPORAL_GRANULARITY;
import static org.metadatacenter.model.ModelNodeNames.UI_TIMEZONE_ENABLED;
import static org.metadatacenter.model.ModelNodeNames.UI_VALUE_RECOMMENDATION_ENABLED;
import static org.metadatacenter.model.ModelNodeNames.UI_WIDTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACRONYM;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTION;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTIONS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ACTION_TO;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_CLASSES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DECIMAL_PLACE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_DEFAULT_VALUE_TERM_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_LITERALS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_DEPTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MAX_STRING_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MIN_STRING_LENGTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_MULTIPLE_CHOICE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NAME;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_RECOMMENDED_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_REQUIRED_VALUE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SELECTED_BY_DEFAULT;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_SOURCE_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TEMPORAL_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TERM_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_TYPE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_UNIT_OF_MEASURE;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_URI;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS_VS_COLLECTION;
import static org.metadatacenter.model.ModelNodeValues.TEMPORAL_GRANULARITIES;
import static org.metadatacenter.model.ModelNodeValues.TIME_FORMATS;

public class JsonSchemaArtifactReader implements ArtifactReader<ObjectNode>
{
  public JsonSchemaArtifactReader()
  {
  }

  /**
   * Read a JSON Schema specification for a template schema artifact
   * <p></p>
   * An example template schema artifact specification could look as follows:
   * <pre>
   * {
   *   "$schema": "http://json-schema.org/draft-04/schema#",
   *   "type": "object",
   *   "title": "Study template schema", "description": "Study template schema generated by the CEDAR Artifact Library",
   *   "schema:name": "Study", "schema:description": "Study template",
   *   "schema:schemaVersion": "1.6.0", "schema:identifier": "id1212132",
   *   "pav:version": "0.0.1", "bibo:status": "bibo:draft",
   *   "pav:previousVersion": "https://repo.metadatacenter.org/templates/54343",
   *   "pav:derivedFrom": "https://repo.metadatacenter.org/templates/232323",
   *   "@id": "https://repo.metadatacenter.org/templates/474378",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332",
   *   "@context": { ... },
   *   "properties": { ... },
   *   "required": [ "@context", "@id",
   *                 "schema:isBasedOn", "schema:name", "schema:description",
   *                 "pav:createdOn", "pav:createdBy", "pav:lastUpdatedOn", "oslc:modifiedBy",
   *                 "Child Name 1", ... "Child Name n"],
   *   "additionalProperties": false,
   *   "_ui": { ... }
   *  }
   * </pre>
   */
  public TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode sourceNode)
  {
    return readTemplateSchemaArtifact(sourceNode, "");
  }

  /**
   * Read a JSON Schema specification for an element schema artifact
   * <p></p>
   * An example JSON Schema element artifact specification could look as follows:
   * <pre>
   * {
   *   "$schema": "http://json-schema.org/draft-04/schema#",
   *   "type": "object",
   *   "title": "Address element schema", "description: "Address element schema generated by the CEDAR Template Editor 2.6.19",
   *   "schema:name": "Address", "schema:description": "Address element",
   *   "schema:schemaVersion": "1.6.0", "schema:identifier": "id999434",
   *   "pav:version": "0.0.1", "bibo:status": "bibo:draft",
   *   "pav:previousVersion": "https://repo.metadatacenter.org/template-elements/54343",
   *   "pav:derivedFrom": "https://repo.metadatacenter.org/template-elements/232323",
   *   "@type": "https://schema.metadatacenter.org/core/TemplateElement",
   *   "@id": "https://repo.metadatacenter.org/templates-elements/474378",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332",
   *   "@context": { ... },
   *   "properties": { ... },
   *   "required": [ "@context", "@id", "Child Name 1", ... "Child Name n"],
   *   "additionalProperties": false,
   *   "_ui": { ... }
   *  }
   * </pre>
   */
  public ElementSchemaArtifact readElementSchemaArtifact(ObjectNode sourceNode)
  {
    String name = readRequiredString(sourceNode, "/", SCHEMA_ORG_NAME);
    return readElementSchemaArtifact(sourceNode, "", name, false, Optional.empty(), Optional.empty(), Optional.empty());
  }

  /**
   * Read a JSON Schema specification for a field schema artifact
   * <p></p>
   * An example JSON Schema field artifact could look as follows:
   * <pre>
   * {
   *   "$schema": "http://json-schema.org/draft-04/schema#",
   *   "type": "object",
   *   "title": "Disease field schema", "description": "Disease field schema generated by the CEDAR Template Editor 2.6.19",
   *   "schema:name": "Disease", "schema:description": "Please enter a disease",
   *   "schema:schemaVersion": "1.6.0", "schema:identifier": "id5666",
   *   "pav:version": "0.0.1", "bibo:status": "bibo:draft",
   *   "pav:previousVersion": "https://repo.metadatacenter.org/templates/435454",
   *   "pav:derivedFrom": "https://repo.metadatacenter.org/templates/893443",
   *   "@type": "https://schema.metadatacenter.org/core/Template",
   *   "@id": "https://repo.metadatacenter.org/templates/127666",
   *   "pav:createdOn": "2023-07-28T11:10:41-07:00", "pav:createdBy": "https://metadatacenter.org/users/656433",
   *   "pav:lastUpdatedOn": "2023-07-28T11:10:41-07:00", "oslc:modifiedBy": "https://metadatacenter.org/users/524332",
   *   "@context": { ... },
   *   "properties": { ... },
   *   "required": [ "@id" ],
   *   "additionalProperties": false,
   *   "skos:prefLabel": "Condition", "skos:altLabel": [ "Problem", "Illness" ],
   *   "_valueConstraints": { ... },
   *   "_ui": { ... }
   *  }
   * </pre>
   */
  public FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode sourceNode)
  {
    String name = readRequiredString(sourceNode, "/", SCHEMA_ORG_NAME);
    return readFieldSchemaArtifact(sourceNode, "", name, false, Optional.empty(), Optional.empty(), Optional.empty());
  }

  /**
   * Read a JSON-LD template instance artifact
   * <p></p>
   * An example template instance artifact could look as follows:
   * <pre>
   *   {
   *   "@context": {
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "pav": "http://purl.org/pav/",
   *     "schema": "http://schema.org/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "schema:name": {"@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:derivedFrom": { "@type": "@id" },
   *     "pav:createdOn": { "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "pav:lastUpdatedOn": { "@type": "xsd:dateTime" },
   *     "oslc:modifiedBy": { "@type": "@id" },
   *     "schema:isBasedOn": { "@type": "@id" },
   *     "skos:notation": { "@type": "xsd:string" },
   *     "rdfs:label": { "@type": "xsd:string" }
   *   },
   *   "@id": "https://repo.metadatacenter.org/template-instances/66776767"
   *   "schema:isBasedOn": "https://repo.metadatacenter.org/templates/5454545",
   *   "schema:name": "Study metadata", "schema:description": "",
   *   "pav:createdOn": "2023-08-01T11:03:05-07:00",
   *   "pav:createdBy": "https://metadatacenter.org/users/344343",
   *   "pav:lastUpdatedOn": "2023-08-01T11:03:05-07:00",
   *   "oslc:modifiedBy": "https://metadatacenter.org/users/5666565"
   * }
   * </pre>
   */
  public TemplateInstanceArtifact readTemplateInstanceArtifact(ObjectNode sourceNode)
  {
    return readTemplateInstanceArtifact(sourceNode, "");
  }

  private TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode sourceNode, String path)
  {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> instanceJsonLdType = readInstanceJsonLdType(sourceNode, path);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    URI jsonSchemaSchemaUri = readRequiredUri(sourceNode, path, JSON_SCHEMA_SCHEMA);
    String jsonSchemaType = readRequiredString(sourceNode, path, JSON_SCHEMA_TYPE);
    String jsonSchemaTitle = readRequiredString(sourceNode, path, JSON_SCHEMA_TITLE);

    String jsonSchemaDescription = readString(sourceNode, path, JSON_SCHEMA_DESCRIPTION, "");
    Version modelVersion = readModelVersion(sourceNode, path);
    String name = readRequiredString(sourceNode, path, SCHEMA_ORG_NAME);
    String description = readRequiredString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    Optional<String> identifier = readString(sourceNode, path, SCHEMA_ORG_IDENTIFIER);
    Optional<Version> version = readVersion(sourceNode, path, PAV_VERSION);
    Optional<Status> status = readStatus(sourceNode, path, BIBO_STATUS);
    Optional<URI> previousVersion = readUri(sourceNode, path, PAV_PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, PAV_DERIVED_FROM);
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    LinkedHashMap<String, URI> childPropertyUris = getChildPropertyUris(sourceNode, path);
    Optional<String> language = readLanguage(sourceNode, path);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path, ANNOTATIONS);
    Map<String, String> childSchemaOrgNames = readNestedFieldAndElementSchemaArtifacts(sourceNode, path, fieldSchemas, elementSchemas, childPropertyUris);
    TemplateUi templateUi = readTemplateUi(sourceNode, path, UI, childSchemaOrgNames);

    checkTemplateSchemaArtifactJsonLdType(jsonLdTypes, path);

    return TemplateSchemaArtifact.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      instanceJsonLdType,
      name, description, identifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas, language, templateUi, annotations);
  }

  private ElementSchemaArtifact readElementSchemaArtifact(ObjectNode sourceNode, String path,
    String childName, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri)
  {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> instanceJsonLdType = readInstanceJsonLdType(sourceNode, path);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    URI jsonSchemaSchemaUri = readRequiredUri(sourceNode, path, JSON_SCHEMA_SCHEMA);
    String jsonSchemaType = readRequiredString(sourceNode, path, JSON_SCHEMA_TYPE);
    String jsonSchemaTitle = readRequiredString(sourceNode, path, JSON_SCHEMA_TITLE);
    String jsonSchemaDescription = readString(sourceNode, path, JSON_SCHEMA_DESCRIPTION, "");
    Version modelVersion = readModelVersion(sourceNode, path);
    String schemaOrgName = readRequiredString(sourceNode, path, SCHEMA_ORG_NAME);
    String schemaOrgDescription = readRequiredString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    Optional<String> schemaOrgIdentifier = readString(sourceNode, path, SCHEMA_ORG_IDENTIFIER);
    Optional<Version> version = readVersion(sourceNode, path, PAV_VERSION);
    Optional<Status> status = readStatus(sourceNode, path, BIBO_STATUS);
    Optional<URI> previousVersion = readUri(sourceNode, path, PAV_PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, PAV_DERIVED_FROM);
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    Optional<String> preferredLabel = readString(sourceNode, path, SKOS_PREFLABEL);
    Optional<String> language = readLanguage(sourceNode, path);
    LinkedHashMap<String, URI> childPropertyUris = getChildPropertyUris(sourceNode, path);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path, ANNOTATIONS);

    checkElementSchemaArtifactJsonLdType(jsonLdTypes, path);

    Map<String, String> childSchemaOrgNames = readNestedFieldAndElementSchemaArtifacts(sourceNode, path, fieldSchemas, elementSchemas, childPropertyUris);

    ElementUi elementUi = readElementUi(sourceNode, path, UI, childSchemaOrgNames);

    return ElementSchemaArtifact.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      instanceJsonLdType,
      schemaOrgName, schemaOrgDescription, schemaOrgIdentifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      fieldSchemas, elementSchemas,
      isMultiple, minItems, maxItems,
      propertyUri, preferredLabel, language, elementUi, annotations);
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode sourceNode, String path,
    String childName, boolean isMultiple, Optional<Integer> minItems, Optional<Integer> maxItems, Optional<URI> propertyUri)
  {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    URI jsonSchemaSchemaUri = readRequiredUri(sourceNode, path, JSON_SCHEMA_SCHEMA);
    String jsonSchemaType = readRequiredString(sourceNode, path, JSON_SCHEMA_TYPE);
    String jsonSchemaTitle = readRequiredString(sourceNode, path, JSON_SCHEMA_TITLE);
    String jsonSchemaDescription = readString(sourceNode, path, JSON_SCHEMA_DESCRIPTION, "");
    Version modelVersion = readModelVersion(sourceNode, path);
    String schemaOrgName = readRequiredString(sourceNode, path, SCHEMA_ORG_NAME);
    String schemaOrgDescription = readRequiredString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    Optional<String> schemaOrgIdentifier = readString(sourceNode, path, SCHEMA_ORG_IDENTIFIER);
    Optional<Version> version = readVersion(sourceNode, path, PAV_VERSION);
    Optional<Status> status = readStatus(sourceNode, path, BIBO_STATUS);
    Optional<URI> previousVersion = readUri(sourceNode, path, PAV_PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, PAV_DERIVED_FROM);
    Optional<String> preferredLabel = readString(sourceNode, path, SKOS_PREFLABEL);
    List<String> alternateLabels = readStringArray(sourceNode, path, SKOS_ALTLABEL);
    Optional<String> language = readLanguage(sourceNode, path);
    FieldUi fieldUi = readFieldUi(sourceNode, path, UI);
    Optional<ValueConstraints> valueConstraints = readValueConstraints(sourceNode, path, VALUE_CONSTRAINTS, fieldUi.inputType());
    Optional<Annotations> annotations = readAnnotations(sourceNode, path, ANNOTATIONS);

    checkFieldSchemaArtifactJsonLdType(jsonLdTypes, path);

    return FieldSchemaArtifact.create(jsonSchemaSchemaUri, jsonSchemaType, jsonSchemaTitle, jsonSchemaDescription,
      jsonLdContext, jsonLdTypes, jsonLdId,
      schemaOrgName, schemaOrgDescription, schemaOrgIdentifier,
      modelVersion, version, status, previousVersion, derivedFrom,
      isMultiple, minItems, maxItems, propertyUri,
      createdBy, modifiedBy, createdOn, lastUpdatedOn,
      preferredLabel, alternateLabels,
      language, fieldUi, valueConstraints, annotations);
  }

  private Map<String, String> readNestedFieldAndElementSchemaArtifacts(ObjectNode parentNode, String path,
    Map<String, FieldSchemaArtifact> fieldSchemas, Map<String, ElementSchemaArtifact> elementSchemas,
    Map<String, URI> childPropertyUris)
  {
    JsonNode propertiesNode = parentNode.get(JSON_SCHEMA_PROPERTIES);
    Map<String, String> childSchemaOrgNames = new HashMap<>();

    if (propertiesNode == null || !propertiesNode.isObject())
      throw new ArtifactParseException("Invalid JSON Schema properties node", JSON_SCHEMA_PROPERTIES, path);

    Iterator<String> jsonChildNames = propertiesNode.fieldNames();

    while (jsonChildNames.hasNext()) {
      String childName = jsonChildNames.next();
      boolean isMultiple = false;
      Optional<Integer> minItems = Optional.empty();
      Optional<Integer> maxItems = Optional.empty();

      // The /properties field for each schema artifact contains entries constraining fields in instances
      if (!TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS.contains(childName))
        if (!FIELD_INSTANCE_ARTIFACT_KEYWORDS.contains(childName) && !ELEMENT_INSTANCE_ARTIFACT_KEYWORDS.contains(
          childName)) {
          JsonNode jsonFieldOrElementSchemaArtifactNode = propertiesNode.get(childName);
          String fieldOrElementPath = path + "/properties/" + childName;

          if (jsonFieldOrElementSchemaArtifactNode.isObject()) {

            String jsonSchemaType = readRequiredString((ObjectNode)jsonFieldOrElementSchemaArtifactNode,
              fieldOrElementPath, JSON_SCHEMA_TYPE);

            if (jsonSchemaType.equals(JSON_SCHEMA_ARRAY)) {

              isMultiple = true;

              minItems = readInteger((ObjectNode)jsonFieldOrElementSchemaArtifactNode,
                fieldOrElementPath, JSON_SCHEMA_MIN_ITEMS);

              maxItems = readInteger((ObjectNode)jsonFieldOrElementSchemaArtifactNode,
                fieldOrElementPath, JSON_SCHEMA_MAX_ITEMS);

              jsonFieldOrElementSchemaArtifactNode = jsonFieldOrElementSchemaArtifactNode.get(JSON_SCHEMA_ITEMS);

              if (jsonFieldOrElementSchemaArtifactNode == null)
                throw new ArtifactParseException("No items field in array", JSON_SCHEMA_ITEMS, fieldOrElementPath);

              fieldOrElementPath += "/items";

              if (!jsonFieldOrElementSchemaArtifactNode.isObject())
                throw new ArtifactParseException("Non-object items content in array", JSON_SCHEMA_ITEMS,
                  fieldOrElementPath);
            } else if (!jsonSchemaType.equals(JSON_SCHEMA_OBJECT)) {
              throw new ArtifactParseException("Expecting array or object, got " + jsonSchemaType, JSON_SCHEMA_ITEMS,
                fieldOrElementPath);
            }

            List<URI> subSchemaArtifactJsonLdTypes = readUriArray(
              (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath, JSON_LD_TYPE);

            checkSchemaArtifactJsonLdType(subSchemaArtifactJsonLdTypes, fieldOrElementPath);

            URI subSchemaArtifactJsonLdType = subSchemaArtifactJsonLdTypes.get(0);
            Optional<URI> propertyUri = childPropertyUris.containsKey(childName) ?
              Optional.of(childPropertyUris.get(childName)) :
              Optional.empty();

            switch (subSchemaArtifactJsonLdType.toString()) {
            case TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI ->
              throw new ArtifactParseException("Invalid nesting of template schema artifact", childName,
                fieldOrElementPath);
            case ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI -> {
              ElementSchemaArtifact elementSchemaArtifact = readElementSchemaArtifact(
                (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath, childName, isMultiple, minItems, maxItems,
                propertyUri);
              elementSchemas.put(childName, elementSchemaArtifact);
              childSchemaOrgNames.put(childName, elementSchemaArtifact.name());
            }
            case FIELD_SCHEMA_ARTIFACT_TYPE_IRI, STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI -> {
              FieldSchemaArtifact fieldSchemaArtifact = readFieldSchemaArtifact(
                (ObjectNode)jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath, childName, isMultiple, minItems, maxItems,
                propertyUri);
              fieldSchemas.put(childName, fieldSchemaArtifact);
              childSchemaOrgNames.put(childName, fieldSchemaArtifact.name());
            }
            default -> throw new ArtifactParseException("Unknown JSON-LD @type " + subSchemaArtifactJsonLdType,
              childName, fieldOrElementPath);
            }

          } else {
            throw new ArtifactParseException("Unknown non-object schema artifact", childName, fieldOrElementPath);
          }
        }
    }
    return childSchemaOrgNames;
  }

  private TemplateInstanceArtifact readTemplateInstanceArtifact(ObjectNode sourceNode, String path)
  {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    URI isBasedOn = readRequiredUri(sourceNode, path, SCHEMA_IS_BASED_ON);
    Optional<String> name = readString(sourceNode, path, SCHEMA_ORG_NAME);
    Optional<String> description = readString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    List<String> childNames = new ArrayList<>();
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances = new LinkedHashMap<>();
    Optional<Annotations> annotations = readAnnotations(sourceNode, path, ANNOTATIONS);

    readNestedInstanceArtifacts(sourceNode, path, childNames, singleInstanceFieldInstances, multiInstanceFieldInstances,
      singleInstanceElementInstances, multiInstanceElementInstances, attributeValueFieldInstances);

    return TemplateInstanceArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, isBasedOn, childNames, singleInstanceFieldInstances,
      multiInstanceFieldInstances, singleInstanceElementInstances, multiInstanceElementInstances,
      attributeValueFieldInstances, annotations);
  }

  private ElementInstanceArtifact readElementInstanceArtifact(ObjectNode sourceNode, String path)
  {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    Optional<String> name = readString(sourceNode, path, SCHEMA_ORG_NAME);
    Optional<String> description = readString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    List<String> childNames = new ArrayList<>();
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances = new LinkedHashMap<>();

    readNestedInstanceArtifacts(sourceNode, path, childNames, singleInstanceFieldInstances, multiInstanceFieldInstances,
      singleInstanceElementInstances, multiInstanceElementInstances, attributeValueFieldInstances);

    return ElementInstanceArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
      modifiedBy, createdOn, lastUpdatedOn, childNames, singleInstanceFieldInstances, multiInstanceFieldInstances,
      singleInstanceElementInstances, multiInstanceElementInstances, attributeValueFieldInstances);
  }

  private FieldInstanceArtifact readFieldInstanceArtifact(ObjectNode sourceNode, String path)
  {
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<String> jsonLdValue = readString(sourceNode, path, JSON_LD_VALUE);
    Optional<String> rdfsLabel = readString(sourceNode, path, RDFS_LABEL);
    Optional<String> language = readString(sourceNode, path, JSON_LD_LANGUAGE);
    Optional<String> notation = readString(sourceNode, path, SKOS_NOTATION);
    Optional<String> preferredLabel = readString(sourceNode, path, SKOS_PREFLABEL);

    return FieldInstanceArtifact.create(jsonLdTypes, jsonLdId,
      jsonLdValue, rdfsLabel, notation, preferredLabel, language);
  }

  private void readNestedInstanceArtifacts(ObjectNode parentNode, String path,
    List<String> childNames,
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
    LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances,
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups)
  {
    LinkedHashMap<String, List<String>> attributeValueFieldGroupInstanceNames = new LinkedHashMap<>();
    Iterator<String> instanceArtifactFieldNames = parentNode.fieldNames();

    while (instanceArtifactFieldNames.hasNext()) {
      String instanceArtifactFieldName = instanceArtifactFieldNames.next();

      if (!INSTANCE_ARTIFACT_KEYWORDS.contains(instanceArtifactFieldName)) {
        JsonNode nestedNode = parentNode.get(instanceArtifactFieldName);
        String nestedInstanceArtifactPath = path + "/" + instanceArtifactFieldName;

        if (nestedNode.isObject()) {
          ObjectNode nestedInstanceArtifactNode = (ObjectNode)nestedNode;

          readNestedSingleInstanceArtifact(instanceArtifactFieldName, nestedInstanceArtifactPath,
            nestedInstanceArtifactNode, childNames, singleInstanceFieldInstances, singleInstanceElementInstances);

        } else if (nestedNode.isArray()) {
          Iterator<JsonNode> nodeIterator = nestedNode.iterator();

          if (childNames.contains(instanceArtifactFieldName))
            throw new ArtifactParseException("Duplicate field " + instanceArtifactFieldName, instanceArtifactFieldName,
              instanceArtifactFieldName);
          childNames.add(instanceArtifactFieldName);

          if (!nodeIterator.hasNext()) { // Array is empty
            // We do not know if this is (1) an empty attribute-value field array, (2) an empty multi-instance field
            // array, or (3) an empty multi-instance element array. We'll arbitrarily pick (2).
            multiInstanceFieldInstances.put(instanceArtifactFieldName, Collections.emptyList());
          } else {
            int arrayIndex = 0;
            while (nodeIterator.hasNext()) {
              String arrayEnclosedInstanceArtifactPath = nestedInstanceArtifactPath + "[" + arrayIndex + "]";
              JsonNode instanceNode = nodeIterator.next();
              if (instanceNode == null || instanceNode.isNull()) {
                throw new ArtifactParseException(
                  "Expecting field or element instance or attribute-value field name in array, got null",
                  instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath);
              } else {
                if (instanceNode.isObject()) {
                  ObjectNode arrayEnclosedInstanceArtifactNode = (ObjectNode)instanceNode;
                  readNestedMultiInstanceArtifact(instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath,
                    arrayEnclosedInstanceArtifactNode, childNames, multiInstanceFieldInstances,
                    multiInstanceElementInstances);
                } else if (instanceNode.isTextual()) { // A list of attribute-value field names
                  String attributeValueFieldName = instanceNode.asText();
                  if (attributeValueFieldName.isEmpty())
                    throw new ArtifactParseException("Empty attribute-value field name in array",
                      instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath);

                  if (attributeValueFieldGroupInstanceNames.containsKey(instanceArtifactFieldName))
                    attributeValueFieldGroupInstanceNames.get(instanceArtifactFieldName)
                      .add(attributeValueFieldName);
                  else {
                    List<String> attributeValueFieldInstanceNames = new ArrayList<>();
                    attributeValueFieldInstanceNames.add(attributeValueFieldName);
                    attributeValueFieldGroupInstanceNames.put(instanceArtifactFieldName,
                      attributeValueFieldInstanceNames);
                  }
                } else
                  throw new ArtifactParseException(
                    "Expecting field or element instance or attribute-value field name in array",
                    instanceArtifactFieldName, arrayEnclosedInstanceArtifactPath);
              }
              arrayIndex++;
            }
          }
        }
      }
    }
    processAttributeValueFields(path, singleInstanceFieldInstances, attributeValueFieldGroupInstanceNames,
      attributeValueFieldInstanceGroups);
  }

  /**
   * A template or element instance may contain attribute-value fields.
   * <p></p>
   * Their definition could look as follows:
   * <pre>
   *   "Attribute-values field A": [ "Attribute-values field instance name 1", "Attribute-values field instance name 2" ],
   *   "Attribute-values field instance name 1": { "@value": "v1" },
   *   "Attribute-values field instance name 2": { "@value": "v2" },
   *
   *   "Attribute-values field B": [ "Attribute-values field instance name 3", "Attribute-values field instance name 4" ],
   *   "Attribute-values field instance name 3": { "@value": "v3" },
   *   "Attribute-values field instance name 4": { "@value": "v4" },
   * </pre>
   * The individual attribute-value field instances will have ended up in our singleInstanceFieldInstances map when
   * parsing. We thus need to post-process and move these fields from the singleInstanceFieldInstances map to the
   * specialized attributeValueFieldInstances map.
   */
  private void processAttributeValueFields(String path, Map<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    Map<String, List<String>> attributeValueFieldGroupInstanceNames, Map<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups)
  {
    for (var attributeValueFieldGroupsEntry : attributeValueFieldGroupInstanceNames.entrySet()) {
      String attributeValueFieldGroupName = attributeValueFieldGroupsEntry.getKey();
      List<String> attributeValueFieldInstanceNames = attributeValueFieldGroupsEntry.getValue();

      for (String attributeValueFieldInstanceName : attributeValueFieldInstanceNames) {

        if (!singleInstanceFieldInstances.containsKey(attributeValueFieldInstanceName))
          throw new ArtifactParseException(
            "Attribute-value field group " + attributeValueFieldGroupName + " specifies an instance field "
              + attributeValueFieldInstanceName + " that is not present in the template or element instance",
            attributeValueFieldGroupName, path);

        FieldInstanceArtifact perAttributeFieldInstance = singleInstanceFieldInstances.get(attributeValueFieldInstanceName);

        if (attributeValueFieldInstanceGroups.containsKey(attributeValueFieldGroupName)) {
          attributeValueFieldInstanceGroups.get(attributeValueFieldGroupName).put(attributeValueFieldInstanceName, perAttributeFieldInstance);
        } else {
          attributeValueFieldInstanceGroups.put(attributeValueFieldGroupName, new LinkedHashMap<>());
          attributeValueFieldInstanceGroups.get(attributeValueFieldGroupName).put(attributeValueFieldInstanceName, perAttributeFieldInstance);
        }
        singleInstanceFieldInstances.remove(attributeValueFieldInstanceName); // Remove it from the single-instance fields
      }
    }
  }

  private void readNestedSingleInstanceArtifact(String instanceArtifactFieldName, String instanceArtifactPath,
    ObjectNode instanceArtifactNode,
    List<String> childNames,
    Map<String, FieldInstanceArtifact> singleInstanceFieldInstances,
    Map<String, ElementInstanceArtifact> singleInstanceElementInstances)
  {
    if (childNames.contains(instanceArtifactFieldName))
      throw new ArtifactParseException("duplicate field " + instanceArtifactFieldName, instanceArtifactFieldName,
        instanceArtifactPath);

    childNames.add(instanceArtifactFieldName);

    if (hasJsonLdContextField(instanceArtifactNode)) { // Element instance artifacts have @context fields
      ObjectNode elementInstanceArtifactNode = instanceArtifactNode;
      ElementInstanceArtifact elementInstanceArtifact = readElementInstanceArtifact(elementInstanceArtifactNode,
        instanceArtifactPath);
      singleInstanceElementInstances.put(instanceArtifactFieldName, elementInstanceArtifact);
    } else { // Field instance artifacts do not
      FieldInstanceArtifact fieldInstanceArtifact = readFieldInstanceArtifact(instanceArtifactNode,
        instanceArtifactPath);
      singleInstanceFieldInstances.put(instanceArtifactFieldName, fieldInstanceArtifact);
    }
  }

  private void readNestedMultiInstanceArtifact(String instanceArtifactFieldName, String instanceArtifactPath,
    ObjectNode instanceArtifactArrayNode,
    List<String> childNames,
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances)
  {

    if (instanceArtifactArrayNode == null || instanceArtifactArrayNode.isNull())
      throw new ArtifactParseException("Value in instance array must not be null", instanceArtifactFieldName,
        instanceArtifactPath);

    if (hasJsonLdContextField(instanceArtifactArrayNode)) { // Element instance artifacts have @context fields
      ObjectNode elementInstanceArtifactNode = instanceArtifactArrayNode;
      ElementInstanceArtifact elementInstanceArtifact = readElementInstanceArtifact(elementInstanceArtifactNode,
        instanceArtifactPath);

      if (!multiInstanceElementInstances.containsKey(instanceArtifactFieldName))
        multiInstanceElementInstances.put(instanceArtifactFieldName, new ArrayList<>());

      multiInstanceElementInstances.get(instanceArtifactFieldName).add(elementInstanceArtifact);
    } else { // Field instance artifacts do not
      FieldInstanceArtifact fieldInstanceArtifact = readFieldInstanceArtifact(instanceArtifactArrayNode,
        instanceArtifactPath);

      if (!multiInstanceFieldInstances.containsKey(instanceArtifactFieldName))
        multiInstanceFieldInstances.put(instanceArtifactFieldName, new ArrayList<>());

      multiInstanceFieldInstances.get(instanceArtifactFieldName).add(fieldInstanceArtifact);
    }
  }

  private void checkSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path)
  {
    if (schemaArtifactJsonLdTypes.isEmpty())
      throw new ArtifactParseException("Unknown object - must be a JSON-LD type or array of types", JSON_LD_TYPE, path);

    if (schemaArtifactJsonLdTypes.size() != 1)
      throw new ArtifactParseException(
        "Expecting single JSON-LD @type field for schema artifact, got " + schemaArtifactJsonLdTypes.size(),
        JSON_LD_TYPE, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!SCHEMA_ARTIFACT_TYPE_IRIS.contains(schemaArtifactJsonLdType.toString()))
      throw new ArtifactParseException("Unexpected schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
        JSON_LD_TYPE, path);
  }

  private void checkTemplateSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path)
  {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new ArtifactParseException("Unexpected template schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
        JSON_LD_TYPE, path);
  }

  private void checkElementSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path)
  {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new ArtifactParseException("Unexpected element schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
        JSON_LD_TYPE, path);
  }

  private void checkFieldSchemaArtifactJsonLdType(List<URI> schemaArtifactJsonLdTypes, String path)
  {
    checkSchemaArtifactJsonLdType(schemaArtifactJsonLdTypes, path);

    URI schemaArtifactJsonLdType = schemaArtifactJsonLdTypes.get(0);

    if (!schemaArtifactJsonLdType.toString().equals(FIELD_SCHEMA_ARTIFACT_TYPE_IRI) &&
        !schemaArtifactJsonLdType.toString().equals(STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI))
      throw new ArtifactParseException("Unexpected field schema artifact JSON-LD @type " + schemaArtifactJsonLdType,
        JSON_LD_TYPE, path);
  }

  // A parent schema artifact's JSON Schema 'properties' object contains a specification for a JSON-LD @context for
  // the corresponding instance; this @context maps each child name to a URI that represents a property specification
  // for each child.
  //
  // e.g.,
  //
  //    "properties": {
  //      "@context": {
  //         "type": "object",
  //          ....
  //          "Study ID": { "enum": [ "http://semantic-dicom.org/dcm#StudyID" ]
  //          "Disease": { "enum": [ "http://semantic-dicom.org/dcm#Disease" ]
  //      }
  //    }
  private LinkedHashMap<String, URI> getChildPropertyUris(ObjectNode sourceNode, String path)
  {
    LinkedHashMap<String, URI> childName2URI = new LinkedHashMap<>();
    String contextPath = "/" + JSON_SCHEMA_PROPERTIES + "/" + JSON_LD_CONTEXT + "/" + JSON_SCHEMA_PROPERTIES;
    JsonNode contextNode = sourceNode.at(contextPath);

    if (contextNode != null && contextNode.isObject()) {
      ObjectNode jsonSchemaContextSpecificationNode = (ObjectNode)contextNode;
      Iterator<String> childNames = jsonSchemaContextSpecificationNode.fieldNames();

      while (childNames.hasNext()) {
        String childName = childNames.next();
        if (!ARTIFACT_CONTEXT_ENTRIES.contains(childName)) { // Ignore standard context entries
          JsonNode enumNode = jsonSchemaContextSpecificationNode.get(childName);

          if (enumNode != null) { // A property URI specification for a child is optional
            if (!enumNode.isObject())
              throw new ArtifactParseException("Expecting object node with property URI enum specification", childName,
                path + contextPath);

            JsonNode enumArray = enumNode.get(JSON_SCHEMA_ENUM);

            if (enumArray == null || !enumArray.isArray())
              throw new ArtifactParseException("Expecting array for property URI enum specification", JSON_SCHEMA_ENUM,
                path + contextPath + childName);

            if (enumArray.size() != 1)
              throw new ArtifactParseException(
                "Expecting exactly one value for property URI enum specification, got " + enumArray.size(),
                JSON_SCHEMA_ENUM, path + contextPath + childName);

            JsonNode elementNode = enumArray.get(0);

            if (!elementNode.isTextual())
              throw new ArtifactParseException("Expecting text node for property URI enum entry, got " + elementNode.getNodeType(), JSON_SCHEMA_ENUM,
                path + contextPath + childName);

            try {
              URI propertyUri = new URI(elementNode.asText());
              childName2URI.put(childName, propertyUri);
            } catch (URISyntaxException e) {
              throw new ArtifactParseException("Invalid URI " + elementNode.asText() + " for enum specification",
                JSON_SCHEMA_ENUM, path + contextPath + childName);
            }
          }
        }
      }
    }
    return childName2URI;
  }

  /**
   *
   * <pre>
   *   "@type": {
   *     "oneOf": [
   *       { "type": "string", "format": "uri", "enum": [ "https://example.com/T1" ] },
   *       {
   *         "type": "array", "minItems": 1,
   *         "items": { "type": "string", "format": "uri", "enum": [ "https://example.com/T1" ] },
   *         "uniqueItems": true
   *       }
   *     ]
   *   }
   * </pre>
   */
  private Optional<URI> readInstanceJsonLdType(ObjectNode sourceNode, String path)
  {
    String uriPath = "/" + JSON_SCHEMA_PROPERTIES + "/" + JSON_LD_TYPE + "/" + JSON_SCHEMA_ONE_OF + "/0/" + JSON_SCHEMA_ENUM + "/0";
    JsonNode uriNode = sourceNode.at(uriPath);

    if (uriNode != null && uriNode.isTextual()) {
      return Optional.of(URI.create(uriNode.asText()));
    } else
      return Optional.empty();
  }


  private Optional<ValueConstraints> readValueConstraints(ObjectNode sourceNode, String path,
    String fieldName, FieldInputType fieldInputType)
  {
    String vcPath = path + "/" + fieldName;
    ObjectNode vcNode = readValueConstraintsNode(sourceNode, path, fieldName);

    if (vcNode != null) {
      boolean requiredValue = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_REQUIRED_VALUE, false);
      boolean recommendedValue = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_RECOMMENDED_VALUE, false);
      boolean multipleChoice = readBoolean(vcNode, vcPath, VALUE_CONSTRAINTS_MULTIPLE_CHOICE, false);
      Optional<XsdNumericDatatype> numberType = readNumberType(vcNode, vcPath, VALUE_CONSTRAINTS_NUMBER_TYPE);
      Optional<XsdTemporalDatatype> temporalType = readTemporalType(vcNode, vcPath, VALUE_CONSTRAINTS_TEMPORAL_TYPE);
      Optional<String> unitOfMeasure = readString(vcNode, vcPath, VALUE_CONSTRAINTS_UNIT_OF_MEASURE);
      Optional<Number> minValue = readNumber(vcNode, vcPath, VALUE_CONSTRAINTS_MIN_NUMBER_VALUE);
      Optional<Number> maxValue = readNumber(vcNode, vcPath, VALUE_CONSTRAINTS_MAX_NUMBER_VALUE);
      Optional<Integer> decimalPlaces = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_DECIMAL_PLACE);
      Optional<Integer> minLength = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_MIN_STRING_LENGTH);
      Optional<Integer> maxLength = readInteger(vcNode, vcPath, VALUE_CONSTRAINTS_MAX_STRING_LENGTH);
      Optional<String> regex = readString(vcNode, vcPath, "regex"); // TODO Add 'regex' to ModelNodeNames
      List<OntologyValueConstraint> ontologies = readOntologyValueConstraints(vcNode, vcPath,
        VALUE_CONSTRAINTS_ONTOLOGIES);
      List<ValueSetValueConstraint> valueSets = readValueSetValueConstraints(vcNode, vcPath,
        VALUE_CONSTRAINTS_VALUE_SETS);
      List<ClassValueConstraint> classes = readClassValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_CLASSES);
      List<BranchValueConstraint> branches = readBranchValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_BRANCHES);
      List<LiteralValueConstraint> literals = readLiteralValueConstraints(vcNode, vcPath, VALUE_CONSTRAINTS_LITERALS);
      List<ControlledTermValueConstraintsAction> actions = readValueConstraintsActions(vcNode, vcPath,
        VALUE_CONSTRAINTS_ACTIONS);
      Optional<? extends DefaultValue> defaultValue = readDefaultValue(vcNode, vcPath, VALUE_CONSTRAINTS_DEFAULT_VALUE, fieldInputType);

      if (fieldInputType == FieldInputType.NUMERIC) {
        Optional<NumericDefaultValue> numericDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asNumericDefaultValue()) :
          Optional.empty();
        if (!numberType.isPresent())
          numberType = Optional.of(XsdNumericDatatype.DECIMAL); // Default to xsd:decimal if unspecifed
        return Optional.of(
          NumericValueConstraints.create(numberType.get(), minValue, maxValue, decimalPlaces, unitOfMeasure,
            numericDefaultValue, requiredValue, recommendedValue, multipleChoice));
      } else if (fieldInputType == FieldInputType.TEMPORAL) {
        if (!temporalType.isPresent())
          throw new ArtifactParseException("a temporal type must be present for a temporal field", fieldName, path);
        Optional<TemporalDefaultValue> temporalDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asTemporalDefaultValue()) :
          Optional.empty();
        return Optional.of(TemporalValueConstraints.create(temporalType.get(), temporalDefaultValue,
          requiredValue, recommendedValue, multipleChoice));

      } else if (fieldInputType == FieldInputType.LINK) {
        Optional<LinkDefaultValue> linkDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asLinkDefaultValue()) :
          Optional.empty();
        return Optional.of(LinkValueConstraints.create(linkDefaultValue, requiredValue, recommendedValue, multipleChoice));
      } else if (fieldInputType == FieldInputType.ATTRIBUTE_VALUE) {
        return Optional.empty();
      } else if (fieldInputType == FieldInputType.TEXTFIELD && (!ontologies.isEmpty() || !valueSets.isEmpty() || !classes.isEmpty() || !branches.isEmpty())) {
        Optional<ControlledTermDefaultValue> controlledTermDefaultValue = defaultValue.isPresent() && defaultValue.get().asControlledTermDefaultValue() != null?
          Optional.of(defaultValue.get().asControlledTermDefaultValue()) :
          Optional.empty();
        return Optional.of(
          ControlledTermValueConstraints.create(ontologies, valueSets, classes, branches, controlledTermDefaultValue,
            actions, requiredValue, recommendedValue, multipleChoice));
      } else {
        Optional<TextDefaultValue> textDefaultValue = defaultValue.isPresent() ?
          Optional.of(defaultValue.get().asTextDefaultValue()) :
          Optional.empty();
        return Optional.of(
          TextValueConstraints.create(minLength, maxLength, textDefaultValue, literals, requiredValue, recommendedValue,
            multipleChoice, regex));
      }
    } else
      return Optional.empty();
  }

  private Optional<DefaultValue> readDefaultValue(ObjectNode sourceNode, String path, String fieldName,
    FieldInputType fieldInputType)
  {
    JsonNode childNode = sourceNode.get(fieldName);

    if (childNode == null || childNode.isNull())
      return Optional.empty();
    else if (childNode.isObject()) {
      String nestedPath = path + "/" + fieldName;
      ObjectNode defaultValueNode = (ObjectNode)childNode;
      URI termUri = readRequiredUri(defaultValueNode, nestedPath, VALUE_CONSTRAINTS_DEFAULT_VALUE_TERM_URI);
      Optional<String> rdfsLabel = readString(defaultValueNode, nestedPath, RDFS_LABEL);
      return Optional.of(new ControlledTermDefaultValue(termUri, rdfsLabel.orElse("")));
    } else if (childNode.isNumber()) {
      return Optional.of(new NumericDefaultValue(childNode.asDouble()));
    } else if (childNode.isTextual()) {
      if (fieldInputType == FieldInputType.LINK)
        return Optional.of(new LinkDefaultValue(URI.create(childNode.asText())));
      else if (fieldInputType == FieldInputType.TEMPORAL)
        return Optional.of(new TemporalDefaultValue(childNode.asText()));
      else
        return Optional.of(new TextDefaultValue(childNode.asText()));
    } else
      throw new ArtifactParseException(
        "default value must be a string, a number, or an object containing URI/string pair", fieldName, path);
  }

  private List<OntologyValueConstraint> readOntologyValueConstraints(ObjectNode sourceNode, String path, String fieldName)
  {
    List<OntologyValueConstraint> ontologyValueConstraints = new ArrayList<>();

    JsonNode ontologyValueConstraintArrayNode = sourceNode.get(fieldName);

    if (ontologyValueConstraintArrayNode != null && !ontologyValueConstraintArrayNode.isNull()
      && ontologyValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : ontologyValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", fieldName, path);
          OntologyValueConstraint ontologyValueConstraint = readOntologyValueConstraint((ObjectNode)valueConstraintNode,
            path + "/" + fieldName);
          ontologyValueConstraints.add(ontologyValueConstraint);
        }
      }
    }
    return ontologyValueConstraints;
  }

  private List<ClassValueConstraint> readClassValueConstraints(ObjectNode sourceNode, String path, String fieldName)
  {
    List<ClassValueConstraint> classValueConstraints = new ArrayList<>();

    JsonNode classValueConstraintArrayNode = sourceNode.get(fieldName);

    if (classValueConstraintArrayNode != null && !classValueConstraintArrayNode.isNull()
      && classValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : classValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", fieldName, path);
          ClassValueConstraint classValueConstraint = readClassValueConstraint((ObjectNode)valueConstraintNode,
            path + "/" + fieldName);
          classValueConstraints.add(classValueConstraint);
        }
      }
    }
    return classValueConstraints;
  }

  private List<ValueSetValueConstraint> readValueSetValueConstraints(ObjectNode sourceNode, String path, String fieldName)
  {
    List<ValueSetValueConstraint> valueSetValueConstraints = new ArrayList<>();

    JsonNode valueSetValueConstraintArrayNode = sourceNode.get(fieldName);

    if (valueSetValueConstraintArrayNode != null && valueSetValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : valueSetValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", fieldName, path);
          ValueSetValueConstraint valueSetValueConstraint = readValueSetValueConstraint((ObjectNode)valueConstraintNode,
            path + "/" + fieldName);
          valueSetValueConstraints.add(valueSetValueConstraint);
        }
      }
    }
    return valueSetValueConstraints;
  }

  private List<BranchValueConstraint> readBranchValueConstraints(ObjectNode sourceNode, String path, String fieldName)
  {
    List<BranchValueConstraint> branchValueConstraints = new ArrayList<>();

    JsonNode branchValueConstraintArrayNode = sourceNode.get(fieldName);

    if (branchValueConstraintArrayNode != null && branchValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintNode : branchValueConstraintArrayNode) {
        if (valueConstraintNode != null) {
          if (!valueConstraintNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", fieldName, path);
          BranchValueConstraint branchValueConstraint = readBranchValueConstraint((ObjectNode)valueConstraintNode,
            path + "/" + fieldName);
          branchValueConstraints.add(branchValueConstraint);
        }
      }
    }

    return branchValueConstraints;
  }

  private List<LiteralValueConstraint> readLiteralValueConstraints(ObjectNode sourceNode, String path, String fieldName)
  {
    List<LiteralValueConstraint> literalValueConstraints = new ArrayList<>();

    JsonNode literValueConstraintArrayNode = sourceNode.get(fieldName);
    String literalsPath = path + "/" + fieldName;

    if (literValueConstraintArrayNode != null && literValueConstraintArrayNode.isArray()) {

      for (JsonNode valueConstraintsNode : literValueConstraintArrayNode) {
        if (valueConstraintsNode != null) {
          if (!valueConstraintsNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", fieldName, literalsPath);
          LiteralValueConstraint literalValueConstraint = readLiteralValueConstraint((ObjectNode)valueConstraintsNode,
            literalsPath);
          literalValueConstraints.add(literalValueConstraint);
        }
      }
    }
    return literalValueConstraints;
  }

  private List<ControlledTermValueConstraintsAction> readValueConstraintsActions(ObjectNode sourceNode, String path, String fieldName)
  {
    List<ControlledTermValueConstraintsAction> actions = new ArrayList<>();

    JsonNode controlledTermValueConstraintsActionArrayNode = sourceNode.get(fieldName);

    if (controlledTermValueConstraintsActionArrayNode != null
      && controlledTermValueConstraintsActionArrayNode.isArray()) {

      for (JsonNode actionNode : controlledTermValueConstraintsActionArrayNode) {
        if (actionNode != null) {
          if (!actionNode.isObject())
            throw new ArtifactParseException("Value in array must be an object", fieldName, path);
          ControlledTermValueConstraintsAction action = readValueConstraintsAction((ObjectNode)actionNode,
            path + "/" + fieldName);
          actions.add(action);
        }
      }
    }
    return actions;
  }

  private ControlledTermValueConstraintsAction readValueConstraintsAction(ObjectNode sourceNode, String path)
  {
    URI termUri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_TERM_URI);
    Optional<String> source = readString(sourceNode, path, VALUE_CONSTRAINTS_SOURCE);
    ValueConstraintsActionType actionType = readValueConstraintsActionType(sourceNode, path, VALUE_CONSTRAINTS_ACTION);
    ValueType valueType = readValueType(sourceNode, path, VALUE_CONSTRAINTS_TYPE);
    Optional<URI> sourceUri = readUri(sourceNode, path, VALUE_CONSTRAINTS_SOURCE_URI);
    Optional<Integer> to = readInteger(sourceNode, path, VALUE_CONSTRAINTS_ACTION_TO);

    return new ControlledTermValueConstraintsAction(termUri, source.orElse(""), valueType, actionType, sourceUri, to);
  }

  private Optional<XsdTemporalDatatype> readTemporalType(ObjectNode sourceNode, String path, String fieldName)
  {
    Optional<String> temporalTypeValue = readString(sourceNode, path, fieldName);

    if (temporalTypeValue.isPresent())
      return Optional.of(XsdTemporalDatatype.fromString(temporalTypeValue.get()));
    else
      return Optional.empty();
  }

  private Optional<XsdNumericDatatype> readNumberType(ObjectNode sourceNode, String path, String fieldName)
  {
    Optional<String> numberTypeValue = readString(sourceNode, path, fieldName);

    if (numberTypeValue.isPresent())
      return Optional.of(XsdNumericDatatype.fromString(numberTypeValue.get()));
    else
      return Optional.empty();
  }

  private ValueConstraintsActionType readValueConstraintsActionType(ObjectNode sourceNode, String path, String fieldName)
  {
    String actionType = readRequiredString(sourceNode, path, fieldName);

    return ValueConstraintsActionType.fromString(actionType);
  }

  private ValueType readValueType(ObjectNode sourceNode, String path, String fieldName)
  {
    String valueType = readRequiredString(sourceNode, path, fieldName);

    return ValueType.fromString(valueType);
  }

  private OntologyValueConstraint readOntologyValueConstraint(ObjectNode sourceNode, String path)
  {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String acronym = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_ACRONYM);
    String name = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_NAME);
    Optional<Integer> numTerms = readInteger(sourceNode, path, VALUE_CONSTRAINTS_NUM_TERMS);

    return new OntologyValueConstraint(uri, acronym, name, numTerms);
  }

  private ClassValueConstraint readClassValueConstraint(ObjectNode sourceNode, String path)
  {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String preferredLabel = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_PREFLABEL);
    ValueType valueType = readValueType(sourceNode, path, VALUE_CONSTRAINTS_TYPE);
    String label = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_LABEL);
    String source = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_SOURCE);

    return new ClassValueConstraint(uri, source, label, preferredLabel, valueType);
  }

  private ValueSetValueConstraint readValueSetValueConstraint(ObjectNode sourceNode, String path)
  {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String name = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_NAME);
    String vsCollection = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_VS_COLLECTION);
    Optional<Integer> numTerms = readInteger(sourceNode, path, VALUE_CONSTRAINTS_NUM_TERMS);

    return new ValueSetValueConstraint(uri, vsCollection, name, numTerms);
  }

  private BranchValueConstraint readBranchValueConstraint(ObjectNode sourceNode, String path)
  {
    URI uri = readRequiredUri(sourceNode, path, VALUE_CONSTRAINTS_URI);
    String source = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_SOURCE);
    String acronym = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_ACRONYM);
    String name = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_NAME);
    int maxDepth = readRequiredInt(sourceNode, path, VALUE_CONSTRAINTS_MAX_DEPTH);

    return new BranchValueConstraint(uri, source, acronym, name, maxDepth);
  }

  private LiteralValueConstraint readLiteralValueConstraint(ObjectNode sourceNode, String path)
  {
    String label = readRequiredString(sourceNode, path, VALUE_CONSTRAINTS_LABEL);
    boolean selectedByDefault = readBoolean(sourceNode, path, VALUE_CONSTRAINTS_SELECTED_BY_DEFAULT, false);

    return new LiteralValueConstraint(label, selectedByDefault);
  }

  private FieldUi readFieldUi(ObjectNode sourceNode, String path, String fieldName)
  {
    ObjectNode uiNode = readChildNode(sourceNode, path, fieldName);
    String uiPath = path + "/" + fieldName;

    FieldInputType fieldInputType = readFieldInputType(uiNode, uiPath, UI_FIELD_INPUT_TYPE);
    boolean valueRecommendation = readBoolean(uiNode, uiPath, UI_VALUE_RECOMMENDATION_ENABLED, false);
    boolean hidden = readBoolean(uiNode, uiPath, UI_HIDDEN, false);
    boolean recommendedValue = readBoolean(uiNode, uiPath, UI_RECOMMENDED_VALUE, false);
    boolean continuePreviousLine = readBoolean(uiNode, uiPath, UI_CONTINUE_PREVIOUS_LINE, false);
    Optional<Integer> width = readInteger(uiNode, uiPath, UI_WIDTH);
    Optional<Integer> height = readInteger(uiNode, uiPath, UI_HEIGHT);

    if (fieldInputType.isTemporal()) {
      TemporalGranularity temporalGranularity = readTemporalGranularity(uiNode, uiPath, UI_TEMPORAL_GRANULARITY);
      Optional<InputTimeFormat> inputTimeFormat = readInputTimeFormat(uiNode, uiPath, UI_INPUT_TIME_FORMAT);
      Optional<Boolean> timeZoneEnabled = readOptionalBoolean(uiNode, uiPath, UI_TIMEZONE_ENABLED);

      return TemporalFieldUi.create(temporalGranularity, inputTimeFormat, timeZoneEnabled,
        hidden, recommendedValue, continuePreviousLine);
    } else if (fieldInputType.isNumeric()) {
      return NumericFieldUi.create(hidden, recommendedValue, continuePreviousLine);
    } else if (fieldInputType.isStatic()) {
      Optional<String> content = readString(uiNode, uiPath, UI_CONTENT);
      return StaticFieldUi.create(fieldInputType, content, hidden, continuePreviousLine, width, height);
    } else
      return FieldUi.create(fieldInputType, hidden, valueRecommendation, recommendedValue, continuePreviousLine);
  }

  private TemplateUi readTemplateUi(ObjectNode sourceNode, String path, String fieldName, Map<String, String> childKey2Name)
  {
    ObjectNode uiNode = readChildNode(sourceNode, path, fieldName);
    String uiPath = path + "/" + fieldName;

    List<String> order = readStringArray(uiNode, uiPath, UI_ORDER);
    LinkedHashMap<String, String> originalPropertyLabels = readString2StringMap(uiNode, uiPath, UI_PROPERTY_LABELS);
    LinkedHashMap<String, String> originalPropertyDescriptions = readString2StringMap(uiNode, uiPath, UI_PROPERTY_DESCRIPTIONS);
    Optional<String> header = readString(uiNode, uiPath, UI_HEADER);
    Optional<String> footer = readString(uiNode, uiPath, UI_FOOTER);

    LinkedHashMap<String, String> reorderedPropertyLabels = new LinkedHashMap<>();
    LinkedHashMap<String, String> reorderedPropertyDescriptions = new LinkedHashMap<>();

    Set<String> orderEntriesToRemove = new HashSet<>();

    // Reorder to follow the order list
    for (String childKey: order) {
      if (childKey2Name.containsKey(childKey)) {
        if (originalPropertyLabels.containsKey(childKey))
          reorderedPropertyLabels.put(childKey, originalPropertyLabels.get(childKey));
        else
          reorderedPropertyLabels.put(childKey, childKey2Name.get(childKey));

        if (originalPropertyDescriptions.containsKey(childKey))
          reorderedPropertyDescriptions.put(childKey, originalPropertyDescriptions.get(childKey));
        else
          reorderedPropertyDescriptions.put(childKey, "");
      } else
        orderEntriesToRemove.add(childKey);
    }

    order.removeAll(orderEntriesToRemove); // Silently remove order entries with no corresponding children

    return TemplateUi.create(order, reorderedPropertyLabels, reorderedPropertyDescriptions, header, footer);
  }

  private ElementUi readElementUi(ObjectNode sourceNode, String path, String fieldName, Map<String, String> childKey2Name)
  {
    ObjectNode uiNode = readChildNode(sourceNode, path, fieldName);
    String uiPath = path + "/" + fieldName;

    List<String> order = readStringArray(uiNode, uiPath, UI_ORDER);
    LinkedHashMap<String, String> originalPropertyLabels = readString2StringMap(uiNode, uiPath, UI_PROPERTY_LABELS);
    LinkedHashMap<String, String> originalPropertyDescriptions = readString2StringMap(uiNode, uiPath, UI_PROPERTY_DESCRIPTIONS);

    LinkedHashMap<String, String> reorderedPropertyLabels = new LinkedHashMap<>();
    LinkedHashMap<String, String> reorderedPropertyDescriptions = new LinkedHashMap<>();

    Set<String> orderEntriesToRemove = new HashSet<>();

    // Reorder to follow the order list
    for (String childKey: order) {
      if (childKey2Name.containsKey(childKey)) {
        if (originalPropertyLabels.containsKey(childKey))
          reorderedPropertyLabels.put(childKey, originalPropertyLabels.get(childKey));
        else
          reorderedPropertyLabels.put(childKey, childKey2Name.get(childKey));

        if (originalPropertyDescriptions.containsKey(childKey))
          reorderedPropertyDescriptions.put(childKey, originalPropertyDescriptions.get(childKey));
        else
          reorderedPropertyDescriptions.put(childKey, "");
      } else
        orderEntriesToRemove.add(childKey);
    }

    order.removeAll(orderEntriesToRemove); // Silently remove order entries with no corresponding children

    return ElementUi.create(order, reorderedPropertyLabels, reorderedPropertyDescriptions);
  }

  private Optional<Annotations> readAnnotations(ObjectNode sourceNode, String path, String fieldName)
  {
    LinkedHashMap<String, AnnotationValue> annotations = new LinkedHashMap<>();
    ObjectNode annotationNode = readAnnotationsNode(sourceNode, path, fieldName);

    if (annotationNode == null)
      return Optional.empty();

    String annotationPath = path + "/" + fieldName;

    Iterator<Map.Entry<String, JsonNode>> fieldEntries = annotationNode.fields();

    while (fieldEntries.hasNext()) {
      var fieldEntry = fieldEntries.next();
      String annotationName = fieldEntry.getKey();

      if (annotations.containsKey(annotationName))
        throw new ArtifactParseException("Duplicate value for annotation", annotationName, annotationPath);

      JsonNode valueNode = fieldEntry.getValue();

      if (valueNode.isObject()) {
        ObjectNode annotationValueNode = (ObjectNode)valueNode;

        if (annotationValueNode.get(JSON_LD_VALUE) != null) {
          String annnotationValue = readRequiredString(annotationValueNode, annotationPath, JSON_LD_VALUE);
          annotations.put(annotationName, new LiteralAnnotationValue(annnotationValue));
        } else if (annotationValueNode.get(JSON_LD_ID) != null) {
          URI annnotationValue = readRequiredUri(annotationValueNode, annotationPath, JSON_LD_ID);
          annotations.put(annotationName, new IriAnnotationValue(annnotationValue));
        } else
          throw new ArtifactParseException("Value of annotation must contain an @id or @value", annotationName,
            annotationPath);
      } else
        throw new ArtifactParseException("Value of annotation must be an object", annotationName, annotationPath);
    }
    if (!annotations.isEmpty())
      return Optional.of(new Annotations(annotations));
    else
      return Optional.empty();
  }

  private Optional<Integer> readInteger(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isInt())
      throw new ArtifactParseException("Value must be an integer", fieldName, path);

    return Optional.of(jsonNode.asInt());
  }

  private Optional<Number> readNumber(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isNumber())
      throw new ArtifactParseException("Value must be a number", fieldName, path);

    if (jsonNode.isIntegralNumber())
       return Optional.of(jsonNode.asLong());
    else
      return Optional.of(jsonNode.asDouble());
  }

  private boolean readBoolean(ObjectNode sourceNode, String path, String fieldName, boolean defaultValue)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return defaultValue;

    if (!jsonNode.isBoolean())
      throw new ArtifactParseException("Value must be boolean", fieldName, path);

    return jsonNode.asBoolean();
  }

  private Optional<Boolean> readOptionalBoolean(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isBoolean())
      throw new ArtifactParseException("Value must be boolean", fieldName, path);

    return Optional.of(jsonNode.asBoolean());
  }

  private FieldInputType readFieldInputType(ObjectNode sourceNode, String path, String fieldName)
  {
    String inputType = readRequiredString(sourceNode, path, fieldName);

    if (!INPUT_TYPES.contains(inputType))
      throw new ArtifactParseException("Invalid field input type " + inputType, fieldName, path);

    return FieldInputType.fromString(inputType);
  }

  private TemporalGranularity readTemporalGranularity(ObjectNode sourceNode, String path, String fieldName)
  {
    String granularityString = readRequiredString(sourceNode, path, fieldName);

    if (!TEMPORAL_GRANULARITIES.contains(granularityString))
      throw new ArtifactParseException("Invalid granularity " + granularityString, fieldName, path);

    return TemporalGranularity.fromString(granularityString);
  }

  private Optional<InputTimeFormat> readInputTimeFormat(ObjectNode sourceNode, String path, String fieldName)
  {
    Optional<String> timeFormatString = readString(sourceNode, path, fieldName);

    if (timeFormatString.isEmpty())
      return Optional.empty();

    if (!TIME_FORMATS.contains(timeFormatString.get()))
      throw new ArtifactParseException("Invalid time format " + timeFormatString.get(), UI_INPUT_TIME_FORMAT, path);

    return Optional.of(InputTimeFormat.fromString(timeFormatString.get()));
  }

  private ObjectNode readChildNode(ObjectNode parentNode, String path, String fieldName)
  {
    JsonNode childNode = parentNode.get(fieldName);

    if (childNode == null)
      throw new ArtifactParseException("No " + fieldName + " field", fieldName, path);
    else if (childNode.isNull())
      throw new ArtifactParseException("Null " + fieldName + " field", fieldName, path);
    else if (!childNode.isObject())
      throw new ArtifactParseException("Value must be an object", fieldName, path);

     return (ObjectNode)childNode;
  }

  private ObjectNode readValueConstraintsNode(ObjectNode parentNode, String path, String fieldName)
  {
    JsonNode childNode = parentNode.get(fieldName);

    if (childNode == null)
      return null;
    else if (childNode.isNull())
      return null;
    else if (!childNode.isObject())
      throw new ArtifactParseException("Value must be an object", fieldName, path);

    return (ObjectNode)childNode;
  }

  private ObjectNode readAnnotationsNode(ObjectNode parentNode, String path, String fieldName)
  {
    JsonNode childNode = parentNode.get(fieldName);

    if (childNode == null)
      return null;
    else if (childNode.isNull())
      return null;
    else if (!childNode.isObject())
      throw new ArtifactParseException("Value must be an object", fieldName, path);

    return (ObjectNode)childNode;
  }

  private LinkedHashMap<String, String> readString2StringMap(ObjectNode parentNode, String path, String fieldName)
  {
    LinkedHashMap<String, String> string2StringMap = new LinkedHashMap<>();

    JsonNode childNode = parentNode.get(fieldName);

    if (childNode != null && !childNode.isNull()) {

      if (!childNode.isObject())
        throw new ArtifactParseException("Value of field must be an object", fieldName, path);

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = childNode.fields();

      while (fieldEntries.hasNext()) {
        var fieldEntry = fieldEntries.next();

        if (fieldEntry.getValue().isTextual()) {
          String currentFieldName = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          if (currentFieldValue != null)
            string2StringMap.put(currentFieldName, currentFieldValue);
        } else
            throw new ArtifactParseException("Object in field must contain string values", fieldName, path);
      }
    }
    return string2StringMap;
  }

  private LinkedHashMap<String, String> readSimpleContextEntries(ObjectNode parentNode, String path)
  {
    LinkedHashMap<String, String> string2StringMap = new LinkedHashMap<>();

    JsonNode childNode = parentNode.get(JSON_LD_CONTEXT);

    if (childNode != null && !childNode.isNull()) {

      if (!childNode.isObject())
        throw new ArtifactParseException("Value of field must be an object", JSON_LD_CONTEXT, path);

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = childNode.fields();

      while (fieldEntries.hasNext()) {
        var fieldEntry = fieldEntries.next();

        if (fieldEntry.getValue().isTextual()) {
          String currentFieldName = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          if (currentFieldValue != null)
            string2StringMap.put(currentFieldName, currentFieldValue);
        }
      }
    }
    return string2StringMap;
  }


  private LinkedHashMap<String, URI> readString2UriMap(ObjectNode parentNode, String path, String fieldName)
  {
    LinkedHashMap<String, URI> string2UriMap = new LinkedHashMap<>();

    JsonNode childNode = parentNode.get(fieldName);

    if (childNode != null) {

      if (!childNode.isObject())
        throw new ArtifactParseException("Value of field must be an object", fieldName, path);

      Iterator<Map.Entry<String, JsonNode>> fieldEntries = childNode.fields();

      while (fieldEntries.hasNext()) {
        var fieldEntry = fieldEntries.next();

        if (fieldEntry.getValue().isTextual()) { // We only record simple term->term URI entries
          String currentFieldName = fieldEntry.getKey();
          String currentFieldValue = fieldEntry.getValue().textValue();

          try {
            URI currentFieldUriValue = new URI(currentFieldValue);
            string2UriMap.put(currentFieldName, currentFieldUriValue);
          } catch (Exception e) {
            throw new ArtifactParseException("Object in field must contain URI values", fieldName, path);
          }
        }
      }
    }
    return string2UriMap;
  }

  /**
   * Attribute-value fields are defined inside the first element of an "items" array
   */
  private ObjectNode getFieldNode(ObjectNode sourceNode, String path)
  {
    if (sourceNode.isArray()) {
      JsonNode itemsNode = sourceNode.get(JSON_SCHEMA_ITEMS);
      if (itemsNode == null || !itemsNode.isArray() || !itemsNode.iterator().hasNext())
        throw new ArtifactParseException("Expecting array",  JSON_SCHEMA_ITEMS, path);

      JsonNode itemNode = itemsNode.iterator().next();
      if (!itemNode.isObject())
        throw new ArtifactParseException("Expecting object as first element", JSON_SCHEMA_ITEMS, path);
      return (ObjectNode)itemNode;
    } else
      return sourceNode;
  }

  private Optional<Version> readVersion(ObjectNode sourceNode, String path, String fieldName)
  {
    Optional<String> version = readString(sourceNode, path, fieldName);

    if (version.isEmpty())
      return Optional.empty();
    else {
      if (Version.isValidVersion(version.get()))
        return Optional.of(Version.fromString(version.get()));
      else // TODO Revisit this silent version fix
        return Optional.of(Version.fromString("0.0.1"));
    }
  }

  private Optional<Status> readStatus(ObjectNode sourceNode, String path, String fieldName)
  {
    Optional<String> status = readString(sourceNode, path, fieldName);

    if (status.isPresent())
      return Optional.of(Status.fromString(status.get()));
    else
      return Optional.empty();
  }

  private Optional<OffsetDateTime> readOffsetDateTime(ObjectNode sourceNode, String path, String fieldName)
  {
    Optional<String> dateTimeValue = readString(sourceNode, path, fieldName);
    try {
      if (dateTimeValue.isPresent()) {
        // Preprocess non-standard `+0100` timezone offset
        String dateTimeString = dateTimeValue.get();
        // Regex to find and correct timezone offset without colon
        dateTimeString = dateTimeString.replaceAll("([+-]\\d{2})(\\d{2})$", "$1:$2");

        return Optional.of(OffsetDateTime.parse(dateTimeString));
      } else {
        return Optional.empty();
      }
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException(
        "Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(), fieldName, path);
    }
  }

  private OffsetDateTime readRequiredOffsetDateTime(ObjectNode sourceNode, String path, String fieldName)
  {
    String dateTimeValue = readRequiredString(sourceNode, path, fieldName);

    try {
      return OffsetDateTime.parse(dateTimeValue);
    } catch (DateTimeParseException e) {
      throw new ArtifactParseException(
        "Invalid offset datetime value " + dateTimeValue + ": " + e.getMessage(), fieldName, path);
    }
  }

  private Optional<URI> readUri(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();

    if (!jsonNode.isTextual())
      throw new ArtifactParseException("Value of URI field must be textual", fieldName, path);

    String uriValue = jsonNode.asText();

    if (uriValue.isEmpty())
      return Optional.empty();

    if (XsdDatatype.isKnownXsdDatatype(uriValue)) {
      return Optional.of(XsdDatatype.fromString(uriValue).toUri());
    } else {
      try {
        return Optional.of(new URI(uriValue));
      } catch (Exception e) {
        throw new ArtifactParseException("Value " + uriValue + " in URI field must be a valid URI", fieldName, path);
      }
    }
  }

  private Optional<String> readString(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return Optional.empty();
    else if (!jsonNode.isTextual())
      throw new ArtifactParseException("Value of text field must be textual", fieldName, path);
    else
      return Optional.of(jsonNode.asText());
  }

  private String readString(ObjectNode sourceNode, String path, String fieldName, String defaultValue)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null || jsonNode.isNull())
      return defaultValue;
    else if (!jsonNode.isTextual())
      throw new ArtifactParseException("Value of text field must be textual", fieldName, path);
    else
      return jsonNode.asText();
  }

  private String readRequiredString(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null)
      throw new ArtifactParseException("No text value present", fieldName, path);
    else if (jsonNode.isNull())
      throw new ArtifactParseException("Null value present", fieldName, path);
    else {
      if (!jsonNode.isTextual())
        throw new ArtifactParseException("Value must be textual", fieldName, path);

      return jsonNode.asText();
    }
  }

  private int readRequiredInt(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null)
      throw new ArtifactParseException("No int value present", fieldName,  path);
    else if (jsonNode.isNull())
      throw new ArtifactParseException("Null value present", fieldName, path);
    else {
      if (!jsonNode.isInt())
        throw new ArtifactParseException("Value must be an int", fieldName, path);

      return jsonNode.asInt();
    }
  }

  private URI readRequiredUri(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);

    if (jsonNode == null)
      throw new ArtifactParseException("No URI value present", fieldName, path);
    else if (jsonNode.isNull())
      throw new ArtifactParseException("Null value present", fieldName, path);
    else {
      if (!jsonNode.isTextual())
        throw new ArtifactParseException("Value must be a URI", fieldName, path);

      try {
        return new URI(jsonNode.asText());
      } catch (Exception e) {
        throw new ArtifactParseException("Value must be a valid URI", fieldName, path);
      }
    }
  }

  private List<String> readStringArray(ObjectNode sourceNode, String path,  String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);
    List<String> stringValues = new ArrayList<>();

    if (jsonNode != null && !jsonNode.isNull()) {
      if (jsonNode.isArray()) {
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();

        int arrayIndex = 0;
        while (nodeIterator.hasNext()) {
          JsonNode jsonValueNode = nodeIterator.next();
          if (jsonValueNode != null) {
            if (!jsonValueNode.isTextual())
              throw new ArtifactParseException("Value in array at index " + arrayIndex + " must be a string", fieldName, path);
            String stringValue = jsonValueNode.asText();
            if (!stringValue.isEmpty())
              stringValues.add(stringValue);
          }
          arrayIndex++;
        }
      } else {
        String textValue = readString(sourceNode, path, fieldName, "");
        if (textValue != null && !textValue.isEmpty())
          stringValues.add(textValue);
      }
    }
    return stringValues;
  }

  private List<URI> readUriArray(ObjectNode sourceNode, String path, String fieldName)
  {
    JsonNode jsonNode = sourceNode.get(fieldName);
    List<URI> uriValues = new ArrayList<>();

    if (jsonNode != null && !jsonNode.isNull()) {
      if (jsonNode.isArray()) {
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();

        int arrayIndex = 0;
        while (nodeIterator.hasNext()) {
          JsonNode itemNode = nodeIterator.next();
          if (itemNode != null) {
            if (!itemNode.isTextual())
              throw new ArtifactParseException("Value in URI array at index " + arrayIndex + " must be textual", fieldName, path);
            try {
              URI uriValue = new URI(itemNode.asText());
              uriValues.add(uriValue);
            } catch (Exception e) {
              throw new ArtifactParseException("Value in URI array at index " + arrayIndex + " must a valid URI", fieldName, path);
            }
          }
          arrayIndex++;
        }
      } else {
        Optional<URI> uriValue = readUri(sourceNode, path, fieldName);
        if (uriValue.isPresent())
          uriValues.add(uriValue.get());
      }
    }
    return uriValues;
  }

  private Version readModelVersion(ObjectNode sourceNode, String path)
  {
    Optional<String> versionString = readString(sourceNode, path, SCHEMA_ORG_SCHEMA_VERSION);

    if (versionString.isPresent())
      return Version.fromString(versionString.get());
    else
      return Version.fromString("1.6.0"); // TODO Temporarily supply version
  }

  private Optional<String> readLanguage(ObjectNode sourceNode, String path)
  {
    Map<String, String> contextEntries = readSimpleContextEntries(sourceNode, path);

    if (contextEntries.containsKey(JSON_LD_LANGUAGE))
      return Optional.of(contextEntries.get(JSON_LD_LANGUAGE));
    else
      return Optional.empty();
  }

  private boolean hasJsonLdContextField(ObjectNode sourceNode)
  {
    return sourceNode.get(JSON_LD_CONTEXT) != null;
  }
}
