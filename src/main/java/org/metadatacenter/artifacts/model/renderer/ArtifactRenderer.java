package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
import org.metadatacenter.artifacts.model.core.MonitoredArtifact;
import org.metadatacenter.artifacts.model.core.SchemaArtifact;
import org.metadatacenter.artifacts.model.core.TemplateSchemaArtifact;
import org.metadatacenter.model.ModelNodeNames;

import java.net.URI;

public class ArtifactRenderer
{
  private final ObjectMapper mapper;

  public ArtifactRenderer(ObjectMapper mapper)
  {
    this.mapper = mapper;
  }

  public ObjectNode renderTemplateSchemaArtifact(TemplateSchemaArtifact templateSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(templateSchemaArtifact);

    rendering.set(ModelNodeNames.JSON_LD_CONTEXT, renderParentSchemaArtifactContextJsonLdSpecification());

    // TODO properties

    rendering.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_CONTEXT);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_ID);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.SCHEMA_IS_BASED_ON);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.SCHEMA_ORG_NAME);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.SCHEMA_ORG_DESCRIPTION);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.PAV_CREATED_ON);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.PAV_CREATED_BY);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.PAV_LAST_UPDATED_ON);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.OSLC_MODIFIED_BY);

    for (String childName : templateSchemaArtifact.getChildNames())
      rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(childName);

    if (templateSchemaArtifact.hasAttributeValueField())
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES,
        renderAdditionalPropertiesForAttributeValueFieldJsonSchemaSpecification());
    else
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    rendering.put(ModelNodeNames.UI, mapper.valueToTree(templateSchemaArtifact.getTemplateUI()));

    return rendering;
  }

  public ObjectNode renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(elementSchemaArtifact);

    rendering.put(ModelNodeNames.JSON_LD_CONTEXT, renderParentSchemaArtifactContextJsonLdSpecification());

    // TODO properties

    rendering.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_CONTEXT);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_ID);

    for (String childName : elementSchemaArtifact.getChildNames())
      rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(childName);

    if (elementSchemaArtifact.hasAttributeValueField())
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES,
        renderAdditionalPropertiesForAttributeValueFieldJsonSchemaSpecification());
    else
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    rendering.put(ModelNodeNames.UI, mapper.valueToTree(elementSchemaArtifact.getElementUI()));

    // TODO isMultiple!!!

    return rendering;
  }

  public ObjectNode renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(fieldSchemaArtifact);

    rendering.put(ModelNodeNames.JSON_LD_CONTEXT, renderSchemaArtifactContextPrefixesJsonLdSpecification());

    // Static fields have no JSON Schema fields (properties, required, additionalProperties), or
    // value constraints.
    if (!fieldSchemaArtifact.isStatic()) {

      if (fieldSchemaArtifact.hasIRIValue()) {
        rendering.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, renderIRIFieldArtifactPropertiesJsonSchemaSpecification());
        rendering.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
        rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_ID);
      } else {
        rendering.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, renderLiteralFieldArtifactPropertiesJsonSchemaSpecification());
        // Non-IRI fields may have en empty object value to there are no required fields
      }

      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

      if (fieldSchemaArtifact.getSkosPrefLabel().isPresent())
        rendering.put(ModelNodeNames.SKOS_PREFLABEL, fieldSchemaArtifact.getSkosPrefLabel().get().toString());

      if (!fieldSchemaArtifact.getSkosAlternateLabels().isEmpty()) {
        rendering.put(ModelNodeNames.SKOS_ALTLABEL, mapper.createArrayNode());
        for (String skosAlternateLabel : fieldSchemaArtifact.getSkosAlternateLabels())
          rendering.withArray(ModelNodeNames.SKOS_ALTLABEL).add(skosAlternateLabel.toString());
      }
      rendering.put(ModelNodeNames.VALUE_CONSTRAINTS, mapper.valueToTree(fieldSchemaArtifact.getValueConstraints()));
    }

    rendering.put(ModelNodeNames.UI, mapper.valueToTree(fieldSchemaArtifact.getFieldUI()));

    // TODO isMultiple!!!

    return rendering;
  }

  private ObjectNode renderSchemaArtifact(SchemaArtifact schemaArtifact)
  {
    ObjectNode rendering = renderArtifact(schemaArtifact);

    rendering.put(ModelNodeNames.JSON_SCHEMA_SCHEMA, schemaArtifact.getJsonSchemaSchemaUri().toString());
    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    rendering.put(ModelNodeNames.JSON_SCHEMA_TITLE, schemaArtifact.getJsonSchemaTitle());
    rendering.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, schemaArtifact.getJsonSchemaDescription());
    rendering.put(ModelNodeNames.SCHEMA_ORG_NAME, schemaArtifact.getName());
    rendering.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, schemaArtifact.getDescription());
    rendering.put(ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION, schemaArtifact.getVersion().toString());

    if (schemaArtifact.getIdentifier().isPresent())
      rendering.put(ModelNodeNames.SCHEMA_ORG_IDENTIFIER, schemaArtifact.getIdentifier().get());

    if (schemaArtifact.getVersion().isPresent())
      rendering.put(ModelNodeNames.PAV_VERSION, schemaArtifact.getVersion().get().toString());

    if (schemaArtifact.getStatus().isPresent())
      rendering.put(ModelNodeNames.BIBO_STATUS, schemaArtifact.getStatus().get().toString());

    if (schemaArtifact.getPreviousVersion().isPresent())
      rendering.put(ModelNodeNames.PAV_PREVIOUS_VERSION,
        schemaArtifact.getPreviousVersion().get().toString());

    if (schemaArtifact.getDerivedFrom().isPresent())
      rendering.put(ModelNodeNames.PAV_DERIVED_FROM, schemaArtifact.getDerivedFrom().get().toString());

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for an artifact.
   */
  private ObjectNode renderArtifact(Artifact artifact)
  {
    ObjectNode rendering = renderMonitoredArtifact(artifact);

    if (artifact.getJsonLdTypes().size() == 1) {
      rendering.put(ModelNodeNames.JSON_LD_TYPE, artifact.getJsonLdTypes().get(0).toString());
    } else if (artifact.getJsonLdTypes().size() > 1) {
      rendering.put(ModelNodeNames.JSON_LD_TYPE, mapper.createArrayNode());
      for (URI jsonLdType : artifact.getJsonLdTypes())
        rendering.withArray(ModelNodeNames.JSON_LD_TYPE).add(jsonLdType.toString());
    }

    if (artifact.getJsonLdId().isPresent())
      rendering.put(ModelNodeNames.JSON_LD_ID, artifact.getJsonLdId().get().toString());

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a monitored artifact.
   */
  private ObjectNode renderMonitoredArtifact(MonitoredArtifact monitoredArtifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    if (monitoredArtifact.getCreatedBy().isPresent())
      rendering.put(ModelNodeNames.PAV_CREATED_BY, monitoredArtifact.getCreatedBy().get().toString());

    if (monitoredArtifact.getModifiedBy().isPresent())
      rendering.put(ModelNodeNames.OSLC_MODIFIED_BY, monitoredArtifact.getModifiedBy().get().toString());

    if (monitoredArtifact.getCreatedOn().isPresent())
      rendering.put(ModelNodeNames.PAV_CREATED_ON, monitoredArtifact.getCreatedOn().get().toString());

    if (monitoredArtifact.getLastUpdatedOn().isPresent())
      rendering.put(ModelNodeNames.PAV_LAST_UPDATED_ON, monitoredArtifact.getLastUpdatedOn().get().toString());

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for an additionalProperties field for a template or element containing
   * an attribute-value field
   * <p>
   * The specification is defined as follows:
   * <pre>
   * {
   *   "type": "object",
   *   "properties": {
   *     "@value": { "type": [ "string", "null" ] },
   *     "@type": { "type": "string", "format": "uri" }
   *   },
   *   "required": [ "@value" ],
   *   "additionalProperties": false
   * }
   * </pre>
   * The additional properties are of type object and contain two main properties:
   * - "@value": A string or null value.
   * - "@type": A string representing a URI.
   * <p>
   * The "@value" property is required, while the "@type" property is optional.
   * The "@value" property can hold a string value or be null, while the "@type" property must be a string in URI format.
   * <p>
   * Note that no other additional properties are allowed due to "additionalProperties" being set to false.
   */
  private ObjectNode renderAdditionalPropertiesForAttributeValueFieldJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    rendering.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_LD_VALUE, renderStringOrNullJsonSchemaSpecification());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_LD_TYPE, renderURIValueJsonSchemaSpecification());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_VALUE);
    rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    return rendering;
  }

  /**
   *
   * <pre>
   *   {
   *     "@context": {
   *       "type": "object",
   *       "properties": {
   *         "rdfs": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2000/01/rdf-schema#"] },
   *         "xsd": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2001/XMLSchema#"] },
   *         "pav": { "type": "string", "format": "uri", "enum": ["http://purl.org/pav/"] },
   *         "schema": { "type": "string", "format": "uri", "enum": ["http://schema.org/"] },
   *         "oslc": { "type": "string", "format": "uri", "enum": ["http://open-services.net/ns/core#"] },
   *         "skos": { "type": "string", "format": "uri", "enum": ["http://www.w3.org/2004/02/skos/core#"] },
   *         "rdfs:label": { "type": "object", "properties": { "@type": { "type": "string", "enum": ["xsd:string"] }}},
   *         "schema:isBasedOn": { "type": "object", "properties": {"@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "schema:name": { "type": "object", "properties": {"@type": { "type": "string",  "enum": ["xsd:string"] }}},
   *         "schema:description": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:string"] }}},
   *         "pav:derivedFrom": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"  ] }}},
   *         "pav:createdOn": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:dateTime"] }}},
   *         "pav:createdBy": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }}},
   *         "pav:lastUpdatedOn": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["xsd:dateTime"] }}},
   *         "oslc:modifiedBy": { "type": "object", "properties": { "@type": {  "type": "string",  "enum": ["@id"] }} },
   *         "skos:notation": { "type": "object", "properties": { "@type": { "type": "string", "enum": ["xsd:string"] }}},
   *         "<Child Name 1>": { "enum": [ "<PROPERTY_URI_1>"] },
   *         ...
   *         "<Child Name n>": { "enum": [ "<PROPERTY_URI_n>"] }
   *       },
   *       "required": [ "xsd", "pav", "schema", "oslc", "schema:isBasedOn", "schema:name", "schema:description",
   *                     "pav:createdOn", "pav:createdBy", "pav:lastUpdatedOn", "oslc:modifiedBy",
   *                     "<Child Name 1>", ... "<Child Name n>" ],
   *       "additionalProperties": false
   *     }
   * </pre>
   */
  private ObjectNode renderParentSchemaArtifactsPropertiesJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();


    return rendering;
  }

  /**
   * Generate a JSON-LD @context for parent schema artifacts (i.e., templates and elements)
   * <p>
   * Defined as follows:
   * <pre>
   *   "@context": {
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "pav": "http://purl.org/pav/",
   *     "bibo": "http://purl.org/ontology/bibo/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "schema": "http://schema.org/",
   *     "skos": "http://www.w3.org/2004/02/skos/core#",
   *     "schema:name": { "@type": "xsd:string" },
   *     "schema:description": { "@type": "xsd:string" },
   *     "pav:createdOn": { "@type": "xsd:dateTime" },
   *     "pav:createdBy": { "@type": "@id" },
   *     "pav:lastUpdatedOn": { "@type": "xsd:dateTime" },
   *     "oslc:modifiedBy": { "@type": "@id" }
   *   }
   * </pre>
   */
  private ObjectNode renderParentSchemaArtifactContextJsonLdSpecification()
  {
    ObjectNode rendering = renderSchemaArtifactContextPrefixesJsonLdSpecification();

    rendering.put(ModelNodeNames.SCHEMA_ORG_NAME, renderXSDStringJsonLdSpecification());
    rendering.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, renderXSDStringJsonLdSpecification());
    rendering.put(ModelNodeNames.PAV_CREATED_ON, renderXSDDateTimeJsonLdSpecification());
    rendering.put(ModelNodeNames.PAV_CREATED_BY, renderIRIJsonLdSpecification());
    rendering.put(ModelNodeNames.PAV_LAST_UPDATED_ON, renderXSDDateTimeJsonLdSpecification());
    rendering.put(ModelNodeNames.OSLC_MODIFIED_BY, renderIRIJsonLdSpecification());

    return rendering;
  }

  /**
   * Generate JSON-LD @context prefix specification for schema artifacts
   * <p></p>
   * Defined as follows:
   * <pre>
   *   "@context": {
   *     "xsd": "http://www.w3.org/2001/XMLSchema#",
   *     "pav": "http://purl.org/pav/",
   *     "bibo": "http://purl.org/ontology/bibo/",
   *     "oslc": "http://open-services.net/ns/core#",
   *     "schema": "http://schema.org/",
   *     "skos": "http://www.w3.org/2004/02/skos/core#"
   *   }
   * </pre>
   */
  private ObjectNode renderSchemaArtifactContextPrefixesJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    // TODO Put these IRIs in the ModelNodeNames class
    rendering.put(ModelNodeNames.XSD, "http://www.w3.org/2001/XMLSchema#");
    rendering.put(ModelNodeNames.PAV, "http://purl.org/pav/");
    rendering.put(ModelNodeNames.BIBO, "http://purl.org/ontology/bibo/");
    rendering.put(ModelNodeNames.OSLC, "http://open-services.net/ns/core#");
    rendering.put(ModelNodeNames.SCHEMA, "http://schema.org/");
    rendering.put(ModelNodeNames.SKOS, "http://www.w3.org/2004/02/skos/core#");

    return rendering;
  }


  /**
   * Generate a JSON Schema properties specification for a literal-valued field
   * <p></p>
   * Defined as follows:
   * <pre>
   * {
   *   "@type": {
   *     "oneOf": [
   *       { "type": "string", "format": "uri" },
   *       { "type": "array", "minItems": 1, "items": { "type": "string", "format": "uri" },  "uniqueItems": true }
   *     ]
   *   },
   *   "rdfs:label": {  "type": [ "string", "null" ] },
   *   "@value": {  "type": [ "string", "null" ] }
   * }
   * </pre>
   */
  private ObjectNode renderLiteralFieldArtifactPropertiesJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, renderURIOrURIArrayJsonSchemaSpecification(1, true));
    rendering.put(ModelNodeNames.RDFS_LABEL, renderStringOrNullJsonSchemaSpecification());
    rendering.put(ModelNodeNames.JSON_LD_VALUE, renderStringOrNullJsonSchemaSpecification());

    return rendering;
  }


  /**
   * Generate a JSON Schema properties specification for a IRI-valued field
   * <p>
   * Defined as follows:
   * <pre>
   * {
   *   "@type": {
   *     "oneOf": [
   *       { "type": "string", "format": "uri" },
   *       { "type": "array", "minItems": 1, "items": { "type": "string", "format": "uri" },  "uniqueItems": true }
   *     ]
   *   },
   *   "rdfs:label": {  "type": [ "string", "null" ] },
   *   "@id": { "type": "string", "format": "uri" }
   * }
   * </pre>
   */
  private ObjectNode renderIRIFieldArtifactPropertiesJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, renderURIOrURIArrayJsonSchemaSpecification(1, true));
    rendering.put(ModelNodeNames.RDFS_LABEL, renderStringOrNullJsonSchemaSpecification());
    rendering.put(ModelNodeNames.JSON_LD_ID, renderURIValueJsonSchemaSpecification());

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a string or null value
   * <p>
   * Defined as follows:
   * <pre>
   *   { "type": [ "string", "null" ] }
   * </pre>
   */
  private ObjectNode renderStringOrNullJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_TYPE).add("string");
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_TYPE).add("null");

    return rendering;
  }

  /**
   * Generate a JSON-LD @type specification for an xsd:string
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "xsd:string" }
   * </pre>
   */
  private ObjectNode renderXSDStringJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, "xsd:string");

    return rendering;
  }

  /**
   * Generate a JSON-LD @type specification for an xsd:dateTime
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "xsd:dateTime" }
   * </pre>
   */
  private ObjectNode renderXSDDateTimeJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, "xsd:dateTime");

    return rendering;
  }

  /**
   * Generate a JSON-LD @type specification for an IRI
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "@id" }
   * </pre>
   */
  private ObjectNode renderIRIJsonLdSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeNames.JSON_LD_ID);

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for a URI-formatted string
   * <p>
   * Defined as follows:
   * <pre>
   *   { "type": "string", "format": "uri" }
   * </pre>
   */
  private ObjectNode renderURIValueJsonSchemaSpecification()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, "string");
    rendering.put(ModelNodeNames.JSON_SCHEMA_FORMAT, "uri");

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for URI-formatted string or URI-formatted string array
   * <p>
   * Defined as follows:
   * <pre>
   * {
   *   "oneOf": [
   *     { "type": "string", "format": "uri" },
   *     { "type": "array", "minItems": 1, "items": { "type": "string", "format": "uri" }, "uniqueItems": true }
   *   ]
   * }
   * </pre>
   */
  private ObjectNode renderURIOrURIArrayJsonSchemaSpecification(int minItems, boolean uniqueItems)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_ONE_OF, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_ONE_OF).add(renderURIValueJsonSchemaSpecification());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_ONE_OF).add(renderURIArrayJsonSchemaSpecification(1, true));

    return rendering;
  }

  /**
   * Generate a JSON Schema specification for URI-formatted string array
   * <p>
   * Defined as follows:
   * <pre>
   * { "type": "array", "minItems": 1, "items": { "type": "string", "format": "uri" }, "uniqueItems": true }
   * </pre>
   */
  private ObjectNode renderURIArrayJsonSchemaSpecification(int minItems, boolean uniqueItems)
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_ARRAY);
    rendering.put(ModelNodeNames.JSON_SCHEMA_MIN_ITEMS, minItems);
    rendering.put(ModelNodeNames.JSON_SCHEMA_ITEMS, mapper.createObjectNode());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_ITEMS).put(ModelNodeNames.JSON_SCHEMA_TYPE, "string");
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_ITEMS).put(ModelNodeNames.JSON_SCHEMA_FORMAT, "uri");
    rendering.put(ModelNodeNames.JSON_SCHEMA_UNIQUE_ITEMS, uniqueItems);

    return rendering;
  }

}
