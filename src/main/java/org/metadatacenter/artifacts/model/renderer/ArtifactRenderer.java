package org.metadatacenter.artifacts.model.renderer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.artifacts.model.core.Artifact;
import org.metadatacenter.artifacts.model.core.ElementSchemaArtifact;
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact;
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

    rendering.put(ModelNodeNames.JSON_LD_CONTEXT, renderParentSchemaArtifactJsonLdContext());

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
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, renderAdditionalPropertiesForAttributeValueField());
    else
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    rendering.put(ModelNodeNames.UI, mapper.valueToTree(templateSchemaArtifact.getTemplateUI()));

    return rendering;
  }

  public ObjectNode renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(elementSchemaArtifact);

    rendering.put(ModelNodeNames.JSON_LD_CONTEXT, renderParentSchemaArtifactJsonLdContext());

    // TODO properties

    rendering.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_CONTEXT);
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_ID);

    for (String childName : elementSchemaArtifact.getChildNames())
      rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(childName);

    if (elementSchemaArtifact.hasAttributeValueField())
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, renderAdditionalPropertiesForAttributeValueField());
    else
      rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    rendering.put(ModelNodeNames.UI, mapper.valueToTree(elementSchemaArtifact.getElementUI()));

    // TODO isMultiple!!!

    return rendering;
  }

  public ObjectNode renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    ObjectNode rendering = renderSchemaArtifact(fieldSchemaArtifact);

    rendering.put(ModelNodeNames.JSON_LD_CONTEXT, renderSchemaArtifactJsonLdContextPrefixes());

    // Static fields have no JSON Schema fields (properties, required, additionalProperties)
    if (!fieldSchemaArtifact.isStatic()) {

      if (fieldSchemaArtifact.hasIRIValue()) {
        // TODO properties
        rendering.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
        rendering.withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_ID);
      } else {
        // TODO properties
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
    }

    rendering.put(ModelNodeNames.UI, mapper.valueToTree(fieldSchemaArtifact.getFieldUI()));
    rendering.put(ModelNodeNames.VALUE_CONSTRAINTS, mapper.valueToTree(fieldSchemaArtifact.getValueConstraints()));

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

  private ObjectNode renderArtifact(Artifact artifact)
  {
    ObjectNode rendering = mapper.createObjectNode();

    if (artifact.getJsonLdTypes().size() == 1) {
      rendering.put(ModelNodeNames.JSON_LD_TYPE, artifact.getJsonLdTypes().get(0).toString());
    } else if (artifact.getJsonLdTypes().size() > 1) {
      rendering.put(ModelNodeNames.JSON_LD_TYPE, mapper.createArrayNode());
      for (URI jsonLdType : artifact.getJsonLdTypes())
        rendering.withArray(ModelNodeNames.JSON_LD_TYPE).add(jsonLdType.toString());
    }

    if (artifact.getJsonLdId().isPresent())
      rendering.put(ModelNodeNames.JSON_LD_ID, artifact.getJsonLdId().get().toString());

    if (artifact.getCreatedBy().isPresent())
      rendering.put(ModelNodeNames.PAV_CREATED_BY, artifact.getCreatedBy().get().toString());

    if (artifact.getModifiedBy().isPresent())
      rendering.put(ModelNodeNames.OSLC_MODIFIED_BY, artifact.getModifiedBy().get().toString());

    if (artifact.getCreatedOn().isPresent())
      rendering.put(ModelNodeNames.PAV_CREATED_ON, artifact.getCreatedOn().get().toString());

    if (artifact.getLastUpdatedOn().isPresent())
      rendering.put(ModelNodeNames.PAV_LAST_UPDATED_ON, artifact.getLastUpdatedOn().get().toString());

    return rendering;
  }

  /**
   * Generate a JSON Schema representation of an additionalProperties description for a template or element containing
   * an attribute-value field.
   * <p>
   * The additionalProperties are defined as follows:
   * <pre>
   * "additionalProperties": {
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
  private ObjectNode renderAdditionalPropertiesForAttributeValueField()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    rendering.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_LD_VALUE, renderStringOrNullValueType());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_LD_TYPE, renderURIValueType());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    rendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_VALUE);
    rendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    return rendering;
  }


  /**
   * Generate a @context for parent schema artifacts (i.e., templates and elements)
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
  private ObjectNode renderParentSchemaArtifactJsonLdContext()
  {
    ObjectNode rendering = renderSchemaArtifactJsonLdContextPrefixes();

    rendering.put(ModelNodeNames.SCHEMA_ORG_NAME, renderXSDStringJsonLdType());
    rendering.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, renderXSDStringJsonLdType());
    rendering.put(ModelNodeNames.PAV_CREATED_ON, renderXSDDateTimeJsonLdType());
    rendering.put(ModelNodeNames.PAV_CREATED_BY, renderIRIJsonLdType());
    rendering.put(ModelNodeNames.PAV_LAST_UPDATED_ON, renderXSDDateTimeJsonLdType());
    rendering.put(ModelNodeNames.OSLC_MODIFIED_BY, renderIRIJsonLdType());

    return rendering;
  }

  /**
   * Generate @context prefixed for schema artifacts.
   * <p>
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
  private ObjectNode renderSchemaArtifactJsonLdContextPrefixes()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.XSD, "http://www.w3.org/2001/XMLSchema#");
    rendering.put(ModelNodeNames.PAV, "http://purl.org/pav/");
    rendering.put(ModelNodeNames.BIBO, "http://purl.org/ontology/bibo/");
    rendering.put(ModelNodeNames.OSLC, "http://open-services.net/ns/core#");
    rendering.put(ModelNodeNames.SCHEMA, "http://schema.org/");
    rendering.put(ModelNodeNames.SKOS, "http://www.w3.org/2004/02/skos/core#");

    return rendering;
  }

  /**
   * Generate a JSON Schema representation a string or null value.
   * <p>
   * Defined as follows:
   * <pre>
   *   { "type": [ "string", "null" ] }
   * </pre>
   */
  private ObjectNode renderStringOrNullValueType()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, mapper.createArrayNode());
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_TYPE).add("string");
    rendering.withArray(ModelNodeNames.JSON_SCHEMA_TYPE).add("null");

    return rendering;
  }

  /**
   * Generate a JSON-LD representation xsd:string @type.
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "xsd:string" }
   * </pre>
   */
  private ObjectNode renderXSDStringJsonLdType()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, "xsd:string");

    return rendering;
  }

  /**
   * Generate a JSON-LD representation xsd:dateTime @type.
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "xsd:dateTime" }
   * </pre>
   */
  private ObjectNode renderXSDDateTimeJsonLdType()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, "xsd:dateTime");

    return rendering;
  }

  /**
   * Generate a JSON-LD representation an IRI @type.
   * <p>
   * Defined as follows:
   * <pre>
   *   { "@type": "@id" }
   * </pre>
   */
  private ObjectNode renderIRIJsonLdType()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeNames.JSON_LD_ID);

    return rendering;
  }


  /**
   * Generate a JSON Schema representation a URI-formatted string.
   * <p>
   * Defined as follows:
   * <pre>
   *   { "type": "string", "format": "uri" }
   * </pre>
   */
  private ObjectNode renderURIValueType()
  {
    ObjectNode rendering = mapper.createObjectNode();

    rendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, "string");
    rendering.put(ModelNodeNames.JSON_SCHEMA_FORMAT, "uri");

    return rendering;
  }

}
