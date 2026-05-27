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
import org.metadatacenter.artifacts.model.core.fields.FieldInputType;
import org.metadatacenter.artifacts.model.core.fields.InputTimeFormat;
import org.metadatacenter.artifacts.model.core.fields.TemporalGranularity;
import org.metadatacenter.artifacts.model.core.fields.constraints.ValueConstraints;
import org.metadatacenter.artifacts.model.core.ui.ElementUi;
import org.metadatacenter.artifacts.model.core.ui.FieldUi;
import org.metadatacenter.artifacts.model.core.ui.NumericFieldUi;
import org.metadatacenter.artifacts.model.core.ui.StaticFieldUi;
import org.metadatacenter.artifacts.model.core.ui.TemplateUi;
import org.metadatacenter.artifacts.model.core.ui.TemporalFieldUi;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
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
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_IS_BASED_ON;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_DESCRIPTION;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_IDENTIFIER;
import static org.metadatacenter.model.ModelNodeNames.SCHEMA_ORG_NAME;
import static org.metadatacenter.model.ModelNodeNames.SKOS_ALTLABEL;
import static org.metadatacenter.model.ModelNodeNames.SKOS_NOTATION;
import static org.metadatacenter.model.ModelNodeNames.SKOS_PREFLABEL;
import static org.metadatacenter.model.ModelNodeNames.STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS;
import static org.metadatacenter.model.ModelNodeNames.TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI;
import static org.metadatacenter.model.ModelNodeNames.UI;
import static org.metadatacenter.model.ModelNodeNames.UI_CONTENT;
import static org.metadatacenter.model.ModelNodeNames.UI_CONTINUE_PREVIOUS_LINE;
import static org.metadatacenter.model.ModelNodeNames.FIELD_INPUT_TYPE_ATTRIBUTE_VALUE;
import static org.metadatacenter.model.ModelNodeNames.UI_FIELD_INPUT_TYPE;
import static org.metadatacenter.model.ModelNodeNames.UI_FOOTER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEADER;
import static org.metadatacenter.model.ModelNodeNames.UI_HEIGHT;
import static org.metadatacenter.model.ModelNodeNames.UI_HIDDEN;
import static org.metadatacenter.model.ModelNodeNames.UI_INPUT_TIME_FORMAT;
import static org.metadatacenter.model.ModelNodeNames.UI_ORDER;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_DESCRIPTIONS;
import static org.metadatacenter.model.ModelNodeNames.UI_PROPERTY_LABELS;
import static org.metadatacenter.model.ModelNodeNames.UI_TEMPORAL_GRANULARITY;
import static org.metadatacenter.model.ModelNodeNames.UI_TIMEZONE_ENABLED;
import static org.metadatacenter.model.ModelNodeNames.UI_VALUE_RECOMMENDATION_ENABLED;
import static org.metadatacenter.model.ModelNodeNames.UI_WIDTH;
import static org.metadatacenter.model.ModelNodeNames.VALUE_CONSTRAINTS;
import static org.metadatacenter.model.ModelNodeValues.TEMPORAL_GRANULARITIES;
import static org.metadatacenter.model.ModelNodeValues.TIME_FORMATS;

import static org.metadatacenter.artifacts.model.reader.JsonArtifactShapeChecks.*;
import static org.metadatacenter.artifacts.model.reader.JsonNodeReaders.*;
import static org.metadatacenter.artifacts.model.reader.JsonValueConstraintsReader.*;

public class JsonArtifactReader implements ArtifactReader<ObjectNode> {

