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
    ObjectNode templateSchemaArtifactRendering = renderSchemaArtifact(templateSchemaArtifact);

    // TODO @context

    // TODO properties
    // TODO required

    if (templateSchemaArtifact.hasAttributeValueField())
      templateSchemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, renderAdditionalPropertiesForAttributeValueField());
    else
      templateSchemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    templateSchemaArtifactRendering.put(ModelNodeNames.UI, mapper.valueToTree(templateSchemaArtifact.getTemplateUI()));

    return templateSchemaArtifactRendering;
  }

  public ObjectNode renderElementSchemaArtifact(ElementSchemaArtifact elementSchemaArtifact)
  {
    ObjectNode elementSchemaArtifactRendering = renderSchemaArtifact(elementSchemaArtifact);

    // TODO @context

    // TODO properties
    // TODO required
    if (elementSchemaArtifact.hasAttributeValueField())
      elementSchemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, renderAdditionalPropertiesForAttributeValueField());
    else
      elementSchemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    elementSchemaArtifactRendering.put(ModelNodeNames.UI, mapper.valueToTree(elementSchemaArtifact.getElementUI()));

    // TODO isMultiple!!!

    return elementSchemaArtifactRendering;
  }

  public ObjectNode renderFieldSchemaArtifact(FieldSchemaArtifact fieldSchemaArtifact)
  {
    ObjectNode fieldSchemaArtifactRendering = renderSchemaArtifact(fieldSchemaArtifact);

    // TODO @context

    // TODO properties
    // TODO required -- @value or @id and optionally @type

    fieldSchemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    if (fieldSchemaArtifact.getSkosPrefLabel().isPresent())
      fieldSchemaArtifactRendering.put(ModelNodeNames.SKOS_PREFLABEL,
        fieldSchemaArtifact.getSkosPrefLabel().get().toString());

    if (!fieldSchemaArtifact.getSkosAlternateLabels().isEmpty()) {
      fieldSchemaArtifactRendering.put(ModelNodeNames.SKOS_ALTLABEL, mapper.createArrayNode());
      for (String skosAlternateLabel : fieldSchemaArtifact.getSkosAlternateLabels())
        fieldSchemaArtifactRendering.withArray(ModelNodeNames.SKOS_ALTLABEL).add(skosAlternateLabel.toString());
    }

    fieldSchemaArtifactRendering.put(ModelNodeNames.UI, mapper.valueToTree(fieldSchemaArtifact.getFieldUI()));
    fieldSchemaArtifactRendering.put(ModelNodeNames.VALUE_CONSTRAINTS, mapper.valueToTree(fieldSchemaArtifact.getValueConstraints()));

    // TODO isMultiple!!!

    return fieldSchemaArtifactRendering;
  }

  private ObjectNode renderSchemaArtifact(SchemaArtifact schemaArtifact)
  {
    ObjectNode schemaArtifactRendering = renderArtifact(schemaArtifact);

    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_SCHEMA, schemaArtifact.getJsonSchemaSchemaUri().toString());
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_TITLE, schemaArtifact.getJsonSchemaTitle());
    schemaArtifactRendering.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, schemaArtifact.getJsonSchemaDescription());
    schemaArtifactRendering.put(ModelNodeNames.SCHEMA_ORG_NAME, schemaArtifact.getName());
    schemaArtifactRendering.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, schemaArtifact.getDescription());
    schemaArtifactRendering.put(ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION, schemaArtifact.getVersion().toString());

    if (schemaArtifact.getIdentifier().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.SCHEMA_ORG_IDENTIFIER, schemaArtifact.getIdentifier().get());

    if (schemaArtifact.getVersion().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.PAV_VERSION, schemaArtifact.getVersion().get().toString());

    if (schemaArtifact.getStatus().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.BIBO_STATUS, schemaArtifact.getStatus().get().toString());

    if (schemaArtifact.getPreviousVersion().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.PAV_PREVIOUS_VERSION,
        schemaArtifact.getPreviousVersion().get().toString());

    if (schemaArtifact.getDerivedFrom().isPresent())
      schemaArtifactRendering.put(ModelNodeNames.PAV_DERIVED_FROM, schemaArtifact.getDerivedFrom().get().toString());

    return schemaArtifactRendering;
  }

  private ObjectNode renderArtifact(Artifact artifact)
  {
    ObjectNode artifactRendering = mapper.createObjectNode();

    if (artifact.getJsonLdTypes().size() == 1) {
      artifactRendering.put(ModelNodeNames.JSON_LD_TYPE, artifact.getJsonLdTypes().get(0).toString());
    } else if (artifact.getJsonLdTypes().size() > 1) {
      artifactRendering.put(ModelNodeNames.JSON_LD_TYPE, mapper.createArrayNode());
      for (URI jsonLdType : artifact.getJsonLdTypes())
        artifactRendering.withArray(ModelNodeNames.JSON_LD_TYPE).add(jsonLdType.toString());
    }

    if (artifact.getJsonLdId().isPresent())
      artifactRendering.put(ModelNodeNames.JSON_LD_ID, artifact.getJsonLdId().get().toString());

    if (artifact.getCreatedBy().isPresent())
      artifactRendering.put(ModelNodeNames.PAV_CREATED_BY, artifact.getCreatedBy().get().toString());

    if (artifact.getModifiedBy().isPresent())
      artifactRendering.put(ModelNodeNames.OSLC_MODIFIED_BY, artifact.getModifiedBy().get().toString());

    if (artifact.getCreatedOn().isPresent())
      artifactRendering.put(ModelNodeNames.PAV_CREATED_ON, artifact.getCreatedOn().get().toString());

    if (artifact.getLastUpdatedOn().isPresent())
      artifactRendering.put(ModelNodeNames.PAV_LAST_UPDATED_ON, artifact.getLastUpdatedOn().get().toString());

    return artifactRendering;
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
    ObjectNode additionalPropertiesRendering = mapper.createObjectNode();

    additionalPropertiesRendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeNames.JSON_SCHEMA_OBJECT);
    additionalPropertiesRendering.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, mapper.createObjectNode());
    additionalPropertiesRendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_LD_VALUE, renderStringOrNullValueType());
    additionalPropertiesRendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_LD_TYPE, renderURIValueType());
    additionalPropertiesRendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).put(ModelNodeNames.JSON_SCHEMA_REQUIRED, mapper.createArrayNode());
    additionalPropertiesRendering.withObject(ModelNodeNames.JSON_SCHEMA_PROPERTIES).withArray(ModelNodeNames.JSON_SCHEMA_REQUIRED).add(ModelNodeNames.JSON_LD_VALUE);
    additionalPropertiesRendering.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);

    return additionalPropertiesRendering;
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
    ObjectNode stringOrNullValueTypeRendering = mapper.createObjectNode();

    stringOrNullValueTypeRendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, mapper.createArrayNode());
    stringOrNullValueTypeRendering.withArray(ModelNodeNames.JSON_SCHEMA_TYPE).add("string");
    stringOrNullValueTypeRendering.withArray(ModelNodeNames.JSON_SCHEMA_TYPE).add("null");

    return stringOrNullValueTypeRendering;
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
    ObjectNode uriValueTypeRendering = mapper.createObjectNode();

    uriValueTypeRendering.put(ModelNodeNames.JSON_SCHEMA_TYPE, "string");
    uriValueTypeRendering.put(ModelNodeNames.JSON_SCHEMA_FORMAT, "uri");

    return uriValueTypeRendering;
  }

}