  public JsonArtifactReader() {
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
  public TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode sourceNode) {
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
  public ElementSchemaArtifact readElementSchemaArtifact(ObjectNode sourceNode) {
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
  public FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode sourceNode) {
    // Standalone JSON rendering of an attribute-value field wraps the actual field
    // node in a {type: array, minItems: 0, items: {field}} envelope. Multi-instance
    // (non-AV) fields are rendered the same way only when the model marks them
    // multi-instance. Unwrap the envelope so schema:name etc. can be located, and
    // derive isMultiInstance only for non-AV kinds (for AV the wrapper is structural,
    // not a multi-instance marker).
    ObjectNode fieldNode = sourceNode;
    boolean isMultiInstance = false;
    Optional<Integer> minItems = Optional.empty();
    Optional<Integer> maxItems = Optional.empty();
    JsonNode jsonSchemaTypeNode = sourceNode.get(JSON_SCHEMA_TYPE);
    if (jsonSchemaTypeNode != null && jsonSchemaTypeNode.isTextual()
        && JSON_SCHEMA_ARRAY.equals(jsonSchemaTypeNode.asText())) {
      JsonNode items = sourceNode.get(JSON_SCHEMA_ITEMS);
      if (items == null || !items.isObject())
        throw new ArtifactParseException("Array wrapper missing items object", JSON_SCHEMA_ITEMS, "/");
      fieldNode = (ObjectNode) items;
      JsonNode innerUi = fieldNode.get(UI);
      boolean isAttributeValue = innerUi != null && innerUi.isObject() && innerUi.has(UI_FIELD_INPUT_TYPE)
          && FIELD_INPUT_TYPE_ATTRIBUTE_VALUE.equals(innerUi.get(UI_FIELD_INPUT_TYPE).asText());
      if (!isAttributeValue) {
        isMultiInstance = true;
        minItems = readInteger(sourceNode, "/", JSON_SCHEMA_MIN_ITEMS);
        maxItems = readInteger(sourceNode, "/", JSON_SCHEMA_MAX_ITEMS);
      }
    }

    String name = readRequiredString(fieldNode, "/", SCHEMA_ORG_NAME);
    return readFieldSchemaArtifact(fieldNode, "", name, isMultiInstance, true, minItems, maxItems,
        Optional.empty());
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
  public TemplateInstanceArtifact readTemplateInstanceArtifact(ObjectNode sourceNode) {
    return readTemplateInstanceArtifact(sourceNode, "");
  }

  private TemplateSchemaArtifact readTemplateSchemaArtifact(ObjectNode sourceNode, String path) {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> instanceJsonLdType = readInstanceJsonLdType(sourceNode, path);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    String internalName = readRequiredString(sourceNode, path, JSON_SCHEMA_TITLE);
    String internalDescription = readString(sourceNode, path, JSON_SCHEMA_DESCRIPTION, "");
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
    Map<String, String> childSchemaOrgNames = readNestedFieldAndElementSchemaArtifacts(sourceNode, path, fieldSchemas,
        elementSchemas, childPropertyUris);
    TemplateUi templateUi = readTemplateUi(sourceNode, path, UI, childSchemaOrgNames);

    checkArtifactJsonSchemaSchemaUri(sourceNode, path);
    checkArtifactJsonSchemaType(sourceNode, path, JSON_SCHEMA_OBJECT);
    checkTemplateSchemaArtifactJsonLdType(jsonLdTypes, path);
    checkSchemaArtifactModelVersion(sourceNode, path);

    return TemplateSchemaArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, instanceJsonLdType, name, description,
        identifier, version, status, previousVersion, derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        fieldSchemas, elementSchemas, language, templateUi, annotations, internalName, internalDescription);
  }

  private ElementSchemaArtifact readElementSchemaArtifact(ObjectNode sourceNode, String path, String childKey,
                                                          boolean isMultiInstance, Optional<Integer> minItems,
                                                          Optional<Integer> maxItems, Optional<URI> propertyUri) {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> instanceJsonLdType = readInstanceJsonLdType(sourceNode, path);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    String internalName = readRequiredString(sourceNode, path, JSON_SCHEMA_TITLE);
    String internalDescription = readString(sourceNode, path, JSON_SCHEMA_DESCRIPTION, "");
    String schemaOrgName = readRequiredString(sourceNode, path, SCHEMA_ORG_NAME);
    String schemaOrgDescription = readRequiredString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    Optional<String> schemaOrgIdentifier = readString(sourceNode, path, SCHEMA_ORG_IDENTIFIER);
    Optional<Version> version = readVersion(sourceNode, path, PAV_VERSION);
    Optional<Status> status = readStatus(sourceNode, path, BIBO_STATUS);
    Optional<URI> previousVersion = readUri(sourceNode, path, PAV_PREVIOUS_VERSION);
    Optional<URI> derivedFrom = readUri(sourceNode, path, PAV_DERIVED_FROM);
    LinkedHashMap<String, FieldSchemaArtifact> fieldSchemas = new LinkedHashMap<>();
    LinkedHashMap<String, ElementSchemaArtifact> elementSchemas = new LinkedHashMap<>();
    Optional<String> language = readLanguage(sourceNode, path);
    LinkedHashMap<String, URI> childPropertyUris = getChildPropertyUris(sourceNode, path);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path, ANNOTATIONS);

    checkArtifactJsonSchemaSchemaUri(sourceNode, path);
    checkArtifactJsonSchemaType(sourceNode, path, JSON_SCHEMA_OBJECT);
    checkElementSchemaArtifactJsonLdType(jsonLdTypes, path);
    checkSchemaArtifactModelVersion(sourceNode, path);

    Map<String, String> childSchemaOrgNames = readNestedFieldAndElementSchemaArtifacts(sourceNode, path, fieldSchemas,
        elementSchemas, childPropertyUris);

    ElementUi elementUi = readElementUi(sourceNode, path, UI, childSchemaOrgNames);

    return ElementSchemaArtifact.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId,
        instanceJsonLdType, schemaOrgName, schemaOrgDescription, schemaOrgIdentifier, version, status, previousVersion,
        derivedFrom, createdBy, modifiedBy, createdOn, lastUpdatedOn, fieldSchemas, elementSchemas, isMultiInstance,
        minItems, maxItems, propertyUri, language, elementUi, annotations);
  }

  private FieldSchemaArtifact readFieldSchemaArtifact(ObjectNode sourceNode, String path, String childKey,
                                                      boolean isMultiInstance, boolean isStandalone,
                                                      Optional<Integer> minItems, Optional<Integer> maxItems,
                                                      Optional<URI> propertyUri) {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    String internalName = readRequiredString(sourceNode, path, JSON_SCHEMA_TITLE);
    String internalDescription = readString(sourceNode, path, JSON_SCHEMA_DESCRIPTION, "");
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
    Optional<ValueConstraints> valueConstraints = readValueConstraints(sourceNode, path, VALUE_CONSTRAINTS,
        fieldUi.inputType(), isMultiInstance, isStandalone);
    Optional<Annotations> annotations = readAnnotations(sourceNode, path, ANNOTATIONS);

    checkArtifactJsonSchemaSchemaUri(sourceNode, path);
    checkFieldSchemaArtifactJsonLdType(jsonLdTypes, path);
    checkSchemaArtifactModelVersion(sourceNode, path);

    // TODO: Update isMultiInstance, minItems, maxItems

    return FieldSchemaArtifact.create(internalName, internalDescription, jsonLdContext, jsonLdTypes, jsonLdId,
        schemaOrgName, schemaOrgDescription, schemaOrgIdentifier, version, status, previousVersion, derivedFrom,
        isMultiInstance, minItems, maxItems, propertyUri, createdBy, modifiedBy, createdOn, lastUpdatedOn,
        preferredLabel, alternateLabels, language, fieldUi, valueConstraints, annotations);
  }

  private Map<String, String> readNestedFieldAndElementSchemaArtifacts(ObjectNode parentNode, String path,
                                                                       Map<String, FieldSchemaArtifact> fieldSchemas,
                                                                       Map<String, ElementSchemaArtifact> elementSchemas,
                                                                       Map<String, URI> childPropertyUris) {
    JsonNode propertiesNode = parentNode.get(JSON_SCHEMA_PROPERTIES);
    Map<String, String> childSchemaOrgNames = new HashMap<>();

    if (propertiesNode == null || !propertiesNode.isObject()) {
      throw new ArtifactParseException("Invalid JSON Schema properties node", JSON_SCHEMA_PROPERTIES, path);
    }

    Iterator<String> jsonChildKeys = propertiesNode.fieldNames();

    while (jsonChildKeys.hasNext()) {
      String childKey = jsonChildKeys.next();
      boolean isMultiInstance = false;
      Optional<Integer> minItems = Optional.empty();
      Optional<Integer> maxItems = Optional.empty();

      // The /properties field for each schema artifact contains entries constraining fields in instances
      if (!TEMPLATE_INSTANCE_ARTIFACT_KEYWORDS.contains(childKey)) {
        if (!FIELD_INSTANCE_ARTIFACT_KEYWORDS.contains(childKey) && !ELEMENT_INSTANCE_ARTIFACT_KEYWORDS.contains(
            childKey)) {
          JsonNode jsonFieldOrElementSchemaArtifactNode = propertiesNode.get(childKey);
          String fieldOrElementPath = path + "/properties/" + childKey;

          if (jsonFieldOrElementSchemaArtifactNode.isObject()) {

            String jsonSchemaType = readRequiredString((ObjectNode) jsonFieldOrElementSchemaArtifactNode,
                fieldOrElementPath, JSON_SCHEMA_TYPE);

            if (jsonSchemaType.equals(JSON_SCHEMA_ARRAY)) {

              isMultiInstance = true;

              minItems = readInteger((ObjectNode) jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath,
                  JSON_SCHEMA_MIN_ITEMS);

              maxItems = readInteger((ObjectNode) jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath,
                  JSON_SCHEMA_MAX_ITEMS);

              jsonFieldOrElementSchemaArtifactNode = jsonFieldOrElementSchemaArtifactNode.get(JSON_SCHEMA_ITEMS);

              if (jsonFieldOrElementSchemaArtifactNode == null) {
                throw new ArtifactParseException("No items field in array", JSON_SCHEMA_ITEMS, fieldOrElementPath);
              }

              fieldOrElementPath += "/items";

              if (!jsonFieldOrElementSchemaArtifactNode.isObject()) {
                throw new ArtifactParseException("Non-object items content in array", JSON_SCHEMA_ITEMS,
                    fieldOrElementPath);
              }
            } else if (!jsonSchemaType.equals(JSON_SCHEMA_OBJECT)) {
              throw new ArtifactParseException("Expecting array or object, got " + jsonSchemaType, JSON_SCHEMA_ITEMS,
                  fieldOrElementPath);
            }

            List<URI> subSchemaArtifactJsonLdTypes = readUriArray((ObjectNode) jsonFieldOrElementSchemaArtifactNode,
                fieldOrElementPath, JSON_LD_TYPE);

            checkSchemaArtifactJsonLdType(subSchemaArtifactJsonLdTypes, fieldOrElementPath);

            URI subSchemaArtifactJsonLdType = subSchemaArtifactJsonLdTypes.get(0);
            Optional<URI> propertyUri = childPropertyUris.containsKey(childKey) ?
                Optional.of(childPropertyUris.get(childKey)) :
                Optional.empty();

            switch (subSchemaArtifactJsonLdType.toString()) {
              case TEMPLATE_SCHEMA_ARTIFACT_TYPE_IRI ->
                  throw new ArtifactParseException("Invalid nesting of template schema artifact", childKey,
                      fieldOrElementPath);
              case ELEMENT_SCHEMA_ARTIFACT_TYPE_IRI -> {
                ElementSchemaArtifact elementSchemaArtifact = readElementSchemaArtifact(
                    (ObjectNode) jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath, childKey, isMultiInstance,
                    minItems, maxItems, propertyUri);
                elementSchemas.put(childKey, elementSchemaArtifact);
                childSchemaOrgNames.put(childKey, elementSchemaArtifact.name());
              }
              case FIELD_SCHEMA_ARTIFACT_TYPE_IRI, STATIC_FIELD_SCHEMA_ARTIFACT_TYPE_IRI -> {
                FieldSchemaArtifact fieldSchemaArtifact = readFieldSchemaArtifact(
                    (ObjectNode) jsonFieldOrElementSchemaArtifactNode, fieldOrElementPath, childKey, isMultiInstance,
                    false,
                    minItems, maxItems, propertyUri);
                fieldSchemas.put(childKey, fieldSchemaArtifact);
                childSchemaOrgNames.put(childKey, fieldSchemaArtifact.name());
              }
              default ->
                  throw new ArtifactParseException("Unknown JSON-LD @type " + subSchemaArtifactJsonLdType, childKey,
                      fieldOrElementPath);
            }

          } else {
            throw new ArtifactParseException("Unknown non-object schema artifact", childKey, fieldOrElementPath);
          }
        }
      }
    }
    return childSchemaOrgNames;
  }

  private TemplateInstanceArtifact readTemplateInstanceArtifact(ObjectNode sourceNode, String path) {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    URI isBasedOn = readRequiredUri(sourceNode, path, SCHEMA_IS_BASED_ON);
    Optional<URI> derivedFrom = readUri(sourceNode, path, PAV_DERIVED_FROM);
    Optional<String> name = readString(sourceNode, path, SCHEMA_ORG_NAME);
    Optional<String> description = readString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    List<String> childKeys = new ArrayList<>();
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances = new LinkedHashMap<>();
    Optional<Annotations> annotations = readAnnotations(sourceNode, path, ANNOTATIONS);

    readNestedInstanceArtifacts(sourceNode, path, childKeys, singleInstanceFieldInstances, multiInstanceFieldInstances,
        singleInstanceElementInstances, multiInstanceElementInstances, attributeValueFieldInstances);

    return TemplateInstanceArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, isBasedOn, derivedFrom, childKeys, singleInstanceFieldInstances,
        multiInstanceFieldInstances, singleInstanceElementInstances, multiInstanceElementInstances,
        attributeValueFieldInstances, annotations);
  }

  private ElementInstanceArtifact readElementInstanceArtifact(ObjectNode sourceNode, String path) {
    LinkedHashMap<String, URI> jsonLdContext = readString2UriMap(sourceNode, path, JSON_LD_CONTEXT);
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<URI> createdBy = readUri(sourceNode, path, PAV_CREATED_BY);
    Optional<URI> modifiedBy = readUri(sourceNode, path, OSLC_MODIFIED_BY);
    Optional<OffsetDateTime> createdOn = readOffsetDateTime(sourceNode, path, PAV_CREATED_ON);
    Optional<OffsetDateTime> lastUpdatedOn = readOffsetDateTime(sourceNode, path, PAV_LAST_UPDATED_ON);
    Optional<String> name = readString(sourceNode, path, SCHEMA_ORG_NAME);
    Optional<String> description = readString(sourceNode, path, SCHEMA_ORG_DESCRIPTION);
    List<String> childKeys = new ArrayList<>();
    LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances = new LinkedHashMap<>();
    LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances = new LinkedHashMap<>();
    LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstances = new LinkedHashMap<>();

    readNestedInstanceArtifacts(sourceNode, path, childKeys, singleInstanceFieldInstances, multiInstanceFieldInstances,
        singleInstanceElementInstances, multiInstanceElementInstances, attributeValueFieldInstances);

    return ElementInstanceArtifact.create(jsonLdContext, jsonLdTypes, jsonLdId, name, description, createdBy,
        modifiedBy, createdOn, lastUpdatedOn, childKeys, singleInstanceFieldInstances, multiInstanceFieldInstances,
        singleInstanceElementInstances, multiInstanceElementInstances, attributeValueFieldInstances);
  }

  private FieldInstanceArtifact readFieldInstanceArtifact(ObjectNode sourceNode, String path) {
    List<URI> jsonLdTypes = readUriArray(sourceNode, path, JSON_LD_TYPE);
    Optional<URI> jsonLdId = readUri(sourceNode, path, JSON_LD_ID);
    Optional<String> jsonLdValue = readPossiblyNullString(sourceNode, path, JSON_LD_VALUE);
    Optional<String> rdfsLabel = readString(sourceNode, path, RDFS_LABEL);
    Optional<String> language = readString(sourceNode, path, JSON_LD_LANGUAGE);
    Optional<String> notation = readString(sourceNode, path, SKOS_NOTATION);
    Optional<String> preferredLabel = readString(sourceNode, path, SKOS_PREFLABEL);

    return FieldInstanceArtifact.create(jsonLdTypes, jsonLdId, jsonLdValue, rdfsLabel, notation, preferredLabel,
        language);
  }

  private void readNestedInstanceArtifacts(ObjectNode parentNode, String path, List<String> childKeys,
                                           LinkedHashMap<String, FieldInstanceArtifact> singleInstanceFieldInstances,
                                           LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
                                           LinkedHashMap<String, ElementInstanceArtifact> singleInstanceElementInstances,
                                           LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances,
                                           LinkedHashMap<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups) {
    LinkedHashMap<String, List<String>> attributeValueFieldGroupInstanceNames = new LinkedHashMap<>();
    Iterator<String> instanceArtifactFieldKeys = parentNode.fieldNames();

    while (instanceArtifactFieldKeys.hasNext()) {
      String instanceArtifactFieldKey = instanceArtifactFieldKeys.next();

      if (!INSTANCE_ARTIFACT_KEYWORDS.contains(instanceArtifactFieldKey)) {
        JsonNode nestedNode = parentNode.get(instanceArtifactFieldKey);
        String nestedInstanceArtifactPath = path + "/" + instanceArtifactFieldKey;

        if (nestedNode.isObject()) {
          ObjectNode nestedInstanceArtifactNode = (ObjectNode) nestedNode;

          readNestedSingleInstanceArtifact(instanceArtifactFieldKey, nestedInstanceArtifactPath,
              nestedInstanceArtifactNode, childKeys, singleInstanceFieldInstances, singleInstanceElementInstances);

        } else if (nestedNode.isArray()) {
          Iterator<JsonNode> nodeIterator = nestedNode.iterator();

          if (childKeys.contains(instanceArtifactFieldKey)) {
            throw new ArtifactParseException("Duplicate field " + instanceArtifactFieldKey, instanceArtifactFieldKey,
                instanceArtifactFieldKey);
          }
          childKeys.add(instanceArtifactFieldKey);

          if (!nodeIterator.hasNext()) { // Array is empty
            // We do not know if this is (1) an empty attribute-value field array, (2) an empty multi-instance field
            // array, or (3) an empty multi-instance element array. We'll arbitrarily pick (2).
            multiInstanceFieldInstances.put(instanceArtifactFieldKey, Collections.emptyList());
          } else {
            int arrayIndex = 0;
            while (nodeIterator.hasNext()) {
              String arrayEnclosedInstanceArtifactPath = nestedInstanceArtifactPath + "[" + arrayIndex + "]";
              JsonNode instanceNode = nodeIterator.next();
              if (instanceNode == null || instanceNode.isNull()) {
                throw new ArtifactParseException(
                    "Expecting field or element instance or attribute-value field name in array, got null",
                    instanceArtifactFieldKey, arrayEnclosedInstanceArtifactPath);
              } else {
                if (instanceNode.isObject()) {
                  ObjectNode arrayEnclosedInstanceArtifactNode = (ObjectNode) instanceNode;
                  readNestedMultiInstanceArtifact(instanceArtifactFieldKey, arrayEnclosedInstanceArtifactPath,
                      arrayEnclosedInstanceArtifactNode, childKeys, multiInstanceFieldInstances,
                      multiInstanceElementInstances);
                } else if (instanceNode.isTextual()) { // A list of attribute-value field names
                  String attributeValueFieldName = instanceNode.asText();
                  if (attributeValueFieldName.isEmpty()) {
                    throw new ArtifactParseException("Empty attribute-value field name in array",
                        instanceArtifactFieldKey, arrayEnclosedInstanceArtifactPath);
                  }

                  if (attributeValueFieldGroupInstanceNames.containsKey(instanceArtifactFieldKey)) {
                    attributeValueFieldGroupInstanceNames.get(instanceArtifactFieldKey).add(attributeValueFieldName);
                  } else {
                    List<String> attributeValueFieldInstanceNames = new ArrayList<>();
                    attributeValueFieldInstanceNames.add(attributeValueFieldName);
                    attributeValueFieldGroupInstanceNames.put(instanceArtifactFieldKey,
                        attributeValueFieldInstanceNames);
                  }
                } else {
                  throw new ArtifactParseException(
                      "Expecting field or element instance or attribute-value field name in array",
                      instanceArtifactFieldKey, arrayEnclosedInstanceArtifactPath);
                }
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
                                           Map<String, List<String>> attributeValueFieldGroupInstanceNames,
                                           Map<String, Map<String, FieldInstanceArtifact>> attributeValueFieldInstanceGroups) {
    for (var attributeValueFieldGroupsEntry : attributeValueFieldGroupInstanceNames.entrySet()) {
      String attributeValueFieldGroupName = attributeValueFieldGroupsEntry.getKey();
      List<String> attributeValueFieldInstanceNames = attributeValueFieldGroupsEntry.getValue();

      for (String attributeValueFieldInstanceName : attributeValueFieldInstanceNames) {

        if (!singleInstanceFieldInstances.containsKey(attributeValueFieldInstanceName)) {
          throw new ArtifactParseException(
              "Attribute-value field group " + attributeValueFieldGroupName + " specifies an instance field "
                  + attributeValueFieldInstanceName + " that is not present in the template or element instance",
              attributeValueFieldGroupName, path);
        }

        FieldInstanceArtifact perAttributeFieldInstance = singleInstanceFieldInstances.get(
            attributeValueFieldInstanceName);

        if (attributeValueFieldInstanceGroups.containsKey(attributeValueFieldGroupName)) {
          attributeValueFieldInstanceGroups.get(attributeValueFieldGroupName)
              .put(attributeValueFieldInstanceName, perAttributeFieldInstance);
        } else {
          attributeValueFieldInstanceGroups.put(attributeValueFieldGroupName, new LinkedHashMap<>());
          attributeValueFieldInstanceGroups.get(attributeValueFieldGroupName)
              .put(attributeValueFieldInstanceName, perAttributeFieldInstance);
        }
        singleInstanceFieldInstances.remove(
            attributeValueFieldInstanceName); // Remove it from the single-instance fields
      }
    }
  }

  private void readNestedSingleInstanceArtifact(String instanceArtifactFieldKey, String instanceArtifactPath,
                                                ObjectNode instanceArtifactNode, List<String> childKeys,
                                                Map<String, FieldInstanceArtifact> singleInstanceFieldInstances,
                                                Map<String, ElementInstanceArtifact> singleInstanceElementInstances) {
    if (childKeys.contains(instanceArtifactFieldKey)) {
      throw new ArtifactParseException("duplicate field " + instanceArtifactFieldKey, instanceArtifactFieldKey,
          instanceArtifactPath);
    }

    childKeys.add(instanceArtifactFieldKey);

    if (hasJsonLdContextField(instanceArtifactNode)) { // Element instance artifacts have @context fields
      ObjectNode elementInstanceArtifactNode = instanceArtifactNode;
      ElementInstanceArtifact elementInstanceArtifact = readElementInstanceArtifact(elementInstanceArtifactNode,
          instanceArtifactPath);
      singleInstanceElementInstances.put(instanceArtifactFieldKey, elementInstanceArtifact);
    } else { // Field instance artifacts do not
      FieldInstanceArtifact fieldInstanceArtifact = readFieldInstanceArtifact(instanceArtifactNode,
          instanceArtifactPath);
      singleInstanceFieldInstances.put(instanceArtifactFieldKey, fieldInstanceArtifact);
    }
  }

  private void readNestedMultiInstanceArtifact(String instanceArtifactFieldKey, String instanceArtifactPath,
                                               ObjectNode instanceArtifactArrayNode, List<String> childKeys,
                                               LinkedHashMap<String, List<FieldInstanceArtifact>> multiInstanceFieldInstances,
                                               LinkedHashMap<String, List<ElementInstanceArtifact>> multiInstanceElementInstances) {

    if (instanceArtifactArrayNode == null || instanceArtifactArrayNode.isNull()) {
      throw new ArtifactParseException("Value in instance array must not be null", instanceArtifactFieldKey,
          instanceArtifactPath);
    }

    if (hasJsonLdContextField(instanceArtifactArrayNode)) { // Element instance artifacts have @context fields
      ObjectNode elementInstanceArtifactNode = instanceArtifactArrayNode;
      ElementInstanceArtifact elementInstanceArtifact = readElementInstanceArtifact(elementInstanceArtifactNode,
          instanceArtifactPath);

      if (!multiInstanceElementInstances.containsKey(instanceArtifactFieldKey)) {
        multiInstanceElementInstances.put(instanceArtifactFieldKey, new ArrayList<>());
      }

      multiInstanceElementInstances.get(instanceArtifactFieldKey).add(elementInstanceArtifact);
    } else { // Field instance artifacts do not
      FieldInstanceArtifact fieldInstanceArtifact = readFieldInstanceArtifact(instanceArtifactArrayNode,
          instanceArtifactPath);

      if (!multiInstanceFieldInstances.containsKey(instanceArtifactFieldKey)) {
        multiInstanceFieldInstances.put(instanceArtifactFieldKey, new ArrayList<>());
      }

      multiInstanceFieldInstances.get(instanceArtifactFieldKey).add(fieldInstanceArtifact);
    }
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
  private LinkedHashMap<String, URI> getChildPropertyUris(ObjectNode sourceNode, String path) {
    LinkedHashMap<String, URI> childKey2URI = new LinkedHashMap<>();
    String contextPath = "/" + JSON_SCHEMA_PROPERTIES + "/" + JSON_LD_CONTEXT + "/" + JSON_SCHEMA_PROPERTIES;
    JsonNode contextNode = sourceNode.at(contextPath);

    if (contextNode != null && contextNode.isObject()) {
      ObjectNode jsonSchemaContextSpecificationNode = (ObjectNode) contextNode;
      Iterator<String> childKeys = jsonSchemaContextSpecificationNode.fieldNames();

      while (childKeys.hasNext()) {
        String childKey = childKeys.next();
        if (!ARTIFACT_CONTEXT_ENTRIES.contains(childKey)) { // Ignore standard context entries
          JsonNode enumNode = jsonSchemaContextSpecificationNode.get(childKey);

          if (enumNode != null) { // A property URI specification for a child is optional
            if (!enumNode.isObject()) {
              throw new ArtifactParseException("Expecting object node with property URI enum specification", childKey,
                  path + contextPath);
            }

            JsonNode enumArray = enumNode.get(JSON_SCHEMA_ENUM);

            if (enumArray == null || !enumArray.isArray()) {
              throw new ArtifactParseException("Expecting array for property URI enum specification", JSON_SCHEMA_ENUM,
                  path + contextPath + childKey);
            }

            if (enumArray.size() != 1) {
              throw new ArtifactParseException(
                  "Expecting exactly one value for property URI enum specification, got " + enumArray.size(),
                  JSON_SCHEMA_ENUM, path + contextPath + childKey);
            }

            JsonNode elementNode = enumArray.get(0);

            if (!elementNode.isTextual()) {
              throw new ArtifactParseException(
                  "Expecting text node for property URI enum entry, got " + elementNode.getNodeType(), JSON_SCHEMA_ENUM,
                  path + contextPath + childKey);
            }

            try {
              URI propertyUri = new URI(elementNode.asText());
              childKey2URI.put(childKey, propertyUri);
            } catch (URISyntaxException e) {
              throw new ArtifactParseException("Invalid URI " + elementNode.asText() + " for enum specification",
                  JSON_SCHEMA_ENUM, path + contextPath + childKey);
            }
          }
        }
      }
    }
    return childKey2URI;
  }

  /**
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
  private Optional<URI> readInstanceJsonLdType(ObjectNode sourceNode, String path) {
    String uriPath =
        "/" + JSON_SCHEMA_PROPERTIES + "/" + JSON_LD_TYPE + "/" + JSON_SCHEMA_ONE_OF + "/0/" + JSON_SCHEMA_ENUM + "/0";
    JsonNode uriNode = sourceNode.at(uriPath);

    if (uriNode != null && uriNode.isTextual()) {
      return Optional.of(URI.create(uriNode.asText()));
    } else {
      return Optional.empty();
    }
  }

  private FieldUi readFieldUi(ObjectNode sourceNode, String path, String fieldKey) {
    ObjectNode uiNode = readChildNode(sourceNode, path, fieldKey);
    String uiPath = path + "/" + fieldKey;

    FieldInputType fieldInputType = readFieldInputType(uiNode, uiPath, UI_FIELD_INPUT_TYPE);
    boolean valueRecommendationEnabled = readBoolean(uiNode, uiPath, UI_VALUE_RECOMMENDATION_ENABLED, false);
    boolean hidden = readBoolean(uiNode, uiPath, UI_HIDDEN, false);
    boolean continuePreviousLine = readBoolean(uiNode, uiPath, UI_CONTINUE_PREVIOUS_LINE, false);
    Optional<Integer> width = readInteger(uiNode, uiPath, UI_WIDTH);
    Optional<Integer> height = readInteger(uiNode, uiPath, UI_HEIGHT);

    if (fieldInputType.isTemporal()) {
      TemporalGranularity temporalGranularity = readTemporalGranularity(uiNode, uiPath, UI_TEMPORAL_GRANULARITY);
      Optional<InputTimeFormat> inputTimeFormat = readInputTimeFormat(uiNode, uiPath, UI_INPUT_TIME_FORMAT);
      Optional<Boolean> timeZoneEnabled = readOptionalBoolean(uiNode, uiPath, UI_TIMEZONE_ENABLED);

      return TemporalFieldUi.create(temporalGranularity, inputTimeFormat, timeZoneEnabled, hidden,
          continuePreviousLine);
    } else if (fieldInputType.isNumeric()) {
      return NumericFieldUi.create(hidden, continuePreviousLine);
    } else if (fieldInputType.isStatic()) {
      Optional<String> content = readString(uiNode, uiPath, UI_CONTENT);
      return StaticFieldUi.create(fieldInputType, content, hidden, continuePreviousLine, width, height);
    } else {
      return FieldUi.create(fieldInputType, hidden, continuePreviousLine, valueRecommendationEnabled);
    }
  }

  private TemplateUi readTemplateUi(ObjectNode sourceNode, String path, String fieldKey,
                                    Map<String, String> childKey2Name) {
    ObjectNode uiNode = readChildNode(sourceNode, path, fieldKey);
    String uiPath = path + "/" + fieldKey;

    List<String> order = readStringArray(uiNode, uiPath, UI_ORDER);
    LinkedHashMap<String, String> originalPropertyLabels = readString2StringMap(uiNode, uiPath, UI_PROPERTY_LABELS);
    LinkedHashMap<String, String> originalPropertyDescriptions = readString2StringMap(uiNode, uiPath,
        UI_PROPERTY_DESCRIPTIONS);
    Optional<String> header = readString(uiNode, uiPath, UI_HEADER);
    Optional<String> footer = readString(uiNode, uiPath, UI_FOOTER);

    LinkedHashMap<String, String> reorderedPropertyLabels = new LinkedHashMap<>();
    LinkedHashMap<String, String> reorderedPropertyDescriptions = new LinkedHashMap<>();

    Set<String> orderEntriesToRemove = new HashSet<>();

    // Reorder to follow the order list
    for (String childKey : order) {
      if (childKey2Name.containsKey(childKey)) {
        if (originalPropertyLabels.containsKey(childKey)) {
          reorderedPropertyLabels.put(childKey, originalPropertyLabels.get(childKey));
        }

        if (originalPropertyDescriptions.containsKey(childKey)) {
          reorderedPropertyDescriptions.put(childKey, originalPropertyDescriptions.get(childKey));
        }
      } else {
        orderEntriesToRemove.add(childKey);
      }
    }

    order.removeAll(orderEntriesToRemove); // Silently remove order entries with no corresponding children

    return TemplateUi.create(order, reorderedPropertyLabels, reorderedPropertyDescriptions, header, footer);
  }

  private ElementUi readElementUi(ObjectNode sourceNode, String path, String fieldKey,
                                  Map<String, String> childKey2Name) {
    ObjectNode uiNode = readChildNode(sourceNode, path, fieldKey);
    String uiPath = path + "/" + fieldKey;

    List<String> order = readStringArray(uiNode, uiPath, UI_ORDER);
    LinkedHashMap<String, String> originalPropertyLabels = readString2StringMap(uiNode, uiPath, UI_PROPERTY_LABELS);
    LinkedHashMap<String, String> originalPropertyDescriptions = readString2StringMap(uiNode, uiPath,
        UI_PROPERTY_DESCRIPTIONS);

    LinkedHashMap<String, String> reorderedPropertyLabels = new LinkedHashMap<>();
    LinkedHashMap<String, String> reorderedPropertyDescriptions = new LinkedHashMap<>();

    Set<String> orderEntriesToRemove = new HashSet<>();

    // Reorder to follow the order list
    for (String childKey : order) {
      if (childKey2Name.containsKey(childKey)) {
        if (originalPropertyLabels.containsKey(childKey)) {
          reorderedPropertyLabels.put(childKey, originalPropertyLabels.get(childKey));
        }

        if (originalPropertyDescriptions.containsKey(childKey)) {
          reorderedPropertyDescriptions.put(childKey, originalPropertyDescriptions.get(childKey));
        }
      } else {
        orderEntriesToRemove.add(childKey);
      }
    }

    order.removeAll(orderEntriesToRemove); // Silently remove order entries with no corresponding children

    return ElementUi.create(order, reorderedPropertyLabels, reorderedPropertyDescriptions);
  }

  private Optional<Annotations> readAnnotations(ObjectNode sourceNode, String path, String fieldKey) {
    LinkedHashMap<String, AnnotationValue> annotations = new LinkedHashMap<>();
    ObjectNode annotationNode = readAnnotationsNode(sourceNode, path, fieldKey);

    if (annotationNode == null) {
      return Optional.empty();
    }

    String annotationPath = path + "/" + fieldKey;

    Iterator<Map.Entry<String, JsonNode>> fieldEntries = annotationNode.fields();

    while (fieldEntries.hasNext()) {
      var fieldEntry = fieldEntries.next();
      String annotationName = fieldEntry.getKey();

      if (annotations.containsKey(annotationName)) {
        throw new ArtifactParseException("Duplicate value for annotation", annotationName, annotationPath);
      }

      JsonNode valueNode = fieldEntry.getValue();

      if (valueNode.isObject()) {
        ObjectNode annotationValueNode = (ObjectNode) valueNode;

        if (annotationValueNode.get(JSON_LD_VALUE) != null) {
          String annnotationValue = readRequiredString(annotationValueNode, annotationPath, JSON_LD_VALUE);
          annotations.put(annotationName, new LiteralAnnotationValue(annnotationValue));
        } else if (annotationValueNode.get(JSON_LD_ID) != null) {
          URI annnotationValue = readRequiredUri(annotationValueNode, annotationPath, JSON_LD_ID);
          annotations.put(annotationName, new IriAnnotationValue(annnotationValue));
        } else {
          throw new ArtifactParseException("Value of annotation must contain an @id or @value", annotationName,
              annotationPath);
        }
      } else {
        throw new ArtifactParseException("Value of annotation must be an object", annotationName, annotationPath);
      }
    }
    if (!annotations.isEmpty()) {
      return Optional.of(new Annotations(annotations));
    } else {
      return Optional.empty();
    }
  }

  private FieldInputType readFieldInputType(ObjectNode sourceNode, String path, String fieldKey) {
    String inputType = readRequiredString(sourceNode, path, fieldKey);

    if (!INPUT_TYPES.contains(inputType)) {
      throw new ArtifactParseException("Invalid field input type " + inputType, fieldKey, path);
    }

    return FieldInputType.fromString(inputType);
  }

  private TemporalGranularity readTemporalGranularity(ObjectNode sourceNode, String path, String fieldKey) {
    String granularityString = readRequiredString(sourceNode, path, fieldKey);

    if (!TEMPORAL_GRANULARITIES.contains(granularityString)) {
      throw new ArtifactParseException("Invalid granularity " + granularityString, fieldKey, path);
    }

    return TemporalGranularity.fromString(granularityString);
  }

  private Optional<InputTimeFormat> readInputTimeFormat(ObjectNode sourceNode, String path, String fieldKey) {
    Optional<String> timeFormatString = readString(sourceNode, path, fieldKey);

    if (timeFormatString.isEmpty()) {
      return Optional.empty();
    }

    if (!TIME_FORMATS.contains(timeFormatString.get())) {
      throw new ArtifactParseException("Invalid time format " + timeFormatString.get(), UI_INPUT_TIME_FORMAT, path);
    }

    return Optional.of(InputTimeFormat.fromString(timeFormatString.get()));
  }

  /**
   * Attribute-value fields are defined inside the first element of an "items" array
   */
}
